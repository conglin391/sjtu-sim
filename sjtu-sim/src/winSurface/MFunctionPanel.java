package winSurface;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

public class MFunctionPanel  extends JPanel implements ActionListener{
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
                if (cmd.equals("生成")) {
                        if (!creatMFunction()) {
                                ErrorInfo("m函数生成失败，请检查参数是否完整或格式是否正确");
                        }

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

        /**
         * @wbp.parser.entryPoint
         */

        MFunctionPanel() {
                super();

                setLayout(null);
                setBounds(0, 0, 764, 138);
                JTextPane textPane = new JTextPane();
                textPane.setText("交互函数名");
                textPane.setBackground(SystemColor.menu);
                textPane.setBounds(25, 10, 66, 21);
                add(textPane);
                
                funNameField = new JTextField();
                funNameField.setText("pvbattery_for_once");
                funNameField.setColumns(10);
                funNameField.setBounds(106, 10, 200, 21);
                add(funNameField);
                
                JTextPane textPane_1 = new JTextPane();
                textPane_1.setText("物理模型名");
                textPane_1.setBackground(SystemColor.menu);
                textPane_1.setBounds(412, 10, 66, 21);
                add(textPane_1);
                
                modleNameField = new JTextField();
                modleNameField.setText("PVBATTERY");
                modleNameField.setColumns(10);
                modleNameField.setBounds(488, 10, 200, 21);
                add(modleNameField);
                
                JTextPane textPane_2 = new JTextPane();
                textPane_2.setText("控制参数");
                textPane_2.setBackground(SystemColor.menu);
                textPane_2.setBounds(25, 41, 66, 21);
                add(textPane_2);
                
                JTextPane textPane_3 = new JTextPane();
                textPane_3.setText("输出参数");
                textPane_3.setBackground(SystemColor.menu);
                textPane_3.setBounds(25, 72, 66, 21);
                add(textPane_3);
                
                parameterField = new JTextField();
                parameterField.setText("(arg,I)");
                parameterField.setColumns(10);
                parameterField.setBounds(106, 41, 582, 21);
                add(parameterField);
                
                retValueField = new JTextField();
                retValueField.setText("(y1,V)-(y2,SOC)|(Ppv,Ppv)");
                retValueField.setColumns(10);
                retValueField.setBounds(106, 72, 582, 21);
                add(retValueField);
                
                JButton button = new JButton("工作空间");
                button.setBounds(10, 103, 81, 23);
                button.addActionListener(this);
                add(button);
                
                basePathField = new JTextField();
                basePathField.setText(" C:\\Users\\Administrator");
                basePathField.setColumns(10);
                basePathField.setBounds(106, 104, 582, 21);
                add(basePathField);
                
                JButton button_1 = new JButton("生成");
                button_1.setBounds(698, 41, 56, 57);
                button_1.addActionListener(this);
                add(button_1);
                
                fileChooser = new JFileChooser();

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

                        out.write("str = ['<socketMessage>'];\n"
                                        + "str = [str,'<iteration>',num2str(iteration),'</iteration>'];\n"
                                        + "str = [str,'<simulationTime>',num2str(evalin('base','simulationTime')),'</simulationTime>'];\n");
                        out.write("str = [str,'<PtolemyDataset mdlFileName=\""
                                        + modleNameField.getText() + "\">'];\n");
                        for (int i = 0; i < inputVal.length; i++)
                                out.write("str = [str,'<point name=\"" + inputValRef[i]
                                                + "\">',num2str(" + inputVal[i] + "),'</point>'];\n");
                        out.write("str = [str,'</PtolemyDataset>'];\n"
                                        + "str = [str,'<SimulinkDatasets>'];\n");

                        for (int i = 0; i < returnVal.length; i++) {
                                out.write("str = [str,'<dataset>'];\n");
                                for (int j = 0; j < returnVal[i].length; j++) {
                                        out.write(returnVal[i][j] + " = evalin('base','"
                                                        + returnValRef[i][j] + "');\n");
                                        out.write("str = [str,'<point name=\"" + returnValRef[i][j]
                                                        + "\">',num2str(" + returnVal[i][j]
                                                        + "),'</point>'];\n");
                                }
                                out.write("str = [str,'</dataset>'];\n");
                        }
                        out.write("str = [str,'</SimulinkDatasets>'];\n"
                                        + "str = [str,'</socketMessage>'];\n"
                                        + "test.CreatSocket(str);\n");

                        out.close();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return false;
                }

                return true;

        }


}
