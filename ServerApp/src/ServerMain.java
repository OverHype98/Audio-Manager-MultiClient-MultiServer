import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ServerMain extends Thread {

	List<ClientWorker> clients;
	ServerSocket server;
	int port = 9090;
	static DBConnection connection;
	static int id;

	public ServerMain(DBConnection con, int port) {
		clients = new ArrayList<ClientWorker>();
		this.port = port;
		connection = con;

	}

	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			System.out.println("Start server");

			while (!server.isClosed()) {
				try {
					Socket socket = server.accept();
					connection.addClient(socket);
					ClientWorker clientWorker = new ClientWorker(this, socket, connection, ++id);
					new Thread(clientWorker).start();
					clients.add(clientWorker);
					System.out.println("Client: " + id + " with port: " + port);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	public void removeClient(ClientWorker client) {
		clients.remove(client);
	}

	public boolean allowClients() {
		return clients.size() < 5;
	}

	public int getPort() {
		return port;
	}

	public static void main(String[] args) {
		try {
			int port = 9090;
			List<ServerMain> servers = new ArrayList<ServerMain>();
			DBConnection conection = new DBConnection();
			ServerSocket server = new ServerSocket(port);
			System.out.println("Start server");
			id = 0;

			while (!server.isClosed()) {
				try {
					Socket socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					InputStreamReader dis = new InputStreamReader(socket.getInputStream());
					BufferedReader br = new BufferedReader(dis);
					String s = br.readLine();
					if (s.equalsIgnoreCase("da-mi port")) {
						boolean ok = false;
						int p = 0;
						for (ServerMain srv : servers) {
							if (srv.allowClients()) {
								System.out.println("iti dau server existent");
								p = srv.getPort();
								ok = true;
								break;
							}
						}
						if (!ok) {
							port++;
							p = port;
							System.out.println("creez server nou");
							ServerMain sv = new ServerMain(conection, p);
							sv.start();
							servers.add(sv);
						}
						pw.write("port: " + p);
						pw.write("\n");
						pw.flush();
					}
					
					
					socket.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			try {
				server.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
