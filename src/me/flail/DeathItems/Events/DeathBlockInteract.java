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

							if (invEnabled) {
								Inventory deathInv = new DeathInventory().deathInv(player);
								player.openInventory(deathInv);

							} else {

								int deathExp = drops.getInt(pUuid + ".DeathExp");

								player.giveExp(deathExp);

								List<ItemStack> playerDeathItems = (List<ItemStack>) drops
										.getList(pUuid + ".DeathDrops");

								for (ItemStack ditems : playerDeathItems) {

									event.getClickedBlock().breakNaturally(new ItemStack(Material.AIR));

									player.getWorld().dropItemNaturally(blockLoc, ditems);

								}

								drops.set(pUuid + ".DeathDrops", null);

								String dItemsRetrieved = config.getString("ItemsRetrieved");

								String prefix = config.getString("Prefix");

								player.sendMessage(chat.m(prefix + " " + dItemsRetrieved));

							}

							plugin.saveDrops();
						}

					}

				}

			}

		}

	}

}
