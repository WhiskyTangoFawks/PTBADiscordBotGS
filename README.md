# PbtADiscordBot
A customizable discord bot for Powered By The Apocalypse Games
Two primary functions
1) Dice roller
2) Print stored move text to discord chat

**Example Command**

**!roll +2** to roll 2d6 + 2

**!roll dis** to roll 2d6 with disadvantage

**!roll -1 adv** to roll 2d6-1 with advantage

**!roll 1d8 +1** to roll 1d8 +1, works with dice of any size

**!move_name** to print the name of a move stored in the moves.properties file to the chat. If no move of that name is present in the moves.properties file, it give you an error message. If you enter a partial move name, the bot will try to find a corresponding move, e.g. !defy will get the move text for "defydanger" if present in the move.properties file, or even !d will work, if no other moves are registered that start with d

**!shutdown** to shutdown the bot

To run, download and double click file. This will create a directory with a config.properties, and a moves.properties file, and give you an invalid token error.

Follow these instructions to generate a discord bot token
https://www.writebots.com/discord-bot-token/

and paste the token into the config.properties file.

Adding moves:

Moves are added as a single line to the moves.properties file, use a linebreak token for a new line. Heres an example of the Defy Danger move from Homebrew World

defydanger=Defy Danger\nWhen danger looms, the stakes are high, and you act anyway, check if another move applies. If not,roll...\n +STR to power through or test your might\n +DEX to employ speed, agility, or finesse\n +CON to endure or hold steady\n +INT to apply expertise or enact a clever plan\n +WIS to exert willpower or rely on your senses\n +CHA to charm, bluff, impress, or fit in\nOn a 10+, you pull it off as well as one could hope; on a 7-9, you can do it, but the GM will present a lesser success, a cost, or a consequence (and maybe a choice between them, or a chance to back down).