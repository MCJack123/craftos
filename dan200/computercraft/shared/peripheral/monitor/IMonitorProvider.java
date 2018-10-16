package dan200.computercraft.shared.peripheral.monitor;

import dan200.computercraft.core.terminal.Terminal;

public interface IMonitorProvider {
    Terminal getTerminal();
    void setTextScale(int scale);
    boolean isColour();
    void destroy();
    void tick();
}
