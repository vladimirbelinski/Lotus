/*******************************************************************************
Name: Main.java
Authors: Acácia dos Campos da Terra- terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com
Version: 1.0
Copyright: Your copyleft.
Description: Main class of Lotus, a programming language based on Java.
*******************************************************************************/

import java.util.*;
import java.io.*;

/* To execute the test, run:
 * javac *.java
 * java Lotus ../exemplos/testing.lt
 */

class Lotus {
    public static Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws Exception {
        File f;
        Scanner sc;
        int max, ind = 0;
        String tmpInput, lineEnding;
        boolean hasParam = true, validParam = true;
        ArrayList<String> input = new ArrayList<String>();

        if (args.length > 0) {
            for (ind = 0; ind < args.length; ind++) {
                if (args[ind].matches(".+\\.lt")) {
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
                tmpInput = sc.nextLine().replaceAll("( )+", " ").trim();

                lineEnding = tmpInput.substring(tmpInput.lastIndexOf(';') + 1);
                // comments after the last ';'
                if (lineEnding.length() >= 2) {
                    if (lineEnding.startsWith("--") ||
                        (lineEnding.charAt(0) == ' ' && lineEnding.startsWith("--", 1)))
                    {
                        tmpInput = tmpInput.replace(lineEnding, "");
                    }
                }

                input.add(tmpInput);
            }
            sc.close();

            max = input.size();
            for (int i = 0; i < max; i++) {
                tmpInput = input.get(i);
                if (tmpInput.isEmpty() || (tmpInput.length() >= 2 && tmpInput.substring(0, 2).equals("--"))) {
                    continue; // ignoring comments and blank lines
                }

                try {
                    interpreter.execute(tmpInput);
                } catch (LotusException e) {
                    System.out.println("\nError at line: " + (i + 1));
                    System.out.println("> " + e);
                    System.exit(1);
                }
            }
        }
        else if (!validParam) {
            System.out.println("Invalid input file.");
        }

        /**********************************************************************/

        // System.out.println("---------------------------------------");
        // StringVar g = new StringVar("Gabriel");
        // DoubleVar d = new DoubleVar(7.0);
        // IntVar tni = new IntVar(11);
        // Variable test = new IntVar(0);
        //
        // System.out.println("---------------------------------------");
        // interpreter.newVar("g", g);
        // System.out.println(interpreter.getVar("g"));
        // interpreter.setVar("g", "Acácia");
        // System.out.println(interpreter.getVar("g"));
        // System.out.println("---------------------------------------");
    }
}
