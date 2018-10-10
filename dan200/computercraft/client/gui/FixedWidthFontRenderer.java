/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.client.gui;

import dan200.computercraft.core.terminal.TextBuffer;
import dan200.computercraft.shared.util.Palette;
import com.jackmacwindows.craftos.TerminalWindow;

import java.util.Arrays;

public class FixedWidthFontRenderer
{
    public static int FONT_HEIGHT = 9;
    public static int FONT_WIDTH = 6;

    private TerminalWindow window;

    public FixedWidthFontRenderer( /*TextureManager textureManager*/ )
    {
        if (TerminalWindow.currentWindow != null) window = TerminalWindow.currentWindow;
        else System.out.println("Warning: Window not detected!");
    }

    private static void greyscaleify( double[] rgb )
    {
        Arrays.fill( rgb, ( rgb[0] + rgb[1] + rgb[2] ) / 3.0f );
    }

    private boolean isGreyScale( int colour )
    {
        return (colour == 0 || colour == 15 || colour == 7 || colour == 8);
    }

    public void drawStringBackgroundPart( int x, int y, TextBuffer backgroundColour, double leftMarginSize, double rightMarginSize, boolean greyScale, Palette p )
    {
        String c = "0123456789abcdef";
        for( int i = 0; i < backgroundColour.length(); i++ ) {
            int colour = c.indexOf( backgroundColour.charAt( i ) );
            if( colour < 0 || ( greyScale && !isGreyScale( colour ) ) )
            {
                colour = 15;
            }
            window.panel.colors[x/FONT_WIDTH+i][y/FONT_HEIGHT] &= (char)colour << 4;
        }
    }

    public void drawStringTextPart( int x, int y, TextBuffer s, TextBuffer textColour, boolean greyScale, Palette p )
    {
        String c = "0123456789abcdef";
        for( int i = 0; i < s.length(); i++ )
        {
            // Switch colour
            int colour = c.indexOf( textColour.charAt( i ) );
            if( colour < 0 || ( greyScale && !isGreyScale( colour ) ) )
            {
                colour = 0;
            }

            // Draw char
            int index = (int)s.charAt( i );
            if( index < 0 || index > 255 )
            {
                index = (int)'?';
            }
            window.panel.colors[x/FONT_WIDTH+i][y/FONT_HEIGHT] &= (char)colour;
            window.panel.text[x/FONT_WIDTH+i][y/FONT_HEIGHT] = (char)index;
        }
    }

    public void drawString( TextBuffer s, int x, int y, TextBuffer textColour, TextBuffer backgroundColour, double leftMarginSize, double rightMarginSize, boolean greyScale, Palette p )
    {
        // Draw background
        if( backgroundColour != null )
        {
            // Draw the quads
            drawStringBackgroundPart( x, y, backgroundColour, leftMarginSize, rightMarginSize, greyScale, p );
        }
    
        // Draw text
        if( s != null && textColour != null )
        {
            // Draw the quads
            drawStringTextPart( x, y, s, textColour, greyScale, p );
        }
    }

    public int getStringWidth(String s)
    {
        if(s == null)
        {
            return 0;
        }
        return s.length() * FONT_WIDTH;
    }
}
