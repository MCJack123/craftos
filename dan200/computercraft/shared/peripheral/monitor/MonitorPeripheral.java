/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.monitor;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.apis.TermAPI;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.util.Palette;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;

import static dan200.computercraft.core.apis.ArgumentHelper.*;

public class MonitorPeripheral implements IPeripheral
{
    private final IMonitorProvider m_monitor;

    public MonitorPeripheral( IMonitorProvider monitor )
    {
        m_monitor = monitor;
    }

    @Override
    public void tick() {m_monitor.tick();}

    // IPeripheral implementation

    @Nonnull
    @Override
    public String getType()
    {
        return "monitor";
    }

    @Nonnull
    @Override
    public String[] getMethodNames()
    {
        return new String[] {
            "write",
            "scroll",
            "setCursorPos",
            "setCursorBlink",
            "getCursorPos",
            "getSize",
            "clear",
            "clearLine",
            "setTextScale",
            "setTextColour",
            "setTextColor",
            "setBackgroundColour",
            "setBackgroundColor",
            "isColour",
            "isColor",
            "getTextColour",
            "getTextColor",
            "getBackgroundColour",
            "getBackgroundColor",
            "blit",
            "setPaletteColour",
            "setPaletteColor",
            "getPaletteColour",
            "getPaletteColor",
                "setGraphicsMode",
                "getGraphicsMode",
                "setPixel",
                "getPixel"
        };
    }

    @Override
    public Object[] callMethod( @Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull Object[] args) throws LuaException
    {
        switch( method )
        {
            case 0:
            {
                // write
                String text;
                if( args.length > 0 && args[0] != null ) {
                    text = args[0].toString();
                } else {
                    text = "";
                }
                Terminal terminal = m_monitor.getTerminal();
                terminal.write( text );
                terminal.setCursorPos( terminal.getCursorX() + text.length(), terminal.getCursorY() );
                return null;
            }
            case 1:
            {
                // scroll
                int value = getInt( args, 0 );
                Terminal terminal = m_monitor.getTerminal();
                terminal.scroll( value );
                return null;
            }
            case 2:
            {
                // setCursorPos
                int x = getInt( args, 0 ) - 1;
                int y = getInt( args, 1 ) - 1;
                Terminal terminal = m_monitor.getTerminal();
                terminal.setCursorPos( x, y );
                return null;
            }
            case 3:
            {
                // setCursorBlink
                boolean blink = getBoolean( args, 0 );
                Terminal terminal = m_monitor.getTerminal();
                terminal.setCursorBlink( blink );
                return null;
            }
            case 4:
            {
                // getCursorPos
                Terminal terminal = m_monitor.getTerminal();
                return new Object[] {
                    terminal.getCursorX() + 1,
                    terminal.getCursorY() + 1
                };
            }
            case 5:
            {
                // getSize
                Terminal terminal = m_monitor.getTerminal();
                return new Object[] {
                    terminal.getWidth(),
                    terminal.getHeight()
                };
            }
            case 6:
            {
                // clear
                Terminal terminal = m_monitor.getTerminal();
                terminal.clear();
                return null;
            }
            case 7:
            {
                // clearLine
                Terminal terminal = m_monitor.getTerminal();
                terminal.clearLine();
                return null;
            }
            case 8:
            {
                // setTextScale
                int scale = (int) (getReal( args, 0 ) * 2.0);
                if( scale < 1 || scale > 10 )
                {
                    throw new LuaException( "Expected number in range 0.5-5" );
                }
                m_monitor.setTextScale( scale );
                return null;
            }
            case 9:
            case 10:
            {
                // setTextColour/setTextColor
                int colour = TermAPI.parseColour( args );
                Terminal terminal = m_monitor.getTerminal();
                terminal.setTextColour( colour );
                return null;
            }
            case 11:
            case 12:
            {
                // setBackgroundColour/setBackgroundColor
                int colour = TermAPI.parseColour( args );
                Terminal terminal = m_monitor.getTerminal();
                terminal.setBackgroundColour( colour );
                return null;
            }
            case 13:
            case 14:
            {
                // isColour/isColor
                return new Object[] {
                    m_monitor.isColour()
                };
            }
            case 15:
            case 16:
            {
                // getTextColour/getTextColor
                Terminal terminal = m_monitor.getTerminal();
                return TermAPI.encodeColour( terminal.getTextColour() );
            }
            case 17:
            case 18:
            {
                // getBackgroundColour/getBackgroundColor
                Terminal terminal = m_monitor.getTerminal();
                return TermAPI.encodeColour( terminal.getBackgroundColour() );
            }
            case 19:
            {
                // blit
                String text = getString( args, 0 );
                String textColour = getString( args, 1 );
                String backgroundColour = getString( args, 2 );
                if( textColour.length() != text.length() || backgroundColour.length() != text.length() )
                {
                    throw new LuaException( "Arguments must be the same length" );
                }

                Terminal terminal = m_monitor.getTerminal();
                terminal.blit( text, textColour, backgroundColour );
                terminal.setCursorPos( terminal.getCursorX() + text.length(), terminal.getCursorY() );
                return null;
            }
            case 20:
            case 21:
            {
                // setPaletteColour/setPaletteColor
                Terminal terminal = m_monitor.getTerminal();

                int colour = 15 - TermAPI.parseColour( args );
                if( args.length == 2 )
                {
                    int hex = getInt( args, 1 );
                    double[] rgb = Palette.decodeRGB8( hex );
                    TermAPI.setColour( terminal, colour, rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] );
                }
                else
                {
                    double r = getReal( args, 1 );
                    double g = getReal( args, 2 );
                    double b = getReal( args, 3 );
                    TermAPI.setColour( terminal, colour, r, g, b );
                }
                return null;
            }
            case 22:
            case 23:
            {
                // getPaletteColour/getPaletteColor
                Terminal terminal = m_monitor.getTerminal();
                Palette palette = terminal.getPalette();

                int colour = 15 - TermAPI.parseColour( args );

                if( palette != null )
                {
                    return ArrayUtils.toObject( palette.getColour( colour ) );
                }
                return null;
            }
            case 24:
            {
                // setGraphicsMode
                Terminal m_terminal = m_monitor.getTerminal();
                m_terminal.clear();
                m_terminal.setGraphicsMode(getBoolean(args, 0));
                m_terminal.setCursorPos(1, 1);
                return null;
            }
            case 25:
            {
                // getGraphicsMode

                return new Object[] { m_monitor.getTerminal().getGraphicsMode() };
            }
            case 26:
            {
                // setPixel
                Terminal m_terminal = m_monitor.getTerminal();
                int colour = getInt( args, 2 );
                if( colour <= 0 )
                {
                    throw new LuaException( "Colour out of range" );
                }
                colour = getHighestBit( colour ) - 1;
                if( colour < 0 || colour > 15 )
                {
                    throw new LuaException( "Colour out of range" );
                }
                int x = getInt(args, 0);
                int y = getInt(args, 1);
                if (x >= m_terminal.m_width * 6 || y >= m_terminal.m_height * 9 || x < 0 || y < 0)
                    throw new LuaException("Position " + x + ", " + y + " out of bounds");
                m_terminal.setPixel(x, y, (char)colour);
                return null;
            }
            case 27:
            {
                // getPixel
                return encodeColour(m_monitor.getTerminal().getPixel(getInt(args, 0), getInt(args, 1)));
            }
        }
        return null;
    }

    private static int getHighestBit( int group )
    {
        int bit = 0;
        while( group > 0 )
        {
            group >>= 1;
            bit++;
        }
        return bit;
    }

    private static Object[] encodeColour(int colour) {
        return new Object[] {
                1 << colour
        };
    }

    /*@Override
    public void attach( @Nonnull IComputerAccess computer )
    {
        m_monitor.addComputer( computer );
    }

    @Override
    public void detach( @Nonnull IComputerAccess computer )
    {
        m_monitor.removeComputer( computer );
    }*/

    @Override
    public void willDetach() {
        m_monitor.destroy();
    }

    @Override
    public boolean equals( IPeripheral other )
    {
        if(other instanceof MonitorPeripheral)
        {
            MonitorPeripheral otherMonitor = (MonitorPeripheral)other;
            return otherMonitor.m_monitor == this.m_monitor;
        }
        return false;
    }
}
