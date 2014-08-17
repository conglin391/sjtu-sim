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
		if (cmd.equals("确定")) {
			if (creatMFunction()) {
				dialog.dispose();
			} else {
				ErrorInfo("m函数生成失败，请检查参数是否完整或格式是否正确");
			}

		} else if (cmd.equals("取消")) {
			dialog.dispose();
		} else if (cmd.equals("工作空间")) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setApproveButtonText("确定");
			fileChooser.setDialogTitle("设置matlab工作空间目录");
			result = fileChooser.showOpenDialog(f);
			if (result == JFileChooser.APPROVE_OPTION) {
				// 获得该文件
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
		dialog = new JDialog(f, "生成m函数", true);
		GridBagConstraints c;
		int gridx, gridy, gridwidth, gridheight, anchor, fill, ipadx, ipady;
		double weightx, weighty;
		Insets inset;

		GridBagLayout gridbag = new GridBagLayout();
		Container dialogPane = dialog.getContentPane();
		dialogPane.setLayout(gridbag);

		JLabel label = new JLabel("函数名称：");
		gridx = 0; // 第0列
		gridy = 0; // 第0行
		gridwidth = 1; // 占一单位宽度
		gridheight = 1; // 占一单位高度
		weightx = 0; // 窗口增大时组件宽度增大比率0
		weighty = 0; // 窗口增大时组件高度增大比率0
		anchor = GridBagConstraints.CENTER; // 容器大于组件时将组件置于容器中央
		fill = GridBagConstraints.BOTH; // 窗口拉大时会填满水平与垂直空间
		inset = new Insets(0, 0, 0, 0); // 组件间间距
		ipadx = 0; // 组件内水平宽度
		ipady = 0; // 组件内垂直高度
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(label, c);
		dialogPane.add(label);

		label = new JLabel("函数参数：");
		gridx = 0;
		gridy = 2;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(label, c);
		dialogPane.add(label);

		label = new JLabel("函数返回值：");
		gridx = 0;
		gridy = 4;
		c = new GridBagConstraints(gridx, gridy, gridwidth, gridheight,
				weightx, weighty, anchor, fill, inset, ipadx, ipady);
		gridbag.setConstraints(label, c);
		dialogPane.add(label);

		label = new JLabel("Simulink模型名称：");
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

		retValueField = new JTextField("(y1,V)-(y2,SOC)|(Ppv,Ppv)");
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
		JButton b = new JButton("确定");
		b.addActionListener(this);
		panel.add(b);
		b = new JButton("取消");
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
		b = new JButton("工作空间");
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

		/* === 处理返回值 === */
		String[] returnValtmptmp = retValueField.getText().split("\\|");
		String[][] returnValtmp = new String[returnValtmptmp.length][];
		String[][] returnVal = new String[returnValtmp.length][];
		String[][] returnValRef = new String[returnValtmp.length][];
		for (int i = 0; i < returnValtmp.length; i++) {
			returnValtmp[i] = returnValtmptmp[i].split("-");
			returnVal[i] = new String[returnValtmp[i].length];
			returnValRef[i] = new String[returnValtmp[i].length];
			for (int j = 0; j < returnValtmp[i].length; j++) {
				returnVal[i][j] = returnValtmp[i][j].split(",")[0].replace("(",
						"");
				returnValRef[i][j] = returnValtmp[i][j].split(",")[1].replace(
						")", "");
			}

		}

		/* === 处理输入值 === */
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
			String retmp = "";
			for (int i = 0; i < returnVal.length; i++)
				retmp += Arrays.toString(returnVal[i]).replace("[", "")
						.replace("]", ", ");
			retmp = retmp.substring(0, retmp.length() - 2);

			// 函数头部分
			out.write("function ["
					+ retmp
					+ "] = "
					+ funNameField.getText()
					+ "("
					+ Arrays.toString(inputVal).replace("[", "")
							.replace("]", "") + ")\n");
			// 主体部分
			out.write("import newtest.ClientforM;\ntest = ClientforM();\n");
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

			// socket输出，返回值部分
			out.write("str = ['<',num2str(evalin('base','iteration'))];\n"
					+ "str = [str,'-',num2str(evalin('base','simulationTime'))];\n");
			
			for(int i=0;i<returnVal.length;i++){
				out.write("str = [str,'-['];\n");
				for (int j=0;j<returnVal[i].length;j++){
					out.write(returnVal[i][j] + " = evalin('base','" + returnValRef[i][j]
							+ "');\n");
					out.write("str = [str,'(','"+returnValRef[i][j]+"',',',num2str("+returnVal[i][j]+"),')'];\n");
					if (j<returnVal[i].length-1)
						out.write("str = [str,'-'];\n");
				}
				out.write("str = [str,']'];\n");		
			}
			out.write("str = [str,'>'];\ntest.CreatSocket(str);\n");
			
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public static void main(String[] args) {
		final JFrame j = new JFrame();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setVisible(true); // 默认为false
		j.setSize(100, 100);
		j.setLocation(500, 300);
		JButton btnMfunction = new JButton("MFunction");
		btnMfunction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MFunctionPanel(j);
			}
		});
		j.getContentPane().add(btnMfunction, BorderLayout.CENTER);

	}

}
