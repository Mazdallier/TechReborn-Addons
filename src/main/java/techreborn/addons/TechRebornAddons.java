package main.java.techreborn.addons;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import techreborn.api.TechRebornItems;

@Mod(name = "TechReborn-Addons", modid = "techreborn-addons", version = "@MODVERSION@")
public class TechRebornAddons {

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        System.out.println(TechRebornItems.getItem("gems").getUnlocalizedName());
    }
}
