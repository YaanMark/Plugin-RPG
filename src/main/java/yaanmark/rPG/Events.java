
package yaanmark.rPG;

import yaanmark.rPG.combat.VitalityManager;
import yaanmark.rPG.manager.PlayerDataManager;
import yaanmark.rPG.player.PlayerAttributes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {

    private final PlayerDataManager playerDataManager;
    private final VitalityManager vitalityManager =
            new VitalityManager();

    public Events(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerAttributes attributes =
                playerDataManager.getAttributes(player);

        vitalityManager.updateMaxHealth(player, attributes);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDataManager.unloadPlayer(event.getPlayer());
    }
}