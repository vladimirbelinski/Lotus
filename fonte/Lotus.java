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
    public static void main(String[] args) throws Exception {
        File f;
        Scanner sc;
        int max, ind = 0;
        String tmpInput, lineEnding;
        Interpreter lotus = new Interpreter();
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
                // takes off all duplicated and trailing spaces
                tmpInput = sc.nextLine().replaceAll("( )+", " ").trim();
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
                    System.out.println("\n" + e.getCode() + " at line " + (i + 1));
                    System.out.println("> " + e.getMessage());
                    System.exit(1);
                }
            }
        }
        else if (!validParam) {
            System.out.println("Invalid input file.");
        }
    }
}
