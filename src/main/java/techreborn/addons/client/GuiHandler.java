package techreborn.addons.client;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import techreborn.addons.tiles.TileFarm;
import techreborn.client.gui.GuiFarm;

public class GuiHandler implements IGuiHandler {
    public static final int farmID = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == farmID){
            return new ContainerFarm((TileFarm) world.getTileEntity(x, y, z), player);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == farmID){
            return new GuiFarm(new ContainerFarm((TileFarm) world.getTileEntity(x, y, z), player));
        }
        return null;
    }
}
