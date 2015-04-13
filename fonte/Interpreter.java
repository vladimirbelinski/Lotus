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
				throw new LotusException("Cannot assign a " + other.getClass() + " value to a " + v.getClass() + " variable");
			}
		}
		else if (v == null){
			throw new LotusException("Could not find variable: \"" + name + "\"");
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
			throw new LotusException("Could not find variable: \"" + name + "\"");
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
			throw new LotusException("Could not find variable: \"" + name + "\"");
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
			throw new LotusException("Could not find variable: \"" + name + "\"");
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
			throw new LotusException("Could not find variable: \"" + name + "\"");
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
			// gets that variable and calls assignment() on it
			// passing the string after the '=' token
			Variable v = this.getVar(atr[0]);

			if (v != null) {
				try {
					v.assign(atr[0], atr[1]);
				} catch (LotusException e) {
					throw e;
				}
			}
			else {
				throw new LotusException("Could not find variable \"" + atr[0] + "\"");
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
			throw new LotusException("Unknown expression \"" + line + "\"");
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
				throw new LotusException("Invalid type \"" + type + "\"");
			}
			i--;
		}
	}

	private void print(String line) throws LotusException {
		int i, offset;
		String[] content;
		String text = "";
		Variable v = null;
		String lineEnding;

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");
		if (line.startsWith("println")) {
			line += "\n";
		}

		line = line.replaceFirst("(print|println)( )*\\(", "");
		// substitui todas as substrings "\n" pelo próprio caractere
		// '\n', que representa uma quebra de linha.
		line = line.replaceAll("\\\\n", "\n");
		// Por que todas essas '\'? Porque '\' é "escape" em regex
		// e em Java:
		// Pra printar "\n", fazemos: System.out.println("\\n");
		// Desse modo, em regex junto com Java, precisa-se de "\\\\n",
		// porque "\\" == '\' da regex, "\\" = '\' pro Java e
		// enfim o 'n' pra então formar um "\\n" que é a string
		// que representa uma string "\n".
		// Agora, substitui todas as substrings "\\n" (nesse caso \n é o
		// caracter quebra de linha que foi substituído acima) pela
		// substring "\n". Desse modo, onde há "\\n", iremos printar
		// "\n", de fato. E onde há apenas "\n", printa uma quebra de linha.
		line = line.replaceAll("\\\\\n", "\\\\n");
		// Agora precisa-se de MAIS UMA '\' porque queremos capturar o padrão
		// "\\n", sendo que '\n' é a quebra de linha. Como a regex pra "\n"
		// é "\\\\n" e queremos uma '\' na frente, fica "\\\\\n".
		// SIM, ISSO BUGA MEU CÉREBRO :)
		content = line.split("");

		for (i = 0; i < content.length; i++) {
			if (content[i].equals("$")) {
				// i is the index of the first '$'!
				try {
					v = this.varToPrint(line, i);
					if (v != null) {
						text += v.toString();
						i = line.indexOf("$", i + 1);
					}
					else {
						text += content[i];
					}
				} catch (LotusException e) {
					throw e;
				}
			}
			/*else if (content[i].equals("\\") && i + 1 < content.length && content[i + 1].equals("n")) {
				text += "\n";
				i++;
			}*/
			else {
				text += content[i];
			}
		}
		System.out.print(text);
	}

	private Variable varToPrint(String content, int fromIndex) throws LotusException {
		String name;
		Variable var = null;
		int offset = content.indexOf("$", fromIndex + 1);

		if (offset > fromIndex) {
			name = content.substring(fromIndex + 1, offset);
			if (name.matches(Variable.nameRegex)) {
				var = this.getVar(name);
				if (var == null) {
					throw new LotusException("Could not find variable \"" + name + "\"");
				}
			}
		}

		return var;
	}

	public static final String atrRegex = "(\\w)+( )*=( )*.+;";
	public static final String stripAtrRegex = "( )*=( )*";
	public static final String printRegex = "(print|println)( )*\\(.*\\)( )*;";
	public static final String printVarRegex = ".*(\\$(\\w)+\\$).*";
	// public static final String stripNameRegex = "( )*=( )*.+;";
	// public static final String stripExpRegex = "(\\w)+( )*=( )*";
}
