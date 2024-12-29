package tech.thatgravyboat.skycubed

import com.mojang.logging.LogUtils
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skycubed.api.overlays.Overlays
import tech.thatgravyboat.skycubed.config.ConfigManager
import tech.thatgravyboat.skycubed.features.misc.ElementHider
import tech.thatgravyboat.skycubed.features.chat.ChatManager
import tech.thatgravyboat.skycubed.features.commands.hypixel.HypixelCommands
import tech.thatgravyboat.skycubed.features.equipment.EquipmentManager
import tech.thatgravyboat.skycubed.features.items.CooldownManager
import tech.thatgravyboat.skycubed.features.items.ItemBarManager
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.misc.UpdateChecker
import tech.thatgravyboat.skycubed.features.notifications.NotificationManager
import tech.thatgravyboat.skycubed.features.overlays.DialogueOverlay
import tech.thatgravyboat.skycubed.features.overlays.WindOverlay
import tech.thatgravyboat.skycubed.features.overlays.map.DungeonMap
import tech.thatgravyboat.skycubed.features.overlays.map.MinimapOverlay
import tech.thatgravyboat.skycubed.features.overlays.pickuplog.PickUpLog
import tech.thatgravyboat.skycubed.features.tablist.CompactTablist
import tech.thatgravyboat.skycubed.utils.ContributorHandler

object SkyCubed : ModInitializer {

    override fun onInitialize() {
        SkyBlockAPI.eventBus.register(ConfigManager)
        SkyBlockAPI.eventBus.register(Overlays)
        SkyBlockAPI.eventBus.register(ElementHider)
        SkyBlockAPI.eventBus.register(CooldownManager)
        SkyBlockAPI.eventBus.register(NotificationManager)
        SkyBlockAPI.eventBus.register(ItemBarManager)
        SkyBlockAPI.eventBus.register(HypixelCommands)
        SkyBlockAPI.eventBus.register(EquipmentManager)
        SkyBlockAPI.eventBus.register(ChatManager)
        SkyBlockAPI.eventBus.register(DialogueOverlay)
        SkyBlockAPI.eventBus.register(Maps)
        SkyBlockAPI.eventBus.register(CompactTablist)
        SkyBlockAPI.eventBus.register(PickUpLog)
        SkyBlockAPI.eventBus.register(MinimapOverlay)
        SkyBlockAPI.eventBus.register(WindOverlay)
        SkyBlockAPI.eventBus.register(DungeonMap)
        SkyBlockAPI.eventBus.register(ContributorHandler)
        SkyBlockAPI.eventBus.register(UpdateChecker)
    }


    val logger = LogUtils.getLogger()

    fun id(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("skycubed", path)
    }
}
