class StringVar extends Variable<String> {

    public StringVar(String value) {
        super(value);
    }

    public int toInt() {
        if (this.value.matches(Expression.intRegex)) {
            return Integer.parseInt(this.value);
        }
        else {
            System.out.println("Cannot convert to int");
            return 0; // throw Exception?
        }
    }

    public double toDouble() {
        if (this.value.matches(Expression.fpRegex)) {
            return Double.parseDouble(this.value);
        }
        else {
            System.out.println("Cannot convert to double");
            return 0.0; // throw Exception?
        }
    }

    public String toString() {
        return this.value;
    }
}
