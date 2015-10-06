package techreborn.addons.parts;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import techreborn.addons.client.RenderFluidCable;
import techreborn.lib.Functions;
import techreborn.lib.Location;
import techreborn.lib.vecmath.Vecs3d;
import techreborn.lib.vecmath.Vecs3dCube;
import techreborn.partSystem.IModPart;
import techreborn.partSystem.IPartDesc;
import techreborn.partSystem.ModPart;
import techreborn.partSystem.ModPartUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidCable  extends ModPart implements IPartDesc {
    public Vecs3dCube[] boundingBoxes = new Vecs3dCube[14];
    public float center = 0.6F;
    public float offset = 0.10F;
    public Map<ForgeDirection, TileEntity> connectedSides;
    public int ticks = 0;
    public boolean addedToEnergyNet = false;
    public ItemStack stack;
    protected ForgeDirection[] dirs = ForgeDirection.values();
    private boolean[] connections = new boolean[6];
    private boolean hasCheckedSinceStartup;

    public FluidCable() {
        connectedSides = new HashMap<ForgeDirection, TileEntity>();
    }

    public static float getCableThickness() {
        return 16F / 16F;
    }

    public void refreshBounding() {
        float centerFirst = center - offset;
        double w = getCableThickness() / 2;
        boundingBoxes[6] = new Vecs3dCube(centerFirst - w - 0.03, centerFirst
                - w - 0.08, centerFirst - w - 0.03, centerFirst + w + 0.08,
                centerFirst + w + 0.04, centerFirst + w + 0.08);

        boundingBoxes[6] = new Vecs3dCube(centerFirst - w, centerFirst - w,
                centerFirst - w, centerFirst + w, centerFirst + w, centerFirst
                + w);

        int i = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            double xMin1 = (dir.offsetX < 0 ? 0.0
                    : (dir.offsetX == 0 ? centerFirst - w : centerFirst + w));
            double xMax1 = (dir.offsetX > 0 ? 1.0
                    : (dir.offsetX == 0 ? centerFirst + w : centerFirst - w));

            double yMin1 = (dir.offsetY < 0 ? 0.0
                    : (dir.offsetY == 0 ? centerFirst - w : centerFirst + w));
            double yMax1 = (dir.offsetY > 0 ? 1.0
                    : (dir.offsetY == 0 ? centerFirst + w : centerFirst - w));

            double zMin1 = (dir.offsetZ < 0 ? 0.0
                    : (dir.offsetZ == 0 ? centerFirst - w : centerFirst + w));
            double zMax1 = (dir.offsetZ > 0 ? 1.0
                    : (dir.offsetZ == 0 ? centerFirst + w : centerFirst - w));

            boundingBoxes[i] = new Vecs3dCube(xMin1, yMin1, zMin1, xMax1,
                    yMax1, zMax1);
            i++;
        }
    }

    @Override
    public void addCollisionBoxesToList(List<Vecs3dCube> boxes, Entity entity) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (connectedSides.containsKey(dir))
                boxes.add(boundingBoxes[Functions.getIntDirFromDirection(dir)]);
        }
        boxes.add(boundingBoxes[6]);
    }

    @Override
    public List<Vecs3dCube> getSelectionBoxes() {
        List<Vecs3dCube> vec3dCubeList = new ArrayList<Vecs3dCube>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (connectedSides.containsKey(dir))
                vec3dCubeList.add(boundingBoxes[Functions
                        .getIntDirFromDirection(dir)]);
        }
        vec3dCubeList.add(boundingBoxes[6]);
        return vec3dCubeList;
    }

    @Override
    public List<Vecs3dCube> getOcclusionBoxes() {
        List<Vecs3dCube> vecs3dCubesList = new ArrayList<Vecs3dCube>();
        vecs3dCubesList.add(boundingBoxes[6]);
        return vecs3dCubesList;
    }

    @Override
    public void renderDynamic(Vecs3d translation, double delta) {
    }

    @Override
    public boolean renderStatic(Vecs3d translation, int pass) {
        return RenderFluidCable.renderStatic(translation, pass, this);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        writeConnectedSidesToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

    }

    @Override
    public String getName() {
        return "FluidCable";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getItemTextureName() {
        return "";
    }

    @Override
    public void tick() {
        if (worldObj != null) {
            if (worldObj.getTotalWorldTime() % 80 == 0 || !hasCheckedSinceStartup) {
                checkConnectedSides();
                hasCheckedSinceStartup = true;
            }
        }

    }

    @Override
    public void nearByChange() {
        checkConnectedSides();
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            worldObj.markBlockForUpdate(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
            IModPart part = ModPartUtils.getPartFromWorld(world, new Location(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ), this.getName());
            if (part != null) {
                FluidCable cablePart = (FluidCable) part;
                cablePart.checkConnectedSides();
            }
        }
    }

    @Override
    public void onAdded() {
        checkConnections(world, getX(), getY(), getZ());
        nearByChange();
    }

    @Override
    public void onRemoved() {
    }

    @Override
    public IModPart copy() {
        FluidCable part = new FluidCable();
        return part;
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    public boolean shouldConnectTo(TileEntity entity, ForgeDirection dir) {
        if (entity == null) {
            return false;
        } else {
            if (ModPartUtils.hasPart(entity.getWorldObj(), entity.xCoord, entity.yCoord, entity.zCoord, this.getName())) {
                FluidCable otherCable = (FluidCable) ModPartUtils.getPartFromWorld(entity.getWorldObj(), new Location(entity.xCoord, entity.yCoord, entity.zCoord), this.getName());
                int thisDir = Functions.getIntDirFromDirection(dir);
                int thereDir = Functions.getIntDirFromDirection(dir.getOpposite());
                boolean hasconnection = otherCable.connections[thereDir];

                otherCable.connections[thereDir] = false;

                if (ModPartUtils.checkOcclusion(entity.getWorldObj(), entity.xCoord, entity.yCoord, entity.zCoord, boundingBoxes[thereDir])) {
                    otherCable.connections[thereDir] = true;
                    return true;
                }
                otherCable.connections[thereDir] = hasconnection;
            }
            return false;
        }
    }

    public void checkConnectedSides() {
        refreshBounding();
        connectedSides = new HashMap<ForgeDirection, TileEntity>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int d = Functions.getIntDirFromDirection(dir);
            if (world == null) {
                return;
            }
            TileEntity te = world.getTileEntity(getX() + dir.offsetX, getY()
                    + dir.offsetY, getZ() + dir.offsetZ);
            if (shouldConnectTo(te, dir)) {
                if (ModPartUtils.checkOcclusion(getWorld(), getX(),
                        getY(), getZ(), boundingBoxes[d])) {
                    connectedSides.put(dir, te);
                }
            }
            if (te != null) {
                te.getWorldObj().markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
            }
        }
        checkConnections(world, getX(), getY(), getZ());
        getWorld().markBlockForUpdate(getX(), getY(), getZ());
    }

    public void checkConnections(World world, int x, int y, int z) {
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = dirs[i];
            int dx = x + dir.offsetX;
            int dy = y + dir.offsetY;
            int dz = z + dir.offsetZ;
            connections[i] = shouldConnectTo(world.getTileEntity(dx, dy, dz),
                    dir);
            world.func_147479_m(dx, dy, dz);
        }
        world.func_147479_m(x, y, z);
    }

    private void readConnectedSidesFromNBT(NBTTagCompound tagCompound) {

        NBTTagCompound ourCompound = tagCompound.getCompoundTag("connectedSides");

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            connections[dir.ordinal()] = ourCompound.getBoolean(dir.ordinal() + "");
        }
        checkConnectedSides();
    }

    private void writeConnectedSidesToNBT(NBTTagCompound tagCompound) {

        NBTTagCompound ourCompound = new NBTTagCompound();
        int i = 0;
        for (boolean b : connections) {
            ourCompound.setBoolean(i + "", b);
            i++;
        }

        tagCompound.setTag("connectedSides", ourCompound);
    }

    @Override
    public void readDesc(NBTTagCompound tagCompound) {
        readConnectedSidesFromNBT(tagCompound);
    }

    @Override
    public void writeDesc(NBTTagCompound tagCompound) {
        writeConnectedSidesToNBT(tagCompound);
    }

    @Override
    public boolean needsItem() {
        return true;
    }
}