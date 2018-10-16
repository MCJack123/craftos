package dan200.computercraft.shared.peripheral.speaker;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;

import javax.annotation.Nonnull;

public interface ISpeakerProvider {
    @Nonnull
    Object[] playNote( Object[] arguments, ILuaContext context ) throws LuaException;
    @Nonnull
    Object[] playSound( Object[] arguments, ILuaContext context, boolean isNote ) throws LuaException;
}
