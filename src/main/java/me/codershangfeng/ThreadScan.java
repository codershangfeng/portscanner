package me.codershangfeng;

import me.codershangfeng.view.AboutDialog;
import me.codershangfeng.view.MainFrame;
import me.codershangfeng.view.MenuBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThreadScan implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(ThreadScan.class);

    // 主窗口
    static JFrame main = MainFrame.shared;

    // 菜单栏
    private static JMenuBar mainBar = MenuBar.mainBar;
    private static JMenu fileMenu = MenuBar.fileMenu;
    private static JMenuItem scanItem = MenuBar.scanItem;
    private static JMenuItem saveItem = MenuBar.saveItem;
    private static JMenuItem exitItem = MenuBar.exitItem;
    private static JMenuItem aboutItem = MenuBar.aboutItem;
    private static Map<JMenu, ArrayList<JMenuItem>> menus = MenuBar.menus;

    // 文本标签
    private static JLabel[] labels = new JLabel[4];

    static {
        labels[0] = new JLabel("请选择:");
        labels[1] = new JLabel("端口范围:");
        labels[2] = new JLabel("线程数:");
        labels[3] = new JLabel("扫描结果:");
    }

    // IP扫描和域名扫描的选择按钮
    private static JRadioButton addrScanJRadio = new JRadioButton("IP地址:");
    private static JRadioButton domeScanJRadio = new JRadioButton("域名:");
    private static ButtonGroup btnGroup = new ButtonGroup();

    static {
        // 设为默认
        addrScanJRadio.setSelected(true);
        // 加入按键组
        btnGroup.add(addrScanJRadio);
        btnGroup.add(domeScanJRadio);
    }

    // 输入IP地址的文本框
    private static JTextField[] ipAddrTextField = new JTextField[5];
    private static JLabel[] symbolLabel = new JLabel[5];

    static {
        for (int i = 0; i < 5; i++) {
            ipAddrTextField[i] = new JTextField("0", 3);
            if (i < 3)
                symbolLabel[i] = new JLabel(".");
            else
                symbolLabel[i] = new JLabel("~");
        }

    }

    // 输入主机名的文本框
    private static JTextField domainNameTextField = new JTextField("localhost", 8);

    static {
        domainNameTextField.setEnabled(false);
    }

    // 输入端口范围的文本框, [0]为起始端口, [1]为结束端口.
    private static JTextField[] portTextField = new JTextField[2];

    static {
        portTextField[0] = new JTextField("0", 4);
        portTextField[1] = new JTextField("1000", 4);
    }

    // 输入并发线程数量的文本框
    private static JTextField numOfThreadsTextField = new JTextField("100", 3);


    // 按钮
    static JButton submitBtn = new JButton("开始扫描");
    private static JButton saveBtn = new JButton("保存扫描结果");
    private static JButton exitBtn = new JButton("退出");

    // 显示扫描结果
    static JTextArea resultTextArea = new JTextArea(null, 4, 40);
    // 滚动条面板
    private static JScrollPane resultJPanel =
            new JScrollPane(resultTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    /**
     *
     */
    ThreadScan() {
        logger.debug("{}: 主线程类创建", this.getClass().getSimpleName());

        CreateAndShowGUIThread createAndShowGUI = new CreateAndShowGUIThread(this);
        createAndShowGUI.start();

        ActionListenerThread addActionListener = new ActionListenerThread(this);
        addActionListener.start();
    }

    @Override
    public void run() {
        logger.debug("{}: 主线程开始运行", this.getClass().getSimpleName());
        showMainFrame();
    }

    private void showMainFrame() {
        main.setSize(1000, 800);
        main.setLocation(300, 100);
        main.setResizable(false);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        main.setVisible(true);
    }

    /**
     * 创建和显示GUI组件的线程类
     */
    private class CreateAndShowGUIThread extends Thread {
        private Container mainJPanel;
        private GridBagConstraints ctrs;

        CreateAndShowGUIThread(Runnable target) {
            super(target);
        }

        @Override
        public void run() {
            logger.debug("{}: 添加菜单栏和窗体内容", this.getClass().getSimpleName());

            // 添加菜单栏
            for (Map.Entry<JMenu, ArrayList<JMenuItem>> menu : menus.entrySet()) {
                for (JMenuItem item : menu.getValue())
                    menu.getKey().add(item);
                mainBar.add(menu.getKey());
            }
            main.setJMenuBar(mainBar);

            // 添加窗体内容
            addWindowBody();

        }

        private void addWindowBody() {
            main.setLayout(new GridBagLayout());

            // 初始化面板和布局
            mainJPanel = main.getContentPane();
            ctrs = new GridBagConstraints();
            ctrs.insets = new Insets(10, 0, 0, 10);
            ctrs.fill = GridBagConstraints.BOTH;
            ctrs.anchor = GridBagConstraints.CENTER;

            // 第1行: "请选择"提示
            gridBagAddItem(labels[0], 0, 0, 10);
            // 第2行: IP地址
            gridBagAddItem(addrScanJRadio, 0, 1, 1);
            for (int i = 0; i < 5; i++) {
                gridBagAddItem(ipAddrTextField[i], 2 * i + 1, 1, 1);
                if (i < 4)
                    gridBagAddItem(symbolLabel[i], 2 * i + 2, 1, 1);
            }
            // 第3行: 域名
            gridBagAddItem(domeScanJRadio, 0, 2, 1);
            gridBagAddItem(domainNameTextField, 1, 2, 3);
            // 第4行: 端口范围
            gridBagAddItem(labels[1], 0, 3, 1);
            gridBagAddItem(portTextField[0], 1, 3, 1);
            gridBagAddItem(symbolLabel[4], 2, 3, 1);
            gridBagAddItem(portTextField[1], 3, 3, 1);
            // 第5行: 线程数
            gridBagAddItem(labels[2], 0, 4, 1);
            gridBagAddItem(numOfThreadsTextField, 1, 4, 3);
            // 第6行: 按钮
            gridBagAddItem(submitBtn, 0, 5, 3);
            gridBagAddItem(saveBtn, 3, 5, 3);
            gridBagAddItem(exitBtn, 6, 5, 3);
            // 第7行: 扫描结果
            gridBagAddItem(labels[3], 0, 6, 10);
            resultTextArea.setLineWrap(true);
            resultTextArea.setEditable(false);
            ctrs.gridheight = 4;
            gridBagAddItem(resultJPanel, 0, 7, 10);
        }

        private void gridBagAddItem(JComponent component, int gridx, int gridy, int gridwidth) {
            ctrs.gridx = gridx;
            ctrs.gridy = gridy;
            ctrs.gridwidth = gridwidth;
            mainJPanel.add(component, ctrs);
        }
    }

    /**
     * 添加动作监听器的线程类
     */
    private class ActionListenerThread extends Thread {
        ActionListenerThread(Runnable target) {
            super(target);
        }

        @Override
        public void run() {
            logger.debug("{}: 添加动作监听器", this.getClass().getSimpleName());

            // 设置热键及快键键
            setMnemonicAndAccelerator();

            // 设置动作监听
            setActionListener();
        }

        /**
         * 设置热键及快捷键
         */
        private void setMnemonicAndAccelerator() {
            // 设置热键
            fileMenu.setMnemonic(KeyEvent.VK_F);

            scanItem.setMnemonic(KeyEvent.VK_R);
            scanItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK));
            saveItem.setMnemonic(KeyEvent.VK_S);
            saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));  // 快捷键
            exitItem.setMnemonic(KeyEvent.VK_Q);
            exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        }

        /**
         * 设置动作监听
         */
        private void setActionListener() {
            // 扫描键(选项)的动作监听
            ScanAction scanAction = new ScanAction();
            scanItem.addActionListener(scanAction);
            submitBtn.addActionListener(scanAction);

            // 保存键(选项)的动作监听
            SaveAction saveAction = new SaveAction();
            saveItem.addActionListener(saveAction);
            saveBtn.addActionListener(saveAction);

            // 退出键(选项)的动作监听
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logger.debug("{}: \"退出\"按钮动作监听器", this.getClass().getSimpleName());
                    System.exit(0);
                }
            });
            exitBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logger.debug("{}: \"退出\"按钮动作监听器", this.getClass().getSimpleName());
                    System.exit(0);
                }
            });

            // 扫描方式Radio键的动作监听
            addrScanJRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (addrScanJRadio.isSelected()) {
                        for (int i = 0; i < ipAddrTextField.length; i++)
                            ipAddrTextField[i].setEnabled(true);

                        domainNameTextField.setEnabled(false);
                    }
                }
            });
            domeScanJRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (domeScanJRadio.isSelected()) {
                        domainNameTextField.setEnabled(true);

                        for (int i = 0; i < ipAddrTextField.length; i++)
                            ipAddrTextField[i].setEnabled(false);
                    }
                }
            });

            // 帮助选项的动作监听
            aboutItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new AboutDialog();
                }
            });
        }
    }

    private class ScanAction implements ActionListener {
        private int[] secIP = new int[5];   // IP地址段
        private String hostName = "";       // 域名
        private int[] port = new int[2];    // 端口
        private int numOfThreads;           // 线程数

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.debug("{}: \"开始扫描\"按钮动作监听器", this.getClass().getSimpleName());

            // 使用IP地址进行扫描
            if (addrScanJRadio.isSelected()) {
                // 验证IP地址的有效性和正确性
                for (int i = 0; i < ipAddrTextField.length; i++) {
                    try {
                        secIP[i] = Integer.parseInt(ipAddrTextField[i].getText());
                        checkAddressRange(secIP[i]);
                    } catch (NumberFormatException | InputMismatchException e1) {
                        logger.debug("错误:IP地址应为0-255的整数--第{}个输入框内容为{}", i + 1, ipAddrTextField[i].getText());

                        String msg = "错误:IP地址应为0-255的整数--"
                                + "第".intern() + (i + 1)
                                + "个输入框内容为:" + ipAddrTextField[i].getText();

                        JOptionPane.showMessageDialog(main, msg, "IP地址错误", JOptionPane.ERROR_MESSAGE);

                        return;
                    }
                }
                // 一种特殊情况的处理: 末段IP地址的结束地址大于起始地址
                if (secIP[4] < secIP[3]) {
                    logger.debug("错误:末段IP地址的结束地址应大于等于起始地址--起始地址为{},终止地址为{}", secIP[3], secIP[4]);

                    String msg = "错误:末段IP地址的结束地址应大于等于起始地址--起始地址为"
                            + secIP[3] + ",终止地址为" + secIP[4];

                    JOptionPane.showMessageDialog(main, msg, "IP地址错误", JOptionPane.ERROR_MESSAGE);

                    return;
                }
            } else if (domeScanJRadio.isSelected()) {
                try {
                    InetAddress host = InetAddress.getByName(domainNameTextField.getText());
                    hostName = host.getHostName();
                } catch (UnknownHostException e1) {
                    logger.debug("错误的域名,或主机不可达");

                    String msg = "错误: 域名错误,或主机不可达";

                    JOptionPane.showMessageDialog(main, msg, "域名错误或主机不可达", JOptionPane.ERROR_MESSAGE);

                    return;
                }

            } else {
                throw new RuntimeException("扫描方式出现运行时错误");
            }

            // 验证端口的有效性和正确性
            for (int i = 0; i < port.length; i++) {
                try {
                    port[i] = Integer.parseInt(portTextField[i].getText());
                    checkPortRange(port[i]);
                } catch (NumberFormatException | InputMismatchException e1) {
                    logger.debug("错误:端口地址应为0-65535的整数--第{}个输入框内容为{}", i + 1, portTextField[i].getText());

                    return;
                }
            }

            // 验证线程数的有效性和正确性
            try {
                numOfThreads = Integer.parseInt(numOfThreadsTextField.getText());
                checkThreadsNumRange(numOfThreads);
            } catch (NumberFormatException | InputMismatchException e1) {
                logger.debug("错误:线程数应为0-200的整数--输入框内容为{}", numOfThreadsTextField.getText());
                return;
            }

            /*
             * 验证无误后,给TCPThread类参数赋值
             * 1. String scanType
             * 2. int[] secIP / String hostName
             * 3. int[] port
             * 4. int numOfThreads
             */
            if (addrScanJRadio.isSelected()) {

                TCPThread.scanType = TCPThread.ADDRESS_SCAN;
                TCPThread.secIP = secIP;

                logger.debug("按IP地址进行扫描");
                logger.debug("目标IP地址为{}", Arrays.toString(TCPThread.secIP));
            } else {

                TCPThread.scanType = TCPThread.DOMAINNAME_SCAN;
                TCPThread.hostName = hostName;

                logger.debug("按域名进行扫描");
                logger.debug("目标主机域名为{}", hostName);
            }

            TCPThread.port = port;
            TCPThread.numOfThreads = numOfThreads;

            logger.debug("目标主机端口范围为{}", Arrays.toString(port));
            logger.debug("最大启动线程数为{}", numOfThreads);

            // 启动扫描
            if (submitBtn.isEnabled()) {
                submitBtn.setEnabled(!submitBtn.isEnabled());
                if (resultTextArea.getText() != null)
                    resultTextArea.setText(null);
            }
            for (int i = 0; i < numOfThreads; i++)
                new TCPThread("T" + i, i).start();

        }

        // 检查IP地址的范围
        private void checkAddressRange(int ip) throws InputMismatchException {
            if (ip < 0 || ip > 255)
                throw new InputMismatchException();
        }

        // 检查端口的范围
        private void checkPortRange(int port) throws InputMismatchException {
            if (port < 0 || port > 65535)
                throw new InputMismatchException();
        }

        // 检查线程数的范围
        private void checkThreadsNumRange(int numOfThreads) throws InputMismatchException {
            if (numOfThreads < 0 || numOfThreads > 200)
                throw new InputMismatchException();
        }
    }

    private class SaveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (resultTextArea.getText().length() == 0) {
                logger.debug("result为空,不可保存");
            } else {
                logger.debug("result可以保存, 请选择");
                logger.debug("内容为{}", resultTextArea.getText());

                JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new TxtFilter());
                fc.setAcceptAllFileFilterUsed(false);

                String filename = new SimpleDateFormat("hh时mm分ss秒-yyyy年MM月dd日").format(new Date()) + ".txt";
                fc.setSelectedFile(new File("./" + filename));

                int returenVal = fc.showSaveDialog(null);

                if (returenVal == JFileChooser.APPROVE_OPTION) {
                    logger.debug("{}", fc.getSelectedFile());

                    Path saveFile = fc.getSelectedFile().toPath();
                    try (BufferedWriter out = Files.newBufferedWriter(saveFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
                        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                        out.append("******** " + date + " ********");
                        out.newLine();

                        if (addrScanJRadio.isSelected()) {
                            out.append("目标主机(IP地址扫描): ");
                            for (int i = 0; i < ipAddrTextField.length; i++) {
                                out.append(ipAddrTextField[i].getText());
                                if (i < 3)
                                    out.append(".");
                                else if (i == 3)
                                    out.append("-");
                                else
                                    out.newLine();
                            }
                        } else {
                            out.append("目标主机(域名扫描): ").append(domainNameTextField.getText());
                            out.newLine();
                        }

                        String[] log = resultTextArea.getText().split("\\n");
                        for (int i = 0; i < log.length; i++) {
                            out.append(log[i]);
                            out.newLine();
                        }

                        out.flush();
                    } catch (FileAlreadyExistsException e1) {
                        JOptionPane.showMessageDialog(fc, "文件名已存在", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    } catch (IOException e1) {
                        logger.error("文件保存出现未知异常");
                        e1.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(fc, "保存完成", "信息", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }
    }
}
