package fonte;
/*******************************************************************************
Name: Variable.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class Variable of Lotus, a programming language based on Java.
             Specifies what a variable must have, be and do.
*******************************************************************************/
import java.util.*;

abstract class Variable<T> {
    public T value;

    public Variable(T value) {
        this.value = value;
    }

    public Class type() {
        return this.value.getClass();
    }

    public String toString() {
        return this.value.toString();
    }

    public BoolVar toBoolVar() {
        return new BoolVar(this.toBool());
    }

    public IntVar toIntVar() {
        return new IntVar(this.toInt());
    }

    public DoubleVar toDoubleVar() {
        return new DoubleVar(this.toDouble());
    }

    public StringVar toStringVar() {
        return new StringVar(this.toString());
    }

    public Variable and(Variable other) {
        return this.toBoolVar().and(other);
    }

    public Variable or(Variable other) {
        return this.toBoolVar().or(other);
    }

    public abstract Integer toInt();
    public abstract Boolean toBool();
    public abstract Double toDouble();
    public abstract Variable inverted();
    public abstract void setValue(Variable other);
    public abstract Variable plus(Variable other);
    public abstract Variable minus(Variable other);
    public abstract Variable times(Variable other);
    public abstract Variable pow(Variable other);
    public abstract Variable divided(Variable other) throws LotusException;
    public abstract Variable mod(Variable other) throws LotusException;
    public abstract Variable equals(Variable other);
    public abstract Variable lessThan(Variable other);
    public abstract Variable lessEquals(Variable other);
    public abstract Variable greaterThan(Variable other);
    public abstract Variable greaterEquals(Variable other);
}
