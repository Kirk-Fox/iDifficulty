# iDifficulty

View this plugin on Spigot's website [here](https://www.spigotmc.org/resources/idifficulty.95730/)

This plugin aims to allow players with various different playstyles to all enjoy the same server together without giving certain players advantages over others. Players can choose a difficulty setting that suits their liking.

Currently, difficulty settings affect whether players keep their inventory and/or experience after death, the damage from mobs, and the amount of time poison from bees and cave spiders last. To counterbalance the advantage players on easier difficulties would have, harder difficulties give bonuses to the amount of experience dropped by ores and mobs as well as having a chance that ores and mobs will drop double their regular loot.

## Commands
- __/idiff__ - Gives a description of the plugin as well as the version.
- __/idiff set__ \<difficulty\> [\<player\>] - Sets your difficulty or another player's difficulty to the setting specified. By default, only operators can change other players' difficulties.
- __/idiff list__ - Gives a list of applicable difficulties.
- __/idiff info__ \<difficulty\> - Gives a description and details about the difficulty setting specified.
- __/idiff view__ [\<player\>] - Gives your own difficulty or the difficulty of the player specified. By default, only operators can view other players' difficulties.
- __/idiff reload__ - Reloads the config file for the plugin. (to be implemented)

## Permissions
This plugin's permission nodes are
- __idifficulty.*__ - Gives all permissions.
- __idifficulty.set__ - Allows players to use __/idiff set__ on themselves. Players have this permission by default.
- __idifficulty.set.others__ - Allows players to use __/idiff set__ on other players.
- __idifficulty.view__ - Allows players to use __/idiff view__ on themselves. Players have this permission by default.
- __idifficulty.view.others__ - Allows players to use __/idiff__ view on others.
- __idifficulty.list__ - Allows players to use __/idiff list__. Players have this permission by default.
- __idifficulty.info__ - Allows players to use __/idiff info__. Players have this permission by default.
- __idifficulty.ignoredelay__ - Allows the player to circumvent the delay between changing difficulties.
- __idifficulty.reload__ - Allows the player to reload the config file. (to be implemented)
- __idifficulty.updatecheck__ - Players with this permission will be notified when a new version of the plugin is available. (to be implemented)
- __idifficulty.diff.[difficulty_name]__ - If a difficulty is toggled to require a permission, this permission node is required to switch to it with __[difficulty_name]__ replaced by the name of the difficulty in the config file. (to be implemented)

## Configuration
In this plugin's configuration, server owner's may do the following

- Adjust the effects of difficulties
- Add new difficulties
- Remove difficulties
- Change default difficulty
- Change how often players may change their difficulty (including never)
- Toggle certain effects of the plugin


## Possible Future Features:
The following are a few features that I am considering working on in the future

- ~~Hunger effects based on difficulty~~ (to be implemented in v0.2.0)
- An optional peaceful difficulty that will cause mobs to do no damage but also drop no loot or experience
- ~~The option to require a permission for certain difficulty settings~~ (to be implemented in v0.2.0)
- ~~Dividing the chance for double items from mobs and ores into two separate settings (one for mobs and one for ores)~~ (to be implemented in v0.2.0)
- ~~Dividing the experience bonus from mobs and ores into two separate settings (one for mobs and one for ores)~~ (to be implemented in v0.2.0)
- Vault interaction involving losing an amount of money on death depending on difficulty (far future)
- Plugin integration that will revoke or give permissions to players depending on their difficulty (far future)
- ~~Further backwards compatibility~~ (Backwards compatability to 1.8 will likely be implemented in v0.2.0)
- Different difficulties allowing custom permissions

If you notice any issues, please report them on the issues tab
