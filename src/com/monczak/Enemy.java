package com.monczak;

import java.util.HashSet;

// A special subset of entities, which is reserved for enemies
public class Enemy extends Entity {

    protected HashSet<Spell> spells;
    protected String cry;

    public Enemy(String name, int health, int mana, HashSet<SpellType> immunities, HashSet<SpellType> weaknesses, HashSet<Spell> spells, String cry) {
        super(name, health, mana, immunities, weaknesses);

        this.spells = spells;
        this.cry = cry;
    }

    public HashSet<Spell> getSpells() { return new HashSet<>(spells); }

    public String getCry() { return cry; }
    public void setCry(String cry) { this.cry = cry; }

    @Override
    public void display() {
        System.out.printf("%40s\n", String.format("- %s -", name));
        System.out.printf("%40s\n", String.format("Health %d/%d\n", health, maxHealth));
    }
}
