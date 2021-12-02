package com.monczak;

import java.util.HashSet;

// Represents a generic entity with health, mana, immunities to certain spell types, weaknesses, and status timers
public abstract class Entity {

    protected String name;
    protected int health, maxHealth;
    protected int mana, maxMana;

    protected final HashSet<SpellType> immunities;
    protected final HashSet<SpellType> weaknesses;

    protected boolean dead;

    protected int paralyzeTimer;
    protected int burnTimer;

    public Entity(String name, int health, int mana, HashSet<SpellType> immunities, HashSet<SpellType> weaknesses) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.maxMana = mana;
        this.mana = mana;
        this.immunities = immunities;
        this.weaknesses = weaknesses;
        paralyzeTimer = 0;
        burnTimer = 0;
    }

    // Takes damage and sets whether the entity is dead after the attack
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0)
            dead = true;
    }

    public boolean isDead() { return dead; }

    public boolean isImmuneTo(SpellType type) {
        return immunities.contains(type);
    }

    public boolean isWeakTo(SpellType type) {
        return weaknesses.contains(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
    public int getMaxMana() {
        return maxMana;
    }

    // Deducts some mana from the entity (the amount of mana cannot be negative)
    public void takeMana(int mana) {
        this.mana -= mana;
        if (this.mana < 0) this.mana = 0;
    }

    // Ticks down the status timers
    public void tick() {
        paralyzeTimer--;
        if (paralyzeTimer < 0) paralyzeTimer = 0;
        burnTimer--;
        if (burnTimer < 0) burnTimer = 0;
    }

    public void setParalyzeTimer(int timer) {
        paralyzeTimer = timer;
    }

    public void setBurnTimer(int timer) {
        burnTimer = timer;
    }

    public int getParalyzeTimer() {
        return paralyzeTimer;
    }

    public int getBurnTimer() {
        return burnTimer;
    }

    // Prints the entity's status (to implement by child classes)
    public abstract void display();
}
