package yaanmark.rPG.manager;

import yaanmark.rPG.database.DatabaseManager;
import yaanmark.rPG.player.PlayerAttributes;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final Map<UUID, PlayerAttributes> playerAttributes = new HashMap<>();

    private final DatabaseManager databaseManager;

    public PlayerDataManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public PlayerAttributes getAttributes(Player player) {
        UUID uuid = player.getUniqueId();

        return playerAttributes.computeIfAbsent(
                uuid,
                databaseManager::loadAttributes
        );
    }

    public void saveAttributes(Player player) {
        UUID uuid = player.getUniqueId();

        PlayerAttributes attributes = playerAttributes.get(uuid);

        if (attributes != null) {
            databaseManager.saveAttributes(uuid, attributes);
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, PlayerAttributes> entry : playerAttributes.entrySet()) {
            databaseManager.saveAttributes(
                    entry.getKey(),
                    entry.getValue()
            );
        }
    }

    public void unloadPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        saveAttributes(player);
        playerAttributes.remove(uuid);
    }

    public int addExperience(Player player, int amount) {
        PlayerAttributes attributes = getAttributes(player);

        int levelsGained = attributes.addExperience(amount);

        if (amount > 0) {
            saveAttributes(player);
        }

        return levelsGained;
    }

}
