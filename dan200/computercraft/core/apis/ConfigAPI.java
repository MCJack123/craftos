package dan200.computercraft.core.apis;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ConfigAPI implements ILuaAPI {
    @Override
    public String[] getNames() {
        return new String[] {"config"};
    }

    @Override
    public void startup() {

    }

    @Override
    public void advance(double _dt) {

    }

    @Override
    public void shutdown() {

    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[] {"get", "set", "list", "getType"};
    }

    private Map<Object, Object> toTable(Object[] list) {
        Map<Object, Object> table = new HashMap<>();
        int i = 0;
        for (Object obj : list) table.put(i++, obj);
        return table;
    }

    @Nullable
    @Override
    public Object[] callMethod(@Nonnull ILuaContext context, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException {
        switch (method) {
            case 0:
                // get
                return new Object[]{ComputerCraft.config.get((String)arguments[0])};
            case 1:
                // set
                ComputerCraft.config.set((String)arguments[0], arguments[1]);
                ComputerCraft.syncConfig();
                return new Object[0];
            case 2:
                // list
                return new Object[] {toTable(ComputerCraft.config.list())};
            case 3:
                // getType
                return new Object[] {ComputerCraft.config.getType((String)arguments[0])};
            default:
                throw new LuaException("Unknown command");
        }
    }
}
