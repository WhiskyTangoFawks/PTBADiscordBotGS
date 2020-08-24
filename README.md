# PbtADiscordBot
A customizable discord bot for Powered By The Apocalypse Games

-Primary functions
1) Dice roller that automatically applies relevant bonuses and penalties
2) Integrate moves, stats, and resources from a google sheet

## Getting Help
For full documentation, check out the wiki
https://github.com/WhiskyTangoFawks/PTBADiscordBotGS/wiki

Join my Dev Channel on Discord for help setting up and using the bot 

https://discord.gg/NqPrvJ

## Adding the Bot to a Server
https://discord.com/api/oauth2/authorize?client_id=655109520577527821&permissions=2048&scope=bot

## Compatible Sheets
To use the bot for a game, make a copy of one of the compatible sheets, add your players and the Discord Bot as editors (you can also select "share with the same people" while copying to maintain the sheets bot-shared status).

Dungeon World hhttps://docs.google.com/spreadsheets/d/12ovZUJX4ZwMoVobi7lx1cI0jlOYLlkwawS9_cBFBGN8/

HomeBrew World https://docs.google.com/spreadsheets/d/1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqHPgaY/

## How To Register A Game
After adding the bot to your channel, share the Google Sheet with the bot either by allowing anyone with the link to edit, or specifically sharing it with _discordbotserviceaccount@homebrewworld-1592647481774.iam.gserviceaccount.com_

After share permissions are setup, copy and paste the URL of your google sheet into the chat.

Resources and Stats are not stored in the bot, they are always retreived fresh from the sheet. All other information (moves, discord name assignments, etc) is stored in the bot, any changes to the values in the sheet will not be reflected until the game is re-registered.


## How To Roll

| tag                 | description                                                                                                            |
|---------------------|------------------------------------------------------------------------------------------------------------------------|
| xdx (Dice Notation) | Specifies dice to be rolled. If not provided, the default system dice will be rolled.                                  |
| +/-Modifier         | integer value to add to the roll result                                                                                |
| +Stat               | adds the associated value (and debility, if present) from the sheet                                                    |
| MoveName            | Prints the move text out as part of the roll. If a single "Roll +Stat occurs in the move text, will also add that stat |
| adv                 | advantage, rolls an extra die drops the lowest                                                                         |
| dis                 | disadvantage, rolls an extra die and drops the highest                                                                 |

## Example Commands

| Example Commands | Result                                                                                                                                                                       |
|------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _!roll_            | rolls the dice specified in the default_system_dice property                                                                                                                 |
| _!roll +2_         | rolls default dice, adds 2 to the result                                                                                                                                     |
| _!roll +STR_       | rolls the default dice, retrieves the STR stat from the players sheet, and if a debility is marked against it applies the debility defined in the stat_debility_tag property |
| _!roll hack_       | rolls the default dice, and prints the move text for a move that starts with "hack" (e.g. hack and slash).                                                                   |
| _!1d8 +2 adv_      | rolls 1d8 +2 with advantage                                                                                                                                                  |
| _!hack_            | prints the text for a move starting Hack (e.g. hack and slash)                                                                                                               |

## Resources
Resources registered on the sheet can be modified with bot commands. Type the resource name, and the amount to add

Example: _!hp +2_