import dan200.computercraft.shared.util.Palette;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

class TerminalWindow {

    static final int width = 51;
    static final int height = 19;
    static final int fontWidth = 6;
    static final int fontHeight = 9;
    static final int fontScale = 2;
    static final int charWidth = fontWidth * fontScale;
    static final int charHeight = fontHeight * fontScale;
    TestPane panel;
    //private int column = 0;
    //private int row = 0;
    private Color[] colors = new Color[16];
    public Palette p = Palette.DEFAULT;

    TerminalWindow() {
        for (int i = 0; i < 16; i++) {
            double[] c = p.getColour(i);
            colors[i] = new Color((float)c[0], (float)c[1], (float)c[2], 0.0f);
        }
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            }
            JFrame frame = new JFrame("CraftOS Terminal");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            panel = new TestPane(colors);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
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

    public class TestPane extends JPanel {

        private BufferedImage img;
        final char[][] screen = new char[TerminalWindow.width][TerminalWindow.height];
        // upper nybble is bg, lower nybble is fg
        final char[][] colors = new char[TerminalWindow.width][TerminalWindow.height];
        final char[][] pixels = new char[TerminalWindow.width*TerminalWindow.fontWidth][TerminalWindow.height*TerminalWindow.fontHeight];
        boolean isPixel = false;
        public static final long serialVersionUID = 26;
        Color[] palette;
        int blinkX = 0;
        int blinkY = 0;
        boolean blink = false;

        TestPane(Color[] p) {
            try {
                img = ImageIO.read(getClass().getResourceAsStream("craftos@2x.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Failed to read font");
            }
            palette = p;
            for (int x = 0; x < TerminalWindow.width; x++) {
                for (int y = 0; y < TerminalWindow.height; y++) {
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
            return img.getSubimage(((TerminalWindow.fontWidth + 2) * 2)*(c & 0x0F)+2, ((TerminalWindow.fontHeight + 2) * 2)*(c >> 4)+2, TerminalWindow.charWidth, TerminalWindow.charHeight);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(TerminalWindow.width*TerminalWindow.charWidth+(4 * TerminalWindow.fontScale), TerminalWindow.height*TerminalWindow.charHeight+(4 * TerminalWindow.fontScale));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //System.out.println("painting");
            Graphics2D g2d = (Graphics2D) g.create();
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
                for (int x = 0; x < TerminalWindow.width * TerminalWindow.fontWidth * fontScale; x+=fontScale) {
                    for (int y = 0; y < TerminalWindow.height * TerminalWindow.fontHeight * fontScale; y+=fontScale) {
                        char c = pixels[x/fontScale][y/fontScale];
                        g2d.setColor(palette[c]);
                        g2d.fillRect(x + (2 * TerminalWindow.fontScale), y + (2 * TerminalWindow.fontScale), TerminalWindow.fontScale, TerminalWindow.fontScale);
                        /*if (x == 0)
                            g2d.fillRect(0, y + (2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, TerminalWindow.fontScale);
                        if (y == 0)
                            g2d.fillRect(x + (2 * TerminalWindow.fontScale), 0, TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        if (x + fontScale == TerminalWindow.width * TerminalWindow.fontWidth * fontScale)
                            g2d.fillRect((x + fontScale) + (2 * TerminalWindow.fontScale), y + (2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, TerminalWindow.fontScale);
                        if (y + fontScale == TerminalWindow.height * TerminalWindow.fontHeight * fontScale)
                            g2d.fillRect(x + (2 * TerminalWindow.fontScale), (y + fontScale) + (2 * TerminalWindow.fontScale), TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        if (x == 0 && y == 0)
                            g2d.fillRect(0, 0, 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        if (x == 0 && y + fontScale == TerminalWindow.height * TerminalWindow.fontHeight * fontScale)
                            g2d.fillRect(0, (y + fontScale) + (2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        if (x + fontScale == TerminalWindow.width * TerminalWindow.fontWidth * fontScale && y == 0)
                            g2d.fillRect((x + fontScale) + (2 * TerminalWindow.fontScale), 0, 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        if (x + fontScale == TerminalWindow.width * TerminalWindow.fontWidth * fontScale && y + fontScale == TerminalWindow.height * fontScale * TerminalWindow.fontHeight)
                            g2d.fillRect((x + fontScale) + (2 * TerminalWindow.fontScale), (y + fontScale) + (2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);*/
                    }
                }
            } else {
                for (int x = 0; x < TerminalWindow.width; x++) {
                    for (int y = 0; y < TerminalWindow.height; y++) {
                        BufferedImage c = convert(screen[x][y]);
                        g2d.setColor(palette[colors[x][y] >> 4]);
                        g2d.setXORMode(Color.white);
                        g2d.fillRect(x * TerminalWindow.charWidth + (2 * TerminalWindow.fontScale), y * TerminalWindow.charHeight + (2 * TerminalWindow.fontScale), TerminalWindow.charWidth, TerminalWindow.charHeight);
                        if (x == 0)
                            g2d.fillRect(0, y * TerminalWindow.charHeight + (2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, TerminalWindow.charHeight);
                        if (y == 0)
                            g2d.fillRect(x * TerminalWindow.charWidth + (2 * TerminalWindow.fontScale), 0, TerminalWindow.charWidth, 2 * TerminalWindow.fontScale);
                        if (x + 1 == TerminalWindow.width)
                            g2d.fillRect((x + 1) * TerminalWindow.charWidth + (2 * TerminalWindow.fontScale), y * TerminalWindow.charHeight + (2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, TerminalWindow.charHeight);
                        if (y + 1 == TerminalWindow.height)
                            g2d.fillRect(x * TerminalWindow.charWidth + (2 * TerminalWindow.fontScale), (y + 1) * TerminalWindow.charHeight + (2 * TerminalWindow.fontScale), TerminalWindow.charWidth, 2 * TerminalWindow.fontScale);
                        if (x == 0 && y == 0)
                            g2d.fillRect(0, 0, 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        if (x == 0 && y + 1 == TerminalWindow.height)
                            g2d.fillRect(0, (y + 1) * TerminalWindow.charHeight + (2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        if (x + 1 == TerminalWindow.width && y == 0)
                            g2d.fillRect((x + 1) * TerminalWindow.charWidth + (2 * TerminalWindow.fontScale), 0, 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        if (x + 1 == TerminalWindow.width && y + 1 == TerminalWindow.height)
                            g2d.fillRect((x + 1) * TerminalWindow.charWidth + (2 * TerminalWindow.fontScale), (y + 1) * TerminalWindow.charHeight + (2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                        g2d.setXORMode(invertColor(palette[colors[x][y] & 0x0F], palette[colors[x][y] >> 4]));
                        g2d.setColor(palette[0]);
                        g2d.drawImage(c, x * TerminalWindow.charWidth + (2 * TerminalWindow.fontScale), y * TerminalWindow.charHeight + (2 * TerminalWindow.fontScale), this);
                        g2d.setXORMode(invertColor(palette[0], palette[colors[x][y] >> 4]));
                        g2d.setColor(Color.white);
                        if (blink) {
                            g2d.drawImage(convert('_'), blinkX * TerminalWindow.charWidth + (2 * TerminalWindow.fontScale), blinkY * TerminalWindow.charHeight + (2 * TerminalWindow.fontScale), this);
                        }
                        g2d.setXORMode(Color.white);
                    }
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