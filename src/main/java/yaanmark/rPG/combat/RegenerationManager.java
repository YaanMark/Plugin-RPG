
package yaanmark.rPG.combat;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import yaanmark.rPG.manager.PlayerDataManager;
import yaanmark.rPG.player.PlayerAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegenerationManager {

    private final JavaPlugin plugin;
    private final PlayerDataManager playerDataManager;

    private final Map<UUID, Long> nextRegenerationTick =
            new HashMap<>();

    private BukkitTask regenerationTask;
    private long elapsedTicks = 0;

    public RegenerationManager(
            JavaPlugin plugin,
            PlayerDataManager playerDataManager
    ) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    public void start() {
        if (regenerationTask != null) {
            return;
        }

        regenerationTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::regeneratePlayers,
                20L,
                20L
        );
    }

    private void regeneratePlayers() {
        elapsedTicks += 20;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isDead()) {
                continue;
            }

            UUID uuid = player.getUniqueId();

            PlayerAttributes attributes =
                    playerDataManager.getAttributes(player);

            int vitality = attributes.getVitality();

            long interval = Math.max(
                    40L,
                    200L - (vitality - 10L) * 4L
            );

            // Agenda a primeira regeneração individual.
            if (!nextRegenerationTick.containsKey(uuid)) {
                nextRegenerationTick.put(
                        uuid,
                        elapsedTicks + interval
                );
                continue;
            }

            // Ainda não chegou a hora de regenerar.
            if (elapsedTicks < nextRegenerationTick.get(uuid)) {
                continue;
            }

            AttributeInstance maxHealth =
                    player.getAttribute(Attribute.MAX_HEALTH);

            if (maxHealth == null) {
                continue;
            }

            double currentHealth = player.getHealth();
            double maximumHealth = maxHealth.getValue();

            if (currentHealth < maximumHealth) {
                player.setHealth(
                        Math.min(currentHealth + 1.0, maximumHealth)
                );
            }

            nextRegenerationTick.put(
                    uuid,
                    elapsedTicks + interval
            );
        }
    }

    public void stop() {
        if (regenerationTask != null) {
            regenerationTask.cancel();
            regenerationTask = null;
        }

        nextRegenerationTick.clear();
        elapsedTicks = 0;
    }
}
