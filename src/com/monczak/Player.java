package com.monczak;

import java.util.HashSet;

// A special subset of entities, which is meant for players
public class Player extends Entity {

    protected HashSet<Spell> spells;

    public Player(String name, int health, int mana, HashSet<SpellType> immunities, HashSet<SpellType> weaknesses, HashSet<Spell> spells) {
        super(name, health, mana, immunities, weaknesses);
        this.spells = spells;
    }

    public HashSet<Spell> getSpells() { return new HashSet<>(spells); }

    @Override
    public void display() {
        System.out.printf("- %s -\n", name);
        System.out.printf("Health %d/%d\tMana %d/%d\n", health, maxHealth, mana, maxMana);
    }
}
