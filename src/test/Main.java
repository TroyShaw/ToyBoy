package test;

import java.io.FileNotFoundException;

import emulator.Z80;
import fileio.Loader;
import gui.EmulatorFrame;

public class Main {

	public static void main(String[] args) {
		//newGame();
		loadStart();
	}
	
	public static void loadStart() {
		Z80 z = null;
		
		try {
		z = new Z80(Loader.load("src/Pokemon Red.gb"));
		} catch (Exception e) {
			System.out.println("That file was not found.");
			System.exit(1);;
		}
		
		while (true) {
			z.tick();
			System.out.println(z);
		}
	}
	
	public static void newGame() {
		new EmulatorFrame();
	}
}
