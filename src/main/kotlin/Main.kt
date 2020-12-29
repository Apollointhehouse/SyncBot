package com.example

import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands

/*
 * This can be replaced with any method to load the bot token.  This specific method is provided only for convenience
 * and as a way to prevent accidental disclosure of bot tokens.
 */
private val BOT_TOKEN = try {
    ClassLoader.getSystemResource(".bot-token").readText().trim()
} catch (error: Exception) {
    throw RuntimeException("Failed to load bot token. Make sure to create a file named .bot-token in" +
            " src/main/resources and paste the bot token into that file.", error)
}

suspend fun main(args: Array<String>) {
    bot(BOT_TOKEN) {
        commands {
            command("ping") {
                reply("Pong!")
            }
        }
    }
}
