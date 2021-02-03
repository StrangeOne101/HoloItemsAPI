# HoloItems
A plugin to implement custom items for HoloCons

# API
The main thing that makes up custom items is:
1. A class that extends [CustomItem](https://github.com/StrangeOne101/HoloItems/blob/master/src/main/java/com/strangeone101/holoitems/CustomItem.java)`
2. Abilities that extend [ItemAbility](https://github.com/StrangeOne101/HoloItems/blob/master/src/main/java/com/strangeone101/holoitems/ItemAbility.java)`

All custom items must be registered. Abilities are called from listeners when they should be started, and allow code to be run every tick until it's deemed unneeded anymore.

Abilities work based on the following methods:
- `start()` - Called to start calling `tick()` every tick until `remove()` is called
- `tick()` - What the ability should do every tick
- `remove()` - Stops it being called every tick

An example of how the Rushia Shield ability works:
- [Test for the item usage with a listener](https://github.com/StrangeOne101/HoloItems/blob/master/src/main/java/com/strangeone101/holoitems/listener/ItemListener.java#L48-L58)
- [Display smoke while the shield is rising](https://github.com/StrangeOne101/HoloItems/blob/master/src/main/java/com/strangeone101/holoitems/abilities/RushiaShieldAbility.java#L48-L57)
- [Spawn the ghost when the shield is fully raised](https://github.com/StrangeOne101/HoloItems/blob/master/src/main/java/com/strangeone101/holoitems/abilities/RushiaShieldAbility.java#L59-L99)
- [Move the ghost to the current position until it dies](https://github.com/StrangeOne101/HoloItems/blob/master/src/main/java/com/strangeone101/holoitems/abilities/RushiaShieldAbility.java#L102-L131)