package patches.deps;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public aspect WindowsFirewallPatch {

    // Prevent windows firewall from triggering if selenium library tests port availability on a public interface.
    void around(SocketAddress address): call(void java.net.ServerSocket.bind(java.net.SocketAddress)) && args(address) {
        if (address instanceof InetSocketAddress) {
            System.err.println("[Patch] Prevented bind() to bind to a public interface.");
            address = new InetSocketAddress("127.0.0.1", ((InetSocketAddress) address).getPort());
        }
        proceed(address);
    }

}
