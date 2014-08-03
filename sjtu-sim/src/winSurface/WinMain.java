package winSurface;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.event.*;

import ptolemy.plot.*;

import java.io.*;

public class WinMain {
    private static final long serialVersionID = 1L;
    static final int WIN_WIDTH = 900;
    static final int WIN_HEIGHT = 450;
    JFrame mainFrame = null; // 程序主窗口变量
    // JRootPane rootPane = null; // 程序窗口要加载的面板，是一个容器，要向里面添加可视化的组件
    // JLayeredPane layeredPane = null;
    JTabbedPane mainTabbedPane = null; // JRootPane中内容面板要加载的选项卡面板
    JMenuBar menuBar = null;
    JMenu menuFile = null;
    JMenu menuEdit = null;
    JMenu menuView = null;
    JMenu menuTool = null;
    JMenu menuHelp = null;
    
    
    public WinMain()
    {
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
        
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuView);
        menuBar.add(menuTool);
        menuBar.add(menuHelp);
        
        JMenuItem menuItemOpenInFile = new JMenuItem("打开(O)");
        // 为“打开”这个菜单项 设定 快捷键 --- Ctrl + O
        menuItemOpenInFile.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemOpenInFile); // 将这个菜单项添加到“文件”菜单下
        
        JMenuItem menuItemOpenMATALBInFile = new JMenuItem("启动MATALB");
        menuFile.add(menuItemOpenMATALBInFile);
        
        JMenuItem menuItemOpenPTInFile = new JMenuItem("启动 Vergil");
        menuFile.add(menuItemOpenPTInFile);
        
        
        
        mainTabbedPane = new JTabbedPane(); // 创建窗口加载的选项卡面板
        mainFrame.setContentPane(mainTabbedPane); // 设置窗口的显示面板为 上面的选项卡面板
        mainTabbedPane.setVisible(true); // 设置这个选项卡面板可见
       
        // 添加选项卡容器，并且设置每个选项卡的标签以及其是否可用
        JPanel panel0 = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        JPanel panel5 = new JPanel();
        JPanel panelPlot = new JPanel();
        
        mainTabbedPane.addTab("开始",panel0); // 将“开始”面板 panel0 作为第一个选项卡
        mainTabbedPane.setEnabledAt(0,true);
        
        mainTabbedPane.addTab("电压", panel1);
        mainTabbedPane.setEnabledAt(1, true);
        
        mainTabbedPane.addTab("总功率", panel2);
        mainTabbedPane.setEnabledAt(2, true);
        
        mainTabbedPane.addTab("光伏电流", panel3);
        mainTabbedPane.setEnabledAt(3, true);
        
        mainTabbedPane.addTab("光伏功率", panel4);
        mainTabbedPane.setEnabledAt(4, true);
        
        mainTabbedPane.addTab("储能功率", panel5);
        mainTabbedPane.setEnabledAt(5, true);
        
        mainTabbedPane.addTab("画图测试面板", panelPlot);
        mainTabbedPane.setEnabledAt(6,true);
        
        Plot testPlot = new Plot();
        panelPlot.add(testPlot);
        // testPlot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT); // 没有效果
        // testPlot.setPlotRectangle(new Rectangle(0,0,WIN_WIDTH,WIN_HEIGHT) ); // 有效
        testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // 有效，以像素为单位，确定画图区域的范围
        testPlot.setButtons(true);// 有效，非常重要
        testPlot.addPoint(0, 0.4, 0.5, true); // 无效，有待研究
        testPlot.addPoint(0, 0.9, 10, true);
        testPlot.addPoint(0, 1.0, 15, true);
        testPlot.fillPlot();
        
        //testPlot.samplePlot();
        
        // 设置选项卡面板的大小及其选项卡的位置方向
        mainTabbedPane.setPreferredSize(new Dimension(500,200));
        mainTabbedPane.setTabPlacement(JTabbedPane.TOP);
        // 设置选项卡在容器内的显示形式
        mainTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // mainFrame.pack(); // 让主窗口适应组件的大小
        
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
                        // 将这个文件传给 Vergil，开启仿真
                        //String startVergilcmd = "vergil -run E:\\PT_workspace\\pvbattery_50-60_org.xml";
                        String startVergilcmd = "vergil -run " + file.getAbsolutePath();
                        
                        Runtime run = Runtime.getRuntime(); //启动与应用程序相关的运行时对象   
                        try {   
                            Process p = run.exec(startVergilcmd);// 启动另一个进程来执行 指定的系统 命令   
                            BufferedInputStream in = new BufferedInputStream(p.getInputStream());   
                            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));   
                            String lineStr;   
                            while ((lineStr = inBr.readLine()) != null)   
                                //获得命令执行后在控制台的输出信息   
                                System.out.println(lineStr);// 打印输出信息   
                                //检查命令是否执行失败。   
                                if (p.waitFor() != 0) {   
                                    if (p.exitValue() == 1)//p.exitValue()==0表示正常结束，1：非正常结束   
                                       System.err.println("命令执行失败!  ");   
                                }   
                                inBr.close();   
                                in.close();   
                        } 
                        catch (Exception e) {   
                            e.printStackTrace();   
                        }
                    }                    
                    // return;
                } // if( iResult == JFileChooser.APPROVE_OPTION )
                else
                {
                    // 选中的并不是 xml 文件，弹出对话框提示用户
                }
                
            }
        }); // menuItemOpenInFile.addActionListener(new ActionListener()
        
        
    
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new WinMain(); // 创建出主窗口
    }    

}
