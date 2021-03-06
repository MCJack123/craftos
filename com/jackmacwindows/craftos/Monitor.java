import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.peripheral.monitor.IMonitorProvider;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class Monitor implements IMonitorProvider, ResizeListener {

    private final TerminalWindow window;
    private final Terminal terminal;
    private final PeriphemuAPI api;
    private final String side;
    private long lastBlink = 0;

    public Monitor(PeriphemuAPI p, String s) {
        api = p;
        side = s;
        window = new TerminalWindow(this, "CraftOS Terminal: Monitor ".concat(s));
        terminal = new Terminal(window.width, window.height);
        terminal.reset();
    }

    @Override
    public void finalize() {
        destroy();
    }

    @Override
    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public void setTextScale(int scale) {
        window.setCharScale(scale);
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
            //System.out.println("changed");
            if (terminal.getPalette() != window.p) window.setPalette(terminal.getPalette());
            char[] text, bg, fg, pixels;
            try {
                for (int y = 0; y < window.height; y++) {
                    text = terminal.getLine(y).toString().toCharArray();
                    bg = terminal.getBackgroundColourLine(y).toString().toCharArray();
                    fg = terminal.getTextColourLine(y).toString().toCharArray();
                    //System.out.println(y);
                    //System.out.println(bg);
                    //System.out.println(fg);
                    //System.out.println(text);
                    for (int x = 0; x < text.length && x < window.width; x++) {
                        try {
                            window.panel.screen[x][y] = text[x];
                            window.panel.colors[x][y] = (char) (((char) ("0123456789abcdef".indexOf(bg[x])) << 4) | (char) ("0123456789abcdef".indexOf(fg[x])));
                        } catch (NullPointerException n) {
                            //System.out.printf("Error printing: (%d, %d)\n", x, y);
                        }
                    }
                }
            } catch (NullPointerException e) {
                return;
            }
            for (int y = 0; y < window.height * TerminalWindow.fontHeight; y++) {
                pixels = terminal.getPixelLine(y).toString().toCharArray();
                for (int x = 0; x < pixels.length && x < window.width * TerminalWindow.fontWidth; x++) {
                    window.panel.pixels[x][y] = (char)((byte)pixels[x]);
                }
            }
            window.panel.isPixel = terminal.getGraphicsMode();
            //System.out.println("repainting");
            terminal.clearChanged();
        }
        if ((new Date()).getTime() - lastBlink >= 500 && terminal.getCursorBlink()) {
            changed = true;
            lastBlink = (new Date()).getTime();
            window.panel.blink = !window.panel.blink;
        } else if (!terminal.getCursorBlink()) window.panel.blink = false;
        if (changed) {
            window.panel.repaint();
            System.gc();
            Runtime.getRuntime().gc();
        }
    }

    @Override
    public void didResizeWindow(int width, int height) {
        terminal.resize(width, height);
        api.didResize(side);
    }

    @Override
    public void willClose() {
        api.remove(side);
    }
}
