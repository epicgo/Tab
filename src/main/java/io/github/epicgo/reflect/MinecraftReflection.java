package io.github.epicgo.reflect;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    // Clase del paquete de paquetes de información del jugador
    public static final Class<?> PLAYER_INFO_CLASS = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
    // Clase de enumeración para las acciones de información del jugador
    public static final Class<?> PLAYER_INFO_ENUM_CLASS = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
    // Clase de datos de información del jugador en el paquete de información del jugador
    public static final Class<?> PLAYER_INFO_DATA_CLASS = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$PlayerInfoData");
    // Clase del componente de chat de texto
    public static final Class<?> CHAT_COMPONENT_TEXT_CLASS = Reflection.getMinecraftClass("ChatComponentText");
    // Clase de enumeración para los ajustes del mundo, incluido el modo de juego
    public static final Class<?> WORLD_SETTINGS_ENUM_CLASS = Reflection.getMinecraftClass("WorldSettings$EnumGamemode");
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
     * @param uuid         el UUID del perfil de juego.
     * @param name         el nombre del perfil de juego.
     * @param propertyName el nombre de la propiedad adicional a agregar.
     * @param value        el valor de la propiedad adicional.
     * @param signature    la firma de la propiedad adicional.
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
     * Envía un paquete de información del jugador al jugador especificado.
     *
     * @param player        el jugador al que se enviará el paquete.
     * @param action        la acción del paquete de información del jugador.
     * @param gameProfile   el perfil del jugador.
     * @param ping          el ping del jugador.
     * @param gamemode      el modo de juego del jugador.
     * @param componentText el texto del componente de chat.
     */
    public static void sendPlayerInfoPacket(Player player, EnumPlayerInfoAction action, Object gameProfile, int ping, EnumGamemode gamemode, String componentText) {
        // Crea el paquete de información del jugador y lo envía al jugador especificado
        sendPacket(player, createPlayerInfoPacket(action, createPlayerInfoData(gameProfile, ping, gamemode, componentText)));
    }

    /**
     * Envía un paquete de información del jugador al jugador especificado.
     *
     * @param player el jugador al que se enviará el paquete.
     * @param action la acción del paquete de información del jugador.
     * @param target el jugador para el que se crea el paquete.
     */
    public static void sendPlayerInfoPacket(Player player, EnumPlayerInfoAction action, Player target) {
        // Envía al jugador especificado el paquete de información del jugador creado para el jugador objetivo
        sendPacket(player, createPlayerInfoPacket(action, target));
    }

    /**
     * Crea un paquete de información del jugador para un jugador específico.
     *
     * @param action la acción del paquete de información del jugador.
     * @param target el jugador para el que se crea el paquete.
     * @return un paquete de información del jugador.
     */
    public static Object createPlayerInfoPacket(EnumPlayerInfoAction action, Player target) {
        // Crea un array de objetos para el jugador objetivo
        Object objectArray = Array.newInstance(CRAFT_PLAYER_CLASS, 1);
        Array.set(objectArray, 0, getEntityPlayer(target));

        // Crea y devuelve el paquete de información del jugador
        return Reflection.getConstructor(PLAYER_INFO_CLASS, PLAYER_INFO_ENUM_CLASS, objectArray.getClass())
                .invoke(Reflection.getEnum(PLAYER_INFO_ENUM_CLASS, action.name()), objectArray);
    }

    /**
     * Crea un paquete de información del jugador con datos de información específicos.
     *
     * @param action    la acción del paquete de información del jugador.
     * @param infoData  los datos de información del jugador.
     * @return un paquete de información del jugador.
     */
    public static Object createPlayerInfoPacket(EnumPlayerInfoAction action, Object infoData) {
        // Crea un nuevo paquete de información del jugador
        Reflection.ConstructorInvoker constructor = Reflection.getConstructor(PLAYER_INFO_CLASS);
        Object packetInvoked = constructor.invoke();

        // Establece la acción y los datos de información en el paquete
        Object actionEnum = Reflection.getEnum(PLAYER_INFO_ENUM_CLASS, action.name());
        Reflection.getField(PLAYER_INFO_CLASS, "a", Object.class).set(packetInvoked, actionEnum);
        Reflection.getField(PLAYER_INFO_CLASS, "b", Object.class).set(packetInvoked, Collections.singletonList(infoData));

        return packetInvoked;
    }


    /**
     * Crea un objeto de datos de información del jugador utilizando reflection.
     *
     * @param profile       el perfil del jugador.
     * @param ping          el ping del jugador.
     * @param gamemode      el modo de juego del jugador.
     * @param componentText el texto del componente de chat.
     * @return un objeto de datos de información del jugador.
     */
    public static Object createPlayerInfoData(Object profile, int ping, EnumGamemode gamemode, String componentText) {
        // Obtiene el constructor del paquete de información del jugador
        Reflection.ConstructorInvoker constructorInvoker = Reflection.getConstructor(PLAYER_INFO_DATA_CLASS, 0);

        // Lista de parámetros para el constructor
        List<Object> parameters = new ArrayList<>();

        // Añade el valor null si el constructor tiene un primer parámetro
        if (constructorInvoker.getParameterTypes()[0] == PLAYER_INFO_CLASS) {
            parameters.add(null);
        }

        // Añade los parámetros requeridos al constructor
        parameters.add(profile); // Perfil del jugador
        parameters.add(ping); // Ping del jugador
        parameters.add(Reflection.getEnum(WORLD_SETTINGS_ENUM_CLASS, gamemode.name())); // Modo de juego
        parameters.add(Reflection.getConstructor(CHAT_COMPONENT_TEXT_CLASS, String.class)
                .invoke(ChatColor.translateAlternateColorCodes('&', componentText))); // Texto del componente de chat

        // Invoca el constructor con los parámetros proporcionados y devuelve el objeto creado
        return constructorInvoker.invoke(parameters.toArray());
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

    /**
     * Enumeración que representa los diferentes modos de juego disponibles para los jugadores.
     */
    public enum EnumGamemode {
        NOT_SET,    // Modo de juego no establecido
        SURVIVAL,   // Modo de supervivencia
        CREATIVE,   // Modo creativo
        ADVENTURE,  // Modo de aventura
        SPECTATOR;  // Modo espectador
    }

    /**
     * Enumeración que define las acciones que se pueden realizar en la información del jugador.
     */
    public enum EnumPlayerInfoAction {
        ADD_PLAYER,             // Agregar jugador
        UPDATE_GAMEMODE,        // Actualizar modo de juego
        UPDATE_LATENCY,         // Actualizar latencia
        UPDATE_DISPLAY_NAME,    // Actualizar nombre de pantalla
        REMOVE_PLAYER;          // Eliminar jugador
    }

}
