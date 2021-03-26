# HoloItems
A plugin API to implement custom items for a minecraft server. This is not a plugin on its own and must be shaded into another spigot plugin to be used.

# API
An example of implementation is coming soon. Start by calling `HoloItemsAPI.setup(plugin);` to setup the API.

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
        <version>0.5</version>
        <type>jar</type>
    </dependency>
</dependencies>
```
Note that `<scope>provided</scope>` is not included because this API must be shaded into your own jar