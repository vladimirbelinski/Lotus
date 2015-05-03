class Line {
	private final String line;
	private final int number;

	public Line(String line, int number) {
		this.line = line;
		this.number = number;
	}

	public String toString() {
		return this.line;
	}

	public int getNumber() {
		return this.number;
	}
}
