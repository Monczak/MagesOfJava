package com.monczak;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        GameManager gameManager = GameManager.getGameManager();

        // Define some spells
        gameManager.registerSpells(
                new Spell("Bug Stomp", SpellType.Earth, 5, 28, (spell, user, target) -> {
                    String wormChars = "._-";
                    StringBuilder builder = new StringBuilder();

                    int wormCount = gameManager.getRandom().nextInt(5) + 2;
                    for (int i = 0; i < wormCount; i++) {
                        int wormLength = gameManager.getRandom().nextInt(8) + 5;
                        StringBuilder worm = new StringBuilder();
                        for (int j = 0; j < wormLength; j++)
                            worm.append(wormChars.charAt(gameManager.getRandom().nextInt(wormChars.length())));
                        builder.append(worm).append("O  ");
                    }
                    System.out.println(builder);
                }),
                new Spell("Burning Hands", SpellType.Fire, 5, 26, (spell, user, target) -> {
                    System.out.printf("The arena lights up as %s's hands flame.\n", user.name);
                }),
                new Spell("Stagnant Wave", SpellType.Water, 5, 25, (spell, user, target) -> {
                    System.out.printf("%s is feeling a little bit wet.\n", target.name);
                }),
                new Spell("Unnatural Gust", SpellType.Air, 5, 20, (spell, user, target) -> {
                    String[] messages = new String[] {
                        "A slight wind sweeps through the air...",
                        "A gust blows nearby...",
                        "Leaves shake slightly as the wind blows...",
                        "You hear a slight wind passing through..."
                    };
                    System.out.println(messages[gameManager.getRandom().nextInt(messages.length)]);
                }),
                new Spell("Mockery", SpellType.Psycho, 5, 20, (spell, user, target) -> {
                    StringBuilder builder = new StringBuilder();

                    int mockCount = gameManager.getRandom().nextInt(3) + 3;
                    for (int i = 0; i < mockCount; i++) {
                        StringBuilder mock = new StringBuilder();
                        for (int j = 0; j < target.getName().length(); j++) {
                            String c = Character.toString(target.getName().charAt(j));
                            mock.append(gameManager.getRandom().nextBoolean() ? c.toUpperCase() : c.toLowerCase());
                        }
                        builder.append(mock).append(i == mockCount - 1 ? "" : " ");
                    }
                    System.out.printf("%s chants \"%s\".\n", user.getName(), builder);
                }),
                new Spell("Commemoration", SpellType.Earth, 12, 0, (spell, user, target) -> {
                    target.setParalyzeTimer(4);
                    System.out.printf("A dirt statue has been erected in %s's place.\n%s cannot move for 3 turns.\n", target.getName(), target.getName());
                }),
                new Spell("Defender's Firewall", SpellType.Fire, 10, 0, (spell, user, target) -> {
                    target.setBurnTimer(3);
                    System.out.printf("A wall of fire appears before %s.\n%s has been burnt for 2 turns.\n", user.getName(), target.getName());
                }),
                new Spell("Blue Screen of Death", SpellType.Water, 10, 60, (spell, user, target) -> {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < 5; i++)
                        builder.append("\u001B[34m---").append(UUID.randomUUID().toString().replace("-", "")).append("---\u001B[0m\n");
                    System.out.print(builder);
                }),
                new Spell("Turbulence", SpellType.Air, 12, 70, (spell, user, target) -> {
                    for (int i = 0; i < 5; i++) {
                        int spaces = gameManager.getRandom().nextInt(10);
                        for (int j = 0; j < spaces; j++)
                            System.out.print(" ");
                        System.out.println("Whoosh!");
                    }
                }),
                new Spell("Health Swap", SpellType.Psycho, 15, 0, (spell, user, target) -> {
                    int health = user.getHealth();
                    user.setHealth(target.getHealth());
                    target.setHealth(health);
                    System.out.printf("%s and %s have exchanged their health.\n", user.getName(), target.getName());
                }),
                new Spell("Quake of the Earth", SpellType.Earth, 30, 140, (spell, user, target) -> {
                    System.out.println("A massive earthquake disrupts the ground!");

                    for (int i = 0; i < 30; i++)
                        System.out.print(gameManager.getRandom().nextBoolean() ? "\\" : "/");
                    System.out.println();

                    user.takeDamage(user.getMaxHealth() / 2);
                    if (user.isDead())
                        System.out.printf("%s couldn't handle it.", user.getName());
                }),
                new Spell("Phoenix's Wrath", SpellType.Fire, 30, 150, (spell, user, target) -> {
                    System.out.println("A huge phoenix appears in the sky!");

                    if (gameManager.getRandom().nextDouble() <= 0.8) {
                        System.out.printf("%s couldn't handle the heat and has been burnt for 3 turns.\n", target.getName());
                        target.setBurnTimer(4);
                    }
                    else {
                        System.out.printf("%s seems to have withstood the heat.\n", target.getName());
                    }

                    if (gameManager.getRandom().nextDouble() <= 0.1) {
                        System.out.printf("However, the phoenix seems not to be very fond of %s. %s has been burnt for 2 turns.\n", user.getName(), user.getName());
                        user.setBurnTimer(3);
                    }
                }),
                new Spell("Torrential Downpour", SpellType.Water, 30, 130, (spell, user, target) -> {
                    System.out.println("A huge rainstorm passes by!");

                    if (gameManager.getRandom().nextDouble() < 0.3) {
                        System.out.printf("The cold has left %s paralyzed for 2 turns.\n", target.getName());
                        target.setParalyzeTimer(3);
                    }
                }),
                new Spell("Blast of Atmosphere", SpellType.Air, 30, 160, (spell, user, target) -> {
                    System.out.println("A huge, shrieking wind blasts through!");
                    if (gameManager.getRandom().nextDouble() <= 0.2) {
                        System.out.printf("%s has been left paralyzed by the enormous wind for 2 turns.\n", user.getName());
                        user.setParalyzeTimer(3);
                    }
                    if (gameManager.getRandom().nextDouble() <= 0.2) {
                        System.out.printf("%s has been left paralyzed by the enormous wind for 2 turns.\n", target.getName());
                        target.setParalyzeTimer(3);
                    }
                }),
                new Spell("Mental Discombobulation", SpellType.Psycho, 30, 170, (spell, user, target) -> {
                    int limit = gameManager.getRandom().nextInt(50) + 50;
                    for (int i = 0; i < limit; i++)
                        System.out.print(gameManager.getRandom().nextBoolean() ? "b" : "l");
                    System.out.println();

                    HashSet<Spell> spells = gameManager.getAllSpells();
                    spells.remove(spell);
                    int spellsToTrigger = gameManager.getRandom().nextInt(3) + 1;
                    for (int i = 0; i < spellsToTrigger; i++) {
                        Spell selectedSpell = (Spell) spells.toArray()[gameManager.getRandom().nextInt(spells.size())];
                        System.out.printf("The spell seems to have triggered %s...\n", selectedSpell.getName());
                        selectedSpell.executeEffect(user, target);
                    }
                })
        );

        // Define some enemies
        gameManager.registerEnemies(
                new Enemy("Feeble Imp", 50, 30, new HashSet<>(List.of()), new HashSet<>(List.of(SpellType.Fire)), new HashSet<>(List.of(
                        gameManager.getSpell("Stagnant Wave"),
                        gameManager.getSpell("Mockery")
                )), "Impaimpa!"),
                new Enemy("The Droplet", 70, 45, new HashSet<>(List.of(SpellType.Fire)), new HashSet<>(List.of(SpellType.Earth, SpellType.Air)), new HashSet<>(List.of(
                        gameManager.getSpell("Stagnant Wave"),
                        gameManager.getSpell("Blue Screen of Death")
                )), "Plop! Haiiya!"),
                new Enemy("Mind Twister", 300, 100, new HashSet<>(List.of(SpellType.Air, SpellType.Psycho)), new HashSet<>(List.of(SpellType.Fire, SpellType.Earth)), new HashSet<>(List.of(
                        gameManager.getSpell("Mockery"),
                        gameManager.getSpell("Health Swap"),
                        gameManager.getSpell("Mental Discombobulation")
                )), "Mmmmmmiiiinnnnndssss...")
        );

        // And launch the game!
        try {
            gameManager.setupGame();
            gameManager.announceEnemy();
            gameManager.gameLoop();
            gameManager.announceResult();
        }
        catch (Exception e) {
            System.out.println("\u001b[31mAn error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
