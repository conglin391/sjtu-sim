package winSurface;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class JPhysicalDataPlotPanel extends JPanel {
    
    // ����Ϊ���ֶ������plot�������Ƶ�panel
    
    private ArrayList <JPanel>_aryListPlotPanel = new ArrayList();
    private ArrayList <JButton>_aryListButton = new ArrayList();
    private CardLayout cardLayout = new CardLayout();
    
    private JPanel _mainPlotPanel = new JPanel(cardLayout); // �������ض������Ƭ������panel,���Ĳ��־���CardLayout
    
    private JPanel _buttonPanel = new JPanel(); // �������ض����ť�����

    public JPhysicalDataPlotPanel() {
        // TODO Auto-generated constructor stub
        
        super();
        
        setLayout(new BorderLayout());
                
        add(_mainPlotPanel,BorderLayout.CENTER);
        add(_buttonPanel,BorderLayout.SOUTH);
        
        JButton buttonPre = new JButton("��һҳ");
        buttonPre.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cardLayout.previous(_mainPlotPanel);
            }
        });
        
        _aryListButton.add(buttonPre);
        
    }

    public JPhysicalDataPlotPanel(LayoutManager arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public JPhysicalDataPlotPanel(boolean arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public JPhysicalDataPlotPanel(LayoutManager arg0, boolean arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    
    public void addOneCard(JPanel oneCardPanel)
    {
        _aryListPlotPanel.add(oneCardPanel);
        int iButtonNum = _aryListPlotPanel.size();
        _mainPlotPanel.add(oneCardPanel, String.valueOf(iButtonNum));
        
        
        JButton b = new JButton(String.valueOf(iButtonNum)); // ��ʾ�ڼ�ҳ�İ�ť
        b.addActionListener(new ActionListener()
        {
            int iButtonNum = _aryListPlotPanel.size();
            public void actionPerformed(ActionEvent e)
            {
                cardLayout.show(_mainPlotPanel,String.valueOf(iButtonNum));
            }
        });
        
        _aryListButton.add(b);
    }
    
    public void showButtons()
    {
        // �ȴ����á���һҳ����ť
        JButton buttonNext = new JButton("��һҳ");
        buttonNext.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cardLayout.next(_mainPlotPanel);
            }
        });
        
        _aryListButton.add(buttonNext);
        
        // ��ʼ�� _aryListButton �еİ�ť��ӵ� _buttonPanel�����
        for(int i = 0; i < _aryListButton.size(); ++i)
        {
            _buttonPanel.add(_aryListButton.get(i));
        }
    }

}
