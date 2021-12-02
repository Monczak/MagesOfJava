package com.monczak;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class GameManager {

    private static GameManager instance;

    private Player player;
    private Enemy enemy;

    private final HashSet<Enemy> enemyPool;

    private final HashMap<String, Spell> spellSet;

    private final Random random;
    private final Scanner scanner;

    private final String introScreen = """
            888b     d888                                                     .d888\s
            8888b   d8888                                                    d88P" \s
            88888b.d88888                                                    888   \s
            888Y88888P888  8888b.   .d88b.   .d88b.  .d8888b         .d88b.  888888\s
            888 Y888P 888     "88b d88P"88b d8P  Y8b 88K            d88""88b 888   \s
            888  Y8P  888 .d888888 888  888 88888888 "Y8888b.       888  888 888   \s
            888   "   888 888  888 Y88b 888 Y8b.          X88       Y88..88P 888   \s
            88888888  888 "Y888888  "Y88888  "Y8888   88888P'        "Y88P"  888   \s
                "88b                    888                                        \s
                 888               Y8b d88P                                        \s
                 888  8888b.  888  888888888b.                                     \s
                 888     "88b 888  888     "88b                                    \s
                 888 .d888888 Y88  88P .d888888                                    \s
                 88P 888  888  Y8bd8P  888  888                                    \s
                 888 "Y888888   Y88P   "Y888888                           v1.0     \s
               .d88P                                                               \s
             .d88P"                                                                \s
            888P"                                                                  \s
            """;

    private GameManager() {
        random = new Random();
        enemyPool = new HashSet<>();
        spellSet = new HashMap<>();
        scanner = new Scanner(System.in);
    }

    // Gets the GameManager singleton
    public static GameManager getGameManager() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }

    public Random getRandom() {
        return random;
    }

    //region Entity creation

    // Creates the enemy
    // Basically pulls a random enemy from the enemy pool
    private void createEnemy() {
        int enemyIndex = random.nextInt(enemyPool.size());
        int i = 0;
        for (Enemy e : enemyPool) {
            if (i++ == enemyIndex) {
                enemy = e;
                break;
            }
        }
    }

    // Guides the player through a character creation process
    private void createPlayer(int defaultPlayerSpellCount) {
        System.out.println("Welcome, young apprentice!\nWould you like to create your own character, or fight with one we provide? (1/2)");
        String selection = readInput(scanner, scanner::nextLine, input -> input.equals("1") || input.equals("2"), "Hey, I don't understand!");
        if (selection.equals("1")) {
            // Create a player-designed player
            System.out.print("Enter your name: ");
            String name = readInput(scanner, scanner::nextLine, input -> !input.isEmpty(), "Unfortunately for you, you must have a name.");
            System.out.print("How many health points will you have? ");
            int health = readInput(scanner, scanner::nextInt, input -> input >= 1, "I expected you to be full of life.");
            System.out.print("How many mana points will you have? ");
            int mana = readInput(scanner, scanner::nextInt, input -> input >= 1, "As an apprentice mage, you must be able to handle at least some magic.");
            scanner.nextLine();

            int i = 1;
            SpellType[] types = SpellType.values();

            HashMap<Integer, SpellType> typeTranslator = new HashMap<>();   // For use in readMultiple as a mapper from ints to SpellTypes
            for (SpellType type : types) {
                typeTranslator.put(31 - Integer.numberOfLeadingZeros(type.getValue()), type);
            }

            System.out.println("Choose what you are going to be immune to. Enter the appropriate numbers one after another, separated by a space. (Or, enter \"n\" for nothing.)");
            HashSet<SpellType> immunities = new HashSet<>();
            for (SpellType type : types) {
                System.out.printf("(%d) %s %s", i, type.name(), i == types.length ? "" : "| ");
                i++;
            }
            readMultiple(immunities, typeTranslator, "Too many spell types here.", "I don't understand %s.");

            i = 1;
            System.out.println("Choose what you are going to be weak to. Enter the appropriate numbers one after another, separated by a space. (Or, enter \"n\" for nothing.)");
            HashSet<SpellType> weaknesses = new HashSet<>();
            for (SpellType type : types) {
                System.out.printf("(%d) %s %s", i, type.name(), i == types.length ? "" : "| ");
                i++;
            }
            readMultiple(weaknesses, typeTranslator, "Too many spell types here.", "I don't understand %s.");

            i = 1;
            System.out.println("Assemble your spell book.  Enter the appropriate numbers one after another, separated by a space.");
            HashSet<Spell> spells = new HashSet<>();
            HashMap<Integer, Spell> spellTranslator = new HashMap<>();  // For use in readMultiple as a mapper from ints to Spells

            ArrayList<Spell> listOfSpells = new ArrayList<>(getAllSpells());
            listOfSpells.sort(Comparator.comparing(Spell::getType).thenComparing(Spell::getName));
            for (Spell spell : listOfSpells) {
                System.out.printf("(%d) %s (%s)\n", i, spell.getName(), spell.getType().name());
                spellTranslator.put(i, spell);
                i++;
            }
            readMultiple(spells, spellTranslator, "Too many spells here.", "I don't understand %s.");

            player = new Player(name, health, mana, immunities, weaknesses, spells);
            System.out.printf("Your character %s has been created.", name);
        }
        else {
            // Create a default player named The Apprentice, with 200 health, 80 mana, no immunities/weaknesses and some random spells
            ArrayList<Spell> shuffledSpells = new ArrayList<>(getAllSpells());
            Collections.shuffle(shuffledSpells);
            HashSet<Spell> newSpells = new HashSet<>();
            for (int i = 0; i < defaultPlayerSpellCount; i++)
                newSpells.add(shuffledSpells.get(i));

            player = new Player("The Apprentice", 200, 80, new HashSet<>(), new HashSet<>(), newSpells);
        }
    }

    //endregion

    //region Game flow

    // The main game loop
    // Display enemies, handle turns, handle burn statuses, update entity statuses
    public void gameLoop() throws Exception {
        while (true) {
            System.out.println("----------------------------------------");
            enemy.display();
            player.display();
            System.out.println();

            if (handleTurn(player, enemy, this::selectPlayerSpell)) break;
            if (handleTurn(enemy, player, this::selectEnemySpell)) break;

            if (handleBurn(player)) break;
            if (handleBurn(enemy)) break;

            player.tick();
            enemy.tick();
        }
    }

    // Sets up the game, checks whether enemy pool and spell set is valid and creates the player and enemy
    public void setupGame() throws Exception {
        System.out.println(introScreen);

        if (enemyPool.isEmpty()) {
            throw new Exception("Enemy pool is empty");
        }
        if (spellSet.isEmpty()) {
            throw new Exception("Spell set is empty");
        }

        createPlayer(4);
        createEnemy();
    }

    //endregion

    //region Game logic

    // Handles a battle turn
    // Responsible for selecting spells, casting them, executing their effects and dealing the appropriate damage
    // If returns true, the game loop should break
    private boolean handleTurn(Entity source, Entity target, Callable<Spell> spellSelector) throws Exception {
        if (source.getParalyzeTimer() == 0) {
            Spell spell = spellSelector.call();
            if (spell != null)
                return castSpell(source, spell, target);
        }
        else {
            System.out.printf("%s is paralyzed for %d more turn%s.\n", source.getName(), source.getParalyzeTimer() - 1, source.getParalyzeTimer() - 1 == 1 ? "" : "s");
        }
        return false;
    }

    // Makes burnt entities take 15 damage
    // If returns true, the game loop should break
    private boolean handleBurn(Entity entity) {
        if (entity.getBurnTimer() > 0) {
            entity.takeDamage(15);
            return entity.isDead();
        }
        return false;
    }

    // Casts a spell on the target entity
    // Executes its effect and deals damage
    // If the target is dead, return true
    private boolean castSpell(Entity source, Spell spell, Entity target) {
        System.out.printf("\n%s casts %s!\n", source.getName(), spell.getName());
        if (!target.isImmuneTo(spell.getType()))
            spell.executeEffect(source, target);
        if (target.isDead()) return true;

        dealDamage(source, spell, target);
        return target.isDead();
    }

    // Inflicts damage on the target
    // Also deducts mana from the source
    public void dealDamage(Entity source, Spell spell, Entity target) {
        int damage = spell.getDamage();
        if (damage != 0 && target.isWeakTo(spell.getType())) {
            System.out.printf("%s is hit with a staggering blow!\n", target.getName());
            damage *= 2;
        }
        if (target.isImmuneTo(spell.getType())) {
            System.out.printf("But %s is immune to %s...\n", target.getName(), spell.getName());
            damage = 0;
        }

        if (damage != 0) System.out.printf("%s takes %d damage!\n", target.getName(), damage);

        target.takeDamage(damage);
        source.takeMana(spell.getCost());
    }

    //endregion

    //region Announcements

    // Announces the enemy once the battle starts
    public void announceEnemy() {
        System.out.printf("%s has appeared!\n%s\n\nLet the fight begin!\n\n", enemy.getName(), enemy.getCry());
    }

    // Displays who won, or announces a tie
    public void announceResult() {
        if (player.isDead() && enemy.isDead()) {
            System.out.printf("Both %s and %s have perished. The fight is over, with no one victorious.\n", player.getName(), enemy.getName());
        }
        else {
            System.out.printf("%s has perished. The fight is over and %s stands victorious.\n", player.isDead() ? player.getName() : enemy.getName(), enemy.isDead() ? player.getName() : enemy.getName());
        }
    }

    //endregion

    //region Input

    // Reads multiple ints from standard input and stashes their corresponding objects in the target set
    // Enforces valid inputs (if player entered invalid data, start over)
    private <T> void readMultiple(HashSet<T> targetSet, HashMap<Integer, T> dict, String messageOnWrongNumber, String messageOnInvalid) {
        while (true) {
            String input = readInput(scanner, scanner::nextLine, in -> !in.isEmpty(), "Please say something.");
            if (input.equals("n"))
                return;

            HashSet<String> set = new HashSet<>(List.of(input.split("\\s* \\s*")));
            if (set.size() == 0 || set.size() > dict.size()) {
                System.out.println(messageOnWrongNumber);
                continue;
            }

            boolean success = true;
            for (String s : set) {
                try {
                    int id = Integer.parseInt(s);
                    if (id < 1 || id > dict.size())
                        throw new Exception();
                    targetSet.add(dict.get(id));
                }
                catch (Exception e) {
                    success = false;
                    System.out.printf(messageOnInvalid, s);
                    break;
                }
            }
            if (success)
                break;
        }
    }

    // Reads a value from the input
    // If it does not match the predicate, a message will be printed, and it'll start over
    private <T> T readInput(Scanner scanner, Callable<T> reader, Predicate<T> predicate, String errorMessage) {
        T input = null;
        while (true) {
            boolean success = true;
            try {
                input = reader.call();
                if (!predicate.test(input))
                    throw new Exception();
            }
            catch (Exception e) {
                System.out.println(errorMessage);
                scanner.nextLine();
                success = false;
            }
            if (success)
                break;
        }
        return input;
    }

    //endregion

    //region Spell selection

    // Allows the player to select a spell from their spell set
    public Spell selectPlayerSpell() {
        HashSet<Spell> spells = player.getSpells();
        spells.removeIf(spell -> spell.getCost() > player.getMana());

        if (spells.isEmpty()) {
            System.out.println("You don't have enough mana to cast spells.");
            return null;
        }

        System.out.println("Which spell shall you cast?");
        int i = 1;

        Spell[] spellCache = new Spell[spells.size()];
        for (Spell spell : spells) {
            System.out.printf("(%d) %s (%s) %s ", i, spell.getName(), spell.getType().name(), i == spells.size() ? "" : "|");
            spellCache[i - 1] = spell;
            i++;
        }
        int spellId = readInput(scanner, scanner::nextInt, input -> input >= 1 && input <= spellCache.length, "I don't know what you're talking about.");
        return spellCache[spellId - 1];
    }

    // Selects a random spell from the enemy's spell set
    private Spell selectEnemySpell() {
        HashSet<Spell> enemySpells = enemy.getSpells();
        enemySpells.removeIf(spell -> spell.getCost() > enemy.getMana());
        if (enemySpells.isEmpty()) {
            System.out.printf("%s does not have enough mana to cast spells.\n", enemy.getName());
            return null;
        }

        int spellId = random.nextInt(enemySpells.size());
        Spell enemySpell = new Spell("Default", SpellType.Earth, 0, 0, (spell, user, target) -> {});
        int i = 0;
        for (Spell spell : enemySpells) {
            enemySpell = spell;
            if (spellId == i++)
                break;
        }
        return enemySpell;
    }

    //endregion

    //region Loading

    // Adds enemies to the enemy pool
    public void registerEnemies(Enemy... enemies) {
        enemyPool.addAll(List.of(enemies));
    }

    // Adds spells to the global spell set
    public void registerSpells(Spell... spells) {
        for (Spell spell : spells)
            spellSet.put(spell.getName(), spell);
    }

    //endregion

    //region Utilities

    // Retrieves a spell by its name
    public Spell getSpell(String name) {
        return spellSet.get(name);
    }

    // Returns a copy of the global spell set
    public HashSet<Spell> getAllSpells() {
        return new HashSet<>(spellSet.values());
    }

    //endregion
}
