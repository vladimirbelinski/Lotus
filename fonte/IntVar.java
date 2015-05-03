/*******************************************************************************
Name: IntVar.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class IntVar of Lotus, a programming language based on Java.
             Implements the functionality specified by the Variable class
             for the Integer type.
*******************************************************************************/
class IntVar extends Variable<Integer> {

    public IntVar(Integer value) {
        super(value);
    }

    public Variable inverted() {
        return new IntVar(-this.value);
    }

    public Integer toInt() {
        return this.value;
    }

    public Boolean toBool() {
        if (this.value.equals(0)) {
            return new Boolean(false);
        }
        else {
            return new Boolean(true);
        }
    }

    public Double toDouble() {
        return new Double(this.value.doubleValue());
    }

    public Variable plus(Variable other) {
        if (other instanceof IntVar) {
            return new IntVar(this.value + other.toInt());
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().plus(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().plus(other);
        }
        else {
            return this.toStringVar().plus(other);
        }
    }

    public Variable minus(Variable other) {
        if (other instanceof IntVar) {
            return new IntVar(this.value - other.toInt());
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().minus(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().minus(other);
        }
        else {
            return this.toStringVar().minus(other);
        }
    }

    public Variable times(Variable other) {
        if (other instanceof IntVar) {
            return new IntVar(this.value * other.toInt());
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().times(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().times(other);
        }
        else {
            return this.toStringVar().times(other);
        }
    }

    public Variable divided(Variable other) throws LotusException {
        if (other instanceof IntVar) {
            if (!other.value.equals(0)) {
                return new IntVar(this.value / other.toInt());
            }
            else {
                throw new LotusException("divisionByZero", this + " / " + other);
            }
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().divided(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().divided(other);
        }
        else {
            return this.toStringVar().divided(other);
        }
    }

    public Variable mod(Variable other) throws LotusException {
        if (other instanceof IntVar) {
            if (!other.value.equals(0)) {
                return new IntVar(this.value % other.toInt());
            }
            else {
                throw new LotusException("divisionByZero", this.toString() + " % " + other.toString());
            }
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().mod(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().mod(other);
        }
        else {
            return this.toStringVar().mod(other);
        }
    }

    public Variable pow(Variable other) {
        if (other instanceof IntVar) {
            return new IntVar((int)Math.pow(this.toDouble(), other.toDouble()));
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().pow(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().pow(other);
        }
        else {
            return this.toStringVar().pow(other);
        }
    }

    public Variable equals(Variable other) {
        if (other instanceof IntVar) {
            return new BoolVar(this.value.equals(other.toInt()));
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().equals(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().equals(other);
        }
        else {
            return this.toStringVar().equals(other);
        }
    }

    public Variable lessThan(Variable other) {
        if (other instanceof IntVar) {
            return new BoolVar(this.value < other.toInt());
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().lessThan(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().lessThan(other);
        }
        else {
            return this.toStringVar().lessThan(other);
        }
    }

    public Variable lessEquals(Variable other) {
        if (other instanceof IntVar) {
            return new BoolVar(this.value <= other.toInt());
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().lessEquals(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().lessEquals(other);
        }
        else {
            return this.toStringVar().lessEquals(other);
        }
    }

    public Variable greaterThan(Variable other) {
        if (other instanceof IntVar) {
            return new BoolVar(this.value > other.toInt());
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().greaterThan(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().greaterThan(other);
        }
        else {
            return this.toStringVar().greaterThan(other);
        }
    }

    public Variable greaterEquals(Variable other) {
        if (other instanceof IntVar) {
            return new BoolVar(this.value >= other.toInt());
        }
        else if (other instanceof DoubleVar) {
            return this.toDoubleVar().greaterEquals(other);
        }
        else if (other instanceof BoolVar) {
            return this.toBoolVar().greaterEquals(other);
        }
        else {
            return this.toStringVar().greaterEquals(other);
        }
    }
}
