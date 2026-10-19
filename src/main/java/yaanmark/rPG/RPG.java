package yaanmark.rPG;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import yaanmark.rPG.manager.PlayerDataManager;
import yaanmark.rPG.database.*;
import yaanmark.rPG.combat.CombatManager;
import yaanmark.rPG.combat.VitalityManager;
import yaanmark.rPG.combat.RegenerationManager;

public final class RPG extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private DatabaseManager databaseManager;
    private RegenerationManager regenerationManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        playerDataManager = new PlayerDataManager(databaseManager);
        getServer().getPluginManager().registerEvents(new CombatManager(playerDataManager), this);
        getCommand("rpg").setExecutor(new RPGCommand(playerDataManager));
        Bukkit.getPluginManager().registerEvents(new Events(playerDataManager), this);
        regenerationManager = new RegenerationManager(this, playerDataManager);
        regenerationManager.start();
        getLogger().info("O Plugin RPG foi iniciado!");
    }


    @Override
    public void onDisable() {
        if (regenerationManager != null) {
            regenerationManager.stop();
        }

        if (playerDataManager != null) {
            playerDataManager.saveAll();
        }

        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("O Plugin RPG foi encerrado!");
    }


    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
