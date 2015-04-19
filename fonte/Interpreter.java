import java.util.*;
import java.util.regex.*;

class Interpreter {
	private HashMap<String, Variable> vars;
	// this doesn't make that much sense now, but it's faster to
	// look up in a hash than an array. And later on we can replace
	// the boolean value to a Runnable...
	private static final Map<String, Boolean> reservedWords = mapReservedWords();
	private static boolean patternsInitd = false;
	public static Pattern typePattern, wholeDeclPattern, varNamePattern, atrPattern, wholeAtrPattern, semicPattern, wholePrintPattern, wholeScanPattern, wholeScanlnPattern, cutPrintPattern, cutScanPattern, scanContentPattern, wholeOpPattern, signPattern, intPattern, fpPattern, charPattern, strPattern, strAssignPattern, quotMarkPattern, strBackPattern, parenPattern, numBuildPattern, boolPattern, upperCasePattern, strExpPattern;

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
		Matcher intMatcher = intPattern.matcher(value), fpMatcher = fpPattern.matcher(value);

		if (v instanceof StringVar) {
			((StringVar)v).setValue(value);
		}
		else if (v instanceof BoolVar) {
			((BoolVar)v).setValue(Boolean.valueOf(value));
		}
		else if (v instanceof IntVar) {
			if (intMatcher.matches()) {
				((IntVar)v).setValue(Integer.parseInt(value));
	        }
			else if (fpMatcher.matches()) {
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
			if (fpMatcher.matches()) {
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
		Matcher wholeDeclMatcher, wholeAtrMatcher, wholePrintMatcher, wholeScanMatcher, wholeScanlnMatcher;
		int semicolon = line.indexOf(";");

		if (semicolon < 0) {
			throw new LotusException("syntaxError", line);
		}

		String lineEnding = line.substring(semicolon + 1).trim();
		if (!lineEnding.isEmpty() && !lineEnding.startsWith("--")) {
			throw new LotusException("multipleCommands", line);
		}

		line = line.substring(0, line.indexOf(";") + 1);

		wholeDeclMatcher = wholeDeclPattern.matcher(line);
		wholeAtrMatcher = wholeAtrPattern.matcher(line);
		wholePrintMatcher = wholePrintPattern.matcher(line);
		wholeScanMatcher = wholeScanPattern.matcher(line);
		wholeScanlnMatcher = wholeScanlnPattern.matcher(line);

		if (wholeDeclMatcher.matches()) {
			this.let(line);
		}
		else if (wholeAtrMatcher.matches()) {
			this.assign(line);
		}
		else if (wholePrintMatcher.matches()) {
			this.print(line);
		}
		else if (wholeScanMatcher.matches()) {
			this.scan(line);
		}
		else if (wholeScanlnMatcher.matches()) {
			this.scanln(line);
		}
		else {
			throw new LotusException("unknownCommand", line);
		}
	}

	private void let(String line) throws LotusException {
		String[] decl = this.fixDecl(line);
		int i, max = decl.length - 1;
		Matcher atrMatcher, varMatcher, strMatcher;
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
				atrMatcher = atrPattern.matcher(decl[i]);

				if (atrMatcher.matches()) {
					varMatcher = varNamePattern.matcher(decl[i]);
					varMatcher.find();

					// if the variable being declared is a string and
					// it has an assignment, it must have ""
					if (v instanceof StringVar) {
						strMatcher = strPattern.matcher(decl[i].substring(decl[i].indexOf("=") + 1));
						if (!strMatcher.matches()) {
							throw new LotusException("syntaxError", decl[i]);
						}
					}

					this.newVar(varMatcher.group(), v);
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
		Matcher atrMatcher, charMatcher, typeMatcher;
        String[] t = line.replace("let ", "").replaceAll(" ", "").split("");
        ArrayList<String> tokens = new ArrayList<String>();

        for (i = 0; i < t.length && !t[i].equals(":"); i++) {
			charMatcher = charPattern.matcher(t[i]);
            if (charMatcher.matches()) {
                var += t[i];
            }
			else if (!var.isEmpty() && t[i].equals("=")) {
				while (i < t.length && !t[i].equals(",") && !t[i].equals(":")) {
					var += t[i];
					i++;
				}
				i--;
				atrMatcher = atrPattern.matcher(var);
				if (atrMatcher.matches()) {
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
			charMatcher = charPattern.matcher(t[i]);
            if (charMatcher.matches()) {
                var += t[i];
            }
            i++;
        }

		typeMatcher = typePattern.matcher(var);
        if (typeMatcher.matches()) {
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
		Matcher quotMarkMatcher, strBackMatcher, semicMatcher, strAssignMatcher;

		equalsIndex = line.indexOf("=");
		atr[0] = line.substring(0, equalsIndex).trim();
		atr[1] = line.substring(equalsIndex + 1).trim();

		// if it's a string (enclosed with "");
		strAssignMatcher = strAssignPattern.matcher(atr[1]);

        if (strAssignMatcher.matches()) {
			quotMarkMatcher = quotMarkPattern.matcher(atr[1]);
			atr[1] = quotMarkMatcher.replaceFirst("");

			strBackMatcher = strBackPattern.matcher(atr[1]);
			atr[1] = strBackMatcher.replaceFirst("");

			this.setVar(this.getVar(atr[0]), atr[1]);
        }
        else {
			semicMatcher = semicPattern.matcher(atr[1]);
			atr[1] = semicMatcher.replaceFirst("");
            v = this.getVar(atr[0]);

			this.setVar(v, this.solve(new Expression(atr[1])));
        }
    }

	public Variable solve(Expression exp) throws LotusException {
        Variable answ = null, num1 = null, num2 = null;
		Matcher wholeOpMatcher, strExpMatcher;
		String tokens = exp.toPostfix();
		String[] t, front, back;
		int i = 0, offset = 0;
		String op;

		// strExpMatcher = strExpPattern.matcher(tokens);
		// if (strExpMatcher.matches()) {
		// 	t = tokens.split("\"");
		// }
		// else {
		// 	t = tokens.split(" ");
		// }

		t = tokens.split(" ");

        if (t.length == 1) {
            answ = this.getOperand(t[0]);
        }

		while (t.length > 1) {

			System.out.println();
			for (String s: t) {
				System.out.print("[" + s + "]");
			} System.out.println();
			System.out.println();

			wholeOpMatcher = wholeOpPattern.matcher(t[i]);
			// advance until you don't find an operation to perform
			while (i < t.length && !wholeOpMatcher.matches()) {
				i++;
				if (i < t.length) wholeOpMatcher = wholeOpPattern.matcher(t[i]);
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
        Matcher intMatcher, fpMatcher, boolMatcher, stringMatcher, varNameMatcher;
        Variable v = null;

        intMatcher = intPattern.matcher(t);
        fpMatcher = fpPattern.matcher(t);
		boolMatcher = boolPattern.matcher(t);
		stringMatcher = strPattern.matcher(t);
        varNameMatcher = varNamePattern.matcher(t);

		System.out.println("getOperand t: " + t);

        if (intMatcher.matches()) {
            v = new IntVar(Integer.parseInt(t));
        }
        else if (fpMatcher.matches()) {
            v = new DoubleVar(Double.parseDouble(t));
        }
        else if (t.startsWith("-")) {
            t = t.replace("-", "");

            varNameMatcher = varNamePattern.matcher(t);
            if (varNameMatcher.matches()) {
                v = this.getVar(t);
                if (v != null) v = v.inverted();
                else {
                    throw new LotusException("varNotFound", t);
                }
            }
            else {
                throw new LotusException("unknownSymbol", t);
            }
        }
        else if (t.startsWith("+")) {
            t = t.replace("+", "");

            varNameMatcher = varNamePattern.matcher(t);
            if (varNameMatcher.matches()) {
                v = this.getVar(t);
                if (v == null) {
                    throw new LotusException("varNotFound", t);
                }
            }
            else {
                throw new LotusException("unknownSymbol", t);
            }
        }
		else if (boolMatcher.matches()) {
			if (t.equals("true")) {
				v = new BoolVar(true);
			}
			else {
				v = new BoolVar(false);
			}
		}
		else if (stringMatcher.matches()) {
			t = t.substring(t.indexOf("\"") + 1);
			v = new StringVar(t.substring(0, t.indexOf("\"")));
		}
        else if (varNameMatcher.matches()) {
            v = this.getVar(t);
        }
        else {
            throw new LotusException("unknownSymbol", t);
        }

        return v;
    }

    private Variable calculate(Variable v1, Variable v2, String op) throws LotusException {
        Variable answ = null;
        Matcher opMatcher = wholeOpPattern.matcher(op);

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
            else if (opMatcher.matches()) {
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
		Matcher cutPrintMatcher;
		boolean breakLine = false;

		if (line.startsWith("println")) {
			breakLine = true;
		}

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");

		cutPrintMatcher = cutPrintPattern.matcher(line);
		line = cutPrintMatcher.replaceFirst("");
		// line = line.replaceFirst(printRegex + "( )*\\(", "");

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
		Matcher varNameMatcher;
		int offset = content.indexOf("$", fromIndex + 1);

		if (offset > fromIndex) {
			name = content.substring(fromIndex + 1, offset);

			varNameMatcher = varNamePattern.matcher(name);
			if (varNameMatcher.matches()) {
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
		Matcher cutScanMatcher, scanContentMatcher, varMatcher, intMatcher, fpMatcher, strMatcher;

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");

		cutScanMatcher = cutScanPattern.matcher(line);
		line = cutScanMatcher.replaceFirst("");
		// line = line.replaceFirst("(scan)( )*\\(", "");

		scanContentMatcher = scanContentPattern.matcher(line);
		if (!scanContentMatcher.find()) {
			throw new LotusException("syntaxError", line);
		}

		max = 0;
		varMatcher = varNamePattern.matcher(line);
		while (varMatcher.find()) {
			max++; // counting how many matches (vars) I got inside scan
		}
		varMatcher = varMatcher.reset();

		for (i = 0; i < max; i++) {
			input = sc.nextLine().split(" ");

			for (j = 0; j < input.length; j++) {
				if (varMatcher.find()) {
					name = varMatcher.group();

					if ((v = this.getVar(name)) != null) {
						intMatcher = intPattern.matcher(input[j]);
						fpMatcher = fpPattern.matcher(input[j]);

						if (intMatcher.matches()) {
							other = new IntVar(Integer.parseInt(input[j]));
						}
						else if (fpMatcher.matches()) {
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
		Matcher cutScanMatcher, scanContentMatcher, varMatcher, strMatcher;

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");

		cutScanMatcher = cutScanPattern.matcher(line);
		line = cutScanMatcher.replaceFirst("");
		// line = line.replaceFirst("(scanln)( )*\\(", "");

		scanContentMatcher = scanContentPattern.matcher(line);
		if (!scanContentMatcher.find()) {
			throw new LotusException("syntaxError", line);
		}

		varMatcher = varNamePattern.matcher(line);
		while (varMatcher.find()) {
			input = sc.nextLine();
			name = varMatcher.group();

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

	public static final String semicRegex = "( )*;";

	public static final String typeRegex = "int|double|string|bool";
	public static final String varNameRegex = "(?!\\d)\\w+";
	public static final String wholeDeclRegex = "(let)( )+((.+)+((,( )*(.+)+)( )*)*)( )*:( )*(\\w)+" + semicRegex;

	public static final String parenRegex = "\\(|\\)";
	public static final String boolOpRegex = "\\!|\\&\\&|\\|\\|";
	public static final String compOpRegex = "\\<|\\<\\=|\\=\\=|\\>\\=|\\>|\\!\\=";
	public static final String compOrBoolRegex = compOpRegex + "|" + boolOpRegex;
	public static final String mathOpRegex = "\\-|\\+|\\/|\\%|\\*|\\^";

	public static final String wholeOpRegex = parenRegex + "|" + boolOpRegex + "|" + compOpRegex + "|" + mathOpRegex;

	public static final String strRegex = "\\\"\\S+\\\"";

	public static final String strExpRegex = "(" + strRegex + wholeOpRegex + strRegex + ")+";

	public static final String atrRegex = varNameRegex + "( )*=( )*.+";
	public static final String wholeAtrRegex = atrRegex + semicRegex;
	public static final String stripAtrRegex = "( )*=( )*";

	public static final String fnParentheses = "( )*\\(.*\\)" + semicRegex;
	public static final String printRegex = "(print|println)";
	public static final String wholePrintRegex = printRegex + fnParentheses;
	public static final String wholeScanRegex = "(scan)" + fnParentheses;
	public static final String wholeScanlnRegex = "(scanln)" + fnParentheses;
	public static final String scanContentRegex = "(" + varNameRegex + ")(( )*,( )*(" + varNameRegex + "))*";

	public static final String quotMarkRegex = "\\\"";
	public static final String strBackRegex = quotMarkRegex + semicRegex;

	public static final String signRegex = "[+-]";
	public static final String intRegex = signRegex + "?[0-9]+";
	public static final String boolRegex = "(true|false)";

    // public static final String zeroRegex = "0+(\\.)?0*";
    /* fpRegex taken from Java documentation */
	private static final String Digits     = "(\\p{Digit}+)";
	private static final String HexDigits  = "(\\p{XDigit}+)";
	// an exponent is 'e' or 'E' followed by an optionally
	// signed decimal integer.
	private static final String Exp        = "[eE][+-]?"+Digits;
	public static final String fpRegex    =
		signRegex + "?(" + // Optional sign character
		"NaN|" +           // "NaN" string
		"Infinity|" +      // "Infinity" string

		// A decimal floating-point string representing a finite positive
		// number without a leading sign has at most five basic pieces:
		// Digits . Digits ExponentPart FloatTypeSuffix
		//
		// Since this method allows integer-only strings as input
		// in addition to strings of floating-point literals, the
		// two sub-patterns below are simplifications of the grammar
		// productions from section 3.10.2 of
		// The Java™ Language Specification.

		// Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		"((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		// . Digits ExponentPart_opt FloatTypeSuffix_opt
		"(\\.("+Digits+")("+Exp+")?)|"+

		// Hexadecimal strings
		"((" +
		// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		"(0[xX]" + HexDigits + "(\\.)?)|" +

		// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		"(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		")[pP][+-]?" + Digits + "))" +
		"[fFdD]?))";

	public static final String boolAtrRegex =
		"(" + varNameRegex + "|" + boolRegex + "|" + fpRegex + ")( )*" +
		"((" + compOpRegex + ")|(" + boolOpRegex + "))( )*" +
		"(" + varNameRegex + "|" + boolRegex + "|" + fpRegex + ")( )*";

	private static void initPatterns() {
		varNamePattern = Pattern.compile(varNameRegex);
		typePattern = Pattern.compile(typeRegex);
		atrPattern = Pattern.compile(atrRegex);

		wholeOpPattern = Pattern.compile(wholeOpRegex);
		signPattern = Pattern.compile(signRegex);
		intPattern = Pattern.compile(intRegex);
		fpPattern = Pattern.compile(fpRegex);
		boolPattern = Pattern.compile(boolRegex);
		charPattern = Pattern.compile("\\w");
		strPattern = Pattern.compile(strRegex);
		quotMarkPattern = Pattern.compile(quotMarkRegex);
		strBackPattern = Pattern.compile(strBackRegex);

		strExpPattern = Pattern.compile(strExpRegex);

		strAssignPattern = Pattern.compile(strRegex + semicRegex);
		upperCasePattern = Pattern.compile("[A-Z]+");

		wholeDeclPattern = Pattern.compile(wholeDeclRegex);
		wholeAtrPattern = Pattern.compile(wholeAtrRegex);

		semicPattern = Pattern.compile(semicRegex);
		wholePrintPattern = Pattern.compile(wholePrintRegex);
		wholeScanPattern = Pattern.compile(wholeScanRegex);
		wholeScanlnPattern = Pattern.compile(wholeScanlnRegex);
		cutPrintPattern = Pattern.compile(printRegex + "( )*\\(");
		cutScanPattern = Pattern.compile("(scan|scanln)( )*\\(");
		scanContentPattern = Pattern.compile(scanContentRegex);

		parenPattern = Pattern.compile("[()]");
		numBuildPattern = Pattern.compile("\\w|\\.");

		patternsInitd = true;
	}
}
