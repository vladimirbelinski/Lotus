public class Variable/*<Type, Value>*/ {
    /*private final Type type;
    public Value value;*/
    private final String type;
    public Double value;

    //public Variable(Type t, Value v) {
    public Variable(Double v) {
        //this.type = t;
        this.value = new Double(v);
        this.type = "Double";
    }

    //public Type type() {
    public String type() {
        return this.type;
    }

    public String toString() {
        return this.type + ", " + this.value;
    }
}
