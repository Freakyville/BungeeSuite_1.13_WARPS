package com.minecraftdimensions.bungeesuitewarps;

import com.minecraftdimensions.bungeesuiteteleports.BungeeSuiteTeleports;
import com.minecraftdimensions.bungeesuitewarps.commands.DeleteWarpCommand;
import com.minecraftdimensions.bungeesuitewarps.commands.ListWarpsCommand;
import com.minecraftdimensions.bungeesuitewarps.commands.SetWarpCommand;
import com.minecraftdimensions.bungeesuitewarps.commands.WarpCommand;
import com.minecraftdimensions.bungeesuitewarps.listeners.WarpsListener;
import com.minecraftdimensions.bungeesuitewarps.listeners.WarpsMessageListener;
import com.minecraftdimensions.bungeesuitewarps.managers.CooldownManager;
import com.minecraftdimensions.bungeesuitewarps.redis.RedisManager;
import io.github.freakyville.utils.config.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BungeeSuiteWarps extends JavaPlugin {


    public static String OUTGOING_PLUGIN_CHANNEL = "bsuite:warps-in";
    static String INCOMING_PLUGIN_CHANNEL = "bsuite:warps-out";
    public static BungeeSuiteWarps instance;
    public static boolean usingTeleports = false;
    public static String server;

    @Override
    public void onEnable() {
        instance = this;
        registerListeners();
        registerChannels();
        registerCommands();
        BungeeSuiteTeleports bt = (BungeeSuiteTeleports) Bukkit.getPluginManager().getPlugin("Teleports");
        if (bt != null) {
            if (bt.getDescription().getAuthors().contains("Bloodsplat")) {
                usingTeleports = true;
            }
        }

        ConfigHandler configHandler = new ConfigHandler(instance, "config.yml");

        server = configHandler.getString("server");
        Map<String, Map<String, Integer>> cooldowns = new HashMap<>();
        for (String command : configHandler.getConfigSection("cooldowns").getKeys(false)) {
            cooldowns.putIfAbsent(command, new HashMap<>());
            for (String perm : configHandler.getConfigSection("cooldowns." + command).getKeys(false)) {
                cooldowns.get(command).put(perm, configHandler.getInt("cooldowns." + command + "." + perm));
            }
        }
        CooldownManager.getInstance().setCooldowns(cooldowns);

        RedisManager.getInstance().init(configHandler.getString("host"), configHandler.getString("password"), configHandler.getInt("port"), configHandler.getInt("timeout"));
    }

    private void registerCommands() {
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("warps").setExecutor(new ListWarpsCommand());
        getCommand("setwarp").setExecutor(new SetWarpCommand());
        getCommand("delwarp").setExecutor(new DeleteWarpCommand());
    }

    private void registerChannels() {
//        Bukkit.getMessenger().registerIncomingPluginChannel(this,
//                INCOMING_PLUGIN_CHANNEL, new WarpsMessageListener());
//        Bukkit.getMessenger().registerOutgoingPluginChannel(this,
//                OUTGOING_PLUGIN_CHANNEL);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
                new WarpsListener(), this);
    }

}
