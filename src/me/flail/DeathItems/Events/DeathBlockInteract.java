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

package me.flail.DeathItems.Events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.flail.DeathItems.Chat;
import me.flail.DeathItems.DeathInventory;
import me.flail.DeathItems.DeathItems;

public class DeathBlockInteract implements Listener {

	private DeathItems plugin = DeathItems.getPlugin(DeathItems.class);

	private Chat chat = new Chat();

	@SuppressWarnings("unchecked")
	@EventHandler
	public void playerClickDeathBlock(PlayerInteractEvent event) {

		FileConfiguration config = plugin.getConfig();

		FileConfiguration drops = plugin.getDrops();

		FileConfiguration pData = plugin.getPlayerData();

		Player player = event.getPlayer();

		Action eventAction = event.getAction();

		String pUuid = player.getUniqueId().toString();

		String pWorld = player.getWorld().getName();

		EquipmentSlot playerHand = event.getHand();

		if (eventAction.equals(Action.RIGHT_CLICK_BLOCK) && playerHand.equals(EquipmentSlot.HAND)) {

			String deathBlock = config.getString("DeathBlockType");

			String clickedBlock = event.getClickedBlock().getType().toString();

			if (clickedBlock.equalsIgnoreCase(deathBlock)) {

				if (pData.getKeys(false).contains(pUuid)) {

					ConfigurationSection cs = pData.getConfigurationSection(pUuid);

					String deathWorld = cs.getString("DeathLocation.World");

					if (deathWorld.equalsIgnoreCase(pWorld)) {

						World dw = plugin.getServer().getWorld(deathWorld.toUpperCase());

						int dbx = cs.getInt("DeathLocation.X");
						int dby = cs.getInt("DeathLocation.Y");
						int dbz = cs.getInt("DeathLocation.Z");

						Location deathLocation = new Location(dw, dbx, dby, dbz);

						Location blockLoc = event.getClickedBlock().getLocation();

						if (blockLoc.equals(deathLocation)) {

							boolean invEnabled = config.getBoolean("EnableInventoryMode");
							String invStringEnabled = config.getString("EnableInventoryMode");

							if (invEnabled || invStringEnabled.equalsIgnoreCase("true")) {
								Inventory deathInv = new DeathInventory().deathInv(player);
								player.openInventory(deathInv);

							} else {

								// It doesn't like me casting ItemStack to an "Unchecked" list... so it throws a
								// warning, this is blocked above...
								List<ItemStack> playerDeathItems = (List<ItemStack>) drops
										.getList(pUuid + ".DeathDrops");

								event.getClickedBlock().breakNaturally(new ItemStack(Material.AIR));

								if (playerDeathItems != null) {

									for (ItemStack ditems : playerDeathItems) {

										player.getWorld().dropItemNaturally(blockLoc, ditems);

									}

									String dItemsRetrieved = config.getString("ItemsRetrieved");

									player.sendMessage(chat.m(dItemsRetrieved));

								} else {

									String invEmpty = config.getString("InvEmpty");

									player.sendMessage(chat.m(invEmpty));

								}

								drops.set(pUuid + ".DeathDrops", null);

							}

							plugin.saveDrops();
							// cancel the event just so players dont go placing blocks in undesired places
							event.setCancelled(true);
						}

					}

				}

			}

		} else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			// Keep players from breaking their own block...
			/*
			 * I will add a way for other players to steal a death inventory, or maybe a
			 * method for players to "rob" a death inv. for now, if the player isn't the
			 * owner of the death inv block, they won't be able to break it either.
			 */

			String deathBlock = config.getString("DeathBlockType");

			String clickedBlock = event.getClickedBlock().getType().toString();

			if (clickedBlock.equalsIgnoreCase(deathBlock)) {

				if (!player.isOp() && !player.hasPermission("deathitems.*")
						&& !player.hasPermission("deathitems.commmand.*")) {

					for (String p : pData.getKeys(false)) {
						String world = pData.getString(p + ".DeathLocation.World");
						if (world != null) {
							World w = plugin.getServer().getWorld(world);
							int x = pData.getInt(p + ".DeathLocation.X");
							int y = pData.getInt(p + ".DeathLocation.Y");
							int z = pData.getInt(p + ".DeathLocation.Z");

							Location dBlockLoc = new Location(w, x, y, z);

							Location blockLoc = event.getClickedBlock().getLocation();

							if (blockLoc.equals(dBlockLoc)) {
								event.setCancelled(true);
							}

							break;
						}
					}
				}

				if (pData.getKeys(false).contains(pUuid)) {

					ConfigurationSection cs = pData.getConfigurationSection(pUuid);

					String deathWorld = cs.getString("DeathLocation.World");

					if (deathWorld.equalsIgnoreCase(pWorld)) {

						World dw = plugin.getServer().getWorld(deathWorld.toUpperCase());

						int dbx = cs.getInt("DeathLocation.X");
						int dby = cs.getInt("DeathLocation.Y");
						int dbz = cs.getInt("DeathLocation.Z");

						Location deathLocation = new Location(dw, dbx, dby, dbz);

						Location blockLoc = event.getClickedBlock().getLocation();

						if (blockLoc.equals(deathLocation)) {

							String cantBreak = config.getString("CannotBreak");

							player.sendMessage(chat.m(cantBreak));

							event.setCancelled(true);

						}

					}

				}

			}

		}

	}

}
