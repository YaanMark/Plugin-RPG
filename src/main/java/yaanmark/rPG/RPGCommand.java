package yaanmark.rPG;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import yaanmark.rPG.manager.PlayerDataManager;
import yaanmark.rPG.player.PlayerAttributes;
import yaanmark.rPG.combat.VitalityManager;

public class RPGCommand implements CommandExecutor {

    private final PlayerDataManager playerDataManager;
    private final VitalityManager vitalityManager = new VitalityManager();

    public RPGCommand(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6§lRPG Foundation");
            sender.sendMessage("§7Bem-vindo ao seu RPG!");
            sender.sendMessage("§eUse /rpg ajuda para ver os comandos.");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "upar":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(
                            "§cEste comando só pode ser usado por um jogador."
                    );
                    return true;
                }

                Player player = (Player) sender;

                if (args.length != 3) {
                    player.sendMessage(
                            "§eUso: /rpg upar <atributo> <quantidade>"
                    );
                    player.sendMessage(
                            "§7Atributos: forca, destreza, inteligencia, vitalidade"
                    );
                    return true;
                }

                int quantity;

                try {
                    quantity = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(
                            "§cA quantidade deve ser um número inteiro."
                    );
                    return true;
                }

                if (quantity <= 0) {
                    player.sendMessage(
                            "§cA quantidade deve ser maior que zero."
                    );
                    return true;
                }

                PlayerAttributes playerAttributes =
                        playerDataManager.getAttributes(player);

                String attribute = args[1].toLowerCase()
                        .replace("ç", "c")
                        .replace("ã", "a");

                long cost = playerAttributes.getAttributeUpgradeCost(
                        attribute, quantity
                );

                if (cost < 0) {
                    player.sendMessage(
                            "§cAtributo inválido. Use forca, destreza, inteligencia ou vitalidade."
                    );
                    return true;
                }

                if (cost > playerAttributes.getAttributePoints()) {
                    player.sendMessage(
                            "§cPontos insuficientes! Custo: " + cost
                                    + " | Disponíveis: "
                                    + playerAttributes.getAttributePoints()
                    );
                    return true;
                }

                if (!playerAttributes.spendAttributePoints(attribute, quantity)) {
                    player.sendMessage(
                            "§cNão foi possível realizar a melhoria."
                    );
                    return true;
                }

                playerDataManager.saveAttributes(player);

                if (attribute.equals("vitalidade")
                        || attribute.equals("vitality")) {
                    vitalityManager.updateMaxHealth(player, playerAttributes);
                }

                player.sendMessage(
                        "§aAtributo melhorado com sucesso!"
                );
                player.sendMessage(
                        "§eAtributo: §f" + attribute
                                + " §7| §eAumentado em: §f+" + quantity
                );
                player.sendMessage(
                        "§eCusto: §f" + cost
                                + " §7| §ePontos restantes: §f"
                                + playerAttributes.getAttributePoints()
                );

                return true;

            case "xp":
                if (!sender.hasPermission("rpg.admin")) {
                    sender.sendMessage("§cVocê não tem permissão para isso.");
                    return true;
                }

                if (args.length != 3) {
                    sender.sendMessage("§eUso: /rpg xp <jogador> <quantidade>");
                    return true;
                }

                Player xpTarget = Bukkit.getPlayerExact(args[1]);

                if (xpTarget == null) {
                    sender.sendMessage("§cJogador não encontrado ou offline.");
                    return true;
                }

                int amount;

                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cA quantidade de XP deve ser um número inteiro.");
                    return true;
                }

                if (amount <= 0) {
                    sender.sendMessage("§cA quantidade de XP deve ser maior que zero.");
                    return true;
                }

                int levelsGained = playerDataManager.addExperience(xpTarget, amount);

                sender.sendMessage(
                        "§aVocê concedeu " + amount + " XP para " + xpTarget.getName() + "."
                );

                xpTarget.sendMessage("§aVocê recebeu " + amount + " XP!");

                if (levelsGained > 0) {
                    PlayerAttributes xpAttributes =
                            playerDataManager.getAttributes(xpTarget);

                    xpTarget.sendMessage(
                            "§6Você subiu " + levelsGained
                                    + " nível(is)! Nível atual: "
                                    + xpAttributes.getLevel()
                    );
                }

                return true;

            case "set":
                if (!(sender instanceof Player)
                        || !sender.hasPermission("rpg.admin")) {
                    sender.sendMessage("§cVocê não tem permissão para isso.");
                    return true;
                }

                if (args.length != 4) {
                    sender.sendMessage(
                            "§eUso: /rpg set <jogador> <atributo> <valor>"
                    );
                    return true;
                }

                Player target = org.bukkit.Bukkit.getPlayerExact(args[1]);

                if (target == null) {
                    sender.sendMessage("§cJogador não encontrado ou offline.");
                    return true;
                }

                int value;

                try {
                    value = Integer.parseInt(args[3]);

                    if (value < 0) {
                        sender.sendMessage("§cO valor não pode ser negativo.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cO valor precisa ser um número inteiro.");
                    return true;
                }

                PlayerAttributes targetAttributes =
                        playerDataManager.getAttributes(target);

                switch (args[2].toLowerCase()) {
                    case "forca":
                        targetAttributes.setStrength(value);
                        break;

                    case "destreza":
                        targetAttributes.setDexterity(value);
                        break;

                    case "inteligencia":
                        targetAttributes.setIntelligence(value);
                        break;

                    case "vitalidade":
                        targetAttributes.setVitality(value);
                        break;

                    default:
                        sender.sendMessage(
                                "§cAtributo inválido. Use forca, destreza, inteligencia ou vitalidade."
                        );
                        return true;
                }

                sender.sendMessage(
                        "§aAtributo atualizado para " + target.getName() + "!"
                );

                target.sendMessage("§eUm dos seus atributos foi atualizado.");
                return true;

            case "status":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Este comando deve ser usado por um jogador.");
                    return true;
                }

                PlayerAttributes attributes = playerDataManager.getAttributes((Player) sender);

                sender.sendMessage("§6§l=== SEU STATUS ===");
                sender.sendMessage("§cForça: §f" + attributes.getStrength());
                sender.sendMessage("§aDestreza: §f" + attributes.getDexterity());
                sender.sendMessage("§9Inteligência: §f" + attributes.getIntelligence());
                sender.sendMessage("§eVitalidade: §f" + attributes.getVitality());
                sender.sendMessage("§6Nível: §f" + attributes.getLevel());
                sender.sendMessage("§ePontos de atributo: §f" + attributes.getAttributePoints());

                sender.sendMessage(
                        "§6Experiência: §f"
                                + attributes.getExperience()
                                + "/"
                                + attributes.getExperienceToNextLevel()
                );
                return true;

            case "ajuda":
            case "help":
                sender.sendMessage("§6§l=== RPG FOUNDATION ===");
                sender.sendMessage("§e/rpg §7- Mensagem inicial");
                sender.sendMessage("§e/rpg status §7- Mostra seus atributos");
                sender.sendMessage("§e/rpg ajuda §7- Mostra esta ajuda");
                sender.sendMessage("§e/rpg xp <jogador> <quantidade> §7- Concede XP");
                sender.sendMessage("§e/rpg upar <atributo> <quantidade> §7- Evolui um atributo");
                return true;

            default:
                sender.sendMessage("§cSubcomando desconhecido.");
                sender.sendMessage("§7Use /rpg ajuda.");
                return true;
        }
    }
}
