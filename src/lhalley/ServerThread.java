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

public class ServerThread extends Thread {
	
	public static void main(String[] args) {
		Connect c = new Connect();
		c.run();
	}

}

@SuppressWarnings("serial")
class BuildGUI extends JFrame {
	
	JTextArea serverStatus;
	JTextPane clientTitle;
	JLabel currentStudentNumberLabel;
	JTextPane currentStudentNumber;
	JLabel currentStudentNameLabel;
	JTextPane currentStudentName;
	
	public BuildGUI() {

		getContentPane().setLayout(null);
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setBounds(0, 0, 484, 0);
		p.setLayout(new BorderLayout());
		getContentPane().add(p);

//		JTextArea serverStatus = new JTextArea();
//		serverStatus.setBounds(10, 11, 370, 239);
//		serverStatus.setEditable(false);
//		getContentPane().add(serverStatus);

		JTextPane clientTitle = new JTextPane();
		clientTitle.setText("WIT Student Login");
		clientTitle.setEditable(false);
		clientTitle.setBounds(390, 11, 284, 20);
		getContentPane().add(clientTitle);

		JLabel currentStudentNumberLabel = new JLabel("Student Number");
		currentStudentNumberLabel.setBounds(390, 42, 284, 20);
		getContentPane().add(currentStudentNumberLabel);

		JLabel currentStudentNameLabel = new JLabel("Student Name");
		currentStudentNameLabel.setBounds(390, 104, 284, 20);
		getContentPane().add(currentStudentNameLabel);

//		JTextPane currentStudentName = new JTextPane();
//		currentStudentName.setEditable(false);
//		currentStudentName.setBounds(390, 135, 284, 20);
//		getContentPane().add(currentStudentName);

		setTitle("Server");
		setSize(700, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	public JTextArea getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(JTextArea serverStatus) {
		this.serverStatus = serverStatus;
		getContentPane().add(serverStatus);
	}

	public JTextPane getClientTitle() {
		return clientTitle;
	}

	public void setClientTitle(JTextPane clientTitle) {
		this.clientTitle = clientTitle;
		getContentPane().add(clientTitle);
	}

	public JLabel getCurrentStudentNumberLabel() {
		return currentStudentNumberLabel;
	}

	public void setCurrentStudentNumberLabel(JLabel currentStudentNumberLabel) {
		this.currentStudentNumberLabel = currentStudentNumberLabel;
		getContentPane().add(currentStudentNumberLabel);
	}

	public JTextPane getCurrentStudentNumber() {
		return currentStudentNumber;
	}

	public void setCurrentStudentNumber(JTextPane currentStudentNumber) {
		this.currentStudentNumber = currentStudentNumber;
		getContentPane().add(currentStudentNumber);
	}

	public JLabel getCurrentStudentNameLabel() {
		return currentStudentNameLabel;
	}

	public void setCurrentStudentNameLabel(JLabel currentStudentNameLabel) {
		this.currentStudentNameLabel = currentStudentNameLabel;
		getContentPane().add(currentStudentNameLabel);
	}

	public JTextPane getCurrentStudentName() {
		return currentStudentName;
	}

	public void setCurrentStudentName(JTextPane currentStudentName) {
		this.currentStudentName = currentStudentName;
		getContentPane().add(currentStudentName);
	}
	
}

@SuppressWarnings("serial")
class Connect extends JPanel {

	public final String userName = "root";
	public final String password = "";
	public final String serverName = "localhost";
	public final int portNumber = 3306;
	public final String dbName = "wit";
	public String studentTable = "students";

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

	@SuppressWarnings("unused")
	public void run() {
		// Loading all the student id's in

		try {
			BuildGUI panel = new BuildGUI();
			System.out.println(panel);
			// Init of connection object
			Connection dbConnection = null;
			// Init of statement object
			Statement statement = null;

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

			try {
				// Create a server socket
				ServerSocket serverSocket = new ServerSocket(8000);

				// Listen for a connection request
				Socket socket = serverSocket.accept();

				// Create data input and output streams
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
				
				JTextArea serverStatus = new JTextArea();
				serverStatus.setBounds(10, 11, 370, 239);
				serverStatus.setEditable(false);
				panel.setServerStatus(serverStatus);
				
				JTextPane currentStudentNumber = new JTextPane();
				currentStudentNumber.setEditable(false);
				currentStudentNumber.setBounds(390, 73, 284, 20);
				panel.setCurrentStudentNumber(currentStudentNumber);
				
				JTextPane currentStudentName = new JTextPane();
				currentStudentName.setEditable(false);
				currentStudentName.setBounds(390, 135, 284, 20);
				panel.setCurrentStudentName(currentStudentName);
				
				System.out.println(serverStatus);
				System.out.println(currentStudentNumber);
				System.out.println(currentStudentName);
				
				while (true) {

					// Receive radius from the client
					int recievedStudentNum = inputFromClient.readInt();

					String sn = Integer.toString(recievedStudentNum);

					String getUserByStNum = "SELECT * FROM " + studentTable + " WHERE STUD_ID = " + sn;

					System.out.println(getUserByStNum);

					ResultSet r = statement.executeQuery(getUserByStNum);
					r.next();
					String customerSID = r.getString("SID");
					String customerSTUDID = r.getString("STUD_ID");
					String customerFNAME = r.getString("FNAME");
					String customerSNAME = r.getString("SNAME");
					String customerNAME = customerFNAME + " " + customerSNAME;

					// Compute area
					boolean loginStatus;
					
					JTextArea serverStat = panel.getServerStatus();
					JTextPane currStudName = panel.getCurrentStudentName();
					JTextPane currStudNum = panel.getCurrentStudentNumber();
					
					serverStat.append("Student number received from client: " + recievedStudentNum + '\n');

					if (ids.contains(sn)) {
						loginStatus = true;
						serverStat.append(
								"Student '" + customerNAME + "' is now logged in and connected to the Server " + '\n');

						currStudNum.setText(customerSTUDID);
						currStudName.setText(customerNAME);
						try {
							outputToClient.writeBoolean(loginStatus);
							outputToClient.writeUTF(customerNAME);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else {
						loginStatus = false;
						serverStat.append("Student does NOT exsist in database, login unsucessfull" + '\n');
						serverSocket.close();
					}

				}
				
				
				
			} catch (IOException ex) {
				System.err.println(ex);
			}

		} catch (SQLException e) {
			// Catch any errors and display them in a pop on the users screen aswell as in
			// the console
			final JPanel panel = new JPanel();
			JOptionPane.showMessageDialog(panel, "Data could not be initialized! Error: " + e.getMessage(),
					"Initialization Error!", JOptionPane.ERROR_MESSAGE);
			System.err.println("Data could not be initialized! Error: " + e.getMessage());

		}

	}

}