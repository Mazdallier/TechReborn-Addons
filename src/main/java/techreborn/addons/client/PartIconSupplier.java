package techreborn.addons.client;


import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;

public class PartIconSupplier {


    public static IIcon fluidPipeIcon;

    @SubscribeEvent
    public void textureStitchEvent(TextureStitchEvent event){
        if(event.map.getTextureType() == 1){
            fluidPipeIcon = event.map.registerIcon("techreborn-addon:blocks/fluidPipe");
        }
    }

}
