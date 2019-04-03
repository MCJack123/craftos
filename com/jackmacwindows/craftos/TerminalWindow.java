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
    private static int openWindows = 0;
    static final int fontWidth = 6;
    static final int fontHeight = 9;
    static final int fontScale = 1;
    private int charScale = 2;
    int charWidth = fontWidth * fontScale * charScale;
    int charHeight = fontHeight * fontScale * charScale;
    TestPane panel;
    //private int column = 0;
    //private int row = 0;
    private Color[] colors = new Color[16];
    public Palette p = Palette.DEFAULT;
    private ResizeListener delegate;

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
            TerminalFrame frame = new TerminalFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            panel = new TestPane(colors);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            openWindows++;
        });
    }
/*
    public void print(String text) {
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                row++;
                column = 0;
            } else if (c == '\b' && column > 0) {
                column--;
            } else if (c == '\r') {
                column = 0;
            } else {
                panel.screen[column][row] = c;
                column++;
            }
            if (column >= width) {
                row++;
                column = 0;
            }
            if (row >= height) {
                for (int y = 1; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        panel.colors[x][y-1] = panel.colors[x][y];
                        panel.screen[x][y-1] = panel.screen[x][y];
                    }
                }
                for (int x = 0; x < width; x++) {
                    panel.colors[x][height-1] = 0xF0;
                    panel.screen[x][height-1] = '\0';
                }
                row--;
            }
        }
        //System.out.print(column);
        panel.repaint();
    }
*/
    void setPalette(Palette p) {
        for (int i = 0; i < 16; i++) colors[i] = new Color(Palette.encodeRGB8(p.getColour(15-i)));
        panel.palette = colors;
    }

    void setCharScale(int scale) {
        charScale = scale;
        charWidth = fontWidth * fontScale * charScale;
        charHeight = fontHeight * fontScale * charScale;
    }

    private void resize() {
        int newWidth = (panel.getWidth() - 4*fontScale) / charWidth;
        int newHeight = (panel.getHeight() - 4*fontScale) / charHeight;
        if (newWidth == this.width && newHeight == this.height) return;
        this.width = newWidth;
        this.height = newHeight;
        panel.didResizeWindow(this.width, this.height);
        delegate.didResizeWindow(this.width, this.height);
    }

    public class TerminalFrame extends JFrame {
        TerminalFrame(String title) {
            super(title);
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent evt) {
                    //System.out.println("Got resize event");
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

        private BufferedImage img;
        char[][] screen = new char[width][height];
        // upper nybble is bg, lower nybble is fg
        char[][] colors = new char[width][height];
        char[][] pixels = new char[width*TerminalWindow.fontWidth][height*TerminalWindow.fontHeight];
        boolean isPixel = false;
        public static final long serialVersionUID = 26;
        Color[] palette;
        int blinkX = 0;
        int blinkY = 0;
        boolean blink = false;

        TestPane(Color[] p) {
            try {
                img = ImageIO.read(getClass().getResourceAsStream("craftos.png"));
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

        BufferedImage convert(char c) {
            /*System.out.print(c);
            System.out.print(' ');
            System.out.print(8*(c >> 4));
            System.out.print(' ');
            System.out.println(11*(c & 0x0F));*/
            return img.getSubimage(((TerminalWindow.fontWidth + 2) * fontScale)*(c & 0x0F)+fontScale, ((TerminalWindow.fontHeight + 2) * fontScale)*(c >> 4)+fontScale, fontWidth * fontScale, fontHeight * fontScale);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width*charWidth+(4 * TerminalWindow.fontScale), height*charHeight+(4 * TerminalWindow.fontScale));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.println("painting");
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            /*
            List<BufferedImage> text = convert("This is a test");
            int x = (getWidth() - (8 * text.size())) / 2;
            int y = (getHeight() - 8) / 2;
            for (BufferedImage img : text) {
                g2d.drawImage(img, x, y, this);
                x += img.getWidth();
            }*/
            if (isPixel) {
                g2d.setXORMode(Color.white);
                g2d.setColor(palette[15]);
                g2d.fillRect(0, 0, (width+1)*fontWidth*fontScale, (height+1)*fontHeight*fontScale);
                g2d.setXORMode(palette[15]);
                for (int x = 0; x < width * TerminalWindow.fontWidth * fontScale; x+=fontScale) {
                    for (int y = 0; y < height * TerminalWindow.fontHeight * fontScale; y+=fontScale) {
                        char c = pixels[x/fontScale][y/fontScale];
                        g2d.setColor(palette[c]);
                        g2d.fillRect(x + (2 * TerminalWindow.fontScale * charScale), y + (2 * TerminalWindow.fontScale * charScale), TerminalWindow.fontScale, TerminalWindow.fontScale);
                        /*if (x == 0)
                            g2d.fillRect(0, y + (2 * TerminalWindow.fontScale * charScale), 2 * TerminalWindow.fontScale * charScale, TerminalWindow.fontScale);
                        if (y == 0)
                            g2d.fillRect(x + (2 * TerminalWindow.fontScale * charScale), 0, TerminalWindow.fontScale, 2 * TerminalWindow.fontScale * charScale);
                        if (x + fontScale == width * TerminalWindow.fontWidth * fontScale)
                            g2d.fillRect((x + fontScale) + (2 * TerminalWindow.fontScale * charScale), y + (2 * TerminalWindow.fontScale * charScale), 2 * TerminalWindow.fontScale * charScale, TerminalWindow.fontScale);
                        if (y + fontScale == height * TerminalWindow.fontHeight * fontScale)
                            g2d.fillRect(x + (2 * TerminalWindow.fontScale * charScale), (y + fontScale) + (2 * TerminalWindow.fontScale * charScale), TerminalWindow.fontScale, 2 * TerminalWindow.fontScale * charScale);
                        if (x == 0 && y == 0)
                            g2d.fillRect(0, 0, 2 * TerminalWindow.fontScale * charScale, 2 * TerminalWindow.fontScale * charScale);
                        if (x == 0 && y + fontScale == height * TerminalWindow.fontHeight * fontScale)
                            g2d.fillRect(0, (y + fontScale) + (2 * TerminalWindow.fontScale * charScale), 2 * TerminalWindow.fontScale * charScale, 2 * TerminalWindow.fontScale * charScale);
                        if (x + fontScale == width * TerminalWindow.fontWidth * fontScale && y == 0)
                            g2d.fillRect((x + fontScale) + (2 * TerminalWindow.fontScale * charScale), 0, 2 * TerminalWindow.fontScale * charScale, 2 * TerminalWindow.fontScale * charScale);
                        if (x + fontScale == width * TerminalWindow.fontWidth * fontScale && y + fontScale == height * fontScale * TerminalWindow.fontHeight)
                            g2d.fillRect((x + fontScale) + (2 * TerminalWindow.fontScale * charScale), (y + fontScale) + (2 * TerminalWindow.fontScale * charScale), 2 * TerminalWindow.fontScale * charScale, 2 * TerminalWindow.fontScale * charScale);*/
                    }
                }
            } else {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        BufferedImage c = convert(screen[x][y]);
                        g2d.setColor(palette[colors[x][y] >> 4]);
                        g2d.setXORMode(Color.white);
                        g2d.fillRect(x * charWidth + (2 * TerminalWindow.fontScale * charScale), y * charHeight + (2 * TerminalWindow.fontScale * charScale), charWidth, charHeight);
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
                        g2d.setXORMode(invertColor(palette[colors[x][y] & 0x0F], palette[colors[x][y] >> 4]));
                        g2d.setColor(palette[0]);
                        g2d.drawImage(c, x * charWidth + (2 * TerminalWindow.fontScale * charScale), y * charHeight + (2 * TerminalWindow.fontScale * charScale), charWidth, charHeight, this);
                        g2d.setXORMode(Color.white);
                    }
                }
                if (blink) {
                    g2d.setXORMode(invertColor(palette[0], palette[colors[blinkX][blinkY] >> 4]));
                    g2d.setColor(Color.white);
                    g2d.drawImage(convert('_'), blinkX * charWidth + (2 * TerminalWindow.fontScale * charScale), blinkY * charHeight + (2 * TerminalWindow.fontScale * charScale), charWidth, charHeight, this);
                    g2d.setXORMode(Color.white);
                }
            }
            g2d.dispose();
        }

        private Color invertColor(Color src, Color mod) {
            int r = (255-src.getRed()) ^ (mod.getRed());
            int g = (255-src.getGreen()) ^ (mod.getGreen());
            int b = (255-src.getBlue()) ^ (mod.getBlue());
            int a = 0;
            //int hex = (r << 16) & (g << 8) & b;
            return new Color(r, g, b, a);
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

         /*
         * Resizes an image using a Graphics2D object backed by a BufferedImage.
         * @param srcImg - source image to scale
         * @param w - desired width
         * @param h - desired height
         * @return - the new resized image
         *
        private BufferedImage getScaledImage(BufferedImage srcImg, int w, int h){
            BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
            Graphics2D g2 = resizedImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(srcImg, 0, 0, w, h, null);
            g2.dispose();
            return resizedImg;
        }*/
    }

}