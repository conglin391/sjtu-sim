package winSurface;

import java.awt.LayoutManager;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

public class JDataShowTablePanel extends JPanel {

	private JTable c2ptable, p2ctable;
	DefaultTableModel dtm1, dtm2;
	private Color[] color = new Color[2];
	private int colorcnt1 = 0, colorcnt2 = 0, lastRow1 = 0, lastRow2 = 0;

	/****************************
	 *
	 * �ṩ���ֽӿڣ�һ����ͬʱ�������ű�һ����ѡ�񵥶�����ĳ�ű���1,2���֣� ����������object[3]
	 *
	 *
	 *******************************/

	public void addOneLine(Object[] str1, Object[] str2) {
		// ��������Object[3]�ֱ�����ű�
		dtm1.addRow(str1);
		dtm2.addRow(str2);
		c2ptable.changeSelection(c2ptable.getRowCount() - 1, 0, false, false);
		p2ctable.changeSelection(p2ctable.getRowCount() - 1, 0, false, false);
		// colorcnt++;

	}

	public void addOneLine(Object[] str, int whichtable) {
		if (whichtable == 1) {
			dtm1.addRow(str);
		} else if (whichtable == 2) {
			dtm2.addRow(str);

		}
		c2ptable.changeSelection(c2ptable.getRowCount() - 1, 0, false, false);
		p2ctable.changeSelection(p2ctable.getRowCount() - 1, 0, false, false);

	}

	public JDataShowTablePanel() {
		// TODO Auto-generated constructor stub

		super();
		color[0] = new Color(23, 242, 241);
		color[1] = new Color(255, 0, 255);
		setLayout(null);

		int tableWidth = 372;
		int tableHeight = 500;
		int scrollPanelWidth = 372;
		int scrollPanelHeight = 450;
		int panelWidth = 372;
		int panelHeight = 600;

		setBounds(0, 0, 879, 362);

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBounds(21, 21, 372, panelHeight);
		add(panel_1);

		JTextPane textPane = new JTextPane();
		textPane.setText("��Ϣģ��  --->  ����ģ��");
		textPane.setBackground(SystemColor.menu);
		textPane.setBounds(108, 10, 164, 21);
		panel_1.add(textPane);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(60, 70, 250, scrollPanelHeight);
		panel_1.add(scrollPane);

		c2ptable = new JTable();
		scrollPane.setViewportView(c2ptable);
		dtm1 = (DefaultTableModel) c2ptable.getModel();
		dtm1.addColumn("����");
		dtm1.addColumn("ʱ��");
		dtm1.addColumn("ֵ");
		c2ptable.setDefaultRenderer(Object.class,
				new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						Component comp = super
								.getTableCellRendererComponent(table, value,
										isSelected, hasFocus, row, column);
						Object lastTime;
						System.out.println(row + " " + column);
						if (row != 0) {
							lastTime = c2ptable.getValueAt(row - 1, 1);
							Object thisTime = c2ptable.getValueAt(row, 1);
							if ((lastRow1 != row)
									&& (!thisTime.equals(lastTime)))
								colorcnt1++;
						}
						lastRow1 = row;
						comp.setBackground(color[colorcnt1 % 2]);
						return comp;
					}

				});

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBounds(391, 21, 372, panelHeight);
		add(panel_2);

		JTextPane textPane_1 = new JTextPane();
		textPane_1.setText("����ģ��  --->  ��Ϣģ��");
		textPane_1.setBackground(SystemColor.menu);
		textPane_1.setBounds(110, 10, 164, 21);
		panel_2.add(textPane_1);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(60, 70, 250, scrollPanelHeight);
		panel_2.add(scrollPane_1);

		p2ctable = new JTable();
		scrollPane_1.setViewportView(p2ctable);
		dtm2 = (DefaultTableModel) p2ctable.getModel();
		dtm2.addColumn("����");
		dtm2.addColumn("ʱ��");
		dtm2.addColumn("ֵ");
		p2ctable.setDefaultRenderer(Object.class,
				new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						Component comp = super
								.getTableCellRendererComponent(table, value,
										isSelected, hasFocus, row, column);
						Object lastTime;
						System.out.println(row + " " + column);
						if (row != 0) {
							lastTime = p2ctable.getValueAt(row - 1, 1);
							Object thisTime = p2ctable.getValueAt(row, 1);
							if ((lastRow2 != row)
									&& (!thisTime.equals(lastTime)))
								colorcnt2++;
						}
						lastRow2 = row;
						comp.setBackground(color[colorcnt2 % 2]);
						return comp;
					}

				});
	}

	public JDataShowTablePanel(LayoutManager arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public JDataShowTablePanel(boolean arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public JDataShowTablePanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}