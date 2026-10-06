package gustav;

import java.io.IOException;

import gustav.server.Server;

public class Application {
	
	Server server;
	
	public void start() {
		server = new Server();
		
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
