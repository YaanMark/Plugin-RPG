package yaanmark.rPG;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import yaanmark.rPG.manager.PlayerDataManager;
import yaanmark.rPG.database.*;

public final class RPG extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        playerDataManager = new PlayerDataManager(databaseManager);
        getCommand("rpg").setExecutor(new RPGCommand(playerDataManager));
        Bukkit.getPluginManager().registerEvents(new Events(playerDataManager), this);
        getLogger().info("O Plugin RPG foi iniciado!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (playerDataManager != null) { playerDataManager.saveAll(); }
        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("O Plugin RPG foi encerrado!");
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
