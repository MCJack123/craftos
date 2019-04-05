import dan200.computercraft.shared.util.Palette;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

class TerminalWindow {

    int width = 51;
    int height = 19;
    static final int fontWidth = 6;
    static final int fontHeight = 9;
    private static final int fontScale = 1;
    int charScale = 2;
    int charWidth = fontWidth * fontScale * charScale;
    int charHeight = fontHeight * fontScale * charScale;
    TestPane panel;
    private TerminalFrame frame;
    private final Color[] colors = new Color[16];
    public final Palette p = Palette.DEFAULT;
    private final ResizeListener delegate;

    TerminalWindow(ResizeListener d, String title) {
        for (int i = 0; i < 16; i++) {
            double[] c = p.getColour(i);
            colors[i] = new Color((float)c[0], (float)c[1], (float)c[2], 0.0f);
        }
        delegate = d;
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            }
            frame = new TerminalFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            panel = new TestPane(colors);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            try {
                frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("Ccblink.gif")));
            } catch (IOException ignored) {}
        });
    }

    void setPalette(Palette p) {
        for (int i = 0; i < 16; i++) colors[i] = new Color(Palette.encodeRGB8(p.getColour(15-i)));
        panel.palette = colors;
    }

    void setCharScale(int scale) {
        charScale = scale;
        charWidth = fontWidth * fontScale * charScale;
        charHeight = fontHeight * fontScale * charScale;
        frame.pack();
    }

    private void resize() {
        int newWidth = (panel.getWidth() - 4*fontScale*charScale) / charWidth;
        int newHeight = (panel.getHeight() - 4*fontScale*charScale) / charHeight;
        if (newWidth == this.width && newHeight == this.height) return;
        this.width = newWidth;
        this.height = newHeight;
        panel.didResizeWindow(this.width, this.height);
        delegate.didResizeWindow(this.width, this.height);
    }

    class TerminalFrame extends JFrame {
        TerminalFrame(String title) {
            super(title);
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent evt) {
                    TerminalWindow.this.resize();
                }
            });
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    delegate.willClose();
                }
            });
        }
    }

    public class TestPane extends JPanel implements ResizeListener {

        private boolean[][][] characters = new boolean[256][fontWidth][fontHeight];
        char[][] screen = new char[width][height];
        // upper nybble is bg, lower nybble is fg
        char[][] colors = new char[width][height];
        char[][] pixels = new char[width*fontWidth][height*fontHeight];
        boolean isPixel = false;
        public static final long serialVersionUID = 26;
        Color[] palette;
        int blinkX = 0;
        int blinkY = 0;
        boolean blink = false;

        TestPane(Color[] p) {
            try {
                BufferedImage img = toCompatibleImage(ImageIO.read(getClass().getResourceAsStream("craftos.png")));
                for (char i = 0; i < 255; i++) {
                    BufferedImage ch = convert(img, i);
                    for (int x = 0; x < fontWidth; x++) {
                        for (int y = 0; y < fontHeight; y++) {
                            characters[i][x][y] = (new Color(ch.getRGB(x, y), true)).getAlpha() > 0x7F;
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Failed to read font");
            }
            palette = p;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    colors[x][y] = 0xF0;
                }
            }
        }

        BufferedImage convert(BufferedImage img, char c) {
            return img.getSubimage(((TerminalWindow.fontWidth + 2) * fontScale)*(c & 0x0F)+fontScale, ((TerminalWindow.fontHeight + 2) * fontScale)*(c >> 4)+fontScale, fontWidth * fontScale, fontHeight * fontScale);
        }

        void drawChar(Graphics2D g2d, char c, int x, int y, Color fg, Color bg) {
            for (int px = 0; px < charWidth; px+=charScale) {
                for (int py = 0; py < charHeight; py+=charScale) {
                    if (!characters[c][px/charScale][py/charScale] && bg == null) continue;
                    g2d.setColor(characters[c][px/charScale][py/charScale] ? fg : bg);
                    g2d.fillRect(x * charWidth + (2 * charScale * fontScale) + px, y * charHeight + (2 * charScale * fontScale) + py, charScale, charScale);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width*charWidth+(4 * charScale), height*charHeight+(4 * charScale));
        }

        private BufferedImage toCompatibleImage(BufferedImage image) {
            // obtain the current system graphical settings
            GraphicsConfiguration gfxConfig = GraphicsEnvironment.
                    getLocalGraphicsEnvironment().getDefaultScreenDevice().
                    getDefaultConfiguration();

            /*
             * if image is already compatible and optimized for current system
             * settings, simply return it
             */
            if (image.getColorModel().equals(gfxConfig.getColorModel()))
                return image;

            // image is not optimized, so create a new image that is
            BufferedImage newImage = gfxConfig.createCompatibleImage(
                    image.getWidth(), image.getHeight(), image.getTransparency());

            // get the graphics context of the new image to draw the old image on
            Graphics2D g2d = newImage.createGraphics();

            // actually draw the image and dispose of context no longer needed
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            // return the new optimized image
            return newImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            if (isPixel) {
                g2d.setXORMode(Color.white);
                g2d.setColor(palette[15]);
                g2d.fillRect(0, 0, (width+1)*charWidth, (height+1)*charHeight);
                g2d.setXORMode(palette[15]);
                for (int x = 0; x < width * charWidth; x+=fontScale*charScale) {
                    for (int y = 0; y < height * charHeight; y += fontScale * charScale) {
                        char c = pixels[x / fontScale / charScale][y / fontScale / charScale];
                        g2d.setColor(palette[c]);
                        g2d.fillRect(x + (2 * TerminalWindow.fontScale * charScale), y + (2 * TerminalWindow.fontScale * charScale), TerminalWindow.fontScale * charScale, TerminalWindow.fontScale * charScale);
                    }
                }
            } else {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        g2d.setColor(palette[colors[x][y] >> 4]);
                        g2d.setXORMode(Color.white);
                        if (x == 0)
                            g2d.fillRect(0, y * charHeight + (2 * TerminalWindow.fontScale * charScale), 2 * TerminalWindow.fontScale * charScale, charHeight);
                        if (y == 0)
                            g2d.fillRect(x * charWidth + (2 * TerminalWindow.fontScale * charScale), 0, charWidth, 2 * TerminalWindow.fontScale * charScale);
                        if (x + 1 == width)
                            g2d.fillRect((x + 1) * charWidth + (2 * TerminalWindow.fontScale * charScale), y * charHeight + (2 * TerminalWindow.fontScale * charScale), 2 * TerminalWindow.fontScale * charScale, charHeight);
                        if (y + 1 == height)
                            g2d.fillRect(x * charWidth + (2 * TerminalWindow.fontScale * charScale), (y + 1) * charHeight + (2 * TerminalWindow.fontScale * charScale), charWidth, 2 * TerminalWindow.fontScale * charScale);
                        if (x == 0 && y == 0)
                            g2d.fillRect(0, 0, 2 * TerminalWindow.fontScale * charScale, 2 * TerminalWindow.fontScale * charScale);
                        if (x == 0 && y + 1 == height)
                            g2d.fillRect(0, (y + 1) * charHeight + (2 * TerminalWindow.fontScale * charScale), 2 * TerminalWindow.fontScale * charScale, 2 * TerminalWindow.fontScale * charScale);
                        if (x + 1 == width && y == 0)
                            g2d.fillRect((x + 1) * charWidth + (2 * TerminalWindow.fontScale * charScale), 0, 2 * TerminalWindow.fontScale * charScale, 2 * TerminalWindow.fontScale * charScale);
                        if (x + 1 == width && y + 1 == height)
                            g2d.fillRect((x + 1) * charWidth + (2 * TerminalWindow.fontScale * charScale), (y + 1) * charHeight + (2 * TerminalWindow.fontScale * charScale), 2 * TerminalWindow.fontScale * charScale, 2 * TerminalWindow.fontScale * charScale);
                        drawChar(g2d, screen[x][y], x, y, palette[colors[x][y] & 0x0F], palette[colors[x][y] >> 4]);
                    }
                }
                if (blink) drawChar(g2d, '_', blinkX, blinkY, Color.black, null);
            }
            g2d.dispose();
        }

        @Override
        public void didResizeWindow(int width, int height) {
            char[][] newScreen = new char[width][height];
            char[][] newColors = new char[width][height];
            char[][] newPixels = new char[width*fontWidth][height*fontHeight];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    try {
                        newScreen[x][y] = screen[x][y];
                        newColors[x][y] = colors[x][y];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        newScreen[x][y] = 0;
                        newColors[x][y] = 0xF0;
                    }
                }
            }
            for (int x = 0; x < width * fontWidth; x++) {
                for (int y = 0; y < height * fontHeight; y++) {
                    try {
                        newPixels[x][y] = pixels[x][y];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        newPixels[x][y] = 0;
                    }
                }
            }
            screen = newScreen;
            colors = newColors;
            pixels = newPixels;
        }

        @Override
        public void willClose() {

        }
    }

}