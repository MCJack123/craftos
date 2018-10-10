import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main implements KeyListener {

    public Terminal term;

    public Main() {
        term = new Terminal();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                //synchronized (IsKeyPressed.class) {
                    switch (ke.getID()) {
                    case KeyEvent.KEY_TYPED:
                        keyTyped(ke);
                        break;
                    }
                    return false;
                //}
            }
        });
    }
    public static void main(String[] args) {
        new Main();
    }

    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == 0x1B) {System.exit(0); return;}
        String s = String.valueOf(c);
        if (c == '\b') s = "\b \b";
        System.out.print(s);
        term.print(s);
    }

    /** Handle the key-pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        //term.print(new String(e.getKeyChar()));
    }

    /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {
        
    }

}