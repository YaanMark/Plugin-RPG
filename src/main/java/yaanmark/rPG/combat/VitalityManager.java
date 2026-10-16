
package yaanmark.rPG.combat;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import yaanmark.rPG.player.PlayerAttributes;

public class VitalityManager {

    public double calculateMaxHealth(int vitality) {
        return Math.max(1.0, 20.0 + (vitality - 10) / 2.0);
    }

    public void updateMaxHealth(Player player, PlayerAttributes attributes) {
        AttributeInstance maxHealth =
                player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        double newMaxHealth =
                calculateMaxHealth(attributes.getVitality());

        maxHealth.setBaseValue(newMaxHealth);

        if (player.getHealth() > newMaxHealth) {
            player.setHealth(newMaxHealth);
        }
    }
}