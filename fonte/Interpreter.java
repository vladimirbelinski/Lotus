import java.util.*;
import java.util.regex.*;

class Interpreter {
	private HashMap<String, Variable> vars;
	// this doesn't make that much sense now, but it's faster to
	// look up in a hash than an array. And later on we can replace
	// the boolean value to a Runnable...
	private static final Map<String, Boolean> reservedWords = mapReservedWords();
	private static boolean patternsInitd = false;
	public static Pattern typeP, wholeDeclP, varNameP, atrP, wholeAtrP, semicP, wholePrintP, wholeScanP, wholeScanlnP, cutPrintP, cutScanP, scanContentP, wholeOpP, signP, intP, fpP, charP, strP, strAssignP, quotMarkP, strBackP, parenP, numBuildP, boolP, upperCaseP, strNotEmptyP, opGroupP, ufpP, quotInStrP, invalidFpP, intStrP;

	public Interpreter() {
		vars = new HashMap<String, Variable>();

		if (!patternsInitd) {
			initPatterns();
		}
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
		if (v == null){
			throw new LotusException("varNotFound", name);
		}

		this.setVar(v, other);
	}

	public void setVar(Variable v, Variable other) throws LotusException {
		if (other instanceof IntVar) {
			this.setVar(v, (Integer)other.value);
		}
		else if (other instanceof BoolVar) {
			this.setVar(v, (Boolean)other.value);
		}
		else if (other instanceof DoubleVar) {
			this.setVar(v, (Double)other.value);
		}
		else if (other instanceof StringVar) {
			this.setVar(v, (String)other.value);
		}
	}

	public void setVar(Variable v, Integer value) throws LotusException {
		if (v instanceof IntVar) {
			((IntVar)v).setValue(value);
		}
		else if (v instanceof BoolVar) {
			if (value.equals(0)) {
				((BoolVar)v).setValue(false);
			}
			else ((BoolVar)v).setValue(true);
		}
		else if (v instanceof DoubleVar) {
			((DoubleVar)v).setValue(value.doubleValue());
		}
		else if (v instanceof StringVar) {
			((StringVar)v).setValue(value.toString());
		}
		else {
			throw new LotusException("cantAssign", IntVar.class + ";" + v.getClass().toString());
		}
	}

	public void setVar(Variable v, Boolean value) throws LotusException {
		if (v instanceof IntVar) {
			if (value.equals(true)) {
				((IntVar)v).setValue(1);
			}
			else {
				((IntVar)v).setValue(0);
			}
		}
		else if (v instanceof BoolVar) {
			((BoolVar)v).setValue(value);
		}
		else if (v instanceof DoubleVar) {
			if (value.equals(true)) {
				((DoubleVar)v).setValue(1.0);
			}
			else {
				((DoubleVar)v).setValue(0.0);
			}
		}
		else if (v instanceof StringVar) {
			((StringVar)v).setValue(value.toString());
		}
		else {
			throw new LotusException("cantAssign", BoolVar.class + ";" + v.getClass().toString());
		}
	}

	public void setVar(Variable v, Double value) throws LotusException {
		if (v instanceof IntVar) {
			((IntVar)v).setValue(value.intValue());
		}
		else if (v instanceof BoolVar) {
			if (value.equals(0.0)) {
				((BoolVar)v).setValue(false);
			}
			else ((BoolVar)v).setValue(true);
		}
		else if (v instanceof DoubleVar) {
			((DoubleVar)v).setValue(value);
		}
		else if (v instanceof StringVar) {
			((StringVar)v).setValue(value.toString());
		}
		else {
			throw new LotusException("cantAssign", DoubleVar.class + ";" + v.getClass().toString());
		}
	}

	public void setVar(Variable v, String value) throws LotusException {
		Matcher intM = intP.matcher(value), fpM = fpP.matcher(value);

		if (v instanceof StringVar) {
			((StringVar)v).setValue(value);
		}
		else if (v instanceof BoolVar) {
			((BoolVar)v).setValue(Boolean.valueOf(value));
		}
		else if (v instanceof IntVar) {
			if (intM.matches()) {
				((IntVar)v).setValue(Integer.parseInt(value));
	        }
			else if (fpM.matches()) {
				((IntVar)v).setValue((int)Double.parseDouble(value));
	        }
			else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
				if (Boolean.valueOf(value)) {
					((IntVar)v).setValue(1);
				}
				else {
					((IntVar)v).setValue(0);
				}
			}
	        else {
				throw new LotusException("cantAssign", StringVar.class + ";" + v.getClass().toString());
	        }
		}
		else if (v instanceof DoubleVar) {
			if (fpM.matches()) {
				((DoubleVar)v).setValue(Double.parseDouble(value));
	        }
			else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
				if (Boolean.valueOf(value)) {
					((DoubleVar)v).setValue(1.0);
				}
				else {
					((DoubleVar)v).setValue(0.0);
				}
			}
	        else {
				throw new LotusException("cantAssign", StringVar.class + ";" + v.getClass().toString());
	        }
		}
		else {
			throw new LotusException("cantAssign", StringVar.class + ";" + v.getClass().toString());
		}
	}

	/* ---------------------------------------------------------------------- */

	public void execute(String line) throws LotusException {
		Matcher wholeDeclM, wholeAtrM, wholePrintM, wholeScanM, wholeScanlnM;
		int semicolon = line.indexOf(";");

		if (semicolon < 0) {
			throw new LotusException("syntaxError", line);
		}

		String lineEnding = line.substring(semicolon + 1).trim();
		if (!lineEnding.isEmpty() && !lineEnding.startsWith("--")) {
			throw new LotusException("multipleCommands", line);
		}

		line = line.substring(0, line.indexOf(";") + 1);

		wholeDeclM = wholeDeclP.matcher(line);
		wholeAtrM = wholeAtrP.matcher(line);
		wholePrintM = wholePrintP.matcher(line);
		wholeScanM = wholeScanP.matcher(line);
		wholeScanlnM = wholeScanlnP.matcher(line);

		if (wholeDeclM.matches()) {
			this.let(line);
		}
		else if (wholeAtrM.matches()) {
			this.assign(line);
		}
		else if (wholePrintM.matches()) {
			this.print(line);
		}
		else if (wholeScanM.matches()) {
			this.scan(line);
		}
		else if (wholeScanlnM.matches()) {
			this.scanln(line);
		}
		else {
			throw new LotusException("unknownCommand", line);
		}
	}

	private void let(String line) throws LotusException {
		String[] decl = this.fixDecl(line);
		int i, max = decl.length - 1;
		Matcher atrM, varM, strM;
		Variable v = null;

		for (i = 0; i < max; i++) {
			if (reservedWords.containsKey(decl[i])) {
				throw new LotusException("usingReservedWords", decl[i]);
			}

			// decl[max] holds the type
			switch (decl[max]) {
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

				default:
				v = null;
				break;
			}
			if (v != null) {
				atrM = atrP.matcher(decl[i]);

				if (atrM.matches()) {
					varM = varNameP.matcher(decl[i]);
					varM.find();

					// if the variable being declared is a string and
					// it has an assignment, it must have ""
					if (v instanceof StringVar) {
						strM = strP.matcher(decl[i].substring(decl[i].indexOf("=") + 1));
						if (!strM.matches()) {
							throw new LotusException("syntaxError", decl[i]);
						}
					}

					this.newVar(varM.group(), v);
					this.assign(decl[i] + ";");
				}
				else {
					this.newVar(decl[i], v);
				}
			}
			else {
				throw new LotusException("invalidType", decl[max]);
			}
		}
	}

	// remember the Arrays!
    public String[] fixDecl(String line) throws LotusException {
        int i;
        String var = new String("");
		Matcher atrM, charM, typeM;
        String[] t = line.replace("let ", "").replaceAll(" ", "").split("");
        ArrayList<String> tokens = new ArrayList<String>();

        for (i = 0; i < t.length && !t[i].equals(":"); i++) {
			charM = charP.matcher(t[i]);
            if (charM.matches()) {
                var += t[i];
            }
			else if (!var.isEmpty() && t[i].equals("=")) {
				while (i < t.length && !t[i].equals(",") && !t[i].equals(":")) {
					var += t[i];
					i++;
				}
				i--;
				atrM = atrP.matcher(var);
				if (atrM.matches()) {
					tokens.add(var);
					var = "";
				}
				else {
					throw new LotusException("syntaxError", var);
				}
			}
            else if (!var.isEmpty() && (t[i].equals(" ") || t[i].equals(","))) {
                tokens.add(var);
                var = "";
            }
			else if (!t[i].equals(",")/* && t[i].matches("\\W")*/) {
                throw new LotusException("invalidVarName", t[i]);
            }
        }
        // the last var left before ":"
        if (var != "") {
            tokens.add(var);
            var = "";
        }

        // type
        while (i < t.length) {
			charM = charP.matcher(t[i]);
            if (charM.matches()) {
                var += t[i];
            }
            i++;
        }

		typeM = typeP.matcher(var);
        if (typeM.matches()) {
            tokens.add(var);
        }
        else {
            throw new LotusException("syntaxError", line);
        }

        t = new String[tokens.size()];
        tokens.toArray(t);

        return t;
    }

	public void assign(String line) throws LotusException {
		int equalsIndex;
        Variable v = null;
        Expression assign = null;
		String[] atr = new String[2];
		Matcher quotMarkM, strBackM, semicM, strAssignM, quotInStrM;

		equalsIndex = line.indexOf("=");
		atr[0] = line.substring(0, equalsIndex).trim();
		atr[1] = line.substring(equalsIndex + 1).trim();

		// if it's a string (enclosed with "");
		strAssignM = strAssignP.matcher(atr[1]);

        if (strAssignM.matches()) {
			// removing first "
			quotMarkM = quotMarkP.matcher(atr[1]);
			atr[1] = quotMarkM.replaceFirst("");

			// removing ";
			strBackM = strBackP.matcher(atr[1]);
			atr[1] = strBackM.replaceFirst("");

			// replacing all \" for an actual "
			quotInStrM = quotInStrP.matcher(atr[1]);
	        atr[1] = quotInStrM.replaceAll("\"");

			this.setVar(this.getVar(atr[0]), atr[1]);
        }
		else {
			semicM = semicP.matcher(atr[1]);
			atr[1] = semicM.replaceFirst("");
            v = this.getVar(atr[0]);

			this.setVar(v, this.solve(new Expression(atr[1])));
        }
    }

	public Variable solve(Expression exp) throws LotusException {
        Variable answ = null, num1 = null, num2 = null;
		String tokens = exp.toPostfix();
		String[] t = tokens.split(Expression.SEP.toString());
		int i = 0, offset = 0;
		String[] front, back;
		Matcher wholeOpM;
		String op;

        if (t.length == 1) {
            answ = this.getOperand(t[0]);
        }

		while (t.length > 1) {

			wholeOpM = wholeOpP.matcher(t[i]);
			// advance until you don't find an operation to perform
			while (i < t.length && !wholeOpM.matches()) {
				i++;
				if (i < t.length) wholeOpM = wholeOpP.matcher(t[i]);
			}

			// then, the operation char is at i's position in the array
			op = t[i];
			// as it's postfix, the 2nd operand is right before op
            num2 = this.getOperand(t[i - 1]);

			/* now, if i > 1, then 1st operand is certainly 2 positions
			 * before op. Else, it doesn't exist lol
			 */
			if (i > 1) {
                num1 = this.getOperand(t[i - 2]);
            }
			else {
                num1 = null;
            }

            if (num2 instanceof DoubleVar || (num1 != null && num1 instanceof DoubleVar)) {
                answ = new DoubleVar(0.0);
            }
            else {
                answ = new IntVar(0);
            }

			/* As I am overwriting my token vector, I gotta
			 * copy the parts that I'm not currently working with:
			 * the first (frontside) and the second (backside) parts,
             * with the result f the current operation in between.
             * Then, if my first operand doesn't exist, I gotta copy
             * the first part until this position (i) - 1. If it exists,
			 * until this position - 2. This way I discard
			 * the spots where the current operands were located
			 * and I leave one spot for the result of this operation
			 * in the middle of the new vector :)
			 */
			if (num1 == null) offset = i - 1;
			else offset = i - 2;

			front = Arrays.copyOfRange(t, 0, offset);
			back = Arrays.copyOfRange(t, i, t.length);
			t = this.merge(front, back);

			/* calculating where the result will be put
			 * same logic as the else above
			 */
			if (num1 == null) i -= 1;
			else i -= 2;

			answ = this.calculate(num1, num2, op);
			t[i] = answ.toString();
		}

		System.out.println(">>>> result: " + answ + "\n");

		return answ;
	}

    private Variable getOperand(String t) throws LotusException {
        Matcher intM, fpM, boolM, strM, quotInStrM, intStrM, varNameM;
        Variable v = null;
		int index;

        intM = intP.matcher(t);
        fpM = fpP.matcher(t);
		boolM = boolP.matcher(t);
		strM = strP.matcher(t);
		intStrM = intStrP.matcher(t);
		varNameM = varNameP.matcher(t);

        if (intM.matches()) {
            v = new IntVar(Integer.parseInt(t));
        }
        else if (fpM.matches()) {
            v = new DoubleVar(Double.parseDouble(t));
        }
        else if (t.startsWith("-")) {
            t = t.replace("-", "");

            v = checkAndGetVar(t, true);
        }
        else if (t.startsWith("+")) {
            t = t.replace("+", "");

            v = checkAndGetVar(t, false);
        }
		else if (boolM.matches()) {
			if (t.equals("true")) {
				v = new BoolVar(true);
			}
			else {
				v = new BoolVar(false);
			}
		}
		else if (strM.matches()) {
			t = t.substring(t.indexOf("\"") + 1);

			// needs to the the index of the last quotation mark
			index = t.indexOf("\"");
			while (t.indexOf("\"", index + 1) > index) {
				index = t.indexOf("\"", index + 1);
			}

			t = t.substring(0, index);
			// replacing all \" for an actual "
			quotInStrM = quotInStrP.matcher(t);
			t = quotInStrM.replaceAll("\"");

			v = new StringVar(t);
		}
		else if (intStrM.matches()) {
			if (varNameM.matches()) {
				v = this.getVar(t);
			}
			else {
				v = new StringVar(t);
			}
		}
		else {
			throw new LotusException("invalidExp", t);
		}

        return v;
    }

	private Variable checkAndGetVar(String t, boolean neg) throws LotusException {
		Variable v = null;
		Matcher varNameM = varNameP.matcher(t);

		if (varNameM.matches()) {
			v = this.getVar(t);

			if (v != null) {
				if (neg) v = v.inverted();
			}
			else {
				throw new LotusException("varNotFound", t);
			}
		}
		else {
			throw new LotusException("invalidExp", t);
		}

		return v;
	}

    private Variable calculate(Variable v1, Variable v2, String op) throws LotusException {
        Variable answ = null;
        Matcher opM = wholeOpP.matcher(op);

        // If it doesn't have 2 operands and the
        // operation is a '-', simply return -v2.
        if (v1 == null) {
            if (op.equals("-")) {
                answ = v2.inverted();
            }
            else if (op.equals("+")) {
                answ = v2;
            }
			else if (op.equals("!")) {
				answ = new BoolVar(!v2.toBool());
			}
            else if (opM.matches()) {
                throw new LotusException("syntaxError", v1 + " " + op + " " + v2);
            }
        }
        else {
            switch (op) {
                /* pow() returns double. Maybe I will implement
                 * a binary exponentiation later...
                 */
                case "^":
                answ = v1.pow(v2);
                break;

                case "*":
				answ = v1.times(v2);
                // if (intOpns) answ = new IntVar(v1.toInt() * v2.toInt());
                // else answ = new DoubleVar(v1.toDouble() * v2.toDouble());
                break;

				case "/":
                answ = v1.divided(v2);
                break;

                case "%":
				answ = v1.mod(v2);
                break;

                case "+":
                answ = v1.plus(v2);
                break;

                case "-":
                answ = v1.minus(v2);
                break;

				case "&&":
				answ = v1.and(v2);
				break;

				case "||":
				answ = v1.or(v2);
				break;

				case "<":
				answ = v1.lessThan(v2);
				break;

				case "<=":
				answ = v1.lessEquals(v2);
				break;

				case "==":
				answ = v1.equals(v2);
				break;

				case ">=":
				answ = v1.greaterEquals(v2);
				break;

				case ">":
				answ = v1.greaterThan(v2);
				break;

				case "!=":
				answ = v1.equals(v2).inverted();
				break;

                default:
                throw new LotusException("unknownSymbol", op);
            }
        }

        return answ;
    }

    private String[] merge(String[] front, String[] back) {
        int i = 0;
        String[] ret = new String[front.length + back.length];

        for (String s: front) {
            ret[i++] = s;
        }
        for (String s: back) {
            ret[i++] = s;
        }

        return ret;
    }

	private void print(String line) throws LotusException {
		int i, offset;
		String[] content;
		String text = "";
		Variable v = null;
		String lineEnding;
		Matcher cutPrintM;
		boolean breakLine = false;

		if (line.startsWith("println")) {
			breakLine = true;
		}

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");

		cutPrintM = cutPrintP.matcher(line);
		line = cutPrintM.replaceFirst("");

		content = line.split("");
		for (i = 0; i < content.length; i++) {
			// \t 	Insert a tab in the text at this point.
			// \n 	Insert a newline in the text at this point.
			// \\ 	Insert a backslash character in the text at this point.
			if (content[i].equals("\\") && i + 1 < content.length) {
				if (content[i + 1].equals("t")) {
					text += "\t";
				}
				else if (content[i + 1].equals("n")) {
					text += "\n";
				}
				// if you want to print something like "$gabriel$",
				// you just need to escape one '$' (or both)
				else if (content[i + 1].equals("$")) {
					text += "$";
				}
				else if (content[i + 1].equals("\\")) {
					if (i + 2 < content.length && content[i + 2].equals("n")) {
						text += "\\n";
						i++;
					}
					else text += "\\";
				}
				else {
					throw new LotusException("unknownEscape", content[i] + content[i + 1]);
				}

				i++;
			}
			else if (content[i].equals("$")) {
				// i is the index of the first '$'!
				v = this.whatVar(line, i);
				if (v != null) {
					text += v.toString();
					i = line.indexOf("$", i + 1)/* + 1*/;
				}
				else {
					text += content[i];
				}
			}
			else {
				text += content[i];
			}
		}
		if (breakLine) System.out.println(text);
		else System.out.print(text);
	}

	private Variable whatVar(String content, int fromIndex) throws LotusException {
		String name;
		Variable var = null;
		Matcher varNameM;
		int offset = content.indexOf("$", fromIndex + 1);

		if (offset > fromIndex) {
			name = content.substring(fromIndex + 1, offset);

			varNameM = varNameP.matcher(name);
			if (varNameM.matches()) {
				var = this.getVar(name);
				if (var == null) {
					throw new LotusException("varNotFound", name);
				}
			}
			// if does not follow the pattern for a variable name
			// in a print command, it doesn't matter, as that
			// variable doesn't exist anyways
		}

		return var;
	}

	// directly assigns the input read into the requested variable(s)
	// interprets mutiple inputs as separated by spaces or line breaks.
	// to read a full line, use scanln
	private void scan(String line) throws LotusException {
		int i, j, max;
		String[] input;
		Variable v, other;
		String lineEnding, name;
		Scanner sc = new Scanner(System.in);
		Matcher cutScanM, scanContentM, varM, intM, fpM, strM;

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");

		cutScanM = cutScanP.matcher(line);
		line = cutScanM.replaceFirst("");
		// line = line.replaceFirst("(scan)( )*\\(", "");

		scanContentM = scanContentP.matcher(line);
		if (!scanContentM.find()) {
			throw new LotusException("syntaxError", line);
		}

		max = 0;
		varM = varNameP.matcher(line);
		while (varM.find()) {
			max++; // counting how many matches (vars) I got inside scan
		}
		varM = varM.reset();

		for (i = 0; i < max; i++) {
			input = sc.nextLine().split(" ");

			for (j = 0; j < input.length; j++) {
				if (varM.find()) {
					name = varM.group();

					if ((v = this.getVar(name)) != null) {
						intM = intP.matcher(input[j]);
						fpM = fpP.matcher(input[j]);

						if (intM.matches()) {
							other = new IntVar(Integer.parseInt(input[j]));
						}
						else if (fpM.matches()) {
							other = new DoubleVar(Double.parseDouble(input[j]));
						}
						else {
							other = new StringVar(input[j]);
						}

						this.setVar(v, other);
					}
					else {
						throw new LotusException("varNotFound", name);
					}

					if (j + 1 < input.length) i++;
				}
			}
		}
	}

	private void scanln(String line) throws LotusException {
		Variable v;
		String lineEnding, name, input;
		Scanner sc = new Scanner(System.in);
		Matcher cutScanM, scanContentM, varM, strM;

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");

		cutScanM = cutScanP.matcher(line);
		line = cutScanM.replaceFirst("");
		// line = line.replaceFirst("(scanln)( )*\\(", "");

		scanContentM = scanContentP.matcher(line);
		if (!scanContentM.find()) {
			throw new LotusException("syntaxError", line);
		}

		varM = varNameP.matcher(line);
		while (varM.find()) {
			input = sc.nextLine();
			name = varM.group();

			if ((v = this.getVar(name)) != null) {
				this.setVar(v, input);
			}
			else {
				throw new LotusException("varNotFound", name);
			}
		}
	}

	private static Map<String, Boolean> mapReservedWords() {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        result.put("let", true);
        result.put("int", true);
        result.put("double", true);
        result.put("string", true);
        result.put("bool", true);
        result.put("true", true);
        result.put("false", true);

        result.put("print", true);
        result.put("println", true);
        result.put("scan", true);
        result.put("scanln", true);

        result.put("unless", true);
        result.put("if", true);
        result.put("elsif", true);
        result.put("else", true);

        result.put("for", true);
        result.put("while", true);
        result.put("break", true);
        result.put("continue", true);

        result.put("fn", true);
        result.put("main", true);
        return Collections.unmodifiableMap(result);
    }

	public static final String semicR = "( )*;";

	public static final String typeR = "int|double|string|bool";
	public static final String varNameR = "[A-Za-z_][A-Za-z_0-9]*";
	public static final String wholeDeclR = "(let)( )+((.+)+((,( )*(.+)+)( )*)*)( )*:( )*(\\w)+" + semicR;

	public static final String parenR = "\\(|\\)";
	public static final String boolOpR = "\\!|\\&\\&|\\|\\|";
	public static final String compOpR = "\\<|\\<\\=|\\=\\=|\\>\\=|\\>|\\!\\=";
	public static final String compOrBoolR = compOpR + "|" + boolOpR;
	public static final String mathOpR = "\\-|\\+|\\/|\\%|\\*|\\^";

	public static final String wholeOpR = parenR + "|" + boolOpR + "|" + compOpR + "|" + mathOpR;

	public static final String atrR = varNameR + "( )*=( )*.+";
	public static final String wholeAtrR = atrR + semicR;
	public static final String stripAtrR = "( )*=( )*";

	public static final String fnParentheses = "( )*\\(.*\\)" + semicR;
	public static final String printR = "(print|println)";
	public static final String wholePrintR = printR + fnParentheses;
	public static final String wholeScanR = "(scan)" + fnParentheses;
	public static final String wholeScanlnR = "(scanln)" + fnParentheses;
	public static final String scanContentR = "(" + varNameR + ")(( )*,( )*(" + varNameR + "))*";

	public static final String quotMarkR = "\\\"";
	public static final String quotInStrR = "\\\\" + quotMarkR;
	public static final String strBackR = quotMarkR + semicR;

	// strings with any character enclosed with "". Supports escaped " too.
	public static final String strR = quotMarkR + "(?:\\\\.|[^" + quotMarkR + "\\\\])*" + quotMarkR;
	// "(?:\\.|[^"\\])*"
	// matches 0 or more of the following, enclosed with "
	// matches a \ followed by any character, or...
	// doesn't match a " or a \

	public static final String signR = "[+-]";
	public static final String intR = signR + "?[0-9]+";
	public static final String boolR = "(true|false)";

	public static final String ufpR = "(\\d+(\\.\\d*)?|(\\d*\\.)?\\d+)";
	public static final String fpR = "(" + signR + "( )*)?" + ufpR;
	public static final String invalidFpR = "([+-]( )*)?(\\d+( )+\\.(( )*\\d)*|(\\d( )*)*\\.( )+\\d+)";

	// public static final String wholeExpR = "(" + varNameR + "|" + fpR + "|" + strR + "|" + boolR + "|" + wholeOpR + ")";

	private static void initPatterns() {
		varNameP = Pattern.compile(varNameR);
		typeP = Pattern.compile(typeR);
		atrP = Pattern.compile(atrR);

		wholeOpP = Pattern.compile(wholeOpR);
		signP = Pattern.compile(signR);
		intP = Pattern.compile(intR);
		ufpP = Pattern.compile(ufpR);
		fpP = Pattern.compile(fpR);
		invalidFpP = Pattern.compile(invalidFpR);
		boolP = Pattern.compile(boolR);
		charP = Pattern.compile("\\w");
		intStrP = Pattern.compile(".+");
		strP = Pattern.compile(strR);
		quotMarkP = Pattern.compile(quotMarkR);
		quotInStrP = Pattern.compile(quotInStrR);
		strBackP = Pattern.compile(strBackR);
		strNotEmptyP = Pattern.compile("\\S");
		opGroupP = Pattern.compile(signR + "(( )*" + signR + ")+");
		// "((" + signR + "+)(( )*(" + signR + "+))*)+"

		strAssignP = Pattern.compile(strR + semicR);
		upperCaseP = Pattern.compile("[A-Z]+");

		wholeDeclP = Pattern.compile(wholeDeclR);
		wholeAtrP = Pattern.compile(wholeAtrR);

		semicP = Pattern.compile(semicR);
		wholePrintP = Pattern.compile(wholePrintR);
		wholeScanP = Pattern.compile(wholeScanR);
		wholeScanlnP = Pattern.compile(wholeScanlnR);
		cutPrintP = Pattern.compile(printR + "( )*\\(");
		cutScanP = Pattern.compile("(scan|scanln)( )*\\(");
		scanContentP = Pattern.compile(scanContentR);

		parenP = Pattern.compile("[()]");
		numBuildP = Pattern.compile("(\\w|\\.)+");

		patternsInitd = true;
	}
}
