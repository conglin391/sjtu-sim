package winSurface;
import javax.swing.*;

import java.util.regex.Pattern;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import ptolemy.plot.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*; // java数据结构的包，用到其中的 ArrayList

//对代码结构做了调整，将各按钮动作集合到一起 方便代码阅读和修改,所有事件集合到actionPerformed类 -ZH
/**
 * @author ZH
 *
 */
public class WinMain implements ActionListener{

    /****************************************************************
     *******************   private members *************************/

    private static final long serialVersionID = 1L;
    private int WIN_WIDTH; // 窗口的长
    private int WIN_HEIGHT; // 窗口的宽
    static JFrame mainFrame = null; // 程序主窗口变量
    
    static JConsoleTabbedPanel _consoleTabbedPanel = null; // add by bruse 2014-10-21

    static JPhysicalDataPlotPanel _physicalPlotPanel = null; // add by Bruse 2014-10-12

    static JDataShowTablePanel _dataExchangePanel = null; // add by Bruse 2014-10-12

    static JTabbedPane mainTabbedPane = null; // JRootPane中内容面板要加载的选项卡面板
    JMenuBar menuBar = null;
    JMenu menuFile = null;
    JMenu menuEdit = null;
    JMenu menuView = null;
    JMenu menuTool = null;
    JMenu menuHelp = null;

    // 这里考虑后续的扩展需求，暂时将这两个成员变量保留
    static ArrayList _aryListVariableName = null; // 用来记录一次仿真过程，涉及到的全部变量名称
    static ArrayList _aryListVariableValue = null; // 用来记录一次仿真过程，涉及到的所有变量的值

    static ArrayList _aryListDataSets = null; // 用来存放数据点
    // 这个 _aryListDataSets 变量稍显复杂，它的里面每个元素都是ArrayList类型，_aryListDataSets中单个ArrayList元素
    // 又分别存放了两个ArrayList元素（分别用来存放一个数据集的变量名称集合和仿真数值集合），举个例子来说，一个 _aryListDataSets 的数据组成可能是下面的内容：
    //
    //  _aryListDataSets(ArrayList类型)
    //     |--(ArrayList类型元素)
    //     |       |--(ArrayList类型元素)
    //     |       |       |--[Ppv],[V],[V1]  .....里面存放了3个String类型变量，这是变量的名称
    //     |       |--(ArrayList类型元素)
    //     |               |--[8.9],[1.23],[9.876]  .....里面存放了3个double类型数值，和上面的3个String类型相对应，这是变量的仿真数值
    //     |--(ArrayList类型元素)
    //     |       |--(ArrayList类型元素)
    //     |       |       |--[I1],[I2]   ..... 里面存放了2个String类型的变量名称
    //     |       |--(ArrayList类型元素)
    //     |               |--[0.78],[1.482]  ..... 里面存放了2个double类型的仿真数值
    //     |--(ArrayList类型元素)
    //             |--(ArrayList类型元素)
    //             |       |--[SOC]
    //             |--(ArrayList类型元素)
    //                     |--[0.845]

    static int _iExtraTabbedPaneNum = 0; // 用来记录当前仿真程序共产生了多少个额外的选项卡面板，所谓的Extra就是除去“控制台”选项卡，程序新添加的所有选项卡数量

    static ArrayList _aryListSimulinkPlotPanel = null; // 用来存放绘制物理仿真结果的那几个Card面板 --add by Bruse 2014-10-12
    static ArrayList <JPanel>_aryListPlotHoldPanel = null; // 用来存放Plot的面板的动态数组
    static ArrayList <Plot>_aryListPlotPanel = null; // 用来存放每个选项卡中的Plot画图面板

    static boolean _bSimulationBegin = false; // 用来指示当前时间，界面是否有仿真在运行

    static int _iLoopCount = 0;

    // add by Bruse, 2014-8-23
    static double _dSimulationTime = 0.0; // 记录当前的仿真时间，用于后面 _handleNextSocket函数对socket消息做过滤检查

    // add by Bruse, 2014-9-15
    static String _mdlFileName = null; // 记录matlab中的mdl文件名，一般日后扩展，考虑通过matlab命令控制simulink暂停和继续，那样
    // 的话，_mdlFileName必不可少

    static String _simulinkDataTabpaneName = "仿真曲线"; // 物理系统仿真产生的数据曲线选项卡名称
    // add by Bruse, 2014-9-26
    static ArrayList _aryListCyberDataset = null; // 记录PT向Simulink发送的数据
    // 这个ArrayList存放的是两个ArrayList，其中，一个用来存放变量名称，一个用来存放变量的值
    // 这个_aryListCyberDataset本身的结构就像下面的_aryListPhysicalDataSet表中的一个元素一样。


    static ArrayList _aryListPhysicalDataset = null;
    // 这个 _aryListPhysicalDataSet 变量稍显复杂，它的里面每个元素都是ArrayList类型，_aryListPhysicalDataSet中单个ArrayList元素
    // 又分别存放了两个ArrayList元素（分别用来存放一个数据集的变量名称集合和仿真数值集合），举个例子来说，一个 _aryListPhysicalDataSet 的数据组成可能是下面的内容：
    //
    //  _aryListPhysicalDataSet(ArrayList类型)
    //     |--(ArrayList类型元素)
    //     |       |--(ArrayList类型元素)
    //     |       |       |--[Ppv],[V],[V1]  .....里面存放了3个String类型变量，这是变量的名称
    //     |       |--(ArrayList类型元素)
    //     |               |--[8.9],[1.23],[9.876]  .....里面存放了3个double类型数值，和上面的3个String类型相对应，这是变量的仿真数值
    //     |--(ArrayList类型元素)
    //     |       |--(ArrayList类型元素)
    //     |       |       |--[I1],[I2]   ..... 里面存放了2个String类型的变量名称
    //     |       |--(ArrayList类型元素)
    //     |               |--[0.78],[1.482]  ..... 里面存放了2个double类型的仿真数值
    //     |--(ArrayList类型元素)
    //             |--(ArrayList类型元素)
    //             |       |--[SOC]
    //             |--(ArrayList类型元素)
    //                     |--[0.845]


    //将所有Action集合到此处，便于编写和修改 -ZH
    //因为本软件各种交互事件较多，建议：所以主界面中需要添加事件的，可直接添加.addActionListener(this)，然后在此类中捕获后用if判断，执行响应动作
    //若动作语句较长，建议分拆成单独的函数，利于结构的清晰（如打开xml，我暂且分拆成openPTxml函数）
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("打开(O)")) {     
            openPTxml();      
        }
        if (cmd.equals("启动MATALB")){
            try {
                Runtime.getRuntime().exec("matlab");
            } catch (IOException e1) {
                e1.printStackTrace();
                //输出启动异常信息~~~~
            }
        }
        if (cmd.equals("启动Vergil")){
            try {
                Runtime.getRuntime().exec("vergil");
            } catch (IOException e1) {
                e1.printStackTrace();
                //输出启动异常信息~~~~
            }
        }
        if (cmd.equals("退出程序(X)")){
            //之后再加个确认窗口
            System.exit(0);
        }
        

    }
    
    public boolean getSimulationStatus()
    {
        return _bSimulationBegin;
    }
    public void setSimulationStatus(boolean simulationStatus)
    {
        _bSimulationBegin = simulationStatus;
    }

    public WinMain()
    {
        // 类成员变量的初始化
        _aryListVariableName = new ArrayList();
        _aryListVariableValue = new ArrayList();
        _aryListDataSets = new ArrayList();
        _aryListPlotHoldPanel = new ArrayList();
        _aryListPlotPanel = new ArrayList();

        _aryListCyberDataset = new ArrayList();
        _aryListPhysicalDataset = new ArrayList();
        _aryListSimulinkPlotPanel = new ArrayList();

        _iExtraTabbedPaneNum = 0; // 所谓的Extra就是除去“控制台”选项卡，程序新添加的所有选项卡数量

        _bSimulationBegin = false;

        _iLoopCount = 0;

        _dSimulationTime = 0.0; // add by Bruse, 2014-8-23


        // 下面开始创建出界面窗口，并在窗口中添加好各个菜单项
        _createWindowAndMenu();


        // 下面开始创建选项卡面板，并创建一个“控制台”选项卡加入到选项卡面板中去
        _createTabbedPaneAndConsoleTabbedPanel();

        _dataExchangePanel = new JDataShowTablePanel();
        mainTabbedPane.addTab("数据交互", _dataExchangePanel);
        //额外选项卡数量记录+1    add by Bruse, 2014-10-12
        ++_iExtraTabbedPaneNum;

        /*String[] a = {"avc","12.2","123"};
        String[] b = {"V","0.063","9.0234"};
        for (int i=0;i<10;i++)
        {
            if(i % 4 == 0)
            { _dataExchangePanel.addOneLine(a, b); }
            else
            { _dataExchangePanel.addOneLine(b, a); }
        }*/


        /** 下面是测试 JPhysicalDataPlotPanel类的代码
         *  add by Bruse , 2014-10-11
         * */
        /*JPhysicalDataPlotPanel physicalPlotPanel = new JPhysicalDataPlotPanel();
        mainTabbedPane.add(physicalPlotPanel, "仿真曲线");
        JPanel plotPaneltemp = new JPanel();

        plotPaneltemp.add(new Plot(),BorderLayout.CENTER);
        physicalPlotPanel.addOneCard(plotPaneltemp);
        physicalPlotPanel.addOneCard(new JPanel());
        physicalPlotPanel.showButtons();*/

        // OK,上面几行代码测试通过
    }

    private void _createWindowAndMenu()
    {
        // 下面开始创建出界面窗口，并在窗口中添加好各个菜单项
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){}
        mainFrame = new JFrame("SimJ&M"); // 创建程序窗口，窗口标题为 SimJ&M
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 获取屏幕大小，并设置窗口初始的显示位置
        Toolkit kit= Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();

        // 设置窗口初始尺寸
        WIN_WIDTH = screenSize.width - 50;
        WIN_HEIGHT = screenSize.height -50;

        int x = (screenSize.width - WIN_WIDTH)/2;
        int y = (screenSize.height - WIN_HEIGHT)/2;
        mainFrame.setLocation(x, y); // 设置窗口出现时，在屏幕的中央

        mainFrame.setVisible(true); // 默认为false
        mainFrame.setSize(WIN_WIDTH, WIN_HEIGHT); // 设置窗口的尺寸

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
        //添加事件侦听 -ZH
        menuItemOpenInFile.addActionListener(this);
        // 为“打开”这个菜单项 设定 快捷键 --- Ctrl + O
        menuItemOpenInFile.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemOpenInFile); // 将这个菜单项添加到“文件”菜单下
        menuFile.addSeparator();

        JMenuItem menuItemOpenMATALBInFile = new JMenuItem("启动MATALB");
        menuItemOpenMATALBInFile.addActionListener(this);
        menuFile.add(menuItemOpenMATALBInFile);

        JMenuItem menuItemOpenPTInFile = new JMenuItem("启动 Vergil");
        menuItemOpenPTInFile.addActionListener(this);
        menuFile.add(menuItemOpenPTInFile);

        menuFile.addSeparator();
        JMenuItem menuItemExitInFile = new JMenuItem("退出程序(X)");
        menuItemExitInFile.addActionListener(this);
        menuItemExitInFile.setAccelerator(KeyStroke.getKeyStroke('X', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemExitInFile);        

        
        JMenuItem menuItemClearOldSimulation = new JMenuItem("清楚所有仿真信息(R)");
        menuItemClearOldSimulation.setAccelerator(KeyStroke.getKeyStroke('R', java.awt.Event.CTRL_MASK, false));
        menuEdit.add(menuItemClearOldSimulation);
        
        
        
        JMenuItem menuItemConfigure = new JMenuItem("设置");
        menuTool.add(menuItemConfigure);
        
        
        JMenuItem menuItemGetHelpInHelp = new JMenuItem("使用说明");
        menuItemGetHelpInHelp.addActionListener(this);
        menuHelp.add(menuItemGetHelpInHelp);
        menuHelp.addSeparator();

        JMenuItem menuItemInfoInHelp = new JMenuItem("关于SimJ&M");
        menuItemInfoInHelp.addActionListener(this);
        menuHelp.add(menuItemInfoInHelp);
    }


    private void _createTabbedPaneAndConsoleTabbedPanel() {
        // 下面开始创建选项卡面板，并创建一个“开始”选项卡加入到选项卡面板中去

        mainTabbedPane = new JTabbedPane(); // 创建窗口加载的选项卡面板
        mainFrame.setContentPane(mainTabbedPane); // 设置窗口的显示面板为 上面的选项卡面板
        mainTabbedPane.setVisible(true); // 设置这个选项卡面板可见
        // 设置选项卡面板的大小及其选项卡的位置方向
        // mainTabbedPane.setPreferredSize(new Dimension(500,200));
        mainTabbedPane.setTabPlacement(JTabbedPane.TOP);
        // 设置选项卡在容器内的显示形式
        mainTabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        // mainTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // mainFrame.pack(); // 让主窗口适应组件的大小

        
        // 添加选项卡容器，并且设置每个选项卡的标签以及其是否可用

        /*
         * JPanel panel0 = new JPanel(); panel0.setSize(800,900);
         * panel0.setBorder(new LineBorder(new Color(255,255,100),2,true));
         * 
         * 
         * JScrollPane jScrollPanel0 = new JScrollPane(panel0,
         * ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
         * ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
         */

        /*
         * jScrollPanel0.setLayout(new BorderLayout());
         * 
         * jScrollPanel0.add(panel0,"Center");
         */

        // JScrollPane startpanel = new JScrollPane();

        _consoleTabbedPanel = new JConsoleTabbedPanel(mainFrame, this);
        mainTabbedPane.addTab("控制台", _consoleTabbedPanel); // 将“开始”面板 panel0 作为第一个选项卡
        mainTabbedPane.setEnabledAt(0, true);

        // jScrollPanel0.scrollRectToVisible(new Rectangle(900,500));
        
        
        
    }

    
    private void openPTxml(){
        // 创建一个文件选择对话框，供用户选择 .xml 文件
        JFileChooser fileChooser = new JFileChooser("D:\\");
        // 加入文件类型选择 -ZH
        fileChooser.addChoosableFileFilter(new JAVAFileFilter("xml"));
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
                
                // 先将选中的文件路径添加到“控制台”的XML文件列表中
                _consoleTabbedPanel.addOneXMLfilePath(file.getAbsolutePath());
                
                //String startVergilcmd = "vergil -run E:\\PT_workspace\\pvbattery_50-60_org.xml";
                //针对路径空格问题，增加双引号  -ZH
                String startVergilcmd = "vergil -run \"" + file.getAbsolutePath()+"\"";

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
                        // 清除当前仿真的所有选项卡以及相关信息
                        // _clearOldSimulation(); 此处考虑暂不执行这个函数，保留旧的仿真所有信息，因为可能新的仿真无效（比如xml不正确）

                        // 修改 _bSimulationBegin 的值，表示可以开启新的仿真

                        //kill PT Matlab进程 --ZH

                        try {
                            // 经测试，这里不能简单地创建两个进程kill掉matlab呃vergil，
                            // 必须等待两个进程执行完毕，才能继续执行if结构外面的代码，否则if结构外面的
                            // _startNewSimulationInVergil 函数执行失效，不能启动新的vergil程序
                            // 出现这样的情况，可能的原因是两个taskkill进程在_startNewSimulationInVergil函数执行时，
                            // 还没有结束，这样第二条taskkill进程会kill掉新出现的vergil程序

                            String commandStr="taskkill /f /im MATLAB.exe"; 
                            Process p = Runtime.getRuntime().exec(commandStr);
                            // 等待命令执行完毕，在执行后续操作
                            if(p.waitFor() != 0)
                            {
                                if( p.exitValue() == 1 ) // 0表示正常结束，1：非正常结束
                                {
                                    // do nothing
                                }
                                else
                                {
                                    // 表示taskkill命令执行失败，考虑生成日志文件报告错误
                                }
                            }
                            commandStr="taskkill /f /im vergil.exe"; 
                            p = Runtime.getRuntime().exec(commandStr);
                            // 等待命令执行完毕，在执行后续操作
                            if(p.waitFor() != 0)
                            {
                                if( p.exitValue() == 1 ) // 0表示正常结束，1：非正常结束
                                {
                                    // do nothing
                                }
                                else
                                {
                                    // 表示taskkill命令执行失败，考虑生成日志文件报告错误
                                }
                            }
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        _bSimulationBegin = false;

                    }
                    else
                    {
                        return; // 事件处理函数结束，不会启动新的仿真
                    }                            
                } //  if( _bSimulationBegin == true )

                // 开启仿真
                _startNewSimulationInVergil(startVergilcmd);
                
                // 将“控制台”选项卡中的“暂停”“停止”按钮激活
                _consoleTabbedPanel.setStopAndPauseButtonEnabled(true);
                
                // 将“控制台”总时间文本框关闭
                _consoleTabbedPanel.setSimTimeEditable(false);
                
                // 将“控制台”两个进度显示空间重置
                _consoleTabbedPanel.resetProgress();


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

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new WinMain(); // 创建出主窗口

        // 在这个主函数里，测试能否动态向 mainTabbedPane添加新的选项卡

        /*JPanel panel_1 = new JPanel();       
        mainTabbedPane.addTab("由主函数添加",panel_1); 
        mainTabbedPane.setEnabledAt(1,true);*/
        // 经测试，上面代码是OK的，也就是可以在主函数中动态添加选项卡面板


        /*// 下面开始测试添加2个以上的Plot面板，可能有很大的问题

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
        testPlot1.fillPlot(); */      

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
            server = new ServerSocket(4700); // 在端口4700上创建服务器Socket对象

            /****************** 
             * 下面while循环代码经过测试是OK的，可以接受多次单独的socket客户端连接发送的消息
             * **********************************/
            while(true)
            {
                socket = server.accept(); /*至此，调用该方法的服务器进程阻塞，直到收到客户端的请求；
                                                                当客户端在设定的端口有请求时，服务器Socket和客户端的Socket将绑定起来*/
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

                if( str_tem == "close" ) 
                { 
                    // 象征性地给出一个berak分支语句，可能在软件后期开发中有用处，比如pt或者matlab可以发送这个“close”消息给
                    // 界面，以通知界面关闭Socket服务进程，并且可以清除界面中所有的仿真信息
                    break; 
                }

                String str = "";
                if( str_tem.startsWith("<socketMessage>") )
                {

                    // 一个xml-socket消息的开始
                    if(str_tem.endsWith("</socketMessage>"))
                    {
                        // 接收到一个完整的<socketMessage>..</socketMessage>消息
                        System.out.println("客户端：" + str_tem); // 显示字符串    

                        String str_dataset = str_tem.trim();

                        _handleSocketMessage(str_dataset);

                    }
                    else
                    {
                        System.out.println("客户端：" + str_tem); // 显示字符串    
                        // 继续接收不完整的消息，直到有 </socketMessage> 结束标记
                        while( !(str = bfreader.readLine()).endsWith("</socketMessage>") )
                        {
                            if( str.startsWith("<socketMessage>") )
                            {
                                // 一个完整的socket消息没有接受完，却又过来一个消息头，这是严重错误
                                // 考虑生成日志文件来记录这个错误
                                // 此处，先知打印出这个错误到控制台窗口
                                System.out.println("fatal error!\r Socket消息接收出现多个消息头部！");
                                break;
                            }
                            else
                            {
                                System.out.println("客户端：" + str); // 显示字符串    
                                str_tem = str_tem + str;
                            }
                        }
                        str_tem = str_tem + str;
                        // 接收到一个完整的<socketMessage>..</socketMessage>消息
                        System.out.println("客户端：" + str_tem); // 显示字符串    

                        String str_dataset = str_tem.trim();

                        _handleSocketMessage(str_dataset);
                    }
                }
                else // if( str_tem.startsWith("<socketMessage>") )
                {
                    // 这也是个致命的错误，消息开始传输，却没有消息头
                    System.out.println("fatal error! socket消息没有消息头！");
                }

                // System.out.println("客户端：" + str_tem); // 显示字符串    

                // 关闭这次socket连接
                bfreader.close();
                //ps.close();
                fIn.close();
                //fOut.close();
                socket.close();
                /* ！！！！！！这里值得注意，ServerSocket——server 并没有被关闭！！！！！！ */


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
     * //------不是很理解为何所有方法都要设成了静态方法？                    -ZH
     *******************   private methods *************************/

    /** 以下是将socket格式设置成xml之后，用于处理socket消息所使用的函数定义，包括xml解析函数，数据提取函数
     * 更新于 2014年9月26日，by Bruse
     * ---函数定义开始 */

    // xml语法解析函数
    static String _getTagValue(String tagName, String xmlText)
    {
        // 根据标签tagName的内容，获取该标签对包含的值.
        // 例如，对于一个标签对  xmlText = "<point>0.89</point>",调用函数 _getTagValue("point",xmlText)
        // 将得到字符串 "0.89"
        // 如果xmlText含有多个 tagName标签，将以第一个tagName为准

        // 参数检查
        if( tagName == null || xmlText == null || tagName.isEmpty() || xmlText.isEmpty())
        { return null; }

        // 检查xmlText中是否有<tagName>..</tagName>标签对
        String PatternStr = "^.*<" + tagName + ".*>.*</" + tagName + ">.*$";
        boolean b = Pattern.matches(
                PatternStr,
                xmlText);
        if(b == false)
        {  return null;}

        // 开始提取
        int iBegin = xmlText.indexOf(tagName); // 找到tagName起始位置
        iBegin = xmlText.indexOf('>', iBegin); // 找到tagName后面的 '>' 字符位置
        int iEnd = xmlText.indexOf('<', iBegin);
        // iBeign 和 iEnd 之间的内容就是tagName标签对包含的东西。
        if( iBegin + 1 >= iEnd ) // tagName标签对之间没有包含任何内容
        {return null;}
        String tagValue = xmlText.substring(iBegin + 1, iEnd);

        return tagValue.trim(); // 去掉tagValue开头和结尾可能存在的空白字符后返回
    }

    static String _getTagPropertyValue(String tagName, String propertyName, String xmlText)
    {
        // 根据标签tagName的内容，获取该标签属性propertyName对应的值.
        // 例如，对于一个标签对  xmlText = "<point name="SOC">0.89</point>",调用函数 
        // _getTagPropertyValue("point","name",xmlText)
        // 将得到字符串 "SOC"
        // 如果xmlText含有多个 tagName标签，将以第一个tagName为准

        // 参数检查
        if(tagName.isEmpty() || propertyName.isEmpty() || xmlText.isEmpty()
                || tagName == null || propertyName == null || xmlText == null)
        {return null; }

        // 检查xmlText中是否有<tagName propertyName="..">..</tagName>标签对
        String PatternStr = "^.*<" + tagName + "\\s+" + propertyName +"\\s*=\\s*\".+\".*>.*</" + tagName + ">.*$";
        boolean b = Pattern.matches(
                PatternStr,
                xmlText);
        if(b == false)
        {  return null;}

        // 开始提取
        int iBegin = xmlText.indexOf("<" + tagName + " "); // 找到 <tagName_   起始位置,_表示tagName后面有一个空格
        iBegin = xmlText.indexOf(propertyName, iBegin); // 找到tagName后面的 propertyName 字符位置
        iBegin = xmlText.indexOf('"', iBegin); // 找到propertyName后面的 " 的位置
        int iEnd = xmlText.indexOf('"', iBegin+1); // 正则表达式匹配后，一定能找到成对的双引号
        // iBeign 和 iEnd 之间的内容就是tagName标签对包含的东西。
        String tagValue = xmlText.substring(iBegin + 1, iEnd);

        return tagValue.trim(); // 去掉tagValue开头和结尾可能存在的空白字符后返回
    }

    static int _getLoopCountFromXML(String xmlSocketMessage)
    {
        String sLoopCount = _getTagValue("iteration",xmlSocketMessage);
        if( sLoopCount == null ) return -1;
        int iLoopCount = Integer.valueOf(sLoopCount);

        // 测试性输出语句
        System.out.println("loop count is :" + iLoopCount);
        return iLoopCount;
    }

    static double _getSimulationTimeFromXML(String xmlSocketMessage)
    {
        String sTime = _getTagValue("simulationTime",xmlSocketMessage);
        if( sTime == null ) return 0.0;
        return Double.valueOf(sTime);
    }


    static String _getSubXML(String tagName, String xmlMessage, int iFromIndex)
    {
        int iBegin = xmlMessage.indexOf("<" + tagName, iFromIndex);
        if( iBegin == -1) return null;
        int iEnd = xmlMessage.indexOf("</" + tagName + ">", iBegin);
        iEnd = iEnd + 3 + tagName.length();
        return xmlMessage.substring(iBegin, iEnd);
    }

    static String _getSubXML(String tagName, String xmlMessage)
    {
        int iBegin = xmlMessage.indexOf("<" + tagName);
        if( iBegin == -1) return null;
        int iEnd = xmlMessage.indexOf("</" + tagName + ">", iBegin);
        iEnd = iEnd + 3 + tagName.length();
        return xmlMessage.substring(iBegin, iEnd);
    }
    static ArrayList _getPtolemyDataset(String xmlSocketMessage)
    {
        // 获取xml格式定义的socket消息中PT发送给Simulink的数据

        ArrayList <ArrayList>aryListPtolemyDataset = new ArrayList();

        // 参数检查
        if(xmlSocketMessage == null || xmlSocketMessage.isEmpty())
        {
            return aryListPtolemyDataset;
        }

        ArrayList <String>aryListPointName = new ArrayList();
        ArrayList <Double>aryListPointValue = new ArrayList();

        String ptDataset = _getSubXML("PtolemyDataset", xmlSocketMessage);
        int iBegin = 0, iEnd = 0;
        while( (iBegin = ptDataset.indexOf("<point", iEnd)) != -1 ) // 还有<point></point>数据对没有访问到
        {
            iEnd = iBegin + "point".length();
            String pointXML = _getSubXML("point",ptDataset,iBegin);
            aryListPointName.add(_getTagPropertyValue("point","name",pointXML));
            aryListPointValue.add(Double.valueOf( _getTagValue("point",pointXML) ));
        }

        aryListPtolemyDataset.add(aryListPointName);
        aryListPtolemyDataset.add(aryListPointValue);

        // 测试输出
        System.out.println("---data from PT:");
        aryListPointName = aryListPtolemyDataset.get(0);
        aryListPointValue = aryListPtolemyDataset.get(1);
        for(int i = 0; i < aryListPointName.size(); ++i)
        {
            System.out.println("  name:" + aryListPointName.get(i) + ", value:" + 
                    aryListPointValue.get(i));
        }

        return aryListPtolemyDataset;
    }

    static ArrayList _getSimulinkDataset(String xmlSocketMessage)
    {
        // 获取xml格式定义的socket消息中包含的simulink仿真数据，也就是Simulink反馈给PT的仿真数据

        ArrayList <ArrayList>aryListSimulinkDataset = new ArrayList();
        // 参数检查
        if(xmlSocketMessage == null || xmlSocketMessage.isEmpty())
        {
            return aryListSimulinkDataset;
        }

        ArrayList <ArrayList>aryListdataset = new ArrayList();
        ArrayList <String>aryListPointName = new ArrayList();
        ArrayList <Double>aryListPointValue = new ArrayList();

        String simulinkDataset = _getSubXML("SimulinkDatasets",xmlSocketMessage);

        int iDatasetBegin = 0, iDatasetEnd = 0;
        while( (iDatasetBegin = simulinkDataset.indexOf("<dataset", iDatasetEnd)) != -1 ) // 还有<dataset></dataset>标签对没有访问
        {
            String strDataset = _getSubXML("dataset",simulinkDataset,iDatasetBegin);
            aryListdataset = new ArrayList();
            iDatasetEnd = iDatasetBegin + "<dataset".length();
            int iBegin = 0, iEnd = 0;
            aryListPointName = new ArrayList();
            aryListPointValue = new ArrayList();
            while( (iBegin = strDataset.indexOf("<point", iEnd)) != -1 ) // 还有<point></point>数据对没有访问到
            {
                iEnd = iBegin + "<point".length();
                String pointXML = _getSubXML("point",strDataset,iBegin);
                aryListPointName.add(_getTagPropertyValue("point","name",pointXML));
                aryListPointValue.add(Double.valueOf(_getTagValue("point",pointXML)));
            }
            aryListdataset.add(aryListPointName);
            aryListdataset.add(aryListPointValue);
            aryListSimulinkDataset.add(aryListdataset);
        }

        // 测试输出
        System.out.println("datasets from simulink");
        for(int i = 0; i < aryListSimulinkDataset.size(); ++i)
        {
            aryListdataset = aryListSimulinkDataset.get(i);
            aryListPointName = aryListdataset.get(0);
            aryListPointValue = aryListdataset.get(1);
            System.out.println("  第" + i + "个点集的数据：");
            for(int k = 0; k < aryListPointName.size(); ++k)
            {
                System.out.println("\tname:" + aryListPointName.get(k) + "\tvalue:" + aryListPointValue.get(k));
            }
        }

        return aryListSimulinkDataset;
    }

    /** 以上是将socket格式设置成xml之后，用于处理socket消息所使用的函数定义，包括xml解析函数，数据提取函数
     * 更新于 2014年9月26日，by Bruse
     * ---函数定义结束 */

    static private void _handleSocketMessage(String socketMessage)
    {
        // 下面开始重点，解读客户端发送的数据字符串，从中读取出: 
        // （1）数据集的编次，即表达客户端第几次向服务器发送数据集
        // （2）仿真时间
        // （3）各个变量的数据点
        // 客户端发送的消息格式如下——
        // <仿真循环编次-仿真时间-[(变量1,数值1)-(变量2,数值2)-...-(变量n,数值n)]-[(变量1,数值1)-(变量2,数值2)-...-(变量n,数值n)]-...-[(变量1,数值1)-(变量2,数值2)-...-(变量n,数值n)]>

        // 先进行参数检查
        if( socketMessage == null || socketMessage.isEmpty() )
        {
            return;
        }

        // 检查客户端发送来的消息格式是否正确，使用正则表达式检查
        // String str_regex = "^<[0-9]+\\-[0-9]+\\.?[0-9]*(\\-\\[\\(.+,.+\\)(\\-\\(.+,.+\\))*\\])+>$";
        String str_regex = "^<socketMessage>.*"
                + "<iteration>\\s*\\d*\\.?\\d*\\s*</iteration>.*"
                + "<simulationTime>.*</simulationTime>.*"
                + "<PtolemyDataset.*>.*"
                + "(<point\\s+name.*>.*</point>.*)+.*"
                + "</PtolemyDataset>.*"
                + "<SimulinkDatasets>.*"
                + "(<dataset>.*(<point\\s+.*>\\s*\\d*\\.?\\d*\\s*</point>.*)+</dataset>.*)+.*"
                + "</SimulinkDatasets>.*"
                + "</socketMessage>$";
        boolean b = Pattern.matches(
                str_regex,
                socketMessage);
        if(b == true) // 消息格式正确，可以进行下一步的处理工作
        {
            System.out.println("xml-socket消息格式正确！");

            int iLoopCount = _getLoopCountFromXML(socketMessage);
            if( iLoopCount > 0 ) // socket消息的数据集循环次数合法
            {
                // 获取socket消息中的数据集


                if( iLoopCount == 1 && _bSimulationBegin == false) 
                {
                    // 当次仿真过程发送的第一条socket消息，而且没有仿真程序在执行或者可以开启新的仿真程序
                    _handleFirstSocketXML(socketMessage);

                }
                else
                {
                    // 处理后续的socket消息，动态解析数据点并添加到plot面板中去即可
                    _handleNextSocketXML(socketMessage);
                }
            }
            else // socket消息的数据集循环次数不合法
            {
                //数据出现异常，可以考虑以生成日志文件的形式来排查错误

                return;
            }

        } //  if( socketMessage.endsWith(">") && socketMessage.startsWith("<") )
        else
        {
            // ******************************** 这里考虑最好生成警告对话框
            System.out.println("socket消息格式错误！！！");
            return;
        }
    }

    // add by Bruse, 2014-9-27
    static private void _handleFirstSocketXML(String socketMessage)
    {
        // 当次仿真过程发送的第一条socket消息，而且没有仿真程序在执行或者可以开启新的仿真程序

        // 先进行参数检查
        if(socketMessage.isEmpty())
        {
            return;
        }

        // 下面开始做一些初始化的工作
        _clearOldSimulation(); // 清除旧的仿真信息

        // 获取socket消息中的数据集
        ArrayList <ArrayList>aryListCyberDataset = _getPtolemyDataset(socketMessage);
        ArrayList <ArrayList>aryListPhysicalDataset = _getSimulinkDataset(socketMessage);


        if( aryListCyberDataset.isEmpty() || aryListPhysicalDataset.isEmpty())
        {
            // 弹出错误对话框
            JOptionPane.showInternalMessageDialog(mainFrame, 
                    "新的仿真程序没有检测到数据的传输，请检查您的仿真模型！", 
                    "错误！无仿真变量可供监测！", JOptionPane.ERROR_MESSAGE);
            return; // 结束这次消息处理
        }
        else // 
        {
            // 检测一下获取的数据集是否正确合理
            /*if( !_validateDataSet(aryListDataSets, true) )
            {
                // 数据集不合法，退出这次消息处理
                return;
            }*/

            // 将第一次socket消息数据集保存到类的静态列表中
            _aryListCyberDataset = aryListCyberDataset;
            _aryListPhysicalDataset = aryListPhysicalDataset;

            double dSimulationTime = _getSimulationTimeFromXML(socketMessage);


            // 更具两个数据集列表aryListCyberDataset和aryListCyberDataset，
            // 把数据点动态添加到动态表格(_dataExchangePanel)中
            _addExchangeDatasIntoTable(aryListCyberDataset,0.0,aryListPhysicalDataset,dSimulationTime);


            // 存放每个plot面板对应的标题
            Vector<String> v_plotCaption = new Vector();
            
            // 暂时没有实现把 aryListCyberDataset中的数据点画出来，也就是，没有吧PT传给simulink的数据画出来
            // 消息正确，开始针对aryListPhysicalDataset中每个变量数据集，添加好plot画图选项卡
            for(int i = 0; i < aryListPhysicalDataset.size(); ++i)
            {
                // 创建出一个Plot面板，以及其依附的JPanel
                JPanel panelPlot = new JPanel();
                panelPlot.setLayout(new BorderLayout());
                Plot testPlot = new Plot();

                // 将创建好的 panelPlot 和 testPlot 添加到 本类的静态列表中
                _aryListPlotHoldPanel.add(panelPlot);
                _aryListPlotPanel.add(testPlot);

                ((JPanel)_aryListPlotHoldPanel.get(i)).add((Plot)_aryListPlotPanel.get(i)
                        , BorderLayout.CENTER);
                // testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // 有效，以像素为单位，确定画图区域的范围
                ((Plot)_aryListPlotPanel.get(i)).setButtons(true);// 有效，非常重要



                String tabTitle = ""; // 由变量名称组成的选项卡标题
                Vector vcCaption = new Vector(); // 用于存放每个变量的名称，在设置每条曲线的标示时使用

                ArrayList aryListDataSet = (ArrayList)aryListPhysicalDataset.get(i);
                ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
                ArrayList<Double> aryListPointValue = (ArrayList<Double>)aryListDataSet.get(1);
                for(int k = 0; k < aryListPointName.size(); ++k)
                {
                    vcCaption.add( (String)aryListPointName.get(k) );
                    tabTitle = tabTitle + "," + (String)aryListPointName.get(k);
                    ((Plot)_aryListPlotPanel.get(i)).addPoint(k, 
                            dSimulationTime, 
                            //Double.valueOf((String) aryListPointValue.get(k)),
                            aryListPointValue.get(k),
                            true);                   
                    //
                }

                // ((Plot)_aryListPlotPanel.get(i)).setCaptions(vcCaption); // 运行结果显示，
                // 这个函数只是在面板的下部添加了几行标题，并不是针对曲线进行添加，有待改进


                ((Plot)_aryListPlotPanel.get(i)).fillPlot(); // 重绘图形

                /*if(false)
                {
                    tabTitle = tabTitle.substring(1, tabTitle.length()); // 去掉第一个变量前的逗号
                    int iTitleLength = tabTitle.length();
                    int iMinTitleLength = 5;
                    if(iTitleLength < iMinTitleLength) // 变量名太短，在后面扩充空格
                    {
                        for(int n = 0; n < iMinTitleLength - iTitleLength; ++n)
                        {
                            tabTitle = tabTitle + " "; 
                        }
                    }
                    mainTabbedPane.addTab( tabTitle , (JPanel)_aryListPlotHoldPanel.get(i));
                    _iExtraTabbedPaneNum++;
                    mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);
                }*/
                
                tabTitle = tabTitle.substring(1, tabTitle.length()); // 去掉第一个变量前的逗号
                v_plotCaption.add(tabTitle);
                
            }

            /* 下面根据数据集，创建好多个绘图面板 */
            int iDatasetNum = aryListPhysicalDataset.size();
            if( iDatasetNum > 0 )
            {
                if( iDatasetNum == 1 ) // 只有一个数据集，这时不需要 JPhysicalDataPlotPanel类的面板来展示数据曲线
                {
                    // 只要将 _aryListPlotHoldPanel中的唯一的面板元素添加进一张 选项卡 即可
                    mainTabbedPane.addTab(_simulinkDataTabpaneName, _aryListPlotHoldPanel.get(0));

                    // 额外选项卡数量记录+1
                    ++_iExtraTabbedPaneNum;
                }
                else
                {
                    
                    // 下面被注释的代码，本想是生成一个集合多个plot的panel并展示出来，结果不如人意，显示失败，有待改进
                    /*// 这种情况下，不止一个数据集，需要产生总的一个绘图panel，还有把_aryListPlotHoldPanel中的面板添加到_simulinkDataTabpaneName中去
                    JScrollPane sumScrollPlotPanel = new JScrollPane(); // 这个panel布局管理将采用 GridLayout
                    JPanel sumPlotPanel = new JPanel();
                    // 根据数据集数量（也就是真实的Plot面板数量），计算需要的表格尺寸（行数、列数）
                    int grid_rowNum = 2;
                    int grid_colNum = 0;
                    if( iDatasetNum % 2 == 0 )
                    {
                        grid_colNum = iDatasetNum / 2;
                    }
                    else
                    {
                        grid_colNum = (iDatasetNum + 1) / 2;
                    }
                    GridLayout grid_layout = new GridLayout(grid_rowNum, grid_colNum);
                    sumPlotPanel.setLayout(grid_layout);
                    for(int i_plot = 0; i_plot < _aryListPlotHoldPanel.size(); ++i_plot)
                    {
                        // 将所有的绘图面板添加到表格中
                        sumPlotPanel.add(_aryListPlotHoldPanel.get(i_plot));
                    }*/

                    // 创建 JPhysicalDataPlotPanel 对象

                    _physicalPlotPanel = new JPhysicalDataPlotPanel();

                    mainTabbedPane.addTab(_simulinkDataTabpaneName, _physicalPlotPanel);

                    // 额外选项卡数量记录+1
                    ++_iExtraTabbedPaneNum;

                   /* _physicalPlotPanel.addOneCard(sumPlotPanel);
                    _aryListSimulinkPlotPanel.add(sumPlotPanel);*/

                    for(int i_plot = 0; i_plot < _aryListPlotHoldPanel.size(); ++i_plot)
                    {
                        // 将所有的绘图面板添加到  _physicalPlotPanel 中
                        _physicalPlotPanel.addOneCard(_aryListPlotHoldPanel.get(i_plot), v_plotCaption.get(i_plot));

                        _aryListSimulinkPlotPanel.add(_aryListPlotHoldPanel.get(i_plot));
                    }
                    // 最后显示出_physicalPlotPanel底部的选页按钮
                    _physicalPlotPanel.showButtons();
                }
            }
            else
            {
                // 出现异常，数据集列表为空,可以考虑生成日志文件
                return;
            }


            // ****函数最后的收尾工作 ****
            // 更新当前类的静态变量，循环次数变量更新为1
            _iLoopCount = 1;

            // 设置已存在仿真进程标志
            _bSimulationBegin = true;
            
            // 更新进度值和进度条的显示
            _consoleTabbedPanel.setProcess(dSimulationTime);

            // 更新仿真时间， add by Bruse, 2014-8-23
            _dSimulationTime = dSimulationTime;
        }
    }

    static private void _handleNextSocketXML(String socketMessage)
    {
        // 先进行参数检查
        if(socketMessage.isEmpty())
        {
            return;
        }

        // 处理后续的socket消息，动态解析数据点并添加到plot面板中去即可
        double dSimulationTime = _getSimulationTimeFromXML(socketMessage);

        if( dSimulationTime > _dSimulationTime ) // 本次socket消息的仿真时间大于当前静态变量记录的时间，是这次仿真的后续循环结果消息
        {
            // 数据合理，开始向每个Plot面板中添加数据点

            ArrayList aryListPhysicalDataset = _getSimulinkDataset(socketMessage); 

            for(int i = 0; i < aryListPhysicalDataset.size(); ++i)
            {
                ArrayList aryListDataSet = (ArrayList)aryListPhysicalDataset.get(i);
                ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
                ArrayList<Double> aryListPointValue = (ArrayList<Double>)aryListDataSet.get(1);

                for(int k = 0; k < aryListPointName.size(); ++k)
                {
                    ((Plot)_aryListPlotPanel.get(i)).addPoint(k, 
                            dSimulationTime, 
                            aryListPointValue.get(k), 
                            true);                   
                }

                ((Plot)_aryListPlotPanel.get(i)).fillPlot(); // 重绘图形
            }


            // 将数据添加到动态表格中显示

            _addExchangeDatasIntoTable(_getPtolemyDataset(socketMessage), _dSimulationTime, aryListPhysicalDataset, dSimulationTime);

            // 更新当前类的静态循环次数变量
            _iLoopCount = _getLoopCountFromXML(socketMessage);

            // 更新进度值和进度条的显示
            _consoleTabbedPanel.setProcess(dSimulationTime);
            
            // 更新当前类的静态存放的仿真时间
            _dSimulationTime = dSimulationTime;

        } 
    }


    static private void _clearOldSimulation()
    {
        if( _iExtraTabbedPaneNum > 1 ) // 说明这次仿真之前，已经存在仿真进程,清空所有仿真变量的Plot面板
        {
            // 从mainTabbedPane中逆序删除所有Plot选项卡面板
            for(int i = 1; i < _iExtraTabbedPaneNum; ++i)
            {
                mainTabbedPane.remove( mainTabbedPane.getTabCount() - 1 );
            }
        }

        // 开始对类的静态变量和列表做清空
        _iExtraTabbedPaneNum = 0; // 清空plot选项卡数目
        _aryListCyberDataset.clear();
        _aryListPhysicalDataset.clear();

        _aryListSimulinkPlotPanel.clear();

        _aryListPlotHoldPanel.clear(); // 清空装载Plot面板的JPanel面板数组
        _aryListPlotPanel.clear(); // 清空Plot面板数组
        _aryListVariableName.clear(); // 清空变量名列表
        _aryListVariableValue.clear(); // 清空变量值列表
        _aryListDataSets.clear(); // 清空数据集列表
        _bSimulationBegin = false;
        _iLoopCount = 0;

        _dSimulationTime = 0.0;

        _mdlFileName = "";
        _physicalPlotPanel = null;
    }


    static private void _addExchangeDatasIntoTable(ArrayList aryListCyberDataset,double last_simTime,ArrayList aryListPhysicalDataset,double dSimulationTime)
    {
        // 考虑对参数进行检查
        ArrayList <String>aryListName = (ArrayList <String>)aryListCyberDataset.get(0);
        ArrayList <Double>aryListValue = (ArrayList <Double>)aryListCyberDataset.get(1);
        // 将PT发送到matlab的数据添加到表格1中
        for(int i=0; i<aryListName.size(); ++i)
        {
            Object[] a = new Object[3];
            a[0] = aryListName.get(i);
            a[1] = last_simTime;
            a[2] = aryListValue.get(i);
            _dataExchangePanel.addOneLine(a, 1);
        }

        for(int i=0; i<aryListPhysicalDataset.size(); ++i)
        {
            aryListName = ((ArrayList <ArrayList>)(aryListPhysicalDataset.get(i))).get(0);
            aryListValue = ((ArrayList <ArrayList>)(aryListPhysicalDataset.get(i))).get(1);
            for(int j=0; j<aryListName.size(); ++j)
            {
                Object[] a = new Object[3];
                a[0] = aryListName.get(j);
                a[1] = dSimulationTime;
                a[2] = aryListValue.get(j);
                _dataExchangePanel.addOneLine(a, 2);
            }
        }


    }
    static private ArrayList _getDataSets(String str)
    {
        ArrayList aryListDataSets = new ArrayList();
        // 先进行参数
        if( str == null || str.isEmpty() )
        {
            return aryListDataSets;
        }

        aryListDataSets.clear(); // 首先清空 aryListDataSet

        int iBeginIndexMBracket = str.indexOf('['); // 查找第一个 [ 的位置
        int iEndIndexMBracket = str.indexOf(']'); // 查找第一个 ] 的位置，以和 [ 匹配
        if( iBeginIndexMBracket >= iEndIndexMBracket ) return aryListDataSets;

        while( iBeginIndexMBracket != -1 && iEndIndexMBracket != -1 )
        {
            ArrayList aryListDataSet = new ArrayList();
            ArrayList aryListPointName = new ArrayList();
            ArrayList aryListPointValue = new ArrayList();

            String strDataset = str.substring(iBeginIndexMBracket, iEndIndexMBracket + 1);

            int iBeginSBracket = strDataset.indexOf('('); // 查找这个[]数据集中第一个(
            int iEndSBracket = strDataset.indexOf(')'); // 查找这个[]数据集中第一个)
            int iComma = strDataset.indexOf(',', iBeginSBracket); // 寻找()数据对中的逗号位置
            while( iBeginSBracket != -1 && iEndSBracket != -1 && iComma != -1 )
            {
                aryListPointName.add( strDataset.substring(iBeginSBracket + 1, iComma) );
                aryListPointValue.add( Double.valueOf(strDataset.substring(iComma + 1, iEndSBracket)) );

                iBeginSBracket = strDataset.indexOf('(', iEndSBracket);
                if( iBeginSBracket == -1 )
                {
                    break; // 已经是最后一个()数据对了
                }
                iEndSBracket = strDataset.indexOf(')', iBeginSBracket); // 查找这个[]数据集中下一个)
                iComma = strDataset.indexOf(',', iBeginSBracket); // 寻找()数据对中的逗号位置
            }
            aryListDataSet.add( aryListPointName );
            aryListDataSet.add( aryListPointValue );

            aryListDataSets.add(aryListDataSet);
            iBeginIndexMBracket = str.indexOf('[', iEndIndexMBracket);
            if(iBeginIndexMBracket == -1)
            {
                break; // 已经没有 [] 表示的数据点集了
            }
            iEndIndexMBracket = str.indexOf(']', iBeginIndexMBracket);

        } // while( iBeginIndexMBracket != -1 && iEndIndexMBracket != -1 )

        // 测试语句，输出一下  aryListDataSets
        for(int i = 0; i < aryListDataSets.size(); ++i)
        {
            ArrayList aryListDataSet = (ArrayList)aryListDataSets.get(i);
            ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
            ArrayList aryListPointValue = (ArrayList)aryListDataSet.get(1);

            System.out.println("第" + i + "个数据点值为：");
            for(int j = 0; j < aryListPointName.size(); ++j)
            {
                System.out.println("\t(" + aryListPointName.get(j) + "," + aryListPointValue.get(j) + ")");
            }
        }

        return aryListDataSets;
    }

    static private boolean _validateDataSet(ArrayList aryListDataSets, boolean bFirstSocketMessage)
    {
        // ** 这个函数有时间再来完善，先期不考虑验证数据集了 **
        // 用于对获取到的数据集进行检测，数据集合理返回 true，否则返回 false
        // 参数检查
        if( aryListDataSets == null || aryListDataSets.isEmpty() )
        {
            return false;
        }

        if( bFirstSocketMessage == true )
        {
            // 目前只检查aryListDataSets中变量名称数组和数值数组的维数是否相等，不相等时出现异常，返回false

            if( aryListDataSets.size() == 1 )
            {
                ArrayList aryListPointName = (ArrayList)aryListDataSets.get(0);
                ArrayList aryListPointValue = (ArrayList)aryListDataSets.get(1);

                if( aryListPointName.size() != aryListPointValue.size() )
                {
                    // 两个数据集包含的元素个数对应不上，说明有的变量名称或者数值缺失，存在问题
                    return false;
                }
            }
            else
            {
                for(int i = 0; i < aryListDataSets.size(); ++i)
                {
                    ArrayList aryListDataSet = (ArrayList)aryListDataSets.get(i);
                    ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
                    ArrayList aryListPointValue = (ArrayList)aryListDataSet.get(1);

                    if( aryListPointName.size() != aryListPointValue.size() )
                    {
                        // 两个数据集包含的元素个数对应不上，说明有的变量名称或者数值缺失，存在问题
                        return false;
                    }
                }
            }
        }
        else //检验后续的数据集合法性
        {
            // 将 aryListDataSets 同类的静态列表 _aryListDataSets相比较，维数相同说明aryListDataSets合法。
            // 而 _aryListCyberDataset 和  _aryListPhsicalDataset 是第一次socket消息提取得到的数据集，从这个角度考虑，
            // 第一次发送的socket消息非常重要

            if( aryListDataSets.size() != _aryListDataSets.size() )
            {
                return false;
            }
            else
            {
                for(int i = 0; i < aryListDataSets.size(); ++i)
                {
                    ArrayList aryListDataSet = (ArrayList)aryListDataSets.get(i);
                    ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
                    ArrayList aryListPointValue = (ArrayList)aryListDataSet.get(1);

                    ArrayList __aryListDataSet = (ArrayList)_aryListDataSets.get(i);
                    ArrayList __aryListPointName = (ArrayList)__aryListDataSet.get(0);
                    // ArrayList __ryListPointValue = (ArrayList)__aryListDataSet.get(1);

                    if( aryListPointName.size() != aryListPointValue.size() 
                            ||  aryListPointName.size() != __aryListPointName.size())
                    {
                        // 两个数据集包含的元素个数对应不上，说明有的变量名称或者数值缺失，存在问题
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static private void _startNewSimulationInVergil(String startVergilcmd)
    {
        // Runtime run = Runtime.getRuntime(); //启动与应用程序相关的运行时对象
        try{
            Runtime.getRuntime().exec(startVergilcmd);// 启动另一个进程来执行 指定的系统 命令   
        }
        catch (Exception e){}
        //        Runtime run = Runtime.getRuntime(); //启动与应用程序相关的运行时对象
        //        // 这里已经试过，必须使用try-catch结构才行
        //        try {   
        //            Process p = run.exec(startVergilcmd);// 启动另一个进程来执行 指定的系统 命令   
        //            BufferedInputStream in = new BufferedInputStream(p.getInputStream());   
        //            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));   
        //            String lineStr;   
        //            while ((lineStr = inBr.readLine()) != null)   
        //                //获得命令执行后在控制台的输出信息   
        //                // 控制台有输出信息，那说明本机没有安装好Ptolemy II或者没有为Ptolemy II设置好环境变量
        //
        //                System.out.println(lineStr);// 打印输出信息   
        //            //检查命令是否执行失败。   
        //            if (p.waitFor() != 0) {   
        //                if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束 
        //                    JOptionPane.showMessageDialog(mainFrame, 
        //                            lineStr + "!" + "出现这种错误，有两种可能：（1）本机没有安装Ptomely II；\n;"
        //                                    + "（2）本机已正确安装了Ptolemy II，但是没有为其设置好系统环境变量", 
        //                                    "无法启动Vergil", 
        //                                    JOptionPane.ERROR_MESSAGE);
        //                System.err.println("命令执行失败!  ");   
        //            }   
        //            inBr.close();   
        //            in.close();   
        //        } 
        //        catch (Exception e) {   
        //            e.printStackTrace();   
        //        }
    }
    
}


class JAVAFileFilter extends FileFilter{
    String ext;
    public JAVAFileFilter(String s){
        ext=s;
    }
    @Override
    public boolean accept(File file) {
        if (file.isDirectory())
            return true;
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index>0 && index<fileName.length()-1){
            String extension = fileName.substring(index+1).toLowerCase();
            if (extension.equals(ext))
                return true;
        }
        return false;
    }
    public String getDescription(){
        if (ext.equals("xml"))
            return "PT xml file (*.xml)";
        
        if (ext.equals("mdl"))
            return "Matlab Simulink model file (*.mdl)";
        return "";
    }


}