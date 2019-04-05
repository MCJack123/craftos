package dan200.computercraft.core.apis.handles;

import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;

import java.io.Closeable;
import java.io.IOException;

abstract class HandleGeneric implements ILuaObject
{
    protected final Closeable m_closable;
    protected boolean m_open = true;

    public HandleGeneric( Closeable m_closable )
    {
        this.m_closable = m_closable;
    }

    protected void checkOpen() throws LuaException
    {
        if( !m_open ) throw new LuaException( "attempt to use a closed file" );
    }

    protected void close()
    {
        try
        {
            m_closable.close();
            m_open = false;
        }
        catch( IOException ignored )
        {
        }
    }
}
