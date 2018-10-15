import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

class TerminalWindow {

    static final int width = 51;
    static final int height = 19;
    private static final int fontWidth = 6;
    private static final int fontHeight = 9;
    private static final int fontScale = 2;
    static final int charWidth = fontWidth * fontScale;
    static final int charHeight = fontHeight * fontScale;
    TestPane panel;
    //private int column = 0;
    //private int row = 0;
    private static final Color[] colors = {
        new Color(0xF0F0F0), new Color(0xF2B233), new Color(0xE57FD8), new Color(0x99B2F2),
        new Color(0xDEDE6C), new Color(0x7FCC19), new Color(0xF2B2CC), new Color(0x4C4C4C), 
        new Color(0x999999), new Color(0x4C99B2), new Color(0xB266E5), new Color(0x3366CC), 
        new Color(0x7F664C), new Color(0x57A64E), new Color(0xCC4C4C), new Color(0x191919)
    };

    TerminalWindow() {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            }

            JFrame frame = new JFrame("CraftOS Terminal");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            panel = new TestPane();
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
    public class TestPane extends JPanel {

        private BufferedImage img;
        final char[][] screen = new char[TerminalWindow.width][TerminalWindow.height];
        // upper nybble is bg, lower nybble is fg
        final char[][] colors = new char[TerminalWindow.width][TerminalWindow.height];
        public static final long serialVersionUID = 26;
        int blinkX = 0;
        int blinkY = 0;
        boolean blink = false;

        TestPane() {
            try {
                img = ImageIO.read(getClass().getResourceAsStream("craftos@2x.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Failed to read font");
            }
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
            for (int x = 0; x < TerminalWindow.width; x++) {
                for (int y = 0; y < TerminalWindow.height; y++) {
                    BufferedImage c = convert(screen[x][y]);
                    g2d.setColor(TerminalWindow.colors[colors[x][y] >> 4]);
                    g2d.setXORMode(Color.white);
                    g2d.fillRect(x*TerminalWindow.charWidth+(2 * TerminalWindow.fontScale), y*TerminalWindow.charHeight+(2 * TerminalWindow.fontScale), TerminalWindow.charWidth, TerminalWindow.charHeight);
                    if (x == 0) g2d.fillRect(0, y*TerminalWindow.charHeight+(2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, TerminalWindow.charHeight);
                    if (y == 0) g2d.fillRect(x*TerminalWindow.charWidth+(2 * TerminalWindow.fontScale), 0, TerminalWindow.charWidth, 2 * TerminalWindow.fontScale);
                    if (x+1 == TerminalWindow.width)
                        g2d.fillRect((x+1)*TerminalWindow.charWidth+(2 * TerminalWindow.fontScale), y*TerminalWindow.charHeight+(2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, TerminalWindow.charHeight);
                    if (y+1 == TerminalWindow.height)
                        g2d.fillRect(x*TerminalWindow.charWidth+(2 * TerminalWindow.fontScale), (y+1)*TerminalWindow.charHeight+(2 * TerminalWindow.fontScale), TerminalWindow.charWidth, 2 * TerminalWindow.fontScale);
                    if (x == 0 && y == 0) g2d.fillRect(0, 0, 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                    if (x == 0 && y+1 == TerminalWindow.height) g2d.fillRect(0, (y+1)*TerminalWindow.charHeight+(2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                    if (x+1 == TerminalWindow.width && y == 0) g2d.fillRect((x+1)*TerminalWindow.charWidth+(2 * TerminalWindow.fontScale), 0, 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                    if (x+1 == TerminalWindow.width && y+1 == TerminalWindow.height) g2d.fillRect((x+1)*TerminalWindow.charWidth+(2 * TerminalWindow.fontScale), (y+1)*TerminalWindow.charHeight+(2 * TerminalWindow.fontScale), 2 * TerminalWindow.fontScale, 2 * TerminalWindow.fontScale);
                    g2d.setXORMode(invertColor(TerminalWindow.colors[colors[x][y] & 0x0F], TerminalWindow.colors[colors[x][y] >> 4]));
                    g2d.setColor(Color.white);
                    g2d.drawImage(c, x*TerminalWindow.charWidth+(2 * TerminalWindow.fontScale), y*TerminalWindow.charHeight+(2 * TerminalWindow.fontScale), this);
                    g2d.setXORMode(invertColor(TerminalWindow.colors[0], TerminalWindow.colors[colors[x][y] >> 4]));
                    g2d.setColor(Color.white);
                    if (blink) {
                        g2d.drawImage(convert('_'), blinkX*TerminalWindow.charWidth+(2 * TerminalWindow.fontScale), blinkY*TerminalWindow.charHeight+(2 * TerminalWindow.fontScale), this);
                    }
                    g2d.setXORMode(Color.white);
                }
            }
            g2d.dispose();
        }

        private Color invertColor(Color src, Color mod) {
            int r = (255-src.getRed()) ^ mod.getRed();
            int g = (255-src.getGreen()) ^ mod.getGreen();
            int b = (255-src.getBlue()) ^ mod.getBlue();
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