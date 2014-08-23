package winSurface;
import javax.swing.*;

import java.util.regex.Pattern;
import java.awt.*;
import java.awt.event.*;

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
    
    static int _iPlotNumber = 0; // 用来记录当前仿真程序共产生了几个Plot选项卡面板
    
    static ArrayList _aryListTabbedPanel = null; // 用来存放选项卡的动态数组
    static ArrayList _aryListPlotPanel = null; // 用来存放每个选项卡中的Plot画图面板
    static boolean _bSimulationBegin = false; // 用来指示当前时间，界面是否有仿真在运行
    
    static int _iLoopCount = 0;
    
    // add by Bruse, 2014-8-23
    static double _dSimulationTime = 0.0; // 记录当前的仿真时间，用于后面 _handleNextSocket函数对socket消息做过滤检查
    
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
	
    public WinMain()
    {
        // 类成员变量的初始化
        _aryListVariableName = new ArrayList();
        _aryListVariableValue = new ArrayList();
        
        _aryListDataSets = new ArrayList();
        
        _iPlotNumber = 0;
        
        _aryListTabbedPanel = new ArrayList();
        _aryListPlotPanel = new ArrayList();
        _bSimulationBegin = false;
        
        _iLoopCount = 0;
        
        _dSimulationTime = 0.0; // add by Bruse, 2014-8-23


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

        JMenuItem menuItemGetHelpInHelp = new JMenuItem("使用说明");
        menuItemGetHelpInHelp.addActionListener(this);
        menuHelp.add(menuItemGetHelpInHelp);
        menuHelp.addSeparator();

        JMenuItem menuItemInfoInHelp = new JMenuItem("关于SimJ&M");
        menuItemInfoInHelp.addActionListener(this);
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

                _startNewSimulationInVergil(startVergilcmd);
                
                
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
     * //------不是很理解为何所有方法都要设成了静态方法？			-ZH
     *******************   private methods *************************/
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
        boolean b = Pattern.matches(
                "^<[0-9]+\\-[0-9]+\\.?[0-9]*(\\-\\[\\(.+,.+\\)(\\-\\(.+,.+\\))*\\])+>$",
                socketMessage);
        if(b == true) // 消息格式正确，可以进行下一步的处理工作
        {
            int iLoopCount = _getLoopCount(socketMessage);
            if( iLoopCount > 0 ) // socket消息的数据集循环次数合法
            {
                if( iLoopCount == 1 && _bSimulationBegin == false) 
                {
                    // 当次仿真过程发送的第一条socket消息，而且没有仿真程序在执行或者可以开启新的仿真程序
                    _handleFirstSocket(socketMessage);
                    
                }
                else
                {
                    // 处理后续的socket消息，动态解析数据点并添加到plot面板中去即可
                    _handleNextSocket(socketMessage);
                }
            }
            else // socket消息的数据集循环次数不合法
            {
                //数据出现异常，可以考虑以生成日志文件的形式来排查错误
                return;
            }

        } //  if( socketMessage.endsWith(">") && socketMessage.startsWith("<") )
    }
    
    static private void _handleFirstSocket( String socketMessage)
    {
        // 当次仿真过程发送的第一条socket消息，而且没有仿真程序在执行或者可以开启新的仿真程序
        
        // 先进行参数检查
        if(socketMessage.isEmpty())
        {
            return;
        }
        
        // 下面开始做一些初始化的工作
        _clearOldSimulation(); // 清除旧的仿真信息
     
        // 获取socket消息中包含的数据点名称和数值，
        // 这里 保留 _aryListVariableName 和 _aryListVariableValue 两个成员变量，因为考虑到在软件开发后期，
        // 可以用来对接受到的数据的正确性、合理性进行 检查校验
        _aryListVariableName = _getPointNameAryList(socketMessage);  // 获取消息中包含的所有变量名称
        _aryListVariableValue = _getPointValueAryList(socketMessage); // 获取消息中包含的所有变量的数值
        
        
        // 获取socket消息中的数据集
        ArrayList aryListDataSets = _getDataSets(socketMessage);
        
        if( aryListDataSets.isEmpty() || aryListDataSets.isEmpty() )
        {
            // 弹出错误对话框
            JOptionPane.showInternalMessageDialog(mainFrame, 
                    "新的仿真程序没有检测到变量输出，请检查您的仿真模型！", 
                    "错误！无仿真变量可供监测！", JOptionPane.ERROR_MESSAGE);
            return; // 结束这次消息处理
        }
        else // if( _aryListVariableName.isEmpty() || _aryListVariableName == null)
        {
            // 检测一下获取的数据集是否正确合理
            if( !_validateDataSet(aryListDataSets, true) )
            {
                // 数据集不合法，退出这次消息处理
                return;
            }

            // 将第一次socket消息数据集保存到类的静态列表中
            _aryListDataSets = aryListDataSets;
            
            double dSimulationTime = _getSimulationTime(socketMessage);

            // 消息正确，开始针对每个变量数据集，添加好plot画图选项卡
            for(int i = 0; i < aryListDataSets.size(); ++i)
            {
                
                // 创建出一个Plot面板，以及其依附的JPanel
                JPanel panelPlot = new JPanel();
                panelPlot.setLayout(new BorderLayout());
                Plot testPlot = new Plot();
                
                // 将创建好的 panelPlot 和 testPlot 添加到 本类的静态列表中
                _aryListTabbedPanel.add(panelPlot);
                _aryListPlotPanel.add(testPlot);
                
                ((JPanel)_aryListTabbedPanel.get(i)).add((Plot)_aryListPlotPanel.get(i)
                        , BorderLayout.CENTER);
                // testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // 有效，以像素为单位，确定画图区域的范围
                ((Plot)_aryListPlotPanel.get(i)).setButtons(true);// 有效，非常重要
                
                
                
                String tabTitle = ""; // 由变量名称组成的选项卡标题
                Vector vcCaption = new Vector(); // 用于存放每个变量的名称，在设置每条曲线的标示时使用
                
                ArrayList aryListDataSet = (ArrayList)_aryListDataSets.get(i);
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
                
                //((Plot)_aryListPlotPanel.get(i)).setCaptions(vcCaption); // 运行结果显示，
                // 这个函数只是在面板的下部添加了几行标题，并不是针对曲线进行添加，有待改进
                
                
                ((Plot)_aryListPlotPanel.get(i)).fillPlot(); // 重绘图形
                
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
                mainTabbedPane.addTab( tabTitle , (JPanel)_aryListTabbedPanel.get(i));
                _iPlotNumber++;
                mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);
            }
            
            // 更新当前类的静态变量，循环次数变量更新为1
            _iLoopCount = 1;
            
            // 设置已存在仿真进程标志
            _bSimulationBegin = true;
            
            // 更新仿真时间， add by Bruse, 2014-8-23
            _dSimulationTime = dSimulationTime;
        }
    }
    
    static private void _handleNextSocket(String socketMessage)
    {
        // 先进行参数检查
        if(socketMessage.isEmpty())
        {
            return;
        }
        
        // 处理后续的socket消息，动态解析数据点并添加到plot面板中去即可
        double dSimulationTime = _getSimulationTime(socketMessage);
        
        if( dSimulationTime > _dSimulationTime ) // 本次socket消息的仿真时间大于当前静态变量记录的时间，是这次仿真的后续循环结果消息
        {
            // 获取这次socket消息的数据集
            ArrayList aryListDataSets = _getDataSets(socketMessage);
            
            // 检查数据的合法性，合理时才处理这个消息
            if( _validateDataSet(aryListDataSets, false) )
            {
                // 数据合理，开始向每个Plot面板中添加数据点
                
                for(int i = 0; i < aryListDataSets.size(); ++i)
                {
                    ArrayList aryListDataSet = (ArrayList)aryListDataSets.get(i);
                    ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
                    ArrayList<Double> aryListPointValue = (ArrayList<Double>)aryListDataSet.get(1);
                    
                    for(int k = 0; k < aryListPointName.size(); ++k)
                    {
                        ((Plot)_aryListPlotPanel.get(i)).addPoint(k, 
                                dSimulationTime, 
                                aryListPointValue.get(k), 
                                true);
                        
                        //
                    }
                    
                   ((Plot)_aryListPlotPanel.get(i)).fillPlot(); // 重绘图形
                }
                
                // 更新当前类的静态循环次数变量
                _iLoopCount = _getLoopCount(socketMessage);
                
                // 更新当前类的静态存放的仿真时间
                _dSimulationTime = dSimulationTime;
            }          
        }             
    }
    
    static private void _clearOldSimulation()
    {
        if( _iPlotNumber > 0 ) // 说明这次仿真之前，已经存在仿真进程,清空所有仿真变量的Plot面板
        {
            // 从mainTabbedPane中逆序删除所有Plot选项卡面板
            for(int i = 0; i < _iPlotNumber; ++i)
            {
                mainTabbedPane.remove( mainTabbedPane.getTabCount() - 1 );
                
            }
        }
        
        // 开始对类的静态变量和列表做清空
        _iPlotNumber = 0; // 清空plot选项卡数目
        _aryListTabbedPanel.clear(); // 清空装在Plot面板的JPanel面板数组
        _aryListPlotPanel.clear(); // 清空Plot面板数组
        _aryListVariableName.clear(); // 清空变量名列表
        _aryListVariableValue.clear(); // 清空变量值列表
        _aryListDataSets.clear(); // 清空数据集列表
        _bSimulationBegin = false;
        _iLoopCount = 0;
        
        _dSimulationTime = 0.0;
    }
    

    static private int _getLoopCount(String str)
    {
        // 先进行参数检查
        if( str == null || str.isEmpty() )
        {
            return -1;
        }
        
        // 开始提取仿真的循环次数，即iteration变量的值
        int i = str.indexOf('-', 0);
        String loopCount = str.substring(1, i);
        
        int iLoopCount = Integer.valueOf(loopCount);
        
        // 测试性输出语句
        System.out.println("loop count is :" + iLoopCount);
                
        return iLoopCount;
    }
    static private double _getSimulationTime(String str)
    {
        // 先进行参数检查
        if( str == null || str.isEmpty() )
        {
            return -1;
        }
        int beginIndex = str.indexOf('-') + 1;
        int endIndex = str.indexOf('-', beginIndex);
        if(endIndex == beginIndex) // 表示仿真时间的字符串为空，有异常
        {
            // 这里有异常，可以考虑生成日志文件
            return 0.0;
        }
        String strSimuTime = str.substring(beginIndex, endIndex);
        double dSimuTime = Double.valueOf(strSimuTime);
        
        // 测试性输出语句
        System.out.println("simulation time is : " + dSimuTime);
        
        return dSimuTime;
    }
    
    static private ArrayList _getPointNameAryList(String str)
    {
        ArrayList aryListVariableName = new ArrayList();
        // 先进行参数检查
        if( str == null || str.isEmpty() )
        {
            return aryListVariableName;
        }
        // 首先清空 aryListVariableName
        aryListVariableName.clear();
        int iBeginIndex = str.indexOf('(') + 1; // 找到第一个数据点 名称开始的位置
        int iEndIndex = 0;
        
        while( (iEndIndex = str.indexOf(',', iBeginIndex)) != -1)
        {
            String strVariableName = str.substring(iBeginIndex, iEndIndex); // 获取当前点变量名称
            aryListVariableName.add(strVariableName); // 将这个变量名称放入 aryListVariableName中
            
            // 获取下一个点 变量名称的开始位置
            iBeginIndex = str.indexOf('(', iEndIndex);
            if( iBeginIndex == -1) // 已经访问了最后一个点，可以退出循环
            {
                break;
            }
            iBeginIndex = iBeginIndex + 1;
        }
        
        // 测试输出语句
        for(int i = 0; i < aryListVariableName.size(); ++i)
        {
            System.out.println("第" + i + "个变量名称： " + aryListVariableName.get(i));
        }
        return aryListVariableName;
    }
    
    static private ArrayList _getPointValueAryList(String str)
    {
        ArrayList aryListVariableValue = new ArrayList();
        // 先进行参数检查
        if( str == null || str.isEmpty() )
        {
            return aryListVariableValue;
        }
        
        // 首先清空 aryListVariableValue
        aryListVariableValue.clear();
        
        int iBeginIndex = str.indexOf(',') + 1; // 寻找第一个变量值开始的地方
        int iEndIndex = 0;
        String strVariableValue = "";
        double dVariableValue = 0.0;
        while( ( iEndIndex = str.indexOf(')', iBeginIndex) ) != -1 )
        {
            strVariableValue = str.substring(iBeginIndex, iEndIndex);
            dVariableValue = Double.valueOf(strVariableValue);
            aryListVariableValue.add(dVariableValue);
            iBeginIndex = str.indexOf(',', iEndIndex);
            if( iBeginIndex == -1 )
            {
                break;
            }
            iBeginIndex = iBeginIndex + 1;
        }
        // 测试输出语句
        for(int i = 0; i < aryListVariableValue.size(); ++i)
        {
            System.out.println("第" + i + "个变量名称： " + aryListVariableValue.get(i));
        }
        return aryListVariableValue;
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
        // 用于对获取到的数据集进行检测，数据集合理返回 true，否则返回 false
        // 参数检查
        if( aryListDataSets == null || aryListDataSets.isEmpty() )
        {
            return false;
        }
        
        if( bFirstSocketMessage == true )
        {
            // 目前只检查aryListDataSets每个元素中两个数组的维数是否相等，不相等时出现异常，返回false

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
        else //检验后续的数据集合法性
        {
            // 将 aryListDataSets 同类的静态列表 _aryListDataSets相比较，维数相同说明aryListDataSets合法。
            // 而 _aryListDataSets是第一次socket消息提取得到的数据集，从这个角度考虑，
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
		return "";
	}
	
	
}
