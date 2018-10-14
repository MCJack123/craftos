package dan200.computercraft.shared.network;

import java.util.ArrayList;

public class NetworkManager {
    private INetworkedThing server;
    private ArrayList<INetworkedThing> clients;

    public void sendTo(ComputerCraftPacket packet, int player) {
        System.out.println("sending packet");
        clients.get(player).handlePacket(packet, 0);
    }

    public void sendToAll(ComputerCraftPacket packet) {
        System.out.println("sending packet to all");
        for (INetworkedThing thing : clients) thing.handlePacket(packet, 0);
    }

    public void sendToServer(ComputerCraftPacket packet) {
        System.out.println("sending packet to server");
        server.handlePacket(packet, 0);
    }

    public void registerClient(INetworkedThing client) {
        clients.add(client);
    }

    public void setServer(INetworkedThing server) {
        this.server = server;
    }
}
