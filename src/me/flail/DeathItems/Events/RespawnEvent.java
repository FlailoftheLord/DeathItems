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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.flail.DeathItems.Chat;
import me.flail.DeathItems.DeathItems;

public class RespawnEvent implements Listener {

	private DeathItems plugin = DeathItems.getPlugin(DeathItems.class);

	private Chat chat = new Chat();

	@EventHandler
	public void playerRespawn(PlayerRespawnEvent event) {

		FileConfiguration config = plugin.getConfig();

		FileConfiguration pData = plugin.getPlayerData();

		Player player = event.getPlayer();

		String pUuid = player.getUniqueId().toString();

		String respawnMsg = config.getString("DeathMessage");

		ConfigurationSection cs = pData.getConfigurationSection(pUuid + ".DeathLocation");

		if (cs != null) {

			int x = pData.getInt(pUuid + ".DeathLocation.X");
			int y = pData.getInt(pUuid + ".DeathLocation.Y");
			int z = pData.getInt(pUuid + ".DeathLocation.Z");

			String loc = x + " " + y + " " + z;

			String world = pData.getString(pUuid + ".DeathLocation.World");

			player.sendMessage(chat.m(respawnMsg).replaceAll("%death-location%", loc).replace("%world%", world));

		}

	}

}
