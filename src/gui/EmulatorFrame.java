package gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

import emulator.Emulator;

public class EmulatorFrame extends JFrame {

	private EmulatorPanel panel;
	private Emulator emulator;
	private Controller controller;
	
	private MenuBar menuBar;
	
	public EmulatorFrame() {
		super("Troyboy Gameboy Emulator");
		
		setNativeLAndF();
		initComponents();
		initMenubar();
		setupLayout();
		initFrame();
	}
	
	private void initComponents() {
		emulator = new Emulator();
		panel = new EmulatorPanel();
		
		controller = new Controller(emulator, panel);
		
		panel.registerButtonController(emulator);
	}
	
	private void initMenubar() {
		menuBar = new MenuBar();
		
		setJMenuBar(menuBar);
	}
	
	private void setupLayout() {
		getContentPane().add(panel);
	}
	
	private void initFrame() {
		pack();
		center();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
	
	/**
	 * Sets the look and feel of the GUI to the current systems Look and feel.
	 */
	private void setNativeLAndF() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			//do nothing. It will default to normal
		}
	}
	
	/**
	 * Centers the screen.
	 */
	private void center() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		setLocation(x, y);
	}
}