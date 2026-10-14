package yaanmark.rPG.database;

import org.bukkit.plugin.java.JavaPlugin;
import yaanmark.rPG.player.PlayerAttributes;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File folder = plugin.getDataFolder();

            if (!folder.exists() && !folder.mkdirs()) {
                throw new IllegalStateException(
                        "Não foi possível criar a pasta do plugin."
                );
            }

            File databaseFile = new File(folder, "rpg.db");
            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

            connection = DriverManager.getConnection(url);

            createTables();

            plugin.getLogger().info("Banco SQLite conectado com sucesso!");

        } catch (SQLException | IllegalStateException e) {
            plugin.getLogger().severe(
                    "Não foi possível conectar ao SQLite: " + e.getMessage()
            );
            throw new RuntimeException("Falha ao iniciar o banco de dados.", e);
        }
    }

    private void createTables() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_attributes (
                uuid TEXT PRIMARY KEY,
                strength INTEGER NOT NULL DEFAULT 10,
                dexterity INTEGER NOT NULL DEFAULT 10,
                intelligence INTEGER NOT NULL DEFAULT 10,
                vitality INTEGER NOT NULL DEFAULT 10
            )
            """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }

        addColumnIfMissing(
                "level",
                "ALTER TABLE player_attributes ADD COLUMN level INTEGER NOT NULL DEFAULT 1"
        );

        addColumnIfMissing(
                "experience",
                "ALTER TABLE player_attributes ADD COLUMN experience INTEGER NOT NULL DEFAULT 0"
        );

        addColumnIfMissing(
                "attribute_points",
                "INTEGER NOT NULL DEFAULT 0"
        );
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
            connection = null;
            plugin.getLogger().info("Conexão SQLite encerrada.");
        } catch (SQLException e) {
            plugin.getLogger().severe(
                    "Erro ao fechar o SQLite: " + e.getMessage()
            );
        }
    }
    public PlayerAttributes loadAttributes(UUID uuid) {
        String sql = """
            SELECT strength, dexterity, intelligence, vitality
            FROM player_attributes
            WHERE uuid = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    PlayerAttributes attributes = new PlayerAttributes();

                    attributes.setLevel(result.getInt("level"));
                    attributes.setExperience(result.getInt("experience"));
                    attributes.setStrength(result.getInt("strength"));
                    attributes.setDexterity(result.getInt("dexterity"));
                    attributes.setIntelligence(result.getInt("intelligence"));
                    attributes.setVitality(result.getInt("vitality"));
                    attributes.setAttributePoints(result.getInt("attribute_points"));

                    return attributes;
                }
            }

            PlayerAttributes attributes = new PlayerAttributes();
            saveAttributes(uuid, attributes);

            return attributes;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar atributos do jogador.", e);
        }
    }

    public void saveAttributes(UUID uuid, PlayerAttributes attributes) {
        String sql = """
        INSERT INTO player_attributes
            (uuid, strength, dexterity, intelligence, vitality, level, experience)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(uuid) DO UPDATE SET
            strength = excluded.strength,
            dexterity = excluded.dexterity,
            intelligence = excluded.intelligence,
            vitality = excluded.vitality,
            level = excluded.level,
            experience = excluded.experience
        """;


        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setInt(2, attributes.getStrength());
            statement.setInt(3, attributes.getDexterity());
            statement.setInt(4, attributes.getIntelligence());
            statement.setInt(5, attributes.getVitality());
            statement.setInt(6, attributes.getLevel());
            statement.setInt(7, attributes.getExperience());
            statement.setInt(8, attributes.getAttributePoints());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar atributos do jogador.", e);
        }
    }

    private void addColumnIfMissing(String columnName, String sql) throws SQLException {
        boolean columnExists = false;

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "PRAGMA table_info(player_attributes)"
             )) {

            while (result.next()) {
                if (columnName.equalsIgnoreCase(result.getString("name"))) {
                    columnExists = true;
                    break;
                }
            }
        }

        if (!columnExists) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        }
    }

}