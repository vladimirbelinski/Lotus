import java.util.regex.*;

class StringVar extends Variable<String> {

    public StringVar(String value) {
        super(value);
    }

    // what should we do here? Flip all the chars?
    public Variable inverted() {
        return null;
    }

    // cannot throw exception because this method
    // should override the one from Variable. But that one
    // doesn't throw exceptions (and in the other variables
    // we don't need to throw any exception)... so I don't know.
    public Integer toInt() /*throws LotusException*/ {
        Matcher intMatcher = Interpreter.intPattern.matcher(this.value);
        if (intMatcher.matches()) {
            return Integer.valueOf(this.value);
        }
        else {
            return new Integer(0);
            //throw new LotusException("Cannot convert \"" + this.value + "\" to int");
        }
    }

    public Boolean toBool() {
        if (this.value.isEmpty() || this.value.equals("false")) {
            return new Boolean(false);
        }
        else {
            return new Boolean(true);
        }
    }

    public Double toDouble()/* throws LotusException*/ {
        Matcher fpMatcher = Interpreter.fpPattern.matcher(this.value);
        if (fpMatcher.matches()) {
            return Double.valueOf(this.value);
        }
        else {
            return new Double(0.0);
            //throw new LotusException("Cannot convert \"" + this.value + "\" to double");
        }
    }

    public Variable equals(Variable other) {
        return new BoolVar(this.value.equals(other.toString()));
    }
}
