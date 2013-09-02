package test;

import emulator.Z80;
import fileio.Loader;
import gui.EmulatorFrame;

public class Main {

	public static void main(String[] args) {
		//newGame();
		loadStart();
	}
	
	public static void loadStart() {
		Z80 z = new Z80(Loader.load("src/Pokemon Red.gb"));
		
		while (true) {
			z.tick();
			System.out.println(z);
		}
	}
	
	public static void newGame() {
		new EmulatorFrame();
	}
}
