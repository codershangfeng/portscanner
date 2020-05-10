# portscanner
网络主机的端口扫描器--基于Java Swing技术

主要功能：根据IP地址或域名，对目标网络主机的开放端口的进行扫描（多线程），并将结果进行分别保存。

原理：利用TCP协议的建立连接机制，通过创建java.net.Socket实例对象，确定目标主机端口是否打开，进而根据端口号判定其服务类型（端口对应的服务类型在程序内由TreeMap管理）。

结构：

- **Main.java** 
    - 程序启动主入口

- **ThreadScan.java** 
    - GUI主界面，创建给定的最大线程数的TCPThread.java实例对象，多线程分组扫描目标主机的所有端口。

- **TCPThread.java** 
    - 继承自java.util.Thread的线程类。
    - 当以“域名”方式进行扫描时，每个实例对象将负责扫描（端口数 / 最大线程数）个端口；
    - 当以“IP地址”方式进行扫描时，每个实例对象将负责扫描（IP地址数 * 端口数 / 最大线程数）个端口。
             
- **AboutDialog.java** 
    - 继承自javax.swing.JDialog，帮助对话框，显示简单的帮助信息。

- **TxtFileFilter.java** 
    - 继承自javax.swing.filechooser.FileFilter类，配合javax.swing.JFileChooser显示后缀“.txt”文件。

主要知识点：

（某本Java书籍上的例子，具体哪本记不清了）
- Thread
- Socket
- Swing(除了授课外，估计很少有项目用了！)

