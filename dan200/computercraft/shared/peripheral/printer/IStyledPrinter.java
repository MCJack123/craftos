package dan200.computercraft.shared.peripheral.printer;

import dan200.computercraft.core.terminal.Terminal;

interface IStyledPrinter {
    Terminal getCurrentPage();
    boolean startNewPage();
    boolean endCurrentPage();
    int getInkLevel(); // 0-64
    int getPaperLevel(); // 0-384
    void setPageTitle(String title);
}
