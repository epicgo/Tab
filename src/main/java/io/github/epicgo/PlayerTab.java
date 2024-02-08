package io.github.epicgo;

import io.github.epicgo.reflect.MinecraftReflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Clase que gestiona la modificación del Tablist de los jugadores en el servidor.
 */
public class PlayerTab {

    // Perfiles falsos, latencias y líneas de texto para modificar el Tab de jugadores
    private final Object[] profiles = new Object[80];
    private final int[] latencies = new int[80];
    private final String[] textLines = new String[80];

    // Jugador asociado al objeto PlayerTab
    public Player player;

    // Constructor para PlayerTab
    public PlayerTab(Player player) {
        this.player = player;

        // Mostrar jugadores reales y perfiles falsos al inicializar
        showFakePlayers();
        hideRealPlayers();
    }

    public void removeTab() {
        hideFakePlayers();
        showRealPlayers();
    }

    // Método para mostrar en el Tab a todos los jugadores reales en el servidor
    private void showRealPlayers() {
        for (Player target : Bukkit.getOnlinePlayers()) {
            // Enviar paquete para añadir jugador real al Tab
            MinecraftReflection.sendPlayerInfoPacket(player, MinecraftReflection.EnumPlayerInfoAction.ADD_PLAYER, target);
        }
    }

    // Método para esconder del Tab a todos los jugadores reales en el servidor
    private void hideRealPlayers() {
        for (Player target : Bukkit.getOnlinePlayers()) {
            // Enviar paquete para remover jugador real del Tab
            MinecraftReflection.sendPlayerInfoPacket(player, MinecraftReflection.EnumPlayerInfoAction.REMOVE_PLAYER, target);
        }
    }

    // Método para mostrar todos los perfiles falsos creados para modificar el Tab al jugador
    private void showFakePlayers() {
        for (int index = 0; index < 80; index++) {
            // Crear perfiles falsos con UUIDs aleatorios y nombres de equipo formateados
            MinecraftReflection.sendPlayerInfoPacket(player, MinecraftReflection.EnumPlayerInfoAction.ADD_PLAYER, profiles[index] = MinecraftReflection.createGameProfile(UUID.randomUUID(), getTeamName(index)), latencies[index] = 0, MinecraftReflection.EnumGamemode.NOT_SET, textLines[index] = " ");
        }
    }

    // Método para esconder todos los perfiles falsos del jugador
    private void hideFakePlayers() {
        for (Object profile : profiles) {
            // Enviar paquete para remover perfil falso del Tab
            MinecraftReflection.sendPlayerInfoPacket(player, MinecraftReflection.EnumPlayerInfoAction.REMOVE_PLAYER, profile, 0, MinecraftReflection.EnumGamemode.NOT_SET, " ");
        }
    }

    // Método para obtener un nombre de equipo formateado basado en un valor entero
    private String getTeamName(final int valueToFormat) {
        if (valueToFormat >= 10) {
            // Formato con dos colores para valores mayores o iguales a 10
            int firstDigit = valueToFormat / 10;
            int secondDigit = valueToFormat % 10;
            return ChatColor.BOLD + "" + ChatColor.GREEN + ChatColor.UNDERLINE + ChatColor.YELLOW + ChatColor.COLOR_CHAR + firstDigit + ChatColor.COLOR_CHAR + secondDigit;
        } else {
            // Formato con un solo color para valores menores a 10
            return ChatColor.BOLD + "" + ChatColor.BLACK + ChatColor.COLOR_CHAR + valueToFormat;
        }
    }
}
