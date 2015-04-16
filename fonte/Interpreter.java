import java.util.*;
import java.util.regex.*;

class Interpreter {
	private HashMap<String, Variable> vars;
	// this doesn't make that much sense now, but it's faster to
	// look up in a hash than an array. And later on we can replace
	// the boolean value to a Runnable...
	private static final Map<String, Boolean> reservedWords = mapReservedWords();
	private static boolean patternsInitd = false;
	public static Pattern typePattern, wholeDeclPattern, varNamePattern, atrPattern, wholeAtrPattern, wholePrintPattern, wholeScanPattern, scanContentPattern, opPattern, signPattern, intPattern, fpPattern, boolAssignPattern, charPattern, stringPattern, stringAssignPattern;

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
		Matcher intMatcher = intPattern.matcher(value), fpMatcher = fpPattern.matcher(value);

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
				throw new LotusException("cantAssignString", v.getClass().toString());
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
				throw new LotusException("cantAssignString", v.getClass().toString());
	        }
		}
		else {
			throw new LotusException("cantAssignString", v.getClass().toString());
		}
	}

	/* ---------------------------------------------------------------------- */

	public void execute(String line) throws LotusException {
		Matcher declMatcher, wholeAtrMatcher, wholePrintMatcher, scanMatcher;
		int semicolon = line.indexOf(";");

		if (semicolon < 0) {
			throw new LotusException("syntaxError", line);
		}

		String lineEnding = line.substring(semicolon + 1).trim();
		if (!lineEnding.isEmpty() && !lineEnding.startsWith("--")) {
			throw new LotusException("multipleCommands", line);
		}

		line = line.substring(0, line.indexOf(";") + 1);

		declMatcher = wholeDeclPattern.matcher(line);
		wholeAtrMatcher = wholeAtrPattern.matcher(line);
		wholePrintMatcher = wholePrintPattern.matcher(line);
		scanMatcher = wholeScanPattern.matcher(line);

		if (declMatcher.matches()) {
			this.let(line);
		}
		else if (wholeAtrMatcher.matches()) {
			// +=, -=...?
			this.assign(line);
		}
		else if (wholePrintMatcher.matches()) {
			this.print(line);
		}
		else if (scanMatcher.matches()) {
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
				atrMatcher = atrPattern.matcher(decl[i]);
				if (atrMatcher.matches()) {
					varMatcher = varNamePattern.matcher(decl[i]);
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
        Variable v = null;
        Expression assign = null;
		String[] atr = line.split(stripAtrRegex);
		Matcher stringAssignMatcher, boolAssignMatcher;

		// if it's a string (enclosed with "");
		stringAssignMatcher = stringAssignPattern.matcher(atr[1]);
		boolAssignMatcher = boolAssignPattern.matcher(atr[1]);
        if (stringAssignMatcher.matches()) {
            atr[1] = atr[1].replaceFirst("\\\"", "");
            atr[1] = atr[1].replaceFirst("\\\"( )*;", "");

            this.setVar(this.getVar(atr[0]), atr[1]);
        }
        else if (boolAssignMatcher.matches()){
            atr[1] = atr[1].replaceFirst("( )*;", "");

            this.setVar(this.getVar(atr[0]), Boolean.parseBoolean(atr[1]));
        }
        else {
            v = this.getVar(atr[0]);
			if (v instanceof StringVar) {
				throw new LotusException("cantAssign", Expression.class + ";" + v.getClass());
			}
            this.setVar(v, this.solve(new Expression(atr[1])));
        }
    }

	private Variable solve(Expression exp) throws LotusException {
        Variable answ = null, num1 = null, num2 = null;
		String tokens = exp.infixToPostfix();
		String t[] = tokens.split(" ");
		String front[], back[];
		int i = 0, offset = 0;
		Matcher opMatcher;
		String op;

        if (t.length == 1) {
            answ = this.getOperand(t[0]);
        }

		while (t.length > 1) {

			opMatcher = opPattern.matcher(t[i]);
			// advance until you don't find an operation to perform
			while (i < t.length && !opMatcher.matches()) {
				i++;
				if (i < t.length) opMatcher = opPattern.matcher(t[i]);
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
		Matcher intMatcher, fpMatcher, varNameMatcher;
        Variable v = null;

		intMatcher = intPattern.matcher(t);
		fpMatcher = fpPattern.matcher(t);
		varNameMatcher = varNamePattern.matcher(t);

        if (intMatcher.matches()) {
            v = new IntVar(Integer.parseInt(t));
        }
        else if (fpMatcher.matches()) {
            v = new DoubleVar(Double.parseDouble(t));
        }
        else if (t.charAt(0) == '-') {
            t = t.replace("-", "");
            if (varNameMatcher.matches()) {
                v = this.getVar(t);
                if (v != null) v.invert();
            }
        }
        else if (varNameMatcher.matches()){
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
		Matcher opMatcher = opPattern.matcher(op);

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
            else if (opMatcher.matches()) {
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

		line = line.replaceFirst(printRegex + "( )*\\(", "");
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
			// name = content.substring(fromIndex, offset);
			name = content.substring(fromIndex + 1, offset);
			varNameMatcher = varNamePattern.matcher(name);
			if (varNameMatcher.matches()) {
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
		Matcher scanContentMatcher, varMatcher, intMatcher, fpMatcher, stringMatcher;

		lineEnding = line.substring(line.lastIndexOf(")"));
		line = line.replace(lineEnding, "");
		line = line.replaceFirst("(scan)( )*\\(", "");

		scanContentMatcher = scanContentPattern.matcher(line);
		if (!scanContentMatcher.find()) {
			throw new LotusException("syntaxError", line);
		}

		max = 0;
		varMatcher = varNamePattern.matcher(line);
		while (varMatcher.find()) {
			max++; // counting how many matches (vars) I got inside scan
		}
		// varMatcher = varNamePattern.matcher(line);
		varMatcher = varMatcher.reset();

		for (i = 0; i < max; i++) {
			input = sc.nextLine().split(" ");

			for (j = 0; j < input.length; j++) {
				varMatcher.find();
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
	public static final String wholeDeclRegex = "(let)( )+((.+)+((,( )*(.+)+)( )*)*)( )*:( )*(\\w)+;";
	public static final String atrRegex = varNameRegex + "( )*=( )*.+";
	public static final String wholeAtrRegex = atrRegex + ";";
	public static final String stripAtrRegex = "( )*=( )*";
	public static final String printRegex = "(print|println)";
	public static final String wholePrintRegex = printRegex + "( )*\\(.*\\)( )*;";
	public static final String wholeScanRegex = "(scan)( )*\\(.*\\)( )*;";
	public static final String scanContentRegex = "(\\$" + varNameRegex + "\\$)(( )*,( )*(\\$" + varNameRegex + "\\$))*";
	// public static final String printVarRegex = ".*(\\$(\\w)+\\$).*";
	// public static final String stripNameRegex = "( )*=( )*.+;";
	// public static final String stripExpRegex = "(\\w)+( )*=( )*";
    public static final String opRegex = "\\^|\\*|\\%|\\/|\\+|\\-|\\(|\\)";
    public static final String signRegex = "[+-]";
    public static final String intRegex = signRegex + "?[0-9]+";
    public static final String boolRegex = "(true|false)";
    public static final String stringRegex = "\\\"\\S+\\\"";
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

	private static void initPatterns() {
		varNamePattern = Pattern.compile(varNameRegex);
		typePattern = Pattern.compile(typeRegex);
		atrPattern = Pattern.compile(atrRegex);

		opPattern = Pattern.compile(opRegex);
		signPattern = Pattern.compile(signRegex);
		intPattern = Pattern.compile(intRegex);
		fpPattern = Pattern.compile(fpRegex);
		charPattern = Pattern.compile("\\w");
		stringPattern = Pattern.compile(stringRegex);

		boolAssignPattern = Pattern.compile(boolRegex + "( )*;");
		stringAssignPattern = Pattern.compile(stringRegex + "( )*;");

		wholeDeclPattern = Pattern.compile(wholeDeclRegex);
		wholeAtrPattern = Pattern.compile(wholeAtrRegex);
		wholePrintPattern = Pattern.compile(wholePrintRegex);
		wholeScanPattern = Pattern.compile(wholeScanRegex);
		scanContentPattern = Pattern.compile(scanContentRegex);

		patternsInitd = true;
	}
}
