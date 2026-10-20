
package yaanmark.rPG.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import yaanmark.rPG.manager.PlayerDataManager;
import yaanmark.rPG.player.PlayerAttributes;
import java.util.concurrent.ThreadLocalRandom;

public class CombatManager implements Listener {

    private final PlayerDataManager playerDataManager;

    public CombatManager(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        Entity attacker = event.getDamager();
        Entity target = event.getEntity();

        if (attacker instanceof Player) {
            Player player = (Player) attacker;

            PlayerAttributes attackerAttributes =
                    playerDataManager.getAttributes(player);

            int strength = attackerAttributes.getStrength();

            double bonus = Math.max(0, (strength - 10) * 0.5);

            event.setDamage(event.getDamage() + bonus);
        }

        if (!(target instanceof Player)) {
            return;
        }

        Player defender = (Player) target;

        PlayerAttributes defenderAttributes =
                playerDataManager.getAttributes(defender);

        int dexterity = defenderAttributes.getDexterity();

        double dodgeChance = Math.min(
                100.0,
                Math.max(0.0, dexterity * 0.5)
        );

        double roll = ThreadLocalRandom.current().nextDouble(100.0);

        if (roll < dodgeChance) {
            event.setCancelled(true);

            defender.sendMessage(
                    "§a§lESQUIVA! §7Você evitou o ataque."
            );

            if (attacker instanceof Player) {
                ((Player) attacker).sendMessage(
                        "§c§lATAQUE ESQUIVADO! §7Seu adversário evitou o golpe."
                );
            }
        }
    }
}
