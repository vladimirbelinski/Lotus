/*******************************************************************************
Name: Lotus.java
Authors: Acácia dos Campos da Terra - terra.acacia@gmail.com
         Gabriel Batista Galli - g7.galli96@gmail.com
         Vladimir Belinski - vlbelinski@gmail.com

Description: Main class of Lotus, a programming language based on Java.
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.util.regex.*;

/* To execute a test, from inside the folder that contains this file, run:
 * javac *.java
 * java Lotus ../exemplos/<filename>.lt
 * */

class Lotus {
    public static void main(String[] args) throws Exception {
        File f;
        int ind = 0;
        Matcher extM;
        Interpreter lotus = new Interpreter();
        Pattern extP = Pattern.compile(".+\\.lt");
        boolean hasParam = true, validParam = true;
        ArrayList<Line> input = new ArrayList<Line>();
        SourceScanner ltSc = new SourceScanner();

        if (args.length > 0) {
            for (ind = 0; ind < args.length; ind++) {
                extM = extP.matcher(args[ind]);
                if (extM.matches()) {
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
            input = ltSc.scan(f);
            // System.out.println("------------------------------------------");
            // ltSc.print(input);
            // System.out.println("------------------------------------------");

            try {
                lotus.execute(input, false);
            } catch (LotusException e) {
                System.out.println("\n> " + e.getMessage() + " @ line #" + e.getNumber() + ":");
                System.out.println(e.getLine());
                System.exit(1);
            }
        }
        else if (!validParam) {
            System.out.println("# Invalid input file.");
        }
    }
}
