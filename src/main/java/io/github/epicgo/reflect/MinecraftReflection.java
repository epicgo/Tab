package io.github.epicgo.reflect;

public class MinecraftReflection {

    // Clase de CraftPlayer de CraftBukkit
    public static final Class<?> CRAFT_PLAYER_CLASS = Reflection.getCraftBukkitClass("entity.CraftPlayer");

    // Clase de Packet de Minecraft
    public static final Class<?> NMS_PACKET_CLASS = Reflection.getMinecraftClass("Packet");

    // Clase de EntityPlayer de Minecraft
    public static final Class<?> NMS_ENTITY_PLAYER_CLASS = Reflection.getMinecraftClass("EntityPlayer");

    // Clase de PlayerConnection de Minecraft
    public static final Class<?> NMS_PLAYER_CONNECTION_CLASS = Reflection.getMinecraftClass("PlayerConnection");

}
