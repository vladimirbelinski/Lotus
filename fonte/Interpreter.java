import java.util.*;
import java.util.regex.*;

class Interpreter {
	private HashMap<String, Variable> vars;
	// this doesn't make that much sense now, but it's faster to
	// look up in a hash than an array. And later on we can replace
	// the boolean value to a Runnable...
	private static final Map<String, Boolean> reservedWords = mapReservedWords();
	public static Pattern word, string, type, decl, varName, atr, wholeAtr, stripAtr, print, scan, scanContent, oper, integer, boole, fp;

	public Interpreter() {
		vars = new HashMap<String, Variable>();

		word = Pattern.compile("\\w+");
		string = Pattern.compile("\\\"\\S+\\\"");
		type = Pattern.compile(typeRegex);
		decl = Pattern.compile(declRegex);
		varName = Pattern.compile(varNameRegex);
		atr = Pattern.compile(atrRegex);
		wholeAtr = Pattern.compile(wholeAtrRegex);
		stripAtr = Pattern.compile(stripAtrRegex);
		print = Pattern.compile(printRegex);
		scan = Pattern.compile(scanRegex);
		scanContent = Pattern.compile(scanContentRegex);
		oper = Pattern.compile(operRegex);
		integer = Pattern.compile(intRegex);
		boole = Pattern.compile(boolRegex);
		fp = Pattern.compile(fpRegex);
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
		if (other == null) {
			throw new LotusException("nullAssignment", name);
		}

		Variable v = this.getVar(name);
		if (v == null){
			throw new LotusException("varNotFound", name);
		}

		this.setVar(v, other);
	}

	public void setVar(Variable v, Variable other) throws LotusException {
		if (v != null && other != null) {
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
	}

	public void setVar(Variable v, Integer value) throws LotusException {
		if (v == null) {
			throw new LotusException("nullVar", (Thread.currentThread().getStackTrace()[1]).toString() + "\n" + (Thread.currentThread().getStackTrace()[2]).toString());
		}
		else if (v instanceof IntVar) {
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
			throw new LotusException("cantAssignInt", v.getClass().toString());
		}
	}

	public void setVar(Variable v, Boolean value) throws LotusException {
		if (v == null) {
			throw new LotusException("nullVar", (Thread.currentThread().getStackTrace()[1]).toString() + "\n" + (Thread.currentThread().getStackTrace()[2]).toString());
		}
		else if (v instanceof IntVar) {
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
			throw new LotusException("cantAssignBool", v.getClass().toString());
		}
	}

	public void setVar(Variable v, Double value) throws LotusException {
		if (v == null) {
			throw new LotusException("nullVar", (Thread.currentThread().getStackTrace()[1]).toString() + "\n" + (Thread.currentThread().getStackTrace()[2]).toString());
		}
		else if (v instanceof IntVar) {
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
			throw new LotusException("cantAssignDouble", v.getClass().toString());
		}
	}

	public void setVar(Variable v, String value) throws LotusException {
		if (v == null) {
			throw new LotusException("nullVar", (Thread.currentThread().getStackTrace()[1]).toString() + "\n" + (Thread.currentThread().getStackTrace()[2]).toString());
		}
		else if (v instanceof StringVar) {
			((StringVar)v).setValue(value);
		}
		else if (v instanceof BoolVar) {
			((BoolVar)v).setValue(Boolean.valueOf(value));
		}
		else if (v instanceof IntVar) {
			if (value.matches(intRegex)) {
				((IntVar)v).setValue(Integer.parseInt(value));
	        }
			else if (value.matches(fpRegex)) {
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
				throw new LotusException("cantAssignString", v.getClass().toString());
	        }
		}
		else if (v instanceof DoubleVar) {
			if (value.matches(fpRegex)) {
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
				throw new LotusException("cantAssignString", v.getClass().toString());
	        }
		}
		else {
			throw new LotusException("cantAssignString", v.getClass().toString());
		}
	}

	/* ---------------------------------------------------------------------- */

	public void execute(String line) throws LotusException {
		int semicolon = line.indexOf(";");

		if (semicolon < 0) {
			throw new LotusException("syntaxError", line);
		}

		String lineEnding = line.substring(semicolon + 1).trim();
		if (!lineEnding.isEmpty() && !lineEnding.startsWith("--")) {
			throw new LotusException("multipleCommands", line);
		}

		line = line.substring(0, line.indexOf(";") + 1);

		if (line.matches(declRegex)) {
			this.let(line);
		}
		else if (line.matches(wholeAtrRegex)) {
			// +=, -=...?
			this.assign(line);
		}
		else if (line.matches(printRegex)) {
			this.print(line);
		}
		else if (line.matches(scanRegex)) {
			this.scan(line);
		}
		else {
			throw new LotusException("unknownCommand", line);
		}
	}

	private void let(String line) throws LotusException {
		String[] decl = this.fixDecl(line);
		int i, max = decl.length - 1;
		Matcher atrMatcher, varMatcher;
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
				atrMatcher = atr.matcher(decl[i]);
				if (atrMatcher.matches()) {
					varMatcher = varName.matcher(decl[i]);
					varMatcher.find();
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
		Matcher atrMatcher;
        String var = new String("");
        String[] t = line.replace("let ", "").replaceAll(" ", "").split("");
        ArrayList<String> tokens = new ArrayList<String>();

        for (i = 0; i < t.length && !t[i].equals(":"); i++) {
            if (t[i].matches("\\w")) {
                var += t[i];
            }
			else if (!var.isEmpty() && t[i].equals("=")) {
				while (i < t.length && !t[i].equals(",") && !t[i].equals(":")) {
					var += t[i];
					i++;
				}
				i--;
				atrMatcher = atr.matcher(var);
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
            if (t[i].matches("\\w")) {
                var += t[i];
            }
            i++;
        }

        if (var.matches(typeRegex)) {
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
        Variable result = null;
        Expression assign = null;
		Matcher stringMatcher;
		String[] atr = line.split(stripAtrRegex);

		// if it's a string (enclosed with "");
        // if (atr[1].matches("\\\".+\\\"( )*;")) {
		stringMatcher = string.matcher(atr[1]);
        if (stringMatcher.find()) {
            atr[1] = atr[1].replaceFirst("\\\"", "");
            atr[1] = atr[1].replaceFirst("\\\"( )*;", "");

            this.setVar(this.getVar(atr[0]), atr[1]);
        }
        else if (atr[1].matches(boolRegex + "( )*;")){
            atr[1] = atr[1].replaceFirst("( )*;", "");

            this.setVar(this.getVar(atr[0]), Boolean.parseBoolean(atr[1]));
        }
        else {
            result = this.solve(new Expression(atr[1]));
            this.setVar(this.getVar(atr[0]), result);
        }
    }

	private Variable solve(Expression exp) throws LotusException {
        Variable answ = null, num1 = null, num2 = null;
		String tokens = exp.infixToPostfix();
		String t[] = tokens.split(" ");
		String front[], back[];
		int i = 0, offset = 0;
		String op;

        if (t.length == 1) {
            answ = this.getOperand(t[0]);
        }

		while (t.length > 1) {
			// advance until you don't find an operation to perform
			while (i < t.length && !t[i].matches(operRegex)) {
				i++;
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

            // if (num1 == null && num2 == null) {
			// 	// throw Exception?
            //     answ = new StringVar("undefined");
            //     break;
            // }

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

		return answ;
	}

    private Variable getOperand(String t) throws LotusException {
        Variable v = null;

        if (t.matches(intRegex)) {
            v = new IntVar(Integer.parseInt(t));
        }
        else if (t.matches(fpRegex)) {
            v = new DoubleVar(Double.parseDouble(t));
        }
        else if (t.charAt(0) == '-') {
            t = t.replace("-", "");
            if (t.matches(varNameRegex)) {
                v = this.getVar(t);
                if (v != null) v.invert();
            }
        }
        else if (t.matches(varNameRegex)){
            v = this.getVar(t);
        }
        else {
            throw new LotusException("unknownSymbol", t);
        }

        return v;
    }

	private Variable calculate(Variable v1, Variable v2, String op) throws LotusException {
		boolean intOpns = true;
		Variable answ = null;

		if (v2 instanceof DoubleVar || (v1 != null && v1 instanceof DoubleVar)) {
			intOpns = false;
		}

		/* If it doesn't and the operation is a '-'
		 * simply return -v2 (it's a number sign).
		 * Else, return v2 to prevent a crash in
		 * the operations below
		 */
		if (v1 == null) {
            if (op.equals("-")) {
                if (intOpns) {
                    answ = new IntVar(0 - v2.toInt());
                }
                else {
                    answ = new DoubleVar(0.0 - v2.toDouble());
                }
            }
            else if (op.equals("+")) {
                answ = v2;
            }
            else if (op.matches(operRegex)) {
                throw new LotusException("syntaxError", v1 + " " + op + " " + v2);
            }
        }
		else {
    		switch (op) {
    			/* pow() returns double. I will implement
    			 * a binary exponentiation later...
    			 */
    			case "^":
    			answ = new DoubleVar(Math.pow(v1.toDouble(), v2.toDouble()));
    			break;

    			case "*":
    			if (intOpns) answ = new IntVar(v1.toInt() * v2.toInt());
    			else answ = new DoubleVar(v1.toDouble() * v2.toDouble());
    			break;

                case "%":
                if (v2 instanceof IntVar && !v2.equals(0) ||
                    v2 instanceof DoubleVar && !v2.equals(0.0))
                {
                    if (intOpns) answ = new IntVar(v1.toInt() % v2.toInt());
                    else {
                        throw new LotusException("nonIntMod", v1 + " % " + v2);
                    }
                }
                else {
                    throw new LotusException("divisionByZero", v1 + " / " + v2);
                }
                break;

    			case "/":
    			/* if v2 is not zero, then I can properly divide...
    			 * I think this regex works as it should, identifying
    			 * integer zeroes and floating point zeroes, but needs
    			 * more testing (or a different, better way lol)
    			 */
    			if (v2 instanceof IntVar && !v2.equals(0) ||
                    v2 instanceof DoubleVar && !v2.equals(0.0))
                {
    				if (intOpns) {
    					answ = new IntVar(v1.toInt() / v2.toInt());
    				}
    				else {
    					answ = new DoubleVar(v1.toDouble() / v2.toDouble());
    				}
    			}
    			else {
                    throw new LotusException("divisionByZero", v1 + " / " + v2);
                }
    			break;

    			case "+":
    			if (intOpns) {
    				answ = new IntVar(v1.toInt() + v2.toInt());
    			}
    			else {
    				answ = new DoubleVar(v1.toDouble() + v2.toDouble());
    			}
    			break;

    			case "-":
    			if (intOpns) {
    				answ = new IntVar(v1.toInt() - v2.toInt());
    			}
    			else {
    				answ = new DoubleVar(v1.toDouble() - v2.toDouble());
    			}
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
		boolean breakLine = false;

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");
		if (line.startsWith("println")) {
			breakLine = true;
		}

		line = line.replaceFirst("(print|println)( )*\\(", "");
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
		int offset = content.indexOf("$", fromIndex + 1);

		if (offset > fromIndex) {
			// name = content.substring(fromIndex, offset);
			name = content.substring(fromIndex + 1, offset);
			if (name.matches(varNameRegex)) {
				var = this.getVar(name);
				if (var == null) {
					throw new LotusException("varNotFound", name);
				}
			}
		}

		return var;
	}

	// directly assigns the input read into the requested variable
	// (if there's any) and returnds all the successful read inputs
	// as a string. A successfully read variable will be returned
	// as its name between '$'.
	private void scan(String line) throws LotusException {
		int i, j, max;
		String[] input;
		Variable v, other;
		String lineEnding, name;
		Scanner sc = new Scanner(System.in);
		Matcher varMatcher, intMatcher, fpMatcher, stringMatcher;

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");
		line = line.replaceFirst("(scan)( )*\\(", "");

		if (!line.matches(scanContentRegex)) {
			throw new LotusException("syntaxError", line);
		}

		max = 0;
		varMatcher = varName.matcher(line);
		while (varMatcher.find()) {
			max++;
		}

		varMatcher = varName.matcher(line); // varMatcher = varMatcher.reset(); ?
		input = sc.nextLine().split(" ");

		for (i = 0; i < max; i++) {
			varMatcher.find();
			name = varMatcher.group();

			if ((v = this.getVar(name)) != null) {
				if (input[i].matches(intRegex)) {
					other = new IntVar(Integer.parseInt(input[i]));
				}
				else if (input[i].matches(fpRegex)) {
					other = new DoubleVar(Double.parseDouble(input[i]));
				}
				else {
					other = new StringVar(input[i]);
				}

				this.setVar(v, other);
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

        result.put("unless", true); // ? :)
        result.put("if", true);
        result.put("elsif", true);
        result.put("else", true);

        result.put("for", true);
        result.put("while", true);
        result.put("break", true);
        result.put("continue", true);

        result.put("fn", true);
        // result.put("main", true); // ?
        return Collections.unmodifiableMap(result);
    }

	public static final String typeRegex = "int|double|string|bool";
	public static final String varNameRegex = "(?!\\d)\\w+";
	public static final String declRegex = "(let)( )+((.+)+((,( )*(.+)+)( )*)*)( )*:( )*(\\w)+;";
	public static final String atrRegex = varNameRegex + "( )*=( )*.+";
	public static final String wholeAtrRegex = atrRegex + ";";
	public static final String stripAtrRegex = "( )*=( )*";
	public static final String printRegex = "(print|println)( )*\\(.*\\)( )*;";
	public static final String scanRegex = "(scan)( )*\\(.*\\)( )*;";
	public static final String scanContentRegex = "(\\$" + varNameRegex + "\\$)(( )*,( )*(\\$" + varNameRegex + "\\$))*";
	// public static final String printVarRegex = ".*(\\$(\\w)+\\$).*";
	// public static final String stripNameRegex = "( )*=( )*.+;";
	// public static final String stripExpRegex = "(\\w)+( )*=( )*";
    public static final String operRegex = "\\^|\\*|\\%|\\/|\\+|\\-|\\(|\\)";
    public static final String intRegex = "[+-]?[0-9]+";
    public static final String boolRegex = "(true|false)";
    // public static final String zeroRegex = "0+(\\.)?0*";
    /* fpRegex taken from Java documentation */
	private static final String Digits     = "(\\p{Digit}+)";
	private static final String HexDigits  = "(\\p{XDigit}+)";
	// an exponent is 'e' or 'E' followed by an optionally
	// signed decimal integer.
	private static final String Exp        = "[eE][+-]?"+Digits;
	public static final String fpRegex    =
		"[+-]?(" + // Optional sign character
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
}
