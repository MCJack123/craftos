/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2016. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.speaker;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nonnull;

public class SpeakerPeripheral implements IPeripheral {
    private ISpeakerProvider m_speaker;
    private long m_clock;
    private long m_lastPlayTime;
    private int m_notesThisTick;

    public SpeakerPeripheral()
    {
        m_clock = 0;
        m_lastPlayTime = 0;
        m_notesThisTick = 0;
    }

    public SpeakerPeripheral(ISpeakerProvider speaker)
    {
        this();
        m_speaker = speaker;
    }

    public synchronized void update()
    {
        m_clock++;
        m_notesThisTick = 0;
    }

    /*public World getWorld()
    {
        return m_speaker.getWorld();
    }

    public BlockPos getPos()
    {
        return m_speaker.getPos();
    }

    /* IPeripheral implementation */

    @Override
    public boolean equals( IPeripheral other )
    {
        return other instanceof SpeakerPeripheral;
    }

    @Nonnull
    @Override
    public String getType()
    {
        return "speaker";
    }

    @Nonnull
    @Override
    public String[] getMethodNames()
    {
        return new String[] {
                "playSound", // Plays sound at resourceLocator
                "playNote" // Plays note
        };
    }

    @Override
    public Object[] callMethod( @Nonnull IComputerAccess computerAccess, @Nonnull ILuaContext context, int methodIndex, @Nonnull Object[] args ) throws LuaException
    {
        switch( methodIndex )
        {
            // playSound
            case 0:
            {
                return m_speaker.playSound(args, context, false);
            }

            // playNote
            case 1:
            {
                return m_speaker.playNote(args, context);
            }

            default:
            {
                throw new LuaException("Method index out of range!");
            }

        }
    }
}

