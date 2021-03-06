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
	private Runnable runnable;
	private boolean isDisposed = false;


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
		shell.setSize(1350, 700);
		shell.setText("Playing Kahoot");
		shell.setLayout(new RowLayout(SWT.HORIZONTAL));

		index = 0;
		startTime = System.currentTimeMillis();
		

		Color green = new Color(display, 0, 255, 0);

		Composite lbComposite = new Composite(shell, SWT.BORDER);
		lbComposite.setLayoutData(new RowData(320, 648));

		Label lblLeaderboard = new Label(lbComposite, SWT.NONE);
		lblLeaderboard.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblLeaderboard.setAlignment(SWT.CENTER);
		lblLeaderboard.setBounds(86, 10, 127, 27);
		lblLeaderboard.setText("Leaderboard");

		Composite tableComposite = new Composite(lbComposite, SWT.NONE);
		tableComposite.setBounds(21, 59, 289, 464);

		Table table = new Table(tableComposite, SWT.BORDER | SWT.HIDE_SELECTION | SWT.V_SCROLL);
		table.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		table.setBounds(31, 28, 230, 395);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnPlayer = new TableColumn(table, SWT.CENTER);
		tblclmnPlayer.setWidth(112);
		tblclmnPlayer.setText("Player");

		TableColumn tblclmnScore = new TableColumn(table, SWT.CENTER);
		tblclmnScore.setWidth(110);
		tblclmnScore.setText("Score");

		Composite exitComposite = new Composite(lbComposite, SWT.NONE);
		exitComposite.setBounds(86, 569, 127, 69);

		Button btnExit = new Button(exitComposite, SWT.NONE);
		btnExit.setBounds(10, 10, 107, 49);
		btnExit.setFont(SWTResourceManager.getFont("Times New Roman", 15, SWT.NORMAL));
		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Click exit to back to Client window
				display.timerExec(-1, runnable);
				try {
					isDisposed = true;
					display.timerExec(-1, runnable);
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
		btnExit.setText("Exit");

		Composite answerComposite = new Composite(shell, SWT.NONE);
		answerComposite.setLayoutData(new RowData(991, 653));

		Composite topComposite = new Composite(answerComposite, SWT.NONE);
		topComposite.setBounds(10, 10, 957, 186);

		Label lblPlayer = new Label(topComposite, SWT.NONE);
		lblPlayer.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblPlayer.setAlignment(SWT.CENTER);
		lblPlayer.setBounds(10, 20, 157, 29);
		lblPlayer.setText("Player: " + clientName);

		Label lblRoom = new Label(topComposite, SWT.NONE);
		lblRoom.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblRoom.setAlignment(SWT.CENTER);
		lblRoom.setBounds(682, 20, 136, 29);
		lblRoom.setText("Room: " + room);

		Label lblQuestion = new Label(topComposite, SWT.NONE);
		lblQuestion.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblQuestion.setAlignment(SWT.CENTER);
		lblQuestion.setBounds(424, 53, 107, 29);
		lblQuestion.setText("Question " + (index+1));

		Label lblAnswer = new Label(topComposite, SWT.NONE);
		lblAnswer.setAlignment(SWT.CENTER);
		lblAnswer.setFont(SWTResourceManager.getFont("Times New Roman", 20, SWT.NORMAL));
		lblAnswer.setBounds(309, 121, 305, 42);
		lblAnswer.setText("");

//		Label lblAnswersTime = new Label(topComposite, SWT.NONE);
//		lblAnswersTime.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
//		lblAnswersTime.setBounds(66, 118, 136, 27);
//		lblAnswersTime.setText("");

		ProgressBar timeBar = new ProgressBar(topComposite, SWT.SMOOTH);
		timeBar.setBounds(309, 87, 305, 17);
		timeBar.setForeground(green);
		timeBar.setMaximum(140);  //Set maximum of time value is 10s

		Composite questionComposite = new Composite(answerComposite, SWT.NONE);
		questionComposite.setBounds(10, 215, 957, 428);

		Label lblQuestion_1 = new Label(questionComposite, SWT.NONE);
		lblQuestion_1.setFont(SWTResourceManager.getFont("Times New Roman", 12, SWT.NORMAL));
		lblQuestion_1.setBounds(11, 57, 64, 31);
		lblQuestion_1.setText("Question: ");

		Text text = new Text(questionComposite, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		text.setFont(SWTResourceManager.getFont("Times New Roman", 15, SWT.NORMAL));
		text.setBounds(133, 25, 797, 88);
		text.setText(questions.get(index).getQuestion());

		Button btnA = new Button(questionComposite, SWT.NONE | SWT.WRAP | SWT.LEFT);
		btnA.setFont(SWTResourceManager.getFont("Times New Roman", 15, SWT.NORMAL));
		btnA.setBounds(11, 156, 397, 102);
		btnA.setText("  A. " + questions.get(index).getA());

		Button btnB = new Button(questionComposite, SWT.NONE | SWT.WRAP | SWT.LEFT);
		btnB.setFont(SWTResourceManager.getFont("Times New Roman", 15, SWT.NORMAL));
		btnB.setBounds(550, 156, 397, 102);
		btnB.setText("  B. " + questions.get(index).getB());

		Button btnC = new Button(questionComposite, SWT.NONE | SWT.WRAP | SWT.LEFT);
		btnC.setFont(SWTResourceManager.getFont("Times New Roman", 15, SWT.NORMAL));
		btnC.setBounds(11, 304, 397, 102);
		btnC.setText("  C. " + questions.get(index).getC());

		Button btnD = new Button(questionComposite, SWT.NONE | SWT.WRAP | SWT.LEFT);
		btnD.setFont(SWTResourceManager.getFont("Times New Roman", 15, SWT.NORMAL));
		btnD.setBounds(550, 304, 397, 102);
		btnD.setText("  D. " + questions.get(index).getD());

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


		runnable = new Runnable() {

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
					btnA.setText("A. " + questions.get(index).getA());
					btnB.setText("B. " + questions.get(index).getB());
					btnC.setText("C. " + questions.get(index).getC());
					btnD.setText("D. " + questions.get(index).getD());
					lblAnswer.setText("");
					timeBar.setSelection(0);

					playerList = getScoreFromServer(client);
					printPlayerScore(playerList, table);

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
					//					playerList = getScoreFromServer(client);
					//					printPlayerScore(playerList, table);

					playerList = getScoreFromServer(client);
					printPlayerScore(playerList, table);
					runnable = new Runnable() {

						@Override
						public void run() {
							if (!isDisposed) {
								playerList = getScoreFromServer(client);
								printPlayerScore(playerList, table);
							}
							display.timerExec(2*1000, this);
						}
					};

					display.timerExec(2*1000, runnable);


					answerComposite.dispose();
					shell.setSize(320, 700);
					btnExit.setEnabled(true);
				}
			}

		};

		btnA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateAfterChooseAnswer(timeBar, barRunnable, client, table, display, runnable, "A", lblAnswer, lblQuestion, btnA, text, btnA, btnB, btnC, btnD, shell, btnExit, answerComposite);
			}
		});

		btnB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateAfterChooseAnswer(timeBar, barRunnable, client, table, display, runnable, "B", lblAnswer, lblQuestion, btnA, text, btnA, btnB, btnC, btnD, shell, btnExit, answerComposite);
			}
		});

		btnC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateAfterChooseAnswer(timeBar, barRunnable, client, table, display, runnable, "C", lblAnswer, lblQuestion, btnA, text, btnA, btnB, btnC, btnD, shell, btnExit, answerComposite);
			}
		});

		btnD.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateAfterChooseAnswer(timeBar, barRunnable, client, table, display, runnable, "D", lblAnswer, lblQuestion, btnA, text, btnA, btnB, btnC, btnD, shell, btnExit, answerComposite);
			}
		});

		countdown(display, runnable, true);
		display.timerExec(50, barRunnable);


	}
	private void changeColor (Button bA, Button bB, Button bC, Button bD) {
		
	}

	private void updateAfterChooseAnswer(ProgressBar timeBar, Runnable barRunnable, Client client, Table table, Display display, Runnable runnable, String ans, Label lblAnswer, Label lblQuestion, Button btn, Text text, Button btnA, Button btnB, Button btnC, Button btnD, Shell shell, Button btnExit, Composite answerComposite) {
		//Function to update window and data after choose answer

		countdown(display, runnable, false);

		display.timerExec(-1, barRunnable);

		//Update time to answer question
		long answerTime = System.currentTimeMillis() - startTime;
		

		Color green = new Color(display, 0, 255, 0);
		Color red = new Color(display, 255, 0, 0);

		if(ans.compareTo(questions.get(index).getAnswer()) == 0) {
			lblAnswer.setForeground(green);
			lblAnswer.setText("Correct");
			score += 10 + (10000 - (int) answerTime) / 1000 ;
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
		timeBar.setSelection(0);
		index = index + 1;
		if(index < questions.size()) {
			text.setText(questions.get(index).getQuestion());
			btnA.setText("A. " + questions.get(index).getA());
			btnB.setText("B. " + questions.get(index).getB());
			btnC.setText("C. " + questions.get(index).getC());
			btnD.setText("D. " + questions.get(index).getD());

			playerList = getScoreFromServer(client);
			printPlayerScore(playerList, table);

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
			runnable = new Runnable() {

				@Override
				public void run() {
					if (!isDisposed) {
						playerList = getScoreFromServer(client);

						printPlayerScore(playerList, table);
					}
					display.timerExec(2*1000, this);
				}
			};

			display.timerExec(2*1000, runnable);

			answerComposite.dispose();
			shell.setSize(320, 700);
			btnExit.setEnabled(true);
		}
	}

	private void countdown(Display display, Runnable runnable, boolean choose) {
		if(choose == true) {
			//Start countdown
			//10s each time
			display.timerExec(10*1000, runnable);

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
		table.removeAll();

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
			
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, pL.get(i).getPlayerName());
			item.setText(1, pL.get(i).getScore() + "");
		}
	}
}