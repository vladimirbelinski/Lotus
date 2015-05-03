import java.io.*;
import java.util.*;
import java.util.regex.*;

class SourceScanner {
    public ArrayList<Line> scan(File f) throws Exception {
        int i, comm;
        String line;
        Scanner sc = new Scanner(f);
        ArrayList<Line> input = new ArrayList<Line>();

        for (i = 1; sc.hasNext(); i++) {
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

            input.add(new Line(line, i));
        }
        sc.close();

        return input;
    }

    public void print(ArrayList<Line> code) {
        int i, max = code.size();
        for (i = 0; i < max; i++) {
            System.out.println(code.get(i));
        }
    }
}
