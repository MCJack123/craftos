import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

class ComputerKey extends Number {

    private int new_key;
    // these just have to be unique, doesn't matter what number it'll be since it'll be converted internally
    private static final int VK_LEFT_CONTROL = 17;
    private static final int VK_RIGHT_CONTROL = 317;
    private static final int VK_LEFT_SHIFT = 16;
    private static final int VK_RIGHT_SHIFT = 316;
    private static final int VK_LEFT_ALT = 18;
    private static final int VK_RIGHT_ALT = 318;

    private static final Map<Integer, Integer> mappings = new HashMap<>();
    static {
        mappings.put(KeyEvent.VK_1, 2);
        mappings.put(KeyEvent.VK_2, 3);
        mappings.put(KeyEvent.VK_3, 4);
        mappings.put(KeyEvent.VK_4, 5);
        mappings.put(KeyEvent.VK_5, 6);
        mappings.put(KeyEvent.VK_6, 7);
        mappings.put(KeyEvent.VK_7, 8);
        mappings.put(KeyEvent.VK_8, 9);
        mappings.put(KeyEvent.VK_9, 10);
        mappings.put(KeyEvent.VK_0, 11);
        mappings.put(KeyEvent.VK_MINUS, 12);
        mappings.put(KeyEvent.VK_EQUALS, 13);
        mappings.put(KeyEvent.VK_BACK_SPACE, 14);
        mappings.put(KeyEvent.VK_TAB, 15);
        mappings.put(KeyEvent.VK_Q, 16);
        mappings.put(KeyEvent.VK_W, 17);
        mappings.put(KeyEvent.VK_E, 18);
        mappings.put(KeyEvent.VK_R, 19);
        mappings.put(KeyEvent.VK_T, 20);
        mappings.put(KeyEvent.VK_Y, 21);
        mappings.put(KeyEvent.VK_U, 22);
        mappings.put(KeyEvent.VK_I, 23);
        mappings.put(KeyEvent.VK_O, 24);
        mappings.put(KeyEvent.VK_P, 25);
        mappings.put(KeyEvent.VK_BRACELEFT, 26);
        mappings.put(KeyEvent.VK_BRACERIGHT, 27);
        mappings.put(KeyEvent.VK_ENTER, 28);
        mappings.put(VK_LEFT_CONTROL, 29);
        mappings.put(KeyEvent.VK_A, 30);
        mappings.put(KeyEvent.VK_S, 31);
        mappings.put(KeyEvent.VK_D, 32);
        mappings.put(KeyEvent.VK_F, 33);
        mappings.put(KeyEvent.VK_G, 34);
        mappings.put(KeyEvent.VK_H, 35);
        mappings.put(KeyEvent.VK_J, 36);
        mappings.put(KeyEvent.VK_K, 37);
        mappings.put(KeyEvent.VK_L, 38);
        mappings.put(KeyEvent.VK_SEMICOLON, 39);
        mappings.put(KeyEvent.VK_QUOTE, 40);
        mappings.put(KeyEvent.VK_DEAD_GRAVE, 41);
        mappings.put(VK_LEFT_SHIFT, 42);
        mappings.put(KeyEvent.VK_BACK_SLASH, 43);
        mappings.put(KeyEvent.VK_Z, 44);
        mappings.put(KeyEvent.VK_X, 45);
        mappings.put(KeyEvent.VK_C, 46);
        mappings.put(KeyEvent.VK_V, 47);
        mappings.put(KeyEvent.VK_B, 48);
        mappings.put(KeyEvent.VK_N, 49);
        mappings.put(KeyEvent.VK_M, 50);
        mappings.put(KeyEvent.VK_COMMA, 51);
        mappings.put(KeyEvent.VK_PERIOD, 52);
        mappings.put(KeyEvent.VK_SLASH, 53);
        mappings.put(VK_RIGHT_SHIFT, 54);
        mappings.put(KeyEvent.VK_MULTIPLY, 55);
        mappings.put(VK_LEFT_ALT, 56);
        mappings.put(KeyEvent.VK_SPACE, 57);
        mappings.put(KeyEvent.VK_CAPS_LOCK, 58);
        mappings.put(KeyEvent.VK_F1, 59);
        mappings.put(KeyEvent.VK_F2, 60);
        mappings.put(KeyEvent.VK_F3, 61);
        mappings.put(KeyEvent.VK_F4, 62);
        mappings.put(KeyEvent.VK_F5, 63);
        mappings.put(KeyEvent.VK_F6, 64);
        mappings.put(KeyEvent.VK_F7, 65);
        mappings.put(KeyEvent.VK_F8, 66);
        mappings.put(KeyEvent.VK_F9, 67);
        mappings.put(KeyEvent.VK_F10, 68);
        mappings.put(KeyEvent.VK_NUM_LOCK, 69);
        mappings.put(KeyEvent.VK_SCROLL_LOCK, 70);
        mappings.put(KeyEvent.VK_NUMPAD7, 71);
        mappings.put(KeyEvent.VK_NUMPAD8, 72);
        mappings.put(KeyEvent.VK_NUMPAD9, 73);
        mappings.put(KeyEvent.VK_SUBTRACT, 74);
        mappings.put(KeyEvent.VK_NUMPAD4, 75);
        mappings.put(KeyEvent.VK_NUMPAD5, 76);
        mappings.put(KeyEvent.VK_NUMPAD6, 77);
        mappings.put(KeyEvent.VK_ADD, 78);
        mappings.put(KeyEvent.VK_NUMPAD1, 79);
        mappings.put(KeyEvent.VK_NUMPAD2, 80);
        mappings.put(KeyEvent.VK_NUMPAD3, 81);
        mappings.put(KeyEvent.VK_NUMPAD0, 82);
        mappings.put(KeyEvent.VK_DECIMAL, 83);
        mappings.put(KeyEvent.VK_F11, 87);
        mappings.put(KeyEvent.VK_F12, 88);
        mappings.put(KeyEvent.VK_F13, 100);
        mappings.put(KeyEvent.VK_F14, 101);
        mappings.put(KeyEvent.VK_F15, 102);
        mappings.put(KeyEvent.VK_KANA_LOCK, 112);
        mappings.put(KeyEvent.VK_CONVERT, 121);
        mappings.put(KeyEvent.VK_NONCONVERT, 123);
        mappings.put(KeyEvent.VK_DOLLAR, 125);
        mappings.put(KeyEvent.VK_CIRCUMFLEX, 144);
        mappings.put(KeyEvent.VK_AT, 145);
        mappings.put(KeyEvent.VK_COLON, 146);
        mappings.put(KeyEvent.VK_UNDERSCORE, 147);
        mappings.put(KeyEvent.VK_KANJI, 148);
        mappings.put(KeyEvent.VK_STOP, 149);
        mappings.put(VK_RIGHT_CONTROL, 157);
        mappings.put(KeyEvent.VK_DIVIDE, 181);
        mappings.put(VK_RIGHT_ALT, 184);
        mappings.put(KeyEvent.VK_PAUSE, 197);
        mappings.put(KeyEvent.VK_HOME, 199);
        mappings.put(KeyEvent.VK_UP, 200);
        mappings.put(KeyEvent.VK_PAGE_UP, 201);
        mappings.put(KeyEvent.VK_LEFT, 203);
        mappings.put(KeyEvent.VK_RIGHT, 205);
        mappings.put(KeyEvent.VK_END, 207);
        mappings.put(KeyEvent.VK_DOWN, 208);
        mappings.put(KeyEvent.VK_PAGE_DOWN, 209);
        mappings.put(KeyEvent.VK_INSERT, 210);
        mappings.put(KeyEvent.VK_DELETE, 211);
    }

// --Commented out by Inspection START (10/14/18 22:46):
//    public ComputerKey(int i) {
//        orig_key = i;
//        new_key = mappings.get(i);
//    }
// --Commented out by Inspection STOP (10/14/18 22:46)

    ComputerKey(KeyEvent e) {
        int orig_key = e.getKeyCode();
        if (e.getKeyLocation() != 2 && orig_key >= 16 && orig_key <= 18) orig_key += 300;
        new_key = mappings.getOrDefault(orig_key, 0);
    }

    @Override
    public int intValue() {
        return new_key;
    }

    @Override
    public long longValue() {
        return new_key;
    }

    @Override
    public float floatValue() {
        return (float)new_key;
    }

    @Override
    public double doubleValue() {
        return (double)new_key;
    }
}
