package dan200.computercraft.shared.proxy;

import java.io.File;

public abstract class ComputerCraftProxyCommon implements IComputerCraftProxy {
    public void preInit() {}
    public void init() {}
    public abstract boolean isClient();

    public abstract boolean getGlobalCursorBlink();
    public abstract long getRenderFrame();
    public void deleteDisplayLists( int list, int range ) {}
    public abstract Object getFixedWidthFontRenderer();

    public abstract File getWorldDir();
}