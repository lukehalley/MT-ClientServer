package lhalley;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
class BuildGUI extends JFrame {

	JTextArea serverStatus;
	JTextPane clientTitle;
	JLabel currentStudentNumberLabel;
	JTextArea currentStudentNumber;
	JLabel currentStudentNameLabel;
	JTextArea currentStudentName;

	public BuildGUI() {

		getContentPane().setLayout(null);
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		getContentPane().add(p);

		JTextArea serverStatusTemp = new JTextArea();
		serverStatusTemp.setBounds(10, 11, 370, 239);
		serverStatusTemp.setEditable(false);
		getContentPane().add(serverStatusTemp);

		JTextPane clientTitle = new JTextPane();
		clientTitle.setText("WIT Student Login");
		clientTitle.setEditable(false);
		clientTitle.setBounds(390, 11, 284, 20);
		getContentPane().add(clientTitle);

		JLabel currentStudentNumberLabel = new JLabel("Student Number");
		currentStudentNumberLabel.setBounds(390, 42, 284, 20);
		getContentPane().add(currentStudentNumberLabel);

		JTextPane currentStudentNumberTemp = new JTextPane();
		currentStudentNumberTemp.setEditable(false);
		currentStudentNumberTemp.setBounds(390, 73, 284, 20);
		getContentPane().add(currentStudentNumberTemp);

		JLabel currentStudentNameLabel = new JLabel("Student Name");
		currentStudentNameLabel.setBounds(390, 104, 284, 20);
		getContentPane().add(currentStudentNameLabel);

		JTextPane currentStudentNameTemp = new JTextPane();
		currentStudentNameTemp.setEditable(false);
		currentStudentNameTemp.setBounds(390, 135, 284, 20);
		getContentPane().add(currentStudentNameTemp);

		setTitle("Server");
		setBackground(Color.WHITE);
		setSize(700, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		getContentPane().repaint();

	}

	public JTextArea getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(JTextArea serverStat) {
		this.serverStatus = serverStat;
		getContentPane().add(serverStat);
		getContentPane().repaint();
	}

	public JTextPane getClientTitle() {
		return clientTitle;
	}

	public void setClientTitle(JTextPane clientTitle) {
		this.clientTitle = clientTitle;
		getContentPane().add(clientTitle);
		getContentPane().repaint();
	}

	public JLabel getCurrentStudentNumberLabel() {
		return currentStudentNumberLabel;
	}

	public void setCurrentStudentNumberLabel(JLabel currentStudentNumberLabel) {
		this.currentStudentNumberLabel = currentStudentNumberLabel;
		getContentPane().add(currentStudentNumberLabel);
		getContentPane().repaint();
	}

	public JTextArea getCurrentStudentNumber() {
		return currentStudentNumber;
	}

	public void setCurrentStudentNumber(JTextArea currentStudentNumber) {
		this.currentStudentNumber = currentStudentNumber;
		getContentPane().add(currentStudentNumber);
		getContentPane().repaint();
	}

	public JLabel getCurrentStudentNameLabel() {
		return currentStudentNameLabel;
	}

	public void setCurrentStudentNameLabel(JLabel currentStudentNameLabel) {
		this.currentStudentNameLabel = currentStudentNameLabel;
		getContentPane().add(currentStudentNameLabel);
		getContentPane().repaint();
	}

	public JTextArea getCurrentStudentName() {
		return currentStudentName;
	}

	public void setCurrentStudentName(JTextArea currentStudentName) {
		this.currentStudentName = currentStudentName;
		getContentPane().add(currentStudentName);
		getContentPane().repaint();
	}

}

//Server class 
public class Server {
	private static ServerSocket ss;

	public static void main(String[] args) throws IOException {
		ss = new ServerSocket(8000);

		// running infinite loop for getting
		// client request
		while (true) {
			
			Socket s = null;

			try {
				// socket object to receive incoming client requests
				s = ss.accept();

				System.out.println("A new client is connected : " + s);

				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				System.out.println("Assigning new thread for this client");

				// create a new thread object
				Thread t = new ClientHandler(s, dis, dos);

				// Invoking the start() method
				t.start();

			} catch (Exception e) {
				s.close();
				e.printStackTrace();
			}
		}
	}
}

// ClientHandler class 
class ClientHandler extends Thread {
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
	public final String userName = "root";
	public final String password = "";
	public final String serverName = "localhost";
	public final int portNumber = 3306;
	public final String dbName = "wit";
	public String studentTable = "students";

	// Constructor
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
		this.s = s;
		this.dis = dis;
		this.dos = dos;
	}
	
	// Function which is used to get a connection to the database
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		// Setting credentials
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);
		// Getting the connection using jdbc
		conn = DriverManager.getConnection("jdbc:mysql://" + this.serverName + ":" + this.portNumber + "/" + this.dbName
				+ "?verifyServerCertificate=false&useSSL=true", connectionProps);
		return conn;
	}
	
	@Override
	public void run() {
		

		
		BuildGUI panel = new BuildGUI();

		JTextArea serverStatus = new JTextArea();
		serverStatus.setBounds(10, 11, 370, 239);
		serverStatus.setEditable(false);
		panel.setServerStatus(serverStatus);

		JTextArea currentStudentNumber = new JTextArea();
		currentStudentNumber.setEditable(false);
		currentStudentNumber.setBounds(390, 73, 284, 20);
		panel.setCurrentStudentNumber(currentStudentNumber);

		JTextArea currentStudentName = new JTextArea();
		currentStudentName.setEditable(false);
		currentStudentName.setBounds(390, 135, 284, 20);
		panel.setCurrentStudentName(currentStudentName);
		
		try {
			Connection dbConnection = null;
			Statement statement = null;
			boolean loginStatus;
			// Get all student IDs
			String getAllStudentIDs = "SELECT * FROM " + studentTable;
			// Getting the connection
			dbConnection = getConnection();
			// Begin creation of the db statement before executing command
			statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery(getAllStudentIDs);
			ArrayList<String> ids = new ArrayList<String>();
			while (rs.next()) {
				ids.add(rs.getString(2));
			}
			
			while (true) {
				// Receive radius from the client
				int recievedStudentNum = dis.readInt();
				String sn = Integer.toString(recievedStudentNum);
				String getUserByStNum = "SELECT * FROM " + studentTable + " WHERE STUD_ID = " + sn;
				ResultSet r = statement.executeQuery(getUserByStNum);
				if (r.next() == false) {
					// Compute area
					JTextArea serverStat = panel.getServerStatus();
					JTextArea currStudName = panel.getCurrentStudentName();
					JTextArea currStudNum = panel.getCurrentStudentNumber();
					loginStatus = false;
					dos.writeBoolean(loginStatus);
					serverStat.append("Student does NOT exsist in database, login unsucessfull" + '\n');
					currStudName.setText("N/A");
					currStudNum.setText("N/A");
//					s.close();
//					dbConnection.close();
				} else {
					String customerSTUDID = r.getString("STUD_ID");
					String customerFNAME = r.getString("FNAME");
					String customerSNAME = r.getString("SNAME");
					String customerNAME = customerFNAME + " " + customerSNAME;
					// Compute area
					JTextArea serverStat = panel.getServerStatus();
					JTextArea currStudName = panel.getCurrentStudentName();
					JTextArea currStudNum = panel.getCurrentStudentNumber();
					serverStat.append("Student number received from client: " + recievedStudentNum + '\n');
					// Check log in
					if (ids.contains(sn)) {
						loginStatus = true;
						serverStat.append(
								"Student '" + customerNAME + "' is now logged in and connected to the Server " + '\n');
						currStudNum.setText(customerSTUDID);
						currStudName.setText(customerNAME);
						try {
							dos.writeBoolean(loginStatus);
							dos.writeUTF(customerNAME);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else {
						loginStatus = false;
						serverStat.append("Student does NOT exsist in database, login unsucessfull" + '\n');
						s.close();
						dbConnection.close();
					}
				}
			}
			
		} catch (SQLException e) {
			System.out.println("TESTER No data");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
