# PbtADiscordBot
A customizable discord bot for Powered By The Apocalypse Games

-Primary functions
1) Dice roller
2) Integrate moves and stats from a google sheet keeper

# Register Game Info
To register a game, add the discord bot to your channel, and type
register new game /sheetID/
The sheet ID can be obtained from the URL of the sheet, for example

URL: https://docs.google.com/spreadsheets/d/1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqHPgaY/

SheetID: /1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqHPgaY/


# How To Roll

**!roll xdx +/-Modifier +Stat MoveName adv/dis**

**xdx** - Dice Notation: the number and size of dice to roll, this tag may be included multiple times to roll different dice. When used multiple times, the adv/dis tags should immediately follow the dice to receive the effect, e.g. roll 2d6 adv 1d4. If no dice notation tags are included in a roll command, the default dice (set in the properties tab of the game spreadsheet) will be rolled.

**+/-Modifier** : any integer to be added to the sum of the rolls

**+Stat** : the stat modifier to be used for the roll, the system will get the live value from the spreadsheet (minus any debility penalty)

**MoveName**: if a move name or partial move name is included the text of that move will be printed along with the roll result. If the move includes a single "roll +STAT" in it's text, (where STAT is a stat registered in the properties file), a +Stat tag for that stat is added to the roll command if one is not speicified. If used as a command without "roll", it will print the move text (without rolling)

**adv/dis**: roll and additional die, and drop the highest/lowest result

# GoogleSheet Setup Info
To enable a google sheet keeper to be used with this bot

1) Add a properties tab, copy the required properties from the example, and modify their values to correspond to the relevant locations of your sheet.
2) Moves loading: moves need to be seperated by blank cells, start with the Move Name in a cell, the move text in subsequent cells below (or checkbox true/false values). A blank cell will signify the end of text for a move, to create indented lists use =" " (this adds a single space to the cell, define it with the formula to prevent googlesheets from clearing it because it thinks it is empty). To specify that a move modifies a basic move, add the baic move in parenthesis to the move name, e.g. Hard To Kill (Last Breath), this will cause the basic move to be loaded with modifying move text appended to it to that playbooks moves (and then the player uses that move, it will print the appended text).
3) Character sheets need to all be in a single tab, setup in a series of columns (width defined by the width property)
4) Stats are defined in the properties tab, any property with the prefix "stat_" can be used in rolls and, and must also have a corresponding entry with a "penalty" suffix (this is used to check for debility).