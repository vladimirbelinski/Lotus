import java.io.*;
import java.util.*;
import java.util.regex.*;

class SourceScanner {
    public ArrayList<String> scan(File f) throws Exception {
        int comm;
        String line;
        Scanner sc = new Scanner(f);
        ArrayList<String> input = new ArrayList<String>();

        while (sc.hasNext()) {
            line = sc.nextLine().trim();

            if (line.startsWith("--")) {
                line = "";
            }
            else {
                comm = line.indexOf("--");
                if (comm >= 0) {
                    line = line.substring(0, comm).trim();
                }
            }

            input.add(line);
        }
        sc.close();

        return input;
    }

    public void print(ArrayList<String> code) {
        int i, max = code.size();
        for (i = 0; i < max; i++) {
            System.out.println(code.get(i));
        }
    }
}
