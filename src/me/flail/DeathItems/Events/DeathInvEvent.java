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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.flail.DeathItems.Chat;
import me.flail.DeathItems.DeathInventory;
import me.flail.DeathItems.DeathItems;

@SuppressWarnings({ /* "unused", */ "unchecked" })
public class DeathInvEvent implements Listener {

	private DeathItems plugin = DeathItems.getPlugin(DeathItems.class);

	private Chat chat = new Chat();

	@EventHandler
	public void dInvClick(InventoryClickEvent click) {

		FileConfiguration config = plugin.getConfig();

		FileConfiguration drops = plugin.getDrops();

		Inventory eventInv = click.getView().getTopInventory();

		Player player = (Player) click.getWhoClicked();

		InventoryHolder owner = eventInv.getHolder();

		if (owner instanceof Player) {

			Player holder = (Player) eventInv.getHolder();
			Inventory deathInv = new DeathInventory().deathInv(player);

			InventoryAction dragEvent = click.getAction();

			int slot = click.getRawSlot();
			int eventInvSlot = deathInv.getSize() - 1;
			ItemStack cItem = click.getCurrentItem();

			if (holder.equals(player)) {

				String pUuid = player.getUniqueId().toString();

				String dInvTitle = ChatColor.translateAlternateColorCodes('&', config.getString("DeathInventoryTitle"));

				String invTitle = eventInv.getTitle();

				if (invTitle.equalsIgnoreCase(dInvTitle)) {

					SlotType outsideClick = click.getSlotType();

					if (outsideClick.equals(SlotType.OUTSIDE)) {
						player.closeInventory();
					}

					if (dragEvent.equals(InventoryAction.PLACE_ALL) || dragEvent.equals(InventoryAction.PLACE_SOME)
							|| dragEvent.equals(InventoryAction.PLACE_ONE)) {

						if (slot <= eventInvSlot) {
							click.setCancelled(true);
						}

					} else if (dragEvent.equals(InventoryAction.PICKUP_ALL)
							|| dragEvent.equals(InventoryAction.PICKUP_SOME)
							|| dragEvent.equals(InventoryAction.PICKUP_HALF)
							|| dragEvent.equals(InventoryAction.PICKUP_ONE)) {
						if (slot >= eventInvSlot) {
							click.setCancelled(true);
						}

					}

					else if (dragEvent.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {

						if (slot > eventInvSlot) {

							click.setCancelled(true);

						} else {

							List<ItemStack> deathItems = (List<ItemStack>) drops.getList(pUuid + ".DeathDrops");

							for (ItemStack i : deathItems) {

								if (cItem.equals(i)) {

									List<ItemStack> updatedDeathItems = new ArrayList<>();

									ItemStack[] newItems = eventInv.getContents();

									if (newItems != null) {

										for (ItemStack newItem : newItems) {

											if ((newItem != null) && (newItem.getType() != Material.AIR)) {
												updatedDeathItems.add(newItem);
											}
										}

										drops.set(pUuid, null);
										drops.set(pUuid + ".DeathDrops", updatedDeathItems);

									} else {

										drops.set(pUuid, null);

									}

								}

							}

						}

					} else {

						if (slot <= eventInvSlot) {

							click.setCancelled(true);

						} else {

							List<ItemStack> deathItems = (List<ItemStack>) drops.getList(pUuid + ".DeathDrops");

							if (deathItems != null) {

								for (ItemStack i : deathItems) {

									if (cItem.equals(i)) {

										List<ItemStack> updatedDeathItems = new ArrayList<>();

										ItemStack[] newItems = eventInv.getContents();

										if (newItems != null) {

											for (ItemStack newItem : newItems) {

												if ((newItem != null) && (newItem.getType() != Material.AIR)) {
													updatedDeathItems.add(newItem);
													drops.set(pUuid, null);
													drops.set(pUuid + ".DeathDrops", updatedDeathItems);
												}
											}

										} else {

											drops.set(pUuid, null);

										}

									}

								}

							}

						}

					}

					plugin.saveDrops();
				}

			} else {
				player.sendMessage("Em");
			}

		}

	}

	@EventHandler
	public void dInvDrag(InventoryDragEvent drag) {

		FileConfiguration config = plugin.getConfig();

		Inventory eventInv = drag.getView().getTopInventory();

		Player player = (Player) drag.getWhoClicked();

		InventoryHolder owner = eventInv.getHolder();

		if (owner instanceof Player) {

			Player holder = (Player) eventInv.getHolder();

			if (holder.equals(player)) {

				String dInvTitle = ChatColor.translateAlternateColorCodes('&', config.getString("DeathInventoryTitle"));

				String invTitle = eventInv.getTitle();

				if (invTitle.equalsIgnoreCase(dInvTitle)) {

					drag.setCancelled(true);

				}

			}

		}

	}

	@EventHandler
	public void dInvClose(InventoryCloseEvent event) {

		FileConfiguration config = plugin.getConfig();

		boolean invEnabled = config.getBoolean("EnableInventoryMode");

		Player player = (Player) event.getPlayer();

		if (invEnabled) {

			Inventory eventInv = event.getView().getTopInventory();

			if (eventInv.getHolder() instanceof Player) {

				Player holder = (Player) eventInv.getHolder();

				String dInvTitle = chat.m(config.getString("DeathInventoryTitle"));

				String invTitle = eventInv.getTitle();

				if (holder.equals(player)) {

					if (invTitle.equals(dInvTitle)) {

						FileConfiguration drops = plugin.getDrops();

						boolean hasStuff = false;

						ItemStack air = new ItemStack(Material.AIR);

						if (eventInv.getContents() != null) {

							for (ItemStack i : eventInv.getContents()) {

								if ((i != null) && (i != air)) {
									hasStuff = true;
									break;
								}

							}

						} else {
							hasStuff = false;
						}
						if (hasStuff) {

							String invNotEmpty = config.getString("InvNotEmpty");

							player.sendMessage(chat.m(invNotEmpty));

						} else {

							FileConfiguration pData = plugin.getPlayerData();

							String pUuid = player.getUniqueId().toString();

							ConfigurationSection cs = pData.getConfigurationSection(pUuid);

							int x = cs.getInt("DeathLocation.X");
							int y = cs.getInt("DeathLocation.Y");
							int z = cs.getInt("DeathLocation.Z");
							String world = cs.getString("DeathLocation.World");
							World dWorld = plugin.getServer().getWorld(world);

							Location dLoc = new Location(dWorld, x, y, z);

							Block targetBlock = dWorld.getBlockAt(dLoc);

							String deathBlockType = config.getString("DeathBlockType").toUpperCase().replace(" ", "_");

							Material dBlockMaterial = Material.matchMaterial(deathBlockType);

							if (targetBlock.getType().equals(dBlockMaterial)) {
								targetBlock.setType(Material.AIR);
							}

							drops.set(pUuid + ".DeathDrops", null);

							String dItemsRetrieved = config.getString("ItemsRetrieved");

							player.sendMessage(chat.m(dItemsRetrieved));
						}

					}

				}

			}

		}

	}

}