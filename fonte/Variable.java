import java.util.*;

class Variable<T> {
    public T value;

    public Variable(T value) {
        this.value = value;
    }

    public Class type() {
        return this.value.getClass();
    }

    public double toDouble() {
        if (this.value instanceof Double) {
            return ((Double)this.value).doubleValue();
        }
        else if (this.value instanceof Integer) {
            return ((Integer)this.value).doubleValue();
        }
        else {
            System.out.println("Cannot convert " + this.type() + " to double");
            return 0.0; // throw Exception
        }
    }

    public int toInt() {
        if (this.value instanceof Double) {
            return ((Double)this.value).intValue();
        }
        else if (this.value instanceof Integer) {
            return ((Integer)this.value).intValue();
        }
        else {
            System.out.println("Cannot convert " + this.type() + " to int");
            return 0; // throw Exception
        }
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    // remember the Arrays!
    public static String[] fix(String line) {
        // takes ou "let" and removes all trailing spaces
        line = line.replace("let", "").trim();
        if (!line.matches(varRegex)) {
            System.out.println("Syntax error!");
            return null; // throw Exception
        }

        int i;
        String var = new String("");
        String[] t = line.split("");
        ArrayList<String> tokens = new ArrayList<String>();

        for (i = 0; i < t.length && !t[i].equals(":"); i++) {
            if (t[i].matches("\\w")) {
                var += t[i];
            }
            else if (var != "" && (t[i].equals(" ") || t[i].equals(","))) {
                tokens.add(var);
                var = "";
            }
        }
        // the last var left before ":"
        if (var != "") {
            tokens.add(var);
            var = "";
        }

        // type
        while (i < t.length) {
            if (t[i].matches("\\w")) {
                var += t[i];
            }
            i++;
        }

        if (var.matches(typeRegex)) {
            tokens.add(var);
        }
        else {
            System.out.println("Syntax error!");
            return null; // throw Exception
        }

        t = new String[tokens.size()];
        tokens.toArray(t);

        return t;
    }

    private static final String varRegex = "((\\w)+( )*(,( )*(\\w)+)*)( )*\\:( )*(\\w)+( )*\\;";
    private static final String typeRegex = "int|double|string";
}
