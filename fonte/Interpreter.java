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

	public boolean setVar(String name, Variable other) {
		boolean success = false;
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
				success = true;
			}
		}
		else if (v == null){
			System.out.println("Cannot find variable: " + name);
		    // throw Exception?
		}
		else if (other != null) {
			System.out.println("Incompatible assignment of types " + v.getClass() + " and " + v.getClass());
		    // throw Exception
		}
		else {
			System.out.println("Incompatible assignment");
			// throw Exception
		}

		return success;
	}

	public void setVar(String name, Integer value) {
		Variable v = this.getVar(name);

		if (v instanceof IntVar) {
			((IntVar)v).setValue(value);
		}
		else if (v == null) {
			System.out.println("Cannot find variable: " + name);
		    // throw Exception?
		}
		else {
			System.out.println("Variable " + name + " is not Integer");
		    // throw Exception?
		}
	}

	public void setVar(String name, Boolean value) {
		Variable v = this.getVar(name);

		if (v instanceof BoolVar) {
			((BoolVar)v).setValue(value);
		}
		else if (v == null) {
			System.out.println("Cannot find variable: " + name);
		    // throw Exception?
		}
		else {
			System.out.println("Variable " + name + " is not Boolean");
		    // throw Exception?
		}
	}

	public void setVar(String name, Double value) {
		Variable v = this.getVar(name);

		if (v instanceof DoubleVar) {
			((DoubleVar)v).setValue(value);
		}
		else if (v == null) {
			System.out.println("Cannot find variable: " + name);
		    // throw Exception?
		}
		else {
			System.out.println("Variable " + name + " is not Double");
		    // throw Exception?
		}
	}

	public void setVar(String name, String value) {
		Variable v = this.getVar(name);

		if (v instanceof StringVar) {
			((StringVar)v).setValue(value);
		}
		else if (v == null) {
			System.out.println("Cannot find variable: " + name);
		    // throw Exception?
		}
		else {
			System.out.println("Variable " + name + " is not String");
		    // throw Exception?
		}
	}

	/* ---------------------------------------------------------------------- */

	public void execute(String line) {
		line = line.trim();
		//String[] t = line.split(" ");

		if (line.matches("\\-{2}.*")) {
			return; // a line comment
		}
		else if (line.matches("\\/\\-.*")) {
			// it's a block comment
		}
		else if (line.matches("(\\w)+( )*=( )*.+;")/* && this.vars.containsKey(t[0])*/) {
			System.out.println("Variable assignment, for example");
			// gets only the name of the variable being assigned to
			String vname = line.split("( )*=( )*.+;")[0];
			// gets that variable and calls assignment() on it
			// passing the string after the '=' token
			Variable v = this.getVar(vname);

			if (v != null) {
				v.assign(vname, line.split("(\\w)+( )*=( )*")[1]);
				//v.setValue(vname, result); // tratar o tipo... retorno?
			}
			else {
				System.out.println("Could not find variable " + vname);
				// throw Exception
			}
		}
		else if (line.matches(Variable.declRegex)) {
			this.let(line);
		}
		/*else {
			switch(t[0]) {
				case "fn":
				break;

				case "let":
				this.let(line);
				break;

				case "if":
				break;

				case "elsif":
				break;

				case "else":
				break;

				case "for":
				break;

				case "while":
				break;
			}
		}*/
	}

	private void let(String line) {
		String[] decl = Variable.fixDecl(line);
		int i = decl.length - 1;
		String type = decl[i];
		Variable v = null;

		i--; // decl[i] is the type
		while (i >= 0) {
			switch (type) {
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
			// else throws Exception
			i--;
		}
	}
}
