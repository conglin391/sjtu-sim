package winSurface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;

public class InteractionTablePanle implements ActionListener{
	private JTable c2ptable,p2ctable;
	DefaultTableModel dtm1,dtm2;
	/****************************
	*
	*提供两种接口，一种是同时插入两张表，一种是选择单独插入某张表（用1,2区分）
	*插入类型用object[3]
	*
	*
	*******************************/
	public void addOneLine(Object[] str1,Object[] str2){
		//输入两个Object[3]分别给两张表
		dtm1.addRow(str1);
		dtm2.addRow(str2);
		
		c2ptable.changeSelection(c2ptable.getRowCount() - 1, 0, false, false);
		p2ctable.changeSelection(p2ctable.getRowCount() - 1, 0, false, false);

	}
	
	public void addOneLine(Object[] str,int whichtable){
		if (whichtable==1){
			dtm1.addRow(str);
		}
		else if(whichtable==2){
			dtm2.addRow(str);

		}
		c2ptable.changeSelection(c2ptable.getRowCount() - 1, 0, false, false);
		p2ctable.changeSelection(p2ctable.getRowCount() - 1, 0, false, false);
			
	}
	
	public InteractionTablePanle(Container p){
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(0, 0, 879, 362);
		p.add(panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBounds(21, 21, 372, 320);
		panel.add(panel_1);
		
		JTextPane textPane = new JTextPane();
		textPane.setText("信息模型  --->  物理模型");
		textPane.setBackground(SystemColor.menu);
		textPane.setBounds(108, 10, 164, 21);
		panel_1.add(textPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(60, 70, 250, 210);
		panel_1.add(scrollPane);
		
		c2ptable = new JTable();
		scrollPane.setViewportView(c2ptable);
		dtm1=(DefaultTableModel)c2ptable.getModel();
		dtm1.addColumn("对象");
		dtm1.addColumn("时间");
		dtm1.addColumn("值");

		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBounds(391, 21, 372, 320);
		panel.add(panel_2);
		
		JTextPane textPane_1 = new JTextPane();
		textPane_1.setText("物理模型  --->  信息模型");
		textPane_1.setBackground(SystemColor.menu);
		textPane_1.setBounds(110, 10, 164, 21);
		panel_2.add(textPane_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(60, 70, 250, 210);
		panel_2.add(scrollPane_1);
		
		p2ctable = new JTable();
		scrollPane_1.setViewportView(p2ctable);
		dtm2=(DefaultTableModel)p2ctable.getModel();
		dtm2.addColumn("对象");
		dtm2.addColumn("时间");
		dtm2.addColumn("值");
		

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args){
		JFrame j = new JFrame();
		j.setBounds(400, 400, 800, 400);
		InteractionTablePanle pp = new InteractionTablePanle(j);
		String[] a = {"a","12.2","123"};
		String[] b = {"b","123.2","234"};
		for (int i=0;i<20;i++)
			pp.addOneLine(a, b);
		j.show();
		
	}

}
