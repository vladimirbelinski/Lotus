package fonte;
/*******************************************************************************
Name: Line.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class Line of Lotus, a programming language based on Java.
             Helps the localization of a line when an error occurs on it.
*******************************************************************************/

class Line {
	private final String line;
	private final int number;

	public Line(String line, int number) {
		this.line = line.trim();
		this.number = number;
	}

	public String toString() {
		return this.line;
	}

	public int getNumber() {
		return this.number;
	}
}
