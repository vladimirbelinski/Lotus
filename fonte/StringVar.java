class StringVar extends Variable<String> {

    public StringVar(String value) {
        super(value);
    }

    // what should we do here? Flip all the chars?
    public void invert() {
        return;
    }

    // cannot throw exception because this method
    // should override the one from Variable. But that one
    // doesn't throw exceptions... (and in the other variables)
    // we don't need it... so I don't know.
    public int toInt() /*throws LotusException*/ {
        if (this.value.matches(Interpreter.intRegex)) {
            return Integer.parseInt(this.value);
        }
        else {
            return 0;
            //throw new LotusException("Cannot convert \"" + this.value + "\" to int");
        }
    }

    public boolean toBool() {
        if (this.value.isEmpty() || this.value.equals("false")) {
            return false;
        }
        else {
            return true;
        }
    }

    public double toDouble()/* throws LotusException*/ {
        if (this.value.matches(Interpreter.fpRegex)) {
            return Double.parseDouble(this.value);
        }
        else {
            return 0.0;
            //throw new LotusException("Cannot convert \"" + this.value + "\" to double");
        }
    }

    public boolean equals(Object value) {
        return this.value.equals(value);
    }
}
