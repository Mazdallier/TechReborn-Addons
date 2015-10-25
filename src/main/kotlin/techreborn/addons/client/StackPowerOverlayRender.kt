package techreborn.addons.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11
import techreborn.api.power.IEnergyInterfaceItem
import java.awt.Color

class StackPowerOverlayRender : IItemRenderer {

    override fun renderItem(type: IItemRenderer.ItemRenderType?, item: ItemStack?, vararg data: Any?) {
        RenderItem.getInstance().renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, item, 0, 0);
        renderPowerCount(item)
    }

    public fun renderPowerCount(stack: ItemStack?) {
        var str : String
        var num: Int
        var item = stack!!.item
        if (item is IEnergyInterfaceItem) {
            num = percentage(item.getMaxPower(stack).toInt(), item.getEnergy(stack).toInt())
            str = num.toString() + "%"
        } else {
            return
        }

        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_BLEND)

        GL11.glScalef(0.7.toFloat(), 0.7.toFloat(), 0.7.toFloat())
        var color: Int
        if (num <= 25) {
            GL11.glColor3f(255F, 0F, 0F)
            color = Color.red.rgb
        } else if (num >= 75) {
            color = Color.green.rgb
        } else {
            color = Color.yellow.rgb
        }
        val fontRenderer = Minecraft.getMinecraft().fontRenderer
        fontRenderer.drawStringWithShadow(str, 7 + 19 - 2 - fontRenderer.getStringWidth(str), 7 + 6 + 3, color)

        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }

    override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType?): Boolean {
        return type == IItemRenderer.ItemRenderType.INVENTORY;
    }

    override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?): Boolean {
        return false;
    }

    fun percentage(MaxValue: Int, CurrentValue: Int): Int {
        return if (CurrentValue == 0) 0 else (CurrentValue.toFloat() * 100.0f / MaxValue.toFloat()).toInt()
    }
}