import org.json.simple.JSONObject;

public class Music {
	private int id;
	private String title;
	private String genre;
	public int rating;

	public Music() {

	}

	public Music(JSONObject json) {
		id = ((Long) json.get("id")).intValue();
		title = (String) json.get("title");
		genre = (String) json.get("genre");
		rating = ((Long) json.get("rating")).intValue();
	}

	public String getXML() {
		return "<music id=\"" + id + "\"><title>" + title + "</title><genre>" + genre + "</genre><rating>" + rating
				+ "</rating></music>";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public JSONObject toJson() {
		JSONObject jsonMusic = new JSONObject();
		jsonMusic.put("id", this.getId());
		jsonMusic.put("title", this.getTitle());
		jsonMusic.put("genre", this.getGenre());
		jsonMusic.put("rating", this.getRating());
		return jsonMusic;
	}

}
// public class Elev {
// private int id;
// private String nume;
// private String prenume;
// private int nrNote;
// private double media;
// private Vector<Integer> note;
// public int varsta;
//
// public Elev()
// {
// note = new Vector<Integer>();
// }
//
// public Elev(int _nrNote)
// {
// nrNote = _nrNote;
// note = new Vector<Integer>();
// }
//
// public String getXML()
// {
// return "<elev
// id="+id+"><nume>"+nume+"</nume><prenume>"+prenume+"</prenume><varsta>"+varsta+"</varsta></elev>";
// }
//
// public void setNume(String nume)
// {
// //proceseaza nume
// //nume = String.format("%s!", nume);
// this.nume = nume;
// }
//
// public String getNume()
// {
// return nume;
// }
//
// public Double getMedia()
// {
// return media;
// }
//
// public void PromoveazaExamen(int nota)
// {
// note.addElement(nota);
// }
//
// public void determinaMedia()
// {
// double suma = 0;
// for(Integer nota : note)
// {
// suma += nota;
// }
//
// if(note.size() > 0)
// {
// media = suma / note.size();
// }
//
// else
// {
// media = 0;
// }
// }
//
// }
