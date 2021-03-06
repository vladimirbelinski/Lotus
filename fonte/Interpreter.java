package fonte;
/*******************************************************************************
Name: Interpreter.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Class Interpreter of Lotus, a programming language based on Java.
             Responsible for the interpretation of the code.
*******************************************************************************/

import java.util.*;
import java.util.regex.*;

class Interpreter {
	private static final int IF = 1, FOR = 2, WHILE = 3;
	private boolean doBreak, doContinue;
	private Stack<Integer> rec;
	private HashMap<String, Variable> vars;
	// this doesn't make that much sense now, but it's faster to
	// look up in a hash than an array. And later on we can replace
	// the boolean value to a Runnable...
	private static final Map<String, Boolean> reservedWords = mapReservedWords();
	private static boolean patternsInitd = false;
	public static Pattern typeP, wholeDeclP, varNameP, atrP, wholeAtrP, semicP, wholePrintP, wholeScanP, wholeScanlnP, wholeOpP, signP, intP, fpP, charP, strP, strAssignP, quotMarkP, strBackP, parenP, numBuildP, boolP, upperCaseP, strNotEmptyP, opGroupP, ufpP, jufpP, jfpP, quotInStrP, invalidFpP, wholeIfP, wholeElsifP, wholeElseP, ifP, elsifP, ifEndingP, wholeWhileP, wholeForP, forSplitP, anyP, fixAtrP, fixAtrTypeP;

	public Interpreter() {
		this.vars = new HashMap<String, Variable>();
		this.rec = new Stack<Integer>();
		this.doBreak = this.doContinue = false;

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

	public void setVar(Variable v, Variable other) {
		v.setValue(other);
	}

	public void setVar(Variable v, String value) {
		v.setValue(new StringVar(value));
	}

	public void execute(ArrayList<Line> code) throws LotusException {
		Matcher wholeDeclM, wholeAtrM, wholePrintM, wholeScanM, wholeScanlnM, wholeIfM, elsifM, elseM, wholeWhileM, wholeForM, forSplitM;
		int i, j, max, semicolon, clBracket;
		String command = "", forInit, forCond, forInc;
		ArrayList<ArrayList<Line>> ifChain;
		ArrayList<Line> codeBlock, loopInc;
		Expression loopCond;
		boolean endOfChain;
		Line line = null;

		max = code.size();
		for (i = 0; i < max; i++) {

			if (!rec.empty() && (this.doContinue || this.doBreak)) {
				if (rec.search(FOR) == 1 && this.doContinue) {
					i = code.size() - 1;
					this.doContinue = false;
				}
				else if (rec.search(WHILE) >= 1 || rec.search(FOR) >= 1) {
					if (rec.search(WHILE) == 1) {
						if (this.doContinue) this.doContinue = false;
					}
					else if (rec.search(FOR) == 1) {
						if (this.doContinue) this.doContinue = false;
					}

					return;
				}
			}

			try {
				line = code.get(i);
				command = line.toString();

			    if (command.isEmpty()) {
			        continue;
			    }

				wholeIfM = wholeIfP.matcher(command);
				wholeAtrM = wholeAtrP.matcher(command);
				wholeDeclM = wholeDeclP.matcher(command);
				wholeScanM = wholeScanP.matcher(command);
				wholePrintM = wholePrintP.matcher(command);
				wholeScanlnM = wholeScanlnP.matcher(command);
				wholeWhileM = wholeWhileP.matcher(command);
				wholeForM = wholeForP.matcher(command);

				if (wholeDeclM.matches()) {
					this.let(command);
				}
				else if (wholeAtrM.matches()) {
					this.assign(command);
				}
				else if (wholePrintM.matches()) {
					this.print(command);
				}
				else if (wholeScanM.matches()) {
					this.scan(command);
				}
				else if (wholeScanlnM.matches()) {
					this.scanln(command);
				}
				else if (wholeIfM.matches()) {
					ifChain = new ArrayList<ArrayList<Line>>();
					endOfChain = false;

					while (!endOfChain) {
						command = code.get(i).toString();

						if (command.isEmpty()) {
							i++;
							continue; // ignoring commented and blank lines
						}

						if (!endOfChain) {
							codeBlock = buildBlock(code, i);
							ifChain.add(codeBlock);
							i += codeBlock.size();

							if (i < max) {
								command = code.get(i).toString();
								while (i < max && command.isEmpty()) {
									i++;
									command = code.get(i).toString();
								}

								elsifM = wholeElsifP.matcher(command);
								elseM = wholeElseP.matcher(command);
								if (!elsifM.matches() && !elseM.matches()) {
									endOfChain = true;
									i -= 1;
								}
							}
							else {
								endOfChain = true;
								i -= 1;
							}
						}
					}

					this.rec.push(IF);
					this.runIfChain(ifChain);
					this.rec.pop();
				}
				else if (wholeWhileM.matches()) {
					codeBlock = buildBlock(code, i);
					i += codeBlock.size() - 1;
					codeBlock.remove(0);
					codeBlock.remove(codeBlock.size() - 1);

					loopCond = new Expression(command.substring(command.indexOf("(") + 1, command.lastIndexOf(")")));

					this.rec.push(WHILE);
					while (!this.doBreak && this.solve(loopCond).toBool()) {
						this.execute(codeBlock);
					}
					this.doBreak = false;
					this.rec.pop();
				}
				else if (wholeForM.matches()) {
					// getting the string: command.substring(0, index of ';' after condition);
					forSplitM = forSplitP.matcher(command);
					forSplitM.find();
					forCond = forSplitM.group().substring(0, forSplitM.end() - 2);

					// for increment. It's the command.substring starting right after the
					// previous match until the end, without ") {", with ';' appended
					forInc = command.substring(forSplitM.end()).replaceFirst("\\)( )*\\{", "").trim() + ";";
					if (forInc.equals("break;") || forInc.equals("continue;")) {
						throw new LotusException("invalidLoopComm", command);
					}

					// for condition
					forSplitM = forSplitP.matcher(forCond);
					forCond = forSplitM.replaceFirst("");
					loopCond = new Expression(forCond);

					// for init
					forInit = command.substring(forSplitM.start(), forSplitM.end()).replaceFirst("for( )*\\(", "").trim();
					if (forInit.equals("break;") || forInit.equals("continue;")) {
						throw new LotusException("invalidLoopComm", command);
					}
					codeBlock = new ArrayList<Line>();
					codeBlock.add(new Line(forInit, line.getNumber()));
					this.execute(codeBlock);

					// building the block of code that will be executed
					codeBlock = buildBlock(code, i);
					i += codeBlock.size() - 1;
					codeBlock.remove(0);
					codeBlock.remove(codeBlock.size() - 1);

					// for increment is last line of the block going to be executed
					codeBlock.add(new Line(forInc, line.getNumber()));

					this.rec.push(FOR);
					while (!this.doBreak && this.solve(loopCond).toBool()) {
						this.execute(codeBlock);
					}
					this.doBreak = false;
					this.rec.pop();
				}
				else if (command.equals("break;") || command.equals("continue;")) {
					if (!rec.empty() && (rec.search(FOR) >= 1 || rec.search(WHILE) >= 1)) {
						if (command.equals("break;")) {
							this.doBreak = true;
							return;
						}
						else {
							if (rec.search(FOR) == 1) {
								i = code.size() - 2;
								continue;
							}
							else {
								this.doContinue = true;
								break;
							}
						}
					}
					else {
						throw new LotusException("notLooping", command);
					}
				}
				else {
					throw new LotusException("syntaxError", command);
				}
			} catch (LotusException e) {
				e.setNumber(line.getNumber());
				throw e;
			}
		}
	}

	private void printBlock(ArrayList<Line> block, boolean sep) {
		int i, max = block.size();

		if (sep) System.out.println("----------------------------------------");
		for (i = 0; i < max; i++) {
			System.out.println(block.get(i));
		}
		if (sep) System.out.println("----------------------------------------");
	}

	private void runIfChain(ArrayList<ArrayList<Line>> chain) throws LotusException {
		int i, max;
		boolean done;
		Matcher elseM;
		String statement;
		Expression condition;
		ArrayList<Line> block = chain.get(0);

		statement = block.get(0).toString();
		statement = statement.substring(statement.indexOf("(") + 1, statement.lastIndexOf(")"));
		condition = new Expression(statement);

		if (this.solve(condition).toBool()) {
			block.remove(0);
			block.remove(block.size() - 1);
			this.execute(block);
		}
		else {
			done = false;
			max = chain.size();

			for (i = 1; i < max && !done; i++) {
				block = chain.get(i);
				statement = block.get(0).toString();

				elseM = wholeElseP.matcher(statement);
				if (elseM.matches()) {
					block.remove(0);
					block.remove(block.size() - 1);
					this.execute(block);
					done = true;
				}
				else {
					statement = statement.substring(statement.indexOf("(") + 1, statement.lastIndexOf(")"));
					condition = new Expression(statement);
					if (this.solve(condition).toBool()) {
						block.remove(0);
						block.remove(block.size() - 1);
						this.execute(block);
						done = true;
					}
				}
			}
		}
	}

	private ArrayList<Line> buildBlock(ArrayList<Line> code, int index) throws LotusException {
		ArrayList<Line> block = new ArrayList<Line>();
		int i = index, max = code.size(), bracketCount = 0;
		Matcher ifM, elsifM, elseM, whileM, forM;
		boolean endOfBlock = false;
		String command, lineEnding;
		Line line = null;
		int clBracket;

		while (i < max && !endOfBlock) {
			line = code.get(i);
			command = line.toString();

			ifM = wholeIfP.matcher(command);
			elsifM = wholeElsifP.matcher(command);
			elseM = wholeElseP.matcher(command);
			whileM = wholeWhileP.matcher(command);
			forM = wholeForP.matcher(command);

			clBracket = command.lastIndexOf("}");
			if (clBracket >= 0) {
				bracketCount--;
			}
			else if (ifM.matches() || elsifM.matches() || elseM.matches() || whileM.matches() || forM.matches()) {
				bracketCount++;
			}

			if (!endOfBlock) {
				block.add(line);
				i++;
			}

			if (bracketCount == 0) {
				endOfBlock = true;
			}
		}

		if (!endOfBlock) {
			// verify this:
			throw new LotusException("bracketNotFound", code.get(index).toString());
		}

		return block;
	}

	private void let(String line) throws LotusException {
		String[] decl = this.fixDecl(line);
		int i, max = decl.length - 1;
		Matcher atrM, varM, strM;
		Variable v = null;
		String varName;

		for (i = 0; i < max; i++) {
			if (reservedWords.containsKey(decl[i])) {
				throw new LotusException("usingReservedWords", decl[i] + ", from \"" + line + "\"");
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
				varM = varNameP.matcher(decl[i]);

				if (atrM.matches()) {
					varM.find();

					// if the variable being declared is a string and
					// it has an assignment, it must have ""
					if (v instanceof StringVar) {
						strM = strP.matcher(decl[i].substring(decl[i].indexOf("=") + 1).trim());
						if (!strM.matches()) {
							throw new LotusException("syntaxError", line);
						}
					}

					varName = varM.group();
					if (!this.hasVar(varName)) {
						this.newVar(varName, v);
					}
					this.assign(decl[i] + ";");
				}
				else {
					if (varM.matches()) {
						this.newVar(decl[i], v);
					}
					else {
						throw new LotusException("invalidVarName", decl[i] + ", from \"" + line + "\"");
					}
				}
			}
			else {
				throw new LotusException("invalidType", decl[max] + ", from \"" + line + "\"");
			}
		}
	}

	public String[] fixDecl(String line) throws LotusException {
		int i, j;
		Matcher strM;
		String tmp = "";
		String[] aux, output = null;
		TreeMap<Integer, String> tokens = new TreeMap<Integer, String>();

		// from right after "let" and until ';', inclusive
		line = line.substring(3);
		// type
		i = line.lastIndexOf(":");
		tokens.put(i + 1, line.substring(i + 1, line.length() - 1).trim());
		line = line.substring(0, i);

		aux = line.split(",");
		for (i = 0; i < aux.length; i++) {

			strM = strP.matcher(aux[i].trim());
			if (aux[i].contains("\"") && !strM.find()) {
				tmp = aux[i];

				for (j = i + 1; j < aux.length; j++) {
					tmp += "," + aux[j];

					strM = strP.matcher(tmp.trim());
					if (strM.find()) {
						break;
					}
				}
				i = j;
				tokens.put(i, tmp.trim());
			}
			else {
				tokens.put(i, aux[i].trim());
			}
		}

		output = new String[tokens.size()];
		tokens.values().toArray(output);

		return output;
	}

	public void assign(String line) throws LotusException {
		int equalsIndex;
        Variable v = null;
        Expression assign = null;
		String[] atr = new String[2];
		Matcher quotMarkM, strBackM, strAssignM, quotInStrM;

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

			if ((v = this.getVar(atr[0])) != null) {
				this.setVar(v, atr[1]);
			}
			else {
				throw new LotusException("varNotFound", atr[0] + ", from \"" + line + "\"");
			}
        }
		else {
			atr[1] = atr[1].substring(0, atr[1].lastIndexOf(";")).trim();

			if ((v = this.getVar(atr[0])) != null) {
				this.setVar(v, this.solve(new Expression(atr[1])));
			}
			else {
				throw new LotusException("varNotFound", atr[0] + ", from \"" + line + "\"");
			}
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

		// System.out.println("[INFO_LOG]: SOLVE_EXP = {" + exp + "}");
		// System.out.println("[INFO_LOG]: SOLVE_TOKENS = {" + tokens + "}");

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

			if (i >= t.length) {
				throw new LotusException("invalidExp", exp.original);
			}

			// then, the operation char is at i's position in the array
			op = t[i];
			// as it's postfix, the 2nd operand is right before op
            num2 = this.getOperand(t[i - 1]);

			/* now, if i > 1, then 1st operand is certainly 2 positions
			 * before op. Else, it doesn't exist lol
			 */
			if (!op.equals("!") && i > 1) {
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

			// System.out.println("[INFO_LOG]: CALCULATE = {" + num1 + ", " + op + ", " + num2 + "}");

			answ = this.calculate(num1, num2, op);
			t[i] = answ.toString();
		}

		// System.out.println("[INFO_LOG]: SOLVE_RESULT = {" + answ + "}");

		return answ;
	}

    private Variable getOperand(String t) throws LotusException {
        Matcher intM, fpM, boolM, strM, quotInStrM, varNameM, anyM;
        Variable v = null;
		int index;

        fpM = jfpP.matcher(t);
        intM = intP.matcher(t);
		strM = strP.matcher(t);
		anyM = anyP.matcher(t);
		boolM = boolP.matcher(t);
		varNameM = varNameP.matcher(t);

		// System.out.println("[INFO_LOG]: GET_OPERAND = {" + t + "}");

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
			index = t.lastIndexOf("\"");

			t = t.substring(0, index);
			// replacing all \" for an actual "
			quotInStrM = quotInStrP.matcher(t);
			t = quotInStrM.replaceAll("\"");

			v = new StringVar(t);
		}
		else if (varNameM.matches()) {
			if ((v = this.getVar(t)) == null) {
				throw new LotusException("varNotFound", t);
			}
		}
		// for when I operate with direct strings in an expression.
		// will probably never fall into the else, but leave it alone :P
		else if (anyM.matches()) {
			v = new StringVar(t);
		}
		else {
			throw new LotusException("unknownSymbol", t);
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
				answ = v2.toBoolVar().inverted();
			}
            else if (opM.matches()) {
                throw new LotusException("invalidExp", v2 + " " + op + " ?");
            }
        }
        else {
            switch (op) {
                case "^":
                answ = v1.pow(v2);
                break;

                case "*":
				answ = v1.times(v2);
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
		Matcher varNameM;
		Variable v = null;
		boolean breakLine = false;
		String lineEnding, exp, text = "";

		if (line.startsWith("println")) {
			breakLine = true;
		}

		line = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")"));
		content = line.split("");
		for (i = 0; i < content.length; i++) {
			// \t 	Insert a tab in the text at this point.
			// \n 	Insert a newline in the text at this point.
			// \$ 	Insert a '$' character in the text at this point.
			// \- 	Insert a hyphen character in the text at this point.
			// \\ 	Insert a backslash character in the text at this point.
			if (content[i].equals("\\") && i + 1 < content.length) {

				if (content[i + 1].equals("t")) {
					text += "\t";
				}
				else if (content[i + 1].equals("n")) {
					text += "\n";
				}
				else if (content[i + 1].equals("$")) {
					text += "$";
				}
				else if (content[i + 1].equals("-")) {
					text += "-";
				}
				else if (content[i + 1].equals("\\")) {
					if (i + 2 < content.length && content[i + 2].equals("n")) {
						text += "\\n";
						i++;
					}
					else text += "\\";
				}
				else {
					throw new LotusException("unknownEscape", content[i] + content[i + 1] + ", from \"" + line + "\"");
				}

				i++;
			}
			// content.length - 2 because it's the maximum index in the
			// string for a variable or expression to exist, for example: $x$
			else if (content[i].equals("$")) {
				// i is the index of the first '$'
				exp = this.getExp(line, i);

				varNameM = varNameP.matcher(exp);
				if (varNameM.matches()) {
					v = this.getVar(exp);
					if (v == null) {
						throw new LotusException("varNotFound", exp + ", from \"" + line + "\"");
					}
					text += v.toString();
					i = line.indexOf("$", i + 1);
				}
				else if (!exp.isEmpty()) {
					text += this.solve(new Expression(exp));
					i = line.indexOf("$", i + 1);
				}
				else {
					throw new LotusException("invalidExp", line);
				}
			}
			else {
				text += content[i];
			}
		}
		if (breakLine) System.out.println(text);
		else System.out.print(text);
	}

	private String getExp (String content, int fromIndex) {
		int offset = content.indexOf("$", fromIndex + 1);

		if (offset > fromIndex) {
			return content.substring(fromIndex + 1, offset);
		}

		return "";
	}

	// directly assigns the input read into the requested variable(s)
	// interprets mutiple inputs as separated by spaces or line breaks.
	// to read a full line, use scanln
	private void scan(String line) throws LotusException {
		int i, j, max;
		String[] input;
		Variable v, other;
		String lineEnding, name;
		Matcher varM, intM, jfpM, strM;
		Scanner sc = new Scanner(System.in);

		line = line.substring(line.indexOf("(") + 1, line.indexOf(")")).replaceAll(" ", "");

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
						jfpM = jfpP.matcher(input[j]);

						if (intM.matches()) {
							other = new IntVar(Integer.parseInt(input[j]));
						}
						else if (jfpM.matches()) {
							other = new DoubleVar(Double.parseDouble(input[j]));
						}
						else {
							other = new StringVar(input[j]);
						}

						this.setVar(v, other);
					}
					else {
						throw new LotusException("varNotFound", name + ", from \"" + line + "\"");
					}

					if (j + 1 < input.length) i++;
				}
			}
		}
	}

	private void scanln(String line) throws LotusException {
		Variable v;
		Matcher varM, strM;
		String lineEnding, name, input;
		Scanner sc = new Scanner(System.in);

		line = line.substring(line.indexOf("(") + 1, line.indexOf(")")).replaceAll(" ", "");

		varM = varNameP.matcher(line);
		while (varM.find()) {
			input = sc.nextLine();
			name = varM.group();

			if ((v = this.getVar(name)) != null) {
				this.setVar(v, input);
			}
			else {
				throw new LotusException("varNotFound", name + ", from \"" + line + "\"");
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

        // result.put("unless", true);
        result.put("if", true);
        result.put("elsif", true);
        result.put("else", true);

        result.put("for", true);
        result.put("while", true);
        result.put("break", true);
        result.put("continue", true);

        // result.put("fn", true);
        // result.put("main", true);
        return Collections.unmodifiableMap(result);
    }

	public static final String semicR = "( )*;";
	public static final String typeR = "int|double|string|bool";
	public static final String fixAtrTypeR = ":( )*(" + typeR + ")" + semicR;
	public static final String varNameR = "[A-Za-z_][A-Za-z_0-9]*";
	public static final String wholeDeclR = "(let)( )+(.+)( )*:( )*(\\w)+" + semicR;

	public static final String parenR = "\\(|\\)";
	public static final String boolOpR = "\\!|\\&\\&|\\|\\|";
	public static final String compOpR = "\\!\\=|\\<\\=|\\=\\=|\\>\\=|\\<|\\>";
	public static final String compOrBoolR = compOpR + "|" + boolOpR;
	public static final String mathOpR = "\\-|\\+|\\/|\\%|\\*|\\^";

	public static final String wholeOpR = parenR + "|" + mathOpR + "|" + compOpR + "|" + boolOpR;

	public static final String atrR = varNameR + "( )*=( )*.+";
	public static final String wholeAtrR = atrR + semicR;
	public static final String fixAtrR = ".+[,:]";
	public static final String stripAtrR = "( )*=( )*";

	public static final String fnParentheses = "( )*\\(.*\\)" + semicR;
	public static final String printR = "(print|println)";
	public static final String wholePrintR = printR + fnParentheses;
	public static final String scanContentR = "(" + varNameR + ")(( )*,( )*(" + varNameR + "))*";
	public static final String wholeScanR = "(scan)( )*\\(( )*" + scanContentR + "( )*\\)" + semicR;
	public static final String wholeScanlnR = "(scanln)( )*\\(( )*" + scanContentR + "( )*\\)" + semicR;

	public static final String ifR = "(if)( )*\\(( )*";
	public static final String ifEnding = "( )*\\)( )*\\{";
	public static final String elsifR = "(elsif)( )*\\(( )*";
	public static final String wholeElseR = "(else)( )*\\{";

	public static final String forSplitR = "(for)( )*\\(.+\\;(( )*)\\b";
	public static final String wholeForR = "(for)( )*\\((.+;){2}.+\\)( )*\\{";

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

	// from Javadoc
	private static final String Digits     = "(\\p{Digit}+)";
	private static final String HexDigits  = "(\\p{XDigit}+)";
	// an exponent is 'e' or 'E' followed by an optionally
	// signed decimal integer.
	private static final String Exp        = "[eE][+-]?"+Digits;
	public static final String jufpR    =
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
	    // The Java Language Specification.

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
	    "[fFdD]?)";
	public static final String jfpR = signR + "?(" + jufpR + ")";

	private static void initPatterns() {
		anyP = Pattern.compile(".+");

		varNameP = Pattern.compile(varNameR);
		typeP = Pattern.compile(typeR);
		fixAtrTypeP = Pattern.compile(fixAtrTypeR);
		atrP = Pattern.compile(atrR);

		wholeOpP = Pattern.compile(wholeOpR);
		signP = Pattern.compile(signR);
		intP = Pattern.compile(intR);
		ufpP = Pattern.compile(ufpR);
		fpP = Pattern.compile(fpR);
		jufpP = Pattern.compile(jufpR);
		jfpP = Pattern.compile(jfpR);
		invalidFpP = Pattern.compile(invalidFpR);
		boolP = Pattern.compile(boolR);
		charP = Pattern.compile("\\w");
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
		fixAtrP = Pattern.compile(fixAtrR);

		semicP = Pattern.compile(semicR);
		wholePrintP = Pattern.compile(wholePrintR);
		wholeScanP = Pattern.compile(wholeScanR);
		wholeScanlnP = Pattern.compile(wholeScanlnR);

		ifP = Pattern.compile(ifR);
		wholeIfP = Pattern.compile(ifR + ".+" + ifEnding);
		elsifP = Pattern.compile(elsifR);
		wholeElsifP = Pattern.compile(elsifR + ".+" + ifEnding);
		wholeElseP = Pattern.compile(wholeElseR);
		ifEndingP = Pattern.compile(ifEnding);

		wholeWhileP = Pattern.compile("while( )*\\(.+\\)( )*\\{");
		forSplitP = Pattern.compile(forSplitR);
		wholeForP = Pattern.compile(wholeForR);

		parenP = Pattern.compile("[()]");
		numBuildP = Pattern.compile("(\\w|\\.)+");

		patternsInitd = true;
	}
}
