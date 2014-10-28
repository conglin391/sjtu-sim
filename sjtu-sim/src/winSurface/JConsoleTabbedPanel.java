package winSurface;

import java.awt.Choice;
import java.awt.LayoutManager;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;


import javax.swing.JPanel;

public class JConsoleTabbedPanel extends JPanel implements ActionListener{
    /*
     * 这个面板对应“控制台”选项卡，在这里创建和布控各个按钮和输入文本框等，对按钮的响应以及仿真进度条的更新也放在这里实现
     * 对于仿真进度条的更新，给出一个接口，函数为：
     * public void setProcess(double pro)；
     * 另外，给出一个初始化面板中各组件外观（状体）的接口  public void initComponents() ，用于软件使用者在一个仿真进行时
     * 强制性地开始新的仿真，这时，在主界面的 _clearOldSimulation()中，
     * 需要调用这个接口来重置进度条的状态以及“启动”、“暂停”、“停止”按钮的状态。
     * */
    private JFrame _mainFrame; // 用于存放界面的主窗口指针，以便在事件响应函数中弹出对话框的代码处传入
    private WinMain _pWinMain; // 界面主类的指针
    private double settedSimTime = 1000.0;

    private JTextField SimTime;
    private Choice choiceMatlabmdlPath, choicePTmdlPath,choicePTXMLPath;
    private JTextField progressNum;
    private JProgressBar progressBar;
    private JButton button_start;
    private JButton button_pause;
    private JButton button_stop;

    /**
     * @wbp.parser.constructor
     */
    public JConsoleTabbedPanel(JFrame mainFrame, WinMain pWinMain) {
        // TODO Auto-generated constructor stub

        // 类成员变量赋初值
        _mainFrame = mainFrame;
        _pWinMain = pWinMain;

        settedSimTime = 1000.0;

        // 开始创建各个按钮和文本框
        /**
         * 下面代码是“开始”选项卡界面的各个组件 add by -ZH
         * */


        setLayout(null);

        JPanel panel_sim = new JPanel();
        panel_sim.setToolTipText("");
        panel_sim.setBounds(85, 28, 696, 75);
        add(panel_sim);
        panel_sim.setLayout(null);

        button_start = new JButton("");
        button_start.setEnabled(false);
        Color cc= new Color(240,240,240);
        button_start.setForeground(Color.BLACK);
        button_start.setIcon(new ImageIcon(JConsoleTabbedPanel.class.getResource("/winSurface/UIResource/start.jpg")));
        button_start.setIconTextGap(0);

        button_start.setBounds(77, 10, 30, 27); // 30*27，这个“启动”图片的像素尺寸
        panel_sim.add(button_start);
        button_start.addActionListener(this);

        button_pause = new JButton("");
        button_pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        button_pause.setIcon(new ImageIcon(WinMain.class
                .getResource("/winSurface/UIResource/pause.jpg")));
        button_pause.setBounds(132, 10, 30, 27); // 30*27，这是“暂停”按钮的像素尺寸
        button_pause.setEnabled(false); // 按钮刚被加载到界面上时，“暂停”不可用，因为还没有仿真在运行
        button_pause.setVisible(false); // ***先隐藏掉这个按钮，这个 暂停\继续 功能目前没有可行的解决方案，捉急！******
        panel_sim.add(button_pause);
        button_pause.addActionListener(this);

        button_stop = new JButton("");
        button_stop.setIcon(new ImageIcon(WinMain.class
                .getResource("/winSurface/UIResource/stop.jpg")));
        button_stop.setBounds(184, 10, 29, 27); // 29*27,这是“停止”按钮的像素尺寸
        button_stop.setEnabled(false); // 刚加载时，“停止”按钮不可用
        panel_sim.add(button_stop);
        button_stop.addActionListener(this);

        SimTime = new JTextField();
        SimTime.setBounds(240, 11, 80, 21);
        SimTime.setText("1000");
        // 对这个确定“仿真总时间”的文本框添加事件监听
        SimTime.addFocusListener(new java.awt.event.FocusAdapter(){

            public void focusLost(FocusEvent e) {
                double simTime = Double.valueOf( ((JTextField)e.getSource()).getText() );
                if( simTime < 0.0 )
                {
                    // 设置的仿真时间不合法
                    JOptionPane.showInternalMessageDialog(
                            (((e.getComponent()).getParent()).getParent()).getParent(), 
                            "总仿真时间必须为正值！", 
                            "错误！您设置的总仿真时间不合法，请重新输入！", JOptionPane.ERROR_MESSAGE);
                    ((JTextField)e.getSource()).requestFocus();
                    return; // 结束这次消息处理
                }
                else
                {
                    settedSimTime = simTime;
                }

            }
        }
                );

        panel_sim.add(SimTime);
        SimTime.setColumns(10);

        progressBar = new JProgressBar();

        progressBar.setBounds(158, 43, 487, 22);
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setString("0%");

        panel_sim.add(progressBar);

        progressNum = new JTextField();
        progressNum.setEditable(false);
        progressNum.setText("0.0%");
        progressNum.setBounds(100, 43, 48, 21);
        panel_sim.add(progressNum);
        progressNum.setColumns(10);
        // String progressnow = progressNum.getText().trim().replace("%", "");
        progressBar.setValue( 0 ); // 初始进度为0 

        choicePTXMLPath = new Choice();
        choicePTXMLPath.setBounds(335, 12, 240, 21);
        panel_sim.add(choicePTXMLPath);

        JButton btn_choicePTXML = new JButton("打开PT模型");
        btn_choicePTXML.setBounds(600, 10, 34, 23);
        btn_choicePTXML.addActionListener(this);
        panel_sim.add(btn_choicePTXML);

        JTextPane textPane_3 = new JTextPane();
        textPane_3.setText("仿真进度");
        textPane_3.setEditable(false);
        textPane_3.setBackground(SystemColor.menu);
        textPane_3.setBounds(27, 43, 63, 21);
        panel_sim.add(textPane_3);

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 113, 879, 2);
        add(separator);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(0, 179, 879, 2);
        add(separator_1);

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(UIManager.getColor("Button.background"));
        textPane.setText("仿真");
        textPane.setBounds(10, 10, 49, 21);
        add(textPane);

        JTextPane textPane_1 = new JTextPane();
        textPane_1.setEditable(false);
        textPane_1.setText("建模");
        textPane_1.setBackground(SystemColor.menu);
        textPane_1.setBounds(10, 129, 49, 21);
        add(textPane_1);

        JTextPane textPane_2 = new JTextPane();
        textPane_2.setEditable(false);
        textPane_2.setText("交互");
        textPane_2.setBackground(SystemColor.menu);
        textPane_2.setBounds(10, 191, 49, 21);
        add(textPane_2);

        JPanel panel_interaction = new JPanel();
        panel_interaction.setBounds(55, 203, 764, 138);
        add(panel_interaction);
        panel_interaction.setLayout(null);

        MFunctionPanel _mFunctionPanel = new MFunctionPanel();

        panel_interaction.add(_mFunctionPanel);

        JButton btn_openPT = new JButton("建立离散控制模型");
        btn_openPT.setBounds(69, 139, 136, 23);
        btn_openPT.addActionListener(this);
        add(btn_openPT);

        choicePTmdlPath = new Choice();
        choicePTmdlPath.addItem("Create New model");
        choicePTmdlPath.setBounds(215, 139, 172, 21);
        add(choicePTmdlPath);

        JButton btn_choicePTmdl = new JButton("选择PT模型");
        btn_choicePTmdl.addActionListener(this);
        btn_choicePTmdl.setBounds(393, 139, 32, 23);
        add(btn_choicePTmdl);

        JButton btn_openMatlab = new JButton("建立连续动态模型");
        btn_openMatlab.setBounds(469, 139, 136, 23);
        btn_openMatlab.addActionListener(this);
        add(btn_openMatlab);

        choiceMatlabmdlPath = new Choice();
        choiceMatlabmdlPath.setBounds(609, 141, 172, 21);
        choiceMatlabmdlPath.addItem("Create New model");
        add(choiceMatlabmdlPath);

        JButton btn_choiceMatlabmdl = new JButton("选择Matlab模型");

        btn_choiceMatlabmdl.setBounds(787, 139, 32, 23);
        add(btn_choiceMatlabmdl);
        btn_choiceMatlabmdl.addActionListener(this);

        /**
         * 上面代码是“开始”选项卡界面的各个组件 add by -ZH
         * */


        // 下面代码纯属临时测试
        /*addOneXMLfilePath("哈哈");
        addOneXMLfilePath("hehlo");*/

    }

    public void actionPerformed(ActionEvent e) {
        // 在这里实现“控制面板”中各个按钮的响应事件
        String cmd = e.getActionCommand();
        if (cmd.equals("选择Matlab模型")) {
            String filename = choiceMatlabMdl("mdl");
            if (filename.endsWith(".mdl")) {
                choiceMatlabmdlPath.addItem(filename);
                choiceMatlabmdlPath.select(filename);
            } else {
                JOptionPane.showMessageDialog(_mainFrame, "不是 .mdl文件，请重新选择！",
                        "错误的文件类型", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (cmd.equals("选择PT模型")) {
            String filename = choiceMatlabMdl("xml");
            if (filename.endsWith(".xml")) {
                choicePTmdlPath.addItem(filename);
                choicePTmdlPath.select(filename);
            } else {
                JOptionPane.showMessageDialog(_mainFrame, "不是 .xml文件，请重新选择！",
                        "错误的文件类型", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (cmd.equals("建立离散控制模型")) {
            try {
                String file = choicePTmdlPath.getSelectedItem();
                if (file.equals("Create New model")) {
                    Runtime.getRuntime().exec("vergil");
                } else {
                    //System.out.print("vergil " + file);
                    Runtime.getRuntime().exec("vergil +\"" + file+"\"");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        else if (cmd.equals("建立连续动态模型")) {
            try {
                String file = choiceMatlabmdlPath.getSelectedItem();
                if (file.equals("Create New model")) {
                    Runtime.getRuntime().exec("matlab -r simulink");
                } else {
                    Runtime.getRuntime().exec("matlab -r uiopen(\'" + file+"\'");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        else if (cmd.equals("打开PT模型")) {
            String filename = choiceMatlabMdl("xml");
            if (filename.endsWith(".xml")) {
                choicePTXMLPath.addItem(filename);
                choicePTXMLPath.select(filename);

                // xml文件有效，此时让“启动仿真”按钮变成有效, add by bruse, 2014-10-22
                button_start.setEnabled(true);

            } else {
                JOptionPane.showMessageDialog(_mainFrame, "不是 .xml文件，请重新选择！",
                        "错误的文件类型", JOptionPane.ERROR_MESSAGE);
            }
        }

        //if (cmd.equals("启动仿真"))
        else if (e.getSource()==button_start)
        {
            // if( choicePTXMLPath.countItems() == 0) return;
            String fileName = choicePTXMLPath.getItem(0);
            if( fileName.endsWith("xml") ) // 选中的确实是xml文件
            {
                // 将选中的xml文件传给 Vergil，开启仿真
                //针对路径空格问题，增加双引号  -ZH
                String startVergilcmd = "vergil -run \"" + fileName +"\"";

                // 检查是否已经有仿真在进行
                if( _pWinMain.getSimulationStatus() == true )
                {
                    // 弹出对话框提示用户，是否结束当前仿真，并开启新的仿真
                    int iStartNewSim = JOptionPane.showConfirmDialog(_mainFrame, 
                            "目前系统已经有仿真程序在运行，是否结束当前仿真程序并开启新的仿真", 
                            "是否结束当前仿真程序", 
                            JOptionPane.YES_NO_OPTION);
                    if( iStartNewSim == JOptionPane.YES_OPTION )
                    {
                        try {

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



                    }
                    else
                    {
                        return; // 事件处理函数结束，不会启动新的仿真
                    }                            
                } //  if( _pWinMain.getSimulationStatus() == true )

                // 没有旧的仿真信息，一切正常，可以启动新的仿真
                try
                {
                    Runtime.getRuntime().exec(startVergilcmd);

                    _pWinMain.setSimulationStatus(false); // 设置界面并没有真正开始运行仿真

                    button_start.setEnabled(false); // “启动仿真”按钮不可用
                    button_stop.setEnabled(true); // “结束仿真”按钮可用

                    SimTime.setEditable(false); // 仿真开始后，不可以再输入仿真时间
                }
                catch(Exception e1)
                {}              

            } // if( fileName.endsWith("xml") ) // 选中的确实是xml文件
        }
        //if (cmd.equals("停止仿真"))
        else if (e.getSource()==button_stop)
        {
            // 停止仿真进程，目前考虑直接用 windows的taskkill命令强制关闭PT和matlab
            if( _pWinMain.getSimulationStatus() == true ) // 确实有仿真在运行
            {
                int iStopSimulation = JOptionPane.showConfirmDialog(_mainFrame, 
                        "目前系统仿真程序正在运行，点击“是”将关闭所有的仿真进程，“否”则忽略这次操作", 
                        "是否结束当前仿真程序", 
                        JOptionPane.YES_NO_OPTION);
                if( iStopSimulation != JOptionPane.YES_OPTION ) // 不想真正关闭
                {
                    return;
                }
            }
            else
            {
                // 没有仿真在运行，但“停止仿真”按钮可用
                int iStopSimulation = JOptionPane.showConfirmDialog(_mainFrame, 
                        "目前系统的仿真程序并没有正真启动，点击“是”将关闭所有的仿真进程，“否”则忽略这次操作", 
                        "是否结束已开启的仿真程序", 
                        JOptionPane.YES_NO_OPTION);
                if( iStopSimulation != JOptionPane.YES_OPTION ) // 不想真正关闭
                {
                    return;
                }              
            }

            // 用taskkill杀死进程
            try {

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

            // 将软件的仿真运行标志重置为false，表示可以开启新的仿真
            _pWinMain.setSimulationStatus(false);

            initButtonAndProgress(); // 对按钮和进度条组件做初始化
        }
        else
        {
            return;
        }
    }

    private String choiceMatlabMdl(String type) {
        // 创建一个文件选择对话框，供用户选择 文件
        JFileChooser fileChooser = new JFileChooser();
        // 加入文件类型选择 -ZH
        fileChooser.addChoosableFileFilter(new JAVAFileFilter(type));
        File file = null;
        int iResult = 0;
        fileChooser.setApproveButtonText("确定");
        fileChooser.setDialogTitle("打开文件");
        iResult = fileChooser.showOpenDialog(_mainFrame);
        /*
         * 当用户有选中文件 并且按下"确定"按钮后，就可以通过 getSelectedFile() 方法取得文件对象
         */
        if (iResult == JFileChooser.APPROVE_OPTION) {
            // 用户按下的“确定”按钮
            file = fileChooser.getSelectedFile();
            System.out.println("选择的文件名：" + file.getName());
            System.out.println("选择的文件名：" + file.getAbsolutePath());

            String fileName = file.getName();
            return file.getAbsolutePath();

        }
        return null;

    }

    public void setProcess(double currentTime) {
        // double settedSimTime = Double.valueOf( SimTime.getText() );
        if( currentTime > settedSimTime )
        {
            // 传入的仿真时间超过了用户设定的时间，就不用再跟新进度显示了，没有意义
            return;
        }
        else
        {
            System.out.println(settedSimTime);
            
            double pro = currentTime / settedSimTime * 100;
            String value_pro = String.valueOf(pro);
            
            System.out.println(value_pro);
            
            // value_pro = value_pro.substring(0, value_pro.indexOf('.')+3);// 只保留小数点后两位
            progressNum.setText( value_pro + "%" );
            progressBar.setValue( (int)pro );
            progressBar.setString(value_pro + "%"); 
        }

    }

    public void initComponents() {

        // “开始”按钮不可用
        button_start.setEnabled(false);

        // “停止”按钮不可用
        button_stop.setEnabled(false);

        // “暂停/继续”按钮不可用
        button_pause.setEnabled(false);

        // “仿真时间”的输入文本框可用
        SimTime.setEditable(true);

        // “仿真进度”文本框的值置为 "0.0%"
        progressNum.setText("0.0%");

        // “仿真进度条”置为0
        progressBar.setValue(0);

        // 其他的仿真初始工作，主要修改各个组件的状态，回到软件初始的状态
        choicePTXMLPath.removeAll();
        choiceMatlabmdlPath.removeAll();
        choicePTmdlPath.removeAll();

    }

    public void initButtonAndProgress()
    {
        // “开始”按钮可用与否取决于 choicePTXMLPath中的项目，如果有可用的项目，则“启动”按钮可用
        if( choicePTXMLPath.getItemCount() > 0 )
        {
            button_start.setEnabled(true);
        }
        else
        {
            button_start.setEnabled(false);
        }
        // “停止”按钮不可用
        button_stop.setEnabled(false);

        // “暂停/继续”按钮不可用
        button_pause.setEnabled(false);

        // “仿真时间”的输入文本框可用
        SimTime.setEditable(true);

        // “仿真进度”文本框的值置为 "0.0%"
        progressNum.setText("0.0%");

        // “仿真进度条”置为0
        progressBar.setValue(0);
    }

    public void setStopAndPauseButtonEnabled(boolean b)
    {
        button_stop.setEnabled(b);
        button_pause.setEnabled(b);
    }
    
    public void setSimTimeEditable(boolean b)
    {
        SimTime.setEditable(b);
    }
    
    public void resetProgress()
    {
        progressNum.setText("0.0%");
        progressBar.setValue(0);
    }

    public void addOneXMLfilePath(String XMLfilePath)
    {
        // 参数检查
        if( XMLfilePath == null || XMLfilePath.isEmpty() )
        {
            // 参数为空，发生错误，应该生成日志文件

            return;
        }

        // 检查 待添加的字符串，choicePTXMLPath总是否已经存在
        for(int i=0; i < choicePTXMLPath.getItemCount(); ++i)
        {
            if(choicePTXMLPath.getItem(i).equals(XMLfilePath))
            {
                return;
            }
        }
        choicePTXMLPath.add(XMLfilePath);
    }

    public JConsoleTabbedPanel(LayoutManager arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public JConsoleTabbedPanel(boolean arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public JConsoleTabbedPanel(LayoutManager arg0, boolean arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}