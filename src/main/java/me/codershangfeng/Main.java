package me.codershangfeng;

import javax.swing.*;

public class Main {

    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
    }

    public static void main(String[] args) {
        // 启动
        SwingUtilities.invokeLater(new ThreadScan());
    }
}
