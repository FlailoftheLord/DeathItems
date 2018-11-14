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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings({ "unused", "unchecked" })
public class DeathInventory {

	private DeathItems plugin = DeathItems.getPlugin(DeathItems.class);

	private ConsoleCommandSender console = Bukkit.getConsoleSender();

	private Chat chat = new Chat();

	public ItemStack pInfo(Player player) {

		FileConfiguration config = plugin.getConfig();

		String infoItemType = config.getString("InfoItem").toUpperCase();

		ItemStack iItem = null;

		try {

			iItem = new ItemStack(Material.matchMaterial(infoItemType));

		} catch (IllegalArgumentException e) {
			console.sendMessage(chat.m("&5Death&dItems&8: "));
		}
		if (iItem != null) {

			String color = config.getString("NameColor");

			String pUuid = player.getUniqueId().toString();

			List<String> lore = new ArrayList<>();

			lore.add(chat.m("&8" + pUuid));
			lore.add(chat.m("&7click to collect all items"));

			ItemMeta iiMeta = iItem.getItemMeta();

			iiMeta.setLore(lore);

			iiMeta.setDisplayName(chat.m(color + player.getName()));

			iItem.setItemMeta(iiMeta);

		} else {
			console.sendMessage(chat.m("&cInvalid Item name for InfoItem in config.yml file: " + infoItemType));
			console.sendMessage(chat.m("&cReverting to default Item Type: NAME_TAG"));

			iItem = new ItemStack(Material.NAME_TAG);

			String color = config.getString("NameColor");

			String pUuid = player.getUniqueId().toString();

			List<String> lore = new ArrayList<>();

			lore.add(chat.m("&8" + pUuid));
			lore.add(chat.m("&7click to collect all items"));

			ItemMeta iiMeta = iItem.getItemMeta();

			iiMeta.setLore(lore);

			iiMeta.setDisplayName(chat.m(color + player.getName()));

			iItem.setItemMeta(iiMeta);
		}

		return iItem;
	}

	public ItemStack fillItem() {

		FileConfiguration config = plugin.getConfig();

		boolean fillBar = config.getBoolean("FillBottomBar");

		String fillItem = config.getString("FillItem").toUpperCase();

		ItemStack fI = new ItemStack(Material.getMaterial(fillItem));

		if (fI != null) {

			ItemMeta fiM = fI.getItemMeta();

			fiM.setDisplayName(" ");

			fI.setItemMeta(fiM);
		} else {
			fI.setType(Material.GRAY_STAINED_GLASS_PANE);
			ItemMeta fiM = fI.getItemMeta();

			fiM.setDisplayName(" ");

			fI.setItemMeta(fiM);
		}
		return fI;
	}

	public Inventory deathInv(Player player) {

		FileConfiguration drops = plugin.getDrops();

		FileConfiguration pData = plugin.getPlayerData();

		FileConfiguration config = plugin.getConfig();

		boolean invEnabled = config.getBoolean("EnableInventoryMode");

		String dInvTitle = config.getString("DeathInventoryTitle");

		Inventory deathInv = Bukkit.createInventory(player, 45, chat.m(dInvTitle));

		if (invEnabled) {

			String pUuid = player.getUniqueId().toString();

			List<? extends ItemStack> deathDrops = (List<? extends ItemStack>) drops.getList(pUuid + ".DeathDrops");

			if (deathDrops != null) {

				for (ItemStack dItem : deathDrops) {

					if (dItem != null) {
						deathInv.addItem(dItem);
					}

				}

			}

		}

		return deathInv;
	}

}