package com.bgsoftware.superiorskyblock.hooks.support;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.utils.debug.PluginDebugger;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.plugin.config.ConfigManager;
import com.grinderwolf.swm.plugin.config.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class SlimeWorldManagerHook {

    private static Plugin slimeWorldPlugin = null;

    public static void tryWorldLoad(String worldName) {
        if (slimeWorldPlugin == null)
            return;

        SlimePlugin slimePlugin = (SlimePlugin) slimeWorldPlugin;

        WorldData worldData = ConfigManager.getWorldConfig().getWorlds().get(worldName);

        if (worldData == null)
            return;

        try {
            slimePlugin.getLoader(worldData.getDataSource()).loadWorld(worldName, false);
        } catch (Exception error) {
            PluginDebugger.debug(error);
        }
    }

    public static void register() {
        slimeWorldPlugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    }

}