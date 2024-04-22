package io.github.apollointhehouse

import com.jessecorbett.diskord.api.channel.ChannelClient
import com.jessecorbett.diskord.api.channel.CreateWebhook
import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.api.webhook.CreateWebhookMessage
import com.jessecorbett.diskord.api.webhook.WebhookClient
import com.jessecorbett.diskord.bot.BotContext
import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.classicCommands
import com.jessecorbett.diskord.bot.events
import com.jessecorbett.diskord.internal.client.RestClient
import com.jessecorbett.diskord.util.isFromBot
import com.jessecorbett.diskord.util.isFromUser
import com.jessecorbett.diskord.util.isFromWebhook

/*
 * This can be replaced with any method to load the bot token.  This specific method is provided only for convenience
 * and as a way to prevent accidental disclosure of bot tokens.
 *
 * Alternative methods might include reading from the environment, using a system property, or reading from the CLI.
 */
private val BOT_TOKEN = try {
    ClassLoader.getSystemResource("bot-token.txt").readText().trim()
} catch (error: Exception) {
    throw RuntimeException(
        "Failed to load bot token. Make sure to create a file named bot-token.txt in " +
        "src/main/resources and paste the bot token into that file.", error
    )
}

private val syncedChannels = mutableSetOf<String>()

suspend fun main() {
    val client = RestClient.default(BOT_TOKEN)

    bot(BOT_TOKEN) {
        events {
            onMessageCreate { onMessage(it, client) }
        }

        classicCommands("!sync ") {
            command("register") { register(it, client) }
            command("unregister") { unregister(it, client) }
        }
    }
}

suspend fun BotContext.register(message: Message, client: RestClient) {
    if (message.isFromBot || message.isFromWebhook) return
    if (!syncedChannels.add(message.channelId)) {
        message.respond("Channel is already a synced channel")
        return
    }
    ChannelClient(message.channelId, client).createWebhook(CreateWebhook("SyncBot"))
    message.respond("Registered channel as synced channel")
}

suspend fun BotContext.unregister(message: Message, client: RestClient) {
    if (message.isFromBot || message.isFromWebhook) return
    if (!syncedChannels.remove(message.channelId)) {
        message.respond("Channel is not a synced channel")
        return
    }
    val webhook = ChannelClient(message.channelId, client)
        .getWebhooks()
        .first { it.defaultName == "SyncBot" }
    WebhookClient(webhook.id, client).deleteWebhook()

    message.respond("Unregistered channel as synced channel")
}

suspend fun onMessage(message: Message, client: RestClient) {
    if (!message.isFromUser) return
    if (message.content.startsWith("!sync ")) return
    val user = message.author
    val name = message.partialMember?.nickname ?: user.username
    val avatarURL = "https://cdn.discordapp.com/avatars/${user.id}/${user.avatarHash}.png"
    val content = message.content

    val channelIDs = syncedChannels
        .asSequence()
        .takeIf { message.channelId in it }
        ?.filter { it != message.channelId } ?: return

    for (channelID in channelIDs) {
        val webhook = ChannelClient(channelID, client)
            .getWebhooks()
            .first { it.defaultName == "SyncBot" }

        WebhookClient(webhook.id, client).execute(webhook.token ?: "", CreateWebhookMessage(content, name, avatarURL))
    }
}
