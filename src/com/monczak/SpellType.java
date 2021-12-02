package com.monczak;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// Represents types of spells
public enum SpellType {
    Earth(1),
    Fire(2),
    Water(4),
    Air(8),
    Psycho(16),
    ;

    private final int value;
    SpellType(int i) {
        value = i;
    }

    public int getValue() { return value; }

    // Build a list of spells corresponding to the binary representation of a number
    // Allows for bitmasking (though unused)
    public static ArrayList<SpellType> parseSpellTypes(int value) {
        ArrayList<SpellType> types = new ArrayList<>();
        for (SpellType type : values()) {
            if ((value & type.getValue()) != 0)
                types.add(type);
        }
        return types;
    }
}
