package com.minecraftdimensions.bungeesuitewarps.managers;

import com.minecraftdimensions.bungeesuiteteleports.BungeeSuiteTeleports;
import com.minecraftdimensions.bungeesuiteteleports.managers.TeleportsManager;
import com.minecraftdimensions.bungeesuitewarps.BungeeSuiteWarps;
import com.minecraftdimensions.bungeesuitewarps.redis.RedisManager;
import com.minecraftdimensions.bungeesuitewarps.tasks.PluginMessageTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class WarpsManager {

    public static HashMap<String, Location> pendingWarps = new HashMap<>();

    public static void warpPlayer(CommandSender sender, String player, String warp) {
        StringBuilder sb = new StringBuilder();
        sb.append("WarpPlayer").append(";");
        if (sender instanceof Player) {
            sb.append(sender.getName()).append(";");
        } else {
            sb.append("CONSOLE").append(";");
        }
        sb.append(player).append(";");
        sb.append(warp).append(";");
        sb.append(sender.hasPermission("bungeesuite.warps.warp." + warp.toLowerCase()) || sender.hasPermission("bungeesuite.warps.warp.*")).append(";");
        sb.append(sender.hasPermission("bungeesuite.warps.bypass"));
        RedisManager.getInstance().publish(sb.toString(), "WARP_REQUEST");
//        ByteArrayOutputStream b = new ByteArrayOutputStream();
//        DataOutputStream out = new DataOutputStream(b);
//        try {
//            out.writeUTF("WarpPlayer");
//            if (sender instanceof Player) {
//                out.writeUTF(sender.getName());
//            } else {
//                out.writeUTF("CONSOLE");
//            }
//            out.writeUTF(player);
//            out.writeUTF(warp);
//            out.writeBoolean(sender.hasPermission("bungeesuite.warps.warp." + warp.toLowerCase()) || sender.hasPermission("bungeesuite.warps.warp.*"));
//            out.writeBoolean(sender.hasPermission("bungeesuite.warps.bypass"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        new PluginMessageTask(b).runTaskAsynchronously(BungeeSuiteWarps.instance);
    }

    public static void setWarp(CommandSender sender, String name, boolean hidden, boolean global) {
        Location l = ((Player) sender).getLocation();
        StringBuilder sb = new StringBuilder();
        sb.append("SetWarp").append(";");
        sb.append(sender.getName()).append(";");
        sb.append(name).append(";");
        sb.append(l.getWorld().getName()).append(";");
        sb.append(l.getX()).append(";");
        sb.append(l.getY()).append(";");
        sb.append(l.getZ()).append(";");
        sb.append(l.getYaw()).append(";");
        sb.append(l.getPitch()).append(";");
        sb.append(hidden).append(";");
        sb.append(global);
        RedisManager.getInstance().publish(sb.toString(), "WARP_REQUEST");
    }

    public static void deleteWarp(CommandSender sender, String warp) {
        RedisManager.getInstance().publish("DeleteWarp;" + sender.getName() + ";" + warp, "WARP_REQUEST");
    }


    public static void listWarps(CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        sb.append("GetWarpsList;");
        sb.append(sender.getName()).append(";");
        sb.append(sender.hasPermission("bungeesuite.warps.list.server")).append(";");
        sb.append(sender.hasPermission("bungeesuite.warps.list.global")).append(";");
        sb.append(sender.hasPermission("bungeesuite.warps.list.hidden")).append(";");
        sb.append(sender.hasPermission("bungeesuite.warps.bypass"));
        RedisManager.getInstance().publish(sb.toString(), "WARP_REQUEST");
    }

    public static void teleportPlayerToWarp(final String player, Location location) {
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.teleport(location);
        } else {
            pendingWarps.put(player, location);
            if (BungeeSuiteWarps.usingTeleports) {
                TeleportsManager.ignoreTeleport.add(p);
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(BungeeSuiteWarps.instance, new Runnable() {
                @Override
                public void run() {
                    if (pendingWarps.containsKey(player)) {
                        pendingWarps.remove(player);
                    }
                }
            }, 100);
        }
    }


    public static void sendVersion() {
        RedisManager.getInstance().publish("SendVersion;" + ChatColor.RED + "Warps - " + ChatColor.GOLD + BungeeSuiteTeleports.instance.getDescription().getVersion(), "WARP_REQUEST");
    }
}