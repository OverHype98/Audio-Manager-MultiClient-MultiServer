import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.experimental.theories.Theories;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ClientWorker implements Runnable {
	private Socket socket;
	private int idClient;
	private DBConnection conn;
	private ServerMain parent;
	
	public ClientWorker(ServerMain p, Socket socket, DBConnection conn, int id) {
		parent = p;
		this.socket = socket;
		idClient = id;
		this.conn = conn;
		//conn.addClient(this);
	}

	@Override
	public void run() {
		try {

			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			InputStreamReader dis = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(dis);
			while (true) 
			{
				String s = br.readLine();
				boolean ok = runJSON(s, pw);
				if (!ok)
					break;
			}
		} catch (Exception ex) {

		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		parent.removeClient(this);
	}


	public List<Object> parseJson(String sb) {
		List<Object> r = new ArrayList<Object>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject response = (JSONObject) parser.parse(new StringReader(sb));
			JSONObject responseJSon = (JSONObject) response.get("command");
			String action = (String) responseJSon.get("action");
			r.add(action);
			if (action.equalsIgnoreCase("retrieve")) {
				List<Music> elevi = conn.getMusicList();
				for (Music music : elevi) {
					r.add(music);
				}
			} else if (action.equalsIgnoreCase("insert")) {
				JSONObject elevJSon = (JSONObject) responseJSon.get("content");
				Music music = new Music(elevJSon);
				boolean v = conn.insertMusic(music, idClient);
				r.add(v ? "true" : "false");
			} else if (action.equalsIgnoreCase("delete")) {
				JSONObject elevJSon = (JSONObject) responseJSon.get("content");
				Music music = new Music(elevJSon);
				boolean v = conn.deleteMusic(music, idClient) > 0;
				r.add(v ? "true" : "false");
			} else if (action.equalsIgnoreCase("update")) {
				JSONObject elevJSon = (JSONObject) responseJSon.get("content");
				Music music = new Music(elevJSon);
				boolean v = conn.updateMusic(music, idClient);
				r.add(v ? "true" : "false");
			} else if (action.equalsIgnoreCase("data")) {
				r.add(Calendar.getInstance().getTime().toString());
			} else if (action.equalsIgnoreCase("get port"))
			{
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public boolean runJSON(String s, PrintWriter pw) {
		try {
			List<Object> r = parseJson(s);

			String action = (String) r.get(0);
			System.out.println("Clientul spune:" + s);

			if (action.equalsIgnoreCase("data")) 
			{
				JSONObject obj = new JSONObject();
				obj.put("action", action);
				obj.put("content", r.get(1));
				JSONObject resp = new JSONObject();
				resp.put("response", obj);
				String msg = resp.toJSONString();
				pw.println(msg);
			} 
			else if (action.equalsIgnoreCase(".")) 
			{
				JSONObject obj = new JSONObject();
				obj.put("action", "am inchis");
				JSONObject resp = new JSONObject();
				resp.put("response", obj);
				String msg = resp.toJSONString();
				pw.println(msg);
				pw.flush();
				return false;
			} 
			else if (action.contains("update")) 
			{
				JSONObject obj = new JSONObject();
				obj.put("action", action);
				obj.put("content", r.get(1));
				JSONObject resp = new JSONObject();
				resp.put("response", obj);
				String msg = resp.toJSONString();
				pw.println(msg);
			} 
			else if (action.contains("insert")) 
			{
				JSONObject obj = new JSONObject();
				obj.put("action", action);
				obj.put("content", r.get(1));
				JSONObject resp = new JSONObject();
				resp.put("response", obj);
				String msg = resp.toJSONString();
				pw.println(msg);
			} 
			else if (action.contains("retrieve")) 
			{
				JSONObject obj = new JSONObject();
				obj.put("action", action);
				JSONArray elevi = new JSONArray();
				for (int i = 1; i < r.size(); i++) {
					elevi.add(((Music) r.get(i)).toJson());
				}
				obj.put("content", elevi);
				JSONObject resp = new JSONObject();
				resp.put("response", obj);
				String msg = resp.toJSONString();
				pw.println(msg);
			} 
			else if (action.contains("delete")) 
			{
				JSONObject obj = new JSONObject();
				obj.put("action", action);
				obj.put("content", r.get(1));
				JSONObject resp = new JSONObject();
				resp.put("response", obj);
				String msg = resp.toJSONString();
				pw.println(msg);
			} 
			else 
			{
				JSONObject obj = new JSONObject();
				obj.put("action", action);
				obj.put("content", r.get(1));
				JSONObject resp = new JSONObject();
				resp.put("response", obj);
				String msg = resp.toJSONString();
				pw.println(msg);
			}
			pw.flush();
		} catch (Exception ex) {

		}
		return true;
	}

	public List<Object> parseXML(StringBuffer sb) {
		List<Object> r = new ArrayList<Object>();
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(sb.toString())));
			Element root = doc.getDocumentElement();
			NodeList childrenList = root.getElementsByTagName("action");
			Node action = childrenList.item(0);
			String valAction = action.getTextContent();
			r.add(valAction);
			childrenList = root.getElementsByTagName("content");
			Element content = (Element) childrenList.item(0);
			childrenList = content.getElementsByTagName("elev");
			for (int i = 0; i < childrenList.getLength(); i++) {
				Element n = (Element) childrenList.item(i);
				Music music = new Music();
				music.setId(Integer.parseInt(n.getAttribute("id")));
				music.setTitle(((Element) n.getElementsByTagName("nume").item(0)).getTextContent());
				music.setGenre(((Element) n.getElementsByTagName("prenume").item(0)).getTextContent());
				music.setRating(Integer.parseInt(((Element) n.getElementsByTagName("varsta").item(0)).getTextContent()));
				r.add(music);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}
}
