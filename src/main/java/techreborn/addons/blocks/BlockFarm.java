package techreborn.addons.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import techreborn.addons.TechRebornAddons;
import techreborn.addons.client.GuiHandler;
import techreborn.addons.tiles.TileFarm;
import techreborn.blocks.BlockMachineBase;
import techreborn.client.TechRebornCreativeTab;

public class BlockFarm extends BlockMachineBase {
    public BlockFarm() {
        super(Material.iron);
        setCreativeTab(TechRebornCreativeTab.instance);
        setBlockName("techreborn.farm");
        setHardness(2F);
        setBlockTextureName("techreborn-addon:farm");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFarm();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            player.openGui(TechRebornAddons.instance, GuiHandler.farmID, world, x, y, z);
            return true;
        }
        return false;
    }


}
