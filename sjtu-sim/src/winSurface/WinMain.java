package winSurface;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.event.*;

import ptolemy.plot.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*; // java数据结构的包，用到其中的 arrayList
//test update -zh 8-11
public class WinMain {

    /****************************************************************
     *******************   private members *************************/

    private static final long serialVersionID = 1L;
    static final int WIN_WIDTH = 900; // 窗口的长
    static final int WIN_HEIGHT = 450; // 窗口的宽
    static JFrame mainFrame = null; // 程序主窗口变量

    static JTabbedPane mainTabbedPane = null; // JRootPane中内容面板要加载的选项卡面板
    JMenuBar menuBar = null;
    JMenu menuFile = null;
    JMenu menuEdit = null;
    JMenu menuView = null;
    JMenu menuTool = null;
    JMenu menuHelp = null;

    static ArrayList _aryListVariableName = null; // 用来记录一次仿真过程，涉及到的全部变量名称
    static ArrayList _aryListTabbedPanel = null; // 用来存放选项卡的动态数组
    static ArrayList _aryListPlotPanel = null; // 用来存放每个选项卡中的Plot画图面板
    static boolean _bSimulationBegin = false; // 用来指示当前时间，界面是否有仿真在运行


    public WinMain()
    {
        // 类成员变量的初始化
        _aryListVariableName = new ArrayList();
        _aryListTabbedPanel = new ArrayList();
        _aryListPlotPanel = new ArrayList();
        _bSimulationBegin = false;


        // 下面开始创建出界面窗口，并在窗口中添加好各个菜单项
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){}
        mainFrame = new JFrame("SimJ&M"); // 创建程序窗口，窗口标题为 SimJ&M
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true); // 默认为false
        mainFrame.setSize(WIN_WIDTH, WIN_HEIGHT); // 设置窗口的尺寸
        // 获取屏幕大小，并设置窗口初始的显示位置
        Toolkit kit= Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int x = (screenSize.width - WIN_WIDTH)/2;
        int y = (screenSize.height - WIN_HEIGHT)/2;
        mainFrame.setLocation(x, y); // 设置窗口出现时，在屏幕的中央

        // 创建菜单项，并添加菜单栏
        menuBar = new JMenuBar(); // 创建出菜单栏
        mainFrame.setJMenuBar(menuBar); // 将菜单条添加到窗口中

        menuFile = new JMenu("文件(F)");
        menuFile.setMnemonic('F'); // 设置这个菜单的快捷键为 'F'
        menuEdit = new JMenu("编辑(E)");
        menuEdit.setMnemonic('E');
        menuView = new JMenu("视图(V)");
        menuView.setMnemonic('V');
        menuTool = new JMenu("工具(T)");
        menuTool.setMnemonic('T');
        menuHelp = new JMenu("帮助(H)");
        menuHelp.setMnemonic('H');

        // 将创建好的菜单添加到菜单条中
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuView);
        menuBar.add(menuTool);
        menuBar.add(menuHelp);

        // 创建菜单项，并设置好快捷键
        JMenuItem menuItemOpenInFile = new JMenuItem("打开(O)");
        // 为“打开”这个菜单项 设定 快捷键 --- Ctrl + O
        menuItemOpenInFile.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemOpenInFile); // 将这个菜单项添加到“文件”菜单下
        menuFile.addSeparator();

        JMenuItem menuItemOpenMATALBInFile = new JMenuItem("启动MATALB");
        menuFile.add(menuItemOpenMATALBInFile);

        JMenuItem menuItemOpenPTInFile = new JMenuItem("启动 Vergil");
        menuFile.add(menuItemOpenPTInFile);

        menuFile.addSeparator();
        JMenuItem menuItemExitInFile = new JMenuItem("退出程序(X)");
        menuItemExitInFile.setAccelerator(KeyStroke.getKeyStroke('X', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemExitInFile);        

        JMenuItem menuItemGetHelpInHelp = new JMenuItem("使用说明");
        menuHelp.add(menuItemGetHelpInHelp);
        menuHelp.addSeparator();

        JMenuItem menuItemInfoInHelp = new JMenuItem("关于SimJ&M");
        menuHelp.add(menuItemInfoInHelp);


        // 下面开始创建选项卡面板，并创建一个“开始”选项卡加入到选项卡面板中去
        mainTabbedPane = new JTabbedPane(); // 创建窗口加载的选项卡面板
        mainFrame.setContentPane(mainTabbedPane); // 设置窗口的显示面板为 上面的选项卡面板
        mainTabbedPane.setVisible(true); // 设置这个选项卡面板可见
        // 设置选项卡面板的大小及其选项卡的位置方向
        //mainTabbedPane.setPreferredSize(new Dimension(500,200));
        mainTabbedPane.setTabPlacement(JTabbedPane.TOP);
        // 设置选项卡在容器内的显示形式
        mainTabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        //mainTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // mainFrame.pack(); // 让主窗口适应组件的大小

        // 添加选项卡容器，并且设置每个选项卡的标签以及其是否可用
        JPanel panel0 = new JPanel();
        mainTabbedPane.addTab("开始",panel0); // 将“开始”面板 panel0 作为第一个选项卡
        mainTabbedPane.setEnabledAt(0,true);


        // 经过以上工作，基本的窗口，选项卡面板以及菜单都创建完毕，下面开始针对每个菜单项添加相应的事件处理机制
        menuItemOpenInFile.addActionListener(new ActionListener()
        {
            /* “打开”文件菜单项的动作事件，当单击这个菜单项时，会弹出文件选择对话框，目标是让用户选择 .xml 模型文件
             * 在用户选择好文件之后，获取被选中的文件路径全称，并传递给 Ptolemy II，开启仿真 
             * */
            public void actionPerformed(ActionEvent Event)
            {
                // 创建一个文件选择对话框，供用户选择 .xml 文件
                JFileChooser fileChooser = new JFileChooser("D:\\");
                File file = null;
                int iResult = 0;
                fileChooser.setApproveButtonText("确定");
                fileChooser.setDialogTitle("打开文件");
                iResult = fileChooser.showOpenDialog(mainFrame);
                /* 当用户有选中文件 并且按下"确定"按钮后，就可以通过 getSelectedFile() 方法取得文件对象
                 * */
                if( iResult == JFileChooser.APPROVE_OPTION )
                {
                    // 用户按下的“确定”按钮
                    file = fileChooser.getSelectedFile();
                    System.out.println("选择的文件名：" + file.getName());
                    System.out.println("选择的文件名：" + file.getAbsolutePath());

                    String fileName = file.getName();
                    if( fileName.endsWith("xml") ) // 选中的确实是xml文件
                    {
                        // 将选中的xml文件传给 Vergil，开启仿真
                        //String startVergilcmd = "vergil -run E:\\PT_workspace\\pvbattery_50-60_org.xml";
                        String startVergilcmd = "vergil -run " + file.getAbsolutePath();

                        // 检查是否已经有仿真在进行
                        if( _bSimulationBegin == true )
                        {
                            // 弹出对话框提示用户，是否结束当前仿真，并开启新的仿真
                            int iStartNewSim = JOptionPane.showConfirmDialog(mainFrame, 
                                    "目前系统已经有仿真程序在运行，是否结束当前仿真程序并开启新的仿真", 
                                    "是否结束当前仿真程序", 
                                    JOptionPane.YES_NO_OPTION);
                            if( iStartNewSim == JOptionPane.YES_OPTION )
                            {
                                // 清楚当前仿真的所有选项卡以及相关信息
                                // _clearOldSimulation(); 此处考虑暂不执行这个函数，保留旧的仿真所有信息，因为可能新的仿真无效（比如xml不正确）                      
                            }
                            else
                            {
                                return; // 事件处理函数结束，不会启动新的仿真
                            }                            
                        } //  if( _bSimulationBegin == true )

                        // _startNewSimulationInVergil(startVergilcmd);
                        Runtime run = Runtime.getRuntime(); //启动与应用程序相关的运行时对象
                        // 这里已经试过，必须使用try-catch结构才行
                        try {   
                            Process p = run.exec(startVergilcmd);// 启动另一个进程来执行 指定的系统 命令   
                            BufferedInputStream in = new BufferedInputStream(p.getInputStream());   
                            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));   
                            String lineStr;   
                            while ((lineStr = inBr.readLine()) != null)   
                                //获得命令执行后在控制台的输出信息   
                                // 控制台有输出信息，那说明本机没有安装好Ptolemy II或者没有为Ptolemy II设置好环境变量

                                System.out.println(lineStr);// 打印输出信息   
                            //检查命令是否执行失败。   
                            if (p.waitFor() != 0) {   
                                if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束 
                                    JOptionPane.showMessageDialog(mainFrame, 
                                            lineStr + "!" + "出现这种错误，有两种可能：（1）本机没有安装Ptomely II；\n;"
                                                    + "（2）本机已正确安装了Ptolemy II，但是没有为其设置好系统环境变量", 
                                                    "无法启动Vergil", 
                                                    JOptionPane.ERROR_MESSAGE);
                                System.err.println("命令执行失败!  ");   
                            }   
                            inBr.close();   
                            in.close();   
                        } 
                        catch (Exception e) {   
                            // e.printStackTrace();   
                        }
                        
                    } // if( fileName.endsWith("xml") ) // 选中的确实是xml文件
                    else
                    {
                        // 选中的并不是 xml 文件，弹出对话框提示用户
                        JOptionPane.showMessageDialog(mainFrame, 
                                file.getName() + " 不是 .xml文件，Ptolemy II无法打开此文件，请重新选择！", 
                                "错误的文件类型", 
                                JOptionPane.ERROR_MESSAGE);
                    }

                } // if( iResult == JFileChooser.APPROVE_OPTION )
                else
                {
                    // 用户没有点击确定按钮，do nothing
                }
                // return;
            }
        }); // menuItemOpenInFile.addActionListener(new ActionListener()

    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new WinMain(); // 创建出主窗口

        // 在这个主函数里，测试能否动态向 mainTabbedPane添加新的选项卡

        JPanel panel_1 = new JPanel();       
        mainTabbedPane.addTab("由主函数添加",panel_1); 
        mainTabbedPane.setEnabledAt(1,true);
        // 经测试，上面代码是OK的，也就是可以在主函数中动态添加选项卡面板

        
        // 下面开始测试添加2个以上的Plot面板，可能有很大的问题

        JPanel panelPlot = new JPanel();
        panelPlot.setLayout(new BorderLayout());
        Plot testPlot = new Plot();
        panelPlot.add(testPlot, BorderLayout.CENTER);
        
        //下面这两句要放在addtab前--zh
        
        testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // 有效，以像素为单位，确定画图区域的范围
        testPlot.setButtons(true);// 有效，非常重要
        
        mainTabbedPane.addTab("画图测试面板", panelPlot);
        mainTabbedPane.setEnabledAt(2,true);      

        // mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);
        
        // testPlot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT); // 没有效果
        // testPlot.setPlotRectangle(new Rectangle(0,0,WIN_WIDTH,WIN_HEIGHT) ); // 有效

        testPlot.addPoint(1, 0.4, 0.5, true); // 无效，有待研究
        testPlot.addPoint(1, 0.9, 10, true);
        testPlot.addPoint(0, 1.0, 15, true);
        testPlot.addPoint(0, 1.5, -6.0, true);
        testPlot.fillPlot();


        JPanel panelPlot1 = new JPanel();
        Plot testPlot1 = new Plot();
        panelPlot1.add(testPlot1);
        testPlot1.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // 有效，以像素为单位，确定画图区域的范围
        testPlot1.setButtons(true);// 有效，非常重要
        mainTabbedPane.addTab("画图测试面板1", panelPlot1);
        mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);  
        // testPlot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT); // 没有效果
        // testPlot.setPlotRectangle(new Rectangle(0,0,WIN_WIDTH,WIN_HEIGHT) ); // 有效

        testPlot1.addPoint(1, 0.4, 0.5, true); // 无效，有待研究
        testPlot1.addPoint(1, 0.9, 10, true);
        testPlot1.addPoint(1, 1.0, 15, true);
        testPlot1.fillPlot();       

        // 开启服务器程序，等待客户端的仿真数据
        ServerSocket server;
        Socket socket;
        String str_tem;
        InputStream fIn;
        // OutputStream fOut;
        BufferedReader bfreader;
        // PrintStream ps;

        try
        {
            server = new ServerSocket(6666); // 在端口6666上创建服务器Socket对象

            /****************** 
             * 下面while循环代码经过测试是OK的，可以接受多次单独的socket客户端连接发送的消息
             * **********************************/
            while(true)
            {
                socket = server.accept(); /*至此，调用该方法的服务器进程阻塞，直到收到客户端的请求；
                    当客户端在6666端口有请求时，服务器Socket和客户端的Socket将绑定起来*/
                System.out.println("服务器Ready！");

                //获得对应Socket的输入输出流
                fIn = socket.getInputStream();
                // fOut = socket.getOutputStream();

                //下面建立数据流
                bfreader = new BufferedReader(new InputStreamReader(fIn));
                //ps = new PrintStream(fOut);
                //BufferedReader UserIn = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("等待客户端消息...");
                str_tem = bfreader.readLine(); // 读客户端传送的字符串
                System.out.println("客户端：" + str_tem); // 显示字符串    

                // 下面开始重点，解读客户端发送的数据字符串，从中读取出: 
                // （1）数据集的编次，即表达客户端第几次想服务器发送数据集
                // （2）仿真时间
                // （3）各个变量的数据点
                // 客户端发送的消息格式如下——
                // <编次-仿真时间-(变量名称1,数值1)-(变量名称2,数值2)-(变量名称3,数值3)-...-(变量名称n,数值n)>
                String str_dataset = str_tem.trim();

                _handleSocketMessage(str_dataset);                  

                // 关闭这次socket连接
                bfreader.close();
                //ps.close();
                fIn.close();
                //fOut.close();
                socket.close();
                /* ！！！！！！这里值得注意，ServerSocket——server 并没有被关闭！！！！！！ */

                if( str_tem == "close" ) 
                { 
                    // 象征性地给出一个berak分支语句，可能在软件后期开发中有用处，比如pt或者matlab可以发送这个“close”消息给
                    // 界面，以通知界面关闭Socket服务进程，并且可以清除界面中所有的仿真信息
                    break; 
                }
            } // while(true)

            server.close(); // 关闭服务器端的socket服务。

        }
        catch(Exception e)
        {
            System.out.println("异常是：" + e);
        }

        return; // main return
    }



    /****************************************************************
     *******************   private methods *************************/
    static private void _handleSocketMessage(String socketMessage)
    {
        // 下面开始重点，解读客户端发送的数据字符串，从中读取出: 
        // （1）数据集的编次，即表达客户端第几次想服务器发送数据集
        // （2）仿真时间
        // （3）各个变量的数据点
        // 客户端发送的消息格式如下——
        // <编次-仿真时间-(变量名称1,数值1)-(变量名称2,数值2)-(变量名称3,数值3)-...-(变量名称n,数值n)>

        // 先进行参数检查
        if( socketMessage == null || socketMessage.isEmpty() )
        {
            return;
        }
        //粗略检查一下客户端Socket消息的格式是否正确， if成立说明消息以"<"开头且以">"结尾
        if( socketMessage.endsWith(">") && socketMessage.startsWith("<") )
        {
            int iLoopCount = _get_loop_count(socketMessage);
            if( iLoopCount >= 1 ) // socket消息的数据集循环次数合法
            {
                if( iLoopCount == 1 ) // 档次仿真过程发送的第一条socket消息
                {
                    // 下面开始做一些初始化的工作
                    _clearOldSimulation(); // 清除旧的仿真信息

                    _aryListVariableName = _getPointNameAryList(socketMessage);

                    if( _aryListVariableName.isEmpty() || _aryListVariableName == null)
                    {
                        // 弹出错误对话框
                        JOptionPane.showInternalMessageDialog(mainFrame, 
                                "新的仿真程序没有可检测的变量，请检查你的仿真模型！", 
                                "错误！无仿真变量供检测！", JOptionPane.ERROR_MESSAGE);
                        return; // 结束这次消息处理
                    }
                    else
                    {

                    }
                }
            }
            else // socket消息的数据集循环次数不合法
            {
                //数据出现异常，可以考虑以生成日志文件的形式来排查错误
                return;
            }
            ArrayList AryList_pointName = null;
            ArrayList AryList_value = null;
        } //  if( socketMessage.endsWith(">") && socketMessage.startsWith("<") )
    }
    
    static private void _clearOldSimulation()
    {
        if( _aryListVariableName != null || !_aryListVariableName.isEmpty() )
        {
            // 说明这次仿真之前，已经存在仿真进程,清空所有仿真变量的Plot面板
        }

    }
    
    static private void _startNewSimulationInVergil(String startVergilcmd)
    {
        Runtime run = Runtime.getRuntime(); //启动与应用程序相关的运行时对象
        // 这里已经试过，必须使用try-catch结构才行
        try {   
            Process p = run.exec(startVergilcmd);// 启动另一个进程来执行 指定的系统 命令   
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());   
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));   
            String lineStr;   
            while ((lineStr = inBr.readLine()) != null)   
                //获得命令执行后在控制台的输出信息   
                // 控制台有输出信息，那说明本机没有安装好Ptolemy II或者没有为Ptolemy II设置好环境变量

                System.out.println(lineStr);// 打印输出信息   
            //检查命令是否执行失败。   
            if (p.waitFor() != 0) {   
                if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束 
                    JOptionPane.showMessageDialog(mainFrame, 
                            lineStr + "!" + "出现这种错误，有两种可能：（1）本机没有安装Ptomely II；\n;"
                                    + "（2）本机已正确安装了Ptolemy II，但是没有为其设置好系统环境变量", 
                                    "无法启动Vergil", 
                                    JOptionPane.ERROR_MESSAGE);
                System.err.println("命令执行失败!  ");   
            }   
            inBr.close();   
            in.close();   
        } 
        catch (Exception e) {   
            e.printStackTrace();   
        }
    }
    static private int _get_loop_count(String str)
    {
        return -1;
    }
    static private double _get_simulation_time(String str)
    {
        return 0.0;
    }
    static private ArrayList _getPointNameAryList(String str)
    {
        return null;
    }
    static private ArrayList _getPointValueAryList(String str)
    {
        return null;
    }


}
