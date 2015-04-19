import java.util.regex.*;

class StringVar extends Variable<String> {

    public StringVar(String value) {
        super(value);
    }

    public Variable inverted() {
        Matcher fpMatcher = Interpreter.fpPattern.matcher(this.value);
        Matcher boolMatcher = Interpreter.boolPattern.matcher(this.value);
        Matcher upperCaseMatcher = Interpreter.upperCasePattern.matcher(this.value);

        if (fpMatcher.matches()) {
            return new StringVar("-" + this.value);
        }
        else if (boolMatcher.matches()) {
            if (this.value.equals("true")) {
                return new StringVar("false");
            }
            else {
                return new StringVar("true");
            }
        }
        else if (upperCaseMatcher.matches()) {
            return new StringVar(this.value.toLowerCase());
        }
        else {
            return new StringVar(this.value.toUpperCase());
        }
    }

    public Integer toInt() {
        Matcher intMatcher = Interpreter.intPattern.matcher(this.value);
        if (intMatcher.matches()) {
            return Integer.valueOf(this.value);
        }
        else {
            return new Integer(this.value.length());
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

    public Double toDouble() {
        Matcher fpMatcher = Interpreter.fpPattern.matcher(this.value);
        if (fpMatcher.matches()) {
            return Double.valueOf(this.value);
        }
        else {
            return new Double(this.value.length());
        }
    }

    // appending
    public Variable plus(Variable other) {
        return new StringVar(this.value + other.toString());
    }

    // replaces first occurrence
    public Variable minus(Variable other) {
        return new StringVar(this.value.replaceFirst(other.toString(), ""));
    }

    // adds other.toString() between every char
    public Variable times(Variable other) {
        return new StringVar(this.value.replaceAll("", other.toString()));
    }

    // removes all other.toString() occurrences from this.toString()
    public Variable divided(Variable other) throws LotusException {
        return new StringVar(this.value.replaceAll(other.toString(), ""));
    }

    // removes all this.toString() occurrences from other.toString()
    public Variable mod(Variable other) throws LotusException {
        return new StringVar(other.toString().replaceAll(this.toString(), ""));
    }

    // cannot do a pow
    public Variable pow(Variable other) throws LotusException {
        throw new LotusException("stringPow", this.toString());
    }

    // && and || convert the String to Boolean and then check
    public Variable and(Variable other) {
        return new BoolVar(this.toBool() && other.toBool());
    }

    public Variable or(Variable other) {
        return new BoolVar(this.toBool() || other.toBool());
    }

    public Variable equals(Variable other) {
        return new BoolVar(this.value.equals(other.toString()));
    }

    // checks if this comes lexicographically before other
    public Variable lessThan(Variable other) {
        return new BoolVar(this.value.compareTo(other.toString()) < 0);
    }

    public Variable lessEquals(Variable other) {
        return ((BoolVar)this.lessThan(other)).or(this.equals(other));
    }

    // checks if this comes lexicographically after other
    public Variable greaterThan(Variable other) {
        return new BoolVar(this.value.compareTo(other.toString()) > 0);
    }

    public Variable greaterEquals(Variable other) {
        return ((BoolVar)this.greaterThan(other)).or(this.equals(other));
    }
}
