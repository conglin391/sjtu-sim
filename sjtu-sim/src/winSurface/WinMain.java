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
    JFrame mainFrame = null; // ���������ڱ���
    // JRootPane rootPane = null; // ���򴰿�Ҫ���ص���壬��һ��������Ҫ��������ӿ��ӻ������
    // JLayeredPane layeredPane = null;
    JTabbedPane mainTabbedPane = null; // JRootPane���������Ҫ���ص�ѡ����
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
        
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuView);
        menuBar.add(menuTool);
        menuBar.add(menuHelp);
        
        JMenuItem menuItemOpenInFile = new JMenuItem("��(O)");
        // Ϊ���򿪡�����˵��� �趨 ��ݼ� --- Ctrl + O
        menuItemOpenInFile.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK, false));
        menuFile.add(menuItemOpenInFile); // ������˵�����ӵ����ļ����˵���
        
        JMenuItem menuItemOpenMATALBInFile = new JMenuItem("����MATALB");
        menuFile.add(menuItemOpenMATALBInFile);
        
        JMenuItem menuItemOpenPTInFile = new JMenuItem("���� Vergil");
        menuFile.add(menuItemOpenPTInFile);
        
        
        
        mainTabbedPane = new JTabbedPane(); // �������ڼ��ص�ѡ����
        mainFrame.setContentPane(mainTabbedPane); // ���ô��ڵ���ʾ���Ϊ �����ѡ����
        mainTabbedPane.setVisible(true); // �������ѡ����ɼ�
       
        // ���ѡ���������������ÿ��ѡ��ı�ǩ�Լ����Ƿ����
        JPanel panel0 = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        JPanel panel5 = new JPanel();
        JPanel panelPlot = new JPanel();
        
        mainTabbedPane.addTab("��ʼ",panel0); // ������ʼ����� panel0 ��Ϊ��һ��ѡ�
        mainTabbedPane.setEnabledAt(0,true);
        
        mainTabbedPane.addTab("��ѹ", panel1);
        mainTabbedPane.setEnabledAt(1, true);
        
        mainTabbedPane.addTab("�ܹ���", panel2);
        mainTabbedPane.setEnabledAt(2, true);
        
        mainTabbedPane.addTab("�������", panel3);
        mainTabbedPane.setEnabledAt(3, true);
        
        mainTabbedPane.addTab("�������", panel4);
        mainTabbedPane.setEnabledAt(4, true);
        
        mainTabbedPane.addTab("���ܹ���", panel5);
        mainTabbedPane.setEnabledAt(5, true);
        
        mainTabbedPane.addTab("��ͼ�������", panelPlot);
        mainTabbedPane.setEnabledAt(6,true);
        
        Plot testPlot = new Plot();
        panelPlot.add(testPlot);
        // testPlot.setBounds(0, 0, WIN_WIDTH, WIN_HEIGHT); // û��Ч��
        // testPlot.setPlotRectangle(new Rectangle(0,0,WIN_WIDTH,WIN_HEIGHT) ); // ��Ч
        testPlot.setSize(WIN_WIDTH-100, WIN_HEIGHT-100); // ��Ч��������Ϊ��λ��ȷ����ͼ����ķ�Χ
        testPlot.setButtons(true);// ��Ч���ǳ���Ҫ
        testPlot.addPoint(0, 0.4, 0.5, true); // ��Ч���д��о�
        testPlot.addPoint(0, 0.9, 10, true);
        testPlot.addPoint(0, 1.0, 15, true);
        testPlot.fillPlot();
        
        //testPlot.samplePlot();
        
        // ����ѡ����Ĵ�С����ѡ���λ�÷���
        mainTabbedPane.setPreferredSize(new Dimension(500,200));
        mainTabbedPane.setTabPlacement(JTabbedPane.TOP);
        // ����ѡ��������ڵ���ʾ��ʽ
        mainTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // mainFrame.pack(); // ����������Ӧ����Ĵ�С
        
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
                        // ������ļ����� Vergil����������
                        //String startVergilcmd = "vergil -run E:\\PT_workspace\\pvbattery_50-60_org.xml";
                        String startVergilcmd = "vergil -run " + file.getAbsolutePath();
                        
                        Runtime run = Runtime.getRuntime(); //������Ӧ�ó�����ص�����ʱ����   
                        try {   
                            Process p = run.exec(startVergilcmd);// ������һ��������ִ�� ָ����ϵͳ ����   
                            BufferedInputStream in = new BufferedInputStream(p.getInputStream());   
                            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));   
                            String lineStr;   
                            while ((lineStr = inBr.readLine()) != null)   
                                //�������ִ�к��ڿ���̨�������Ϣ   
                                System.out.println(lineStr);// ��ӡ�����Ϣ   
                                //��������Ƿ�ִ��ʧ�ܡ�   
                                if (p.waitFor() != 0) {   
                                    if (p.exitValue() == 1)//p.exitValue()==0��ʾ����������1������������   
                                       System.err.println("����ִ��ʧ��!  ");   
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
                    // ѡ�еĲ����� xml �ļ��������Ի�����ʾ�û�
                }
                
            }
        }); // menuItemOpenInFile.addActionListener(new ActionListener()
        
        
    
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new WinMain(); // ������������
    }    

}
