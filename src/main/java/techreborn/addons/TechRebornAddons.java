package techreborn.addons;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import techreborn.addons.blocks.BlockFarm;
import techreborn.addons.client.GuiHandler;
import techreborn.addons.farm.FarmTree;
import techreborn.addons.items.ItemFarmPatten;
import techreborn.addons.parts.FarmInventoryCable;
import techreborn.addons.parts.SuperConductorCable;
import techreborn.addons.tiles.TileFarm;
import techreborn.partSystem.ModPartRegistry;

@Mod(name = "TechReborn-Addons", modid = "techreborn-addons", version = "@MODVERSION@")
public class TechRebornAddons {

    public static Block farm;
    public static Item farmPatten;

    @Mod.Instance
    public static TechRebornAddons instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModPartRegistry.registerPart(new SuperConductorCable());
        ModPartRegistry.registerPart(new FarmInventoryCable());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        farm = new BlockFarm();
        GameRegistry.registerBlock(farm, "techreborn.farm");
        GameRegistry.registerTileEntity(TileFarm.class, "TileFarmTR");

        farmPatten = new ItemFarmPatten();
        GameRegistry.registerItem(farmPatten, "farmPatten");

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        FarmTree.harvestableLogs.add(Blocks.log);
        FarmTree.harvestableLogs.add(Blocks.log2);
    }
}
