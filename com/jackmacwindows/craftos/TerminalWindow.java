import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TerminalWindow {

    public static final int width = 51;
    public static final int height = 19;
    public TestPane panel;
    private int column = 0;
    private int row = 0;
    public static Color[] colors = {
        new Color(0xF0F0F0), new Color(0xF2B233), new Color(0xE57FD8), new Color(0x99B2F2),
        new Color(0xDEDE6C), new Color(0x7FCC19), new Color(0xF2B2CC), new Color(0x4C4C4C), 
        new Color(0x999999), new Color(0x4C99B2), new Color(0xB266E5), new Color(0x3366CC), 
        new Color(0x7F664C), new Color(0x57A64E), new Color(0xCC4C4C), new Color(0x191919)
    };
    public static TerminalWindow currentWindow;

    public TerminalWindow() {
        currentWindow = this;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("CraftOS Terminal");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                panel = new TestPane();
                frame.add(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

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

    public class TestPane extends JPanel {

        private BufferedImage img;
        public char[][] screen = new char[Terminal.width][Terminal.height];
        // upper nybble is bg, lower nybble is fg
        public char[][] colors = new char[Terminal.width][Terminal.height];
        public static final long serialVersionUID = 26;

        public TestPane() {
            try {
                img = ImageIO.read(new java.io.File("C:\\Users\\jackb_000\\Downloads\\craftos@2x.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            for (int x = 0; x < Terminal.width; x++) {
                for (int y = 0; y < Terminal.height; y++) {
                    colors[x][y] = 0xF0;
                }
            }
        }

        public List<BufferedImage> convert(String text) {

            List<BufferedImage> images = new ArrayList<>(text.length());

            for (char c : text.toCharArray()) {
                System.out.print(c);
                System.out.print(' ');
                System.out.print(8*(c >> 4));
                System.out.print(' ');
                System.out.println(11*(c & 0x0F));
                BufferedImage sub = img.getSubimage(8*(c & 0x0F)+1, 11*(c >> 4)+1, 6, 9);
                images.add(getScaledImage(sub, sub.getWidth()*2, sub.getHeight()*2));
            }

            return images;

        }

        public BufferedImage convert(char c) {
            /*System.out.print(c);
            System.out.print(' ');
            System.out.print(8*(c >> 4));
            System.out.print(' ');
            System.out.println(11*(c & 0x0F));*/
            BufferedImage sub = img.getSubimage(16*(c & 0x0F)+2, 22*(c >> 4)+2, 12, 18);
            return sub;

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(Terminal.width*12, Terminal.height*18);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            System.out.println("painting");
            Graphics2D g2d = (Graphics2D) g.create();
            /*
            List<BufferedImage> text = convert("This is a test");
            int x = (getWidth() - (8 * text.size())) / 2;
            int y = (getHeight() - 8) / 2;
            for (BufferedImage img : text) {
                g2d.drawImage(img, x, y, this);
                x += img.getWidth();
            }*/
            for (int x = 0; x < Terminal.width; x++) {
                for (int y = 0; y < Terminal.height; y++) {
                    BufferedImage c = convert(screen[x][y]);
                    g2d.setColor(Terminal.colors[colors[x][y] >> 4]);
                    g2d.fillRect(x*12, y*18, 12, 18);
                    g2d.setColor(Terminal.colors[colors[x][y] & 0x0F]);
                    g2d.drawImage(c, x*12, y*18, this);
                }
            }
            g2d.dispose();
        }

         /**
         * Resizes an image using a Graphics2D object backed by a BufferedImage.
         * @param srcImg - source image to scale
         * @param w - desired width
         * @param h - desired height
         * @return - the new resized image
         */
        private BufferedImage getScaledImage(BufferedImage srcImg, int w, int h){
            BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
            Graphics2D g2 = resizedImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(srcImg, 0, 0, w, h, null);
            g2.dispose();
            return resizedImg;
        }
    }

}