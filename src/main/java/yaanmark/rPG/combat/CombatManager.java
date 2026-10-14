
package yaanmark.rPG.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import yaanmark.rPG.manager.PlayerDataManager;
import yaanmark.rPG.player.PlayerAttributes;

public class CombatManager implements Listener {

    private final PlayerDataManager playerDataManager;

    public CombatManager(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {

        Entity attacker = event.getDamager();

        // Por enquanto, somente ataques diretos de jogadores.
        if (!(attacker instanceof Player)) {
            return;
        }

        Player player = (Player) attacker;

        PlayerAttributes attributes =
                playerDataManager.getAttributes(player);

        int strength = attributes.getStrength();

        double bonus = Math.max(0, (strength - 10) * 0.5);

        event.setDamage(event.getDamage() + bonus);
    }
}