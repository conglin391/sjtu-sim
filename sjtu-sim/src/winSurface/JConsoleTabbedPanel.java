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
     * �������Ӧ������̨��ѡ��������ﴴ���Ͳ��ظ�����ť�������ı���ȣ��԰�ť����Ӧ�Լ�����������ĸ���Ҳ��������ʵ��
     * ���ڷ���������ĸ��£�����һ���ӿڣ�����Ϊ��
     * public void setProcess(double pro)��
     * ���⣬����һ����ʼ������и������ۣ�״�壩�Ľӿ�  public void initComponents() ���������ʹ������һ���������ʱ
     * ǿ���Եؿ�ʼ�µķ��棬��ʱ����������� _clearOldSimulation()�У�
     * ��Ҫ��������ӿ������ý�������״̬�Լ���������������ͣ������ֹͣ����ť��״̬��
     * */
    private JFrame _mainFrame; // ���ڴ�Ž����������ָ�룬�Ա����¼���Ӧ�����е����Ի���Ĵ��봦����
    private WinMain _pWinMain; // ���������ָ��

    private JTextField SimTime;
    private Choice choiceMatlabmdlPath, choicePTmdlPath,choicePTXMLPath;
    private JTextField progressNum;
    private JProgressBar progressBar;
    private JButton button_start;
    private JButton button_pause;
    private JButton button_stop;

    public JConsoleTabbedPanel(JFrame mainFrame, WinMain pWinMain) {
        // TODO Auto-generated constructor stub

        // ���Ա��������ֵ
        _mainFrame = mainFrame;
        _pWinMain = pWinMain;

        // ��ʼ����������ť���ı���
        /**
         * ��������ǡ���ʼ��ѡ�����ĸ������ add by -ZH
         * */


        setLayout(null);

        JPanel panel_sim = new JPanel();
        panel_sim.setToolTipText("");
        panel_sim.setBounds(85, 28, 696, 75);
        add(panel_sim);
        panel_sim.setLayout(null);

        button_start = new JButton("��������");
        button_start.setIcon(new ImageIcon(WinMain.class
                .getResource("/winSurface/UIResource/start.jpg")));
        button_start.setBounds(79, 10, 28, 23);
        button_start.setEnabled(false); // ��ʼ������ť������
        panel_sim.add(button_start);
        button_start.addActionListener(this);

        button_pause = new JButton("��ͣ����");
        button_pause.setIcon(new ImageIcon(WinMain.class
                .getResource("/winSurface/UIResource/pause.jpg")));
        button_pause.setBounds(132, 10, 28, 23);
        button_pause.setEnabled(false); // ��ť�ձ����ص�������ʱ������ͣ�������ã���Ϊ��û�з���������
        button_pause.setVisible(false); // ***�����ص������ť����� ��ͣ\���� ����Ŀǰû�п��еĽ��������׽����******
        panel_sim.add(button_pause);

        button_stop = new JButton("ֹͣ����");
        button_stop.setIcon(new ImageIcon(WinMain.class
                .getResource("/winSurface/UIResource/stop.jpg")));
        button_stop.setBounds(184, 10, 28, 23);
        button_stop.setEnabled(false); // �ռ���ʱ����ֹͣ����ť������
        panel_sim.add(button_stop);

        SimTime = new JTextField();
        SimTime.setBounds(270, 11, 66, 21);
        panel_sim.add(SimTime);
        SimTime.setColumns(10);

        progressBar = new JProgressBar();

        progressBar.setBounds(158, 43, 487, 22);
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);

        panel_sim.add(progressBar);

        progressNum = new JTextField();
        progressNum.setText("95.9%");
        progressNum.setBounds(100, 43, 48, 21);
        panel_sim.add(progressNum);
        progressNum.setColumns(10);
        String progressnow = progressNum.getText().trim().replace("%", "");
        progressBar.setValue((int) Double.parseDouble(progressnow));

        choicePTXMLPath = new Choice();
        choicePTXMLPath.setBounds(365, 12, 172, 21);
        panel_sim.add(choicePTXMLPath);

        JButton btn_choicePTXML = new JButton("��PTģ��");
        btn_choicePTXML.setBounds(561, 10, 34, 23);
        btn_choicePTXML.addActionListener(this);
        panel_sim.add(btn_choicePTXML);

        JButton btnNewButton_1 = new JButton("��һ��");
        btnNewButton_1.setBounds(605, 10, 76, 23);
        panel_sim.add(btnNewButton_1);

        JTextPane textPane_3 = new JTextPane();
        textPane_3.setText("�������");
        textPane_3.setEditable(false);
        textPane_3.setBackground(SystemColor.menu);
        textPane_3.setBounds(27, 43, 63, 21);
        panel_sim.add(textPane_3);
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setProcess(12.3);
            }
        });

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 113, 879, 2);
        add(separator);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(0, 179, 879, 2);
        add(separator_1);

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(UIManager.getColor("Button.background"));
        textPane.setText("����");
        textPane.setBounds(10, 10, 49, 21);
        add(textPane);

        JTextPane textPane_1 = new JTextPane();
        textPane_1.setEditable(false);
        textPane_1.setText("��ģ");
        textPane_1.setBackground(SystemColor.menu);
        textPane_1.setBounds(10, 129, 49, 21);
        add(textPane_1);

        JTextPane textPane_2 = new JTextPane();
        textPane_2.setEditable(false);
        textPane_2.setText("����");
        textPane_2.setBackground(SystemColor.menu);
        textPane_2.setBounds(10, 191, 49, 21);
        add(textPane_2);

        JPanel panel_interaction = new JPanel();
        panel_interaction.setBounds(55, 203, 764, 138);
        add(panel_interaction);
        panel_interaction.setLayout(null);

        MFunctionPanel _mFunctionPanel = new MFunctionPanel();

        panel_interaction.add(_mFunctionPanel);

        JButton btn_openPT = new JButton("������ɢ����ģ��");
        btn_openPT.setBounds(69, 139, 136, 23);
        btn_openPT.addActionListener(this);
        add(btn_openPT);

        choicePTmdlPath = new Choice();
        choicePTmdlPath.addItem("Create New model");
        choicePTmdlPath.setBounds(215, 139, 172, 21);
        add(choicePTmdlPath);

        JButton btn_choicePTmdl = new JButton("ѡ��PTģ��");
        btn_choicePTmdl.addActionListener(this);
        btn_choicePTmdl.setBounds(393, 139, 32, 23);
        add(btn_choicePTmdl);

        JButton btn_openMatlab = new JButton("����������̬ģ��");
        btn_openMatlab.setBounds(469, 139, 136, 23);
        btn_openMatlab.addActionListener(this);
        add(btn_openMatlab);

        choiceMatlabmdlPath = new Choice();
        choiceMatlabmdlPath.setBounds(609, 141, 172, 21);
        choiceMatlabmdlPath.addItem("Create New model");
        add(choiceMatlabmdlPath);

        JButton btn_choiceMatlabmdl = new JButton("ѡ��Matlabģ��");

        btn_choiceMatlabmdl.setBounds(787, 139, 32, 23);
        add(btn_choiceMatlabmdl);
        btn_choiceMatlabmdl.addActionListener(this);

        /**
         * ��������ǡ���ʼ��ѡ�����ĸ������ add by -ZH
         * */


    }

    public void actionPerformed(ActionEvent e) {
        // ������ʵ�֡�������塱�и�����ť����Ӧ�¼�
        String cmd = e.getActionCommand();
        if (cmd.equals("ѡ��Matlabģ��")) {
            String filename = choiceMatlabMdl("mdl");
            if (filename.endsWith(".mdl")) {
                choiceMatlabmdlPath.addItem(filename);
                choiceMatlabmdlPath.select(filename);
            } else {
                JOptionPane.showMessageDialog(_mainFrame, "���� .mdl�ļ���������ѡ��",
                        "������ļ�����", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (cmd.equals("ѡ��PTģ��")) {
            String filename = choiceMatlabMdl("xml");
            if (filename.endsWith(".xml")) {
                choicePTmdlPath.addItem(filename);
                choicePTmdlPath.select(filename);
            } else {
                JOptionPane.showMessageDialog(_mainFrame, "���� .xml�ļ���������ѡ��",
                        "������ļ�����", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (cmd.equals("������ɢ����ģ��")) {
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
        if (cmd.equals("����������̬ģ��")) {
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
        if (cmd.equals("��PTģ��")) {
            String filename = choiceMatlabMdl("xml");
            if (filename.endsWith(".xml")) {
                choicePTXMLPath.addItem(filename);
                choicePTXMLPath.select(filename);
                
                // xml�ļ���Ч����ʱ�á��������桱��ť�����Ч, add by bruse, 2014-10-22
                button_start.setEnabled(true);
                
            } else {
                JOptionPane.showMessageDialog(_mainFrame, "���� .xml�ļ���������ѡ��",
                        "������ļ�����", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (cmd.equals("��������"))
        {
            // if( choicePTXMLPath.countItems() == 0) return;
            String fileName = choicePTXMLPath.getItem(0);
            if( fileName.endsWith("xml") ) // ѡ�е�ȷʵ��xml�ļ�
            {
                // ��ѡ�е�xml�ļ����� Vergil����������
                //���·���ո����⣬����˫����  -ZH
                String startVergilcmd = "vergil -run \"" + fileName +"\"";

                // ����Ƿ��Ѿ��з����ڽ���
                if( _pWinMain.getSimulationStatus() == true )
                {
                    // �����Ի�����ʾ�û����Ƿ������ǰ���棬�������µķ���
                    int iStartNewSim = JOptionPane.showConfirmDialog(_mainFrame, 
                            "Ŀǰϵͳ�Ѿ��з�����������У��Ƿ������ǰ������򲢿����µķ���", 
                            "�Ƿ������ǰ�������", 
                            JOptionPane.YES_NO_OPTION);
                    if( iStartNewSim == JOptionPane.YES_OPTION )
                    {
                        try {
                            
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
                        
                        _pWinMain.setSimulationStatus(false); // ���ý��沢û��������ʼ���з���
                        
                        button_start.setEnabled(false); // ���������桱��ť������
                        button_stop.setEnabled(true); // ���������桱��ť����

                    }
                    else
                    {
                        return; // �¼����������������������µķ���
                    }                            
                } //  if( _bSimulationBegin == true )
              
                try
                {
                    Runtime.getRuntime().exec(startVergilcmd);
                }
                catch(Exception e1)
                {}              

            } // if( fileName.endsWith("xml") ) // ѡ�е�ȷʵ��xml�ļ�
        }
        if (cmd.equals("ֹͣ����"))
        {
            // ֹͣ������̣�Ŀǰ����ֱ���� windows��taskkill����ǿ�ƹر�PT��matlab
            if( _pWinMain.getSimulationStatus() == true ) // ȷʵ�з���������
            {
                // ��taskkillɱ������
                try {

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
                
                // ������ķ������б�־����Ϊfalse����ʾ���Կ����µķ���
                _pWinMain.setSimulationStatus(false);
                
                button_start.setEnabled(true); // ����������ť���ã���ʾ���Կ�ʼ�µķ���
                button_stop.setEnabled(false); // �������ɲ����ã���Ϊû�з���������
            }
            else
            {
                // û�з��������У�����ֹͣ���桱��ť���ã��������ش��󣬿���������־�ļ�
            }
        }
    }

    private String choiceMatlabMdl(String type) {
        // ����һ���ļ�ѡ��Ի��򣬹��û�ѡ�� �ļ�
        JFileChooser fileChooser = new JFileChooser();
        // �����ļ�����ѡ�� -ZH
        fileChooser.addChoosableFileFilter(new JAVAFileFilter(type));
        File file = null;
        int iResult = 0;
        fileChooser.setApproveButtonText("ȷ��");
        fileChooser.setDialogTitle("���ļ�");
        iResult = fileChooser.showOpenDialog(_mainFrame);
        /*
         * ���û���ѡ���ļ� ���Ұ���"ȷ��"��ť�󣬾Ϳ���ͨ�� getSelectedFile() ����ȡ���ļ�����
         */
        if (iResult == JFileChooser.APPROVE_OPTION) {
            // �û����µġ�ȷ������ť
            file = fileChooser.getSelectedFile();
            System.out.println("ѡ����ļ�����" + file.getName());
            System.out.println("ѡ����ļ�����" + file.getAbsolutePath());

            String fileName = file.getName();
            return file.getAbsolutePath();

        }
        return null;

    }


    public void setProcess(double pro) {
        progressNum.setText(pro + "%");
        String progressnow = progressNum.getText().trim().replace("%", "");
        progressBar.setValue((int) Double.parseDouble(progressnow));

    }

    public void initComponents() {

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
