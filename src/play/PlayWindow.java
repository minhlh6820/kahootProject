package play;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import client.Client;

import org.eclipse.swt.widgets.Group;
class Player {
	private String playerName;
	private int score;
	
	
	public Player(String name, int score) {
		this.playerName = name;
		this.score = score;
	}
	
	public String getPlayerName() {
		return this.playerName;
	}
	
	public int getScore() {
		return this.score;
	}
	
}

public class PlayWindow {

	protected Shell shell;
	private String room;
	private String clientName;
	private ArrayList<Question> questions;
	private ArrayList<Player> playerList;
	private int score = 0;
	private int index;
	private long startTime; //Time when starting to answer each question
	private Runnable barRunnable;
	
	
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	
	public void setClientName(String name) {
		this.clientName = name;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}
	
	public ArrayList<Question> getQuestions(String sRep) {
		//CODE HERE
		//Return array list of all questions of given room from DB
		String[] parts = sRep.split("--");
		ArrayList<Question> arq = new ArrayList<Question>();
		for (int i = 1; i < parts.length; i+= 6) {
			Question q = new Question(parts[i],parts[i+1],parts[i+2], parts[i+3], parts[i+4], parts[i+5]);
			arq.add(q);
		}
		return arq;
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PlayWindow window = new PlayWindow();
		//	window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open(Client client) {
		Display display = Display.getDefault();
		String sRep = null;
		//Send request to server to get roomlist
		try {
			client.dos.writeUTF(client.getQuestionListMsg(room));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sRep = client.dis.readUTF();
			System.out.println(sRep);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		createContents(display, client, sRep);
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
	protected void createContents(Display display, Client client, String sRep) {
		questions = getQuestions(sRep);
		if(shell == null) shell = new Shell();
		shell.setSize(780, 480);
		shell.setText("Playing");
		shell.setLayout(new RowLayout(SWT.HORIZONTAL));
				
		index = 0;
		
		Color red = new Color(display, 255, 0, 0);
		Color green = new Color(display, 0, 255, 0);
		
		Composite lbComposite = new Composite(shell, SWT.BORDER);
		lbComposite.setLayoutData(new RowData(223, 421));
		
		Label lblLeaderboard = new Label(lbComposite, SWT.NONE);
		lblLeaderboard.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblLeaderboard.setAlignment(SWT.CENTER);
		lblLeaderboard.setBounds(46, 10, 127, 27);
		lblLeaderboard.setText("Leaderboard");
		
		Composite tableComposite = new Composite(lbComposite, SWT.NONE);
		tableComposite.setBounds(10, 73, 203, 276);
		
		Table table = new Table(tableComposite, SWT.BORDER | SWT.HIDE_SELECTION | SWT.V_SCROLL);
		table.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		table.setBounds(10, 22, 183, 230);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnPlayer = new TableColumn(table, SWT.CENTER);
		tblclmnPlayer.setWidth(92);
		tblclmnPlayer.setText("Player");
		
		TableColumn tblclmnScore = new TableColumn(table, SWT.CENTER);
		tblclmnScore.setWidth(85);
		tblclmnScore.setText("Score");
		
		Label lblUpdate = new Label(lbComposite, SWT.NONE);
		lblUpdate.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		lblUpdate.setAlignment(SWT.CENTER);
		lblUpdate.setBounds(60, 43, 101, 24);
		lblUpdate.setText("Update");
		
		Composite exitComposite = new Composite(lbComposite, SWT.NONE);
		exitComposite.setBounds(71, 365, 127, 46);
		
		Button btnExit = new Button(exitComposite, SWT.NONE);
		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Click exit to back to Client window
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
		btnExit.setEnabled(false);
		btnExit.setBounds(10, 10, 107, 26);
		btnExit.setText("Exit");
		
		Composite answerComposite = new Composite(shell, SWT.NONE);
		answerComposite.setLayoutData(new RowData(529, 424));
		
		Composite topComposite = new Composite(answerComposite, SWT.NONE);
		topComposite.setBounds(10, 10, 509, 125);
		
		Label lblPlayer = new Label(topComposite, SWT.NONE);
		lblPlayer.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblPlayer.setAlignment(SWT.CENTER);
		lblPlayer.setBounds(32, 20, 157, 29);
		lblPlayer.setText("Player: ");
		
		Label lblRoom = new Label(topComposite, SWT.NONE);
		lblRoom.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblRoom.setAlignment(SWT.CENTER);
		lblRoom.setBounds(325, 20, 136, 29);
		lblRoom.setText("Room: ");
		
		Label lblQuestion = new Label(topComposite, SWT.NONE);
		lblQuestion.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblQuestion.setAlignment(SWT.CENTER);
		lblQuestion.setBounds(202, 53, 107, 29);
		lblQuestion.setText("Question " + (index+1));
		
		Label lblAnswer = new Label(topComposite, SWT.NONE);
		lblAnswer.setBounds(302, 88, 174, 27);
		lblAnswer.setText("");
		
		Label lblAnswersTime = new Label(topComposite, SWT.NONE);
		lblAnswersTime.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblAnswersTime.setBounds(32, 88, 136, 27);
		lblAnswersTime.setText("");
		
		ProgressBar timeBar = new ProgressBar(topComposite, SWT.SMOOTH);
		timeBar.setBounds(32, 53, 170, 17);
		timeBar.setForeground(green);
		timeBar.setMaximum(72*4);
		
		Composite questionComposite = new Composite(answerComposite, SWT.NONE);
		questionComposite.setBounds(10, 141, 509, 252);
		
		Label lblQuestion_1 = new Label(questionComposite, SWT.NONE);
		lblQuestion_1.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblQuestion_1.setBounds(11, 57, 74, 31);
		lblQuestion_1.setText("Question: ");
		
		Text text = new Text(questionComposite, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		text.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		text.setBounds(91, 26, 394, 88);
		text.setText(questions.get(index).getQuestion());
		
		Text ATxt = new Text(questionComposite, SWT.BORDER | SWT.READ_ONLY);
		ATxt.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		ATxt.setBounds(91, 141, 150, 31);
		ATxt.setText(questions.get(index).getA());
		
		Text BTxt = new Text(questionComposite, SWT.BORDER | SWT.READ_ONLY);
		BTxt.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		BTxt.setBounds(322, 141, 150, 28);
		BTxt.setText(questions.get(index).getB());
		
		Text CTxt = new Text(questionComposite, SWT.BORDER | SWT.READ_ONLY);
		CTxt.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		CTxt.setBounds(91, 198, 150, 31);
		CTxt.setText(questions.get(index).getC());
		
		Text DTxt = new Text(questionComposite, SWT.BORDER | SWT.READ_ONLY);
		DTxt.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		DTxt.setBounds(322, 198, 150, 31);
		DTxt.setText(questions.get(index).getD());
		
		playerList = getScoreFromServer(client);
		printPlayerScore(playerList, table);
		
		barRunnable = new Runnable() {

			int i = 0;
			@Override
			public void run() {
				if (timeBar.isDisposed()) {
					return;
				}
				timeBar.setSelection(i++);
				if (i <= timeBar.getMaximum()) display.timerExec(50, this);
				else i = 0;
			}
			
		};
		
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					lblAnswer.setForeground(null);
					lblAnswer.setText("Answer: " + questions.get(index).getAnswer());
					display.timerExec(-1, barRunnable);

					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//				btnA.setSelection(false);
//				btnB.setSelection(false);
//				btnC.setSelection(false);
//				btnD.setSelection(false);
				
				index = index + 1;
				if(index < questions.size()) {
					text.setText(questions.get(index).getQuestion());
					ATxt.setText(questions.get(index).getA());
					BTxt.setText(questions.get(index).getB());
					CTxt.setText(questions.get(index).getC());
					DTxt.setText(questions.get(index).getD());
					lblAnswer.setText("");
					timeBar.setSelection(0);
					
					playerList = getScoreFromServer(client);
					printPlayerScore(playerList, table);
					
					lblUpdate.setText("Update #" + index);
					lblQuestion.setText("Question " + (index+1));
					startTime = System.currentTimeMillis();
					countdown(display, this, true);
					barRunnable = new Runnable() {

						int i = 0;
						@Override
						public void run() {
							if (timeBar.isDisposed()) {
								return;
							}
							timeBar.setSelection(i++);
							if (i <= timeBar.getMaximum()) display.timerExec(50, this);
							else i = 0;
							
						}
						
					};
					display.timerExec(50, barRunnable);
				} else {
					playerList = getScoreFromServer(client);
					printPlayerScore(playerList, table);
					
					answerComposite.dispose();
					shell.setSize(250, 463);
					btnExit.setEnabled(true);
					lblUpdate.setText("Result");
				}
			}
			
		};
		
		countdown(display, runnable, true);
		display.timerExec(50, barRunnable);
		
		Button btnA = new Button(questionComposite, SWT.RADIO);
		btnA.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
								
				Button source=  (Button) e.getSource();
				String ans = source.getText();
				
				updateAfterChooseAnswer(timeBar, barRunnable, client, table, display, runnable, ans, lblAnswer, lblAnswersTime, lblQuestion, btnA, text, ATxt, BTxt, CTxt, DTxt, lblUpdate, shell, btnExit, answerComposite);
				
			}
		});
		btnA.setBounds(24, 145, 36, 16);
		btnA.setText("A");
		
		Button btnB = new Button(questionComposite, SWT.RADIO);
		btnB.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
								
				Button source=  (Button) e.getSource();
				String ans = source.getText();

				updateAfterChooseAnswer(timeBar, barRunnable, client, table, display, runnable, ans, lblAnswer, lblAnswersTime, lblQuestion, btnB, text, ATxt, BTxt, CTxt, DTxt, lblUpdate, shell, btnExit, answerComposite);
			}
		});
		btnB.setText("B");
		btnB.setBounds(280, 145, 36, 16);
		
		Button btnC = new Button(questionComposite, SWT.RADIO);
		btnC.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
								
				Button source=  (Button) e.getSource();
				String ans = source.getText();

				updateAfterChooseAnswer(timeBar, barRunnable, client, table, display, runnable, ans, lblAnswer, lblAnswersTime, lblQuestion, btnC, text, ATxt, BTxt, CTxt, DTxt, lblUpdate, shell, btnExit, answerComposite);
			}
		});
		btnC.setText("C");
		btnC.setBounds(24, 202, 36, 16);
		
		Button btnD = new Button(questionComposite, SWT.RADIO);
		btnD.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		btnD.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
								
				Button source=  (Button) e.getSource();
				String ans = source.getText();
				
				updateAfterChooseAnswer(timeBar, barRunnable, client, table, display, runnable, ans, lblAnswer, lblAnswersTime, lblQuestion, btnD, text, ATxt, BTxt, CTxt, DTxt, lblUpdate, shell, btnExit, answerComposite);
			}
		});
		btnD.setText("D");
		btnD.setBounds(280, 202, 36, 16);

	}
	
	private void updateAfterChooseAnswer(ProgressBar timeBar, Runnable barRunnable, Client client, Table table, Display display, Runnable runnable, String ans, Label lblAnswer, Label lblAnswersTime, Label lblQuestion, Button btn, Text text, Text ATxt, Text BTxt, Text CTxt, Text DTxt, Label lblUpdate, Shell shell, Button btnExit, Composite answerComposite) {
		//Function to update window and data after choose answer
		
		countdown(display, runnable, false);
		
		display.timerExec(-1, barRunnable);
		
		//Update time to answer question
		long answerTime = System.currentTimeMillis() - startTime;
		lblAnswersTime.setText(answerTime + "ms");
		
		Color green = new Color(display, 0, 255, 0);
		Color red = new Color(display, 255, 0, 0);

		if(ans.compareTo(questions.get(index).getAnswer()) == 0) {
			lblAnswer.setForeground(green);
			lblAnswer.setText("Correct");
			score += 10;
			try {
				client.dos.writeUTF(client.updateScoreMsg(clientName, room, score));
				System.out.println(client.dis.readUTF());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			lblAnswer.setForeground(red);
			lblAnswer.setText("Not " + ans + ". Answer is " + questions.get(index).getAnswer());
		}
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		btn.setSelection(false);
		lblAnswer.setText("");
		lblAnswersTime.setText("");
		timeBar.setSelection(0);
		index = index + 1;
		if(index < questions.size()) {
			text.setText(questions.get(index).getQuestion());
			ATxt.setText(questions.get(index).getA());
			BTxt.setText(questions.get(index).getB());
			CTxt.setText(questions.get(index).getC());
			DTxt.setText(questions.get(index).getD());

			playerList = getScoreFromServer(client);
			printPlayerScore(playerList, table);
			
			lblUpdate.setText("Update #" + index);
			lblQuestion.setText("Question " + (index+1));
			startTime = System.currentTimeMillis();	
			countdown(display, runnable, true);
			barRunnable = new Runnable() {

				int i = 0;
				@Override
				public void run() {
					if (timeBar.isDisposed()) {
						return;
					}
					timeBar.setSelection(i++);
					if (i <= timeBar.getMaximum()) display.timerExec(50, this);
					else i = 0;
					
				}
				
			};
			display.timerExec(50, barRunnable);
		} else {
			playerList = getScoreFromServer(client);
			printPlayerScore(playerList, table);
			
			answerComposite.dispose();
			shell.setSize(250, 463);
			btnExit.setEnabled(true);
			lblUpdate.setText("Result");
		}
	}
	
	private void countdown(Display display, Runnable runnable, boolean choose) {
		if(choose == true) {
			//Start countdown
			//5s each time
			display.timerExec(20*1000, runnable);
			
			//System.out.println("Start countdown");
		} else {
			//Stop countdown
			display.timerExec(-1, runnable);
			//System.out.println("Stop countdown");
		}
	}
	
	private ArrayList<Player> getScoreFromServer(Client client) {
		String sRep = null;
		try {
			client.dos.writeUTF(client.getScore(room));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			sRep = client.dis.readUTF();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] parts = sRep.split("--");
		ArrayList<Player> playerList = new ArrayList<Player>();
		for (int i = 1; i < parts.length; i += 2) {
			Player p = new Player(parts[i], Integer.parseInt(parts[i+1]));
			playerList.add(p);
		}
		return playerList;
	}
	
	private void printPlayerScore(ArrayList<Player> pL, Table table) {
		//Clear old leaderboard data
		table.removeAll();;
		
		//Sort
		Collections.sort(pL, new Comparator<Player>() {
	        @Override
	        public int compare(Player p1, Player p2)
	        {
	            return  p1.getScore() - p2.getScore();
	        }
	    });
		
		//Get new leaderboard data
		for (int i = 0; i < pL.size(); i++) {
			System.out.println(pL.get(i).getPlayerName() + "------" + pL.get(i).getScore());
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, pL.get(i).getPlayerName());
			item.setText(1, pL.get(i).getScore() + "");
		}
	}
}