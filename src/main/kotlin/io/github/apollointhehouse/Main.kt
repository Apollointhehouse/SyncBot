package io.github.apollointhehouse

import com.jessecorbett.diskord.api.channel.CreateWebhook
import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.api.webhook.CreateWebhookMessage
import com.jessecorbett.diskord.bot.BotContext
import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.classicCommands
import com.jessecorbett.diskord.bot.events
import com.jessecorbett.diskord.util.isFromBot
import com.jessecorbett.diskord.util.isFromUser
import com.jessecorbett.diskord.util.isFromWebhook
import java.io.File
import java.util.*

val configPath = ClassLoader.getSystemResource("config.properties").file.replace("%20", " ")
val config = File(configPath).reader().use { Properties().apply { load(it) } }
val botToken: String = config.getProperty("botToken")
val syncedChannels = config.getProperty("syncedChannels").split(" ").toMutableSet()

suspend fun main() = bot(botToken) {
    events {
        onMessageCreate { onMessage(it) }
    }

    classicCommands("!sync ") {
        command("register") { register(it) }
        command("unregister") { unregister(it) }
    }
}

fun saveConfig() {
    val writer = File(configPath).writer()
    config.setProperty("syncedChannels", syncedChannels.joinToString(" "))
    config.store(writer, null)
}

suspend fun BotContext.register(message: Message) {
    if (message.isFromBot || message.isFromWebhook) return
    if (!syncedChannels.add(message.channelId)) {
        message.respond("Channel is already a synced channel")
        return
    }
    saveConfig()
    channel(message.channelId).createWebhook(CreateWebhook("SyncBot"))
    message.respond("Registered channel as synced channel")
}

suspend fun BotContext.unregister(message: Message) {
    if (message.isFromBot || message.isFromWebhook) return
    if (!syncedChannels.remove(message.channelId)) {
        message.respond("Channel is not a synced channel")
        return
    }
    saveConfig()
    val webhook = channel(message.channelId)
        .getWebhooks()
        .first { it.defaultName == "SyncBot" }
    webhook(webhook.id).deleteWebhook()

    message.respond("Unregistered channel as synced channel")
}

suspend fun BotContext.onMessage(message: Message) {
    if (!message.isFromUser) return
    if (message.content.startsWith("!sync ")) return
    val user = message.author
    val name = user.displayName
    val avatarURL = "https://cdn.discordapp.com/avatars/${user.id}/${user.avatarHash}.png"
    val content = message.content

    val channelIDs = syncedChannels
        .asSequence()
        .takeIf { message.channelId in it }
        ?.filter { it != message.channelId } ?: return

    for (channelID in channelIDs) {
        val webhook = channel(channelID)
            .getWebhooks()
            .first { it.defaultName == "SyncBot" }
        val token = webhook.token ?: run {
            message.respond("Failed to send message to synced channel")
            return
        }
        webhook(webhook.id).execute(token, CreateWebhookMessage(content, name, avatarURL))
    }
}
