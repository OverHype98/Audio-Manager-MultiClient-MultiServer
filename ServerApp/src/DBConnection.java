import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import org.json.simple.JSONObject;

public class DBConnection 
{
	private Connection conn = null;
	private Vector<Socket> clienti;

	public DBConnection() 
	{
		clienti = new Vector<Socket>();
		try {
			Class.forName("org.mariadb.jdbc.Driver").newInstance();
			// conn = DriverManager.getConnection("jdbc:mariadb://localhost:3308/databse name?user=proelite&password=proelite");
			conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/music", "root", "123456");
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public synchronized void addClient(Socket s)
	{
		clienti.add(s);
	}
	
	public synchronized void removeClient(Socket s)
	{
		clienti.remove(s);
	}
	
	public synchronized List<Music> getMusicList() {
		if (conn == null)
			return null;
		try {
			List<Music> musicList = new ArrayList<Music>();
			String sql = "SELECT id, title, genre, rating FROM Muzica";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				int rating = rs.getInt("rating");
				String title = rs.getString("title");
				String genre = rs.getString("genre");
				Music song = new Music();
				song.setId(id);
				song.setRating(rating);
				song.setTitle(title);
				song.setGenre(genre);
				musicList.add(song);
			}
			// STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			return musicList;
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return null;

	}

	private void sendInformtoClients(int idClient)
	{
		int i = 1;
		for(Socket clientSocket: clienti)
		{
			if(i != idClient)
			{
				PrintWriter pw;
				try {
					pw = new PrintWriter(clientSocket.getOutputStream());
					JSONObject obj = new JSONObject();
					String mesaj = "The database was updated by client - " + idClient;
					obj.put("action", mesaj);
					JSONObject resp = new JSONObject();
					resp.put("response", obj);
					String msg = resp.toJSONString();
					pw.println(msg);
					pw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i++;
		}
	}
	
	public synchronized boolean updateMusic(Music music, int idClient) {
		
		boolean returnValue = false;
		if (conn == null)
			return false;
		PreparedStatement stmt = null;
		try {
			String sql ="UPDATE Muzica SET title = ?, genre = ?, rating = ? WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, music.getTitle());
			stmt.setString(2, music.getGenre());
			stmt.setInt(3, music.getRating());
			stmt.setInt(4, music.getId());
			returnValue = 1 <= stmt.executeUpdate();
			if(returnValue)
			{
				sendInformtoClients(idClient);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
			} // do nothing
		}
		return returnValue;
	}
	
	public synchronized boolean insertMusic(Music music, int idClient) {
		boolean returnValue = false;
		
		if (conn == null)
		{
			return false;
		}
		PreparedStatement stmt = null;
		try {
			String sql ="INSERT INTO Muzica VALUES (?, ?, ?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, music.getId());
			stmt.setString(2, music.getTitle());
			stmt.setString(3, music.getGenre());
			stmt.setInt(4, music.getRating());
			returnValue = (1 == stmt.executeUpdate());
			if(returnValue)
			{
				sendInformtoClients(idClient);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
			} // do nothing
		}
		return returnValue;
	}
	
	public synchronized int deleteMusic(Music music, int idClient) {
		int returnValue = 0;
		if (conn == null)
			return 0;
		PreparedStatement stmt = null;
		try {
			String sql ="DELETE FROM Muzica WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, music.getId());
			returnValue = stmt.executeUpdate();
			if(returnValue > 0 )
			{
				sendInformtoClients(idClient);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
			} // do nothing
		}
		return returnValue;
	}
	
	public synchronized void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn = null;
		}
	}
}
