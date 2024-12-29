package tech.thatgravyboat.skycubed.features.overlays

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.utils.pushPop

object WindOverlay {

    private var windCompassComponent: Component? = null
    private var index: Int? = null
    private var checked = false

    @Subscription
    @OnlyIn(SkyBlockIsland.DWARVEN_MINES, SkyBlockIsland.CRYSTAL_HOLLOWS)
    fun onHudRender(event: RenderHudEvent) {
        if (!OverlaysConfig.windOverlay) return
        if (windCompassComponent != null) {

            val font = McClient.self.font

            event.graphics.pushPop {
                translate(
                    (event.graphics.guiWidth() / 2).toFloat(),
                    (event.graphics.guiHeight() / 2).toFloat(),
                    0.0F
                );
                scale(1.2F, 1.2F, 1.2F);

                val width = font.width(windCompassComponent)
                event.graphics.drawStringWithBackdrop(font, windCompassComponent, -width / 2, -10, width, 0xffffff);
            }
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.DWARVEN_MINES, SkyBlockIsland.CRYSTAL_HOLLOWS)
    fun onScoreboardUpdate(event: TickEvent) {
        val scoreboard = McClient.scoreboard.toList()
        if (index == null && !checked) {
            scoreboard.forEachIndexed { index, line ->
                if (line.stripped == "Wind Compass") {
                    this.index = index+1
                    windCompassComponent = scoreboard[this.index!!]
                    return@forEachIndexed
                }
            }
            checked = true
        } else if (index != null) {
            val line = scoreboard[this.index!!]
            val previousLine = scoreboard[this.index!! - 1]
            if (previousLine.stripped == "Wind Compass") {
                windCompassComponent = line
            } else {
                index = null
            }
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.DWARVEN_MINES, SkyBlockIsland.CRYSTAL_HOLLOWS)
    fun onChatMessage(event: ChatReceivedEvent) {
        if (event.text.contains("GONE WITH THE WIND ENDED!")) {
            windCompassComponent = null
            index = null
        } else if (event.text.contains("GONE WITH THE WIND STARTED!")) {
            checked = false
        }
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        windCompassComponent = null
        index = null
        checked = false
    }
}
