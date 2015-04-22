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
            System.out.println("* No input file detected.\n");
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

            try {
                lotus.execute(input);
            } catch (LotusException e) {
                System.out.println("\n# " + e.getMessage() + "\n@ line " + e.getLine());
                System.exit(1);
            }
        }
        else if (!validParam) {
            System.out.println("# Invalid input file.");
        }
    }
}
