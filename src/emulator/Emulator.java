package emulator;

import java.awt.Dimension;

import emulator.rom.Rom;

public class Emulator implements ButtonController {

	public static final int WIDTH = 250;
	public static final int HEIGHT = 250;
	public static final Dimension size = new Dimension(WIDTH, HEIGHT);

	private boolean[] keys = new boolean[Button.values().length];

	private Rom rom;

	public Emulator() {

	}

	public void emulate(Rom rom) {
		if (rom == null) throw new NullPointerException("rom cannot be null");

		this.rom = rom;
		
		this.rom.toString();
	}

	@Override
	public void buttonInteracted(Button button, boolean pressed) {
		if (button == null) throw new NullPointerException("button cannot be null");

		keys[button.ordinal()] = pressed;
		
		System.out.println(button + (pressed ? " pressed" : " depressed"));
	}
}