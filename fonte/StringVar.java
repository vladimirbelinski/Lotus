package fonte;
/*******************************************************************************
Name: StringVar.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class StringVar of Lotus, a programming language based on Java.
             Implements the functionality specified by the Variable class
             for the String type.
*******************************************************************************/
import java.util.regex.*;

class StringVar extends Variable<String> {

    public StringVar(String value) {
        super(value);
    }

    public void setValue(Variable other) {
        this.value = other.toString();
    }

    public Variable inverted() {
        Matcher upperCaseM = Interpreter.upperCaseP.matcher(this.value);

        if (upperCaseM.matches()) {
            return new StringVar(this.value.toLowerCase());
        }
        else {
            return new StringVar(this.value.toUpperCase());
        }
    }

    public Integer toInt() {
        return new Integer(this.value.length());
    }

    public Boolean toBool() {
        if (!this.value.isEmpty() && this.value.equalsIgnoreCase("true")) {
            return new Boolean(true);
        }
        else {
            return new Boolean(false);
        }
    }

    public Double toDouble() {
        Matcher fpM = Interpreter.fpP.matcher(this.value);
        if (fpM.matches()) {
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

    public Variable pow(Variable other) {
        if (other instanceof StringVar) {
            return new IntVar(this.value.indexOf(other.toString()));
        }
        else {
            int i = other.toInt() % this.value.length();
            return new StringVar(this.value.substring(i, i + 1));
        }
    }

    public Variable equals(Variable other) {
        return new BoolVar(this.value.equals(other.toString()));
    }

    // checks if this comes lexicographically before other
    public Variable lessThan(Variable other) {
        return new BoolVar(this.value.compareTo(other.toString()) < 0);
    }

    public Variable lessEquals(Variable other) {
        return this.lessThan(other).or(this.equals(other));
    }

    // checks if this comes lexicographically after other
    public Variable greaterThan(Variable other) {
        return new BoolVar(this.value.compareTo(other.toString()) > 0);
    }

    public Variable greaterEquals(Variable other) {
        return this.greaterThan(other).or(this.equals(other));
    }
}
