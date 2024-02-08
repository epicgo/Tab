package io.github.epicgo.reflect;

import org.bukkit.entity.Player;

import java.util.UUID;

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
     * Clase que representa la clase GameProfile de com.mojang.authlib.
     */
    private static final Class<?> GAME_PROFILE_CLASS = Reflection.getUntypedClass("com.mojang.authlib.GameProfile");
    /**
     * Clase que representa la clase Property de la biblioteca de autenticación de Mojang.
     */
    private static final Class<?> PROPERTY_CLASS = Reflection.getUntypedClass("com.mojang.authlib.Property");
    /**
     * Método invocador para obtener las propiedades de un objeto GameProfile.
     */
    private static final Reflection.MethodInvoker GET_GAME_PROFILE_PROPERTIES_METHOD = Reflection.getSingleMethod(GAME_PROFILE_CLASS, "getProperties");

    /**
     * Constructor invocador para crear instancias de la clase GameProfile.
     */
    private static final Reflection.ConstructorInvoker GAME_PROFILE_CONSTRUCTOR = Reflection.getConstructor(GAME_PROFILE_CLASS, UUID.class, String.class);
    /**
     * Constructor invocador para crear instancias de la clase Property.
     */
    private static final Reflection.ConstructorInvoker PROPERTY_CONSTRUCTOR = Reflection.getConstructor(PROPERTY_CLASS, String.class, String.class, String.class);

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
     * Crea un nuevo perfil de juego con el UUID y el nombre especificados.
     *
     * @param uuid el UUID del perfil de juego.
     * @param name el nombre del perfil de juego.
     * @return el perfil de juego creado.
     * @throws RuntimeException si ocurre un error durante la creación del perfil de juego.
     */
    public static Object createGameProfile(UUID uuid, String name) {
        try {
            return GAME_PROFILE_CONSTRUCTOR.invoke(uuid, name);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el perfil de juego.", e);
        }
    }

    /**
     * Crea un nuevo perfil de juego con el UUID y el nombre especificados, y agrega propiedades adicionales al perfil.
     *
     * @param uuid       el UUID del perfil de juego.
     * @param name       el nombre del perfil de juego.
     * @param propertyName el nombre de la propiedad adicional a agregar.
     * @param value      el valor de la propiedad adicional.
     * @param signature  la firma de la propiedad adicional.
     * @return el perfil de juego creado con las propiedades adicionales agregadas.
     * @throws RuntimeException si ocurre un error durante la creación o modificación del perfil de juego.
     */
    public static Object createGameProfileWithProperties(UUID uuid, String name, String propertyName, String value, String signature) {
        try {
            // Crea el perfil de juego base
            Object gameProfile = createGameProfile(uuid, name);

            // Obtiene el mapa de propiedades del perfil de juego
            Object propertyMap = GET_GAME_PROFILE_PROPERTIES_METHOD.invoke(gameProfile);

            // Agrega la propiedad adicional al mapa de propiedades
            Reflection.MethodInvoker putMethod = Reflection.getSingleMethod(propertyMap.getClass(), "put", Object.class, Object.class);
            putMethod.invoke(propertyName, PROPERTY_CONSTRUCTOR.invoke(propertyName, value, signature));

            return gameProfile;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear o modificar el perfil de juego.", e);
        }
    }


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
