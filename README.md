# PbtADiscordBot
A customizable discord bot for Powered By The Apocalypse Games

-Primary functions
1) Dice roller
2) Integrate moves, stats, and resources from a google sheet

#Adding the Bot to a Server
https://discord.com/api/oauth2/authorize?client_id=655109520577527821&permissions=2048&scope=bot

#Compatible Sheets
To use the bot for a game, make a copy of one of the compatible sheets, add your players and the Discord Bot as editors.

HomeBrew World https://docs.google.com/spreadsheets/d/1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqHPgaY/

#How To Register A Game
After adding the bot to your channel, share the Google Sheet with the bot either by allowing anyone with the link to edit, or specifically sharing it with _discordbotserviceaccount@homebrewworld-1592647481774.iam.gserviceaccount.com_

After share permissions are setup, copy and paste the URL of your google sheet into the chat.

###Debug Mode
add debug to the end of the URL to launch the sheet reader in debug mode. This makes the loader much slower, but prints to the chat everything that gets loaded. This is primarily for users trying to modify a the notes that determine how the bot loads the google sheet, to verify that everything is loading correctly

# How To Roll

| tag                 | description                                                                                                            |
|---------------------|------------------------------------------------------------------------------------------------------------------------|
| xdx (Dice Notation) | Specifies dice to be rolled. If not provided, the default system dice will be rolled.                                  |
| +/-Modifier         | integer value to add to the roll result                                                                                |
| +Stat               | adds the associated value (and debility, if present) from the sheet                                                    |
| MoveName            | Prints the move text out as part of the roll. If a single "Roll +Stat occurs in the move text, will also add that stat |
| adv                 | advantage, rolls an extra die drops the lowest                                                                         |
| adv                 | disadvantage, rolls an extra die and drops the highest                                                                 |

###Example Commands

| Example Commands | Result                                                                                                                                                                       |
|------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _!roll_            | rolls the dice specified in the default_system_dice property                                                                                                                 |
| _!roll +2_         | rolls default dice, adds 2 to the result                                                                                                                                     |
| _!roll +STR_       | rolls the default dice, retrieves the STR stat from the players sheet, and if a debility is marked against it applies the debility defined in the stat_debility_tag property |
| _!roll hack_       | rolls the default dice, and prints the move text for a move that starts with "hack" (e.g. hack and slash).                                                                   |
| _!1d8 +2 adv_      | rolls 1d8 +2 with advantage                                                                                                                                                  |
| _!hack_            | prints the text for a move starting Hack (e.g. hack and slash)                                                                                                               |

#Resources
Resources registered on the sheet can be modified with bot commands. Type the resource name, and the amount to add

Example: _!hp +2_

# GoogleSheet Setup Info
To enable a google sheet keeper to be used with this bot, you have to add the required notes to the sheet (right click a cell, insert note). These notes are what the bot uses to determine what to load, and how to load it

##How the Bot Reads the Sheet
The bot will read the entire sheet, and uses notes to decide what and how to load into the bot. Sheets should not contain any other notes.

##Notes and their Functions

| new_playbook           | specifies the start of a new playbook                                                                                                                                    |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| discord_name           | specifies the discord name to associate with a playbook. Required once for each playbook                                                                                 |
| basic_move             | specifies a move to be loaded as a basic move, available to all players                                                                                                  |
| playbook_move          | specifies a move to be loaded as a playbook move, available only to the discord name registered to the playbook                                                          |
| stat=stat_name         | specifies a cell to be assigned as a stat. Cell must be an integer value.                                                                                                |
| stat_penalty=stat_name | specifies a cell to be assigned as a stat penalty. Cell must be a boolean value.                                                                                         |
| resource               | Specifies a cell (or multiple cells, if applied with teh same name more than once in a playbook) to be loaded as a resource. Cells are either a boolean or integer value |
| default_dice=move_name | specifies dice to override default dice for a given move: e.g. Cell must contain dice notation.                                                                                                        |

##Loading Moves
If no additional tags are supplied, a move will be loaded using the cell with the move note as the name, and the cell below as the move text.
If a cell is a boolean value (TRUE or FALSE, e.g. a checkbox), if TRUE, then the loader continues to load the cell to the right, if FALSE, the loader stops loading the line and moves to the next. If the boolean cell is cell with the note, the move will only be loaded if TRUE.

If move text has a single Roll +Stat listed, then when a player with that stat rolls the move, the bot will also use that stat, and apply the associated debility penalty if applicable

##Secondary Moves
Playbook moves with modify basic moves can load the modified basic move into the playbook. To do so, simply add the basic move in parenthesis to the name of the secondary move

Example: Hard to Kill (Last Breath)

This will also accept a comma separated list.

Example: Stealthy (Defy Danger, Scout Ahead, Struggle as One)

In both cases, a modified version of the basic move will be loaded into the set of stored playbook moves for that player.

###Optional Move note tags
The following additional tag may be provided on move notes to change how the moves are loaded. Tags need to be separated with a colon :

| tag       | description                                                                                                             |
|-----------|-------------------------------------------------------------------------------------------------------------------------|
|(move_name)| the name of the move, if provided loads the name from note instead of from the cell                                     |
| move_text | specifies the move text, if provided the move will not load additional text                                             |
| list      | specifies a list, when provided the move loader will continue loading cells down the column until it hits a blank cell. |

example
basic_move
basic_move=Hack And Slash;move_text=When you fight in melee or close quarters, roll +STR...

#Resources
Resources can be loaded from a sheet either as a list of booleans (check boxes), or as a single integer. A min and a max value can be specified for an integer resource

Example: _resource=hp;min=0;max=20_

##Properties Tab
1) Add a properties tab, with the required properties.

| Property                | description                                                                         |
|-------------------------|-------------------------------------------------------------------------------------|
| commandchar=!           | the character used by the discord bot to recognise a command it needs to respond to |
| default_system_dice=2d6 | die notation to be used for roll commands when no die notation is specified         |
| fail_xp=true            | on a failed roll, reminds you to mark xp                                            |
| stat_debility_tag=dis   | when rolling a disabled stat, this tag is added(usaully either dis or -1)           |
