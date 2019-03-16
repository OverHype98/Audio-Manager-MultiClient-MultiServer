package Model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;;

public class MusicTableModel extends DefaultTableModel {
	private List<Music> musicList;
	
	public MusicTableModel(List<Music> musicList) {
		super();
		if(musicList == null)
		{
			this.musicList = new ArrayList<Music>();
		}
		else
		{
			this.musicList = musicList;
		}
	}
	
	public void setMusicList(List<Music>musicList) {
		this.musicList = musicList;
		fireTableDataChanged();
	}
	
	public void addMusic(Music music) {
		this.musicList.add(music);
		fireTableDataChanged();
	}
	
	public Music getMusic(int index) {
		if (index >= 0 && index < musicList.size()) {
			return musicList.get(index);
		}
		return null;
	}
	
	@Override
	public int getColumnCount() {
		return 4;
	}
	
	@Override
	public int getRowCount() {
		if(musicList == null) return 0;
		return musicList.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		Music music = getMusic(row);
		if (music == null) return null;
		if(column == 0)
			return music.getId();
		if (column == 1)
			return music.getTitle();
		if (column == 2)
			return music.getGenre();
		if (column == 3)
			return music.getRating();
		return null;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0 || columnIndex == 3)
			return Integer.class;
		return String.class;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == 0)
			return "Id";
		if (column == 1)
			return "Title";
		if (column == 2)
			return "Genre";
		if (column == 3)
			return "Rating";
		return "";
	}
	
	public void insert(Music music) {
		if(music != null) {
			musicList.add(music);
			fireTableDataChanged();
		}
	}
	public void update(Music music) {
		fireTableDataChanged();
	}
	
	public void delete(Music music) {
		musicList.remove(music);
		fireTableDataChanged();
	}
}
