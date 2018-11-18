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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DiCommands implements CommandExecutor {

	private DeathItems plugin = DeathItems.getPlugin(DeathItems.class);

	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// loads a new instance of the Chat() class. to be used in translating stuff.
		Chat chat = new Chat();

		FileConfiguration config = plugin.getConfig();

		FileConfiguration pData = plugin.getPlayerData();

		// Sticks the command to lowercase, so players can type /cOMManD and still have
		// the command work :>
		String cmd = command.getName().toLowerCase();

		// gets the plugin version from plugin.yml
		String version = plugin.getDescription().getVersion();

		String noPermission = config.getString("NoPermissionMessage");

		// this is the message sent if the player doesnt type anything after the command
		// (aka. no args)
		String defaultMessage = chat.m("&5Death&dItems &8(&eversion &7" + version + "&8) &eby FlailoftheLord.");

		String reloadMessage = config.getString("ReloadMessage");

		boolean invEnabled = config.getBoolean("EnableInventoryMode");

		// Check the command name.
		if (cmd.equals("deathitems")) {

			// Make sure the command sender is actually a player and not some evil admin
			// snooping in the console
			if (sender instanceof Player) {
				Player player = (Player) sender;

				// Base Permission Check for this command.
				if (player.hasPermission("deathitems.command")) {

					if (args.length == 0) {

						// sends the default message described above if the player doesn't stick stuff
						// in their commands
						player.sendMessage(defaultMessage);

					} else if (args.length == 1) {

						// execution of the reload command...
						if (args[0].equalsIgnoreCase("reload")) {

							// Checks if the player has permission
							if (player.hasPermission("deathitems.command.reload")
									|| player.hasPermission("deathitems.command.all")) {

								plugin.loadDrops();
								plugin.savePlayerData();
								plugin.reloadConfig();

								player.sendMessage(chat.m(reloadMessage));

							} else {
								// if they don't, it spanks them and spams their chat.
								player.sendMessage(chat.m(noPermission));
							}
						}

						else if (args[0].equalsIgnoreCase("help")) {

							if (player.hasPermission("deathitems.command.help")) {

								// a very intuitive setup, if they don't have permission for a specific command,
								// it won't show the help section for it :>

								if ((invEnabled && player.hasPermission("deathitems.command.deathinv"))
										|| (invEnabled && player.hasPermission("deathitems.command.all"))) {
									player.sendMessage(
											chat.m("%prefix% &cYou can open your DeathInventory with &7/deathinv"));
								}

								if (player.hasPermission("deathitems.command.deathloc")
										|| player.hasPermission("deathitems.command.all")) {
									player.sendMessage(chat.m(
											"%prefix% &ateleport to your last death location by typing &7/deathloc"));
								}

								if (player.hasPermission("deathitems.command.reload")
										|| player.hasPermission("deathitems.command.all")) {
									player.sendMessage(chat.m(
											"%prefix% &cYou can reload the plugin, if you have made changes to the config file, by typing &7/deathitems reload"));
								}

							} else {
								// Naughty boi message... "you're not allowed dude!"
								player.sendMessage(chat.m(noPermission));
							}

						} else {
							// tell the player how to use the command if they screw up.
							player.sendMessage(chat.m("%prefix% &cProper usage: &7/deathitems [help:reload]"));
						}

					} else {
						// No-Permission messages -- -- --
						player.sendMessage(chat.m(defaultMessage));
					}

				} else {
					player.sendMessage(chat.m(noPermission));
				}

			} else {
				// console spam if the player tries to type this command in the console.
				console.sendMessage(chat.m("&cCommand can only be used in game"));
			}

		} else if (cmd.equals("deathlocation")) {

			if (sender instanceof Player) {

				Player player = (Player) sender;

				// Permissions check.
				if (player.hasPermission("deathitems.command")) {

					// Child Nodes --
					if (player.hasPermission("deathitems.command.all")
							|| player.hasPermission("deathitems.command.deathloc")) {

						// here we get all the location information from the data file.
						String pUuid = player.getUniqueId().toString();

						int dX = pData.getInt(pUuid + ".DeathLocation.X");
						int dY = pData.getInt(pUuid + ".DeathLocation.Y") + 2;
						int dZ = pData.getInt(pUuid + ".DeathLocation.Z");

						String deathWorld = pData.getString(pUuid + ".DeathLocation.World").toLowerCase();

						World dWorld = plugin.getServer().getWorld(deathWorld);

						// check if the world exists
						if (dWorld != null) {
							// if it does we teleport the player,
							Location dLoc = new Location(dWorld, dX, dY, dZ);

							player.teleport(dLoc);

							String deathLocTp = config.getString("DeathLocationTeleport");

							player.sendMessage(chat.m(deathLocTp));

						} else {
							// otherwise, send them a fancy error message :>
							player.sendMessage(
									chat.m("&4Error&8: &cInvalid World name specified in file PlayerData.yml"));
						}

					} else {
						player.sendMessage(chat.m(noPermission));
					}

				} else {
					player.sendMessage(chat.m(noPermission));
				}

			} else {
				// console spam if the player tries to type this command in the console.
				console.sendMessage(chat.m("&cCommand can only be used in game"));
			}

		} else if (cmd.equals("deathinv")) {

			String invDisabledMessage = config.getString("InventoryDisabled");

			boolean dInvEnabled = config.getBoolean("EnableInventoryMode");

			if (sender instanceof Player) {

				Player player = (Player) sender;

				// Check if they have the permission to use the command...
				if (player.hasPermission("deathitems.command")) {

					// Next check if they have the proper child nodes
					if (player.hasPermission("deathitems.command.deathinv")
							|| player.hasPermission("deathitems.command.all")) {

						// Is the InventoryMode enabled in the config.yml ??
						if (dInvEnabled) {

							Inventory deathInv = new DeathInventory().deathInv(player);

							String pUuid = player.getUniqueId().toString();

							FileConfiguration drops = plugin.getDrops();

							ConfigurationSection pDrops = drops.getConfigurationSection(pUuid + ".DeathDrops");

							// Does the player actually have stuff inside their death inventory?
							if (pDrops != null) {
								// if they do then open their DeathInv
								player.openInventory(deathInv);

							} else {

								// otherwise send them an error message
								String invEmpty = config.getString("InvEmpty");
								player.sendMessage(chat.m(invEmpty));

							}

						} else if (dInvEnabled == false) {
							// message sent if the InventoryMode is disabled in the config.yml file
							player.sendMessage(chat.m(invDisabledMessage));
						} else {
							player.sendMessage(chat.m(invDisabledMessage));
						}

					} else {
						// sending no-permission messages
						player.sendMessage(chat.m(noPermission));
					}

				} else {
					player.sendMessage(chat.m(noPermission));
				}

			} else {
				// console spam if the player tries to type this command in the console.
				console.sendMessage(chat.m("&cCommand can only be used in game"));
			}

		}

		return true;
	}

}