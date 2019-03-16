package Model;

import org.json.simple.JSONObject;

public class Music {
	private int id;
	private String title;
	private String genre;
	private int rating;
	
	public Music() {
		
	}
	
	public Music(JSONObject json) {
		id = ((Long)json.get("id")).intValue();
		title = (String)json.get("title");
		genre = (String)json.get("genre");
		rating = ((Long)json.get("rating")).intValue();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public JSONObject toJson()
	{
		JSONObject jsonMusic = new JSONObject();      
		jsonMusic.put("id",this.getId());
		jsonMusic.put("title", this.getTitle());
		jsonMusic.put("genre", this.getGenre());
		jsonMusic.put("rating", this.getRating());
		return jsonMusic;
	}
	public String toXML() {
		return "<music id =\""+id+"\"><title>"+title+"</title><genre>"+genre+"</genre><rating>"+rating+"</rating></music>";
	}
}
