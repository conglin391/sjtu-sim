package winSurface;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.event.*;

import ptolemy.plot.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*; // java���ݽṹ�İ����õ����е� arrayList

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

    static ArrayList _aryListVariableName = null; // ������¼һ�η�����̣��漰����ȫ����������
    static ArrayList _aryListTabbedPanel = null; // �������ѡ��Ķ�̬����
    static ArrayList _aryListPlotPanel = null; // �������ÿ��ѡ��е�Plot��ͼ���
    static boolean _bSimulationBegin = false; // ����ָʾ��ǰʱ�䣬�����Ƿ��з���������


    public WinMain()
    {
        // ���Ա�����ĳ�ʼ��
        _aryListVariableName = new ArrayList();
        _aryListTabbedPanel = new ArrayList();
        _aryListPlotPanel = new ArrayList();
        _bSimulationBegin = false;


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
                            }
                            else
                            {
                                return; // �¼����������������������µķ���
                            }                            
                        } //  if( _bSimulationBegin == true )

                        // _startNewSimulationInVergil(startVergilcmd);
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
                            // e.printStackTrace();   
                        }
                        
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

        
        // ���濪ʼ�������2�����ϵ�Plot��壬�����кܴ������

        JPanel panelPlot = new JPanel();
        panelPlot.setLayout(new BorderLayout());
        Plot testPlot = new Plot();
        panelPlot.add(testPlot, BorderLayout.CENTER);
        
        mainTabbedPane.addTab("��ͼ�������", panelPlot);
        mainTabbedPane.setEnabledAt(2,true);
        // mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);
        
        // testPlot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT); // û��Ч��
        // testPlot.setPlotRectangle(new Rectangle(0,0,WIN_WIDTH,WIN_HEIGHT) ); // ��Ч
        testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // ��Ч��������Ϊ��λ��ȷ����ͼ����ķ�Χ
        testPlot.setButtons(true);// ��Ч���ǳ���Ҫ
        testPlot.addPoint(1, 0.4, 0.5, true); // ��Ч���д��о�
        testPlot.addPoint(1, 0.9, 10, true);
        testPlot.addPoint(0, 1.0, 15, true);
        testPlot.addPoint(0, 1.5, -6.0, true);
        testPlot.fillPlot();


        /*JPanel panelPlot1 = new JPanel();
        mainTabbedPane.addTab("��ͼ�������1", panelPlot1);
        mainTabbedPane.setEnabledAt(mainTabbedPane.getTabCount()-1,true);

        Plot testPlot1 = new Plot();
        panelPlot1.add(testPlot1);
        // testPlot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT); // û��Ч��
        // testPlot.setPlotRectangle(new Rectangle(0,0,WIN_WIDTH,WIN_HEIGHT) ); // ��Ч
        testPlot1.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // ��Ч��������Ϊ��λ��ȷ����ͼ����ķ�Χ
        testPlot1.setButtons(true);// ��Ч���ǳ���Ҫ
        testPlot1.addPoint(1, 0.4, 0.5, true); // ��Ч���д��о�
        testPlot1.addPoint(1, 0.9, 10, true);
        testPlot1.addPoint(1, 1.0, 15, true);
        testPlot1.fillPlot();   */    

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
            server = new ServerSocket(6666); // �ڶ˿�6666�ϴ���������Socket����

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
        // <���-����ʱ��-(��������1,��ֵ1)-(��������2,��ֵ2)-(��������3,��ֵ3)-...-(��������n,��ֵn)>

        // �Ƚ��в������
        if( socketMessage == null || socketMessage.isEmpty() )
        {
            return;
        }
        //���Լ��һ�¿ͻ���Socket��Ϣ�ĸ�ʽ�Ƿ���ȷ�� if����˵����Ϣ��"<"��ͷ����">"��β
        if( socketMessage.endsWith(">") && socketMessage.startsWith("<") )
        {
            int iLoopCount = _get_loop_count(socketMessage);
            if( iLoopCount >= 1 ) // socket��Ϣ�����ݼ�ѭ�������Ϸ�
            {
                if( iLoopCount == 1 ) // ���η�����̷��͵ĵ�һ��socket��Ϣ
                {
                    // ���濪ʼ��һЩ��ʼ���Ĺ���
                    _clearOldSimulation(); // ����ɵķ�����Ϣ

                    _aryListVariableName = _getPointNameAryList(socketMessage);

                    if( _aryListVariableName.isEmpty() || _aryListVariableName == null)
                    {
                        // ��������Ի���
                        JOptionPane.showInternalMessageDialog(mainFrame, 
                                "�µķ������û�пɼ��ı�����������ķ���ģ�ͣ�", 
                                "�����޷����������⣡", JOptionPane.ERROR_MESSAGE);
                        return; // ���������Ϣ����
                    }
                    else
                    {

                    }
                }
            }
            else // socket��Ϣ�����ݼ�ѭ���������Ϸ�
            {
                //���ݳ����쳣�����Կ�����������־�ļ�����ʽ���Ų����
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
            // ˵����η���֮ǰ���Ѿ����ڷ������,������з��������Plot���
        }

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
