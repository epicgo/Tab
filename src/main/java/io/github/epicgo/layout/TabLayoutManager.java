package io.github.epicgo.layout;

import org.bukkit.entity.Player;

public abstract class TabLayoutManager {

    public abstract TabLayout getLayout(Player player);

    protected TabLayout createTabLayout(Player player) {
        // Aquí puedes inicializar un nuevo objeto TabLayout según tus necesidades
        return new TabLayout();
    }
}

