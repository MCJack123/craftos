package dan200.computercraft.core.apis.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.IAPIEnvironment;
import dan200.computercraft.core.apis.handles.EncodedInputHandle;
import dan200.computercraft.core.apis.handles.HTTPResponseHandle;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;

public class HTTPServer {
    private final IAPIEnvironment env;
    private final HashMap<Integer, HttpServer> serverList;

    public HTTPServer(IAPIEnvironment e) {
        env = e;
        serverList = new HashMap<>();
    }

    public void listen(int port) throws LuaException {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", httpExchange -> {
                try {
                    ILuaObject req = wrapInputStream(new EncodedInputHandle(httpExchange.getRequestBody()), httpExchange.getRequestURI().toString(), httpExchange.getRequestMethod(), httpExchange.getRequestHeaders());
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ILuaObject res = new HTTPResponseHandle(httpExchange, new ByteArrayOutputStream());
                    env.queueEvent("http_request", new Object[]{port, req, res});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();
            System.out.println("Listening on " + ((Integer) port).toString());
            serverList.put(port, server);
        } catch (Exception e) {
            throw new LuaException("Could not start server: " + e.getMessage());
        }
    }

    public void stop(int port) throws LuaException {
        try {
            serverList.remove(port).stop(0);
        } catch (Exception e) {
            throw new LuaException("Could not stop server: " + e.getMessage());
        }
    }

    private static ILuaObject wrapInputStream( final ILuaObject reader, final String url, final String _method, final Headers requestHeaders )
    {
        String[] oldMethods = reader.getMethodNames();
        final int methodOffset = oldMethods.length;

        final String[] newMethods = Arrays.copyOf( oldMethods, oldMethods.length + 3 );
        newMethods[ methodOffset + 0 ] = "getURL";
        newMethods[ methodOffset + 1 ] = "getRequestHeaders";
        newMethods[ methodOffset + 2 ] = "getMethod";

        return new ILuaObject()
        {
            @Nonnull
            @Override
            public String[] getMethodNames()
            {
                return newMethods;
            }

            @Override
            public Object[] callMethod(@Nonnull ILuaContext context, int method, @Nonnull Object[] args ) throws LuaException, InterruptedException
            {
                if( method < methodOffset )
                {
                    return reader.callMethod( context, method, args );
                }
                switch( method - methodOffset )
                {
                    case 0:
                    {
                        // getURL
                        return new Object[] { url };
                    }
                    case 1:
                    {
                        // getRequestHeaders
                        return new Object[] { requestHeaders };
                    }
                    case 2:
                        // getMethod
                        return new Object[] { _method };
                    default:
                    {
                        return null;
                    }
                }
            }
        };
    }
}
