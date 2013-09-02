package emulator;

import emulator.rom.Rom;

public class Z80 {


	private int A = 0, B = 1, C= 2, D = 3, E = 4, F = 5, H = 6, L = 7;
	private int PC, SP;
	private byte[] data;
	private int[] registerFile = new int[8];
	private int FZ = 0, FN = 1, FH = 2, FC = 3;
	private boolean[] flags = new boolean[4];
	private int cycles;

	private int[] secondaryReg = {B, C, D, E, H, L, -1, A, B, C, D, E, H, L, -1, A};

	private Rom rom;
	
	public Z80(Rom rom) {
		PC = 100;
		this.rom = rom;
		this.data = rom.getData();
	}
	
	/**
	 * Causes 1 tick of the processor, that is 1 line of command is consumed.
	 */
	public void tick() {
		int op1 = data[PC++]  & 0xFF;
		System.out.println(op1);
		switch (op1) {
		case 0x00:		//NOP                  No Operation
			nop();
			break;
		case 0x01:		//LD BC,nn             Load 16-bit immediate into BC
			loadImmediate(B, C);
			break;
		case 0x02:		//LD (BC),A            Save A to address pointed by BC
			loadFromA(B, C);
			break;
		case 0x03:		//INC BC               Increment 16-bit BC
			increment(B, C);
			break;
		case 0x04:		//INC B                Increment B
			increment(B);
			break;
		case 0x05:		//DEC B                Decrement B
			decrement(B);
			break;
		case 0x06:		//LD B,n               Load 8-bit immediate into B
			loadImmediate(B);
			break;
		case 0x07:		//RLC A                Rotate A left with carry
			rotateALeftWithCarry();
			break;
		case 0x08:		//LD (nn),SP           Save SP to given address
			loadSPToAddress();
			break;
		case 0x09:		//ADD HL,BC            Add 16-bit BC to HL
			addHL(B, C);
			break;
		case 0x0A:		//LD A,(BC)            Load A from address pointed to by BC
			loadIntoA(B, C);
			break;
		case 0x0B:		//DEC BC               Decrement 16-bit BC
			decrement(B, C);
			break;
		case 0x0C:		//INC C                Increment C
			increment(C);
			break;
		case 0x0D:		//DEC C                Decrement C
			decrement(C);
			break;
		case 0x0E:		//LD C,n               Load 8-bit immediate into C
			loadImmediate(C);
			break;
		case 0x0F:		//RRC A                Rotate A right with carry
			rotateARightWithCarry();
			break;
		case 0x10:		//STOP                 Stop processor
			stop();	// TODO maybe this opcode is 0x1000, might need to eat another byte
			break;
		case 0x11:		//LD DE,nn             Load 16-bit immediate into DE
			loadImmediate(D, E);
			break;
		case 0x12:		//LD (DE),A            Save A to address pointed by DE
			loadFromA(D, E);
			break;
		case 0x13:		//INC DE               Increment 16-bit DE
			increment(D, E);
			break;
		case 0x14:		//INC D                Increment D
			increment(D);
			break;
		case 0x15:		//DEC D                Decrement D
			decrement(D);
			break;
		case 0x16:		//LD D,n               Load 8-bit immediate into D
			loadImmediate(D);
			break;
		case 0x17:		//RL A                 Rotate A left
			rotateALeft();
			break;
		case 0x18:		//JR n                 Relative jump by signed immediate
			jumpRelative();
			break;
		case 0x19:		//ADD HL,DE            Add 16-bit DE to HL
			addHL(D, E);
			break;
		case 0x1A:		//LD A,(DE)            Load A from address pointed to by DE
			loadIntoA(D, E);
			break;
		case 0x1B:		//DEC DE               Decrement 16-bit DE
			decrement(D, E);
			break;
		case 0x1C:		//INC E                Increment E
			increment(E);
			break;
		case 0x1D:		//DEC E                Decrement E
			decrement(E);
			break;
		case 0x1E:		//LD E,n               Load 8-bit immediate into E
			loadImmediate(E);
			break;
		case 0x1F:		//RR A                 Rotate A right
			rotateARight();
			break;
		case 0x20:		//JR NZ,n              Relative jump by signed immediate if last result was not zero
			jumpRelative(FZ, false);
			break;
		case 0x21:		//LD HL,nn             Load 16-bit immediate into HL
			loadImmediate(H, L);
			break;
		case 0x22:		//LDI (HL),A           Save A to address pointed by HL, and increment HL
			loadHLAIncrement();
			break;
		case 0x23:		//INC HL               Increment 16-bit HL
			increment(H, L);
			break;
		case 0x24:		//INC H                Increment H
			increment(H);
			break;
		case 0x25:		//DEC H                Decrement H
			decrement(H);
			break;
		case 0x26:		//LD H,n               Load 8-bit immediate into H
			loadImmediate(H);
			break;
		case 0x27:		//DAA                  Adjust A for BCD addition
			decimalAdjustA();
			break;
		case 0x28:		//JR Z,n               Relative jump by signed immediate if last result was zero
			jumpRelative(FZ, true);
			break;
		case 0x29:		//ADD HL,HL            Add 16-bit HL to HL
			addHL(H, L);
			break;
		case 0x2A:		//LDI A,(HL)           Load A from address pointed to by HL, and increment HL
			loadAHLIncrement();
			break;
		case 0x2B:		//DEC HL               Decrement 16-bit HL
			decrement(H, L);
			break;
		case 0x2C:		//INC L                Increment L
			increment(L);
			break;
		case 0x2D:		//DEC L                Decrement L
			decrement(L);
			break;
		case 0x2E:		//LD L,n               Load 8-bit immediate into L
			loadImmediate(L);
			break;
		case 0x2F:		//CPL                  Complement (logical NOT) on A
			complementA();
			break;
		case 0x30:		//JR NC,n              Relative jump by signed immediate if last result caused no carry
			jumpRelative(FC, false);
			break;
		case 0x31:		//LD SP,nn             Load 16-bit immediate into SP
			loadImmediateSP();
			break;
		case 0x32:		//LDD (HL),A           Save A to address pointed by HL, and decrement HL
			loadHLADecrement();
			break;
		case 0x33:		//INC SP               Increment 16-bit SP
			incrementSP();
			break;
		case 0x34:		//INC (HL)             Increment value pointed by HL
			incrementHL();
			break;
		case 0x35:		//DEC (HL)             Decrement value pointed by HL
			decrementHL();
			break;
		case 0x36:		//LD (HL),n            Load 8-bit immediate into address pointed by HL
			loadIntoHLImmediate();
			break;
		case 0x37:		//SCF                  Set carry flag
			setCarryFlag();
			break;
		case 0x38:		//JR C,n               Relative jump by signed immediate if last result caused carry
			jumpRelative(FC, true);
			break;
		case 0x39:		//ADD HL,SP            Add 16-bit SP to HL
			addHLSP();
			break;
		case 0x3A:		//LDD A,(HL)           Load A from address pointed to by HL, and decrement HL
			loadAHLDecrement();
			break;
		case 0x3B:		//DEC SP               Decrement 16-bit SP
			decrementSP();
			break;
		case 0x3C:		//INC A                Increment A
			increment(A);
			break;
		case 0x3D:		//DEC A                Decrement A
			increment(A);
			break;
		case 0x3E:		//LD A,n               Load 8-bit immediate into A
			loadImmediate(A);
			break;
		case 0x3F:		//CCF                  Compliment carry flag
			complimentCarryFlag();
			break;
		case 0x40:		//LD B,B               Copy B to B
			load(B, B);
			break;
		case 0x41:		//LD B,C               Copy C to B
			load(B, C);
			break;
		case 0x42:		//LD B,D               Copy D to B
			load(B, D);
			break;
		case 0x43:		//LD B,E               Copy E to B
			load(B, E);
			break;
		case 0x44:		//LD B,H               Copy H to B
			load(B, H);
			break;
		case 0x45:		//LD B,L               Copy L to B
			load(B, L);
			break;
		case 0x46:		//LD B,(HL)            Copy value pointed by HL to B
			loadFromHL(B);
			break;
		case 0x47:		//LD B,A               Copy A to B
			load(B, A);
			break;
		case 0x48:		//LD C,B               Copy B to C
			load(C, B);
			break;
		case 0x49:		//LD C,C               Copy C to C
			load(C, C);
			break;
		case 0x4A:		//LD C,D               Copy D to C
			load(C, D);
			break;
		case 0x4B:		//LD C,E               Copy E to C
			load(C, E);
			break;
		case 0x4C:		//LD C,H               Copy H to C
			load(C, H);
			break;
		case 0x4D:		//LD C,L               Copy L to C
			load(C, L);
			break;
		case 0x4E:		//LD C,(HL)            Copy value pointed by HL to C
			loadFromHL(C);
			break;
		case 0x4F:		//LD C,A               Copy A to C
			load(C, A);
			break;
		case 0x50:		//LD D,B               Copy B to D
			load(D, B);
			break;
		case 0x51:		//LD D,C               Copy C to D
			load(D, C);
			break;
		case 0x52:		//LD D,D               Copy D to D
			load(D, D);
			break;
		case 0x53:		//LD D,E               Copy E to D
			load(D, E);
			break;
		case 0x54:		//LD D,H               Copy H to D
			load(D, H);
			break;
		case 0x55:		//LD D,L               Copy L to D
			load(D, L);
			break;
		case 0x56:		//LD D,(HL)            Copy value pointed by HL to D
			loadFromHL(D);
			break;
		case 0x57:		//LD D,A               Copy A to D
			load(D, A);
			break;
		case 0x58:		//LD E,B               Copy B to E
			load(E, B);
			break;
		case 0x59:		//LD E,C               Copy C to E
			load(E, C);
			break;
		case 0x5A:		//LD E,D               Copy D to E
			load(E, D);
			break;
		case 0x5B:		//LD E,E               Copy E to E
			load(E, E);
			break;
		case 0x5C:		//LD E,H               Copy H to E
			load(E, H);
			break;
		case 0x5D:		//LD E,L               Copy L to E
			load(E, L);
			break;
		case 0x5E:		//LD E,(HL)            Copy value pointed by HL to E
			loadFromHL(E);
			break;
		case 0x5F:		//LD E,A               Copy A to E
			load(E, A);
			break;
		case 0x60:		//LD H,B               Copy B to H
			load(H, B);
			break;
		case 0x61:		//LD H,C               Copy C to H
			load(H, C);
			break;
		case 0x62:		//LD H,D               Copy D to H
			load(H, D);
			break;
		case 0x63:		//LD H,E               Copy E to H
			load(H, E);
			break;
		case 0x64:		//LD H,H               Copy H to H
			load(H, H);
			break;
		case 0x65:		//LD H,L               Copy L to H
			load(H, L);
			break;
		case 0x66:		//LD H,(HL)            Copy value pointed by HL to H
			loadFromHL(H);
			break;
		case 0x67:		//LD H,A               Copy A to H
			load(H, A);
			break;
		case 0x68:		//LD L,B               Copy B to L
			load(L, B);
			break;
		case 0x69:		//LD L,C               Copy C to L
			load(L, C);
			break;
		case 0x6A:		//LD L,D               Copy D to L
			load(L, D);
			break;
		case 0x6B:		//LD L,E               Copy E to L
			load(L, E);
			break;
		case 0x6C:		//LD L,H               Copy H to L
			load(L, H);
			break;
		case 0x6D:		//LD L,L               Copy L to L
			load(L, L);
			break;
		case 0x6E:		//LD L,(HL)            Copy value pointed by HL to L
			loadFromHL(L);
			break;
		case 0x6F:		//LD L,A               Copy A to L
			load(L, A);
			break;
		case 0x70:		//LD (HL),B            Copy B to address pointed by HL
			loadIntoHL(B);
			break;
		case 0x71:		//LD (HL),C            Copy C to address pointed by HL
			loadIntoHL(C);
			break;
		case 0x72:		//LD (HL),D            Copy D to address pointed by HL
			loadIntoHL(D);
			break;
		case 0x73:		//LD (HL),E            Copy E to address pointed by HL
			loadIntoHL(E);
			break;
		case 0x74:		//LD (HL),H            Copy H to address pointed by HL
			loadIntoHL(H);
			break;
		case 0x75:		//LD (HL),L            Copy L to address pointed by HL
			loadIntoHL(L);
			break;
		case 0x76:		//HALT                 Halt processor
			halt();
			break;
		case 0x77:		//LD (HL),A            Copy A to address pointed by HL
			loadIntoHL(A);
			break;
		case 0x78:		//LD A,B               Copy B to A
			load(A, B);
			break;
		case 0x79:		//LD A,C               Copy C to A
			load(A, C);
			break;
		case 0x7A:		//LD A,D               Copy D to A
			load(A, D);
			break;
		case 0x7B:		//LD A,E               Copy E to A
			load(A, E);
			break;
		case 0x7C:		//LD A,H               Copy H to A
			load(A, H);
			break;
		case 0x7D:		//LD A,L               Copy L to A
			load(A, L);
			break;
		case 0x7E:		//LD A,(HL)            Copy value pointed by HL to A
			loadFromHL(A);
			break;
		case 0x7F:		//LD A,A               Copy A to A
			load(A, A);
			break;
		case 0x80:		//ADD A,B              Add B to A
			add(B);
			break;
		case 0x81:		//ADD A,C              Add C to A
			add(C);
			break;
		case 0x82:		//ADD A,D              Add D to A
			add(D);
			break;
		case 0x83:		//ADD A,E              Add E to A
			add(E);
			break;
		case 0x84:		//ADD A,H              Add H to A
			add(H);
			break;
		case 0x85:		//ADD A,L              Add L to A
			add(L);
			break;
		case 0x86:		//ADD A,(HL)           Add value pointed by HL to A
			addHL();
			break;
		case 0x87:		//ADD A,A              Add A to A
			add(A);
			break;
		case 0x88:		//ADC A,B              Add B and carry flag to A
			addWithCarry(B);
			break;
		case 0x89:		//ADC A,C              Add C and carry flag to A
			addWithCarry(C);
			break;
		case 0x8A:		//ADC A,D              Add D and carry flag to A
			addWithCarry(D);
			break;
		case 0x8B:		//ADC A,E              Add E and carry flag to A
			addWithCarry(E);
			break;
		case 0x8C:		//ADC A,H              Add H and carry flag to A
			addWithCarry(H);
			break;
		case 0x8D:		//ADC A,L              Add and carry flag L to A
			addWithCarry(L);
			break;
		case 0x8E:		//ADC A,(HL)           Add value pointed by HL and carry flag to A
			addWithCarryHL();
			break;
		case 0x8F:		//ADC A,A              Add A and carry flag to A
			addWithCarry(A);
			break;
		case 0x90:		//SUB A,B              Subtract B from A
			subtract(B);
			break;
		case 0x91:		//SUB A,C              Subtract C from A
			subtract(C);
			break;
		case 0x92:		//SUB A,D              Subtract D from A
			subtract(D);
			break;
		case 0x93:		//SUB A,E              Subtract E from A
			subtract(E);
			break;
		case 0x94:		//SUB A,H              Subtract H from A
			subtract(H);
			break;
		case 0x95:		//SUB A,L              Subtract L from A
			subtract(L);
			break;
		case 0x96:		//SUB A,(HL)           Subtract value pointed by HL from A
			subtractHL();
			break;
		case 0x97:		//SUB A,A              Subtract A from A
			subtract(A);
			break;
		case 0x98:		//SBC A,B              Subtract B and carry flag from A
			subtractWithCarry(B);
			break;
		case 0x99:		//SBC A,C              Subtract C and carry flag from A
			subtractWithCarry(C);
			break;
		case 0x9A:		//SBC A,D              Subtract D and carry flag from A
			subtractWithCarry(D);
			break;
		case 0x9B:		//SBC A,E              Subtract E and carry flag from A
			subtractWithCarry(E);
			break;
		case 0x9C:		//SBC A,H              Subtract H and carry flag from A
			subtractWithCarry(H);
			break;
		case 0x9D:		//SBC A,L              Subtract and carry flag L from A
			subtractWithCarry(L);
			break;
		case 0x9E:		//SBC A,(HL)           Subtract value pointed by HL and carry flag from A
			subtractWithCarryHL();
			break;
		case 0x9F:		//SBC A,A              Subtract A and carry flag from A
			subtractWithCarry(A);
			break;
		case 0xA0:		//AND B                Logical AND B against A
			and(B);
			break;
		case 0xA1:		//AND C                Logical AND C against A
			and(C);
			break;
		case 0xA2:		//AND D                Logical AND D against A
			and(D);
			break;
		case 0xA3:		//AND E                Logical AND E against A
			and(E);
			break;
		case 0xA4:		//AND H                Logical AND H against A
			and(H);
			break;
		case 0xA5:		//AND L                Logical AND L against A
			and(L);
			break;
		case 0xA6:		//AND (HL)             Logical AND value pointed by HL against A
			andHL();
			break;
		case 0xA7:		//AND A                Logical AND A against A
			and(A);
			break;
		case 0xA8:		//XOR B                Logical XOR B against A
			xor(B);
			break;
		case 0xA9:		//XOR C                Logical XOR C against A
			xor(C);
			break;
		case 0xAA:		//XOR D                Logical XOR D against A
			xor(D);
			break;
		case 0xAB:		//XOR E                Logical XOR E against A
			xor(E);
			break;
		case 0xAC:		//XOR H                Logical XOR H against A
			xor(H);
			break;
		case 0xAD:		//XOR L                Logical XOR L against A
			xor(L);
			break;
		case 0xAE:		//XOR (HL)             Logical XOR value pointed by HL against A
			xorHL();
			break;
		case 0xAF:		//XOR A                Logical XOR A against A
			xor(A);
			break;
		case 0xB0:		//OR B                 Logical OR B against A
			or(B);
			break;
		case 0xB1:		//OR C                 Logical OR C against A
			or(C);
			break;
		case 0xB2:		//OR D                 Logical OR D against A
			or(D);
			break;
		case 0xB3:		//OR E                 Logical OR E against A
			or(E);
			break;
		case 0xB4:		//OR H                 Logical OR H against A
			or(H);
			break;
		case 0xB5:		//OR L                 Logical OR L against A
			or(L);
			break;
		case 0xB6:		//OR (HL)              Logical OR value pointed by HL against A
			orHL();
			break;
		case 0xB7:		//OR A                 Logical OR A against A
			or(A);
			break;
		case 0xB8:		//CP B                 Compare B against A
			compare(B);
			break;
		case 0xB9:		//CP C                 Compare C against A
			compare(C);
			break;
		case 0xBA:		//CP D                 Compare D against A
			compare(D);
			break;
		case 0xBB:		//CP E                 Compare E against A
			compare(E);
			break;
		case 0xBC:		//CP H                 Compare H against A
			compare(H);
			break;
		case 0xBD:		//CP L                 Compare L against A
			compare(L);
			break;
		case 0xBE:		//CP (HL)              Compare value pointed by HL against A
			compareHL();
			break;
		case 0xBF:		//CP A                 Compare A against A
			compare(A);
			break;
		case 0xC0:		//RET NZ               Return if last result was not zero
			returnIf(FZ, false);
			break;
		case 0xC1:		//POP BC               Pop 16-bit value from stack into BC
			pop(B, C);
			break;
		case 0xC2:		//JP NZ,nn             Absolute jump to 16-bit location if last result was not zero
			jump(FZ, false);
			break;
		case 0xC3:		//JP nn                Absolute jump to 16-bit location
			jump();
			break;
		case 0xC4:		//CALL NZ,nn           Call routine at 16-bit location if last result was not zero
			callIf(FZ, false);
			break;
		case 0xC5:		//PUSH BC              Push 16-bit BC onto stack
			push(B, C);
			break;
		case 0xC6:		//ADD A,n              Add 8-bit immediate to A
			addImmediate();
			break;
		case 0xC7:		//RST 0                Call routine at address 0000h
			restart(0x00);
			break;
		case 0xC8:		//RET Z                Return if last result was zero
			returnIf(FZ, true);
			break;
		case 0xC9:		//RET                  Return to calling routine
			returnUnconditional();
			break;
		case 0xCA:		//JP Z,nn              Absolute jump to 16-bit location if last result was zero
			jump(FZ, true);
			break;
		case 0xCB:		//Ext ops              Extended operations (two-byte instruction code)
			int op = data[PC++]  & 0xFF;
			System.out.println("extended: " + op);
			switch (op) {
			case 0x06:	//RLC (HL)				Rotate value pointed to by HL left with carry
				rotateHLLeftWithCarry();	
				break;
			case 0x0E:	//RRC (HL)				Rotate value pointed to by HL right with carry
				rotateHLRightWithCarry();
				break;
			case 0x16:	//RL (HL)				Rotate value pointed to by HL left
				rotateHLLeft();
				break;
			case 0x1E:	//RR (HL)				Rotate value pointed to by HL right
				rotateHLRight();
				break;
			case 0x26:	//SLA (HL)				Shift value pointed to by HL left preserving sign
				shiftHLLeftSign();
				break;
			case 0x2E:	//SRA (HL)				Shift value pointed to by HL right preserving sign
				shiftHLRightSign();
				break;
			case 0x36:	//SWAP (HL) 			Swap nybbles in value pointed to by HL
				swapNybblesHL();
				break;
			case 0x3E:	//SRL (HL)				Shift value pointed by HL right
				shiftHLRight();
				break;
			default:
				if 		(op <= 0x07) rotateLeftWithCarry	(secondaryReg[op & 0x0F]);
				else if (op <= 0x0F) rotateRightWithCarry	(secondaryReg[op & 0x0F]);
				else if (op <= 0x17) rotateLeft				(secondaryReg[op & 0x0F]);
				else if (op <= 0x1F) rotateRight			(secondaryReg[op & 0x0F]);
				else if (op <= 0x27) shiftLeftSign			(secondaryReg[op & 0x0F]);
				else if (op <= 0x2F) shiftRightSign			(secondaryReg[op & 0x0F]);
				else if (op <= 0x37) swapNybbles			(secondaryReg[op & 0x0F]);
				else if (op <= 0x3F) shiftRight				(secondaryReg[op & 0x0F]);
				else if (op <= 0x7F) testBit				((op - 0x40) / 8, secondaryReg[op & 0x0F]);
				else if (op <= 0xBF) resetBit				((op - 0x80) / 8, secondaryReg[op & 0x0F]);
				else if (op <= 0xFF) setBit					((op - 0xC0) / 8, secondaryReg[op & 0x0F]);
			}
			break;
		case 0xCC:		//CALL Z,nn            Call routine at 16-bit location if last result was zero
			callIf(FZ, true);
			break;
		case 0xCD:		//CALL nn              Call routine at 16-bit location
			call();
			break;
		case 0xCE:		//ADC A,n              Add 8-bit immediate and carry to A
			addWithCarryImmediate();
			break;
		case 0xCF:		//RST 8                Call routine at address 0008h
			restart(0x08);
			break;
		case 0xD0:		//RET NC               Return if last result caused no carry
			returnIf(FC, false);
			break;
		case 0xD1:		//POP DE               Pop 16-bit value from stack into DE
			pop(D, E);
			break;
		case 0xD2:		//JP NC,nn             Absolute jump to 16-bit location if last result caused no carry
			jump(FC, false);
			break;
		case 0xD3:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xD4:		//CALL NC,nn           Call routine at 16-bit location if last result caused no carry
			callIf(FC, false);
			break;
		case 0xD5:		//PUSH DE              Push 16-bit DE onto stack
			push(D, E);
			break;
		case 0xD6:		//SUB A,n              Subtract 8-bit immediate from A
			subtractImmediate();
			break;
		case 0xD7:		//RST 10               Call routine at address 0010h
			restart(0x10);
			break;
		case 0xD8:		//RET C                Return if last result caused carry
			returnIf(FC, true);
			break;
		case 0xD9:		//RETI                 Enable interrupts and return to calling routine
			returnEnableInterrupts();
			break;
		case 0xDA:		//JP C,nn              Absolute jump to 16-bit location if last result caused carry
			jump(FC, true);
			break;
		case 0xDB:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xDC:		//CALL C,nn            Call routine at 16-bit location if last result caused carry
			callIf(FC, true);
			break;
		case 0xDD:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xDE:		//SBC A,n              Subtract 8-bit immediate and carry from A
			subtractWithCarryImmediate();
			break;
		case 0xDF:		//RST 18               Call routine at address 0018h
			restart(0x18);
			break;
		case 0xE0:		//LDH (n),A            Save A at address pointed to by (FF00h + 8-bit immediate)
			loadFromAIntoImmediate();
			break;
		case 0xE1:		//POP HL               Pop 16-bit value from stack into HL
			pop(H, L);
			break;
		case 0xE2:		//LDH (C),A            Save A at address pointed to by (FF00h + C)
			loadFromAIntoC();
			break;
		case 0xE3:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xE4:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xE5:		//PUSH HL              Push 16-bit HL onto stack
			push(H, L);
			break;
		case 0xE6:		//AND n                Logical AND 8-bit immediate against A
			andImmediate();
			break;
		case 0xE7:		//RST 20               Call routine at address 0020h
			restart(0x20);
			break;
		case 0xE8:		//ADD SP,d             Add signed 8-bit immediate to SP
			addSPImmediate();
			break;
		case 0xE9:		//JP (HL)              Jump to 16-bit value pointed by HL
			jumpHL();
			break;
		case 0xEA:		//LD (nn),A            Save A at given 16-bit address
			loadAIntoAddress();
			break;
		case 0xEB:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xEC:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xED:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xEE:		//XOR n                Logical XOR 8-bit immediate against A
			xorImmediate();
			break;
		case 0xEF:		//RST 28               Call routine at address 0028h
			restart(0x28);
			break;
		case 0xF0:		//LDH A,(n)            Load A from address pointed to by (FF00h + 8-bit immediate)
			loadFromImmediateIntoA();
			break;
		case 0xF1:		//POP AF               Pop 16-bit value from stack into AF
			pop(A, F);
			break;
		case 0xF2:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xF3:		//DI                   Disable interrupts
			disableInterrupts();
			break;
		case 0xF4:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xF5:		//PUSH AF              Push 16-bit AF onto stack
			push(A, F);
			break;
		case 0xF6:		//OR n                 Logical OR 8-bit immediate against A
			orImmediate();
			break;
		case 0xF7:		//RST 30               Call routine at address 0030h
			restart(0x30);
			break;
		case 0xF8:		//LDHL SP,d            Add signed 8-bit immediate to SP and save result in HL
			loadHLSPImmediate();
			break;
		case 0xF9:		//LD SP,HL             Load HL to SP
			loadSPHL();
			break;
		case 0xFA:		//LD A,(nn)            Load A from given 16-bit address
			loadIntoAFromImmediate();
			break;
		case 0xFB:		//EI                   Enable interrupts
			enableInterrupts();
			break;
		case 0xFC:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xFD:		//XX                   Operation removed in this CPU
			notImplemented();
			break;
		case 0xFE:		//CP n                 Compare 8-bit immediate against A
			compareImmediate();
			break;
		case 0xFF:		//RST 38               Call routine at address 0038h
			restart(0x38);
			break;
		}
	}




	///////////////////////////////////
	// 8 bit loads
	///////////////////////////////////

	/** load register2 into register1 */
	private void load(int register1, int register2) {
		registerFile[register1] = registerFile[register2];
		cycles += 4;
	}

	/** load 8 bit immediate into given register */
	private void loadImmediate(int register) {
		registerFile[register] = data[PC++];
		cycles += 8;
	}

	/** loads value pointed to by (HL) into given register */
	private void loadFromHL(int register) {
		registerFile[register] = readByte(registerFile[H], registerFile[L]);
		cycles += 8;
	}

	/** load register into memory location (HL) */
	private void loadIntoHL(int register) {
		// TODO finish method
		cycles += 8;
	}

	/** load 8 bit immediate into memory location (HL) */
	private void loadIntoHLImmediate() {
		PC++;
		// TODO finish method
		cycles += 12;
	}

	/** load value pointed to by (register1 register2) into register A */
	private void loadIntoA(int register1, int register2) {
		registerFile[A] = readByte(register1, register2);
		cycles += 8;
	}

	/** load value pointed to by 16 bit immediate into register A */
	private void loadIntoAFromImmediate() {
		registerFile[A] = readByte(data[PC++], data[PC++]);
		cycles += 16;
	}

	/** load A into memory location (register1 register2) */
	private void loadFromA(int register1, int register2) {
		// TODO finish method
		cycles += 8;
	}

	/** load A into 16 bit immediate memory location */
	private void loadAIntoAddress() {
		// TODO finish method
		cycles += 16;
	}

	private void loadFromAIntoImmediate() {
		// TODO finish method
		PC++;
	}

	private void loadFromAIntoC() {
		// TODO finish method

	}

	/** Load A from address pointed to by (FF00h + 8-bit immediate) */
	private void loadFromImmediateIntoA() {
		// TODO finish method
		PC++;
	}

	/** load value pointed to by (HL) into A then increment HL */
	private void loadAHLIncrement() {
		// TODO finish method

	}

	/** load A into memory (HL), then increment HL */
	private void loadHLAIncrement() {
		// TODO finish method

	}

	/** load value pointed to by (HL) into A then decrement HL */
	private void loadAHLDecrement() {
		// TODO finish method

	}

	/** load A into memory (HL), then decrement HL */
	private void loadHLADecrement() {
		// TODO finish method

	}

	///////////////////////////////////
	// 16 bit loads
	///////////////////////////////////

	/** load 16 bit immediate into two given registers */
	private void loadImmediate(int register1, int register2) {
		// TODO finish method
		cycles += 12;
	}

	/** load 16 bit immediate into stack pointer */
	private void loadImmediateSP() {
		// TODO finish method
		cycles += 12;
	}

	/** load stack pointer into HL */
	private void loadSPHL() {
		// TODO finish method
		cycles += 8;
	}

	/** load stack pointer + 8 bit immediate into HL */
	private void loadHLSPImmediate() {
		// TODO finish method
		cycles += 12;
	}

	/** load stack-pointer to 16 bit immediate address */
	private void loadSPToAddress() {
		// TODO finish method
		cycles += 20;
	}

	private void push(int register1, int register2) {
		// TODO finish method
		cycles += 16;
	}

	private void pop(int register1, int register2) {
		// TODO finish method
		cycles += 12;
	}

	///////////////////////////////////
	// 8 bit ALU
	///////////////////////////////////

	/** adds the given register to register A */
	private void add(int register) {
		int a = registerFile[A];
		int b = registerFile[register];

		flags[FH] = (a & 0x0F) + (b & 0x0F) > 0x0F;
		a += b;
		flags[FC] = a > 255;
		flags[FN] = false;
		a &= 0xFF;
		flags[FZ] = a == 0;

		registerFile[A] = a;
		
		cycles += 4;
	}

	private void addHL() {
		cycles += 8;
		// TODO finish method
	}

	private void addImmediate() {
		// TODO finish method
		cycles += 8;
	}

	private void addWithCarry(int register) {
		cycles += 4;
		// TODO finish method
	}

	private void addWithCarryHL() {
		cycles += 8;
		// TODO finish method
	}

	private void addWithCarryImmediate() {
		// TODO finish method
		cycles += 8;
	}

	/** subtracts the given register from register A */
	private void subtract(int register) {
		// TODO finish method
		cycles += 4;
	}

	private void subtractHL() {
		// TODO finish method
		cycles += 8;
	}

	private void subtractImmediate() {
		// TODO finish method
		cycles += 8;
	}

	private void subtractWithCarry(int register) {
		// TODO finish method
		cycles += 4;
	}

	private void subtractWithCarryHL() {
		// TODO finish method
		cycles += 8;
	}

	private void subtractWithCarryImmediate() {
		// TODO finish method
		cycles += 8;
	}

	private void increment(int register) {
		cycles += 4;
		// TODO finish method
	}

	private void incrementHL() {
		// TODO finish method
		cycles += 12;
	}

	private void decrement(int register) {
		cycles += 4;
		// TODO finish method
	}

	private void decrementHL() {
		// TODO finish method
		cycles += 12;
	}

	///////////////////////////////////
	// 8 bit logic
	///////////////////////////////////

	private void and(int register) {
		// TODO finish method
		int a = registerFile[A] & registerFile[register];
		registerFile[A] = a;

		flags[FZ] = a == 0;
		flags[FN] = false;
		flags[FH] = true;
		flags[FC] = false;

		cycles += 4;
	}

	private void andHL() {
		cycles += 8;
		// TODO finish method
	}

	private void andImmediate() {
		// TODO finish method
		int a = registerFile[A] & data[PC++];
		registerFile[A] = a;

		flags[FZ] = a == 0;
		flags[FN] = false;
		flags[FH] = true;
		flags[FC] = false;

		cycles += 8;
	}

	private void or(int register) {
		// TODO finish method
		int a = registerFile[A] | registerFile[register];
		registerFile[A] = a;

		flags[FZ] = a == 0;
		flags[FN] = false;
		flags[FH] = false;
		flags[FC] = false;

		cycles += 4;
	}

	private void orHL() {
		cycles += 8;
		// TODO finish method
	}

	private void orImmediate() {
		// TODO finish method
		int a = registerFile[A] | data[PC++];
		registerFile[A] = a;

		flags[FZ] = a == 0;
		flags[FN] = false;
		flags[FH] = false;
		flags[FC] = false;

		cycles += 8;
	}

	private void xor(int register) {
		// TODO finish method
		int a = registerFile[A] ^ registerFile[register];
		registerFile[A] = a;

		flags[FZ] = a == 0;
		flags[FN] = false;
		flags[FH] = false;
		flags[FC] = false;

		cycles += 4;
	}

	private void xorHL() {
		cycles += 8;
		// TODO finish method
	}

	private void xorImmediate() {
		// TODO finish method
		int a = registerFile[A] ^ data[PC++];
		registerFile[A] = a;

		flags[FZ] = a == 0;
		flags[FN] = false;
		flags[FH] = false;
		flags[FC] = false;

		cycles += 8;
	}

	private void compare(int register) {
		cycles += 4;
		// TODO finish method
	}

	private void compareHL() {
		cycles += 8;
		// TODO finish method
	}

	private void compareImmediate() {
		// TODO finish method
		cycles += 8;
	}

	///////////////////////////////////
	// 16 bit ALU
	///////////////////////////////////

	private void addHL(int register1, int register2) {
		// TODO finish method
		cycles += 8;
	}

	private void addHLSP() {
		// TODO finish method
		cycles += 8;
	}

	private void addSPImmediate() {
		// TODO finish method
		cycles += 16;
	}

	private void increment(int register1, int register2) {
		cycles += 8;
		// TODO finish method
	}

	/** increments the stack-pointer */
	private void incrementSP() {
		// TODO finish method
		if (SP == 0xFF) SP = 0;
		else SP++;

		cycles += 8;
		cycles += 8;
	}


	private void decrement(int register1, int register2) {
		cycles += 8;
		// TODO finish method
	}

	/** decrements the stack-pointer */
	private void decrementSP() {
		// TODO finish method
		if (SP == 0) SP = 0xFFFF;
		else SP--;

		cycles += 8;
	}

	///////////////////////////////////
	// miscellaneous
	///////////////////////////////////

	private void decimalAdjustA() {
		// TODO finish method
		cycles += 4;
	}

	private void complementA() {
		// TODO finish method
		registerFile[A] = ~registerFile[A] & 0xFF;

		flags[FN] = true;
		flags[FH] = true;

		cycles += 4;
	}

	private void complimentCarryFlag() {
		// TODO finish method
		flags[FN] = false;
		flags[FH] = false;
		flags[FC] = !flags[FC];

		cycles += 4;
	}

	private void setCarryFlag() {
		// TODO finish method
		flags[FN] = false;
		flags[FH] = false;
		flags[FC] = true;

		cycles += 4;
	}

	private void nop() {
		// TODO finish method
		cycles += 4;
	}

	private void halt() {
		// TODO finish method
		cycles += 4;
	}

	private void stop() {
		// TODO finish method
		cycles += 4;
	}

	private void disableInterrupts() {
		// TODO finish method
		cycles += 4;
	}

	private void enableInterrupts() {
		// TODO finish method
		cycles += 4;
	}

	///////////////////////////////////
	// rotations and shifts
	///////////////////////////////////

	private void rotateLeft(int register) {
		// TODO finish method
	}

	private void rotateLeftWithCarry(int register) {
		// TODO finish method
	}

	private void rotateALeft() {
		// TODO finish method
		int a = registerFile[A];
		int highBit = (a >> 7);
		registerFile[A] = ((a << 1) & 0xFF) | highBit;

		flags[FZ] = registerFile[A] == 0;
		flags[FN] = false;
		flags[FH] = false;
		flags[FC] = highBit == 1;

		cycles += 4;
	}

	private void rotateALeftWithCarry() {
		// TODO finish method
		cycles += 4;
	}

	private void rotateHLLeft() {
		// TODO finish method

	}

	private void rotateHLLeftWithCarry() {
		// TODO finish method

	}

	private void rotateRight(int register) {
		// TODO finish method
	}

	private void rotateRightWithCarry(int register) {
		// TODO finish method
	}

	private void rotateARight() {
		// TODO finish method
		cycles += 4;
	}

	private void rotateARightWithCarry() {
		// TODO finish method
		cycles += 4;
	}

	private void rotateHLRight() {
		// TODO finish method

	}

	private void rotateHLRightWithCarry() {
		// TODO finish method

	}

	/** shifts the register left preserving sign */
	private void shiftLeftSign(int register) {
		// TODO finish method

	}

	private void shiftHLLeftSign() {
		// TODO finish method

	}

	/** shifts the register right preserving sign */
	private void shiftRightSign(int register) {
		// TODO finish method

	}

	/** shifts the given register right */
	private void shiftRight(int register) {
		// TODO finish method

	}

	private void shiftHLRight() {
		// TODO finish method

	}

	private void shiftHLRightSign() {
		// TODO finish method

	}



	///////////////////////////////////
	// bits
	///////////////////////////////////

	private void testBit(int bit, int register) {
		// TODO finish method

	}

	private void resetBit(int bit, int register) {
		// TODO finish method

	}

	private void setBit(int bit, int register) {
		// TODO finish method

	}


	/** swaps the nybbles of the given register */
	private void swapNybbles(int register) {
		// TODO finish method

	}

	private void swapNybblesHL() {
		// TODO finish method

	}

	///////////////////////////////////
	// Jumps
	///////////////////////////////////

	private void jump() {
		// TODO finish method
		cycles += 12;
	}

	private void jump(int flag, boolean condition) {
		// TODO finish method
		cycles += 12;
	}

	private void jumpRelative() {
		// TODO finish method
		cycles += 8;
	}

	private void jumpRelative(int flag, boolean condition) {
		// TODO finish method
		PC++;
		cycles += 8;
	}

	private void jumpHL() {
		// TODO finish method
		cycles += 4;
	}

	///////////////////////////////////
	// Calls
	///////////////////////////////////

	private void call() {
		// TODO finish method
		cycles += 12;
	}

	private void callIf(int flag, boolean condition) {
		// TODO finish method
		cycles += 12;
	}

	///////////////////////////////////
	// Returns
	///////////////////////////////////

	private void returnUnconditional() {
		// TODO finish method
		cycles += 8;
	}

	private void returnIf(int flag, boolean condition) {
		// TODO finish method
		cycles += 8;
	}

	private void returnEnableInterrupts() {
		// TODO finish method
		cycles += 8;
	}

	///////////////////////////////////
	// Restarts
	///////////////////////////////////

	private void restart(int address) {
		// TODO finish method
		cycles += 32;
	}


	///////////////////////////////////
	// Read byte/ word
	///////////////////////////////////

	private int readByte(int address) {
		return -1;
	}
	
	private int readByte(int hi, int lo) {
		return -1;
	}
	
	private int readWord(int address) {
		return -1;
	}
	
	private void writeByte(int address) {
		
	}

	/**
	 * Method called when an opcode was used for a command that doesn't exist. <p>
	 * Throws a RuntimeException with details.
	 */
	private void notImplemented() {
		throw new RuntimeException("operation not defined");
	}
	
	@Override
	public String toString() {
		int[] r = registerFile;
		boolean[] f = flags;
		return "A: " + r[A] + ", B: " + r[B] + ", C: " + r[C] + ", D: " + r[D] + ", E: " + r[E] + 
				", F: " + r[F] + ", H: " + r[H] + ", L: " + r[L] + ", PC: " + PC + ", SP: " + SP +
				", FZ: " + f[FZ] + ", FN: " + f[FN] + ", FH: " + f[FH] + ", FC: " + f[FC] + ", cycles: " + cycles; 
	}
}
