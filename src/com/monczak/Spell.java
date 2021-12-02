package com.monczak;

import java.util.HashSet;
import java.util.List;

// Represents a spell - an attack entities can cast on each other
// Spells can have custom defined effects
public class Spell {

    private String name;
    private SpellType type;
    private int cost;
    private int damage;

    @FunctionalInterface
    public interface SpellEffect {
        void method(Spell spell, Entity user, Entity target);
    }
    private SpellEffect effect;

    public Spell(String name, SpellType type, int cost, int damage, SpellEffect effect) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.damage = damage;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpellType getType() {
        return type;
    }

    public void setType(SpellType type) {
        this.type = type;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public SpellEffect getEffect() {
        return effect;
    }

    public void setEffect(SpellEffect effect) {
        this.effect = effect;
    }

    public void executeEffect(Entity user, Entity target) {
        effect.method(this, user, target);
    }

    public static HashSet<Spell> buildSet(Spell... spells) {
        return new HashSet<>(List.of(spells));
    }
}
