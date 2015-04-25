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

/* To execute a test, from inside the folder that contains this file, run:
 * javac *.java
 * java Lotus ../exemplos/<filename>.lt
 * */

class Lotus {
    public static void main(String[] args) throws Exception {
        File f;
        Scanner sc;
        Matcher extM;
        String tmpInput, tmpEnding;
        int ind = 0, max, semicolon, bracket;
        Interpreter lotus = new Interpreter();
        boolean hasParam = true, validParam = true;
        Pattern extP = Pattern.compile(".+\\.lt");
        ArrayList<String> input = new ArrayList<String>();

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
            sc = new Scanner(f);

            while (sc.hasNext()) {
                tmpInput = sc.nextLine().trim();

                // discarding comments and empty
                if (tmpInput.startsWith("--")) {
                    tmpInput = "";
                }
                else if (!tmpInput.isEmpty()) {
                    // arrumar isso aqui, porque não é o índice da última ocorrência!
                    semicolon = tmpInput.lastIndexOf(";");
    				bracket = tmpInput.lastIndexOf("{");

                    if (semicolon > bracket) {
                        tmpInput = tmpInput.substring(0, semicolon + 1);
    				}
    				else if (bracket > 0) {
                        tmpInput = tmpInput.substring(0, bracket + 1);
    				}
                }

                // System.out.println("tmpInput: " + tmpInput);

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
