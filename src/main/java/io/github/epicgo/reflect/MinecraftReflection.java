package io.github.epicgo.reflect;

import org.bukkit.entity.Player;

public class MinecraftReflection {

    // Clase de CraftPlayer de CraftBukkit
    public static final Class<?> CRAFT_PLAYER_CLASS = Reflection.getCraftBukkitClass("entity.CraftPlayer");

    // Clase de Packet de Minecraft
    public static final Class<?> NMS_PACKET_CLASS = Reflection.getMinecraftClass("Packet");

    // Clase de EntityPlayer de Minecraft
    public static final Class<?> NMS_ENTITY_PLAYER_CLASS = Reflection.getMinecraftClass("EntityPlayer");

    // Clase de PlayerConnection de Minecraft
    public static final Class<?> NMS_PLAYER_CONNECTION_CLASS = Reflection.getMinecraftClass("PlayerConnection");

    /**
     * Método invocador para obtener el objeto 'handle' de CraftPlayer.
     */
    public static final Reflection.MethodInvoker GET_CRAFTPLAYER_HANDLE_METHOD = Reflection.getMethod(CRAFT_PLAYER_CLASS, "getHandle");

    /**
     * Invocador de método para enviar un paquete a través de la conexión de un jugador.
     */
    public static final Reflection.MethodInvoker SEND_PACKET_METHOD = Reflection.getMethod(NMS_PLAYER_CONNECTION_CLASS, "sendPacket", NMS_PACKET_CLASS);

    /**
     * Accede al campo de la conexión de un jugador en la clase EntityPlayer.
     */
    public static final Reflection.FieldAccessor<?> PLAYER_CONNECTION_FIELD = Reflection.getField(NMS_ENTITY_PLAYER_CLASS, NMS_PLAYER_CONNECTION_CLASS, 0);

    /**
     * Devuelve el objeto EntityPlayer (NMS) asociado a un jugador de Bukkit.
     *
     * @param player El jugador de Bukkit.
     * @return El objeto EntityPlayer (NMS) asociado al jugador.
     */
    public static Object getEntityPlayer(Player player) {
        return GET_CRAFTPLAYER_HANDLE_METHOD.invoke(player);
    }

    /**
     * Envía un paquete a través de la conexión de un jugador.
     *
     * @param player El jugador al que se enviará el paquete.
     * @param packet El paquete que se enviará.
     */
    public static void sendPacket(Player player, Object packet) {
        Object playerConnection = PLAYER_CONNECTION_FIELD.get(getEntityPlayer(player));
        SEND_PACKET_METHOD.invoke(playerConnection, packet);
    }
}
