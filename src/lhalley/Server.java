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
	// Text area for displaying contents
	private JTextArea jta = new JTextArea();

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

	@SuppressWarnings("resource")
	public Server() {
		// Place text area on the frame
		getContentPane().setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(jta);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JButton btnExit = new JButton("Exit");
		scrollPane.setRowHeaderView(btnExit);

		JButton btnSend = new JButton("Send Message Back To Client");
		scrollPane.setColumnHeaderView(btnSend);

		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
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

			System.out.println("Connected!");
			
			ArrayList<String> ids = new ArrayList<String>();
			
			while (rs.next()) { 
				ids.add(rs.getString(2));
			}
			
			System.out.println(ids);
			

		} catch (SQLException e) {
			// Catch any errors and display them in a pop on the users screen aswell as in
			// the console
			final JPanel panel = new JPanel();
			JOptionPane.showMessageDialog(panel, "Data could not be initialized! Error: " + e.getMessage(),"Initialization Error!", JOptionPane.ERROR_MESSAGE);
			System.err.println("Data could not be initialized! Error: " + e.getMessage());

		}

		try {
			// Create a server socket
			ServerSocket serverSocket = new ServerSocket(8000);
			jta.append("Server started at " + new Date() + '\n');

			// Listen for a connection request
			Socket socket = serverSocket.accept();

			// Create data input and output streams
			DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
			DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

			while (true) {
				// Receive radius from the client
				int recievedStudentNum = inputFromClient.readInt();

				// Compute area
				boolean loginStatus = true;

				try {
					outputToClient.writeBoolean(loginStatus);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				jta.append("Student number received from client: " + recievedStudentNum + '\n');
				jta.append("The user is now logged in: " + loginStatus + '\n');

			}
		} catch (IOException ex) {
			System.err.println(ex);
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