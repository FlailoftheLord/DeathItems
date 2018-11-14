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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.DeathItems.DeathItems;

@SuppressWarnings("unused")
public class DeathEvent implements Listener {

	private DeathItems plugin = DeathItems.getPlugin(DeathItems.class);

	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {

		FileConfiguration config = plugin.getConfig();

		FileConfiguration drops = plugin.getDrops();

		FileConfiguration pData = plugin.getPlayerData();

		Player player = event.getEntity();

		String dBlockType = config.getString("DeathBlockType").toUpperCase();

		String pUuid = player.getUniqueId().toString();

		String pName = player.getName();

		Location dLoc = player.getLocation();

		Block dBlock = player.getLocation().getBlock();

		World deathWorld = player.getWorld();

		String dWorld = deathWorld.getName();

		int dX = dBlock.getX();
		int dY = dBlock.getY() + 1;
		int dZ = dBlock.getZ();

		pData.set(pUuid + ".Name", pName);
		pData.set(pUuid + ".DeathLocation.World", dWorld);
		pData.set(pUuid + ".DeathLocation.X", dX);
		pData.set(pUuid + ".DeathLocation.Y", dY);
		pData.set(pUuid + ".DeathLocation.Z", dZ);

		if (Material.matchMaterial(dBlockType) != null) {

			Block deathBlock = player.getWorld().getBlockAt(dX, dY, dZ);

			deathBlock.setType(Material.getMaterial(dBlockType), true);

		}

		List<ItemStack> deathDrops = new ArrayList<>();

		int deathExp = event.getDroppedExp();

		for (ItemStack d : event.getDrops()) {

			if (d != null) {

				int amount = d.getAmount();

				Material dm = d.getType();

				ItemStack pDrops = new ItemStack(dm, amount);

				ItemMeta dMeta = d.getItemMeta();

				pDrops.setItemMeta(dMeta);

				if (pDrops != null) {
					deathDrops.add(pDrops);
				}
			}

		}

		event.getDrops().clear();

		event.setDroppedExp(0);

		drops.set(pUuid + ".DeathExp", deathExp);
		drops.set(pUuid + ".DeathDrops", deathDrops);

		plugin.saveDrops();
		plugin.savePlayerData();

	}

}