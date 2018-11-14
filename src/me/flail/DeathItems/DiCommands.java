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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DiCommands implements CommandExecutor {

	private DeathItems plugin = DeathItems.getPlugin(DeathItems.class);

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Chat chat = new Chat();

		FileConfiguration config = plugin.getConfig();

		FileConfiguration pData = plugin.getPlayerData();

		String cmd = command.getName().toLowerCase();

		String version = plugin.getDescription().getVersion();

		String noPermission = config.getString("NoPermissionMessage");

		String defaultMessage = chat.m("&5Death&dItems &8(&eversion &7" + version + "&8) &eby FlailoftheLord.");

		String reloadMessage = config.getString("ReloadMessage");

		if (cmd.equals("deathitems")) {

			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (player.hasPermission("deathitems.command")) {

					if (args.length == 0) {

						player.sendMessage(defaultMessage);

					} else if (args.length == 1) {

						if (args[0].equalsIgnoreCase("reload")) {

							if (player.hasPermission("deathitems.command.reload")
									|| player.hasPermission("deathitems.command.all")) {

								plugin.saveDrops();
								plugin.savePlayerData();
								plugin.reloadConfig();

								player.sendMessage(chat.m("%prefix% " + reloadMessage));

							} else {
								player.sendMessage(chat.m("%prefix% " + noPermission));
							}
						}

						else if (args[0].equalsIgnoreCase("help")) {

							player.sendMessage(chat.m("%prefix% &cYou can open your DeathInventory with &7/deathinv"));
							player.sendMessage(chat.m("&ateleport to your last death location by typing &7/deathloc"));
							player.sendMessage(chat.m("&cAnd you can reload the plugin by doing &7/deathitems reload"));
							player.sendMessage(chat.m("&aThats about all the commands for this plugin!"));

						}

					}

				} else {
					player.sendMessage(chat.m("%prefix% " + noPermission));
				}

			}

		} else if (cmd.equals("deathlocation")) {

			if (sender instanceof Player) {

				Player player = (Player) sender;

				String pUuid = player.getUniqueId().toString();

				int dX = pData.getInt(pUuid + ".DeathLocation.X");
				int dY = pData.getInt(pUuid + ".DeathLocation.Y") + 2;
				int dZ = pData.getInt(pUuid + ".DeathLocation.Z");

				String deathWorld = pData.getString(pUuid + ".DeathLocation.World").toLowerCase();

				World dWorld = plugin.getServer().getWorld(deathWorld);

				if (dWorld != null) {
					Location dLoc = new Location(dWorld, dX, dY, dZ);

					player.teleport(dLoc);

					String deathLocTp = config.getString("DeathLocationTeleport");

					player.sendMessage(chat.m("%prefix% " + deathLocTp));

				} else {

					player.sendMessage(chat.m("&4Error&8: &cInvalid World name specified in file PlayerData.yml"));

				}

			}

		} else if (cmd.equals("deathinv")) {

			String invDisabledMessage = config.getString("InventoryDisabled");

			String prefix = config.getString("Prefix");

			boolean dInvEnabled = config.getBoolean("EnableInventoryMode");

			if (sender instanceof Player) {

				Player player = (Player) sender;

				if (player.hasPermission("deathitems.command.deathinv")) {

				}

				if (dInvEnabled) {

					Inventory deathInv = new DeathInventory().deathInv(player);

					player.openInventory(deathInv);

				} else if (dInvEnabled == false) {

					player.sendMessage(chat.m(prefix + " " + invDisabledMessage));
				}

			}
		}

		return true;
	}

}