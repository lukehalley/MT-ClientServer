// Luke Halley
// 20071820
// luke123halley@gmail.com
//
// TO RUN APPLICATION:
// Run MultiThreadedServerA2.java
// then run ClientA2.java (this file)
//
// Client part of the Multithreaded Client/Server Java application
// 
// The client connects to the Server on startup which is placed in a new thread. The use can then enter a 8 digit student number
// which is sent over to Server where it validates that the Student number sent exists in a database table.
// The user is sent messages based on their login status and can also logout and sign in again as a different user or the same again.

// set lhalley package
package lhalley;

//importing relavent librarys for usage in this class
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ClientA2 extends JFrame {

	// Initialising the DataOutputStream abd DataInputStream to be used with the
	// connection to the server
	private DataOutputStream toServer;
	private DataInputStream fromServer;

	// Initialising the student number input field
	private JTextField enteredStudentNumber;

	// Creating and assigning values to the varibles which will be used with the
	// socket connection to log in and connect to the Server.
	// The names of the variables explain their purpose.
	String add = "localhost";
	int port = 8000;
	int LOGOUT_CODE = 00000000;
	String LOGOUT_CODE_STR = "00000000";

	// Main method to create a new instance of the ClientA2 class
	public static void main(String[] args) {
		new ClientA2();
	}

	// Method which adds components to Client panel, sets their relavent paramaters
	// etc.
	// using .setEditable(false); to stop user removing text from the gui
	public ClientA2() {

		// setting the window
		getContentPane().setLayout(null);
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setBounds(0, 0, 484, 0);
		p.setLayout(new BorderLayout());
		getContentPane().add(p);

		// All the below components are for creating the temporary blank
		// Server window before its filled with data
		enteredStudentNumber = new JTextField();
		enteredStudentNumber.setBounds(118, 47, 264, 20);
		getContentPane().add(enteredStudentNumber);
		enteredStudentNumber.setColumns(10);

		JLabel lblStudentId = new JLabel("Student Number: ");
		lblStudentId.setBounds(10, 50, 98, 14);
		getContentPane().add(lblStudentId);

		JTextArea lblStudentGuide = new JTextArea(
				"Enter Only Numbers, Must Be 8 Characters, Greater or Equal To 00000001 - Example: 12345678");
		lblStudentGuide.setEditable(false);
		lblStudentGuide.setBounds(10, 11, 684, 20);
		getContentPane().add(lblStudentGuide);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 80, 754, 14);
		getContentPane().add(separator);

		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(494, 46, 95, 23);
		getContentPane().add(btnLogin);

		JTextArea statusView = new JTextArea();
		statusView.setBounds(10, 115, 684, 186);
		statusView.setEditable(false);
		statusView.setLineWrap(true);
		getContentPane().add(statusView);

		JLabel lblStatus = new JLabel("Status: Logged Out");
		lblStatus.setBounds(10, 92, 464, 14);
		getContentPane().add(lblStatus);

		JButton btnExit = new JButton("Exit");
		btnExit.setBounds(599, 312, 95, 23);
		getContentPane().add(btnExit);

		JButton btnLogout = new JButton("Logout");
		btnLogout.setBounds(599, 46, 95, 23);
		btnLogout.setEnabled(false);
		getContentPane().add(btnLogout);

		// sets the various needed paramaters to display the widow in the correct way
		setTitle("Client");
		setSize(710, 375);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);

		try {
			// Create a socket to connect to the server
			Socket socket = new Socket(add, port);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());

			// Set the action listener for the Login button
			btnLogin.addActionListener(new ActionListener() {
				@Override
				// Define what happens when the login button is pressed
				public void actionPerformed(ActionEvent e) {
					try {
						// set the statusView to blank
						statusView.setText("");

						// Check student number entered is not a string
						// ".*\\d+.*" = check its a number - 1234
						if (enteredStudentNumber.getText().matches(".*\\d+.*")
								&& enteredStudentNumber.getText().length() == 8
								&& !enteredStudentNumber.getText().matches(LOGOUT_CODE_STR)) {
							// Get the stduent number from the text field and parse it to an Integer
							int sentStudentNumber = Integer.parseInt(enteredStudentNumber.getText().trim());
							// let the user know that the client going to send over the number he/she has
							// entered
							statusView.append("Checking if: " + sentStudentNumber
									+ " is a valid and exsisting student number... \n");

							// Actually send over the student number to the server
							toServer.writeInt(sentStudentNumber);

							// Wipe the previouslly sent value so we send and recieve more values
							toServer.flush();

							// Get the log in status from the server
							Boolean loginStatus = fromServer.readBoolean();

							// Tell the user if they are logged in or not
							if (loginStatus == true) {
								// Log in has been sucessfully
								// Get the clients name so we can display it in the panel and use it below
								String userName = fromServer.readUTF();
								// Display a pop to let the user know they are logged in
								final JPanel newClientPanel = new JPanel();
								JOptionPane.showMessageDialog(newClientPanel,
										("Welcome " + userName
												+ " you are now logged in and are now connected to the Server!"),
										"Student number not found!", JOptionPane.INFORMATION_MESSAGE);
								// Display a message to let the user know they are logged in
								statusView.append("Welcome " + userName
										+ " you are now logged in and are now connected to the Server!" + '\n');
								// Set the status to logged in with their name
								lblStatus.setText("Status: Logged In As " + userName);
								// Disable the login button so it cant be pressed again which would cause
								// problems
								btnLogin.setEnabled(false);
								// Enable the logout button so the user can press it
								btnLogout.setEnabled(true);
								// Set the action listener for the Logout button
								btnLogout.addActionListener(new ActionListener() {
									@Override
									// Define what happens when the Logout button is pressed
									public void actionPerformed(ActionEvent e) {
										try {
											// Send over the LOGOUT_CODE which lets the Server know the user wants log
											// out
											toServer.writeInt(LOGOUT_CODE);
											// Wipe the statusView text
											statusView.setText("");
											// Let the user they are logged out
											statusView.append(userName
													+ " you are now logged out and are now disconnected from the Server!"
													+ '\n');
											// Set the label status to logged out
											lblStatus.setText("Status: Logged Out");
											// enable the login button so they log in again
											btnLogin.setEnabled(true);
											// disable the logout button so it cant be pressed again which would cause
											// problems
											btnLogout.setEnabled(false);
											// Wipe the enteredStudentNumber text
											enteredStudentNumber.setText("");
										} catch (IOException logoutError) {
											final JPanel ioErrorPan = new JPanel();
											JOptionPane.showMessageDialog(ioErrorPan,
													"There was an problem logging out! Error: "
															+ logoutError.getMessage(),
													"Logout Error!", JOptionPane.ERROR_MESSAGE);
											System.err.println("There was an problem logging out! Error: : "
													+ logoutError.getMessage());
											logoutError.printStackTrace();
										}
									}
								});

							} else if (loginStatus == false) {
								// Student number entered by the user not found in the database by the Server
								// Display a pop up and add a message to the statusView to let the user know
								// they need to try again
								statusView.append("Student number not found! Log in failed - please try again." + '\n');
								final JPanel newClientPanel = new JPanel();
								JOptionPane.showMessageDialog(newClientPanel,
										"Student number not found! Log in failed - please try again!",
										"Student number not found!", JOptionPane.INFORMATION_MESSAGE);
								// enable the login button so they log in again
								btnLogin.setEnabled(true);
								// disable the logout button so it cant be pressed again which would cause
								// problems
								btnLogout.setEnabled(false);
								// set the enteredStudentNumber to blank so the user can try again
								enteredStudentNumber.setText("");
							}

						} else {
							// Handle if a user enters a invalid student number and display what they need
							// to do
							final JPanel newClientPanel = new JPanel();
							JOptionPane.showMessageDialog(newClientPanel,
									"Invalid Student Numbered Entered! Must Be A Number and 8 characters long (Eg: 12345678) ",
									"Invalid Student Numbered Entered!", JOptionPane.INFORMATION_MESSAGE);
							statusView.append(
									"Invalid Student Numbered Entered! Must Be A Number and 8 characters long (Eg: 12345678) \n");
							// set the enteredStudentNumber back to blank so the user can try again
							enteredStudentNumber.setText("");
						}

					} catch (IOException ex) {
						// Handle IOException errors and display them in a pop on the users screen
						// aswell as in the console
						System.err.println(ex);
						final JPanel ioErrorPan = new JPanel();
						JOptionPane.showMessageDialog(ioErrorPan, "There was an IOException! Error: " + ex.getMessage(),
								"IOException Error!", JOptionPane.ERROR_MESSAGE);
						System.err.println("There was an IOException! Error: " + ex.getMessage());
						ex.printStackTrace();
					}

				}
			});
			// Set the ActionListener for the btnExit
			btnExit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
					try {
						socket.close();
					} catch (IOException exitError) {
						// Handle an IOException when the exit button is pressed
						exitError.printStackTrace();
						final JPanel ioErrorPan = new JPanel();
						JOptionPane.showMessageDialog(ioErrorPan,
								"There was a probelm exiting! Error: " + exitError.getMessage(), "Exit Error!",
								JOptionPane.ERROR_MESSAGE);
						System.err.println("There was a probelm exiting! Error: " + exitError.getMessage());
						exitError.printStackTrace();
					}
				}
			});

		} catch (IOException ex) {
			// Handle IOException errors and display them in a pop on the users screen
			// aswell as in the console
			System.out.print(ex.toString() + '\n');
			final JPanel ioErrorPan = new JPanel();
			JOptionPane.showMessageDialog(ioErrorPan, "There was an IOException! Error: " + ex.getMessage(),
					"IOException Error!", JOptionPane.ERROR_MESSAGE);
			System.err.println("There was an IOException! Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}