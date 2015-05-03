/*******************************************************************************
Name: BoolVar.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class BoolVar of Lotus, a programming language based on Java.
             Implements the functionality specified by the Variable class
             for the Boolean type.
*******************************************************************************/

class BoolVar extends Variable<Boolean> {

    public BoolVar(Boolean value) {
        super(value);
    }

    public Variable inverted() {
        return new BoolVar(!this.value);
    }

    public Boolean toBool() {
        return this.value;
    }

    public Integer toInt() {
        if (this.toBool().equals(true)) {
            return new Integer(1);
        }
        else {
            return new Integer(0);
        }
    }

    public Double toDouble() {
        if (this.toBool().equals(true)) {
            return new Double(1.0);
        }
        else {
            return new Double(0.0);
        }
    }

    public Variable or(Variable other) {
        return new BoolVar(this.value || other.toBool());
    }

    // or
    public Variable plus(Variable other) {
        return this.or(other);
    }

    // nor
    public Variable minus(Variable other) {
        return this.or(other).inverted();
    }

    public Variable and(Variable other) {
        return new BoolVar(this.value && other.toBool());
    }

    // and
    public Variable times(Variable other) {
        return this.and(other);
    }

    // nand
    public Variable divided(Variable other) throws LotusException {
        return this.and(other).inverted();
    }

    // xnor
    public Variable mod(Variable other) throws LotusException {
        return this.pow(other).inverted();
    }

    // xor
    public Variable pow(Variable other) {
        return new BoolVar(this.value ^ other.toBool());
    }

    public Variable equals(Variable other) {
        return new BoolVar(this.value.equals(other.toBool()));
    }

    // <, <=, >= and > converts to int and then do the normal comparison
    public Variable lessThan(Variable other) {
        return this.toIntVar().lessThan(other.toIntVar());
    }

    public Variable lessEquals(Variable other) {
        return ((BoolVar)this.lessThan(other)).or(this.equals(other));
    }

    public Variable greaterThan(Variable other) {
        return this.toIntVar().greaterThan(other.toIntVar());
    }

    public Variable greaterEquals(Variable other) {
        return ((BoolVar)this.greaterThan(other)).or(this.equals(other));
    }
}
