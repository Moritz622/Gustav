package gustav.server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.Executors;

public class Server {

    private HttpServer server;

    private final int port = 8030;

    public Server() {
    }

    public void start() throws IOException {
    	String wifiIpAddress = getLocalIpAddress();
    	
        int port = 8030;
        if (wifiIpAddress == null) {
            System.out.println("[Server] Failed to find Wi-Fi IP address, binding to all interfaces.");
            wifiIpAddress = "0.0.0.0";
        }
        server = HttpServer.create(new InetSocketAddress(port), 0);

        init();

        server.setExecutor(Executors.newCachedThreadPool());

        server.start();

        System.out.println("[Server] started on " + wifiIpAddress + ":" + port);
        
        server.createContext("/", new StaticFileHandler("web"));
    }

    private void init() {

    }
    
    private static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.getAddress().length == 4) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}