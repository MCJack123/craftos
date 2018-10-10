/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.server.proxy;

import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.peripheral.diskdrive.TileDiskDrive;
import dan200.computercraft.shared.peripheral.printer.TilePrinter;
import dan200.computercraft.shared.proxy.ComputerCraftProxyCommon;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import com.jackmacwindows.craftos.World;
import net.minecraftforge.common.DimensionManager;

import java.io.File;

public class ComputerCraftProxyServer extends ComputerCraftProxyCommon
{
    public ComputerCraftProxyServer()
    {
    }
    
    // IComputerCraftProxy implementation
    
    @Override
    public void init()
    {
        super.init();
    }

    @Override
    public boolean isClient()
    {
        return false;
    }

    @Override
    public boolean getGlobalCursorBlink()
    {
        return false;
    }

    @Override
    public long getRenderFrame()
    {
        return 0;
    }

    @Override
    public Object getFixedWidthFontRenderer()
    {
        return null;
    }

    @Override
    public File getWorldDir( World world )
    {
        return new File(System.getProperty("user.home").concat("/.craftos"));
    }
}
