package me.flail.DeathItems.Events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.flail.DeathItems.DeathItems;

public class PreDeathEvent implements Listener {

	private DeathItems plugin = DeathItems.getPlugin(DeathItems.class);

	@EventHandler
	public void playerTakesFinalDamage(EntityDamageEvent event) {

		double damage = event.getFinalDamage();

		if (event.getEntity() instanceof Player) {

			FileConfiguration drops = plugin.getDrops();

			Player player = (Player) event.getEntity();

			double health = player.getHealth();

			if (damage >= health) {

				String pUuid = player.getUniqueId().toString();

				float playerExp = player.getExp();

				int deathExp = Math.round(playerExp);

				drops.set(pUuid + ".DeathExp", deathExp);

			}

		}

	}

}
