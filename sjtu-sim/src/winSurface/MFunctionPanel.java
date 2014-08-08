package winSurface;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

public class MFunctionPanel implements ActionListener {
	JTextField retValueField, parameterField, funNameField, basePathField,
			modleNameField;
	JDialog dialog;
	JFileChooser fileChooser = null;
	JFrame f;
	String path;

	public void ErrorInfo(String errorInfo) {
		String title = "ERROR INFO";
		int type = JOptionPane.ERROR_MESSAGE;
		JOptionPane.showMessageDialog(f, errorInfo, title, type);

	}

	public void actionPerformed(ActionEvent e) {
		File file = null;
		// String path;
		int result;

		String cmd = e.getActionCommand();
		if (cmd.equals("ȷ��")) {
			if (creatMFunction()) {
				dialog.dispose();
			} else {
				ErrorInfo("m��������ʧ�ܣ���������Ƿ��������ʽ�Ƿ���ȷ");
			}

		} else if (cmd.equals("ȡ��")) {
			dialog.dispose();
		} else if (cmd.equals("�����ռ�")) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setApproveButtonText("ȷ��");
			fileChooser.setDialogTitle("����matlab�����ռ�Ŀ¼");
			result = fileChooser.showOpenDialog(f);
			if (result == JFileChooser.APPROVE_OPTION) {
				// ��ø��ļ�
				file = fileChooser.getSelectedFile();
				path = file.getPath();
				// System.out.println(path);
				basePathField.setText(path);
			}

		}

	}

	@SuppressWarnings("deprecation")
	MFunctionPanel(JFrame frame) {
		this.f = frame;
		dialog = new JDialog(f, "����m����", true);
		GridBagConstraints c;
		int gridx, gridy, gridwidth, gridheight, anchor, fill, ipadx, ipady;
		double weightx, weighty;
		Insets inset;

		GridBagLayout gridbag = new GridBagLayout();
		Container dialogPane = dialog.getContentPane();
		dialogPane.setLayout(gridbag);

		JLabel label = new JLabel("�������ƣ�");
		gridx = 0; // ��0��
		gridy = 0; // ��0��
		gridwidth = 1; // ռһ��λ���
		gridheight = 1; // ռһ��λ�߶�
		weightx = 0; // ��������ʱ�������������0
		weighty = 0; // ��������ʱ����߶��������0
		anchor = GridBagConstraints.CENTER; // �����������ʱ�����������������
		fill = GridBagConstraints.BOTH; // ��������ʱ������ˮƽ�봹ֱ�ռ�
		inset = new Insets(0, 0, 0, 0); // �������
		ipadx = 0; // �����ˮƽ���
		ipady = 0; // ����ڴ�ֱ�߶�
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(label, c);
		dialogPane.add(label);

		label = new JLabel("����������");
		gridx = 0;
		gridy = 2;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(label, c);
		dialogPane.add(label);

		label = new JLabel("��������ֵ��");
		gridx = 0;
		gridy = 4;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(label, c);
		dialogPane.add(label);

		label = new JLabel("Simulinkģ�����ƣ�");
		gridx = 2;
		gridy = 0;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(label, c);
		dialogPane.add(label);

		funNameField = new JTextField("pvbattery_for_once");
		gridx = 1;
		gridy = 0;
		gridwidth = 1;
		gridheight = 1;
		weightx = 1;
		weighty = 0;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(funNameField, c);
		dialogPane.add(funNameField);

		modleNameField = new JTextField("PVBATTERY");
		gridx = 3;
		gridy = 0;
		gridwidth = 1;
		gridheight = 1;
		weightx = 1;
		weighty = 0;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(modleNameField, c);
		dialogPane.add(modleNameField);

		parameterField = new JTextField("(arg,I)");
		gridx = 0;
		gridy = 3;
		gridwidth = 6;
		gridheight = 1;
		weightx = 1;
		weighty = 0;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(parameterField, c);
		dialogPane.add(parameterField);

		retValueField = new JTextField("(y1,V)|(y2,SOC)|(Ppv,Ppv)");
		gridx = 0;
		gridy = 5;
		gridwidth = 6;
		gridheight = 1;
		weightx = 1;
		weighty = 0;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(retValueField, c);
		dialogPane.add(retValueField);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		JButton b = new JButton("ȷ��");
		b.addActionListener(this);
		panel.add(b);
		b = new JButton("ȡ��");
		b.addActionListener(this);
		panel.add(b);
		gridx = 0;
		gridy = 7;
		gridwidth = 6;
		weightx = 1;
		weighty = 1;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(panel, c);
		dialogPane.add(panel);

		panel = new JPanel();
		b = new JButton("�����ռ�");
		b.addActionListener(this);
		panel.add(b);
		gridx = 0;
		gridy = 6;
		gridwidth = 1;
		weightx = 0;
		weighty = 0;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(panel, c);
		dialogPane.add(panel);

		basePathField = new JTextField();
		gridx = 1;
		gridy = 6;
		gridwidth = 5;
		gridheight = 1;
		weightx = 0;
		weighty = 0;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(basePathField, c);
		dialogPane.add(basePathField);

		fileChooser = new JFileChooser();

		dialog.setBounds(200, 350, 447, 200);
		dialog.show();

	}

	public boolean creatFile(String filename) {
		File f = new File(filename);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public boolean creatMFunction() {
		String filePath = basePathField.getText() + "\\"
				+ funNameField.getText() + ".m";
		if (!creatFile(filePath))
			return false;
		File mFile = new File(filePath);

		String[] returnValtmp = retValueField.getText().split("\\|");
		String[] returnVal = new String[returnValtmp.length];
		String[] returnValRef = new String[returnValtmp.length];
		for (int i = 0; i < returnValtmp.length; i++) {
			String[] tmp = returnValtmp[i].replace("(", "").replace(")", "")
					.split(",");
			returnVal[i] = tmp[0];
			returnValRef[i] = tmp[1];
		}

		String[] inputtmp = parameterField.getText().split("\\|");
		String[] inputVal = new String[inputtmp.length];
		String[] inputValRef = new String[inputtmp.length];
		for (int i = 0; i < inputtmp.length; i++) {
			String[] tmp = inputtmp[i].replace("(", "").replace(")", "")
					.split(",");
			inputVal[i] = tmp[0];
			inputValRef[i] = tmp[1];
		}

		String sim_mod = modleNameField.getText().toLowerCase() + "_modified";

		try {
			Writer out = new FileWriter(mFile);
			out.write("function "
					+ Arrays.toString(returnVal)
					+ " = "
					+ funNameField.getText()
					+ "("
					+ Arrays.toString(inputVal).replace("[", "")
							.replace("]", "") + ")\n");
			out.write("assignin('base','" + sim_mod + "',-1);\n");
			out.write("iteration = evalin('base','iteration');\n");
			out.write("if iteration == 1\n\t" + modleNameField.getText() + "\n");
			out.write("\tset_param('" + modleNameField.getText()
					+ "','SimulationCommand','start');\nelse\n");

			for (int i = 0; i < inputVal.length; i++)
				out.write("\tset_param('" + modleNameField.getText() + "/"
						+ inputValRef[i] + "','Value',num2str(" + inputVal[i]
						+ "));\n");
			out.write("\tset_param('" + modleNameField.getText()
					+ "','SimulationCommand','update');\n");
			out.write("\tset_param('" + modleNameField.getText()
					+ "','SimulationCommand','continue');\n");
			out.write("end\n");

			out.write("while evalin('base','" + sim_mod + "') == -1 \n");
			out.write("\tpause(0.0001);\nend\n");

			for (int i = 0; i < returnVal.length; i++)
				out.write(returnVal[i] + " = evalin('base','" + returnValRef[i]
						+ "');\n");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;

	}

}
