/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.proxy;

import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.network.ComputerCraftPacket;
import dan200.computercraft.shared.peripheral.diskdrive.TileDiskDrive;
import dan200.computercraft.shared.peripheral.printer.TilePrinter;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import com.jackmacwindows.craftos.World;

import javax.annotation.Nonnull;
import java.io.File;

public interface IComputerCraftProxy
{
    void preInit();
    void init();
    boolean isClient();

    boolean getGlobalCursorBlink();
    long getRenderFrame();
    void deleteDisplayLists( int list, int range );
    Object getFixedWidthFontRenderer();

    File getWorldDir( World world );
}
