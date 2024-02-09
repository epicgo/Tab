package io.github.epicgo;

import io.github.epicgo.layout.TabEntry;
import io.github.epicgo.layout.TabLayoutManager;
import io.github.epicgo.reflect.MinecraftReflection;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Clase que gestiona la modificación del Tablist de los jugadores en el servidor.
 */
public class PlayerTab {

    private final Set<TabEntry> entries = new ConcurrentSet<>();
    private final long setupMS;
    // Jugador asociado al objeto PlayerTab
    public Player player;

    // Constructor para PlayerTab
    public PlayerTab(Player player) {
        this.player = player;

        setupMS = System.currentTimeMillis();
    }

    /**
     * Método para eliminar la Tab personalizada del jugador.
     */
    public void removeTab() {
        // Ocultar perfiles falsos y mostrar jugadores reales
        hideFakePlayers();
        showRealPlayers();

        entries.clear();
    }

    /**
     * Método para mostrar la Tab personalizada al jugador.
     */
    public void showTab() {
        // Mostrar perfiles falsos y ocultar jugadores reales
        showFakePlayers();
        hideRealPlayers();
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
        for (int tabSlot = 0; tabSlot < 80; tabSlot++) {
            TabEntry entry = new TabEntry().setId(UUID.randomUUID()).setName(getTeamName(tabSlot)).setTabSlot(tabSlot);
            entries.add(entry);

            // Crear perfiles falsos con UUIDs aleatorios y nombres de equipo formateados
            MinecraftReflection.sendPlayerInfoPacket(player, MinecraftReflection.EnumPlayerInfoAction.ADD_PLAYER, MinecraftReflection.createGameProfile(entry.getId(), entry.getName()), entry.getPing(), MinecraftReflection.EnumGamemode.NOT_SET, entry.getTextLine());
        }
    }

    // Método para esconder todos los perfiles falsos del jugador
    private void hideFakePlayers() {
        for (TabEntry entry : entries) {
            // Enviar paquete para remover perfil falso del Tab
            MinecraftReflection.sendPlayerInfoPacket(player, MinecraftReflection.EnumPlayerInfoAction.REMOVE_PLAYER, MinecraftReflection.createGameProfile(entry.getId(), entry.getName()), entry.getPing(), MinecraftReflection.EnumGamemode.NOT_SET, entry.getTextLine());
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

    public TabEntry getTabEntryBySlot(int tabSlot) {
        return entries.stream().filter(tabEntry -> tabEntry.getTabSlot() == tabSlot).findFirst().orElse(null);
    }
}
