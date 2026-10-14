package yaanmark.rPG.player;

public class PlayerAttributes {

    private int strength;
    private int dexterity;
    private int intelligence;
    private int vitality;
    private int level = 1;
    private int experience = 0;
    private int attributePoints = 0;

    public PlayerAttributes() {
        this.strength = 10;
        this.dexterity = 10;
        this.intelligence = 10;
        this.vitality = 10;
    }

    public int getStrength() {
        return strength;
    }
    public int getDexterity() {
        return dexterity;
    }
    public int getIntelligence() {
        return intelligence;
    }
    public int getVitality() {
        return vitality;
    }
    public int getLevel() {
        return level;
    }
    public int getExperience() {
        return  experience;
    }
    public int getExperienceToNextLevel() {
        return level * 100;
    }
    public int getAttributePoints() {
        return attributePoints;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }
    public void setVitality(int vitality) {
        this.vitality = vitality;
    }
    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }
    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
    }
    public void setAttributePoints(int attributePoints) {
        this.attributePoints = Math.max(0, attributePoints);
    }


    public int addExperience(int amount) {
        if (amount <= 0) {
            return 0;
        }

        experience += amount;
        int levelsGained = 0;

        while (experience >= getExperienceToNextLevel()) {
            experience -= getExperienceToNextLevel();
            level++;
            levelsGained++;
        }

        attributePoints += levelsGained * 5;

        return levelsGained;
    }


    public boolean spendAttributePoints(String attribute, int amount) {
        if (amount <= 0) {
            return false;
        }

        String normalizedAttribute = attribute.toLowerCase()
                .replace("ç", "c")
                .replace("ã", "a");

        int currentValue;

        switch (normalizedAttribute) {
            case "forca":
            case "strength":
                currentValue = strength;
                break;

            case "destreza":
            case "dexterity":
                currentValue = dexterity;
                break;

            case "inteligencia":
            case "intelligence":
                currentValue = intelligence;
                break;

            case "vitalidade":
            case "vitality":
                currentValue = vitality;
                break;

            default:
                return false;
        }

        long totalCost = 0;

        for (int i = 0; i < amount; i++) {
            long difference = (long) currentValue + i - 9;
            long cost = (difference * difference + 9) / 10;
            totalCost += cost;

            if (totalCost > attributePoints) {
                return false;
            }
        }

        switch (normalizedAttribute) {
            case "forca":
            case "strength":
                strength += amount;
                break;

            case "destreza":
            case "dexterity":
                dexterity += amount;
                break;

            case "inteligencia":
            case "intelligence":
                intelligence += amount;
                break;

            case "vitalidade":
            case "vitality":
                vitality += amount;
                break;
        }

        attributePoints -= (int) totalCost;
        return true;
    }


    public long getAttributeUpgradeCost(String attribute, int amount) {
        if (amount <= 0) {
            return -1;
        }

        String normalized = attribute.toLowerCase()
                .replace("ç", "c")
                .replace("ã", "a");

        int currentValue;

        switch (normalized) {
            case "forca":
            case "strength":
                currentValue = strength;
                break;
            case "destreza":
            case "dexterity":
                currentValue = dexterity;
                break;
            case "inteligencia":
            case "intelligence":
                currentValue = intelligence;
                break;
            case "vitalidade":
            case "vitality":
                currentValue = vitality;
                break;
            default:
                return -1;
        }

        long totalCost = 0;

        for (int i = 0; i < amount; i++) {
            long difference = (long) currentValue + i - 9;
            long cost = (difference * difference + 9) / 10;

            totalCost += cost;

            if (totalCost > attributePoints) {
                return totalCost;
            }
        }

        return totalCost;
    }

}
