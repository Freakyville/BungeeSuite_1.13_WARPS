package com.minecraftdimensions.bungeesuitewarps.commands;

import com.minecraftdimensions.bungeesuitewarps.managers.WarpsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class ListWarpsCommand implements CommandExecutor {


	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		WarpsManager.listWarps(sender);
		return true;
	}

}
