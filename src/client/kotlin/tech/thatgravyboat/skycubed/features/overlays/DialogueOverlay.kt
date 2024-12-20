package tech.thatgravyboat.skycubed.features.overlays

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEntityEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.match
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.api.displays.Display
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.api.displays.toRow
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.mixins.LevelInvoker

private const val BACKGROUND_COLOR = 0xA0000000u

object DialogueOverlay : Overlay {

    private val regex = ComponentRegex("\\[NPC] (?<name>[\\w.\\s]+): (?<message>.+)")
    private val yesNoRegex = listOf(
        ComponentRegex("Select an option: (?<yes>\\[YES]) (?<no>\\[NO]) "),
        ComponentRegex("\\nAccept the trapper's task to hunt the animal\\?\\nClick an option: (?<yes>\\[YES]) - (?<no>\\[NO])"),
    )

    private val queue = mutableListOf<Pair<Component, Component>>()
    private var nextCheck = 0L
    private var yesNo: Pair<String, String>? = null
    private var displayedYesNo = false
    private var display: Display = Displays.empty()
    private var lastClickedEntities: MutableMap<LivingEntity, Long> = mutableMapOf()

    override val name: Component = Text.of("Dialogue")
    override val position: Position = Position()
    override val bounds: Pair<Int, Int> = 0 to 0
    override val moveable: Boolean = false
    override val enabled: Boolean get() = OverlaysConfig.npc.enabled

    private val yesNoDisplay by lazy {
        Displays.padding(
            5,
            Displays.text(
                Text.join(
                    Text.of("[Y]es") { this.color = TextColor.GREEN },
                    CommonText.SPACE,
                    Text.of("[N]o") { this.color = TextColor.RED }
                )
            )
        )
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onChatReceived(event: ChatReceivedEvent) {
        if (!enabled) return

        regex.match(event.component, "name", "message") { (name, message) ->
            queue.add(name to message)
            event.cancel()
        }
        yesNoRegex.match(event.component, "yes", "no") { (yes, no) ->
            yesNo = (yes.style.clickEvent?.value ?: "") to (no.style.clickEvent?.value ?: "")
            event.cancel()
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onEntityClick(event: RightClickEntityEvent) {
        if (!enabled) return

        val entity = event.entity as? LivingEntity ?: return
        lastClickedEntities[entity] = System.currentTimeMillis()
    }

    @Subscription
    fun onTick(event: TickEvent) {
        if (!enabled) return

        val config = OverlaysConfig.npc

        if (System.currentTimeMillis() > nextCheck) {
            nextCheck = System.currentTimeMillis() + (config.durationPerMessage * 1000f).toLong()

            if (queue.isEmpty()) {
                if (yesNo != null && !displayedYesNo) {
                    displayedYesNo = true
                    nextCheck = System.currentTimeMillis() + (config.durationForActionMessage * 1000f).toLong()
                    display = Displays.column(
                        display,
                        Displays.empty(0, 5),
                        Displays.background(
                            BACKGROUND_COLOR,
                            OverlaysConfig.npc.overlayRadius.toFloat(),
                            yesNoDisplay,
                        )
                    )
                } else {
                    reset()
                }
            } else {
                val (name, message) = queue.removeFirstOrNull() ?: return
                val armorStands = (McLevel.self as LevelInvoker).callGetEntities().all.filterIsInstance<ArmorStand>()
                val entity = lastClickedEntities.keys.find { npc ->
                    val armorStandsAtNpc = armorStands.filter { npc.x == it.x && npc.z == it.z }
                    armorStandsAtNpc.any { it.customName?.stripped == name.stripped }
                } ?: lastClickedEntities.keys.firstOrNull()

                display = Displays.background(
                    BACKGROUND_COLOR,
                    OverlaysConfig.npc.overlayRadius.toFloat(),
                    Displays.padding(
                        5,
                        listOfNotNull(
                            entity?.let { Displays.entity(it, 0, 0, 60, 60, 30, 80f, 40f) },
                            Displays.text(
                                Text.multiline(name, message),
                                McClient.window.guiScaledWidth / 3,
                            ),
                        ).toRow()
                    )
                )
            }
        }

        val (yesCommand, noCommand) = yesNo ?: return
        val isYes = InputConstants.isKeyDown(McClient.window.window, InputConstants.KEY_Y)
        val isNo = InputConstants.isKeyDown(McClient.window.window, InputConstants.KEY_N)

        val command = if (isYes) yesCommand else if (isNo) noCommand else return
        McClient.sendCommand(command.removePrefix("/"))
        reset()
    }

    private fun reset() {
        lastClickedEntities =
            lastClickedEntities.filterValues { it > System.currentTimeMillis() - 20000 }.toMutableMap()
        yesNo = null
        displayedYesNo = false
        display = Displays.empty()
        nextCheck = 0
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        display.render(graphics, graphics.guiWidth() / 2, graphics.guiHeight() - 90, 0.5f, 1f)
    }

}