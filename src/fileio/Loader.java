package fileio;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import emulator.rom.Rom;

public final class Loader {

	private Loader() {
		//stop instantiation
	}
	
	public static Rom load(String path) {
		File file = new File(path);
		byte [] fileData = new byte[(int)file.length()];
		
		DataInputStream dis = null;
		try {
			dis = new DataInputStream((new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			dis.readFully(fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Rom(fileData);
	}
}