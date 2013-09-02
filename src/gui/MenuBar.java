package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar implements ActionListener {

	JMenu fileMenu, optionsMenu, helpMenu;
	
	JMenuItem reset, load, exit;
	JMenuItem size, mute, volume, controls;
	JMenuItem help, about;
	
	public MenuBar() {
		fileMenu = new JMenu("File");
		optionsMenu = new JMenu("Options");
		helpMenu = new JMenu("Help");
		
		reset = new JMenuItem("Reset");
		load = new JMenuItem("Load");
		exit = new JMenuItem("Exit");
		
		size = new JMenuItem("Screen size");
		mute = new JMenuItem("Mute");
		volume = new JMenuItem("Volume");
		controls = new JMenuItem("Controls");
		
		help = new JMenuItem("Help");
		about = new JMenuItem("About");
		
		reset.addActionListener(this);
		load.addActionListener(this);
		exit.addActionListener(this);
		
		size.addActionListener(this);
		mute.addActionListener(this);
		volume.addActionListener(this);
		controls.addActionListener(this);
		
		help.addActionListener(this);
		about.addActionListener(this);
		
		fileMenu.add(reset);
		fileMenu.addSeparator();
		fileMenu.add(load);
		fileMenu.addSeparator();
		fileMenu.add(exit);
		
		optionsMenu.add(size);
		optionsMenu.addSeparator();
		optionsMenu.add(mute);
		optionsMenu.add(volume);
		optionsMenu.addSeparator();
		optionsMenu.add(controls);
		
		helpMenu.add(help);
		helpMenu.addSeparator();
		helpMenu.add(about);
		
		add(fileMenu);
		add(optionsMenu);
		add(helpMenu);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if (o == reset) {
			
		} else if (o == load) {
			
		} else if (o == exit) {
			System.exit(0);
		} else if (o == help) {
			
		} else if (o == about) {
			
		}
	}
}
