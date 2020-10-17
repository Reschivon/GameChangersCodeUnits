package pathgen;

import java.io.*;
import java.util.StringTokenizer;

public class ImportPath {
    public static Path getPath (String path){
        Path ret = new Path();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer t = new StringTokenizer(line, " ");
                double x = Double.parseDouble(t.nextToken());
                double y = Double.parseDouble(t.nextToken());
                double speed = Double.parseDouble(t.nextToken());
                double dir = Double.parseDouble(t.nextToken());

                PathPoint toAdd = new PathPoint(x, y);
                toAdd.speed = speed;
                toAdd.dir = dir;

                ret.add(toAdd);
            }
        }catch (IOException e) {
            System.out.printf("File %s not found%n", path);
        }

        ret.start = ret.get(0);
        ret.end = ret.get(ret.size()-1);
        return ret;
    }
}
