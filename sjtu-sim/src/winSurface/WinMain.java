package winSurface;
import javax.swing.*;

import java.util.regex.Pattern;
import java.awt.*;
import java.awt.event.*;

import javax.swing.event.*;

import ptolemy.plot.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*; // java���ݽṹ�İ����õ����е� ArrayList

public class WinMain {

    /****************************************************************
     *******************   private members *************************/

    private static final long serialVersionID = 1L;
    static final int WIN_WIDTH = 900; // ���ڵĳ�
    static final int WIN_HEIGHT = 450; // ���ڵĿ�
    static JFrame mainFrame = null; // ���������ڱ���

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
    
    static int _iPlotNumber = 0; // ������¼��ǰ������򹲲����˼���Plotѡ����
    
    static ArrayList _aryListTabbedPanel = null; // �������ѡ��Ķ�̬����
    static ArrayList _aryListPlotPanel = null; // �������ÿ��ѡ��е�Plot��ͼ���
    static boolean _bSimulationBegin = false; // ����ָʾ��ǰʱ�䣬�����Ƿ��з���������
    
    static int _iLoopCount = 0;


    public WinMain()
    {
        // ���Ա�����ĳ�ʼ��
        _aryListVariableName = new ArrayList();
        _aryListVariableValue = new ArrayList();
        
        _aryListDataSets = new ArrayList();
        
        _iPlotNumber = 0;
        
        _aryListTabbedPanel = new ArrayList();
        _aryListPlotPanel = new ArrayList();
        _bSimulationBegin = false;
        
        _iLoopCount = 0;


        // ���濪ʼ���������洰�ڣ����ڴ�������Ӻø����˵���
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){}
        mainFrame = new JFrame("SimJ&M"); // �������򴰿ڣ����ڱ���Ϊ SimJ&M
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true); // Ĭ��Ϊfalse
        mainFrame.setSize(WIN_WIDTH, WIN_HEIGHT); // ���ô��ڵĳߴ�
        // ��ȡ��Ļ��С�������ô��ڳ�ʼ����ʾλ��
        Toolkit kit= Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int x = (screenSize.width - WIN_WIDTH)/2;
        int y = (screenSize.height - WIN_HEIGHT)/2;
        mainFrame.setLocation(x, y); // ���ô��ڳ���ʱ������Ļ������

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
        // Ϊ���򿪡�����˵��� �趨 ��ݼ� --- Ctrl + O
        menuItemOpenInFile.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemOpenInFile); // ������˵�����ӵ����ļ����˵���
        menuFile.addSeparator();

        JMenuItem menuItemOpenMATALBInFile = new JMenuItem("����MATALB");
        menuFile.add(menuItemOpenMATALBInFile);

        JMenuItem menuItemOpenPTInFile = new JMenuItem("���� Vergil");
        menuFile.add(menuItemOpenPTInFile);

        menuFile.addSeparator();
        JMenuItem menuItemExitInFile = new JMenuItem("�˳�����(X)");
        menuItemExitInFile.setAccelerator(KeyStroke.getKeyStroke('X', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemExitInFile);        

        JMenuItem menuItemGetHelpInHelp = new JMenuItem("ʹ��˵��");
        menuHelp.add(menuItemGetHelpInHelp);
        menuHelp.addSeparator();

        JMenuItem menuItemInfoInHelp = new JMenuItem("����SimJ&M");
        menuHelp.add(menuItemInfoInHelp);


        // ���濪ʼ����ѡ���壬������һ������ʼ��ѡ����뵽ѡ������ȥ
        mainTabbedPane = new JTabbedPane(); // �������ڼ��ص�ѡ����
        mainFrame.setContentPane(mainTabbedPane); // ���ô��ڵ���ʾ���Ϊ �����ѡ����
        mainTabbedPane.setVisible(true); // �������ѡ����ɼ�
        // ����ѡ����Ĵ�С����ѡ���λ�÷���
        //mainTabbedPane.setPreferredSize(new Dimension(500,200));
        mainTabbedPane.setTabPlacement(JTabbedPane.TOP);
        // ����ѡ��������ڵ���ʾ��ʽ
        mainTabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        //mainTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // mainFrame.pack(); // ����������Ӧ����Ĵ�С

        // ���ѡ���������������ÿ��ѡ��ı�ǩ�Լ����Ƿ����
        JPanel panel0 = new JPanel();
        mainTabbedPane.addTab("��ʼ",panel0); // ������ʼ����� panel0 ��Ϊ��һ��ѡ�
        mainTabbedPane.setEnabledAt(0,true);


        // �������Ϲ����������Ĵ��ڣ�ѡ�����Լ��˵���������ϣ����濪ʼ���ÿ���˵��������Ӧ���¼��������
        menuItemOpenInFile.addActionListener(new ActionListener()
        {
            /* ���򿪡��ļ��˵���Ķ����¼�������������˵���ʱ���ᵯ���ļ�ѡ��Ի���Ŀ�������û�ѡ�� .xml ģ���ļ�
             * ���û�ѡ����ļ�֮�󣬻�ȡ��ѡ�е��ļ�·��ȫ�ƣ������ݸ� Ptolemy II���������� 
             * */
            public void actionPerformed(ActionEvent Event)
            {
                // ����һ���ļ�ѡ��Ի��򣬹��û�ѡ�� .xml �ļ�
                JFileChooser fileChooser = new JFileChooser("D:\\");
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
                        //String startVergilcmd = "vergil -run E:\\PT_workspace\\pvbattery_50-60_org.xml";
                        String startVergilcmd = "vergil -run " + file.getAbsolutePath();

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
                                _bSimulationBegin = false;
                                
                                /******************  ����������������������������������������������
                                 * �����������������и����⣬��ιر��Ѿ����е�pt��matlab����������������������������
                                 * ������ܹرվɵ�pt��matlab�����¿����ķ�����򣬿�����socket��Ϣ�������ܵ��ͳ����͵���Ϣ�ĸ���
                                 * *****************/
                            }
                            else
                            {
                                return; // �¼����������������������µķ���
                            }                            
                        } //  if( _bSimulationBegin == true )

                        _startNewSimulationInVergil(startVergilcmd);
                        
                        
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
        }); // menuItemOpenInFile.addActionListener(new ActionListener()

    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new WinMain(); // ������������

        // �����������������ܷ�̬�� mainTabbedPane����µ�ѡ�

        JPanel panel_1 = new JPanel();       
        mainTabbedPane.addTab("�����������",panel_1); 
        mainTabbedPane.setEnabledAt(1,true);
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
                    ���ͻ�����6666�˿�������ʱ��������Socket�Ϳͻ��˵�Socket��������*/
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
                System.out.println("�ͻ��ˣ�" + str_tem); // ��ʾ�ַ���    

                // ���濪ʼ�ص㣬����ͻ��˷��͵������ַ��������ж�ȡ��: 
                // ��1�����ݼ��ı�Σ������ͻ��˵ڼ�����������������ݼ�
                // ��2������ʱ��
                // ��3���������������ݵ�
                // �ͻ��˷��͵���Ϣ��ʽ���¡���
                // <���-����ʱ��-(��������1,��ֵ1)-(��������2,��ֵ2)-(��������3,��ֵ3)-...-(��������n,��ֵn)>
                String str_dataset = str_tem.trim();

                _handleSocketMessage(str_dataset);                  

                // �ر����socket����
                bfreader.close();
                //ps.close();
                fIn.close();
                //fOut.close();
                socket.close();
                /* ����������������ֵ��ע�⣬ServerSocket����server ��û�б��رգ����������� */

                if( str_tem == "close" ) 
                { 
                    // �����Եظ���һ��berak��֧��䣬������������ڿ��������ô�������pt����matlab���Է��������close����Ϣ��
                    // ���棬��֪ͨ����ر�Socket������̣����ҿ���������������еķ�����Ϣ
                    break; 
                }
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
     *******************   private methods *************************/
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
        boolean b = Pattern.matches(
                "^<[0-9]+\\-[0-9]+\\.?[0-9]*(\\-\\[\\(.+,.+\\)(\\-\\(.+,.+\\))*\\])+>$",
                socketMessage);
        if(b == true) // ��Ϣ��ʽ��ȷ�����Խ�����һ���Ĵ�����
        {
            int iLoopCount = _getLoopCount(socketMessage);
            if( iLoopCount > _iLoopCount ) // socket��Ϣ�����ݼ�ѭ�������Ϸ�
            {
                if( iLoopCount == 1 && _bSimulationBegin == false) 
                {
                    // ���η�����̷��͵ĵ�һ��socket��Ϣ������û�з��������ִ�л��߿��Կ����µķ������
                    _handleFirstSocket(socketMessage);
                    
                }
                else
                {
                    // ���������socket��Ϣ����̬�������ݵ㲢��ӵ�plot�����ȥ����
                    _handleNextSocket(socketMessage);
                }
            }
            else // socket��Ϣ�����ݼ�ѭ���������Ϸ�
            {
                //���ݳ����쳣�����Կ�����������־�ļ�����ʽ���Ų����
                return;
            }

        } //  if( socketMessage.endsWith(">") && socketMessage.startsWith("<") )
    }
    
    static private void _handleFirstSocket( String socketMessage)
    {
        // ���η�����̷��͵ĵ�һ��socket��Ϣ������û�з��������ִ�л��߿��Կ����µķ������
        
        // �Ƚ��в������
        if(socketMessage.isEmpty())
        {
            return;
        }
        
        // ���濪ʼ��һЩ��ʼ���Ĺ���
        _clearOldSimulation(); // ����ɵķ�����Ϣ
     
        // ��ȡsocket��Ϣ�а��������ݵ����ƺ���ֵ��
        // ���� ���� _aryListVariableName �� _aryListVariableValue ������Ա��������Ϊ���ǵ�������������ڣ�
        // ���������Խ��ܵ������ݵ���ȷ�ԡ������Խ��� ���У��
        _aryListVariableName = _getPointNameAryList(socketMessage);  // ��ȡ��Ϣ�а��������б�������
        _aryListVariableValue = _getPointValueAryList(socketMessage); // ��ȡ��Ϣ�а��������б�������ֵ
        
        
        // ��ȡsocket��Ϣ�е����ݼ�
        ArrayList aryListDataSets = _getDataSets(socketMessage);
        
        if( aryListDataSets.isEmpty() || aryListDataSets.isEmpty() )
        {
            // ��������Ի���
            JOptionPane.showInternalMessageDialog(mainFrame, 
                    "�µķ������û�м�⵽����������������ķ���ģ�ͣ�", 
                    "�����޷�������ɹ���⣡", JOptionPane.ERROR_MESSAGE);
            return; // ���������Ϣ����
        }
        else // if( _aryListVariableName.isEmpty() || _aryListVariableName == null)
        {
            // ���һ�»�ȡ�����ݼ��Ƿ���ȷ����
            if( !_validateDataSet(aryListDataSets, true) )
            {
                // ���ݼ����Ϸ����˳������Ϣ����
                return;
            }

            // ����һ��socket��Ϣ���ݼ����浽��ľ�̬�б���
            _aryListDataSets = aryListDataSets;
            
            double dSimulationTime = _getSimulationTime(socketMessage);

            // ��Ϣ��ȷ����ʼ���ÿ���������ݼ�����Ӻ�plot��ͼѡ�
            for(int i = 0; i < aryListDataSets.size(); ++i)
            {
                
                // ������һ��Plot��壬�Լ���������JPanel
                JPanel panelPlot = new JPanel();
                panelPlot.setLayout(new BorderLayout());
                Plot testPlot = new Plot();
                
                // �������õ� panelPlot �� testPlot ��ӵ� ����ľ�̬�б���
                _aryListTabbedPanel.add(panelPlot);
                _aryListPlotPanel.add(testPlot);
                
                ((JPanel)_aryListTabbedPanel.get(i)).add((Plot)_aryListPlotPanel.get(i)
                        , BorderLayout.CENTER);
                // testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // ��Ч��������Ϊ��λ��ȷ����ͼ����ķ�Χ
                ((Plot)_aryListPlotPanel.get(i)).setButtons(true);// ��Ч���ǳ���Ҫ
                
                
                
                String tabTitle = ""; // �ɱ���������ɵ�ѡ�����
                Vector vcCaption = new Vector(); // ���ڴ��ÿ�����������ƣ�������ÿ�����ߵı�ʾʱʹ��
                
                ArrayList aryListDataSet = (ArrayList)_aryListDataSets.get(i);
                ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
                ArrayList aryListPointValue = (ArrayList)aryListDataSet.get(1);
                for(int k = 0; k < aryListPointName.size(); ++k)
                {
                    vcCaption.add( (String)aryListPointName.get(k) );
                    tabTitle = tabTitle + "," + (String)aryListPointName.get(k);
                    ((Plot)_aryListPlotPanel.get(i)).addPoint(k, 
                            dSimulationTime, 
                            (double)aryListPointValue.get(k), 
                            true);                   
                    //
                }
                
                //((Plot)_aryListPlotPanel.get(i)).setCaptions(vcCaption); // ���н����ʾ��
                // �������ֻ���������²�����˼��б��⣬������������߽�����ӣ��д��Ľ�
                
                
                ((Plot)_aryListPlotPanel.get(i)).fillPlot(); // �ػ�ͼ��
                
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
                mainTabbedPane.addTab( tabTitle , (JPanel)_aryListTabbedPanel.get(i));
                _iPlotNumber++;
                mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);
            }
            
            // ���µ�ǰ��ľ�̬������ѭ��������������Ϊ1
            _iLoopCount = 1;
            
            // �����Ѵ��ڷ�����̱�־
            _bSimulationBegin = true;          
        }
    }
    
    static private void _handleNextSocket(String socketMessage)
    {
        // �Ƚ��в������
        if(socketMessage.isEmpty())
        {
            return;
        }
        
        // ���������socket��Ϣ����̬�������ݵ㲢��ӵ�plot�����ȥ����
        int iLoopCount = _getLoopCount(socketMessage);
        if( iLoopCount > _iLoopCount ) // ����socket��Ϣ��ѭ�������Ϸ�������η���ĺ���ѭ�������Ϣ
        {
            // ��ȡ���socket��Ϣ�����ݼ�
            ArrayList aryListDataSets = _getDataSets(socketMessage);
            
            // ������ݵĺϷ��ԣ�����ʱ�Ŵ��������Ϣ
            if( _validateDataSet(aryListDataSets, false) )
            {
                // ���ݺ�����ʼ��ÿ��Plot�����������ݵ�
                double dSimulationTime = _getSimulationTime(socketMessage);
                for(int i = 0; i < aryListDataSets.size(); ++i)
                {
                    ArrayList aryListDataSet = (ArrayList)aryListDataSets.get(i);
                    ArrayList aryListPointName = (ArrayList)aryListDataSet.get(0);
                    ArrayList aryListPointValue = (ArrayList)aryListDataSet.get(1);
                    
                    for(int k = 0; k < aryListPointName.size(); ++k)
                    {
                        ((Plot)_aryListPlotPanel.get(i)).addPoint(k, 
                                dSimulationTime, 
                                (double)aryListPointValue.get(k), 
                                true);
                        
                        //
                    }
                    
                   ((Plot)_aryListPlotPanel.get(i)).fillPlot(); // �ػ�ͼ��
                }
                // ���µ�ǰ��ľ�̬ѭ����������
                _iLoopCount = iLoopCount;
            }          
        }             
    }
    
    static private void _clearOldSimulation()
    {
        if( _iPlotNumber > 0 ) // ˵����η���֮ǰ���Ѿ����ڷ������,������з��������Plot���
        {
            // ��mainTabbedPane������ɾ������Plotѡ����
            for(int i = 0; i < _iPlotNumber; ++i)
            {
                mainTabbedPane.remove( mainTabbedPane.getTabCount() - 1 );
                
            }
        }
        
        // ��ʼ����ľ�̬�������б������
        _iPlotNumber = 0; // ���plotѡ���Ŀ
        _aryListTabbedPanel.clear(); // ���װ��Plot����JPanel�������
        _aryListPlotPanel.clear(); // ���Plot�������
        _aryListVariableName.clear(); // ��ձ������б�
        _aryListVariableValue.clear(); // ��ձ���ֵ�б�
        _aryListDataSets.clear(); // ������ݼ��б�
        _bSimulationBegin = false;
        _iLoopCount = 0;
    }
    

    static private int _getLoopCount(String str)
    {
        // �Ƚ��в������
        if( str == null || str.isEmpty() )
        {
            return -1;
        }
        
        // ��ʼ��ȡ�����ѭ����������iteration������ֵ
        int i = str.indexOf('-', 0);
        String loopCount = str.substring(1, i);
        
        int iLoopCount = Integer.valueOf(loopCount);
        
        // ������������
        System.out.println("loop count is :" + iLoopCount);
                
        return iLoopCount;
    }
    static private double _getSimulationTime(String str)
    {
        // �Ƚ��в������
        if( str == null || str.isEmpty() )
        {
            return -1;
        }
        int beginIndex = str.indexOf('-') + 1;
        int endIndex = str.indexOf('-', beginIndex);
        if(endIndex == beginIndex) // ��ʾ����ʱ����ַ���Ϊ�գ����쳣
        {
            // �������쳣�����Կ���������־�ļ�
            return 0.0;
        }
        String strSimuTime = str.substring(beginIndex, endIndex);
        double dSimuTime = Double.valueOf(strSimuTime);
        
        // ������������
        System.out.println("simulation time is : " + dSimuTime);
        
        return dSimuTime;
    }
    
    static private ArrayList _getPointNameAryList(String str)
    {
        ArrayList aryListVariableName = new ArrayList();
        // �Ƚ��в������
        if( str == null || str.isEmpty() )
        {
            return aryListVariableName;
        }
        // ������� aryListVariableName
        aryListVariableName.clear();
        int iBeginIndex = str.indexOf('(') + 1; // �ҵ���һ�����ݵ� ���ƿ�ʼ��λ��
        int iEndIndex = 0;
        
        while( (iEndIndex = str.indexOf(',', iBeginIndex)) != -1)
        {
            String strVariableName = str.substring(iBeginIndex, iEndIndex); // ��ȡ��ǰ���������
            aryListVariableName.add(strVariableName); // ������������Ʒ��� aryListVariableName��
            
            // ��ȡ��һ���� �������ƵĿ�ʼλ��
            iBeginIndex = str.indexOf('(', iEndIndex);
            if( iBeginIndex == -1) // �Ѿ����������һ���㣬�����˳�ѭ��
            {
                break;
            }
            iBeginIndex = iBeginIndex + 1;
        }
        
        // ����������
        for(int i = 0; i < aryListVariableName.size(); ++i)
        {
            System.out.println("��" + i + "���������ƣ� " + aryListVariableName.get(i));
        }
        return aryListVariableName;
    }
    
    static private ArrayList _getPointValueAryList(String str)
    {
        ArrayList aryListVariableValue = new ArrayList();
        // �Ƚ��в������
        if( str == null || str.isEmpty() )
        {
            return aryListVariableValue;
        }
        
        // ������� aryListVariableValue
        aryListVariableValue.clear();
        
        int iBeginIndex = str.indexOf(',') + 1; // Ѱ�ҵ�һ������ֵ��ʼ�ĵط�
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
        // ����������
        for(int i = 0; i < aryListVariableValue.size(); ++i)
        {
            System.out.println("��" + i + "���������ƣ� " + aryListVariableValue.get(i));
        }
        return aryListVariableValue;
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
        // ���ڶԻ�ȡ�������ݼ����м�⣬���ݼ������� true�����򷵻� false
        // �������
        if( aryListDataSets == null || aryListDataSets.isEmpty() )
        {
            return false;
        }
        
        if( bFirstSocketMessage == true )
        {
            // Ŀǰֻ���aryListDataSetsÿ��Ԫ�������������ά���Ƿ���ȣ������ʱ�����쳣������false

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
        else //������������ݼ��Ϸ���
        {
            // �� aryListDataSets ͬ��ľ�̬�б� _aryListDataSets��Ƚϣ�ά����ͬ˵��aryListDataSets�Ϸ���
            // �� _aryListDataSets�ǵ�һ��socket��Ϣ��ȡ�õ������ݼ���������Ƕȿ��ǣ�
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
        Runtime run = Runtime.getRuntime(); //������Ӧ�ó�����ص�����ʱ����
        // �����Ѿ��Թ�������ʹ��try-catch�ṹ����
        try {   
            Process p = run.exec(startVergilcmd);// ������һ��������ִ�� ָ����ϵͳ ����   
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());   
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));   
            String lineStr;   
            while ((lineStr = inBr.readLine()) != null)   
                //�������ִ�к��ڿ���̨�������Ϣ   
                // ����̨�������Ϣ����˵������û�а�װ��Ptolemy II����û��ΪPtolemy II���úû�������

                System.out.println(lineStr);// ��ӡ�����Ϣ   
            //��������Ƿ�ִ��ʧ�ܡ�   
            if (p.waitFor() != 0) {   
                if (p.exitValue() == 1)//p.exitValue()==0��ʾ����������1������������ 
                    JOptionPane.showMessageDialog(mainFrame, 
                            lineStr + "!" + "�������ִ��������ֿ��ܣ���1������û�а�װPtomely II��\n;"
                                    + "��2����������ȷ��װ��Ptolemy II������û��Ϊ�����ú�ϵͳ��������", 
                                    "�޷�����Vergil", 
                                    JOptionPane.ERROR_MESSAGE);
                System.err.println("����ִ��ʧ��!  ");   
            }   
            inBr.close();   
            in.close();   
        } 
        catch (Exception e) {   
            e.printStackTrace();   
        }
    }

}
