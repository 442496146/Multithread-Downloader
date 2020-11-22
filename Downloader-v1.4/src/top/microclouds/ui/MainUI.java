package top.microclouds.ui;

import java.awt.Button;
import java.awt.CardLayout;

import java.awt.Dimension;

import java.awt.Font;

import java.awt.GridLayout;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.microclos.uitl.FileDownMessages;

import top.microclouds.download.MutiDownloads;

public class MainUI implements ActionListener {

	protected static String title = "多线程下载器";// 程序主窗口的标题
	protected static JFrame jFrame;// 主窗口框架
	protected static JPanel downPanel = new JPanel();// 下载面板

	protected static Vector rowData;// 添加到表格的数据列表
	protected static DefaultTableModel defaultTableModel;// 表格模型
	protected static JTable jTable;// 表格
	protected static JScrollPane jScrollPane;// 表格滚动条
	
	protected static JButton addBtn, deleteBtn, stopBtn, restartBtn;
	
	protected static FileDownMessages fileDownMessages;

	protected static MutiDownloads mutiDownloads;

	protected static int rowNum = 0;
	
	protected static int oldSumSize = 0;
	
	public MainUI() {
	}

	public MainUI(int i) throws IOException {

		jFrame = new JFrame(title);
		jFrame.setResizable(false);
		jFrame.setBounds(90, 30, 1200, 660);
		jFrame.setLayout(null);

		downPanel.setBounds(0, 60, 1200, 550);
		downPanel.setBorder(new TitledBorder("下 载 列 表"));

		this.topComponent();
		this.downPanelComponent();
		
		jFrame.add(downPanel);
		jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
		jFrame.setIconImage(new ImageIcon("images/cloud.png").getImage());
		jFrame.setVisible(true);
	}

	public void setColumnSize(JTable table, int i, int preferedWidth, int maxWidth, int minWidth) {
		// 表格的列模型
		TableColumnModel cm = table.getColumnModel();
		// 得到第i个列对象
		TableColumn column = cm.getColumn(i);
		column.setPreferredWidth(preferedWidth);
		column.setMaxWidth(maxWidth);
		column.setMinWidth(minWidth);
	}

	public void setTableAllRowHeight(JTable table, int height) {
		table.setRowHeight(height);
	}

	public void setTableColumnCenter(JTable table) {
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, r);
	}

	public void topComponent() {

		JPanel topPanel = new JPanel();
		topPanel.setSize(280, 50);
		GridLayout gridLayout = new GridLayout(1, 4);
		topPanel.setLayout(gridLayout);

		ImageIcon addIcon, deleteIcon, restartIcon, stopIcon;
		addIcon = new ImageIcon("images/add.png");
		deleteIcon = new ImageIcon("images/delete.png");
		restartIcon = new ImageIcon("images/restart.png");
		stopIcon = new ImageIcon("images/stop.png");
		addBtn = new JButton();
		deleteBtn = new JButton();
		stopBtn = new JButton();
		restartBtn = new JButton();

		addBtn.setOpaque(false);
		addBtn.setMargin(new Insets(0, 0, 0, 0));
		addBtn.setFocusPainted(false);
		addBtn.setIcon(addIcon);
		addBtn.setToolTipText("添加任务");
		addBtn.setContentAreaFilled(false);

		deleteBtn.setOpaque(false);
		deleteBtn.setContentAreaFilled(false);
		deleteBtn.setMargin(new Insets(0, 0, 0, 0));
		deleteBtn.setFocusPainted(false);
		deleteBtn.setIcon(deleteIcon);
		deleteBtn.setToolTipText("删除任务");

		stopBtn.setOpaque(false);
		stopBtn.setContentAreaFilled(false);
		stopBtn.setMargin(new Insets(0, 0, 0, 0));
		stopBtn.setFocusPainted(false);
		stopBtn.setIcon(stopIcon);
		stopBtn.setToolTipText("暂停任务");

		restartBtn.setOpaque(false);
		restartBtn.setContentAreaFilled(false);
		restartBtn.setMargin(new Insets(0, 0, 0, 0));
		restartBtn.setFocusPainted(false);
		restartBtn.setIcon(restartIcon);
		restartBtn.setToolTipText("开始任务");

		topPanel.add(addBtn);
		topPanel.add(restartBtn);
		topPanel.add(stopBtn);
		topPanel.add(deleteBtn);

		// 添加按钮监听事件
		addBtn.addActionListener(this);
		stopBtn.addActionListener(new StopHomeworkListener());
		restartBtn.addActionListener(new RestartHomeworkListener());
		deleteBtn.addActionListener(new DeleteHomeworkListener());

		jFrame.add(topPanel);
	}

	public void downPanelComponent() throws IOException {
		// downPanel.setBorder(new TitledBorder("下载列表"));
		String[] col = { "文件名", "文件大小", "状态", "下载速度", "进度", "开始时间", "保存位置" };
		String[][] row = { { "文件名", "文件大小", "状态", "下载速度", "进度", "2018-12-12 12:12", "保存位置" },
				{ "文件名", "文件大小", "状态", "下载速度", "进度", "2018-12-12 12:12", "保存位置" } };

		String[][] row2 = null;

//		Vector lists = new Vector<>();

		defaultTableModel = new DefaultTableModel(row2, col) {
			public boolean isCellEditable(int rowIndex,int columnIndex) {return false;}// 表格内数据不可编辑
		};
		
		String string = new String();
		String[] strings = new String[10];

//		// 导入config.txt中下载记录
//		FileReader readMessages = new FileReader("C:\\Users\\Jason\\Desktop\\java线程下载 - 副本\\Downloader\\messages\\config.txt");
//		BufferedReader bufferedReader = new BufferedReader(readMessages);
//		while ((string = bufferedReader.readLine()) != null) {
//			Vector lists = new Vector<>();
//			strings = string.split("\\*");
//			System.out.println("ddddddddddddddddddd");
//			for (int i = 0; i < 7; i++) {
//				System.out.println(" list : --> " + strings[i]);
//				lists.add(strings[i]);
////				System.out.println("list : " + strings[i]);
//			}
//			defaultTableModel.addRow(lists);
//			lists = null;
//		}
//		bufferedReader.close();

		jTable = new JTable(defaultTableModel);
		jTable.setRowSorter(new TableRowSorter(defaultTableModel));
		// jTable.setEnabled(false); //表格不可编辑
		// defaultTableModel.addRow(rowData2);
		// defaultTableModel.setValueAt("haha~", 0, 4);

		// 去掉表格的线
		// jTable.setShowVerticalLines(false);

		jScrollPane = new JScrollPane(jTable);
		jTable.setPreferredScrollableViewportSize(new Dimension(1200, 550));
		jTable.setFillsViewportHeight(true);
		setTableAllRowHeight(jTable, 24);
		setTableColumnCenter(jTable);
		jTable.setFont(new Font("Menu.font", Font.PLAIN, 15));
		setColumnSize(jTable, 0, 250, 312, 300);// filename
		setColumnSize(jTable, 1, 80, 120, 80);// 大小
		setColumnSize(jTable, 2, 80, 100, 80);// 状态
		setColumnSize(jTable, 3, 80, 120, 80);// 速度
		setColumnSize(jTable, 4, 100, 80, 100);// 进度
		setColumnSize(jTable, 5, 100, 160, 100);// time
		setColumnSize(jTable, 6, 300, 320, 300);// 位置

		downPanel.add(jScrollPane);
	}

	private void string(char[] c, int i, int num) {
		// TODO Auto-generated method stub

	}

	public void updateDwonMessages(String fileName, Long fileSize, String fileSavePath, MutiDownloads mutiDownloads) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

		// fileDownMessages.setFileName(fileName);
		Vector list = new Vector<>();
		list.add(fileName);
		list.add(fileSize + " KB");
		list.add("正在下载");
		list.add(" K/s");
		list.add(" ");
		list.add((String) df.format(new Date()));
		list.add(fileSavePath);
		defaultTableModel.addRow(list);

		// if (defaultTableModel.getRowCount() != 0) {
		rowNum = defaultTableModel.getRowCount() - 1;
		// System.out.println("--------------->" + rowNum);
		// }
		MainUI.mutiDownloads=mutiDownloads;
		new Thread(() -> {
			// double progress = mutiDownloads.getProgress();
			DecimalFormat decimalFormat = new DecimalFormat("0.0");
			while (mutiDownloads.getProgress() < 100) {
				// progress = mutiDownloads.getProgress();
				// System.out.println("pro : " + mutiDownloads.getProgress());
				defaultTableModel.setValueAt(decimalFormat.format(mutiDownloads.getProgress()) + " %", rowNum, 4);
				defaultTableModel.setValueAt((mutiDownloads.getSumSize() - oldSumSize)/1024 + " KB/s", rowNum, 3);
				oldSumSize=mutiDownloads.getSumSize();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			defaultTableModel.setValueAt(100 + " %", 0, 4);
		}).start();
	}

	public static void main(String[] args) throws IOException {
		MainUI mainUI = new MainUI(0);

	}

	// 监听新建任务的按钮的事件
	// static class AddHomeworkListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// JButton button = (JButton) e.getSource();
	// NewHomeworkWindow newHomeworkWindow = new NewHomeworkWindow();
	// }
	//
	// }

	// 监听暂停任务的按钮的事件
	public class StopHomeworkListener implements ActionListener {

		@Override
		public void  actionPerformed(ActionEvent e) {
			//button.setEnabled(false);
			try {
				mutiDownloads.downloadstop();
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
			
		}

	}

	// 监听开始任务的按钮的事件
	static class RestartHomeworkListener implements ActionListener {

		@Override
		public void  actionPerformed(ActionEvent e) {
			//button.setEnabled(false);
				try {
					mutiDownloads.downloadrestart();
				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}
		}

	}

	// 监听删除任务的按钮的事件
	static class DeleteHomeworkListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			//JButton button = (JButton) e.getSource();
		}

	}
	// ------------------------->

	@Override
	public void actionPerformed(ActionEvent e) {
		//JButton button = (JButton) e.getSource();
		NewHomeworkWindow newHomeworkWindow = new NewHomeworkWindow();

	}
}
