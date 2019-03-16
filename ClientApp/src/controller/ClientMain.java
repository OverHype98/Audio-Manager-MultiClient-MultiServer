package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import Model.Music;
import Model.MusicTableModel;
import view.UpdateFrame;

public class ClientMain extends JFrame {
	private JTextArea messageArea;
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	MusicTableModel tableModel;
	boolean useJSonFormat = true;
	int clientCount = 0;

	public ClientMain() {
		super("Client");
		tableModel = new MusicTableModel(null);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		JLabel lblMessage = new JLabel();
		lblMessage.setText("Message:");
		lblMessage.setBounds(10, 10, 100, 30);
		panel.add(lblMessage);

		JTextField txtMessage = new JTextField();
		txtMessage.setBounds(120, 10, 200, 30);
		panel.add(txtMessage);

		JButton btnSend = new JButton("Send");
		btnSend.setBounds(340, 10, 100, 30);
		panel.add(btnSend);
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OperationDb(txtMessage.getText());
			}
		});

		messageArea = new JTextArea();
		messageArea.setBounds(10, 50, 440, 200);
		JScrollPane scrollPane = new JScrollPane(messageArea);
		scrollPane.setBounds(10, 50, 440, 200);
		panel.add(scrollPane);

		JTable tabel = new JTable(tableModel);
		JScrollPane scroll = new JScrollPane(tabel);
		scroll.setBounds(460, 10, 400, 240);
		panel.add(scroll);

		JButton btnRefresh = new JButton("Retrieve");
		btnRefresh.setBounds(460, 270, 90, 30);
		panel.add(btnRefresh);
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OperationDb("retrieve");
			}
		});

		JButton btnInsert = new JButton("Insert");
		btnInsert.setBounds(560, 270, 90, 30);
		panel.add(btnInsert);
		btnInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UpdateFrame updateFrame = new UpdateFrame(ClientMain.this);
				OperationDb("insert", updateFrame.getMusic());
			}
		});

		JButton btnUpdate = new JButton("Update");
		btnUpdate.setBounds(660, 270, 90, 30);
		panel.add(btnUpdate);
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = tabel.getSelectedRow();
				Music music = tableModel.getMusic(index);

				if (music == null) {
					JOptionPane.showMessageDialog(ClientMain.this, "Nothing was selected");
				} else {
					UpdateFrame updateFrame = new UpdateFrame(ClientMain.this, music);
					OperationDb("update", updateFrame.getMusic());
				}

			}
		});

		JButton btnDelete = new JButton("Delete");
		btnDelete.setBounds(760, 270, 90, 30);
		panel.add(btnDelete);
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = tabel.getSelectedRow();
				Music music = tableModel.getMusic(index);
				if (music == null) {
					JOptionPane.showMessageDialog(ClientMain.this, "Nu s-a selectat nimic");
				} else {
					OperationDb("delete", music);
				}
			}
		});

		add(panel);
		pack();
		setSize(900, 350);
		setVisible(true);
		connect(9090, false);
		int port = getServerPort();
		
		if (port != -1) {
			connect(port, true);
		} else {
			JOptionPane.showMessageDialog(this, "Nu am reusit sa preiau portul!");
		}
	}

	public void addMessage(String message) {
		messageArea.append(message);
	}

	public void connect(int port, boolean finala) {
		InetAddress address;
		// int port = 9090;
		try {
			address = InetAddress.getLocalHost();
			socket = new Socket(address, port);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			InputStreamReader dis = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(dis);
			if (finala) {

				new Thread(new RetrieveServerResponse(this, reader, true)).start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private int getServerPort() {

		if (writer != null && reader != null) {
			String msg = "da-mi port";
			try {
				writer.write(msg);
				writer.newLine();
				writer.flush();
				messageArea.append("Client: " + msg + "\n");
				msg = reader.readLine();
				messageArea.append("Server: " + msg + "\n");
				String[] ss = msg.split(": ");
				if (ss[0].equalsIgnoreCase("port")) {
					int port = Integer.parseInt(ss[1]);
					return port;
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
					writer.close();
					socket.close();
				} catch (Exception e) {
				}
			}
		}
		return -1;
	}

	public void sendMessage(String mesaj) {
		String msg = "<comand><action>" + mesaj + "</action>";
		msg += "</comand>";
		if (writer != null) {
			try {
				writer.write(msg);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			messageArea.append("Client: " + msg + "\n");
		}
	}

	public void sendJSonMessage(String mesaj) {

		JSONObject json = new JSONObject();

		json.put("action", mesaj);

		JSONObject jsonObj = new JSONObject();

		jsonObj.put("command", json);

		String message = jsonObj.toJSONString();
		String msg = message;

		if (writer != null) {
			try {
				writer.write(msg);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			messageArea.append("Client: " + msg + "\n");
		}
	}

	public void sendMessageWithElev(String mesaj, Music music) {
		String msg = "<comand><action>" + mesaj + "</action>";
		if (mesaj.equalsIgnoreCase("update")) {
			msg = msg + "<content>" + music.toXML() + "</content>";
		} else if (mesaj.equalsIgnoreCase("insert")) {
			msg = msg + "<content>" + music.toXML() + "</content>";
		} else if (mesaj.equalsIgnoreCase("delete")) {
			msg = msg + "<content>" + music.toXML() + "</content>";
		}

		msg += "</comand>";
		if (writer != null) {
			try {
				writer.write(msg);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			messageArea.append("Client: " + msg + "\n");
		}
	}

	public void sendJsonMessageWithElev(String mesaj, Music music) {
		JSONObject json = new JSONObject();
		json.put("action", mesaj);
		JSONObject jsonElev = music.toJson();
		JSONObject jsonCommand = new JSONObject();
		json.put("content", jsonElev);
		jsonCommand.put("command", json);
		String message = jsonCommand.toJSONString();

		if (writer != null) {
			try {
				writer.write(message);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			messageArea.append("Client: " + message + "\n");
		}
	}

	public void OperationDb(String operation) {
		if (useJSonFormat)
			sendJSonMessage(operation);
		else
			sendMessage(operation);
	}

	public void parseResponse(List<Object> response) {

		String action = (String) response.get(0);

		if (action.toLowerCase().equals("retrieve")) {
			List<Music> elevi = new ArrayList<Music>();
			for (int i = 1; i < response.size(); i++) {
				Music el = (Music) response.get(i);
				elevi.add(el);
			}
			tableModel.setMusicList(elevi);
		} else if (action.toLowerCase().equals("update") || action.toLowerCase().equals("delete")
				|| action.toLowerCase().equals("insert")
				|| action.toLowerCase().startsWith("baza de date a fost updatata de clientul")) {

			{
				OperationDb("retrieve");
			}
		}
	}

	private void OperationDb(String operation, Music e) {
		if (useJSonFormat)
			sendJsonMessageWithElev(operation, e);
		else
			sendMessageWithElev(operation, e);
	}

	public static void main(String[] args) {
		try {

			ClientMain clientMain = new ClientMain();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
