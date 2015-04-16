import java.util.regex.*;

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
    // doesn't throw exceptions (and in the other variables
    // we don't need to throw any exception)... so I don't know.
    public int toInt() /*throws LotusException*/ {
        Matcher intMatcher = Interpreter.intPattern.matcher(this.value);
        if (intMatcher.matches()) {
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
        Matcher fpMatcher = Interpreter.fpPattern.matcher(this.value);
        if (fpMatcher.matches()) {
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
