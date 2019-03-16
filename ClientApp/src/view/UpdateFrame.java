package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import Model.Music;
import controller.ClientMain;

public class UpdateFrame extends JDialog {
	private JLabel idLabel;
	private JLabel titleLabel;
	private JLabel genreLabel;
	private JLabel ratingLabel;
	private JTextField id;
	private JTextField title;
	private JTextField genre;
	private JTextField rating;
	private JButton saveBtn;
	Music music;
	
	public UpdateFrame(ClientMain parent, Music e)
	{
		super(parent, true);
		this.setLayout(new GridLayout(0,2));
		idLabel =  new JLabel("id");
		titleLabel =  new JLabel("title");
		genreLabel =  new JLabel("genre");
		ratingLabel =  new JLabel("rating");
		id = new JTextField(new Integer(e.getId()).toString());
		id.setEnabled(false);
		title = new JTextField(e.getTitle());
		genre = new JTextField(e.getGenre());
		rating = new JTextField(new Integer(e.getRating()).toString());
	
		saveBtn = new JButton("Save");
		add(idLabel);
		add(id);
		
		
		add(titleLabel);
		add(title);		
		
		add(genreLabel);
		add(genre);		
		
		add(ratingLabel);
		add(rating);
		add(saveBtn);
		
		saveBtn.addActionListener(new ActionListener()
		{

					@Override
					public void actionPerformed(ActionEvent e) {
						music = new Music();
						music.setId(new Integer(id.getText()).intValue());
						music.setTitle(title.getText());
						music.setGenre(genre.getText());
						music.setRating(new Integer(rating.getText()).intValue());
						
						UpdateFrame.this.dispose();
						
						
					}
			
		});
		pack();
		
		
		setVisible(true);
	}

	public UpdateFrame(ClientMain parent)
	{
		super(parent, true);
		this.setLayout(new GridLayout(0,2));
		idLabel =  new JLabel("id");
		titleLabel =  new JLabel("title");
		genreLabel =  new JLabel("genre");
		ratingLabel =  new JLabel("rating");
		id = new JTextField();
		title = new JTextField();
		genre = new JTextField();
		rating = new JTextField();
	
		saveBtn = new JButton("Save");
		add(idLabel);
		add(id);
		
		
		add(titleLabel);
		add(title);		
		
		add(genreLabel);
		add(genre);		
		
		add(ratingLabel);
		add(rating);
		add(saveBtn);
		
		saveBtn.addActionListener(new ActionListener()
		{

					@Override
					public void actionPerformed(ActionEvent e) {
						music = new Music();
						music.setId(new Integer(id.getText()).intValue());
						music.setTitle(title.getText());
						music.setGenre(genre.getText());
						music.setRating(new Integer(rating.getText()).intValue());
						
						UpdateFrame.this.dispose();
						
						
					}
			
		});
		pack();
		
		
		setVisible(true);
	}

	public Music getMusic()
	{
		return music;
	}
}
