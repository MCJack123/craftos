/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.modem;

import com.google.common.base.Preconditions;
import dan200.computercraft.api.network.IPacketNetwork;
import dan200.computercraft.api.network.IPacketReceiver;
import dan200.computercraft.api.network.IPacketSender;
import dan200.computercraft.api.network.Packet;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class WirelessNetwork implements IPacketNetwork
{
    private static WirelessNetwork s_universalNetwork = null;

    public static WirelessNetwork getUniversal()
    {
        if( s_universalNetwork == null )
        {
            s_universalNetwork = new WirelessNetwork();
        }
        return s_universalNetwork;
    }

    public static void resetNetworks()
    {
        s_universalNetwork = null;
    }

    private final Set<IPacketReceiver> m_receivers;

    private WirelessNetwork()
    {
        m_receivers = new HashSet<>();
    }

    @Override
    public synchronized void addReceiver( @Nonnull IPacketReceiver receiver )
    {
        Preconditions.checkNotNull( receiver, "device cannot be null" );
        m_receivers.add( receiver );
    }

    @Override
    public synchronized void removeReceiver( @Nonnull IPacketReceiver receiver )
    {
        Preconditions.checkNotNull( receiver, "device cannot be null" );
        m_receivers.remove( receiver );
    }

    @Override
    public synchronized void transmitSameDimension( @Nonnull Packet packet, double range )
    {
        Preconditions.checkNotNull( packet, "packet cannot be null" );
        for( IPacketReceiver device : m_receivers )
        {
            tryTransmit( device, packet, range, false );
        }
    }

    @Override
    public synchronized void transmitInterdimensional( @Nonnull Packet packet )
    {
        Preconditions.checkNotNull( packet, "packet cannot be null" );
        for (IPacketReceiver device : m_receivers)
        {
            tryTransmit( device, packet, 0, true );
        }
    }

    private void tryTransmit( IPacketReceiver receiver, Packet packet, double range, boolean interdimensional )
    {
        IPacketSender sender = packet.getSender();
        if( receiver.getWorld() == sender.getWorld() )
        {
            double receiveRange = Math.max( range, receiver.getRange() ); // Ensure range is symmetrical
            double distanceSq = receiver.getPosition().squareDistanceTo( sender.getPosition() );
            if( interdimensional || receiver.isInterdimensional() || distanceSq <= (receiveRange * receiveRange) )
            {
                receiver.receiveSameDimension( packet, Math.sqrt( distanceSq ) );
            }
        }
        else
        {
            if( interdimensional || receiver.isInterdimensional() )
            {
                receiver.receiveDifferentDimension( packet );
            }
        }
    }

    @Override
    public boolean isWireless()
    {
        return true;
    }
}
