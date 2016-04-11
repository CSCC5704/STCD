package cs5704.project;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public class CCDTool{
	private static Text text_SelectFile;
	private static Table table_File1;
	private static Table table_File2;
	private static Table table_Method1;
	private static Table table_Method2;
	private static Table table_Results;
	
	public static int train_HiddenNodes, train_TrainTimes;
	public static float train_Threshold;
	
	private static boolean sourceDis1Blank = true;
	private static boolean sourceDis2Blank = true;
	
	private static String filePath1 = "", filePath2 = "";
	private static boolean isSingleFile = true;
	
	public static ASTParserTool parserTool = new ASTParserTool();
	public static MethodList methodVectorList1 = new MethodList();
	public static MethodList methodVectorList2 = new MethodList();

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub		
		Display display = new Display();
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
				FileDialog train_FileDialog = new FileDialog(shell, SWT.OPEN);
				String train_Filepath = train_FileDialog.open();
			}
		});
		menu_Train_Open.setText("Open File...");
		
		MenuItem menu_Train_Run = new MenuItem(menu_1, SWT.NONE);
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
				test_FileDialog.setFilterPath("sources/TestFiles/");
				String test_Filepath = test_FileDialog.open();
				if(test_Filepath != null) {
					if(sourceDis1Blank) {
						filePath1 = test_Filepath;
						try {
							test_CodeDisplay(table_File1, filePath1);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sourceDis1Blank = false;
						}
					else if(sourceDis2Blank) {
						filePath2 = test_Filepath;
						if(filePath2.equals(filePath1))
							isSingleFile = true;
						else
							isSingleFile = false;
						try {
							test_CodeDisplay(table_File2, filePath2);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sourceDis2Blank = false;
						}
					else {
						clearDisplay();
						filePath1 = test_Filepath;
						try {
							test_CodeDisplay(table_File1, filePath1);
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
				clearDisplay();
			}
		});
		menu_Test_Clear.setText("Clear Files");
		
		MenuItem menu_Test_Run = new MenuItem(menu_2, SWT.NONE);
		menu_Test_Run.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!sourceDis1Blank) {
					methodVectorList1 = parserTool.parseMethod(parserTool.getCompilationUnit(filePath1));
					test_MethodDisplay(table_Method1, methodVectorList1);
					if(sourceDis2Blank) {
						filePath2 = filePath1;
						try {
							test_CodeDisplay(table_File2, filePath2);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sourceDis2Blank = false;
					}
					methodVectorList2 = parserTool.parseMethod(parserTool.getCompilationUnit(filePath2));
					test_MethodDisplay(table_Method2, methodVectorList2);
					
					cloneListDisplay(table_Results);
				}
				else {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
			        messageBox.setText("Error Message");
			        messageBox.setMessage("No File has been selected!");
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
		
		Label label_SelectFile = new Label(com_TrainFile, SWT.NONE);
		label_SelectFile.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_SelectFile.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		label_SelectFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		label_SelectFile.setText("Select File");
		
		text_SelectFile = new Text(com_TrainFile, SWT.BORDER);
		text_SelectFile.setEditable(false);
		text_SelectFile.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		text_SelectFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(group_Training, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite com_HiddenNodes = new Composite(group_Training, SWT.NONE);
		com_HiddenNodes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		com_HiddenNodes.setLayout(new GridLayout(3, false));
		new Label(com_HiddenNodes, SWT.NONE);
		
		Label lable_HiddenNodes = new Label(com_HiddenNodes, SWT.NONE);
		lable_HiddenNodes.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		lable_HiddenNodes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		lable_HiddenNodes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lable_HiddenNodes.setText("Hidden Nodes: 5");
		
		Label label_MinNodes = new Label(com_HiddenNodes, SWT.NONE);
		label_MinNodes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MinNodes.setText("5");
		
		Slider slider_HiddenNodes = new Slider(com_HiddenNodes, SWT.BORDER);
		slider_HiddenNodes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider_HiddenNodes.setToolTipText("");
		slider_HiddenNodes.setThumb(1);
		slider_HiddenNodes.setPageIncrement(1);
		slider_HiddenNodes.setMaximum(11);
		slider_HiddenNodes.setMinimum(0);
		slider_HiddenNodes.setSelection(0);
		
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
		
		Label label_TrainingTimes = new Label(com_TrainTimes, SWT.NONE);
		label_TrainingTimes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_TrainingTimes.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		label_TrainingTimes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label_TrainingTimes.setText("Training Times: 100");
		
		Label label_MinTimes = new Label(com_TrainTimes, SWT.NONE);
		label_MinTimes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MinTimes.setText("100");
		
		Slider slider_TrainTimes = new Slider(com_TrainTimes, SWT.NONE);
		slider_TrainTimes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider_TrainTimes.setThumb(1);
		slider_TrainTimes.setPageIncrement(1);
		slider_TrainTimes.setMaximum(9);
		slider_TrainTimes.setMinimum(0);
		slider_TrainTimes.setSelection(0);
		
		slider_TrainTimes.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				train_TrainTimes = 50 * (slider_TrainTimes.getSelection() + 2);
				label_TrainingTimes.setText("Training Times: " + train_TrainTimes);
				}
			}
		);
		
		Label label_MaxTimes = new Label(com_TrainTimes, SWT.NONE);
		label_MaxTimes.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MaxTimes.setText("500");
		
		Label label_3 = new Label(group_Training, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite com_Threshold = new Composite(group_Training, SWT.NONE);
		com_Threshold.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		com_Threshold.setLayout(new GridLayout(3, false));
		new Label(com_Threshold, SWT.NONE);
		
		Label label_Threshold = new Label(com_Threshold, SWT.NONE);
		label_Threshold.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_Threshold.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		label_Threshold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label_Threshold.setText("Threshold: 0.5");
		
		Label label_MinThreshold = new Label(com_Threshold, SWT.NONE);
		label_MinThreshold.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MinThreshold.setText("0.5");
		
		Slider slider_Threshold = new Slider(com_Threshold, SWT.NONE);
		slider_Threshold.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider_Threshold.setThumb(1);
		slider_Threshold.setPageIncrement(1);
		slider_Threshold.setMaximum(21);
		slider_Threshold.setMinimum(0);
		
		slider_Threshold.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				train_Threshold = (float) (0.01 * slider_Threshold.getSelection() + 0.5);
				label_Threshold.setText("Threshold: " + train_Threshold);
				}
			}
		);
		
		Label label_MaxThreshold = new Label(com_Threshold, SWT.NONE);
		label_MaxThreshold.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_MaxThreshold.setText("0.7");
		
		Label label = new Label(group_Training, SWT.SEPARATOR | SWT.VERTICAL);
		
		Label label_TrainStatus = new Label(group_Training, SWT.NONE);
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
		label_File1.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
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
		label_File2.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
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
		label_Method1.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		label_Method1.setText("Method1");
		
		table_Method1 = new Table(com_Method1, SWT.BORDER | SWT.FULL_SELECTION);
		table_Method1.setHeaderVisible(true);
		table_Method1.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		table_Method1.setLinesVisible(true);
		GridData gd_table_Method1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_Method1.widthHint = 0;
		table_Method1.setLayoutData(gd_table_Method1);
		
		TableColumn table_Method1_ID = new TableColumn(table_Method1, SWT.CENTER);
		table_Method1_ID.setWidth(25);
		table_Method1_ID.setText("No.");
		
		TableColumn table_Method1_Method = new TableColumn(table_Method1, SWT.NONE);
		table_Method1_Method.setWidth(188);
		table_Method1_Method.setText("  Method");
		
		Composite com_Method2 = new Composite(group_Testing, SWT.NONE);
		GridData gd_com_Method2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_com_Method2.widthHint = 0;
		com_Method2.setLayoutData(gd_com_Method2);
		com_Method2.setLayout(new GridLayout(1, false));
		
		Label label_Method2 = new Label(com_Method2, SWT.NONE);
		label_Method2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		label_Method2.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_Method2.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		label_Method2.setText("Method2");
		
		table_Method2 = new Table(com_Method2, SWT.BORDER | SWT.FULL_SELECTION);
		table_Method2.setHeaderVisible(true);
		table_Method2.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		table_Method2.setLinesVisible(true);
		GridData gd_table_Method2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_Method2.widthHint = 0;
		table_Method2.setLayoutData(gd_table_Method2);
		
		TableColumn table_Method2_ID = new TableColumn(table_Method2, SWT.CENTER);
		table_Method2_ID.setWidth(25);
		table_Method2_ID.setText("No.");
		
		TableColumn table_Method2_Method = new TableColumn(table_Method2, SWT.NONE);
		table_Method2_Method.setWidth(186);
		table_Method2_Method.setText("  Method");
		
		Label label_6 = new Label(group_Testing, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		Composite com_Results = new Composite(group_Testing, SWT.NONE);
		com_Results.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		com_Results.setLayout(new GridLayout(1, false));
		
		Label label_Results = new Label(com_Results, SWT.NONE);
		label_Results.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		label_Results.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		label_Results.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		label_Results.setText("Results");
		
		table_Results = new Table(com_Results, SWT.BORDER | SWT.FULL_SELECTION);
		table_Results.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table_Results.getSelectionIndex() != -1) {
					test_clonePairsDisplay();
					}
				}
			});
		table_Results.setFont(SWTResourceManager.getFont(".SF NS Text", 10, SWT.NORMAL));
		table_Results.setHeaderVisible(true);
		table_Results.setLinesVisible(true);
		GridData gd_table_Results = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_Results.widthHint = 0;
		table_Results.setLayoutData(gd_table_Results);
		
		TableColumn table_Results_ID = new TableColumn(table_Results, SWT.CENTER);
		table_Results_ID.setWidth(25);
		table_Results_ID.setText("No.");
		
		TableColumn table_Results_Sim = new TableColumn(table_Results, SWT.CENTER);
		table_Results_Sim.setWidth(40);
		table_Results_Sim.setText("Sim");
		
		TableColumn table_Results_M1 = new TableColumn(table_Results, SWT.CENTER);
		table_Results_M1.setWidth(108);
		table_Results_M1.setText("Method 1");
		
		TableColumn table_Results_M1S = new TableColumn(table_Results, SWT.CENTER);
		table_Results_M1S.setWidth(40);
		table_Results_M1S.setText("Start");
		
		TableColumn table_Results_M1E = new TableColumn(table_Results, SWT.CENTER);
		table_Results_M1E.setWidth(40);
		table_Results_M1E.setText("End");
		
		TableColumn table_Results_M2 = new TableColumn(table_Results, SWT.CENTER);
		table_Results_M2.setWidth(108);
		table_Results_M2.setText("Method 2");
		
		TableColumn table_Results_M2S = new TableColumn(table_Results, SWT.CENTER);
		table_Results_M2S.setWidth(40);
		table_Results_M2S.setText("Start");
		
		TableColumn table_Results_M2E = new TableColumn(table_Results, SWT.CENTER);
		table_Results_M2E.setWidth(40);
		table_Results_M2E.setText("End");
		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
			}
		display.dispose();
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
	
	public static void clearDisplay() {
		table_File1.removeAll();
		table_File2.removeAll();
		
		sourceDis1Blank = true;
		sourceDis2Blank = true;
		
		isSingleFile = true;
		
		filePath1 = "";
		filePath2 = "";
	}
	
	public static void test_MethodDisplay(Table tb, MethodList mList) {

		for(int index = 0; index < mList.size(); index++) {
			TableItem it = new TableItem(tb, SWT.NONE);
			it.setText(new String[]{String.valueOf(index + 1), mList.getMethodVector(index).methodName});
		}
	}
	
	public static void cloneListDisplay(Table tb) {
		MethodSimilarity methodSim = new MethodSimilarity();
		List<Result> rList = new ArrayList<Result>();
		if(isSingleFile)
			rList = methodSim.simDetector(methodVectorList1);
		else
			rList = methodSim.simDetector(methodVectorList1, methodVectorList2);
		for(int index = 0; index < rList.size(); index++) {
			TableItem it = new TableItem(tb, SWT.NONE);
			it.setText(new String[]{
					String.valueOf(rList.get(index).index),
	    			String.valueOf((double)(Math.round(rList.get(index).similarity * 100)/100.0)),
	    			rList.get(index).methodName1,
	    			String.valueOf(rList.get(index).startLineNum1),
	    			String.valueOf(rList.get(index).endLineNum1 + 1),
	    			rList.get(index).methodName2,
	    			String.valueOf(rList.get(index).startLineNum2),
	    			String.valueOf(rList.get(index).endLineNum2 + 1)
	    			});
		}
	}
	
	public static void test_clonePairsDisplay() {
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
		table_File1.setSelection(test_ColorLine1 + visibleCount - 5);
		table_File1.deselect(table_File1.getSelectionIndex());
		
		int test_ColorLine2 = Integer.valueOf(item.getText(6)) - 1;
		int test_ScrollLine2 = Integer.valueOf(item.getText(7));
		table_File2.setSelection(0);
		table_File2.setSelection(test_ColorLine2 + visibleCount - 5);
		table_File2.deselect(table_File2.getSelectionIndex());
		
		for(color_Index = test_ColorLine1; color_Index < test_ScrollLine1; color_Index++)
			table_File1.getItem(color_Index).setBackground(1, new Color(Display.getCurrent(), 200, 255, 200));
		for(color_Index = test_ColorLine2; color_Index < test_ScrollLine2; color_Index++)
			table_File2.getItem(color_Index).setBackground(1, new Color(Display.getCurrent(), 255, 200, 200));
	}
}