import java.util.*;
import java.io.*;

/* To execute the test, run:
 * java Lotus ../exemplos/testing.lt
 */

class Lotus {
    public static void main(String[] args) throws Exception {
        int max;
        File f = new File(args[0]);
        Scanner s = new Scanner(f);
        ArrayList<String> in = new ArrayList<String>();

        while (s.hasNext()) {
            in.add(s.nextLine());
        }
        s.close();

        max = in.size();
        for (int i = 0; i < max; i++) {
            System.out.println(in.get(i));
            System.out.println("= " + Interpreter.solve(in.get(i)));
            if (i < max - 1) System.out.println();
        }
    }
}
