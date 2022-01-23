# HoloItems
A plugin API to implement custom items for a minecraft server. This is not a plugin on its own and must be shaded into another spigot plugin to be used.

# Setup
Include this in your pom.xml
```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://raw.githubusercontent.com/StrangeOne101/HoloItemsAPI/repository/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.strangeone101</groupId>
        <artifactId>HoloItemsAPI</artifactId>
        <version>0.6.3</version>
        <type>jar</type>
    </dependency>
</dependencies>
```
Note that `<scope>provided</scope>` is not included because this API must be shaded into your own jar

Then include this within your plugin onEnable()
```java
HoloItemsAPI.setup(this);

//Register/Initialize all items here
```
# API
## Defining Custom Items
You can also create your own classes that extend CustomItem.
```java
NETHER_DIAMOND = new CustomItem("nether_diamond", Material.DIAMOND)
    .setDisplayName(ChatColor.RED + "Nether Diamond")
    .addLore(ChatColor.DARK_GRAY + "Crafting Ingredient").addLore("")
    .addLore(ChatColor.GRAY + "Hot to the touch").setInternalID(1).register();
```
The internal ID here is the custom model number set on the item.

You can also implement the following interfaces to add more things to them:
- Interactable - When the item can be right clicked
- BlockInteractable - When the item is meant to be right clicked on certain blocks
- EntityInteractable - When the item is meant to be right clicked on entities
- Edible - When the item can be eaten
- Placable - When the item can be placed (note: this is intended for when you run code when the item is placed. There is nothing that keeps track of custom blocks placed down)
- Swingable - When the item is meant to be swung like a sword, and code can be run when an entity is hit

For anything outside of this, see the ItemEvent system
## Giving items to players
```java
ItemStack item = NETHER_DIAMOND.build(player);
player.getInventory().addItem(item);
```
The player passed to the build method can be null. This allows items to store who crafted them or who owns them if they wanted to (useful for portable pets), but doesn't do this by default. 
## Recipes
Recipes must be registered if they use custom items, or else they can't be crafted (all crafting with custom items is denied so custom items can't be used in place of vanilla items)
```java
ShapelessRecipe netheriteRecipe = new ShapelessRecipe(new NamespacedKey(this, "netherite_ingot"), new ItemStack(Material.NETHERITE_INGOT));
netheriteRecipe.addIngredient(Material.IRON_INGOT);
netheriteRecipe.addIngredient(new CIRecipeChoice(NETHER_DIAMOND)); //CI = CustomItem Recipe Choice
RecipeManager.registerRecipe(netheriteRecipe); //Register to the API AND bukkit
```
## ItemEvents
Like how bukkit has events, instead of registering listeners to listen for when something happens and manually checking if your custom item is active, you can use this.
### Example 1: Holy armor
```java
@ItemEvent(active = ActiveConditions.EQUIPED, target = Target.SELF)
public void onPlayerDamage(EventContext context, EntityDamageByEntityEvent event) {
    if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
        for (Class classCheck: new Class[] {Zombie.class, AbstractSkeleton.class, Zoglin.class, Wither.class}) {
            if (classCheck.isAssignableFrom(event.getDamager().getType().getEntityClass())) {
                event.setDamage(event.getDamage() * 0.75);
                return;
            }
        }
    }
    damageItem(context.getStack(), 1, context.getPlayer());
}
```
Here is an example for "Holy armor". If you have this method in your CustomItem class, when equipped, undead will damage you 25% less.

Things to note:
- `ActiveConditions.EQUIPPED` - The active conditions specifies when this ItemEvent should trigger. Equipped means that it must be worn as a piece of armor.
- `Target.SELF` - This method only fires for the player that it's currently equipped for. If it was set to `ALL`, then this method would fire for all players if any player in the world has holy armor on. This isn't what we want.
- `damageItem` - This damages the custom item. CustomItems can have custom durability, so this lets the plugin handle it
- `EntityDamageByEntityEvent event` - This specifies what event this ItemEvent will trigger for. In this case, it triggers when the EntityDamageByEntityEvent fires AND the custom item is equipped
- `EventContext context` - This holds the information about the holder of the custom item

## Example 2: A normal listener within an item
```java
@ItemEvent(active = ActiveConditions.NONE, target = Target.ALL) 
public void onDeath(EventContext context, EntityDeathEvent event) {
    if (event.getEntity() instanceof Animals) {
        if (event.getEntity().getCustomName() != null) {
            ItemStack nametag = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = nametag.getItemMeta();
            meta.setCustomName(event.getEntity().getCustomName());
            nametag.setItemMeta(meta);
            event.getDrops().add(nametag);
        }
    }
}
```
Instead of making a listener for our item, we can do it straight from the CustomItem class. 
`ActiveConditions.NONE` mean the current item doesn't have to be on any player at all for this to trigger. 
`Target.ALL` means it targets all events.