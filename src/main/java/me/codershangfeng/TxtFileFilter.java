package me.codershangfeng;

import java.io.File;

public class TxtFileFilter extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
         if (f.isDirectory()) {
             return true;
         }

        return f.getName().matches(".+\\.txt");
    }

    @Override
    public String getDescription() {
        return "*.txt";
    }
}
