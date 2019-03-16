package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
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

public class RetrieveServerResponse implements Runnable {

	private BufferedReader reader;
	private ClientMain parent;
	private boolean useJSonFormat;
	
	public RetrieveServerResponse(ClientMain parent, BufferedReader reader, boolean useJsonFormat) {
		this.reader = reader;
		this.parent = parent;
		this.useJSonFormat = useJsonFormat;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				final List<Object> response;
				if(useJSonFormat) {
					response = readJSonMessage();
				} else {
					response = readMessage();
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						parent.parseResponse(response);						
					}
				});
			}catch(Exception ex) {
				ex.printStackTrace();
				break;
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				parent.addMessage("Serverul a inchis conexiunea \n");						
			}
		});
	}
	
	public List<Object> readMessage() {
		if(reader != null) {
			try {
				StringBuffer sf = new StringBuffer();
				String s;
				while (true) {
					s = reader.readLine();
					sf.append(s);
					if (s.endsWith("</response>")) {
						break;
					}
				}
				String mesaj = sf.toString();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						parent.addMessage("Server: "+mesaj+"\n");						
					}
				});

				return parseXML(mesaj);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public List<Object> readJSonMessage() {
		if(reader != null) {
			try {
				String s = reader.readLine();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						parent.addMessage("Server: "+s+"\n");						
					}
				});
				return parseJson(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public List<Object> parseXML(String sb)
	{
		List<Object> r = new ArrayList<Object>();
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(sb)));
			Element root = doc.getDocumentElement();
			NodeList childrenList = root.getElementsByTagName("action");
			Node action = childrenList.item(0);
			String valAction = action.getTextContent();
			r.add(valAction);
			if(valAction.equalsIgnoreCase("retrieve")) {
				childrenList = root.getElementsByTagName("content");
				Element content = (Element)childrenList.item(0);
				childrenList = content.getElementsByTagName("elev");
				for(int i = 0; i < childrenList.getLength(); i++) {
					Element n = (Element)childrenList.item(i);
					Music music = new Music();
					music.setId(Integer.parseInt(n.getAttribute("id")));
					music.setTitle(((Element)n.getElementsByTagName("nume").item(0)).getTextContent());
					music.setGenre(((Element)n.getElementsByTagName("prenume").item(0)).getTextContent());
					music.setRating(Integer.parseInt(((Element)n.getElementsByTagName("varsta").item(0)).getTextContent()));
					r.add(music);
				}
			} else {
				Element content = (Element)(root.getElementsByTagName("content").item(0));
				String val = content.getTextContent();
				if (valAction.equalsIgnoreCase("insert") || valAction.equalsIgnoreCase("delete") || valAction.equalsIgnoreCase("update")) {
					r.add(val.equalsIgnoreCase("true")? Boolean.TRUE: Boolean.FALSE);
				}
				else {
					r.add(val);
				}
			}				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return r;
	}
	
	public List<Object> parseJson(String sb)
	{
		List<Object> r = new ArrayList<Object>();
		try
		{
			JSONParser parser = new JSONParser();
			JSONObject response = (JSONObject) parser.parse(new StringReader(sb));
			JSONObject responseJSon = (JSONObject)response.get("response");
			String action = (String) responseJSon.get("action");
			r.add(action);
			if(action.equalsIgnoreCase("retrieve")){
				JSONArray contents = (JSONArray)responseJSon.get("content");
				for(int i = 0; i < contents.size(); i++) {
					JSONObject obj = (JSONObject)contents.get(i);
					Music music = new Music(obj);
					r.add(music);
				}
			} else {
				String val = (String)responseJSon.get("content");
				if (action.equalsIgnoreCase("insert") || action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("update")) {
					r.add(val.equalsIgnoreCase("true")? Boolean.TRUE: Boolean.FALSE);
				}
				else {
					
					System.out.println("Client se va inchide");
				}
			}				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return r;
	}


}
