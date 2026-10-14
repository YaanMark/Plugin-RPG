package yaanmark.rPG;

import yaanmark.rPG.manager.PlayerDataManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {

    private final PlayerDataManager playerDataManager;

    public Events(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerDataManager.getAttributes(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDataManager.unloadPlayer(event.getPlayer());
    }
}
