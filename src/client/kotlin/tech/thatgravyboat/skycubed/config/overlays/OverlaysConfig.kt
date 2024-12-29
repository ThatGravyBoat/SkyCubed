package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.*
import com.teamresourceful.resourcefulconfig.api.types.info.Translatable

@ConfigInfo(titleTranslation = "config.skycubed.overlays.title")
@Category("overlays", categories = [OverlayPositions::class])
object OverlaysConfig {

    @ConfigEntry(id = "info", translation = "config.skycubed.overlays.info")
    @Comment("", translation = "config.skycubed.overlays.info.desc")
    val info = InfoHudOverlay()

    @ConfigEntry(id = "rpg", translation = "config.skycubed.overlays.rpg")
    @Comment("", translation = "config.skycubed.overlays.rpg.desc")
    val rpg = RpgOverlay()

    @ConfigEntry(id = "text", translation = "config.skycubed.overlays.text")
    @Comment("", translation = "config.skycubed.overlays.text.desc")
    val text = TextOverlays()

    @ConfigEntry(id = "tablist", translation = "config.skycubed.overlays.tablist")
    @Comment("", translation = "config.skycubed.overlays.tablist.desc")
    @SearchTerm("tablist")
    val tablist = TabListOverlay()

    @ConfigEntry(id = "map", translation = "config.skycubed.overlays.map")
    @Comment("", translation = "config.skycubed.overlays.map.desc")
    val map = MapOverlay()

    @ConfigEntry(id = "pickupLog", translation = "config.skycubed.overlays.pickuplog")
    @Comment("", translation = "config.skycubed.overlays.pickuplog.desc")
    val pickupLog = PickupLogOverlay()

    @ConfigEntry(id = "commissions", translation = "config.skycubed.overlays.commissions")
    @Comment("", translation = "config.skycubed.overlays.commissions.desc")
    val commissions = CommissionOverlay()

    @ConfigEntry(id = "npc", translation = "config.skycubed.overlays.npc")
    @Comment("", translation = "config.skycubed.overlays.npc.desc")
    val npc = NpcOverlay()

    @Slider
    @Range(min = 0.0, max = 99.0)
    @ConfigEntry(id = "coldOverlay", translation = "config.skycubed.overlays.coldOverlay")
    @Comment("", translation = "config.skycubed.overlays.coldOverlay.desc")
    var coldOverlay = 80

    @ConfigEntry(id = "movableHotbar", translation = "config.skycubed.overlays.movableHotbar")
    @Comment("", translation = "config.skycubed.overlays.movableHotbar.desc")
    var movableHotbar = false

    @ConfigEntry(id = "windOverlay", translation = "config.skycubed.overlays.windOverlay")
    @Comment("", translation = "config.skycubed.overlays.windOverlay.desc")
    var windOverlay = false
}

enum class HealthDisplay : Translatable {
    DISABLED,
    NORMAL,
    EFFECTIVE;

    override fun getTranslationKey(): String = "config.skycubed.overlays.healthDisplay.${name.lowercase()}"
}