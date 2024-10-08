package tech.thatgravyboat.skycubed.features.chat

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skycubed.config.ChatConfig

object ChatManager {

    private val compactMessage = mapOf(
        "exp" to Regex("^You earned .* from playing SkyBlock!"),
        "cooldowns" to Regex("^(?:Whoa! Slow down there!|This menu has been throttled! Please slow down\\.\\.\\.)"),
        "friends_list" to Regex("^-*\\n *(?:<<)? Friends \\("),
        "pickaxe_ability" to Regex("^You used your .* Ability!"),
    )

    private var cleanMessages = ChatConfig.messagesToClean.get().map(::Regex)

    init {
        ChatConfig.messagesToClean.addListener { _, new ->
            cleanMessages = new.mapNotNull {
                runCatching { Regex(it) }.getOrNull()
            }
        }
    }

    @Subscription
    fun onChatReceived(event: ChatReceivedEvent) {
        if (ChatConfig.compactChat) {
            for ((id, regex) in compactMessage) {
                if (regex.find(event.text) != null) {
                    event.id = id
                    return
                }
            }
        }
        if (cleanMessages.isNotEmpty()) {
            for (regex in cleanMessages) {
                if (regex.find(event.text) != null) {
                    event.cancel()
                    return
                }
            }
        }
    }


}