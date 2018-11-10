// Luke Halley
// 20071820
// luke123halley@gmail.com
// 
// Server part of the Multithreaded Client/Server Java application
// 
// The server only accepts requests from registered students. The client (code for this in the other class ClientA2.java) enters their StudentID and
// submits request to the Server. The server creates a new thread for the client and validates that the
// Student exists in a database table. Invalid logins will result in an appropriate message being sent to
// the client and the socket is closed.

// set lhalley package
package lhalley;

// importing relavent librarys for usage in this class
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
// Class which is used to build the Server's GUI 
class BuildGUI extends JFrame {

	// Initialising all the JSwing components
	JTextArea serverStatus;
	JTextPane serverTitle;
	JLabel currentStudentNumberLabel;
	JTextArea currentStudentNumber;
	JLabel currentStudentNameLabel;
	JTextArea currentStudentName;

	// Method which adds components to Server panel, sets their relavent paramaters etc.
	// using .setEditable(false); to stop user removing text from the gui
	public BuildGUI() {

		// setting the window
		getContentPane().setLayout(null);
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		getContentPane().add(p);

		// All the below components are for creating the temporary blank
		// Server window before its filled with data
		JTextArea serverStatusTemp = new JTextArea();
		serverStatusTemp.setBounds(10, 11, 370, 239);
		serverStatusTemp.setEditable(false);
		getContentPane().add(serverStatusTemp);

		JTextPane serverTitle = new JTextPane();
		serverTitle.setText("WIT Student Login");
		serverTitle.setEditable(false);
		serverTitle.setBounds(390, 11, 284, 20);
		getContentPane().add(serverTitle);

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

		// sets the various needed paramaters to display the widow in the correct way
		setTitle("Server");
		setBackground(Color.WHITE);
		setSize(700, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		getContentPane().repaint();

	}

	// get the server status JTextArea field
	public JTextArea getServerStatus() {
		return serverStatus;
	}

	// set the server status setServerStatus field
	public void setServerStatus(JTextArea serverStat) {
		this.serverStatus = serverStat;
		getContentPane().add(serverStat);
		getContentPane().repaint();
	}

	// get the client title JTextArea field
	public JTextPane getClientTitle() {
		return serverTitle;
	}

	// set the client title JTextArea field
	public void setClientTitle(JTextPane serverTitle) {
		this.serverTitle = serverTitle;
		getContentPane().add(serverTitle);
		getContentPane().repaint();
	}

	// get the current student label
	public JLabel getCurrentStudentNumberLabel() {
		return currentStudentNumberLabel;
	}

	// set the current student label
	public void setCurrentStudentNumberLabel(JLabel currentStudentNumberLabel) {
		this.currentStudentNumberLabel = currentStudentNumberLabel;
		getContentPane().add(currentStudentNumberLabel);
		getContentPane().repaint();
	}

	// get the getCurrentStudentNumber JTextArea
	public JTextArea getCurrentStudentNumber() {
		return currentStudentNumber;
	}

	// set the setCurrentStudentNumber JTextArea
	public void setCurrentStudentNumber(JTextArea currentStudentNumber) {
		this.currentStudentNumber = currentStudentNumber;
		getContentPane().add(currentStudentNumber);
		getContentPane().repaint();
	}

	// get the getCurrentStudentNameLabel JLabel
	public JLabel getCurrentStudentNameLabel() {
		return currentStudentNameLabel;
	}

	// set the getCurrentStudentNameLabel JLabel
	public void setCurrentStudentNameLabel(JLabel currentStudentNameLabel) {
		this.currentStudentNameLabel = currentStudentNameLabel;
		getContentPane().add(currentStudentNameLabel);
		getContentPane().repaint();
	}

	// get the getCurrentStudentName JTextArea
	public JTextArea getCurrentStudentName() {
		return currentStudentName;
	}

	// set the getCurrentStudentName JTextArea
	public void setCurrentStudentName(JTextArea currentStudentName) {
		this.currentStudentName = currentStudentName;
		getContentPane().add(currentStudentName);
		getContentPane().repaint();
	}

}

// Server main class which creates:
// a Socket,
// a DataInputStream on that socket
// and a DataOutputStream on that socket.
//  The above three are sent to the ClientHandler function in a new thread
public class MultiThreadedServerA2 {
	// Initialising server socket
	private static ServerSocket serverSocket;
	// Creating varible for port making it easy to change it and refactor the code
	// for other usage
	static int port = 8000;

	// Main method which runs when class is started
	public static void main(String[] args) throws IOException {
		// create a ServerSocket on the port defined in the 'port' variable
		serverSocket = new ServerSocket(port);

		// infinite loop to receive incoming client requests
		while (true) {
			// Initialising socket to use with client
			Socket socket = null;

			try {
				// socket object to receive incoming client requests
				socket = serverSocket.accept();

				// Creates pop to allow the user of the server window that a client has
				// connected
				final JPanel newClientPanel = new JPanel();
				JOptionPane.showMessageDialog(newClientPanel, "A new client is connected : " + socket,
						"New Client Connected!", JOptionPane.INFORMATION_MESSAGE);

				// obtaining input and out streams
				DataInputStream dataInputStr = new DataInputStream(socket.getInputStream());
				DataOutputStream dataOutputStr = new DataOutputStream(socket.getOutputStream());

				// create a new thread object and pass the Socket, DataInputStream and
				// DataOutputStream
				Thread clientThread = new ConnectClient(socket, dataInputStr, dataOutputStr);

				// Start the thread that was just created above
				clientThread.start();

			} catch (Exception e) {
				// Catch any errors and display them in a pop on the users screen aswell as in
				// the console
				final JPanel panel = new JPanel();
				JOptionPane.showMessageDialog(panel, "Could not create new Thread for client! Error: " + e.getMessage(),
						"Client Thread Error!", JOptionPane.ERROR_MESSAGE);
				System.err.println("Data could not be initialized! Error: " + e.getMessage());
				// Close the socket we created due to error
				socket.close();
			}
		}
	}
}

// ConnectClient class which gets the Student number that the client sends over and queries it against
// the MySQL database. Based on the results two things can happen:
// 1) Exsisting Student Number: The user is allowed to connect to the Server and is seen as logged in by themselves and the Server.
// 2) Non-Exsisting Student Number: The user is NOT allowed to connect to the Server and is seen as logged out by themselves and the Server.
// The server will never recieve an invalid Student number as I check it is valid on the client side (> 8 characters, only an int and above or equal to 00000001 - the first student number of WIT)
// I use the number 00000000 to use as a LOGOUT request which is used by the user once they are logged in - this number is sent over and handled wiping the fields and setting the user as logged out
// allowing the user to log in a s a new student in the same client.
class ConnectClient extends Thread {
	// Creating the socket varables
	final DataInputStream dataInputStr;
	final DataOutputStream dataOutputStr;
	final Socket socket;
	// Creating and assigning values to the varibles which will be used with the
	// MySQL database to log in and query the data.
	// The names of the variables explain their purpose.
	public final String userName = "root";
	public final String password = "";
	public final String serverName = "localhost";
	public final int portNumber = 3306;
	public final String dbName = "wit";
	public String studentTable = "students";

	// Constructor for the ConnectClient class which takes in the three values needed
	// to pass to the thread function above.
	public ConnectClient(Socket socket, DataInputStream dataInputStr, DataOutputStream dataOutputStr) {
		this.socket = socket;
		this.dataInputStr = dataInputStr;
		this.dataOutputStr = dataOutputStr;
	}

	// Function which is used to get a connection to the database
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		// Setting credentials
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);
		// Getting the connection using jdbc using out varibles we created above
		conn = DriverManager.getConnection("jdbc:mysql://" + this.serverName + ":" + this.portNumber + "/" + this.dbName
				+ "?verifyServerCertificate=false&useSSL=true", connectionProps);
		// return the connection object
		return conn;
	}

	// This function handles all the fucntions within the Server window
	@Override
	public void run() {
		// call our BuildGUI fucntion to build our blank window before its filled with
		// data
		BuildGUI panel = new BuildGUI();
		// set the LOGOUT_CODE which is used to handle a logout request
		int LOGOUT_CODE = 00000000;

		// creates the serverStatus JTextArea which shows the user of the Server who is
		// logged in or if nobody is
		JTextArea serverStatus = new JTextArea();
		serverStatus.setBounds(10, 11, 370, 239);
		serverStatus.setEditable(false);
		serverStatus.setEditable(false);
		panel.setServerStatus(serverStatus);

		// creates the currentStudentNumber JTextArea which shows the users student
		// number if they are logged in (set to the relavent number or N/A if they are
		// not later on in the code below)
		JTextArea currentStudentNumber = new JTextArea();
		currentStudentNumber.setEditable(false);
		currentStudentNumber.setBounds(390, 73, 284, 20);
		panel.setCurrentStudentNumber(currentStudentNumber);

		// creates the currentStudentName JTextArea which shows the users student name
		// if they are logged in (set to the relavent name or N/A if they are not later
		// on in the code below)
		JTextArea currentStudentName = new JTextArea();
		currentStudentName.setEditable(false);
		currentStudentName.setBounds(390, 135, 284, 20);
		panel.setCurrentStudentName(currentStudentName);

		try {
			// Initialising the two objects needed for the database connection
			Connection dbConnection = null;
			Statement statement = null;

			// creating the loginStatus which will be set based on the login status of the
			// user
			boolean loginStatus;
			// Get all student IDs
			String getAllStudentIDs = "SELECT * FROM " + studentTable;
			// Getting the connection
			dbConnection = getConnection();
			// Begin creation of the db statement before executing command
			statement = dbConnection.createStatement();

			// get a list of all the ids in the db
			ResultSet rs = statement.executeQuery(getAllStudentIDs);
			// creat an array for the ids
			ArrayList<String> ids = new ArrayList<String>();
			// add the ids from the result set to the array we just created
			while (rs.next()) {
				ids.add(rs.getString(2));
			}
			while (true) {
				// Receive radius from the client
				int recievedStudentNum = dataInputStr.readInt();
				// check if the student was the LOGOUT_CODE, if not its a user logging in
				if (recievedStudentNum != LOGOUT_CODE) {
					// convert the recieved student number into an int
					String sn = Integer.toString(recievedStudentNum);
					// create the query string for selecting a single user by their id
					String getUserByStNum = "SELECT * FROM " + studentTable + " WHERE STUD_ID = " + sn;
					// execute the above query
					ResultSet r = statement.executeQuery(getUserByStNum);

					// if the result set comes back empty we can assume the student id doesnt not
					// exsist in the database
					// in whcih case we return the message that the log in has been unsucessfull
					if (r.next() == false) {
						// get the serverStat, currStudName and currStudNum fields form the panel to set
						// them
						JTextArea serverStat = panel.getServerStatus();
						JTextArea currStudName = panel.getCurrentStudentName();
						JTextArea currStudNum = panel.getCurrentStudentNumber();
						// set the loginStatus boolean to false as log in has been unsucessfull
						loginStatus = false;
						// send the loginStatus boolean back to client which logic will translate to a
						// login which has been unsucessfull
						dataOutputStr.writeBoolean(loginStatus);
						// send a message over to the client to let them know that the Student ID does
						// NOT exsist in database, login unsucessfull
						serverStat.append("Student ID does NOT exsist in database, login unsucessfull" + '\n');
						// set both the currStudName and currStudNum to N/A as the login was
						// unsucessfull
						currStudName.setText("N/A");
						currStudNum.setText("N/A");
					} else {
						// in this case the result set contained SOMETHING - we dont know for SURE if
						// this is a user but it more than likely is
						// We then extract the data we need to display in the Server window from the
						// result set
						String customerSTUDID = r.getString("STUD_ID");
						String customerFNAME = r.getString("FNAME");
						String customerSNAME = r.getString("SNAME");
						String customerNAME = customerFNAME + " " + customerSNAME;
						// get the serverStat, currStudName and currStudNum fields form the panel to set
						// them
						JTextArea serverStat = panel.getServerStatus();
						JTextArea currStudName = panel.getCurrentStudentName();
						JTextArea currStudNum = panel.getCurrentStudentNumber();
						// clear the serverStat field
						serverStat.setText("");
						// let the Server user what number was sent over
						serverStat.append("Student number received from client: " + recievedStudentNum + '\n');
						// Check if the recieved student number is in the array which contains all the
						// ids in the database.
						if (ids.contains(sn)) {
							// Login has been sucessfull a the recieved student number matched with a id in
							// the ids array
							// set the login status to the true
							loginStatus = true;
							// let the Server user know who is logged in student id
							serverStat.append("Student '" + customerNAME
									+ "' is now logged in and connected to the Server " + '\n');
							// set the customerSTUDID and customerNAME to the id and name of the stduent in
							// the database
							currStudNum.setText(customerSTUDID);
							currStudName.setText(customerNAME);
							try {
								// send the loginStatus boolean back to client which logic will translate to a
								// login which has been sucessfull
								dataOutputStr.writeBoolean(loginStatus);
								// send a message over to the client so we can display the clients name in their
								// panel
								dataOutputStr.writeUTF(customerNAME);
							} catch (IOException e1) {
								// Catch any errors and display them in a pop on the users screen aswell as in
								// the console
								final JPanel loginSendPan = new JPanel();
								JOptionPane.showMessageDialog(loginSendPan,
										"Could not send login status and student name to client! Error: "
												+ e1.getMessage(),
												"Login Sucessfull Send Error!", JOptionPane.ERROR_MESSAGE);
								System.err.println("Could not send login status and student name to client! Error "
										+ e1.getMessage());
							}
						} else {
							// set the loginStatus boolean to false as log in has been unsucessfull
							loginStatus = false;
							// send a message over to the client to let them know that the Student ID does
							// NOT exsist in database, login unsucessfull
							serverStat.append("Student does NOT exsist in database, login unsucessfull" + '\n');
							// close the socket connection - I have commented this out as I was unable to
							// find a way to restart the socket once I stopped it or start a new one when
							// needed
							// s.close();
							// close the connection to the SQL database
							dbConnection.close();
						}
					}
					// Handles a logout request, if the student number sent over is equal to the
					// LOGOUT_CODE (highly reccomend keeping it at 00000000 as my code in the client
					// class assumes this by default)
				} else if (recievedStudentNum == LOGOUT_CODE) {
					// get the serverStat, currStudName and currStudNum fields form the panel to set
					// them
					JTextArea serverStat = panel.getServerStatus();
					JTextArea currStudName = panel.getCurrentStudentName();
					JTextArea currStudNum = panel.getCurrentStudentNumber();
					// set the loginStatus boolean to false as the client has logged out
					loginStatus = false;
					// setting the serverStat field to blank
					serverStat.setText("");
					// letting the server user the current client has logged out
					serverStat.append("Student has logged out!" + '\n');
					// set both the currStudName and currStudNum to N/A as the client has logged out
					currStudName.setText("N/A");
					currStudNum.setText("N/A");
				} else {
					// Handle any number which does not match the above logic - this should never be
					// entered but
					// placing it here to cover all areas
					final JPanel loginSendPan = new JPanel();
					JOptionPane.showMessageDialog(loginSendPan, "Invalid Student Number Sent By Client!",
							"Invalid Student Number Sent!", JOptionPane.ERROR_MESSAGE);
					System.err.println("Invalid Student Number Sent!");
				}
			}

		} catch (SQLException e) {
			// Handle SQL errors and display them in a pop on the users screen aswell as in
			// the console
			final JPanel sqlErrorPan = new JPanel();
			JOptionPane.showMessageDialog(sqlErrorPan,
					"There was a problem with the database! Error: " + e.getMessage(), "Database Error!",
					JOptionPane.ERROR_MESSAGE);
			System.err.println("There was a problem with the database! Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// Handle if a user exits without logging out or just shuts down their window
			final JPanel ioErrorPan = new JPanel();
			JOptionPane.showMessageDialog(ioErrorPan, "Client Has Exited Without Logging Out",
					"Client Exit!", JOptionPane.INFORMATION_MESSAGE);
			// get the serverStat, currStudName and currStudNum fields form the panel to set
			// them
			JTextArea serverStat = panel.getServerStatus();
			JTextArea currStudName = panel.getCurrentStudentName();
			JTextArea currStudNum = panel.getCurrentStudentNumber();
			// setting the serverStat field to blank
			serverStat.setText("");
			// letting the server user the current client has logged out
			serverStat.append("Student has logged out!" + '\n');
			// set both the currStudName and currStudNum to N/A as the client has logged out
			currStudName.setText("N/A");
			currStudNum.setText("N/A");
		}
	}
}
