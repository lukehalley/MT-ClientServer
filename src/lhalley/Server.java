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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class Server extends JFrame {

	// DB configurations
	private final String userName = "root";
	private final String password = "";
	private final String serverName = "localhost";
	private final int portNumber = 3306;
	private final String dbName = "wit";
	private final String studentTable = "students";

	public static void main(String[] args) {
		new Server();
	}

	public Server() {

		getContentPane().setLayout(null);
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setBounds(0, 0, 484, 0);
		p.setLayout(new BorderLayout());
		getContentPane().add(p);
		
		JTextArea serverStatus = new JTextArea();
		serverStatus.setBounds(10, 11, 370, 239);
		serverStatus.setEditable(false);
		getContentPane().add(serverStatus);
		
		JTextPane clientTitle = new JTextPane();
		clientTitle.setText("WIT Student Login");
		clientTitle.setEditable(false);
		clientTitle.setBounds(390, 11, 284, 20);
		getContentPane().add(clientTitle);
		
		JLabel currentStudentNumberLabel = new JLabel("Student ID");
		currentStudentNumberLabel.setBounds(390, 42, 284, 20);
		getContentPane().add(currentStudentNumberLabel);
		
		JTextPane currentStudentNumber = new JTextPane();
		currentStudentNumber.setEditable(false);
		currentStudentNumber.setBounds(390, 73, 284, 20);
		getContentPane().add(currentStudentNumber);
		
		JLabel currentStudentNameLabel = new JLabel("Student Name");
		currentStudentNameLabel.setBounds(390, 104, 284, 20);
		getContentPane().add(currentStudentNameLabel);
		
		JTextPane currentStudentName = new JTextPane();
		currentStudentName.setEditable(false);
		currentStudentName.setBounds(390, 135, 284, 20);
		getContentPane().add(currentStudentName);
		
		setTitle("Server");
		setSize(700, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		// Loading all the student id's in
		try {
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
					serverStatus.append("Student number received from client: " + recievedStudentNum + '\n');
					
					if (ids.contains(sn)) {
						loginStatus = true;
						serverStatus.append("Student '" + customerNAME + "' is now logged in and connected to the Server " + '\n');
						currentStudentNumber.setText(customerSTUDID);
						currentStudentName.setText(customerNAME);
						try {
							outputToClient.writeBoolean(loginStatus);
							outputToClient.writeUTF(customerNAME);
						} catch (IOException e1) {
							e1.printStackTrace();
						} 
					} else {
						loginStatus = false;
						serverStatus.append("Student does NOT exsist in database, login unsucessfull" + '\n');
					}
				
				}
			} catch (IOException ex) {
				System.err.println(ex);
			}
			

		} catch (SQLException e) {
			// Catch any errors and display them in a pop on the users screen aswell as in
			// the console
			final JPanel panel = new JPanel();
			JOptionPane.showMessageDialog(panel, "Data could not be initialized! Error: " + e.getMessage(),"Initialization Error!", JOptionPane.ERROR_MESSAGE);
			System.err.println("Data could not be initialized! Error: " + e.getMessage());

		}
		
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
}