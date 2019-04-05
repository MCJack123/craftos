package dan200.computercraft.shared.util;

import dan200.computercraft.ComputerCraft;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IDAssigner
{
    private IDAssigner()
    {
    }
    
    public static int getNextIDFromDirectory( File dir )
    {
        return getNextID( dir, true );
    }
    
    public static int getNextIDFromFile( File file )
    {
        return getNextID( file, false );
    }
    
    private static int getNextID( File location, boolean directory )
    {
        // Determine where to locate ID file
        File lastidFile;
        if( directory )
        {
            location.mkdirs();
            lastidFile = new File( location, "lastid.txt" );
        }
        else
        {
            location.getParentFile().mkdirs();
            lastidFile = location;
        }
        
        // Try to determine the id
        int id = 0;
        if( !lastidFile.exists() )
        {
            // If an ID file doesn't exist, determine it from the file structure
            if( directory && location.exists() && location.isDirectory() )
            {
                String[] contents = location.list();
                for( String content : contents )
                {
                    try
                    {
                        int number = Integer.parseInt( content );
                        id = Math.max( number + 1, id );
                    }
                    catch( NumberFormatException e )
                    {
                        System.out.print( "Unexpected file '" + content + "' in '" + location.getAbsolutePath() + "'" );
                    }
                }
            }
        }
        else
        {
            // If an ID file does exist, parse the file to get the ID string
            String idString;
            try
            {
                FileInputStream in = new FileInputStream( lastidFile );
                InputStreamReader isr;
                isr = new InputStreamReader( in, StandardCharsets.UTF_8);
                try( BufferedReader br = new BufferedReader( isr ) )
                {
                    idString = br.readLine();
                }
            }
            catch( IOException e )
            {
                System.out.print( "Cannot open ID file '" + lastidFile + "'" );
                return 0;
            }

            try
            {
                id = Integer.parseInt( idString ) + 1;
            }
            catch( NumberFormatException e )
            {
                System.out.print( "Cannot parse ID file '" + lastidFile + "', perhaps it is corrupt?" );
                return 0;
            }
        }
        
        // Write the lastID file out with the new value
        try
        {
            BufferedWriter out = new BufferedWriter( new FileWriter( lastidFile, false ) );
            out.write( Integer.toString( id ) );
            out.newLine();
            out.close();
        }
        catch( IOException e )
        {
            System.out.print( "An error occured while trying to create the computer folder. Please check you have relevant permissions." );
        }
        
        return id;
    }
}
