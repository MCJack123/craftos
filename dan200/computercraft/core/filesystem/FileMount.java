/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.core.filesystem;

import dan200.computercraft.api.filesystem.IWritableMount;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.List;

public class FileMount implements IWritableMount
{
    private static final int MINIMUM_FILE_SIZE = 500;
    
    private class CountingOutputStream extends OutputStream
    {
        private final OutputStream m_innerStream;
        private long m_ignoredBytesLeft;
        
        CountingOutputStream(OutputStream innerStream, long bytesToIgnore)
        {
            m_innerStream = innerStream;
            m_ignoredBytesLeft = bytesToIgnore;
        }
        
        @Override
        public void close() throws IOException
        {
            m_innerStream.close();
        }
        
        @Override
        public void flush() throws IOException
        {
            m_innerStream.flush();
        }
        
        @Override
        public void write( @Nonnull byte[] b ) throws IOException
        {
            count( b.length );
            m_innerStream.write( b );
        }
        
        @Override
        public void write( @Nonnull byte[] b, int off, int len ) throws IOException
        {
            count( len );
            m_innerStream.write( b, off, len );
        }

        @Override
        public void write( int b ) throws IOException
        {
            count( 1 );
            m_innerStream.write( b );
        }

        private void count( long n ) throws IOException
        {
            m_ignoredBytesLeft -= n;
            if( m_ignoredBytesLeft < 0 )
            {
                long newBytes = -m_ignoredBytesLeft;
                m_ignoredBytesLeft = 0;
                
                long bytesLeft = m_capacity - m_usedSpace;
                if( newBytes > bytesLeft )
                {
                    throw new IOException( "Out of space" );
                }
                else
                {
                    m_usedSpace += newBytes;
                }
            }
        }
    }
    
    public final File m_rootPath;
    private final long m_capacity;
    private long m_usedSpace;
    
    public FileMount( File rootPath, long capacity )
    {
        m_rootPath = rootPath;
        m_capacity = capacity + MINIMUM_FILE_SIZE;
        if (capacity != 0) m_usedSpace = created() ? measureUsedSpace( m_rootPath ) : MINIMUM_FILE_SIZE;
        else m_usedSpace = 0;
    }

    // IMount implementation
    
    @Override
    public boolean exists( @Nonnull String path ) {
        if( !created() )
        {
            //System.out.println("Doesn't exist");
            return path.length() == 0;
        }
        else
        {
            //System.out.println("getRealPath");
            File file = getRealPath( path );
            //System.out.println("done getRealPath");
            return file.exists();
        }
    }
    
    @Override
    public boolean isDirectory( @Nonnull String path ) {
        if( !created() )
        {
            //System.out.println("Doesn't exist D");
            return path.length() == 0;
        }
        else
        {
            //System.out.println("getRealPath D");
            File file = getRealPath( path );
            //System.out.println("done getRealPath D");
            return file.exists() && file.isDirectory();
        }
    }
    
    @Override
    public void list( @Nonnull String path, @Nonnull List<String> contents ) throws IOException
    {
        if( !created() )
        {
            if( path.length() != 0 )
            {
                throw new IOException( "/" + path + ": Not a directory" );
            }
        }
        else
        {
            File file = getRealPath( path );
            if( file.exists() && file.isDirectory() )
            {
                //System.out.println("Listing");
                String[] paths = file.list();
                //System.out.println("Listed");
                //System.out.println(paths.length);
                for( String subPath : paths )
                {
                    if( new File( file, subPath ).exists() )
                    {
                        contents.add( subPath );
                    }
                }
            }
            else
            {
                throw new IOException( "/" + path + ": Not a directory" );
            }
        }    
    }

    @Override
    public long getSize( @Nonnull String path ) throws IOException
    {
        if( !created() )
        {
            if( path.length() == 0 )
            {
                return 0;
            }
        }
        else
        {
            File file = getRealPath( path );
            if( file.exists() )
            {
                if( file.isDirectory() )
                {
                    return 0;
                }
                else
                {
                    return file.length();
                }
            }
        }
        throw new IOException( "/" + path + ": No such file" );
    }
    
    @Nonnull
    @Override
    public InputStream openForRead( @Nonnull String path ) throws IOException
    {
        if( created() )
        {
            File file = getRealPath( path );
            if( file.exists() && !file.isDirectory() )
            {
                return new FileInputStream( file );
            }
        }
        throw new IOException( "/" + path +  ": No such file" );
    }
    
    // IWritableMount implementation
    
    @Override
    public void makeDirectory( @Nonnull String path ) throws IOException
    {
        create();
        File file = getRealPath( path );
        if( file.exists() )
        {
            if( !file.isDirectory() )
            {
                throw new IOException( "/" + path + ": File exists" );
            }
        }
        else
        {
            int dirsToCreate = 1;
            File parent = file.getParentFile();
            while( !parent.exists() )
            {
                ++dirsToCreate;
                parent = parent.getParentFile();
            }

            if( getRemainingSpace() < dirsToCreate * MINIMUM_FILE_SIZE )
            {
                throw new IOException( "/" + path + ": Out of space" );
            }
            
            boolean success = file.mkdirs();
            if( success )
            {
                m_usedSpace += dirsToCreate * MINIMUM_FILE_SIZE;
            }
            else
            {
                throw new IOException( "/" + path + ": Access denied" );
            }
        }
    }
    
    @Override
    public void delete( @Nonnull String path ) throws IOException
    {
        if( path.length() == 0 )
        {
            throw new IOException( "/" + path + ": Access denied" );
        }
        
        if( created() )
        {
            File file = getRealPath( path );
            if( file.exists() )
            {
                deleteRecursively( file );
            }
        }
    }
    
    private void deleteRecursively( File file ) throws IOException
    {
        // Empty directories first
        if( file.isDirectory() )
        {
            String[] children = file.list();
            for( String aChildren : children )
            {
                deleteRecursively( new File( file, aChildren ) );
            }
        }
        
        // Then delete
        long fileSize = file.isDirectory() ? 0 : file.length();
        boolean success = file.delete();
        if( success )
        {
            m_usedSpace -= Math.max( MINIMUM_FILE_SIZE, fileSize );
        }
        else
        {
            throw new IOException( "Access denied" );
        }
    }
    
    @Nonnull
    @Override
    public OutputStream openForWrite( @Nonnull String path ) throws IOException
    {
        create();
        File file = getRealPath( path );
        if( file.exists() && file.isDirectory() )
        {
            throw new IOException( "/" + path + ": Cannot write to directory" );
        }
        else
        {
            if( !file.exists() )
            {
                if( getRemainingSpace() < MINIMUM_FILE_SIZE )
                {
                    throw new IOException( "/" + path + ": Out of space" );
                }
                else
                {
                    m_usedSpace += MINIMUM_FILE_SIZE;
                }
            }
            else
            {
                m_usedSpace -= Math.max( file.length(), MINIMUM_FILE_SIZE );
                m_usedSpace += MINIMUM_FILE_SIZE;
            }
            return new CountingOutputStream( new FileOutputStream( file, false ), MINIMUM_FILE_SIZE );
        }
    }
    
    @Nonnull
    @Override
    public OutputStream openForAppend( @Nonnull String path ) throws IOException
    {
        if( created() )
        {
            File file = getRealPath( path );
            if( !file.exists() )
            {
                throw new IOException( "/" + path + ": No such file" );
            }
            else if( file.isDirectory() )
            {
                throw new IOException( "/" + path + ": Cannot write to directory" );
            }
            else
            {
                return new CountingOutputStream( new FileOutputStream( file, true ), Math.max( MINIMUM_FILE_SIZE - file.length(), 0 ) );
            }
        }
        else
        {
            throw new IOException( "/" + path + ": No such file" );
        }
    }
    
    @Override
    public long getRemainingSpace() {
        return Math.max( m_capacity - m_usedSpace, 0 );
    }
    
    private File getRealPath(String path)
    {
        return new File( m_rootPath, path );
    }
    
    private boolean created()
    {
        //System.out.println(m_rootPath.toString());
        return m_rootPath.exists();
    }
    
    private void create() throws IOException
    {
        if( !m_rootPath.exists() )
        {
            boolean success = m_rootPath.mkdirs();
            if( !success )
            {
                throw new IOException( "Access denied" );
            }
        }
    }
    
    private long measureUsedSpace( File file )
    {
        if( !file.exists() )
        {
            return 0;
        }
        if( file.isDirectory() )
        {
            long size = MINIMUM_FILE_SIZE;
            String[] contents = file.list();
            for( String content : contents )
            {
                size += measureUsedSpace( new File( file, content ) );
            }
            return size;
        }
        else
        {
            return Math.max( file.length(), MINIMUM_FILE_SIZE );
        }
    }
}
