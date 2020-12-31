# Diskord Starter - A Kotlin Discord SDK Starter Project

A starter project to quickly ramp up with the Diskord library.

## What is included?

* A basic Gradle configuration with the following dependencies:
    * Diskord API (JVM)
    * A basic logger implementation (can be replaced with a more advanced library)
* A very simple Diskord bot implementing a ping example

## How do I use this?

Before this project can be used, a Diskord API bot token will be required.  Check the Discord API
[documentation](https://discord.com/developers/docs/intro) for information on how to acquire this.

Once a bot token has been acquired, create a file in `src/main/resources` called `bot-token.txt` and paste in the bot 
token.  This file is explicitly excluded from Git, so it does not accidentally get publicly exposed.

After creating the bot token file, simply run the main function through your IDE or run the Gradle `run` task 
(`gradlew run` on Windows CLI, `./gradlew run` on Linux or MacOS).

## Frequently Asked Questions

* What is the license of this starter code?
    * This starter code is available under the public domain.  Where public domain is not available, this code is also 
      available under the [WTFPLv2](https://choosealicense.com/licenses/wtfpl/).
* Where can I find the Diskord documentation?
    * Refer to the [project page](https://gitlab.com/jesselcorbett/diskord) on GitLab.
* I don't see my bot! How do I get it to join my guild?
    * Refer to the [Bot Authorization](https://discord.com/developers/docs/topics/oauth2#bots) documentation section of 
      the Discord API documentation.
* Can I contact you to ask a question/contribute to the project/report a bug?
    * We've got a [discord server](https://discord.gg/UPTWsZ5) for just that!
