package emulator.rom;

/**
 * Represents a Gameboy ROM, that is a single Gameboy game. <p>
 * 
 * This object contains header information, and access to the main game program.
 *
 * @author Troy Shaw
 */
public class Rom {

	private String title;
	//true if gameboy color
	private boolean color;

	/**
	 * Rom size in kbytes
	 */
	private int romSize;
	private int romBanks;
	
	/**
	 * Ram size in kbytes
	 */
	private int ramSize;
	private int ramBanks;
	
	private Destination destination;

	private int romType;

	private int checksum;
	
	
	private byte[] rom;

	public Rom(byte[] rom) {
		this.rom = rom;

		title = new String(rom, 0x134, 16).trim();
		color = rom[0x143] == 0x80;
		romBanks = (int) (Math.pow(2, (rom[0x148] + 1)));
		romSize = romBanks * 16;
		ramSize = Math.max(0, (int) Math.pow(2, rom[0x149] * 2 - 1));
		ramBanks = (int) (ramSize == 0 ? 0 : Math.max(1, Math.pow(2, (rom[0x149] - 2) * 2)));
		checksum = (rom[0x14E] << 8) | (rom[0x14F]);
		destination = rom[0x014A] == 0 ? Destination.japanese : Destination.nonJapanese;
	}

	public String getTitle() {
		return title;
	}

	public int getRomSize() {
		return romSize;
	}

	public int getRamSize() {
		return ramSize;
	}

	public Destination getDestination() {
		return destination;
	}

	public int getRomType() {
		return romType;
	}

	public byte[] getData() {
		return rom;
	}
	
	@Override
	public String toString() {
		return "title: " + title + ", rom size: " + romSize + ", rom banks: " + romBanks + 
				", ram size: " + ramSize + ", ram banks: " + ramBanks + ", " + destination;
	}
}
