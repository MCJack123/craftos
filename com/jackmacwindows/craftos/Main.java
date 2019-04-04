import dan200.computercraft.ComputerCraft;
import dan200.computercraft.core.computer.Computer;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;

import java.awt.event.*;
import java.io.File;
import java.util.Date;

public class Main implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener, ResizeListener {

    private final TerminalWindow term;
    private final Terminal comp_term;
    private final Computer computer;
    private final MountAPI mounter;
    private long lastTick;
    private long lastBlink;
    private boolean setListeners = false;
    private int lastDragX = -1;
    private int lastDragY = -1;
    private int lastDragButton = 1;
    private boolean setMounter = false;

    private Main() {
        term = new TerminalWindow(this, "CraftOS Terminal");
        CraftOSEnvironment env = new CraftOSEnvironment();
        ServerComputer server = new ServerComputer(0, "Computer", 0, ComputerFamily.Advanced, term.width, term.height);
        ComputerCraft.instance = new ComputerCraft();
        ComputerCraft.instance.preInit();
        ComputerCraft.instance.init(); // does nothing right now, but it might later?
        ComputerCraft.networkEventChannel.setServer(server);
        comp_term = server.getTerminal();
        computer = new Computer(env, comp_term, 0);
        computer.turnOn();
        mounter = new MountAPI(computer.getFileSystem());
        computer.addAPI(mounter);
        computer.addAPI(new PeriphemuAPI(computer, env));
        lastTick = (new Date()).getTime();
        computer.advance(1);
    }
    public static void main(String[] args) {
        (new File(CraftOSEnvironment.getWorldDir().toString() + "/computer/0")).mkdirs();
        Main l = new Main();
        while (l.term.panel == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {

            }
        }
        if (l.runLoop()) System.exit(0);
    }

    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        String s = String.valueOf(c);
        computer.queueEvent("key", new Object[]{(new ComputerKey(e)).intValue(), false});
        if (c >= 32 && c < 128) computer.queueEvent("char", new Object[]{s});
    }

    /** Handle the key-pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == 't' && e.isControlDown()) computer.queueEvent("terminate", new Object[]{});
        else computer.queueEvent("key", new Object[]{(new ComputerKey(e)).intValue(), true});
    }

    /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {
        computer.queueEvent("key_up", new Object[]{(new ComputerKey(e)).intValue()});
    }

    private boolean runLoop() {
        if (term == null) {System.err.println("NULL"); return false;}
        if (term.panel == null) {System.err.println("NULL2"); return false;}
        if (!setListeners) {
            term.panel.setFocusable(true);
            term.panel.requestFocusInWindow();
            term.panel.setFocusTraversalKeysEnabled(false);
            term.panel.addMouseListener(this);
            term.panel.addKeyListener(this);
            term.panel.addMouseWheelListener(this);
            term.panel.addMouseMotionListener(this);
            setListeners = true;
        }
        while (true) {
            if (!setMounter && computer.getFileSystem() != null) {
                mounter.setFileSystem(computer.getFileSystem());
                setMounter = true;
            }
            if ((new Date()).getTime() - lastTick >= 1000 / ComputerCraft.config.clockSpeed) {
                boolean changed = false;
                if (term.panel.blinkX != comp_term.getCursorX() || term.panel.blinkY != comp_term.getCursorY()) {
                    term.panel.blinkX = comp_term.getCursorX();
                    term.panel.blinkY = comp_term.getCursorY();
                    changed = true;
                }
                try {
                    synchronized (comp_term) {
                        if (comp_term.getChanged()) {
                            changed = true;
                            if (comp_term.getPalette() != term.p) term.setPalette(comp_term.getPalette());
                            char[] text, bg, fg, pixels;
                                for (int y = 0; y < term.height; y++) {
                                    text = comp_term.getLine(y).toString().toCharArray();
                                    bg = comp_term.getBackgroundColourLine(y).toString().toCharArray();
                                    fg = comp_term.getTextColourLine(y).toString().toCharArray();
                                    for (int x = 0; x < text.length && x < term.width; x++) {
                                        try {
                                            term.panel.screen[x][y] = text[x];
                                            term.panel.colors[x][y] = (char) (((char) ("0123456789abcdef".indexOf(bg[x])) << 4) | (char) ("0123456789abcdef".indexOf(fg[x])));
                                        } catch (NullPointerException ignored) {}
                                    }
                                }

                            for (int y = 0; y < term.height * TerminalWindow.fontHeight; y++) {
                                pixels = comp_term.getPixelLine(y).toString().toCharArray();
                                for (int x = 0; x < pixels.length && x < term.width * TerminalWindow.fontWidth; x++) {
                                    term.panel.pixels[x][y] = pixels[x];
                                }
                            }
                            term.panel.isPixel = comp_term.getGraphicsMode();
                            comp_term.clearChanged();
                        }
                    }
                } catch (NullPointerException e) {
                    continue;
                }
                if ((new Date()).getTime() - lastBlink >= 500 && comp_term.getCursorBlink()) {
                    changed = true;
                    lastBlink = (new Date()).getTime();
                    term.panel.blink = !term.panel.blink;
                } else if (!comp_term.getCursorBlink()) term.panel.blink = false;
                if (changed) {term.panel.repaint(); System.gc(); Runtime.getRuntime().gc();}
                if (computer.isOff()) {
                    return !computer.isCrashed();
                }
                lastTick = (new Date()).getTime();
                computer.advance(1);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        lastDragButton = convertButton(mouseEvent.getButton());
        computer.queueEvent("mouse_click", new Object[]{lastDragButton, convertX(mouseEvent.getX()), convertY(mouseEvent.getY())});
    }

    private int convertButton(int b) {
        if (b == 2) return 3;
        if (b == 3) return 2;
        return 1;
    }

    private int convertX(int x) {
        if (comp_term.getGraphicsMode()) {
            if (x < 2 * term.charScale) return 0;
            else if (x >= term.charWidth * term.width + 2 * term.charScale)
                return TerminalWindow.fontWidth * term.width - 1;
            return (x - (2 * term.charScale)) / term.charScale;
        } else {
            if (x < 2 * term.charScale) x = 2 * term.charScale;
            else if (x > term.charWidth * term.width + 2 * term.charScale)
                x = term.charWidth * term.width + 2 * term.charScale;
            return (x - 2 * term.charScale) / term.charWidth + 1;
        }
    }

    private int convertY(int x) {
        if (comp_term.getGraphicsMode()) {
            if (x < 2 * term.charScale) return 0;
            else if (x >= term.charHeight * term.height + 2 * term.charScale)
                return TerminalWindow.fontHeight * term.height - 1;
            return (x - (2 * term.charScale)) / term.charScale;
        } else {
            if (x < 2 * term.charScale) x = 2 * term.charScale;
            else if (x > term.charHeight * term.height + 2 * term.charScale)
                x = term.charHeight * term.height + 2 * term.charScale;
            return (x - 2 * term.charScale) / term.charHeight + 1;
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        computer.queueEvent("mouse_up", new Object[]{mouseEvent.getButton(), convertX(mouseEvent.getX()), convertY(mouseEvent.getY())});
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        computer.queueEvent("mouse_scroll", new Object[]{mouseWheelEvent.getWheelRotation() > 0 ? 1 : -1, convertX(mouseWheelEvent.getX()), convertY(mouseWheelEvent.getY())});
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (lastDragX != convertX(mouseEvent.getX()) || lastDragY != convertY(mouseEvent.getY())) {
            computer.queueEvent("mouse_drag", new Object[]{lastDragButton, convertX(mouseEvent.getX()), convertY(mouseEvent.getY())});
            lastDragX = convertX(mouseEvent.getX());
            lastDragY = convertY(mouseEvent.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }

    @Override
    public void didResizeWindow(int width, int height) {
        comp_term.resize(width, height);
        computer.queueEvent("term_resize", new Object[] {});
    }

    @Override
    public void willClose() {
        System.exit(0);
    }
}