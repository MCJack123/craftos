/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.computer.core;

public class ClientComputerRegistry extends ComputerRegistry<ClientComputer>
{
    public ClientComputerRegistry()
    {
    }

    public void update()
    {
        for( ClientComputer computer : getComputers() )
        {
            computer.update();
        }
    }

    @Override
    public void add( int instanceID, ClientComputer computer )
    {
        //System.out.println( "ADD CLIENT COMPUTER " + instanceID );
        super.add( instanceID, computer );
        computer.requestState();
        //System.out.println( getComputers().size() + " CLIENT COMPUTERS" );
    }

    
}
