package me.codershangfeng;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dfgh on 2017/4/11.
 */
public class AboutDialog extends JDialog {
    private JPanel panel = new JPanel();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JPanel[] p = new JPanel[2];
    private JTextArea[] textAreas = new JTextArea[2];

    public AboutDialog() {
        setTitle("网络主机端口扫描器");
        setSize(300, 200);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container c = this.getContentPane();


        for (int i = 0; i < textAreas.length; i++) {
            textAreas[i] = new JTextArea(6, 6);
            textAreas[i].setSize(260, 200);
            textAreas[i].setEditable(false);
            textAreas[i].setLineWrap(true);
            textAreas[i].setFont(new Font("楷体_GB2312", Font.BOLD, 13));

            p[i] = new JPanel();
            p[i].add(textAreas[i]);
        }

        String principlesOfScan = "基于java.net.Socket类:\n " +
                "第一步: 用\"IP地址(域名) + 端口\"组成1个套接字(Socket);\n" +
                "第二步: 创建与目标主机的TCP连接;\n" +
                "第三步: 连接成功, 即判定端口打开; 否则, 判定端口关闭;\n" +
                "第四步: 将扫描结果返回到GUI界面.";
        textAreas[0].setText(principlesOfScan);
        textAreas[0].setForeground(Color.BLUE);

        String usageOfScan = "1. 选择扫描方式;\n" +
                "2. 输入目标主机的IP地址或域名;\n" +
                "3. 点击\"开始扫描\"或快捷键\"CTRL + ENTER\"开始扫描;\n" +
                "3. 等待若干时间后, 扫描结果将显示在最下方文本框中;" +
                "若需要保存扫描结果, 点击\"保存扫描结果\"或快捷键\"CTRL + S\"进行保存;\n" +
                "4. 点击\"退出\"或快捷键\"CTRL + W\"退出程序.";
        textAreas[1].setText(usageOfScan);
        textAreas[1].setForeground(Color.BLACK);

        tabbedPane.setSize(300, 200);
        tabbedPane.addTab("扫描原理", p[0]);
        tabbedPane.addTab("使用说明", p[1]);

        panel.add(tabbedPane);
        c.add(panel);

        pack();
        this.setVisible(true);
    }
}
