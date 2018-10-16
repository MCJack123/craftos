import dan200.computercraft.ComputerCraft;
import dan200.computercraft.core.computer.Computer;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;

import java.awt.event.*;
import java.io.File;
import java.util.Date;

public class Main implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {

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
        term = new TerminalWindow();
        CraftOSEnvironment env = new CraftOSEnvironment();
        ServerComputer server = new ServerComputer(0, "Computer", 0, ComputerFamily.Advanced, TerminalWindow.width, TerminalWindow.height);
        ComputerCraft.instance = new ComputerCraft();
        ComputerCraft.instance.preInit();
        ComputerCraft.instance.init(); // does nothing right now, but it might later?
        //ComputerCraft.networkEventChannel.registerClient(client);
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
        if (c == 0x1B) {System.exit(0); return;}
        String s = String.valueOf(c);
        //if (c == '\b') s = "\b \b";
        //System.out.print(s);
        //term.print(s);
        computer.queueEvent("key", new Object[]{(new ComputerKey(e)).intValue(), false});
        if (c >= 32 && c < 128) computer.queueEvent("char", new Object[]{s});
    }

    /** Handle the key-pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == 't' && e.isControlDown()) computer.queueEvent("terminate", new Object[]{});
        //term.print(new String(e.getKeyChar()));
        else computer.queueEvent("key", new Object[]{(new ComputerKey(e)).intValue(), true});
    }

    /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {
        //term.print(new String(e.getKeyChar()));
        computer.queueEvent("key_up", new Object[]{(new ComputerKey(e)).intValue()});
    }

    private boolean runLoop() {
        if (term == null) System.err.println("NULL");
        if (term.panel == null) System.err.println("NULL2");
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
                System.out.println("Set mounter");
            }
            if ((new Date()).getTime() - lastTick >= 1000 / ComputerCraft.config.clockSpeed) {
                lastTick = (new Date()).getTime();
                computer.advance(1);
                boolean changed = false;
                if (term.panel.blinkX != comp_term.getCursorX() || term.panel.blinkY != comp_term.getCursorY()) {
                    term.panel.blinkX = comp_term.getCursorX();
                    term.panel.blinkY = comp_term.getCursorY();
                    changed = true;
                }
                if (comp_term.getChanged()) {
                    changed = true;
                    //System.out.println("changed");
                    char[] text, bg, fg;
                    for (int y = 0; y < TerminalWindow.height; y++) {
                        text = comp_term.getLine(y).toString().toCharArray();
                        bg = comp_term.getBackgroundColourLine(y).toString().toCharArray();
                        fg = comp_term.getTextColourLine(y).toString().toCharArray();
                        //System.out.println(y);
                        //System.out.println(bg);
                        //System.out.println(fg);
                        //System.out.println(text);
                        for (int x = 0; x < text.length && x < TerminalWindow.width; x++) {
                            try {
                                term.panel.screen[x][y] = text[x];
                                term.panel.colors[x][y] = (char) (((char) ("0123456789abcdef".indexOf(bg[x])) << 4) | (char) ("0123456789abcdef".indexOf(fg[x])));
                            } catch (NullPointerException n) {
                                //System.out.printf("Error printing: (%d, %d)\n", x, y);
                            }
                        }
                    }
                    //System.out.println("repainting");
                    comp_term.clearChanged();
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
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        //System.out.println(mouseEvent.paramString());
        lastDragButton = convertButton(mouseEvent.getButton());
        computer.queueEvent("mouse_click", new Object[]{lastDragButton, mouseEvent.getX() / TerminalWindow.charWidth + 1, mouseEvent.getY() / TerminalWindow.charHeight + 1});
    }

    private int convertButton(int b) {
        if (b == 2) return 3;
        if (b == 3) return 2;
        return 1;
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        computer.queueEvent("mouse_up", new Object[]{mouseEvent.getButton(), mouseEvent.getX() / TerminalWindow.charWidth + 1, mouseEvent.getY() / TerminalWindow.charHeight + 1});
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        computer.queueEvent("mouse_scroll", new Object[]{mouseWheelEvent.getWheelRotation() > 0 ? 1 : -1, mouseWheelEvent.getX() / TerminalWindow.charWidth + 1, mouseWheelEvent.getY() / TerminalWindow.charHeight + 1});
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.paramString());
        if (lastDragX != mouseEvent.getX() / TerminalWindow.charWidth + 1 || lastDragY != mouseEvent.getY() / TerminalWindow.charHeight + 1) {
            computer.queueEvent("mouse_drag", new Object[]{lastDragButton, mouseEvent.getX() / TerminalWindow.charWidth + 1, mouseEvent.getY() / TerminalWindow.charHeight + 1});
            lastDragX = mouseEvent.getX() / TerminalWindow.charWidth + 1;
            lastDragY = mouseEvent.getY() / TerminalWindow.charHeight + 1;
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }
}