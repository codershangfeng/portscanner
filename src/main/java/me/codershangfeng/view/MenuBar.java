package me.codershangfeng.view;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MenuBar {
    public static final JMenuBar mainBar = new JMenuBar();
    public static final JMenu fileMenu = new JMenu("文件(F)");
    public static final JMenuItem scanItem = new JMenuItem("开始扫描(R)");
    public static final JMenuItem saveItem = new JMenuItem("保存扫描结果(S)");
    public static final JMenuItem exitItem = new JMenuItem("退出(Q)");
    public static final JMenu helpMenu = new JMenu("帮助");
    public static final JMenuItem aboutItem = new JMenuItem("关于");
    public static final Map<JMenu, ArrayList<JMenuItem>> menus = new LinkedHashMap<>();

    static {
        ArrayList<JMenuItem> fileMenuItems = new ArrayList<>();
        fileMenuItems.add(scanItem);
        fileMenuItems.add(saveItem);
        fileMenuItems.add(exitItem);
        menus.put(fileMenu, fileMenuItems);

        ArrayList<JMenuItem> helpMenuItems = new ArrayList<>();
        helpMenuItems.add(aboutItem);
        menus.put(helpMenu, helpMenuItems);
    }

}
