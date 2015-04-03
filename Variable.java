public class Variable<Type, Value> {
    private final Type type;
    public Value value;

    public Variable(Type t, Value v) {
        this.type = t;
        this.value = v;
    }

    public Type type() {
        return this.type;
    }

    public String toString() {
        return this.type + " " + this.value;
    }
}
