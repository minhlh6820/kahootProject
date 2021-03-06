package play;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import client.Client;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.List;

class Question {
	private String question;
	private String a,b,c,d;
	private String answer;
	
	public Question(String Question, String A, String B, String C, String D, String Answer) {
		this.question = Question;
		this.a = A;
		this.b = B;
		this.c = C;
		this.d = D;
		this.answer = Answer;
	}
	
	public String getQuestion() {
		return this.question;
	}
	
	public String getA() {
		return this.a;
	}
	
	public String getB() {
		return this.b;
	}
	
	public String getC() {
		return this.c;
	}
	
	public String getD() {
		return this.d;
	}
	
	public String getAnswer() {
		return this.answer;
	}
}
public class CreateQuestionWindow {

	protected Shell shell;
	private String clientName;
	//private String room;
	private String topicName;
	private String answer;
	
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	
	public void setClientName(String name) {
		this.clientName = name;
	}
	
	public void setTopic(String topic) {
		this.topicName = topic;
	}
	
//	public void setRoom(String room) {
//		this.room = room;
//	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CreateQuestionWindow window = new CreateQuestionWindow();
			window.setClientName("abcd");
			//window.setRoom("1");
			//window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open(Client client) {
		Display display = Display.getDefault();
		createContents(display, client);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display, Client client) {
		if(shell == null) shell = new Shell();
		shell.setSize(1350, 700);
		shell.setText("Create questions");
		
		Composite questionComposite = new Composite(shell, SWT.NONE);
		questionComposite.setBounds(10, 139, 1314, 413);
		
		Label lblQuestion = new Label(questionComposite, SWT.NONE);
		lblQuestion.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblQuestion.setBounds(20, 64, 82, 32);
		lblQuestion.setText("Question: ");
		
		Text questionTxt = new Text(questionComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		questionTxt.setBounds(150, 64, 1096, 59);
		
		Label lblChoices = new Label(questionComposite, SWT.NONE);
		lblChoices.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblChoices.setBounds(20, 144, 82, 32);
		lblChoices.setText("Choices: ");
		
		Button btnA = new Button(questionComposite, SWT.RADIO);
		btnA.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source=  (Button) e.getSource();
				answer = source.getText();
			}
		});
		btnA.setBounds(96, 182, 35, 15);
		btnA.setText("A");
		btnA.setSelection(false);
		
		Button btnB = new Button(questionComposite, SWT.RADIO);
		btnB.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source=  (Button) e.getSource();
				answer = source.getText();
			}
		});
		btnB.setText("B");
		btnB.setBounds(792, 181, 35, 16);
		btnB.setSelection(false);
		
		Button btnC = new Button(questionComposite, SWT.RADIO);
		btnC.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source=  (Button) e.getSource();
				answer = source.getText();
			}
		});
		btnC.setText("C");
		btnC.setBounds(96, 289, 35, 16);
		btnC.setSelection(false);
		
		Button btnD = new Button(questionComposite, SWT.RADIO);
		btnD.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnD.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source=  (Button) e.getSource();
				answer = source.getText();
			}
		});
		btnD.setText("D");
		btnD.setBounds(792, 289, 35, 16);
		btnD.setSelection(false);
		
		Text ATxt = new Text(questionComposite, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		ATxt.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL ));
		ATxt.setBounds(150, 159, 397, 59);
		
		Text BTxt = new Text(questionComposite, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		BTxt.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		BTxt.setBounds(849, 159, 397, 59);
		
		Text CTxt = new Text(questionComposite, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		CTxt.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		CTxt.setBounds(150, 265, 397, 59);
		
		Text DTxt = new Text(questionComposite, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		DTxt.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		DTxt.setBounds(849, 265, 397, 59);
		
		Color red = new Color(display, 255, 0, 0);
		Color green = new Color(display, 0, 255, 0);
		
		Label lblError = new Label(questionComposite, SWT.NONE);
		lblError.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblError.setBounds(150, 356, 520, 49);
		lblError.setText("");
		
		Composite headerComposite = new Composite(shell, SWT.NONE);
		headerComposite.setBounds(10, 10, 1314, 110);
		
		Label lblUser = new Label(headerComposite, SWT.NONE);
		lblUser.setFont(SWTResourceManager.getFont("Times New Roman", 15, SWT.BOLD));
		lblUser.setAlignment(SWT.CENTER);
		lblUser.setBounds(527, 10, 259, 40);
		lblUser.setText("User: " + clientName);
		
		Label lblEnterTopic = new Label(headerComposite, SWT.NONE);
		lblEnterTopic.setFont(SWTResourceManager.getFont("Times New Roman", 15, SWT.BOLD));
		lblEnterTopic.setAlignment(SWT.CENTER);
		lblEnterTopic.setBounds(514, 65, 283, 35);
		lblEnterTopic.setText("Topic: " + topicName);
		
		Composite btnComposite = new Composite(shell, SWT.NONE);
		btnComposite.setBounds(10, 570, 1314, 81);
			
		Button btnConfirm = new Button(btnComposite, SWT.NONE);
		btnConfirm.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				System.out.println("You've just created questions for topic " + topic);
//				Click exit to back to Client window
				try {
					for (Control kid : shell.getChildren()) {
				         kid.dispose();
				    }
					
					//String loginMsg = loginMsg(name, password);
					ClientWindow clientWindow = new ClientWindow();
					clientWindow.setShell(shell);
					clientWindow.setClientName(clientName);
					clientWindow.open(client);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		btnConfirm.setBounds(1126, 10, 128, 56);
		btnConfirm.setText("Confirm");
		btnConfirm.setEnabled(false);
		
		Button btnCreate = new Button(btnComposite, SWT.NONE);
		btnCreate.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//topicName = topicTxt.getText();
				String question = questionTxt.getText();
				String a = ATxt.getText();
				String b = BTxt.getText();
				String c = CTxt.getText();
				String d = DTxt.getText();
				
				btnA.setSelection(false);
				btnB.setSelection(false);
				btnC.setSelection(false);
				btnD.setSelection(false);
				
				btnConfirm.setEnabled(true);
				
				if(isEmpty(topicName) || isEmpty(question) || isEmpty(a) || isEmpty(b) || isEmpty(c) || isEmpty(d) || isEmpty(answer)) {
					lblError.setForeground(red);
					lblError.setText("Error!");
				} else {
					try {
//						System.out.println(client.createQuestionMsg(topicName, question, a, b, c, d, answer));
						client.dos.writeUTF(client.createQuestionMsg(topicName, question, a, b, c, d, answer));
						System.out.println(client.dis.readUTF());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					try {
						Question q = new Question(question, a, b, c, d, answer);
						lblError.setForeground(green);
						lblError.setText("Create success");
						
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
//					System.out.println("Question " + q.getQuestion() + " is created");
//					System.out.println(q.getAnswer());
					
					//Reset text to enter new question
					lblError.setText("");
					questionTxt.setText("");
					ATxt.setText("");
					BTxt.setText("");
					CTxt.setText("");
					DTxt.setText("");
					answer = "";
				}
			}
		});
		btnCreate.setBounds(929, 10, 128, 56);
		btnCreate.setText("Create");
		
//		Button btnBack = new Button(btnComposite, SWT.NONE);
//		btnBack.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				//Go to previous window (Create topic)
//				try {
//					for (Control kid : shell.getChildren()) {
//				          kid.dispose();
//				    }
//					CreateTopicWindow createTopicWindow = new CreateTopicWindow();
//					createTopicWindow.setShell(shell);
//					createTopicWindow.setClientName(clientName);
//					createTopicWindow.open(client);
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//		});
//		btnBack.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
//		btnBack.setBounds(38, 10, 128, 56);
//		btnBack.setText("Back");
		
	}
	
	private boolean isEmpty(String str) {
		if(str == null) return true;
		if(str != null && str == "") return true;
		return false;
	}
}
