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
import java.util.*; // java���ݽṹ�İ����õ����е� ArrayList

//�Դ���ṹ���˵�����������ť�������ϵ�һ�� ��������Ķ����޸�,�����¼����ϵ�actionPerformed�� -ZH
/**
 * @author ZH
 *
 */
public class WinMain implements ActionListener{

    /****************************************************************
     *******************   private members *************************/

    private static final long serialVersionID = 1L;
    private int WIN_WIDTH; // ���ڵĳ�
    private int WIN_HEIGHT; // ���ڵĿ�
    static JFrame mainFrame = null; // ���������ڱ���
    
    static JConsoleTabbedPanel _consoleTabbedPanel = null; // add by bruse 2014-10-21

    static JPhysicalDataPlotPanel _physicalPlotPanel = null; // add by Bruse 2014-10-12

    static JDataShowTablePanel _dataExchangePanel = null; // add by Bruse 2014-10-12

    static JTabbedPane mainTabbedPane = null; // JRootPane���������Ҫ���ص�ѡ����
    JMenuBar menuBar = null;
    JMenu menuFile = null;
    JMenu menuEdit = null;
    JMenu menuView = null;
    JMenu menuTool = null;
    JMenu menuHelp = null;

    // ���￼�Ǻ�������չ������ʱ����������Ա��������
    static ArrayList _aryListVariableName = null; // ������¼һ�η�����̣��漰����ȫ����������
    static ArrayList _aryListVariableValue = null; // ������¼һ�η�����̣��漰�������б�����ֵ

    static ArrayList _aryListDataSets = null; // ����������ݵ�
    // ��� _aryListDataSets �������Ը��ӣ���������ÿ��Ԫ�ض���ArrayList���ͣ�_aryListDataSets�е���ArrayListԪ��
    // �ֱַ���������ArrayListԪ�أ��ֱ��������һ�����ݼ��ı������Ƽ��Ϻͷ�����ֵ���ϣ����ٸ�������˵��һ�� _aryListDataSets ��������ɿ�������������ݣ�
    //
    //  _aryListDataSets(ArrayList����)
    //     |--(ArrayList����Ԫ��)
    //     |       |--(ArrayList����Ԫ��)
    //     |       |       |--[Ppv],[V],[V1]  .....��������3��String���ͱ��������Ǳ���������
    //     |       |--(ArrayList����Ԫ��)
    //     |               |--[8.9],[1.23],[9.876]  .....��������3��double������ֵ���������3��String�������Ӧ�����Ǳ����ķ�����ֵ
    //     |--(ArrayList����Ԫ��)
    //     |       |--(ArrayList����Ԫ��)
    //     |       |       |--[I1],[I2]   ..... ��������2��String���͵ı�������
    //     |       |--(ArrayList����Ԫ��)
    //     |               |--[0.78],[1.482]  ..... ��������2��double���͵ķ�����ֵ
    //     |--(ArrayList����Ԫ��)
    //             |--(ArrayList����Ԫ��)
    //             |       |--[SOC]
    //             |--(ArrayList����Ԫ��)
    //                     |--[0.845]

    static int _iExtraTabbedPaneNum = 0; // ������¼��ǰ������򹲲����˶��ٸ������ѡ���壬��ν��Extra���ǳ�ȥ������̨��ѡ�����������ӵ�����ѡ�����

    static ArrayList _aryListSimulinkPlotPanel = null; // ������Ż���������������Ǽ���Card��� --add by Bruse 2014-10-12
    static ArrayList <JPanel>_aryListPlotHoldPanel = null; // �������Plot�����Ķ�̬����
    static ArrayList <Plot>_aryListPlotPanel = null; // �������ÿ��ѡ��е�Plot��ͼ���

    static boolean _bSimulationBegin = false; // ����ָʾ��ǰʱ�䣬�����Ƿ��з���������

    static int _iLoopCount = 0;

    // add by Bruse, 2014-8-23
    static double _dSimulationTime = 0.0; // ��¼��ǰ�ķ���ʱ�䣬���ں��� _handleNextSocket������socket��Ϣ�����˼��

    // add by Bruse, 2014-9-15
    static String _mdlFileName = null; // ��¼matlab�е�mdl�ļ�����һ���պ���չ������ͨ��matlab�������simulink��ͣ�ͼ���������
    // �Ļ���_mdlFileName�ز�����

    static String _simulinkDataTabpaneName = "��������"; // ����ϵͳ�����������������ѡ�����
    // add by Bruse, 2014-9-26
    static ArrayList _aryListCyberDataset = null; // ��¼PT��Simulink���͵�����
    // ���ArrayList��ŵ�������ArrayList�����У�һ��������ű������ƣ�һ��������ű�����ֵ
    // ���_aryListCyberDataset����Ľṹ���������_aryListPhysicalDataSet���е�һ��Ԫ��һ����


    static ArrayList _aryListPhysicalDataset = null;
    // ��� _aryListPhysicalDataSet �������Ը��ӣ���������ÿ��Ԫ�ض���ArrayList���ͣ�_aryListPhysicalDataSet�е���ArrayListԪ��
    // �ֱַ���������ArrayListԪ�أ��ֱ��������һ�����ݼ��ı������Ƽ��Ϻͷ�����ֵ���ϣ����ٸ�������˵��һ�� _aryListPhysicalDataSet ��������ɿ�������������ݣ�
    //
    //  _aryListPhysicalDataSet(ArrayList����)
    //     |--(ArrayList����Ԫ��)
    //     |       |--(ArrayList����Ԫ��)
    //     |       |       |--[Ppv],[V],[V1]  .....��������3��String���ͱ��������Ǳ���������
    //     |       |--(ArrayList����Ԫ��)
    //     |               |--[8.9],[1.23],[9.876]  .....��������3��double������ֵ���������3��String�������Ӧ�����Ǳ����ķ�����ֵ
    //     |--(ArrayList����Ԫ��)
    //     |       |--(ArrayList����Ԫ��)
    //     |       |       |--[I1],[I2]   ..... ��������2��String���͵ı�������
    //     |       |--(ArrayList����Ԫ��)
    //     |               |--[0.78],[1.482]  ..... ��������2��double���͵ķ�����ֵ
    //     |--(ArrayList����Ԫ��)
    //             |--(ArrayList����Ԫ��)
    //             |       |--[SOC]
    //             |--(ArrayList����Ԫ��)
    //                     |--[0.845]


    //������Action���ϵ��˴������ڱ�д���޸� -ZH
    //��Ϊ��������ֽ����¼��϶࣬���飺��������������Ҫ����¼��ģ���ֱ�����.addActionListener(this)��Ȼ���ڴ����в������if�жϣ�ִ����Ӧ����
    //���������ϳ�������ֲ�ɵ����ĺ��������ڽṹ�����������xml�������ҷֲ��openPTxml������
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("��(O)")) {     
            openPTxml();      
        }
        if (cmd.equals("����MATALB")){
            try {
                Runtime.getRuntime().exec("matlab");
            } catch (IOException e1) {
                e1.printStackTrace();
                //��������쳣��Ϣ~~~~
            }
        }
        if (cmd.equals("����Vergil")){
            try {
                Runtime.getRuntime().exec("vergil");
            } catch (IOException e1) {
                e1.printStackTrace();
                //��������쳣��Ϣ~~~~
            }
        }
        if (cmd.equals("�˳�����(X)")){
            //֮���ټӸ�ȷ�ϴ���
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
        // ���Ա�����ĳ�ʼ��
        _aryListVariableName = new ArrayList();
        _aryListVariableValue = new ArrayList();
        _aryListDataSets = new ArrayList();
        _aryListPlotHoldPanel = new ArrayList();
        _aryListPlotPanel = new ArrayList();

        _aryListCyberDataset = new ArrayList();
        _aryListPhysicalDataset = new ArrayList();
        _aryListSimulinkPlotPanel = new ArrayList();

        _iExtraTabbedPaneNum = 0; // ��ν��Extra���ǳ�ȥ������̨��ѡ�����������ӵ�����ѡ�����

        _bSimulationBegin = false;

        _iLoopCount = 0;

        _dSimulationTime = 0.0; // add by Bruse, 2014-8-23


        // ���濪ʼ���������洰�ڣ����ڴ�������Ӻø����˵���
        _createWindowAndMenu();


        // ���濪ʼ����ѡ���壬������һ��������̨��ѡ����뵽ѡ������ȥ
        _createTabbedPaneAndConsoleTabbedPanel();

        _dataExchangePanel = new JDataShowTablePanel();
        mainTabbedPane.addTab("���ݽ���", _dataExchangePanel);
        //����ѡ�������¼+1    add by Bruse, 2014-10-12
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


        /** �����ǲ��� JPhysicalDataPlotPanel��Ĵ���
         *  add by Bruse , 2014-10-11
         * */
        /*JPhysicalDataPlotPanel physicalPlotPanel = new JPhysicalDataPlotPanel();
        mainTabbedPane.add(physicalPlotPanel, "��������");
        JPanel plotPaneltemp = new JPanel();

        plotPaneltemp.add(new Plot(),BorderLayout.CENTER);
        physicalPlotPanel.addOneCard(plotPaneltemp);
        physicalPlotPanel.addOneCard(new JPanel());
        physicalPlotPanel.showButtons();*/

        // OK,���漸�д������ͨ��
    }

    private void _createWindowAndMenu()
    {
        // ���濪ʼ���������洰�ڣ����ڴ�������Ӻø����˵���
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){}
        mainFrame = new JFrame("SimJ&M"); // �������򴰿ڣ����ڱ���Ϊ SimJ&M
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ��ȡ��Ļ��С�������ô��ڳ�ʼ����ʾλ��
        Toolkit kit= Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();

        // ���ô��ڳ�ʼ�ߴ�
        WIN_WIDTH = screenSize.width - 50;
        WIN_HEIGHT = screenSize.height -50;

        int x = (screenSize.width - WIN_WIDTH)/2;
        int y = (screenSize.height - WIN_HEIGHT)/2;
        mainFrame.setLocation(x, y); // ���ô��ڳ���ʱ������Ļ������

        mainFrame.setVisible(true); // Ĭ��Ϊfalse
        mainFrame.setSize(WIN_WIDTH, WIN_HEIGHT); // ���ô��ڵĳߴ�

        // �����˵������Ӳ˵���
        menuBar = new JMenuBar(); // �������˵���
        mainFrame.setJMenuBar(menuBar); // ���˵�����ӵ�������

        menuFile = new JMenu("�ļ�(F)");
        menuFile.setMnemonic('F'); // ��������˵��Ŀ�ݼ�Ϊ 'F'
        menuEdit = new JMenu("�༭(E)");
        menuEdit.setMnemonic('E');
        menuView = new JMenu("��ͼ(V)");
        menuView.setMnemonic('V');
        menuTool = new JMenu("����(T)");
        menuTool.setMnemonic('T');
        menuHelp = new JMenu("����(H)");
        menuHelp.setMnemonic('H');

        // �������õĲ˵���ӵ��˵�����
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuView);
        menuBar.add(menuTool);
        menuBar.add(menuHelp);

        // �����˵�������úÿ�ݼ�
        JMenuItem menuItemOpenInFile = new JMenuItem("��(O)");
        //����¼����� -ZH
        menuItemOpenInFile.addActionListener(this);
        // Ϊ���򿪡�����˵��� �趨 ��ݼ� --- Ctrl + O
        menuItemOpenInFile.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemOpenInFile); // ������˵�����ӵ����ļ����˵���
        menuFile.addSeparator();

        JMenuItem menuItemOpenMATALBInFile = new JMenuItem("����MATALB");
        menuItemOpenMATALBInFile.addActionListener(this);
        menuFile.add(menuItemOpenMATALBInFile);

        JMenuItem menuItemOpenPTInFile = new JMenuItem("���� Vergil");
        menuItemOpenPTInFile.addActionListener(this);
        menuFile.add(menuItemOpenPTInFile);

        menuFile.addSeparator();
        JMenuItem menuItemExitInFile = new JMenuItem("�˳�����(X)");
        menuItemExitInFile.addActionListener(this);
        menuItemExitInFile.setAccelerator(KeyStroke.getKeyStroke('X', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemExitInFile);        

        
        JMenuItem menuItemClearOldSimulation = new JMenuItem("������з�����Ϣ(R)");
        menuItemClearOldSimulation.setAccelerator(KeyStroke.getKeyStroke('R', java.awt.Event.CTRL_MASK, false));
        menuEdit.add(menuItemClearOldSimulation);
        
        
        
        JMenuItem menuItemConfigure = new JMenuItem("����");
        menuTool.add(menuItemConfigure);
        
        
        JMenuItem menuItemGetHelpInHelp = new JMenuItem("ʹ��˵��");
        menuItemGetHelpInHelp.addActionListener(this);
        menuHelp.add(menuItemGetHelpInHelp);
        menuHelp.addSeparator();

        JMenuItem menuItemInfoInHelp = new JMenuItem("����SimJ&M");
        menuItemInfoInHelp.addActionListener(this);
        menuHelp.add(menuItemInfoInHelp);
    }


    private void _createTabbedPaneAndConsoleTabbedPanel() {
        // ���濪ʼ����ѡ���壬������һ������ʼ��ѡ����뵽ѡ������ȥ

        mainTabbedPane = new JTabbedPane(); // �������ڼ��ص�ѡ����
        mainFrame.setContentPane(mainTabbedPane); // ���ô��ڵ���ʾ���Ϊ �����ѡ����
        mainTabbedPane.setVisible(true); // �������ѡ����ɼ�
        // ����ѡ����Ĵ�С����ѡ���λ�÷���
        // mainTabbedPane.setPreferredSize(new Dimension(500,200));
        mainTabbedPane.setTabPlacement(JTabbedPane.TOP);
        // ����ѡ��������ڵ���ʾ��ʽ
        mainTabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        // mainTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // mainFrame.pack(); // ����������Ӧ����Ĵ�С

        
        // ���ѡ���������������ÿ��ѡ��ı�ǩ�Լ����Ƿ����

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
        mainTabbedPane.addTab("����̨", _consoleTabbedPanel); // ������ʼ����� panel0 ��Ϊ��һ��ѡ�
        mainTabbedPane.setEnabledAt(0, true);

        // jScrollPanel0.scrollRectToVisible(new Rectangle(900,500));
        
        
        
    }

    
    private void openPTxml(){
        // ����һ���ļ�ѡ��Ի��򣬹��û�ѡ�� .xml �ļ�
        JFileChooser fileChooser = new JFileChooser("D:\\");
        // �����ļ�����ѡ�� -ZH
        fileChooser.addChoosableFileFilter(new JAVAFileFilter("xml"));
        File file = null;
        int iResult = 0;
        fileChooser.setApproveButtonText("ȷ��");
        fileChooser.setDialogTitle("���ļ�");
        iResult = fileChooser.showOpenDialog(mainFrame);
        /* ���û���ѡ���ļ� ���Ұ���"ȷ��"��ť�󣬾Ϳ���ͨ�� getSelectedFile() ����ȡ���ļ�����
         * */
        if( iResult == JFileChooser.APPROVE_OPTION )
        {
            // �û����µġ�ȷ������ť
            file = fileChooser.getSelectedFile();
            System.out.println("ѡ����ļ�����" + file.getName());
            System.out.println("ѡ����ļ�����" + file.getAbsolutePath());

            String fileName = file.getName();
            if( fileName.endsWith("xml") ) // ѡ�е�ȷʵ��xml�ļ�
            {
                // ��ѡ�е�xml�ļ����� Vergil����������
                
                // �Ƚ�ѡ�е��ļ�·����ӵ�������̨����XML�ļ��б���
                _consoleTabbedPanel.addOneXMLfilePath(file.getAbsolutePath());
                
                //String startVergilcmd = "vergil -run E:\\PT_workspace\\pvbattery_50-60_org.xml";
                //���·���ո����⣬����˫����  -ZH
                String startVergilcmd = "vergil -run \"" + file.getAbsolutePath()+"\"";

                // ����Ƿ��Ѿ��з����ڽ���
                if( _bSimulationBegin == true )
                {
                    // �����Ի�����ʾ�û����Ƿ������ǰ���棬�������µķ���
                    int iStartNewSim = JOptionPane.showConfirmDialog(mainFrame, 
                            "Ŀǰϵͳ�Ѿ��з�����������У��Ƿ������ǰ������򲢿����µķ���", 
                            "�Ƿ������ǰ�������", 
                            JOptionPane.YES_NO_OPTION);
                    if( iStartNewSim == JOptionPane.YES_OPTION )
                    {
                        // �����ǰ���������ѡ��Լ������Ϣ
                        // _clearOldSimulation(); �˴������ݲ�ִ����������������ɵķ���������Ϣ����Ϊ�����µķ�����Ч������xml����ȷ��

                        // �޸� _bSimulationBegin ��ֵ����ʾ���Կ����µķ���

                        //kill PT Matlab���� --ZH

                        try {
                            // �����ԣ����ﲻ�ܼ򵥵ش�����������kill��matlab��vergil��
                            // ����ȴ���������ִ����ϣ����ܼ���ִ��if�ṹ����Ĵ��룬����if�ṹ�����
                            // _startNewSimulationInVergil ����ִ��ʧЧ�����������µ�vergil����
                            // ������������������ܵ�ԭ��������taskkill������_startNewSimulationInVergil����ִ��ʱ��
                            // ��û�н����������ڶ���taskkill���̻�kill���³��ֵ�vergil����

                            String commandStr="taskkill /f /im MATLAB.exe"; 
                            Process p = Runtime.getRuntime().exec(commandStr);
                            // �ȴ�����ִ����ϣ���ִ�к�������
                            if(p.waitFor() != 0)
                            {
                                if( p.exitValue() == 1 ) // 0��ʾ����������1������������
                                {
                                    // do nothing
                                }
                                else
                                {
                                    // ��ʾtaskkill����ִ��ʧ�ܣ�����������־�ļ��������
                                }
                            }
                            commandStr="taskkill /f /im vergil.exe"; 
                            p = Runtime.getRuntime().exec(commandStr);
                            // �ȴ�����ִ����ϣ���ִ�к�������
                            if(p.waitFor() != 0)
                            {
                                if( p.exitValue() == 1 ) // 0��ʾ����������1������������
                                {
                                    // do nothing
                                }
                                else
                                {
                                    // ��ʾtaskkill����ִ��ʧ�ܣ�����������־�ļ��������
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
                        return; // �¼����������������������µķ���
                    }                            
                } //  if( _bSimulationBegin == true )

                // ��������
                _startNewSimulationInVergil(startVergilcmd);
                
                // ��������̨��ѡ��еġ���ͣ����ֹͣ����ť����
                _consoleTabbedPanel.setStopAndPauseButtonEnabled(true);
                
                // ��������̨����ʱ���ı���ر�
                _consoleTabbedPanel.setSimTimeEditable(false);
                
                // ��������̨������������ʾ�ռ�����
                _consoleTabbedPanel.resetProgress();


            } // if( fileName.endsWith("xml") ) // ѡ�е�ȷʵ��xml�ļ�
            else
            {
                // ѡ�еĲ����� xml �ļ��������Ի�����ʾ�û�
                JOptionPane.showMessageDialog(mainFrame, 
                        file.getName() + " ���� .xml�ļ���Ptolemy II�޷��򿪴��ļ���������ѡ��", 
                        "������ļ�����", 
                        JOptionPane.ERROR_MESSAGE);
            }

        } // if( iResult == JFileChooser.APPROVE_OPTION )
        else
        {
            // �û�û�е��ȷ����ť��do nothing
        }
        // return;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new WinMain(); // ������������

        // �����������������ܷ�̬�� mainTabbedPane����µ�ѡ�

        /*JPanel panel_1 = new JPanel();       
        mainTabbedPane.addTab("�����������",panel_1); 
        mainTabbedPane.setEnabledAt(1,true);*/
        // �����ԣ����������OK�ģ�Ҳ���ǿ������������ж�̬���ѡ����


        /*// ���濪ʼ�������2�����ϵ�Plot��壬�����кܴ������

        JPanel panelPlot = new JPanel();
        panelPlot.setLayout(new BorderLayout());
        Plot testPlot = new Plot();
        panelPlot.add(testPlot, BorderLayout.CENTER);

        //����������Ҫ����addtabǰ--zh

        testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // ��Ч��������Ϊ��λ��ȷ����ͼ����ķ�Χ
        testPlot.setButtons(true);// ��Ч���ǳ���Ҫ

        mainTabbedPane.addTab("��ͼ�������", panelPlot);
        mainTabbedPane.setEnabledAt(2,true);
        // mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);

        // testPlot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT); // û��Ч��
        // testPlot.setPlotRectangle(new Rectangle(0,0,WIN_WIDTH,WIN_HEIGHT) ); // ��Ч

        testPlot.addPoint(1, 0.4, 0.5, true); // ��Ч���д��о�
        testPlot.addPoint(1, 0.9, 10, true);
        testPlot.addPoint(0, 1.0, 15, true);
        testPlot.addPoint(0, 1.5, -6.0, true);
        testPlot.fillPlot();


        JPanel panelPlot1 = new JPanel();

        Plot testPlot1 = new Plot();
        panelPlot1.add(testPlot1);

        testPlot1.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // ��Ч��������Ϊ��λ��ȷ����ͼ����ķ�Χ
        testPlot1.setButtons(true);// ��Ч���ǳ���Ҫ

        mainTabbedPane.addTab("��ͼ�������1", panelPlot1);
        mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);


        // testPlot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT); // û��Ч��
        // testPlot.setPlotRectangle(new Rectangle(0,0,WIN_WIDTH,WIN_HEIGHT) ); // ��Ч

        testPlot1.addPoint(1, 0.4, 0.5, true); // ��Ч���д��о�
        testPlot1.addPoint(1, 0.9, 10, true);
        testPlot1.addPoint(1, 1.0, 15, true);
        testPlot1.fillPlot(); */      

        // �������������򣬵ȴ��ͻ��˵ķ�������
        ServerSocket server;
        Socket socket;
        String str_tem;
        InputStream fIn;
        // OutputStream fOut;
        BufferedReader bfreader;
        // PrintStream ps;

        try
        {
            server = new ServerSocket(4700); // �ڶ˿�4700�ϴ���������Socket����

            /****************** 
             * ����whileѭ�����뾭��������OK�ģ����Խ��ܶ�ε�����socket�ͻ������ӷ��͵���Ϣ
             * **********************************/
            while(true)
            {
                socket = server.accept(); /*���ˣ����ø÷����ķ���������������ֱ���յ��ͻ��˵�����
                                                                ���ͻ������趨�Ķ˿�������ʱ��������Socket�Ϳͻ��˵�Socket��������*/
                System.out.println("������Ready��");

                //��ö�ӦSocket�����������
                fIn = socket.getInputStream();
                // fOut = socket.getOutputStream();

                //���潨��������
                bfreader = new BufferedReader(new InputStreamReader(fIn));
                //ps = new PrintStream(fOut);
                //BufferedReader UserIn = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("�ȴ��ͻ�����Ϣ...");
                str_tem = bfreader.readLine(); // ���ͻ��˴��͵��ַ���

                if( str_tem == "close" ) 
                { 
                    // �����Եظ���һ��berak��֧��䣬������������ڿ��������ô�������pt����matlab���Է��������close����Ϣ��
                    // ���棬��֪ͨ����ر�Socket������̣����ҿ���������������еķ�����Ϣ
                    break; 
                }

                String str = "";
                if( str_tem.startsWith("<socketMessage>") )
                {

                    // һ��xml-socket��Ϣ�Ŀ�ʼ
                    if(str_tem.endsWith("</socketMessage>"))
                    {
                        // ���յ�һ��������<socketMessage>..</socketMessage>��Ϣ
                        System.out.println("�ͻ��ˣ�" + str_tem); // ��ʾ�ַ���    

                        String str_dataset = str_tem.trim();

                        _handleSocketMessage(str_dataset);

                    }
                    else
                    {
                        System.out.println("�ͻ��ˣ�" + str_tem); // ��ʾ�ַ���    
                        // �������ղ���������Ϣ��ֱ���� </socketMessage> �������
                        while( !(str = bfreader.readLine()).endsWith("</socketMessage>") )
                        {
                            if( str.startsWith("<socketMessage>") )
                            {
                                // һ��������socket��Ϣû�н����꣬ȴ�ֹ���һ����Ϣͷ���������ش���
                                // ����������־�ļ�����¼�������
                                // �˴�����֪��ӡ��������󵽿���̨����
                                System.out.println("fatal error!\r Socket��Ϣ���ճ��ֶ����Ϣͷ����");
                                break;
                            }
                            else
                            {
                                System.out.println("�ͻ��ˣ�" + str); // ��ʾ�ַ���    
                                str_tem = str_tem + str;
                            }
                        }
                        str_tem = str_tem + str;
                        // ���յ�һ��������<socketMessage>..</socketMessage>��Ϣ
                        System.out.println("�ͻ��ˣ�" + str_tem); // ��ʾ�ַ���    

                        String str_dataset = str_tem.trim();

                        _handleSocketMessage(str_dataset);
                    }
                }
                else // if( str_tem.startsWith("<socketMessage>") )
                {
                    // ��Ҳ�Ǹ������Ĵ�����Ϣ��ʼ���䣬ȴû����Ϣͷ
                    System.out.println("fatal error! socket��Ϣû����Ϣͷ��");
                }

                // System.out.println("�ͻ��ˣ�" + str_tem); // ��ʾ�ַ���    

                // �ر����socket����
                bfreader.close();
                //ps.close();
                fIn.close();
                //fOut.close();
                socket.close();
                /* ����������������ֵ��ע�⣬ServerSocket����server ��û�б��رգ����������� */


            } // while(true)

            server.close(); // �رշ������˵�socket����

        }
        catch(Exception e)
        {
            System.out.println("�쳣�ǣ�" + e);
        }

        return; // main return
    }



    /****************************************************************
     * //------���Ǻ����Ϊ�����з�����Ҫ����˾�̬������                    -ZH
     *******************   private methods *************************/

    /** �����ǽ�socket��ʽ���ó�xml֮�����ڴ���socket��Ϣ��ʹ�õĺ������壬����xml����������������ȡ����
     * ������ 2014��9��26�գ�by Bruse
     * ---�������忪ʼ */

    // xml�﷨��������
    static String _getTagValue(String tagName, String xmlText)
    {
        // ���ݱ�ǩtagName�����ݣ���ȡ�ñ�ǩ�԰�����ֵ.
        // ���磬����һ����ǩ��  xmlText = "<point>0.89</point>",���ú��� _getTagValue("point",xmlText)
        // ���õ��ַ��� "0.89"
        // ���xmlText���ж�� tagName��ǩ�����Ե�һ��tagNameΪ׼

        // �������
        if( tagName == null || xmlText == null || tagName.isEmpty() || xmlText.isEmpty())
        { return null; }

        // ���xmlText���Ƿ���<tagName>..</tagName>��ǩ��
        String PatternStr = "^.*<" + tagName + ".*>.*</" + tagName + ">.*$";
        boolean b = Pattern.matches(
                PatternStr,
                xmlText);
        if(b == false)
        {  return null;}

        // ��ʼ��ȡ
        int iBegin = xmlText.indexOf(tagName); // �ҵ�tagName��ʼλ��
        iBegin = xmlText.indexOf('>', iBegin); // �ҵ�tagName����� '>' �ַ�λ��
        int iEnd = xmlText.indexOf('<', iBegin);
        // iBeign �� iEnd ֮������ݾ���tagName��ǩ�԰����Ķ�����
        if( iBegin + 1 >= iEnd ) // tagName��ǩ��֮��û�а����κ�����
        {return null;}
        String tagValue = xmlText.substring(iBegin + 1, iEnd);

        return tagValue.trim(); // ȥ��tagValue��ͷ�ͽ�β���ܴ��ڵĿհ��ַ��󷵻�
    }

    static String _getTagPropertyValue(String tagName, String propertyName, String xmlText)
    {
        // ���ݱ�ǩtagName�����ݣ���ȡ�ñ�ǩ����propertyName��Ӧ��ֵ.
        // ���磬����һ����ǩ��  xmlText = "<point name="SOC">0.89</point>",���ú��� 
        // _getTagPropertyValue("point","name",xmlText)
        // ���õ��ַ��� "SOC"
        // ���xmlText���ж�� tagName��ǩ�����Ե�һ��tagNameΪ׼

        // �������
        if(tagName.isEmpty() || propertyName.isEmpty() || xmlText.isEmpty()
                || tagName == null || propertyName == null || xmlText == null)
        {return null; }

        // ���xmlText���Ƿ���<tagName propertyName="..">..</tagName>��ǩ��
        String PatternStr = "^.*<" + tagName + "\\s+" + propertyName +"\\s*=\\s*\".+\".*>.*</" + tagName + ">.*$";
        boolean b = Pattern.matches(
                PatternStr,
                xmlText);
        if(b == false)
        {  return null;}

        // ��ʼ��ȡ
        int iBegin = xmlText.indexOf("<" + tagName + " "); // �ҵ� <tagName_   ��ʼλ��,_��ʾtagName������һ���ո�
        iBegin = xmlText.indexOf(propertyName, iBegin); // �ҵ�tagName����� propertyName �ַ�λ��
        iBegin = xmlText.indexOf('"', iBegin); // �ҵ�propertyName����� " ��λ��
        int iEnd = xmlText.indexOf('"', iBegin+1); // ������ʽƥ���һ�����ҵ��ɶԵ�˫����
        // iBeign �� iEnd ֮������ݾ���tagName��ǩ�԰����Ķ�����
        String tagValue = xmlText.substring(iBegin + 1, iEnd);

        return tagValue.trim(); // ȥ��tagValue��ͷ�ͽ�β���ܴ��ڵĿհ��ַ��󷵻�
    }

    static int _getLoopCountFromXML(String xmlSocketMessage)
    {
        String sLoopCount = _getTagValue("iteration",xmlSocketMessage);
        if( sLoopCount == null ) return -1;
        int iLoopCount = Integer.valueOf(sLoopCount);

        // ������������
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
        // ��ȡxml��ʽ�����socket��Ϣ��PT���͸�Simulink������

        ArrayList <ArrayList>aryListPtolemyDataset = new ArrayList();

        // �������
        if(xmlSocketMessage == null || xmlSocketMessage.isEmpty())
        {
            return aryListPtolemyDataset;
        }

        ArrayList <String>aryListPointName = new ArrayList();
        ArrayList <Double>aryListPointValue = new ArrayList();

        String ptDataset = _getSubXML("PtolemyDataset", xmlSocketMessage);
        int iBegin = 0, iEnd = 0;
        while( (iBegin = ptDataset.indexOf("<point", iEnd)) != -1 ) // ����<point></point>���ݶ�û�з��ʵ�
        {
            iEnd = iBegin + "point".length();
            String pointXML = _getSubXML("point",ptDataset,iBegin);
            aryListPointName.add(_getTagPropertyValue("point","name",pointXML));
            aryListPointValue.add(Double.valueOf( _getTagValue("point",pointXML) ));
        }

        aryListPtolemyDataset.add(aryListPointName);
        aryListPtolemyDataset.add(aryListPointValue);

        // �������
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
        // ��ȡxml��ʽ�����socket��Ϣ�а�����simulink�������ݣ�Ҳ����Simulink������PT�ķ�������

        ArrayList <ArrayList>aryListSimulinkDataset = new ArrayList();
        // �������
        if(xmlSocketMessage == null || xmlSocketMessage.isEmpty())
        {
            return aryListSimulinkDataset;
        }

        ArrayList <ArrayList>aryListdataset = new ArrayList();
        ArrayList <String>aryListPointName = new ArrayList();
        ArrayList <Double>aryListPointValue = new ArrayList();

        String simulinkDataset = _getSubXML("SimulinkDatasets",xmlSocketMessage);

        int iDatasetBegin = 0, iDatasetEnd = 0;
        while( (iDatasetBegin = simulinkDataset.indexOf("<dataset", iDatasetEnd)) != -1 ) // ����<dataset></dataset>��ǩ��û�з���
        {
            String strDataset = _getSubXML("dataset",simulinkDataset,iDatasetBegin);
            aryListdataset = new ArrayList();
            iDatasetEnd = iDatasetBegin + "<dataset".length();
            int iBegin = 0, iEnd = 0;
            aryListPointName = new ArrayList();
            aryListPointValue = new ArrayList();
            while( (iBegin = strDataset.indexOf("<point", iEnd)) != -1 ) // ����<point></point>���ݶ�û�з��ʵ�
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

        // �������
        System.out.println("datasets from simulink");
        for(int i = 0; i < aryListSimulinkDataset.size(); ++i)
        {
            aryListdataset = aryListSimulinkDataset.get(i);
            aryListPointName = aryListdataset.get(0);
            aryListPointValue = aryListdataset.get(1);
            System.out.println("  ��" + i + "���㼯�����ݣ�");
            for(int k = 0; k < aryListPointName.size(); ++k)
            {
                System.out.println("\tname:" + aryListPointName.get(k) + "\tvalue:" + aryListPointValue.get(k));
            }
        }

        return aryListSimulinkDataset;
    }

    /** �����ǽ�socket��ʽ���ó�xml֮�����ڴ���socket��Ϣ��ʹ�õĺ������壬����xml����������������ȡ����
     * ������ 2014��9��26�գ�by Bruse
     * ---����������� */

    static private void _handleSocketMessage(String socketMessage)
    {
        // ���濪ʼ�ص㣬����ͻ��˷��͵������ַ��������ж�ȡ��: 
        // ��1�����ݼ��ı�Σ������ͻ��˵ڼ�����������������ݼ�
        // ��2������ʱ��
        // ��3���������������ݵ�
        // �ͻ��˷��͵���Ϣ��ʽ���¡���
        // <����ѭ�����-����ʱ��-[(����1,��ֵ1)-(����2,��ֵ2)-...-(����n,��ֵn)]-[(����1,��ֵ1)-(����2,��ֵ2)-...-(����n,��ֵn)]-...-[(����1,��ֵ1)-(����2,��ֵ2)-...-(����n,��ֵn)]>

        // �Ƚ��в������
        if( socketMessage == null || socketMessage.isEmpty() )
        {
            return;
        }

        // ���ͻ��˷���������Ϣ��ʽ�Ƿ���ȷ��ʹ��������ʽ���
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
        if(b == true) // ��Ϣ��ʽ��ȷ�����Խ�����һ���Ĵ�����
        {
            System.out.println("xml-socket��Ϣ��ʽ��ȷ��");

            int iLoopCount = _getLoopCountFromXML(socketMessage);
            if( iLoopCount > 0 ) // socket��Ϣ�����ݼ�ѭ�������Ϸ�
            {
                // ��ȡsocket��Ϣ�е����ݼ�


                if( iLoopCount == 1 && _bSimulationBegin == false) 
                {
                    // ���η�����̷��͵ĵ�һ��socket��Ϣ������û�з��������ִ�л��߿��Կ����µķ������
                    _handleFirstSocketXML(socketMessage);

                }
                else
                {
                    // ���������socket��Ϣ����̬�������ݵ㲢��ӵ�plot�����ȥ����
                    _handleNextSocketXML(socketMessage);
                }
            }
            else // socket��Ϣ�����ݼ�ѭ���������Ϸ�
            {
                //���ݳ����쳣�����Կ�����������־�ļ�����ʽ���Ų����

                return;
            }

        } //  if( socketMessage.endsWith(">") && socketMessage.startsWith("<") )
        else
        {
            // ******************************** ���￼��������ɾ���Ի���
            System.out.println("socket��Ϣ��ʽ���󣡣���");
            return;
        }
    }

    // add by Bruse, 2014-9-27
    static private void _handleFirstSocketXML(String socketMessage)
    {
        // ���η�����̷��͵ĵ�һ��socket��Ϣ������û�з��������ִ�л��߿��Կ����µķ������

        // �Ƚ��в������
        if(socketMessage.isEmpty())
        {
            return;
        }

        // ���濪ʼ��һЩ��ʼ���Ĺ���
        _clearOldSimulation(); // ����ɵķ�����Ϣ

        // ��ȡsocket��Ϣ�е����ݼ�
        ArrayList <ArrayList>aryListCyberDataset = _getPtolemyDataset(socketMessage);
        ArrayList <ArrayList>aryListPhysicalDataset = _getSimulinkDataset(socketMessage);


        if( aryListCyberDataset.isEmpty() || aryListPhysicalDataset.isEmpty())
        {
            // ��������Ի���
            JOptionPane.showInternalMessageDialog(mainFrame, 
                    "�µķ������û�м�⵽���ݵĴ��䣬�������ķ���ģ�ͣ�", 
                    "�����޷�������ɹ���⣡", JOptionPane.ERROR_MESSAGE);
            return; // ���������Ϣ����
        }
        else // 
        {
            // ���һ�»�ȡ�����ݼ��Ƿ���ȷ����
            /*if( !_validateDataSet(aryListDataSets, true) )
            {
                // ���ݼ����Ϸ����˳������Ϣ����
                return;
            }*/

            // ����һ��socket��Ϣ���ݼ����浽��ľ�̬�б���
            _aryListCyberDataset = aryListCyberDataset;
            _aryListPhysicalDataset = aryListPhysicalDataset;

            double dSimulationTime = _getSimulationTimeFromXML(socketMessage);


            // �����������ݼ��б�aryListCyberDataset��aryListCyberDataset��
            // �����ݵ㶯̬��ӵ���̬���(_dataExchangePanel)��
            _addExchangeDatasIntoTable(aryListCyberDataset,0.0,aryListPhysicalDataset,dSimulationTime);


            // ���ÿ��plot����Ӧ�ı���
            Vector<String> v_plotCaption = new Vector();
            
            // ��ʱû��ʵ�ְ� aryListCyberDataset�е����ݵ㻭������Ҳ���ǣ�û�а�PT����simulink�����ݻ�����
            // ��Ϣ��ȷ����ʼ���aryListPhysicalDataset��ÿ���������ݼ�����Ӻ�plot��ͼѡ�
            for(int i = 0; i < aryListPhysicalDataset.size(); ++i)
            {
                // ������һ��Plot��壬�Լ���������JPanel
                JPanel panelPlot = new JPanel();
                panelPlot.setLayout(new BorderLayout());
                Plot testPlot = new Plot();

                // �������õ� panelPlot �� testPlot ��ӵ� ����ľ�̬�б���
                _aryListPlotHoldPanel.add(panelPlot);
                _aryListPlotPanel.add(testPlot);

                ((JPanel)_aryListPlotHoldPanel.get(i)).add((Plot)_aryListPlotPanel.get(i)
                        , BorderLayout.CENTER);
                // testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // ��Ч��������Ϊ��λ��ȷ����ͼ����ķ�Χ
                ((Plot)_aryListPlotPanel.get(i)).setButtons(true);// ��Ч���ǳ���Ҫ



                String tabTitle = ""; // �ɱ���������ɵ�ѡ�����
                Vector vcCaption = new Vector(); // ���ڴ��ÿ�����������ƣ�������ÿ�����ߵı�ʾʱʹ��

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

                // ((Plot)_aryListPlotPanel.get(i)).setCaptions(vcCaption); // ���н����ʾ��
                // �������ֻ���������²�����˼��б��⣬������������߽�����ӣ��д��Ľ�


                ((Plot)_aryListPlotPanel.get(i)).fillPlot(); // �ػ�ͼ��

                /*if(false)
                {
                    tabTitle = tabTitle.substring(1, tabTitle.length()); // ȥ����һ������ǰ�Ķ���
                    int iTitleLength = tabTitle.length();
                    int iMinTitleLength = 5;
                    if(iTitleLength < iMinTitleLength) // ������̫�̣��ں�������ո�
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
                
                tabTitle = tabTitle.substring(1, tabTitle.length()); // ȥ����һ������ǰ�Ķ���
                v_plotCaption.add(tabTitle);
                
            }

            /* ����������ݼ��������ö����ͼ��� */
            int iDatasetNum = aryListPhysicalDataset.size();
            if( iDatasetNum > 0 )
            {
                if( iDatasetNum == 1 ) // ֻ��һ�����ݼ�����ʱ����Ҫ JPhysicalDataPlotPanel��������չʾ��������
                {
                    // ֻҪ�� _aryListPlotHoldPanel�е�Ψһ�����Ԫ����ӽ�һ�� ѡ� ����
                    mainTabbedPane.addTab(_simulinkDataTabpaneName, _aryListPlotHoldPanel.get(0));

                    // ����ѡ�������¼+1
                    ++_iExtraTabbedPaneNum;
                }
                else
                {
                    
                    // ���汻ע�͵Ĵ��룬����������һ�����϶��plot��panel��չʾ����������������⣬��ʾʧ�ܣ��д��Ľ�
                    /*// ��������£���ֹһ�����ݼ�����Ҫ�����ܵ�һ����ͼpanel�����а�_aryListPlotHoldPanel�е������ӵ�_simulinkDataTabpaneName��ȥ
                    JScrollPane sumScrollPlotPanel = new JScrollPane(); // ���panel���ֹ������� GridLayout
                    JPanel sumPlotPanel = new JPanel();
                    // �������ݼ�������Ҳ������ʵ��Plot�����������������Ҫ�ı��ߴ磨������������
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
                        // �����еĻ�ͼ�����ӵ������
                        sumPlotPanel.add(_aryListPlotHoldPanel.get(i_plot));
                    }*/

                    // ���� JPhysicalDataPlotPanel ����

                    _physicalPlotPanel = new JPhysicalDataPlotPanel();

                    mainTabbedPane.addTab(_simulinkDataTabpaneName, _physicalPlotPanel);

                    // ����ѡ�������¼+1
                    ++_iExtraTabbedPaneNum;

                   /* _physicalPlotPanel.addOneCard(sumPlotPanel);
                    _aryListSimulinkPlotPanel.add(sumPlotPanel);*/

                    for(int i_plot = 0; i_plot < _aryListPlotHoldPanel.size(); ++i_plot)
                    {
                        // �����еĻ�ͼ�����ӵ�  _physicalPlotPanel ��
                        _physicalPlotPanel.addOneCard(_aryListPlotHoldPanel.get(i_plot), v_plotCaption.get(i_plot));

                        _aryListSimulinkPlotPanel.add(_aryListPlotHoldPanel.get(i_plot));
                    }
                    // �����ʾ��_physicalPlotPanel�ײ���ѡҳ��ť
                    _physicalPlotPanel.showButtons();
                }
            }
            else
            {
                // �����쳣�����ݼ��б�Ϊ��,���Կ���������־�ļ�
                return;
            }


            // ****����������β���� ****
            // ���µ�ǰ��ľ�̬������ѭ��������������Ϊ1
            _iLoopCount = 1;

            // �����Ѵ��ڷ�����̱�־
            _bSimulationBegin = true;
            
            // ���½���ֵ�ͽ���������ʾ
            _consoleTabbedPanel.setProcess(dSimulationTime);

            // ���·���ʱ�䣬 add by Bruse, 2014-8-23
            _dSimulationTime = dSimulationTime;
        }
    }

    static private void _handleNextSocketXML(String socketMessage)
    {
        // �Ƚ��в������
        if(socketMessage.isEmpty())
        {
            return;
        }

        // ���������socket��Ϣ����̬�������ݵ㲢��ӵ�plot�����ȥ����
        double dSimulationTime = _getSimulationTimeFromXML(socketMessage);

        if( dSimulationTime > _dSimulationTime ) // ����socket��Ϣ�ķ���ʱ����ڵ�ǰ��̬������¼��ʱ�䣬����η���ĺ���ѭ�������Ϣ
        {
            // ���ݺ�����ʼ��ÿ��Plot�����������ݵ�

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

                ((Plot)_aryListPlotPanel.get(i)).fillPlot(); // �ػ�ͼ��
            }


            // ��������ӵ���̬�������ʾ

            _addExchangeDatasIntoTable(_getPtolemyDataset(socketMessage), _dSimulationTime, aryListPhysicalDataset, dSimulationTime);

            // ���µ�ǰ��ľ�̬ѭ����������
            _iLoopCount = _getLoopCountFromXML(socketMessage);

            // ���½���ֵ�ͽ���������ʾ
            _consoleTabbedPanel.setProcess(dSimulationTime);
            
            // ���µ�ǰ��ľ�̬��ŵķ���ʱ��
            _dSimulationTime = dSimulationTime;

        } 
    }


    static private void _clearOldSimulation()
    {
        if( _iExtraTabbedPaneNum > 1 ) // ˵����η���֮ǰ���Ѿ����ڷ������,������з��������Plot���
        {
            // ��mainTabbedPane������ɾ������Plotѡ����
            for(int i = 1; i < _iExtraTabbedPaneNum; ++i)
            {
                mainTabbedPane.remove( mainTabbedPane.getTabCount() - 1 );
            }
        }

        // ��ʼ����ľ�̬�������б������
        _iExtraTabbedPaneNum = 0; // ���plotѡ���Ŀ
        _aryListCyberDataset.clear();
        _aryListPhysicalDataset.clear();

        _aryListSimulinkPlotPanel.clear();

        _aryListPlotHoldPanel.clear(); // ���װ��Plot����JPanel�������
        _aryListPlotPanel.clear(); // ���Plot�������
        _aryListVariableName.clear(); // ��ձ������б�
        _aryListVariableValue.clear(); // ��ձ���ֵ�б�
        _aryListDataSets.clear(); // ������ݼ��б�
        _bSimulationBegin = false;
        _iLoopCount = 0;

        _dSimulationTime = 0.0;

        _mdlFileName = "";
        _physicalPlotPanel = null;
    }


    static private void _addExchangeDatasIntoTable(ArrayList aryListCyberDataset,double last_simTime,ArrayList aryListPhysicalDataset,double dSimulationTime)
    {
        // ���ǶԲ������м��
        ArrayList <String>aryListName = (ArrayList <String>)aryListCyberDataset.get(0);
        ArrayList <Double>aryListValue = (ArrayList <Double>)aryListCyberDataset.get(1);
        // ��PT���͵�matlab��������ӵ����1��
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
        // �Ƚ��в���
        if( str == null || str.isEmpty() )
        {
            return aryListDataSets;
        }

        aryListDataSets.clear(); // ������� aryListDataSet

        int iBeginIndexMBracket = str.indexOf('['); // ���ҵ�һ�� [ ��λ��
        int iEndIndexMBracket = str.indexOf(']'); // ���ҵ�һ�� ] ��λ�ã��Ժ� [ ƥ��
        if( iBeginIndexMBracket >= iEndIndexMBracket ) return aryListDataSets;

        while( iBeginIndexMBracket != -1 && iEndIndexMBracket != -1 )
        {
            ArrayList aryListDataSet = new ArrayList();
            ArrayList aryListPointName = new ArrayList();
            ArrayList aryListPointValue = new ArrayList();

            String strDataset = str.substring(iBeginIndexMBracket, iEndIndexMBracket + 1);

            int iBeginSBracket = strDataset.indexOf('('); // �������[]���ݼ��е�һ��(
            int iEndSBracket = strDataset.indexOf(')'); // �������[]���ݼ��е�һ��)
            int iComma = strDataset.indexOf(',', iBeginSBracket); // Ѱ��()���ݶ��еĶ���λ��
            while( iBeginSBracket != -1 && iEndSBracket != -1 && iComma != -1 )
            {
                aryListPointName.add( strDataset.substring(iBeginSBracket + 1, iComma) );
                aryListPointValue.add( Double.valueOf(strDataset.substring(iComma + 1, iEndSBracket)) );

                iBeginSBracket = strDataset.indexOf('(', iEndSBracket);
                if( iBeginSBracket == -1 )
                {
                    break; // �Ѿ������һ��()���ݶ���
                }
                iEndSBracket = strDataset.indexOf(')', iBeginSBracket); // �������[]���ݼ�����һ��)
                iComma = strDataset.indexOf(',', iBeginSBracket); // Ѱ��()���ݶ��еĶ���λ��
            }
            aryListDataSet.add( aryListPointName );
            aryListDataSet.add( aryListPointValue );

            aryListDataSets.add(aryListDataSet);
            iBeginIndexMBracket = str.indexOf('[', iEndIndexMBracket);
            if(iBeginIndexMBracket == -1)
            {
                break; // �Ѿ�û�� [] ��ʾ�����ݵ㼯��
            }
            iEndIndexMBracket = str.indexOf(']', iBeginIndexMBracket);

        } // while( iBeginIndexMBracket != -1 && iEndIndexMBracket != -1 )

        // ������䣬���һ��  aryListDataSets
        for(int i = 0; i < aryListDataSets.size(); ++i)
        {
            ArrayList aryListDataSet = (ArrayList)aryListDataSets.get(i);
            ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
            ArrayList aryListPointValue = (ArrayList)aryListDataSet.get(1);

            System.out.println("��" + i + "�����ݵ�ֵΪ��");
            for(int j = 0; j < aryListPointName.size(); ++j)
            {
                System.out.println("\t(" + aryListPointName.get(j) + "," + aryListPointValue.get(j) + ")");
            }
        }

        return aryListDataSets;
    }

    static private boolean _validateDataSet(ArrayList aryListDataSets, boolean bFirstSocketMessage)
    {
        // ** ���������ʱ���������ƣ����ڲ�������֤���ݼ��� **
        // ���ڶԻ�ȡ�������ݼ����м�⣬���ݼ������� true�����򷵻� false
        // �������
        if( aryListDataSets == null || aryListDataSets.isEmpty() )
        {
            return false;
        }

        if( bFirstSocketMessage == true )
        {
            // Ŀǰֻ���aryListDataSets�б��������������ֵ�����ά���Ƿ���ȣ������ʱ�����쳣������false

            if( aryListDataSets.size() == 1 )
            {
                ArrayList aryListPointName = (ArrayList)aryListDataSets.get(0);
                ArrayList aryListPointValue = (ArrayList)aryListDataSets.get(1);

                if( aryListPointName.size() != aryListPointValue.size() )
                {
                    // �������ݼ�������Ԫ�ظ�����Ӧ���ϣ�˵���еı������ƻ�����ֵȱʧ����������
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
                        // �������ݼ�������Ԫ�ظ�����Ӧ���ϣ�˵���еı������ƻ�����ֵȱʧ����������
                        return false;
                    }
                }
            }
        }
        else //������������ݼ��Ϸ���
        {
            // �� aryListDataSets ͬ��ľ�̬�б� _aryListDataSets��Ƚϣ�ά����ͬ˵��aryListDataSets�Ϸ���
            // �� _aryListCyberDataset ��  _aryListPhsicalDataset �ǵ�һ��socket��Ϣ��ȡ�õ������ݼ���������Ƕȿ��ǣ�
            // ��һ�η��͵�socket��Ϣ�ǳ���Ҫ

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
                        // �������ݼ�������Ԫ�ظ�����Ӧ���ϣ�˵���еı������ƻ�����ֵȱʧ����������
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static private void _startNewSimulationInVergil(String startVergilcmd)
    {
        // Runtime run = Runtime.getRuntime(); //������Ӧ�ó�����ص�����ʱ����
        try{
            Runtime.getRuntime().exec(startVergilcmd);// ������һ��������ִ�� ָ����ϵͳ ����   
        }
        catch (Exception e){}
        //        Runtime run = Runtime.getRuntime(); //������Ӧ�ó�����ص�����ʱ����
        //        // �����Ѿ��Թ�������ʹ��try-catch�ṹ����
        //        try {   
        //            Process p = run.exec(startVergilcmd);// ������һ��������ִ�� ָ����ϵͳ ����   
        //            BufferedInputStream in = new BufferedInputStream(p.getInputStream());   
        //            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));   
        //            String lineStr;   
        //            while ((lineStr = inBr.readLine()) != null)   
        //                //�������ִ�к��ڿ���̨�������Ϣ   
        //                // ����̨�������Ϣ����˵������û�а�װ��Ptolemy II����û��ΪPtolemy II���úû�������
        //
        //                System.out.println(lineStr);// ��ӡ�����Ϣ   
        //            //��������Ƿ�ִ��ʧ�ܡ�   
        //            if (p.waitFor() != 0) {   
        //                if (p.exitValue() == 1)//p.exitValue()==0��ʾ����������1������������ 
        //                    JOptionPane.showMessageDialog(mainFrame, 
        //                            lineStr + "!" + "�������ִ��������ֿ��ܣ���1������û�а�װPtomely II��\n;"
        //                                    + "��2����������ȷ��װ��Ptolemy II������û��Ϊ�����ú�ϵͳ��������", 
        //                                    "�޷�����Vergil", 
        //                                    JOptionPane.ERROR_MESSAGE);
        //                System.err.println("����ִ��ʧ��!  ");   
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