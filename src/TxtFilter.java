import java.io.File;

/**
 * Created by dfgh on 2017/4/10.
 */
public class TxtFilter extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
         if (f.isDirectory())
             return true;

        if (f.getName().matches(".+\\.txt"))
            return true;

        return false;
    }

    @Override
    public String getDescription() {
        return "*.txt";
    }

//    public static void main(String[] args) {
//        String file = "2013-3-2.txt";
////        String file = "abc.txt";
//        System.out.println(file.matches(".\\.txt"));
//
//    }

}
