/*
 *  Copyright (C) 2018 FlailoftheLord
 *
 *  This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package me.flail.DeathItems;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.flail.DeathItems.Events.DeathBlockInteract;
import me.flail.DeathItems.Events.DeathEvent;
import me.flail.DeathItems.Events.DeathInvEvent;
import me.flail.DeathItems.Events.RespawnEvent;

public class DeathItems extends JavaPlugin {

	private File dropsFile;
	private FileConfiguration dropsConfig;

	private File playerData;
	private FileConfiguration playerDataConfig;

	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	private PluginManager plugin = getServer().getPluginManager();

	private String version = getDescription().getVersion();

	@Override
	public void onEnable() {

		// Load up files
		saveDefaultConfig();
		loadDrops();
		loadPlayerData();

		// Register Commands and Events
		registerCommands();
		registerEvents();

		// Friendly Console Spam
		console.sendMessage(ChatColor.DARK_PURPLE + "  ___   " + ChatColor.LIGHT_PURPLE + " _____");
		console.sendMessage(ChatColor.DARK_PURPLE + "  |  \\  " + ChatColor.LIGHT_PURPLE + "   |  ");
		console.sendMessage(ChatColor.DARK_PURPLE + "  |   | " + ChatColor.LIGHT_PURPLE + "   |  ");
		console.sendMessage(ChatColor.DARK_PURPLE + "  |__/  " + ChatColor.LIGHT_PURPLE + " __|__");
		console.sendMessage(ChatColor.LIGHT_PURPLE + "    DeathItems " + ChatColor.DARK_PURPLE + "v" + version);
		console.sendMessage(ChatColor.DARK_GREEN + "      by FlailoftheLord.");
		console.sendMessage(" ");

	}

	@Override
	public void onDisable() {

	}

	public void registerCommands() {

		getCommand("deathinv").setExecutor(new DiCommands());
		getCommand("deathitems").setExecutor(new DiCommands());
		getCommand("deathlocation").setExecutor(new DiCommands());

	}

	public void registerEvents() {

		boolean invEnabled = getConfig().getBoolean("EnableInventoryMode");

		if (invEnabled) {
			plugin.registerEvents(new DeathInvEvent(), this);
		}

		plugin.registerEvents(new DeathEvent(), this);
		plugin.registerEvents(new RespawnEvent(), this);
		plugin.registerEvents(new DeathBlockInteract(), this);

	}

	public FileConfiguration getDrops() {
		if (dropsConfig == null) {
			loadDrops();
		}
		return dropsConfig;
	}

	public void loadDrops() {
		dropsFile = new File(getDataFolder(), "DeathDrops.yml");
		if (!dropsFile.exists()) {
			dropsFile.getParentFile().mkdirs();
			saveResource("DeathDrops.yml", false);
		}

		dropsConfig = new YamlConfiguration();
		try {
			dropsConfig.load(dropsFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, "Could not load " + dropsFile, e);
		}
	}

	public void saveDrops() {
		if ((dropsConfig == null) || (dropsFile == null)) {
			return;
		}

		try {
			getDrops().save(dropsFile);
		} catch (IOException ex) {
			ex.printStackTrace();
			getLogger().log(Level.SEVERE, "Could not save " + dropsFile, ex);
		}

	}

	public FileConfiguration getPlayerData() {
		if (playerDataConfig == null) {
			loadPlayerData();
		}
		return playerDataConfig;
	}

	public void loadPlayerData() {
		playerData = new File(getDataFolder(), "PlayerData.yml");
		if (!playerData.exists()) {
			playerData.getParentFile().mkdirs();
			saveResource("PlayerData.yml", false);
		}

		playerDataConfig = new YamlConfiguration();
		try {
			playerDataConfig.load(playerData);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, "Couldn't load the file: " + playerData, e);
		}

	}

	public void savePlayerData() {

		if ((playerDataConfig == null) || (playerData == null)) {
			return;
		}

		try {
			getPlayerData().save(playerData);
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, "Couldn't save the File: " + playerData, e);
		}

	}

}
