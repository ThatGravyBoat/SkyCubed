package tech.thatgravyboat.skycubed.api.displays

import com.teamresourceful.resourcefullibkt.client.pushPop
import net.minecraft.client.gui.GuiGraphics

interface Display {

    fun getWidth(): Int
    fun getHeight(): Int

    fun render(graphics: GuiGraphics)

    fun render(graphics: GuiGraphics, x: Int, y: Int, alignmentX: Float = 0f, alignmentY: Float = 0f) {
        graphics.pushPop {
            translate((x - getWidth() * alignmentX).toDouble(), (y - getHeight() * alignmentY).toDouble(), 0.0)
            render(graphics)
        }
    }
}