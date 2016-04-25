package cs5704.project;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.wb.swt.SWTResourceManager;

import com.apple.eawt.Application;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class CCDTool{
	
	public static final String APP_NAME = "STCD";
	
	private static Text train_SelectFile;
	private static Label lable_HiddenNodes;
	private static Label label_TrainingTimes;
	private static Label label_Threshold;
	private static Slider slider_HiddenNodes;
	private static Slider slider_TrainTimes;
	private static Slider slider_Threshold;
	private static Label label_TrainStatus;
	private static Table table_File1;
	private static Table table_File2;
	private static Tree tree_Method1;
	private static Tree tree_Method2;
	private static Table table_Results;
	
	public static boolean TRAIN_MODE = false;
	
	public static int train_HiddenNodes = 10;
	public static int train_TrainTimes = 200;
	public static double train_Threshold = 0.87;
	
	public static Runnable task;
	
	public static MultiplePerceptionTool MLP;
	public static double[][] train_Sim;	
	public static int[][] train_Output;

	private static boolean sourceDis1Blank = true;
	private static boolean sourceDis2Blank = true;
	
	private static String train_FilePath = "";
	private static String train_DirPath = "";
	private static String test_FilePath1 = "", test_FilePath2 = "";
	private static boolean isSingleFile = true;
	
	public static ASTParserTool parserTool = new ASTParserTool();
	public static MethodList methodVectorList1 = new MethodList();
	public static MethodList methodVectorList2 = new MethodList();

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Display.setAppName(APP_NAME);
		Display display = Display.getDefault();
				
		Shell shell = new Shell(display);
		shell.setMaximized(true);
		shell.setLayout(new GridLayout(1, false));
		shell.setImage(new Image(display, "sources/icon.png"));
		shell.setText("STCD - Statistical-based Clone Detection");

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmNewSubmenu1 = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu1.setText("Train");
		
		Menu menu_1 = new Menu(mntmNewSubmenu1);
		mntmNewSubmenu1.setMenu(menu_1);
		
		MenuItem menu_Train_Open = new MenuItem(menu_1, SWT.NONE);
		menu_Train_Open.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog train_FileDialog = new FileDialog(shell, SWT.MULTI);
				train_FileDialog.setFilterExtensions(new String[] {new String("*.txt") });
				train_FileDialog.setFilterPath("sources/TrainFiles");
				if(train_FileDialog.open() != null) {
					train_FilePath = "";
					train_DirPath = train_FileDialog.getFilterPath();
					String[] train_Filenames = train_FileDialog.getFileNames();
					for(int i = 0; i < train_Filenames.length; i++)
						train_FilePath += train_Filenames[i] + " ";
					train_SelectFile.setText(train_FilePath);
				}
			}
		});
		menu_Train_Open.setText("Open Files...");
		
		MenuItem menu_Train_Reset = new MenuItem(menu_1, SWT.NONE);
		menu_Train_Reset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TRAIN_MODE = false;
			
				train_SelectFile.setText("");
				train_FilePath = "";
				
				slider_HiddenNodes.setSelection(5);
				slider_TrainTimes.setSelection(10);
				slider_Threshold.setSelection(12);
				
				train_HiddenNodes = 10;
				train_TrainTimes = 200;
				train_Threshold = 0.87;
				
				lable_HiddenNodes.setText("Hidden Nodes: 10");
				label_TrainingTimes.setText("Training Times: 200");
				label_Threshold.setText("Threshold: 0.87");
				
				label_TrainStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
				label_TrainStatus.setText("Untrained!");
			}
		});
		menu_Train_Reset.setText("Reset");
		
		MenuItem menu_Train_Run = new MenuItem(menu_1, SWT.NONE);
		menu_Train_Run.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				if(train_FilePath != "") {
					TRAIN_MODE = true;
					
					try {
						read_TrainData(train_FilePath);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		
					MLP = new MultiplePerceptionTool(9, train_HiddenNodes, 1);
					
					for(int index1 = 0; index1 < train_TrainTimes; index1++) {
						for(int index2 = 0; index2 < train_Sim.length; index2++)
							MLP.trainMLP(1, train_Sim[index2], train_Output[index2]);
					}
				
					label_TrainStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
					label_TrainStatus.setText("Ready!");
				}
				else {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
			        messageBox.setText("Error Message");
			        messageBox.setMessage("No Train Directory has been selected!");
			        int buttonID = messageBox.open();
			        switch(buttonID) {
			          case SWT.ABORT:
			        }
				}
			}
		});
		menu_Train_Run.setText("Run");
		
		MenuItem mntmNewSubmenu2 = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu2.setText("Test");
		
		Menu menu_2 = new Menu(mntmNewSubmenu2);
		mntmNewSubmenu2.setMenu(menu_2);
		
		MenuItem menu_Test_Open = new MenuItem(menu_2, SWT.NONE);
		menu_Test_Open.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog test_FileDialog = new FileDialog(shell, SWT.OPEN);
				test_FileDialog.setFilterExtensions(new String[] {new String("*.java") });
				test_FileDialog.setFilterPath("sources/TestFiles/");
				String test_Filepath = test_FileDialog.open();
				if(test_Filepath != null) {
					if(sourceDis1Blank) {
						test_FilePath1 = test_Filepath;
						try {
							test_CodeDisplay(table_File1, test_FilePath1);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sourceDis1Blank = false;
						}
					else if(sourceDis2Blank) {
						test_FilePath2 = test_Filepath;
						if(test_FilePath2.equals(test_FilePath1))
							isSingleFile = true;
						else
							isSingleFile = false;
						try {
							test_CodeDisplay(table_File2, test_FilePath2);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sourceDis2Blank = false;
						}
					else {
						test_ClearDisplay();
						test_FilePath1 = test_Filepath;
						try {
							test_CodeDisplay(table_File1, test_FilePath1);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sourceDis1Blank = false;
						}
					}
				}
			});
		menu_Test_Open.setText("Open File...");
		
		MenuItem menu_Test_Clear = new MenuItem(menu_2, SWT.NONE);
		menu_Test_Clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				test_ClearDisplay();
			}
		});
		menu_Test_Clear.setText("Clear Files");
		
		MenuItem menu_Test_Run = new MenuItem(menu_2, SWT.NONE);
		menu_Test_Run.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!sourceDis1Blank) {				
					methodVectorList1.clear();
					methodVectorList2.clear();
					
					tree_Method1.removeAll();
					tree_Method2.removeAll();
					table_Results.removeAll();
					
					methodVectorList1 = parserTool.parseMethod(parserTool.getCompilationUnit(test_FilePath1));
					test_MethodDisplay(tree_Method1, methodVectorList1);
					if(sourceDis2Blank) {
						test_FilePath2 = test_FilePath1;
						try {
							test_CodeDisplay(table_File2, test_FilePath2);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sourceDis2Blank = false;
					}
					methodVectorList2 = parserTool.parseMethod(parserTool.getCompilationUnit(test_FilePath2));
					test_MethodDisplay(tree_Method2, methodVectorList2);
					
//					long start_Time = System.currentTimeMillis();
					if(TRAIN_MODE)
						test_Train_CloneListDisplay();
					else
						test_NoTrain_CloneListDisplay();
//					long end_Time = System.currentTimeMillis();
//					long run_Time = (end_Time - start_Time);
//					System.out.println("Running Time: " + run_Time + "ms");
				}
				else {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
			        messageBox.setText("Error Message");
			        messageBox.setMessage("No Test File has been selected!");
			        int buttonID = messageBox.open();
			        switch(buttonID) {
			          case SWT.ABORT:
			        }
				}
			}
		});
		menu_Test_Run.setText("Run");
		
		Label label_Training = new Label(shell, SWT.NONE);
		GridData gd_label_Training = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_label_Training.horizontalIndent = 5;
		label_Training.setLayoutData(gd_label_Training);
		label_Training.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_Training.setFont(SWTResourceManager.getFont(".SF NS Text", 14, SWT.BOLD));
		label_Training.setText("Training");
		
		Label label_4 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Group group_Training = new Group(shell, SWT.NONE);
		group_Training.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		group_Training.setFont(SWTResourceManager.getFont(".SF NS Text", 14, SWT.BOLD));
		group_Training.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gl_group_Training = new GridLayout(9, false);
		gl_group_Training.marginHeight = 0;
		group_Training.setLayout(gl_group_Training);
	    
		Composite com_TrainFile = new Composite(group_Training, SWT.NONE);
		GridData gd_com_TrainFile = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_com_TrainFile.heightHint = 40;
		gd_com_TrainFile.widthHint = 276;
		com_TrainFile.setLayoutData(gd_com_TrainFile);
		GridLayout gl_com_TrainFile = new GridLayout(1, false);
		com_TrainFile.setLayout(gl_com_TrainFile);
		
		Label lblDirectoryPath = new Label(com_TrainFile, SWT.NONE);
		lblDirectoryPath.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		lblDirectoryPath.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		lblDirectoryPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblDirectoryPath.setText("Training Files");
		
		train_SelectFile = new Text(com_TrainFile, SWT.BORDER);
		train_SelectFile.setEditable(false);
		train_SelectFile.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		train_SelectFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(group_Training, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite com_HiddenNodes = new Composite(group_Training, SWT.NONE);
		com_HiddenNodes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		com_HiddenNodes.setLayout(new GridLayout(3, false));
		new Label(com_HiddenNodes, SWT.NONE);
		
		lable_HiddenNodes = new Label(com_HiddenNodes, SWT.NONE);
		lable_HiddenNodes.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		lable_HiddenNodes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		lable_HiddenNodes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lable_HiddenNodes.setText("Hidden Nodes: 10");
		
		Label label_MinNodes = new Label(com_HiddenNodes, SWT.NONE);
		label_MinNodes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MinNodes.setText("5");
		
		slider_HiddenNodes = new Slider(com_HiddenNodes, SWT.BORDER);
		slider_HiddenNodes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider_HiddenNodes.setToolTipText("");
		slider_HiddenNodes.setThumb(1);
		slider_HiddenNodes.setPageIncrement(1);
		slider_HiddenNodes.setMaximum(11);
		slider_HiddenNodes.setMinimum(0);
		slider_HiddenNodes.setSelection(5);
		
		slider_HiddenNodes.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				train_HiddenNodes = slider_HiddenNodes.getSelection() + 5;
				lable_HiddenNodes.setText("Hidden Nodes: " + train_HiddenNodes);
				}
			}
		);
		
		Label label_MaxNodes = new Label(com_HiddenNodes, SWT.NONE);
		label_MaxNodes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MaxNodes.setText("15");
		
		Label label_2 = new Label(group_Training, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite com_TrainTimes = new Composite(group_Training, SWT.NONE);
		com_TrainTimes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		com_TrainTimes.setLayout(new GridLayout(3, false));
		new Label(com_TrainTimes, SWT.NONE);
		
		label_TrainingTimes = new Label(com_TrainTimes, SWT.NONE);
		label_TrainingTimes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_TrainingTimes.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		label_TrainingTimes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label_TrainingTimes.setText("Training Times: 200");
		
		Label label_MinTimes = new Label(com_TrainTimes, SWT.NONE);
		label_MinTimes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MinTimes.setText("100");
		
		slider_TrainTimes = new Slider(com_TrainTimes, SWT.NONE);
		slider_TrainTimes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider_TrainTimes.setThumb(1);
		slider_TrainTimes.setPageIncrement(1);
		slider_TrainTimes.setMaximum(11);
		slider_TrainTimes.setMinimum(0);
		slider_TrainTimes.setSelection(10);
		
		slider_TrainTimes.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				train_TrainTimes = slider_TrainTimes.getSelection() * 10 + 100 ;
				label_TrainingTimes.setText("Training Times: " + train_TrainTimes);
				}
			}
		);
		
		Label label_MaxTimes = new Label(com_TrainTimes, SWT.NONE);
		label_MaxTimes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MaxTimes.setText("200");
		
		Label label_3 = new Label(group_Training, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite com_Threshold = new Composite(group_Training, SWT.NONE);
		com_Threshold.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		com_Threshold.setLayout(new GridLayout(3, false));
		new Label(com_Threshold, SWT.NONE);
		
		label_Threshold = new Label(com_Threshold, SWT.NONE);
		label_Threshold.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_Threshold.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		label_Threshold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label_Threshold.setText("Threshold: 0.87");
		
		Label label_MinThreshold = new Label(com_Threshold, SWT.NONE);
		label_MinThreshold.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MinThreshold.setText("0.75");
		
		slider_Threshold = new Slider(com_Threshold, SWT.NONE);
		slider_Threshold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider_Threshold.setThumb(1);
		slider_Threshold.setPageIncrement(1);
		slider_Threshold.setMaximum(21);
		slider_Threshold.setMinimum(0);
		slider_Threshold.setSelection(12);
		
		slider_Threshold.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				train_Threshold = (double) ((slider_Threshold.getSelection() + 75) / 100.0);
				label_Threshold.setText("Threshold: " + train_Threshold);
				}
			}
		);
		
		Label label_MaxThreshold = new Label(com_Threshold, SWT.NONE);
		label_MaxThreshold.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MaxThreshold.setText("0.95");
		
		Label label = new Label(group_Training, SWT.SEPARATOR | SWT.VERTICAL);
		
		label_TrainStatus = new Label(group_Training, SWT.NONE);
		label_TrainStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
		label_TrainStatus.setFont(SWTResourceManager.getFont(".SF NS Text", 16, SWT.BOLD));
		label_TrainStatus.setAlignment(SWT.CENTER);
		label_TrainStatus.setText("Untrained!");
		
		Label label_Testing = new Label(shell, SWT.NONE);
		GridData gd_label_Testing = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_label_Testing.horizontalIndent = 5;
		label_Testing.setLayoutData(gd_label_Testing);
		label_Testing.setText("Testing");
		label_Testing.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_Testing.setFont(SWTResourceManager.getFont(".SF NS Text", 14, SWT.BOLD));
		
		Label label_8 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Group group_Testing = new Group(shell, SWT.NONE);
		group_Testing.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		group_Testing.setLayout(new GridLayout(5, false));
	    
		Composite com_File1 = new Composite(group_Testing, SWT.NONE);
		com_File1.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.NORMAL));
		GridData gd_com_File1 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 3);
		gd_com_File1.widthHint = 250;
		com_File1.setLayoutData(gd_com_File1);
		com_File1.setLayout(new GridLayout(1, false));
		
		Label label_File1 = new Label(com_File1, SWT.NONE);
		label_File1.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_File1.setFont(SWTResourceManager.getFont(".SF NS Text", 14, SWT.BOLD));
		label_File1.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		label_File1.setText("File1");
		
		table_File1 = new Table(com_File1, SWT.NONE);
		table_File1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		table_File1.setHeaderVisible(true);
		table_File1.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		
		TableColumn table_File1_ID = new TableColumn(table_File1, SWT.RIGHT | SWT.H_SCROLL);
		table_File1_ID.setWidth(30);
		table_File1_ID.setText("No.");
		
		TableColumn table_File1_Code = new TableColumn(table_File1, SWT.NONE | SWT.H_SCROLL);
		table_File1_Code.setWidth(391);
		table_File1_Code.setText("  Code");
		
		Composite com_File2 = new Composite(group_Testing, SWT.NONE);
		GridData gd_com_File2 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 3);
		gd_com_File2.widthHint = 250;
		com_File2.setLayoutData(gd_com_File2);
		com_File2.setLayout(new GridLayout(1, false));
		
		Label label_File2 = new Label(com_File2, SWT.NONE);
		label_File2.setFont(SWTResourceManager.getFont(".SF NS Text", 14, SWT.BOLD));
		label_File2.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_File2.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		label_File2.setText("File2");
		
		table_File2 = new Table(com_File2, SWT.NONE);
		table_File2.setHeaderVisible(true);
		table_File2.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		GridData gd_table_File2 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_table_File2.widthHint = 500;
		table_File2.setLayoutData(gd_table_File2);
		
		TableColumn table_File2_ID = new TableColumn(table_File2, SWT.RIGHT | SWT.H_SCROLL);
		table_File2_ID.setWidth(30);
		table_File2_ID.setText("No.");
		
		TableColumn table_File2_Code = new TableColumn(table_File2, SWT.NONE | SWT.H_SCROLL);
		table_File2_Code.setWidth(389);
		table_File2_Code.setText("  Code");
		
		Label label_7 = new Label(group_Testing, SWT.SEPARATOR | SWT.VERTICAL);
		label_7.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 3));
		
		Composite com_Method1 = new Composite(group_Testing, SWT.NONE);
		GridData gd_com_Method1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_com_Method1.widthHint = 00;
		com_Method1.setLayoutData(gd_com_Method1);
		com_Method1.setLayout(new GridLayout(1, false));
		
		Label label_Method1 = new Label(com_Method1, SWT.NONE);
		label_Method1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		label_Method1.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_Method1.setFont(SWTResourceManager.getFont(".SF NS Text", 14, SWT.BOLD));
		label_Method1.setText("Method1");
		
		tree_Method1 = new Tree(com_Method1, SWT.BORDER | SWT.FULL_SELECTION);
		tree_Method1.setHeaderVisible(true);
		tree_Method1.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		tree_Method1.setLinesVisible(true);
		GridData gd_table_Method1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_Method1.widthHint = 0;
		tree_Method1.setLayoutData(gd_table_Method1);
		
		TreeColumn tree_Method1_Method = new TreeColumn(tree_Method1, SWT.LEFT);
		tree_Method1_Method.setWidth(213);
		tree_Method1_Method.setText("  Method Tree");
		
		Composite com_Method2 = new Composite(group_Testing, SWT.NONE);
		GridData gd_com_Method2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_com_Method2.widthHint = 0;
		com_Method2.setLayoutData(gd_com_Method2);
		com_Method2.setLayout(new GridLayout(1, false));
		
		Label label_Method2 = new Label(com_Method2, SWT.NONE);
		label_Method2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		label_Method2.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_Method2.setFont(SWTResourceManager.getFont(".SF NS Text", 14, SWT.BOLD));
		label_Method2.setText("Method2");
		
		tree_Method2 = new Tree(com_Method2, SWT.BORDER | SWT.FULL_SELECTION);
		tree_Method2.setHeaderVisible(true);
		tree_Method2.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		tree_Method2.setLinesVisible(true);
		GridData gd_table_Method2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_Method2.widthHint = 0;
		tree_Method2.setLayoutData(gd_table_Method2);
		
		TreeColumn tree_Method2_Method = new TreeColumn(tree_Method2, SWT.LEFT);
		tree_Method2_Method.setWidth(211);
		tree_Method2_Method.setText("  Method Tree");
		
		Label label_6 = new Label(group_Testing, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		Composite com_Results = new Composite(group_Testing, SWT.NONE);
		com_Results.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		com_Results.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		com_Results.setLayout(new GridLayout(1, false));
		
		Label label_Results = new Label(com_Results, SWT.NONE);
		label_Results.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		label_Results.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_Results.setFont(SWTResourceManager.getFont(".SF NS Text", 14, SWT.BOLD));
		label_Results.setText("Results");
		
		table_Results = new Table(com_Results, SWT.BORDER | SWT.FULL_SELECTION);
		table_Results.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table_Results.getSelectionIndex() != -1) {
					test_ClonePairDisplay();
					}
				}
			});
		table_Results.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		table_Results.setHeaderVisible(true);
		table_Results.setLinesVisible(true);
		GridData gd_table_Results = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_Results.widthHint = 0;
		table_Results.setLayoutData(gd_table_Results);
		
		TableColumn table_Results_ID = new TableColumn(table_Results, SWT.LEFT);
		table_Results_ID.setWidth(25);
		table_Results_ID.setText("No.");
		
		TableColumn table_Results_Sim = new TableColumn(table_Results, SWT.LEFT);
		table_Results_Sim.setWidth(40);
		table_Results_Sim.setText("Sim");
		
		TableColumn table_Results_M1 = new TableColumn(table_Results, SWT.LEFT);
		table_Results_M1.setWidth(108);
		table_Results_M1.setText("Method 1");
		
		TableColumn table_Results_M1S = new TableColumn(table_Results, SWT.LEFT);
		table_Results_M1S.setWidth(40);
		table_Results_M1S.setText("Start");
		
		TableColumn table_Results_M1E = new TableColumn(table_Results, SWT.LEFT);
		table_Results_M1E.setWidth(40);
		table_Results_M1E.setText("End");
		
		TableColumn table_Results_M2 = new TableColumn(table_Results, SWT.LEFT);
		table_Results_M2.setWidth(108);
		table_Results_M2.setText("Method 2");
		
		TableColumn table_Results_M2S = new TableColumn(table_Results, SWT.LEFT);
		table_Results_M2S.setWidth(40);
		table_Results_M2S.setText("Start");
		
		TableColumn table_Results_M2E = new TableColumn(table_Results, SWT.LEFT);
		table_Results_M2E.setWidth(40);
		table_Results_M2E.setText("End");
		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
			}
		display.dispose();
	}
	
	public static void read_TrainData(String train_FilePath) throws IOException {
		
		String[] fileNameList = train_FilePath.split(" ");
		List<String> list = new ArrayList<String>();
		
		for (int i = 0; i < fileNameList.length; i++) {
			FileInputStream fis = new FileInputStream(train_DirPath + "/" + fileNameList[i]);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			String currentLine = "";
			while((currentLine = br.readLine()) != null) {
				list.add(currentLine);
			}
			fis.close();
		}
		
		int count_One = 0;
		int count_Zero = 0;
		int sub_Count = 0;
		
		train_Sim = new double[200][9];
		train_Output = new int[200][1];
		for(int index = 0; index < list.size(); index++) {
			String[] currentPara = list.get(index).split(",");
			if(Integer.parseInt(currentPara[9]) == 1 && count_One < 50) {
				train_Output[sub_Count][0] = Integer.parseInt(currentPara[9]);
				for(int pos = 0; pos < 9; pos++)
					train_Sim[sub_Count][pos] = Double.parseDouble(currentPara[pos]);
				count_One++;
				sub_Count++;
			}
			else if(Integer.parseInt(currentPara[9]) == 0 && count_Zero < 150) {
				train_Output[sub_Count][0] = Integer.parseInt(currentPara[9]);
				for(int pos = 0; pos < 9; pos++)
					train_Sim[sub_Count][pos] = Double.parseDouble(currentPara[pos]);
				count_Zero++;
				sub_Count++;
			}
			else
				continue;
		}
	}
	
	public static void test_CodeDisplay(Table tb, String filePath) throws IOException {
		int lineCount = 1;
		String readLine = "";
		FileInputStream fis = new FileInputStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		while((readLine = br.readLine()) != null) {
			readLine = readLine.replaceAll("\t", "        ");
			TableItem it = new TableItem(tb, SWT.NONE);
			it.setText(new String[]{String.valueOf(lineCount++), "  " + readLine});
		}
		fis.close();
	}
	
	public static void test_ClearDisplay() {
		table_File1.removeAll();
		table_File2.removeAll();
		
		tree_Method1.removeAll();
		tree_Method2.removeAll();
		
		table_Results.removeAll();
		
		sourceDis1Blank = true;
		sourceDis2Blank = true;
		
		isSingleFile = true;
		
		test_FilePath1 = "";
		test_FilePath2 = "";
	}
	
	public static void test_MethodDisplay(Tree tr, MethodList mList) {

		for(int index = 0; index < mList.size(); index++) {
			TreeItem it = new TreeItem(tr, 0);
			it.setText("  " + mList.getMethodVector(index).methodName);
			
			test_MethodAddInfo(it, mList, index, "Num");
			test_MethodAddInfo(it, mList, index, "Type");
			test_MethodAddInfo(it, mList, index, "Keyword");
			test_MethodAddInfo(it, mList, index, "Marker");
			test_MethodAddInfo(it, mList, index, "Operator");
			test_MethodAddInfo(it, mList, index, "OtherStr");
			test_MethodAddInfo(it, mList, index, "OtherChar");
		}
	}
	
	public static void test_MethodAddInfo(TreeItem it, MethodList mList, int index, String str) {
		int size = mList.getMethodVector(index).methodTokenList.getListByType(str).size();
		if(size != 0) {
			TreeItem it1 = new TreeItem(it, 0);
			it1.setText(str);				
			for(int index1 = 0; index1 < size; index1++) {
				TreeItem it2 = new TreeItem(it1, 0);
				TokenVector tv = mList.getMethodVector(index).methodTokenList.getListByType(str).getTokenVector(index1);
				it2.setText(String.format("%d\t\t%s",  tv.TokenCount, tv.TokenName));
		    }
		}
	}
	
	public static void test_Train_CloneListDisplay() {
		MethodSimilarity methodSim = new MethodSimilarity();
		List<Result> rList = new ArrayList<Result>();
		if(isSingleFile)
			rList = methodSim.simDetectorMLP(MLP, train_Threshold, methodVectorList1);
		else
			rList = methodSim.simDetectorMLP(MLP, train_Threshold, methodVectorList1, methodVectorList2);
//		System.out.println("Methods Num: " + methodVectorList1.size());
		for(int index = 0; index < rList.size(); index++) {
			TableItem it = new TableItem(table_Results, SWT.NONE);
			it.setText(new String[]{
					String.valueOf(rList.get(index).index),
	    			String.valueOf((double)(Math.round(rList.get(index).similarity * 100) / 100.0)),
	    			rList.get(index).methodName1,
	    			String.valueOf(rList.get(index).startLineNum1),
	    			String.valueOf(rList.get(index).endLineNum1 + 1),
	    			rList.get(index).methodName2,
	    			String.valueOf(rList.get(index).startLineNum2),
	    			String.valueOf(rList.get(index).endLineNum2 + 1)
	    			});
		}
	}
	
	public static void test_NoTrain_CloneListDisplay() {
		MethodSimilarity methodSim = new MethodSimilarity();
		List<Result> rList = new ArrayList<Result>();
		if(isSingleFile)
			rList = methodSim.simDetector(methodVectorList1);
		else
			rList = methodSim.simDetector(methodVectorList1, methodVectorList2);
//		System.out.println("Methods Num: " + methodVectorList1.size());
		for(int index = 0; index < rList.size(); index++) {
			TableItem it = new TableItem(table_Results, SWT.NONE);
			it.setText(new String[]{
					String.valueOf(rList.get(index).index),
	    			String.valueOf((double)(Math.round(rList.get(index).similarity * 100) / 100.0)),
	    			rList.get(index).methodName1,
	    			String.valueOf(rList.get(index).startLineNum1),
	    			String.valueOf(rList.get(index).endLineNum1 + 1),
	    			rList.get(index).methodName2,
	    			String.valueOf(rList.get(index).startLineNum2),
	    			String.valueOf(rList.get(index).endLineNum2 + 1)
	    			});
		}
	}
	
	public static void test_ClonePairDisplay() {
		int color_Index;
		for(color_Index = 0; color_Index < table_File1.getItemCount(); color_Index++)
			table_File1.getItem(color_Index).setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		for(color_Index = 0; color_Index < table_File2.getItemCount(); color_Index++)
			table_File2.getItem(color_Index).setBackground(1, Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		Rectangle rect = table_File1.getClientArea ();
		int itemHeight = table_File1.getItemHeight ();
		int headerHeight = table_File1.getHeaderHeight ();
		int visibleCount = (rect.height - headerHeight + itemHeight - 1) / itemHeight;
		
        TableItem item = table_Results.getItem(table_Results.getSelectionIndex());
        
        int test_ColorLine1 = Integer.valueOf(item.getText(3)) - 1;
		int test_ScrollLine1 = Integer.valueOf(item.getText(4));
		table_File1.setSelection(0);
		if(test_ColorLine1 + visibleCount - 5 < table_File1.getItemCount())
			table_File1.setSelection(test_ColorLine1 + visibleCount - 5);
		else
			table_File1.setSelection(test_ScrollLine1);
		table_File1.deselect(table_File1.getSelectionIndex());
		
		int test_ColorLine2 = Integer.valueOf(item.getText(6)) - 1;
		int test_ScrollLine2 = Integer.valueOf(item.getText(7));
		table_File2.setSelection(0);
		if(test_ColorLine2 + visibleCount - 5 < table_File2.getItemCount())
			table_File2.setSelection(test_ColorLine2 + visibleCount - 5);
		else
			table_File2.setSelection(test_ScrollLine2);
		table_File2.deselect(table_File2.getSelectionIndex());
		
		for(color_Index = test_ColorLine1; color_Index < test_ScrollLine1; color_Index++)
			table_File1.getItem(color_Index).setBackground(1, new Color(Display.getCurrent(), 200, 255, 200));
		for(color_Index = test_ColorLine2; color_Index < test_ScrollLine2; color_Index++)
			table_File2.getItem(color_Index).setBackground(1, new Color(Display.getCurrent(), 255, 200, 200));
	}
}