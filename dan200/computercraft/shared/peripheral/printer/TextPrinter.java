package dan200.computercraft.shared.peripheral.printer;

import dan200.computercraft.core.terminal.Terminal;

import java.io.*;

public class TextPrinter implements IStyledPrinter {

    private Terminal currentPage;
    private final OutputStreamWriter file;

    public TextPrinter(OutputStream f) {
        file = new OutputStreamWriter(f);
    }

    @Override
    public Terminal getCurrentPage() {
        return currentPage;
    }

    @Override
    public boolean startNewPage() {
        currentPage = new Terminal(85, 110);
        return true;
    }

    @Override
    public boolean endCurrentPage() {
        for (int y = 0; y < currentPage.getHeight(); y++) {
            try {
                file.write(currentPage.getLine(y).toString());
                file.write("  \n");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            file.flush();
            currentPage = null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public int getInkLevel() {
        return 64;
    }

    @Override
    public int getPaperLevel() {
        return 256;
    }

    @Override
    public void setPageTitle(String title) {
        try {
            file.write("# ");
            file.write(title);
            file.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
