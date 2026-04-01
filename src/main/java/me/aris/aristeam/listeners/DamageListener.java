package me.aris.aristeam.listeners;

import me.aris.aristeam.ArisTeams;
import me.aris.aristeam.manager.TeamData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player victim = (Player) e.getEntity();
            TeamData team = ArisTeams.getInstance().getTeamManager().getTeam(damager);
            if (team != null && team.members.contains(victim.getUniqueId()) && !team.pvp) {
                e.setCancelled(true);
            }
        }
    }
              }
