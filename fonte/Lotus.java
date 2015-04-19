/*******************************************************************************
Name: Main.java
Authors: Acácia dos Campos da Terra- terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com
Version: 1.0
Copyright: Your copyleft.
Description: Main class of Lotus, a programming language based on Java.
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.util.regex.*;

/* To execute the test, run:
 * javac *.java
 * java Lotus ../exemplos/testing.lt
 */

class Lotus {
    public static void main(String[] args) throws Exception {
        File f;
        Scanner sc;
        int max, ind = 0;
        Matcher extMatcher;
        String tmpInput, lineEnding;
        Interpreter lotus = new Interpreter();
        boolean hasParam = true, validParam = true;
        Pattern extPattern = Pattern.compile(".+\\.lt");
        ArrayList<String> input = new ArrayList<String>();

        if (args.length > 0) {
            for (ind = 0; ind < args.length; ind++) {
                extMatcher = extPattern.matcher(args[ind]);
                if (extMatcher.matches()) {
                    validParam = true;
                    break;
                }
                else validParam = false;
            }
        }
        else {
            System.out.println("No input file detected.");
            hasParam = false;
        }

        if (hasParam && validParam) {
            f = new File(args[ind]);
            sc = new Scanner(f);

            while (sc.hasNext()) {
                // takes off all duplicated and trailing spaces
                tmpInput = sc.nextLine().trim();
                input.add(tmpInput);
            }
            sc.close();

            max = input.size();
            for (int i = 0; i < max; i++) {
                tmpInput = input.get(i);
                if (tmpInput.isEmpty() || tmpInput.startsWith("--")) {
                    continue; // ignoring commented and blank lines
                }

                try {
                    lotus.execute(tmpInput);
                } catch (LotusException e) {
                    System.out.println("\n# " + e.getMessage() + "\n@ line " + (i + 1));
                    System.exit(1);
                }
            }
        }
        else if (!validParam) {
            System.out.println("Invalid input file.");
        }

        System.out.println("\n-----------------\n");

        AltExpression a = new AltExpression("(----37.5+41* 2./ (- (gabriel -5)%2) ^2^    3) < 2 || (\"julia\" >= \"Gabriel\")");
        System.out.println("\n------->> a: " + a);

        // AltExpression aa = new AltExpression("a + a +b*c/d % 1 ^ 2");

        Expression b = new Expression("(-37+41* 2/ (- (1 -5)%2) ^2^    3) < 2 || (x >= 25)");
        System.out.println("\n------->> b: " + b);

        /*System.out.println(e);

        e = new Expression("(-oi+41* x/ (- (fn(10) - 4)%2) ^x^    3) + Math.PI + (+x * 25)");
        System.out.println(e);

        e = new Expression("a + b * c || x - y * z");
        System.out.println(e.toPostfix());

        e = new Expression("- 3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3");
        System.out.println(e.toPostfix());

        // this works! \o/
        e = new Expression("(x < y || (square(2) >= 4)) && (25 == 5 * 5)"); // true!
        System.out.println(e.toPostfix());*/
    }
}
