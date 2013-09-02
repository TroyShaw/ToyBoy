package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import emulator.Button;
import emulator.ButtonController;
import emulator.Emulator;

public class EmulatorPanel extends JPanel {

	private ButtonController buttonController;
	private Map<Integer, Button> buttonMapping;
	
	public EmulatorPanel() {
		setPreferredSize(Emulator.size);
		
		registerKeyListener();
		
		setFocusable(true);
		requestFocusInWindow();
	}
	
	private void registerKeyListener() {
		buttonMapping = new HashMap<Integer, Button>();
		buttonMapping.put(KeyEvent.VK_A, Button.a);
		buttonMapping.put(KeyEvent.VK_S, Button.b);
		buttonMapping.put(KeyEvent.VK_UP, Button.up);
		buttonMapping.put(KeyEvent.VK_DOWN, Button.down);
		buttonMapping.put(KeyEvent.VK_LEFT, Button.left);
		buttonMapping.put(KeyEvent.VK_RIGHT, Button.right);
		buttonMapping.put(KeyEvent.VK_ENTER, Button.start);
		buttonMapping.put(KeyEvent.VK_BACK_SPACE, Button.select);
		
		KeyListener kl = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				interacted(e.getKeyCode(), true);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				interacted(e.getKeyCode(), false);
			}
			
			private void interacted(int val, boolean pushed) {
				Button b = buttonMapping.get(val);
				if (b != null) buttonController.buttonInteracted(b, pushed);
			}
		};
		
		addKeyListener(kl);
	}
	
	public void registerButtonController(ButtonController buttonController) {
		this.buttonController = buttonController;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}