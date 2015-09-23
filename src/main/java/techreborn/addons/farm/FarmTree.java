package techreborn.addons.farm;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockSapling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import techreborn.addons.api.farm.IFarmLogicDevice;
import techreborn.addons.tiles.TileFarm;
import techreborn.config.ConfigTechReborn;
import techreborn.lib.Location;
import techreborn.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FarmTree implements IFarmLogicDevice {

    public static ArrayList<Block> harvestableLogs = new ArrayList<Block>();
    ArrayList<Location> farmlandToPlace = new ArrayList<Location>();
    Block farmlandType = Blocks.dirt;
    int harvestx = 0;
    int harvesty = 0;
    int harvestz = 0;
    boolean isHavrvesting = false;

    protected static EntityPlayer fakePlayer = null;
    public static GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes("BlameMJ".getBytes()), "BlameMJ");

    @Override
    public void tick(TileFarm tileFarm) {
        if (tileFarm.getWorldObj().isRemote) {
            return;
        }
        if (tileFarm.canUseEnergy(ConfigTechReborn.farmEu)) {
            tileFarm.useEnergy(ConfigTechReborn.farmEu);
            if (tileFarm.getWorldObj().getTotalWorldTime() % 20 == 0 || tileFarm.inventory.hasChanged) {
                calculateFarmLand(tileFarm);
            }
            if (tileFarm.getWorldObj().getTotalWorldTime() % 10 == 0) {
                farmLandTick(tileFarm);
                saplinTick(tileFarm);
            }
            for (int i = 0; i < 5; i++) {
                harvestTick(tileFarm);
            }
        }
    }

    public void calculateFarmLand(TileFarm tileFarm) {
        if (farmlandToPlace.isEmpty()) {
            for (int x = -tileFarm.size + 1; x < tileFarm.size; x++) {
                for (int z = -tileFarm.size + 1; z < tileFarm.size; z++) {
                    int xpos = x + tileFarm.xCoord;
                    int ypos = tileFarm.yCoord + 1;
                    int zpos = z + tileFarm.zCoord;
                    if (tileFarm.getWorldObj().getBlock(xpos, ypos, zpos) != farmlandType) {
                        farmlandToPlace.add(new Location(xpos, ypos, zpos));
                    }
                }
            }
        }
    }

    public void farmLandTick(TileFarm tileFarm) {
        if (!farmlandToPlace.isEmpty()) {
            Location location = farmlandToPlace.get(0);
            if (tileFarm.getWorldObj().getBlock(location.getX(), location.getY(), location.getZ()) != farmlandType) {
                if (removeInputStack(new ItemStack(Blocks.dirt), tileFarm)) {
                    tileFarm.getWorldObj().setBlock(location.getX(), location.getY(), location.getZ(), farmlandType);
                }
            }
            farmlandToPlace.remove(location);
        }
    }

    int sapx = 0;
    int sapz = 0;
    boolean isplanting;

    public void saplinTick(TileFarm tileFarm) {
        if (!isplanting) {
            sapx = -tileFarm.size;
            sapz = -tileFarm.size;
            isplanting = true;
        } else {
            int xpos = sapx + tileFarm.xCoord;
            int ypos = tileFarm.yCoord + 2;
            int zpos = sapz + tileFarm.zCoord;
            if (getSaplinStack(tileFarm) != null) {
                Block saplin = Block.getBlockFromItem(getSaplinStack(tileFarm).getItem());
                int meta = getSaplinStack(tileFarm).getItemDamage();
                if (saplin != null && tileFarm.getWorldObj().getBlock(xpos, ypos, zpos) == Blocks.air && saplin.canBlockStay(tileFarm.getWorldObj(), xpos, ypos, zpos) && saplin.canPlaceBlockAt(tileFarm.getWorldObj(), xpos, ypos, zpos) && removeInputStack(new ItemStack(saplin, 1, meta), tileFarm)) {
                    tileFarm.getWorldObj().setBlock(xpos, ypos, zpos, saplin, meta, 2);
                }
                sapx++;
                if (sapx == tileFarm.size) {
                    sapx = -tileFarm.size;
                    sapz++;
                }
                if (sapz >= tileFarm.size) {
                    sapz = -tileFarm.size;
                    sapx = -tileFarm.size;
                }
            }
        }
    }

    public void harvestTick(TileFarm tileFarm) {
        int overlap = 2;
        if (farmlandToPlace.isEmpty()) {
            if (!isHavrvesting) {
                harvestx = -tileFarm.size - overlap;
                harvesty = +2;
                harvestz = -tileFarm.size - overlap;
                isHavrvesting = true;
            } else {
                Block block = tileFarm.getWorldObj().getBlock(harvestx + tileFarm.xCoord, harvesty + tileFarm.yCoord, harvestz + tileFarm.zCoord);
                if (block instanceof BlockLeavesBase) {
                    breakBlock(tileFarm, new Location(harvestx + tileFarm.xCoord, harvesty + tileFarm.yCoord, harvestz + tileFarm.zCoord));
                } else if (harvestableLogs.contains(block)) {
                    breakBlock(tileFarm, new Location(harvestx + tileFarm.xCoord, harvesty + tileFarm.yCoord, harvestz + tileFarm.zCoord));
                    ;
                }
                harvestx++;
                if (harvestx >= tileFarm.size + overlap) {
                    harvestx = -tileFarm.size - overlap;
                    harvestz++;
                } else if (harvestz >= tileFarm.size + overlap) {
                    harvestx = -tileFarm.size - overlap;
                    harvestz = -tileFarm.size - overlap;
                    harvesty++;
                } else if (harvesty > 12) {
                    harvestx = 0;
                    harvesty = 2;
                    harvestz = 0;
                    isHavrvesting = false;
                }
            }
        }
    }


    public boolean removeInputStack(ItemStack stack, TileFarm tileFarm) {
        for (int i = 1; i < 9; i++) {
            if (ItemUtils.isItemEqual(stack, tileFarm.getStackInSlot(i), true, true)) {
                tileFarm.decrStackSize(i, 1);
                return true;
            }
        }
        return false;
    }


    public ItemStack getSaplinStack(TileFarm tileFarm) {
        for (int i = 1; i < 14; i++) {
            if (tileFarm.getStackInSlot(i) != null && Block.getBlockFromItem(tileFarm.getStackInSlot(i).getItem()) instanceof BlockSapling) {
                return tileFarm.getStackInSlot(i);
            }
        }
        return null;
    }


    public void breakBlock(TileFarm farm, Location location) {
        World world = farm.getWorldObj();
        int x = location.x;
        int y = location.y;
        int z = location.z;
        world.destroyBlockInWorldPartially(world.rand.nextInt(), x, y, z, -1);


        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(x, y, z, world, block, meta,
                getFakePlayer((WorldServer) world));
        MinecraftForge.EVENT_BUS.post(breakEvent);

        if (!breakEvent.isCanceled()) {
            List<ItemStack> stacks = getItemStackFromBlock((WorldServer) world, x, y, z);

            if (stacks != null) {
                for (ItemStack s : stacks) {
                    if (s != null) {
                        addStackToInventroy(farm, s);
                    }
                }
            }

            world.playAuxSFXAtEntity(
                    null,
                    2001,
                    x, y, z,
                    Block.getIdFromBlock(block)
                            + (meta << 12));

            world.setBlockToAir(x, y, z);
        }

    }

    public List<ItemStack> getItemStackFromBlock(WorldServer world, int i, int j, int k) {
        Block block = world.getBlock(i, j, k);

        if (block == null || block.isAir(world, i, j, k)) {
            return null;
        }

        int meta = world.getBlockMetadata(i, j, k);

        ArrayList<ItemStack> dropsList = block.getDrops(world, i, j, k, meta, 0);
        float dropChance = ForgeEventFactory.fireBlockHarvesting(dropsList, world, block, i, j, k, meta, 0, 1.0F,
                false, getFakePlayer(world));

        ArrayList<ItemStack> returnList = new ArrayList<ItemStack>();
        for (ItemStack s : dropsList) {
            if (world.rand.nextFloat() <= dropChance) {
                returnList.add(s);
            }
        }

        return returnList;
    }

    public EntityPlayer getFakePlayer(WorldServer world) {
        if (fakePlayer == null) {
            fakePlayer = FakePlayerFactory.get(world, gameProfile);
        } else {
            fakePlayer.worldObj = world;
        }

        return fakePlayer;
    }

    public void addStackToInventroy(TileFarm farm, ItemStack stack) {
        for (int i = 5; i < 14; i++) {
            if (farm.getStackInSlot(i) == null) {
                farm.setInventorySlotContents(i, stack);
                return;
            } else if (ItemUtils.isItemEqual(stack, farm.getStackInSlot(i), true, true) && farm.getStackInSlot(i).stackSize + stack.stackSize <= stack.getMaxStackSize()) {
                farm.decrStackSize(i, -stack.stackSize);
                return;
            }
        }
    }


}
