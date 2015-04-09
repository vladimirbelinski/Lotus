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
                input.add(sc.nextLine());
            }
            sc.close();

            max = input.size();
            for (int i = 0; i < max; i++) {
              lotus.interpret(input.get(i));
              System.out.println(lotus.getVar("x"));
              System.out.println(lotus.getVar("y"));
              System.out.println(lotus.getVar("z"));
              System.out.println(lotus.getVar("k"));
              System.out.println(lotus.getVar("s"));

              if (lotus.getVar("s") != null) lotus.setVar("s", "teste");

              lotus.setVar("x", 3);
              System.out.println("new x: " + lotus.getVar("x"));

              lotus.setVar("gabriel", 9);

              lotus.setVar("x", lotus.getVar("y").toInt() + lotus.getVar("z").toInt());
              System.out.println("new x: " + lotus.getVar("x"));

              System.out.println();

              /*System.out.println(input.get(i));
              System.out.println("= " + lotus.solve(input.get(i)));
              if (i < max - 1) System.out.println();*/
            }
        }
        else if (!validParam) {
            System.out.println("Invalid input file.");
        }
        /**********************************************************************/
        StringVar g = new StringVar("Gabriel");
        DoubleVar d = new DoubleVar(7.0);
        IntVar tni = new IntVar(11);

        System.out.println("---------------------------------------");
        IntVar a, b, c;
        a = new IntVar(7);
        b = new IntVar(11);
        c = new IntVar(a.value + b.value);
        System.out.println(a + ", " + b + ", " + c);
        c.value = (a.value + b.value) * 2;
        System.out.println(a + ", " + b + ", " + c);
        c.value = d.toInt() + 5;
        System.out.println(a + ", " + b + ", " + c);
        System.out.println("---------------------------------------");
    }
}
