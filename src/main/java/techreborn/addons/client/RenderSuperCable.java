package techreborn.addons.client;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import techreborn.addons.parts.SuperConductorCable;
import techreborn.lib.Functions;
import techreborn.lib.vecmath.Vecs3d;
import techreborn.lib.vecmath.Vecs3dCube;

public class RenderSuperCable {
    public static void renderBox(Vecs3dCube cube, Block block, Tessellator tessellator, RenderBlocks renderblocks, IIcon texture, Double xD, Double yD, double zD, float thickness) {
        block.setBlockBounds((float) cube.getMinX(), (float) cube.getMinY(), (float) cube.getMinZ(), (float) cube.getMaxX() + thickness, (float) cube.getMaxY() + thickness, (float) cube.getMaxZ() + thickness);
        renderblocks.setRenderBoundsFromBlock(block);
        tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
        renderblocks.renderFaceYNeg(block, xD, yD, zD, texture);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        renderblocks.renderFaceYPos(block, xD, yD, zD, texture);
        tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
        renderblocks.renderFaceZNeg(block, xD, yD, zD, texture);
        renderblocks.renderFaceZPos(block, xD, yD, zD, texture);
        tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        renderblocks.renderFaceXNeg(block, xD, yD, zD, texture);
        renderblocks.renderFaceXPos(block, xD, yD, zD, texture);
    }


    @SideOnly(Side.CLIENT)
    public static boolean renderStatic(Vecs3d translation, int pass, SuperConductorCable part) {
        Tessellator tessellator = Tessellator.instance;
        IIcon texture = Blocks.iron_block.getIcon(0, 0);
        RenderBlocks renderblocks = RenderBlocks.getInstance();
        double xD = part.xCoord;
        double yD = part.yCoord;
        double zD = part.zCoord;
        Block block = part.getBlockType();
        tessellator.setBrightness(block.getMixedBrightnessForBlock(part.getWorld(), part.getX(), part.getY(), part.getZ()));
        renderBox(part.boundingBoxes[6], block, tessellator, renderblocks, texture, xD, yD, zD, 0F);
        for (ForgeDirection direction : ForgeDirection.values()) {
            if (part.connectedSides.get(direction) != null) {
                renderBox(part.boundingBoxes[Functions.getIntDirFromDirection(direction)], block, tessellator, renderblocks, texture, xD, yD, zD, 0f);
            }
        }
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderblocks.setRenderBoundsFromBlock(block);
        return true;
    }


}
