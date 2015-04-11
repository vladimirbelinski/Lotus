import java.util.*;

class Interpreter {
	HashMap<String, Variable> vars;

	public Interpreter() {
		vars = new HashMap<String, Variable>();
	}

	public void newVar(String n, Variable v) {
		this.vars.put(n, v);
	}

	public boolean hasVar(String name) {
		return this.vars.containsKey(name);
	}

	public Variable getVar(String name) {
		return this.vars.get(name);
	}

	public void setVar(String name, Variable other) throws LotusException {
		Variable v = this.getVar(name);

		if (v != null && other != null) {
			if (v.getClass().equals(other.getClass())) {
				if (v instanceof IntVar) {
					this.setVar(name, (Integer)other.value);
				}
				else if (v instanceof BoolVar) {
					this.setVar(name, (Boolean)other.value);
				}
				else if (v instanceof DoubleVar) {
					this.setVar(name, (Double)other.value);
				}
				else if (v instanceof StringVar) {
					this.setVar(name, (String)other.value);
				}
			}
			else {
				throw new LotusException("Cannot assign " + other.getClass() + " to a " + v.getClass() + " variable");
			}
		}
		else if (v == null){
			throw new LotusException("Cannot find variable: " + name);
		}
		else if (other == null) {
			throw new LotusException("Assignment of null");
		}
		else {
			throw new LotusException("Assigning null to null!?");
		}
	}

	public void setVar(String name, Integer value) throws LotusException {
		Variable v = this.getVar(name);

		if (v instanceof IntVar) {
			((IntVar)v).setValue(value);
		}
		else if (v == null) {
			throw new LotusException("Cannot find variable: " + name);
		}
		else {
			throw new LotusException("Cannot assign an int to a non-int variable");
		}
	}

	public void setVar(String name, Boolean value) throws LotusException {
		Variable v = this.getVar(name);

		if (v instanceof BoolVar) {
			((BoolVar)v).setValue(value);
		}
		else if (v == null) {
			throw new LotusException("Cannot find variable: " + name);
		}
		else {
			throw new LotusException("Cannot assign a boolean to a non-bool variable");
		}
	}

	public void setVar(String name, Double value) throws LotusException {
		Variable v = this.getVar(name);

		if (v instanceof DoubleVar) {
			((DoubleVar)v).setValue(value);
		}
		else if (v == null) {
			throw new LotusException("Cannot find variable: " + name);
		}
		else {
			throw new LotusException("Cannot assign a double to a non-double variable");
		}
	}

	public void setVar(String name, String value) throws LotusException {
		Variable v = this.getVar(name);

		if (v instanceof StringVar) {
			((StringVar)v).setValue(value);
		}
		else if (v == null) {
			throw new LotusException("Cannot find variable: " + name);
		}
		else {
			throw new LotusException("Cannot assign a string to a non-string variable");
		}
	}

	/* ---------------------------------------------------------------------- */

	public void execute(String line) throws LotusException {
		String[] atr;

		if (line.matches(Variable.declRegex)) {
			try {
				this.let(line);
			} catch(LotusException e) {
				throw e;
			}
		}
		else if (line.matches(atrRegex)) {
			atr = line.split(stripAtrRegex);
			// gets only the name of the variable being assigned to:
			// String vname = line.split(stripNameRegex)[0];
			// gets that variable and calls assignment() on it
			// passing the string after the '=' token
			// Variable v = this.getVar(vname);
			Variable v = this.getVar(atr[0]);

			if (v != null) {
				try {
					v.assign(atr[0], atr[1]);
				} catch (LotusException e) {
					throw e;
				}
			}
			else {
				throw new LotusException("Could not find variable " + atr[0]);
			}
		}
		else if (line.matches(printRegex)) {
			try {
				this.print(line);
			} catch (LotusException e) {
				throw e;
			}
		}
		else {
			throw new LotusException("Syntax error!");
		}
	}

	private void let(String line) throws LotusException {
		String[] decl = Variable.fixDecl(line);
		int i = decl.length - 1;
		String type = decl[i];
		Variable v = null;

		i--; // decl[i] is the type
		while (i >= 0) {
			switch (type) {
				case "bool":
				v = new BoolVar(false);
				break;

				case "int":
				v = new IntVar(0);
				break;

				case "double":
				v = new DoubleVar(0.0);
				break;

				case "string":
				v = new StringVar("");
				break;
			}
			if (v != null) {
				this.newVar(decl[i], v);
				v = null; // prevents from adding the same variable again
			}
			else {
				throw new LotusException("Invalid type " + type);
			}
			i--;
		}
	}

	// tem que refazer essa função!
	// tratar \n, \$, print($x$)...
	private void print(String line) throws LotusException {
		String var;
		String[] ss, ts;
		Variable v = null;
		int i, j, ind, offset;
		String lineEnding = line.substring(line.lastIndexOf(")"));

		line = line.replaceFirst("(print)( )*\\(", "");
		line = line.replace(lineEnding, "");
		System.out.println(line);
		ss = line.split("\\$(\\w)+\\$");
		ts = line.split(" ");

		j = 0;
		for (i = 0; i < ss.length; i++) {
			System.out.print(ss[i]);

			for (j++; j < ts.length; j++) {
				if (ts[j].matches(printVarRegex)) {
					ind = ts[j].indexOf("$") + 1;
					offset = ts[j].indexOf("$", ind);
					var = ts[j].substring(ind, offset);

					v = this.getVar(var);
					if (v != null) {
						System.out.print(v);
						v = null;
					}
					else {
						throw new LotusException("Could not find variable " + var);
					}
					break;
				}
			}
		}
		System.out.println(); // tratar se é pra printar \n
	}

	public static final String atrRegex = "(\\w)+( )*=( )*.+;";
	public static final String stripAtrRegex = "( )*=( )*";
	public static final String printRegex = "(print)( )*\\(.*\\)( )*;";
	public static final String printVarRegex = ".*(\\$(\\w)+\\$).*";
	// public static final String stripNameRegex = "( )*=( )*.+;";
	// public static final String stripExpRegex = "(\\w)+( )*=( )*";
}
