package cs5704.project;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class CCDTool extends JFrame {

	private GridBagLayout gbl_contentPane;
	private JPanel contentPane;
	private JMenuItem mntmOpenFile, mntmClearFiles, mntmQuit, mntmFindMethods, mntmFindCloneCode;
	private JPanel sourcePanel1, sourcePanel2, methodPanel1, methodPanel2, outputPanel;
	private JScrollPane sourceScrollPanel1, sourceScrollPanel2, methodScrollPanel1, methodScrollPanel2, outputScrollPanel;
	private JTable sourceDis1, sourceDis2, methodDis1, methodDis2, outputDis;
	
	private DefaultTableModel sourceModel1, sourceModel2, methodModel1, methodModel2, outputModel;
	
	private boolean sourceDis1Blank = true;
	private boolean sourceDis2Blank = true;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CCDTool frame = new CCDTool();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CCDTool() {
		setTitle("CodeCloneDetecttion");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 80, 1200, 600);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnBasic = new JMenu("Basic");
		menuBar.add(mnBasic);
		
		mntmOpenFile = new JMenuItem("Open File...");
		mntmOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("C:\\Users\\liuqing\\git\\Code\\CodeCloneDetection\\testfiles\\"));
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = fileChooser.getSelectedFile();
				    try {
				    	if(sourceDis1Blank) {
				    		sourceDisplay(sourceDis1, selectedFile.getAbsolutePath());
				    		sourceDis1Blank = false;
				    	}
				    	else if(sourceDis2Blank) {
				    		sourceDisplay(sourceDis2, selectedFile.getAbsolutePath());
				    		sourceDis2Blank = false;
				    	}
				    	else
				    		clearFiles(sourceDis1, sourceDis2);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		mnBasic.add(mntmOpenFile);
		
		mntmClearFiles = new JMenuItem("Clear Files");
		mntmClearFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearFiles(sourceDis1, sourceDis2);
			}
		});
		mnBasic.add(mntmClearFiles);
		
		mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnBasic.add(mntmQuit);
		
		JMenu mnRun = new JMenu("Run");
		menuBar.add(mnRun);
		
		mntmFindMethods = new JMenuItem("Find Methods");
		mnRun.add(mntmFindMethods);
		
		mntmFindCloneCode = new JMenuItem("Find Clone Code");
		mntmFindCloneCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	sourceDis1.scrollRectToVisible(sourceDis1.getCellRect(60, 0, true));
			}
		});
		mnRun.add(mntmFindCloneCode);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {780, 380};
		gbl_contentPane.rowHeights = new int[] {530, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0};
		gbl_contentPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		sourceModel1 = new DefaultTableModel();
		sourceModel1.setColumnIdentifiers(new Object[]{"#", "Code"});
		
		sourceModel2 = new DefaultTableModel();
		sourceModel2.setColumnIdentifiers(new Object[]{"#", "Code"});
		
		Box SourceBox = Box.createHorizontalBox();
		GridBagConstraints gbc_SourceBox = new GridBagConstraints();
		gbc_SourceBox.fill = GridBagConstraints.BOTH;
		gbc_SourceBox.anchor = GridBagConstraints.NORTH;
		gbc_SourceBox.gridwidth = GridBagConstraints.RELATIVE;
		gbc_SourceBox.insets = new Insets(0, 0, 0, 5);
		gbc_SourceBox.gridx = 0;
		gbc_SourceBox.gridy = 0;
		gbc_SourceBox.weightx = 1.0;
		gbc_SourceBox.weighty = 1.0;
		contentPane.add(SourceBox, gbc_SourceBox);
		
		sourcePanel1 = new JPanel();
		sourcePanel1.setBorder(new TitledBorder(null, "File 1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sourcePanel1.setLayout(new BoxLayout(sourcePanel1, BoxLayout.X_AXIS));
		SourceBox.add(sourcePanel1);
		
		sourceScrollPanel1 = new JScrollPane();
		sourcePanel1.add(sourceScrollPanel1);
		sourceDis1 = new JTable(sourceModel1);
		sourceDis1.setShowGrid(false);
		sourceDis1.setFillsViewportHeight(true);
		sourceDis1.getColumnModel().getColumn(0).setPreferredWidth(40);
		sourceDis1.getColumnModel().getColumn(1).setPreferredWidth(700);
		sourceDis1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sourceScrollPanel1.setViewportView(sourceDis1);
		
		sourcePanel2 = new JPanel();
		sourcePanel2.setBorder(new TitledBorder(null, "File 2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sourcePanel2.setLayout(new BoxLayout(sourcePanel2, BoxLayout.X_AXIS));
		SourceBox.add(sourcePanel2);
		
		sourceScrollPanel2 = new JScrollPane();
		sourcePanel2.add(sourceScrollPanel2);
		sourceDis2 = new JTable(sourceModel2);
		sourceDis2.setShowGrid(false);
		sourceDis2.setFillsViewportHeight(true);
		sourceDis2.getColumnModel().getColumn(0).setPreferredWidth(40);
		sourceDis2.getColumnModel().getColumn(1).setPreferredWidth(700);
		sourceDis2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sourceScrollPanel2.setViewportView(sourceDis2);
		
		Box LocalBox = Box.createVerticalBox();
		GridBagConstraints gbc_LocalBox = new GridBagConstraints();
		gbc_LocalBox.fill = GridBagConstraints.BOTH;
		gbc_LocalBox.anchor = GridBagConstraints.WEST;
		gbc_LocalBox.gridx = 1;
		gbc_LocalBox.gridy = 0;
		contentPane.add(LocalBox, gbc_LocalBox);
		
		Box MethodBox = Box.createHorizontalBox();
		LocalBox.add(MethodBox);
		
		methodPanel1 = new JPanel();
		methodPanel1.setBorder(new TitledBorder(null, "Methods 1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		methodPanel1.setLayout(new BoxLayout(methodPanel1, BoxLayout.X_AXIS));
		MethodBox.add(methodPanel1);
		
		methodScrollPanel1 = new JScrollPane();
		methodPanel1.add(methodScrollPanel1);
		
		methodDis1 = new JTable();
		methodDis1.setFillsViewportHeight(true);
		methodScrollPanel1.setViewportView(methodDis1);
		
		methodPanel2 = new JPanel();
		methodPanel2.setBorder(new TitledBorder(null, "Methods 2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		methodPanel2.setLayout(new BoxLayout(methodPanel2, BoxLayout.X_AXIS));
		MethodBox.add(methodPanel2);
		
		methodScrollPanel2 = new JScrollPane();
		methodPanel2.add(methodScrollPanel2);
		
		methodDis2 = new JTable();
		methodDis2.setFillsViewportHeight(true);
		methodScrollPanel2.setViewportView(methodDis2);
		
		Box OutputBox = Box.createHorizontalBox();
		LocalBox.add(OutputBox);
		
		outputPanel = new JPanel();
		outputPanel.setBorder(new TitledBorder(null, "Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
		OutputBox.add(outputPanel);
		
		outputScrollPanel = new JScrollPane();
		outputPanel.add(outputScrollPanel);
		
		outputDis = new JTable();
		outputDis.setFillsViewportHeight(true);
		outputScrollPanel.setViewportView(outputDis);
	}
	
	public void sourceDisplay(JTable tb, String filePath) throws IOException {
		int lineCount = 1;
		String readLine = "";
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	    while((readLine = br.readLine()) != null) {
	    	DefaultTableModel model = (DefaultTableModel)tb.getModel();
	    	readLine = readLine.replaceAll("\t", "        ");
	    	model.addRow(new Object[]{lineCount++, readLine});
		}
	    fis.close();
	}
	
	public void clearFiles(JTable tb1, JTable tb2) {
		DefaultTableModel model = (DefaultTableModel)tb1.getModel();
		model.setRowCount(0);
		model = (DefaultTableModel)tb2.getModel();
		model.setRowCount(0);
		
		sourceDis1Blank = true;
		sourceDis2Blank = true;
	}
}
