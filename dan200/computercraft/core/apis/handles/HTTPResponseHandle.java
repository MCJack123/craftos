package dan200.computercraft.core.apis.handles;

import com.sun.net.httpserver.HttpExchange;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static dan200.computercraft.core.apis.ArgumentHelper.getString;

public class HTTPResponseHandle extends HandleGeneric {
    private HttpExchange m_exchange;
    private ByteArrayOutputStream m_writer;
    private int m_off = 0;
    private int m_statusCode = 200;

    public HTTPResponseHandle(HttpExchange exchange, ByteArrayOutputStream os) {
        super(new BufferedWriter(new OutputStreamWriter(os)));
        m_exchange = exchange;
        m_writer = os;
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[] {
            "write",
            "writeLine",
            "flush",
            "close",
            "setStatusCode",
            "setResponseHeader"
        };
    }

    @Nullable
    @Override
    public Object[] callMethod(@Nonnull ILuaContext context, int method, @Nonnull Object[] args) throws LuaException {
        switch( method )
        {
            case 0:
            {
                // write
                checkOpen();
                String text;
                if( args.length > 0 && args[ 0 ] != null )
                {
                    text = args[ 0 ].toString();
                }
                else
                {
                    text = "";
                }
                m_writer.write( text.getBytes(StandardCharsets.UTF_8), m_off, text.length() );
                m_off += text.length();
                return null;
            }
            case 1:
            {
                // writeLine
                checkOpen();
                String text;
                if( args.length > 0 && args[ 0 ] != null )
                {
                    text = args[ 0 ].toString();
                }
                else
                {
                    text = "";
                }
                m_writer.write( text.getBytes(StandardCharsets.UTF_8), m_off, text.length() );
                m_writer.write('\n');
                m_off += text.length() + 1;
                return null;
            }
            case 2:
                // flush
                checkOpen();
                try
                {
                    m_writer.flush();
                    return null;
                }
                catch( IOException e )
                {
                    return null;
                }
            case 3:
                // close
                try {
                    m_exchange.sendResponseHeaders(m_statusCode, m_writer.size());
                    m_writer.writeTo(m_exchange.getResponseBody());
                    m_exchange.close();
                } catch (IOException e) {
                    throw new LuaException("Could not send data: " + e.getMessage());
                }
                close();
                return null;
            case 4:
                m_statusCode = dan200.computercraft.core.apis.ArgumentHelper.getInt(args, 0);
                return null;
            case 5:
            {
                // setResponseHeaders
                m_exchange.getResponseHeaders().add(getString(args, 0), getString(args, 1));
                return null;
            }
            default:
                return null;
        }
    }
}
