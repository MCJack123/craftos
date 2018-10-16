import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.peripheral.monitor.IMonitorProvider;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class Monitor implements IMonitorProvider {

    private TerminalWindow window = new TerminalWindow();
    private Terminal terminal = new Terminal(TerminalWindow.width, TerminalWindow.height);
    private long lastBlink = 0;

    @Override
    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public void setTextScale(int scale) {
        // TODO: add proper text scaling
    }

    @Override
    public boolean isColour() {
        return true;
    }

    @Override
    public void destroy() {
        Window win = SwingUtilities.getWindowAncestor(window.panel);
        win.dispose();
    }

    @Override
    public void tick() {
        //System.out.println("Ticking");
        if (window.panel == null) return;
        boolean changed = false;
        if (window.panel.blinkX != terminal.getCursorX() || window.panel.blinkY != terminal.getCursorY()) {
            window.panel.blinkX = terminal.getCursorX();
            window.panel.blinkY = terminal.getCursorY();
            changed = true;
        }
        if (terminal.getChanged()) {
            changed = true;
            System.out.println("changed");
            char[] text, bg, fg;
            for (int y = 0; y < TerminalWindow.height; y++) {
                text = terminal.getLine(y).toString().toCharArray();
                bg = terminal.getBackgroundColourLine(y).toString().toCharArray();
                fg = terminal.getTextColourLine(y).toString().toCharArray();
                //System.out.println(y);
                //System.out.println(bg);
                //System.out.println(fg);
                //System.out.println(text);
                for (int x = 0; x < text.length && x < TerminalWindow.width; x++) {
                    try {
                        window.panel.screen[x][y] = text[x];
                        window.panel.colors[x][y] = (char) (((char) ("0123456789abcdef".indexOf(bg[x])) << 4) | (char) ("0123456789abcdef".indexOf(fg[x])));
                    } catch (NullPointerException n) {
                        //System.out.printf("Error printing: (%d, %d)\n", x, y);
                    }
                }
            }
            //System.out.println("repainting");
            terminal.clearChanged();
        }
        if ((new Date()).getTime() - lastBlink >= 500 && terminal.getCursorBlink()) {
            changed = true;
            lastBlink = (new Date()).getTime();
            window.panel.blink = !window.panel.blink;
        } else if (!terminal.getCursorBlink()) window.panel.blink = false;
        if (changed) {
            window.panel.repaint(); System.gc(); Runtime.getRuntime().gc();}
    }
}
