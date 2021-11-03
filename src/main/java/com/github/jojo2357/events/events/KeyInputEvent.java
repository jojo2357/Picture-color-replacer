package com.github.jojo2357.events.events;

import com.github.jojo2357.events.EventBase;
import com.github.jojo2357.events.EventTypes;

import java.util.HashMap;

public class KeyInputEvent extends EventBase {
    private static final HashMap<Character, Character> mapping;

    private static int lastModifications = 0;

    public static void updateModifications(int newMods){
        lastModifications = newMods;
    }

    public static boolean hasStoredModification(MODIFICATIONS mod) {
        return MODIFICATIONS.bitfieldHasMod(lastModifications, mod);
    }

    static {
        mapping = new HashMap<>();
        mapping.put('`', '~');
        mapping.put('1', '!');
        mapping.put('2', '@');
        mapping.put('3', '#');
        mapping.put('4', '$');
        mapping.put('5', '%');
        mapping.put('6', '^');
        mapping.put('7', '&');
        mapping.put('8', '*');
        mapping.put('9', '(');
        mapping.put('0', ')');
        mapping.put('-', '_');
        mapping.put('=', '+');
        mapping.put('[', '{');
        mapping.put(']', '}');
        mapping.put('\\', '|');
        mapping.put(';', ':');
        mapping.put('\'', '"');
        mapping.put(',', '<');
        mapping.put('.', '>');
        mapping.put('/', '?');
    }

    public final char KEY;
    private final int mods;

    public KeyInputEvent(char pressed, int modifications) {
        super(EventTypes.KeyInputEvent);
        if (pressed >= 'A' && pressed <= 'Z') {
            if (!MODIFICATIONS.bitfieldHasMod(modifications, MODIFICATIONS.SHIFT))
                KEY = Character.toLowerCase(pressed);
            else if (MODIFICATIONS.bitfieldOnlyHasMod(modifications, MODIFICATIONS.NONE))
                KEY = 0;
            else
                KEY = pressed;
        } else {
            if (MODIFICATIONS.bitfieldOnlyHasMod(modifications, MODIFICATIONS.SHIFT) && mapping.containsKey(pressed)) {
                KEY = mapping.get(pressed);
            } else {
                KEY = pressed;
            }
        }
        mods = modifications;
    }

    public boolean hasModification(MODIFICATIONS mod) {
        return MODIFICATIONS.bitfieldHasMod(mods, mod);
    }

    @Override
    public EventBase copy() {
        return this;
    }

    public static enum MODIFICATIONS {
        NONE, SHIFT, CTRL, ALT;

        public static boolean bitfieldOnlyHasMod(int bitfield, MODIFICATIONS mod) {
            return bitfieldHasMod(bitfield, mod) && ((1 << (mod.ordinal() - 1)) ^ bitfield) == 1 << (mod.ordinal() - 1);
        }

        public static boolean bitfieldHasMod(int bitfield, MODIFICATIONS mod) {
            return (bitfield == 0 && mod == NONE) || ((1 << (mod.ordinal() - 1)) & bitfield) == 1 << (mod.ordinal() - 1);
        }
    }
}
