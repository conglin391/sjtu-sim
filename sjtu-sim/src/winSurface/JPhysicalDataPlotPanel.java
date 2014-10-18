package winSurface;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class JPhysicalDataPlotPanel extends JPanel {
    
    // 这是为呈现多个仿真plot画面而设计的panel
    
    private ArrayList <JPanel>_aryListPlotPanel = new ArrayList();
    private ArrayList <JButton>_aryListButton = new ArrayList();
    private CardLayout cardLayout = new CardLayout();
    
    private JPanel _mainPlotPanel = new JPanel(cardLayout); // 创建承载多个“卡片”面板的panel,它的布局就是CardLayout
    
    private JPanel _buttonPanel = new JPanel(); // 创建承载多个按钮的面板

    public JPhysicalDataPlotPanel() {
        // TODO Auto-generated constructor stub
        
        super();
        
        setLayout(new BorderLayout());
                
        add(_mainPlotPanel,BorderLayout.CENTER);
        add(_buttonPanel,BorderLayout.SOUTH);
        
        JButton buttonPre = new JButton("上一页");
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
        
        
        JButton b = new JButton(String.valueOf(iButtonNum)); // 显示第几页的按钮
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
        // 先创建好“下一页”按钮
        JButton buttonNext = new JButton("下一页");
        buttonNext.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cardLayout.next(_mainPlotPanel);
            }
        });
        
        _aryListButton.add(buttonNext);
        
        // 开始把 _aryListButton 中的按钮添加到 _buttonPanel面板中
        for(int i = 0; i < _aryListButton.size(); ++i)
        {
            _buttonPanel.add(_aryListButton.get(i));
        }
    }

}
