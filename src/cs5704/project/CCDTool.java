package cs5704.project;

import java.awt.Color;
import java.awt.Component;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
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
	
	private String filePath1 = "", filePath2 = "";
	private boolean isSingleFile = true;
	
	public static ASTParserTool parserTool = new ASTParserTool();
	public MethodList methodVectorList1 = new MethodList();
	public MethodList methodVectorList2 = new MethodList();

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
				    		filePath1 = selectedFile.getAbsolutePath();
				    		sourceDisplay(sourceDis1, filePath1);
				    		sourceDis1Blank = false;
				    	}
				    	else if(sourceDis2Blank) {
				    		filePath2 = selectedFile.getAbsolutePath();
				    		if(filePath2.equals(filePath1))
				    			isSingleFile = true;
				    		else
				    			isSingleFile = false;
				    		sourceDisplay(sourceDis2, filePath2);
				    		sourceDis2Blank = false;
				    	}
				    	else {
				    		clearDisplay();
				    		filePath1 = selectedFile.getAbsolutePath();
				    		sourceDisplay(sourceDis1, filePath1);
				    		sourceDis1Blank = false;
				    	}
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
				clearDisplay();
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
		mntmFindMethods.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				methodVectorList1 = parserTool.parseMethod(parserTool.getCompilationUnit(filePath1));
				methodDisplay(methodDis1, methodVectorList1);
				if(sourceDis2Blank) {
					try {
						filePath2 = filePath1;
						sourceDisplay(sourceDis2, filePath2);
						sourceDis2Blank = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				methodVectorList2 = parserTool.parseMethod(parserTool.getCompilationUnit(filePath2));
				methodDisplay(methodDis2, methodVectorList2);
			}
		});
		mnRun.add(mntmFindMethods);
		
		mntmFindCloneCode = new JMenuItem("Find Clone Code");
		mntmFindCloneCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cloneListDisplay(outputDis);
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
		
		sourceModel1 = new DefaultTableModel();
		sourceModel1.setColumnIdentifiers(new Object[]{"#", "Code"});
		
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
		
		sourceModel2 = new DefaultTableModel();
		sourceModel2.setColumnIdentifiers(new Object[]{"#", "Code"});
		
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
		
		methodModel1 = new DefaultTableModel();
		methodModel1.setColumnIdentifiers(new Object[]{"#", "Method Name"});
		
		methodDis1 = new JTable(methodModel1);
		methodDis1.setFillsViewportHeight(true);
		methodDis1.getColumnModel().getColumn(0).setMaxWidth(30);
		methodDis1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		methodScrollPanel1.setViewportView(methodDis1);
		
		methodPanel2 = new JPanel();
		methodPanel2.setBorder(new TitledBorder(null, "Methods 2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		methodPanel2.setLayout(new BoxLayout(methodPanel2, BoxLayout.X_AXIS));
		MethodBox.add(methodPanel2);
		
		methodScrollPanel2 = new JScrollPane();
		methodPanel2.add(methodScrollPanel2);
		
		methodModel2 = new DefaultTableModel();
		methodModel2.setColumnIdentifiers(new Object[]{"#", "Method Name"});
		
		methodDis2 = new JTable(methodModel2);
		methodDis2.setFillsViewportHeight(true);
		methodDis2.getColumnModel().getColumn(0).setMaxWidth(30);
		methodDis2.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		methodScrollPanel2.setViewportView(methodDis2);
		
		Box OutputBox = Box.createHorizontalBox();
		LocalBox.add(OutputBox);
		
		outputPanel = new JPanel();
		outputPanel.setBorder(new TitledBorder(null, "Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
		OutputBox.add(outputPanel);
		
		outputScrollPanel = new JScrollPane();
		outputPanel.add(outputScrollPanel);
		
		outputModel = new DefaultTableModel();
		outputModel.setColumnIdentifiers(new Object[]{"#", "Sim", "Method 1", "Start", "End", "Method 2", "Start", "End"});
		
		outputDis = new JTable(outputModel);
		outputDis.setFillsViewportHeight(true);
		outputDis.getColumnModel().getColumn(0).setPreferredWidth(30);
		outputDis.getColumnModel().getColumn(1).setPreferredWidth(50);
		outputDis.getColumnModel().getColumn(2).setPreferredWidth(180);
		outputDis.getColumnModel().getColumn(3).setPreferredWidth(60);
		outputDis.getColumnModel().getColumn(4).setPreferredWidth(50);
		outputDis.getColumnModel().getColumn(5).setPreferredWidth(180);
		outputDis.getColumnModel().getColumn(6).setPreferredWidth(60);
		outputDis.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		outputScrollPanel.setViewportView(outputDis);
		
		outputDis.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent event) {
		    	cloneMethodDisplay();
		    }
		});
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
	
	public void clearDisplay() {
		DefaultTableModel model = (DefaultTableModel)sourceDis1.getModel();
		model.setRowCount(0);
		model = (DefaultTableModel)sourceDis2.getModel();
		model.setRowCount(0);
		model = (DefaultTableModel)methodDis1.getModel();
		model.setRowCount(0);
		model = (DefaultTableModel)methodDis2.getModel();
		model.setRowCount(0);
		model = (DefaultTableModel)outputDis.getModel();
		model.setRowCount(0);
		
		sourceDis1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(Color.WHITE);
                return comp;
            }  
        });
		sourceDis1.updateUI();
		
		sourceDis2.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(Color.WHITE);
                return comp;
            }  
        });
		sourceDis2.updateUI();
		
		sourceDis1Blank = true;
		sourceDis2Blank = true;
		
		isSingleFile = true;
		
		filePath1 = "";
		filePath2 = "";
	}
	
	public void methodDisplay(JTable tb, MethodList mList) {
    	DefaultTableModel model = (DefaultTableModel)tb.getModel();
    	model.setRowCount(0);
		for(int index = 0; index < mList.size(); index++) {
	    	model = (DefaultTableModel)tb.getModel();
	    	model.addRow(new Object[]{index + 1, mList.getMethodVector(index).methodName});
		}
	}
	
	public void cloneListDisplay(JTable tb) {
		MethodSimilarity methodSim = new MethodSimilarity();
		List<Result> rList = new ArrayList<Result>();
		if(isSingleFile)
			rList = methodSim.simDetector(methodVectorList1);
		else
			rList = methodSim.simDetector(methodVectorList1, methodVectorList2);
		for(int index = 0; index < rList.size(); index++) {
	    	DefaultTableModel model = (DefaultTableModel)tb.getModel();
	    	model.addRow(new Object[]{
	    			rList.get(index).index,
	    			(double)(Math.round(rList.get(index).similarity * 100)/100.0),
	    			rList.get(index).methodName1,
	    			rList.get(index).startLineNum1,
	    			rList.get(index).endLineNum1 + 1,
	    			rList.get(index).methodName2,
	    			rList.get(index).startLineNum2,
	    			rList.get(index).endLineNum2 + 1});
		}
	}
	
	public void cloneMethodDisplay() {
		if (outputDis.getSelectedRow() > -1) {
			int colorLine1 = (int) outputDis.getValueAt(outputDis.getSelectedRow(), 3);
			int scrollLine1 = (int) outputDis.getValueAt(outputDis.getSelectedRow(), 4);
			sourceDis1.scrollRectToVisible(sourceDis1.getCellRect(0, 0, true));
			sourceDis1.scrollRectToVisible(sourceDis1.getCellRect(scrollLine1+ 10, 0, true));
			int colorLine2 = (int) outputDis.getValueAt(outputDis.getSelectedRow(), 6);
			int scrollLine2 = (int) outputDis.getValueAt(outputDis.getSelectedRow(), 7);
			sourceDis2.scrollRectToVisible(sourceDis2.getCellRect(0, 0, true));
			sourceDis2.scrollRectToVisible(sourceDis2.getCellRect(scrollLine2 + 10, 0, true));
			
			sourceDis1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
	            @Override
	            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	                Color color = new Color(200, 255, 200);
	                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	                if(row >= colorLine1 - 1 && row < scrollLine1)
	                	comp.setBackground(color);
	                else
	                	comp.setBackground(Color.WHITE);
	                return comp;
	            }  
	        });
			sourceDis1.updateUI();
			
			sourceDis2.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
	            @Override
	            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	                Color color = new Color(255, 200, 200);
	                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	                if(row >= colorLine2 - 1 && row < scrollLine2)
	                	comp.setBackground(color);
	                else
	                	comp.setBackground(Color.WHITE);
	                return comp;
	            }  
	        });
			sourceDis2.updateUI();
        }
	}
}
