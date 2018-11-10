package lhalley;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ClientA2 extends JFrame {

	// IO streams
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private JTextField enteredStudentNumber;
	

	String add = "localhost";
	int port = 8000;
	int LOGOUT_CODE = 0000;
	String LOGOUT_CODE_STR = "0000";

	public static void main(String[] args) {
		new ClientA2();
	}

	public ClientA2() {
		getContentPane().setLayout(null);
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setBounds(0, 0, 484, 0);
		p.setLayout(new BorderLayout());
		getContentPane().add(p);

		enteredStudentNumber = new JTextField();
		enteredStudentNumber.setBounds(118, 47, 264, 20);
		getContentPane().add(enteredStudentNumber);
		enteredStudentNumber.setColumns(10);

		JLabel lblStudentId = new JLabel("Student Number: ");
		lblStudentId.setBounds(10, 50, 98, 14);
		getContentPane().add(lblStudentId);
		
		JTextArea lblStudentGuide = new JTextArea("Enter Only Numbers, Must Be 8 Characters, Greater Than 00000001 - Example: 12345678");
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

			btnLogin.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						statusView.setText("");
						// Get the radius from the text field
						// Check student number entered is not a string
						// ".*\\d+.*" = check its a number - 1234
						if (enteredStudentNumber.getText().matches(".*\\d+.*") && enteredStudentNumber.getText().length() == 8 && !enteredStudentNumber.getText().matches(LOGOUT_CODE_STR)) {
							
							int sentStudentNumber = Integer.parseInt(enteredStudentNumber.getText().trim());

							statusView.append("Checking if: " + sentStudentNumber + " is a valid and exsisting student number... \n");
							
							// Send the radius to the server
							toServer.writeInt(sentStudentNumber);
							toServer.flush();

							// Get the log in status from the server
							Boolean loginStatus = fromServer.readBoolean();
							
							// Tell the user if they are logged in or not
							if (loginStatus == true) {
								String userName = fromServer.readUTF();
								statusView.append("Welcome " + userName + " you are now logged in and are now connected to the Server!" + '\n');
								lblStatus.setText("Status: Logged In As " + userName);
								btnLogin.setEnabled(false);
								btnLogout.setEnabled(true);
								btnLogout.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										try {
											toServer.writeInt(0000);
											statusView.setText("");
											statusView.append(userName + " you are now logged out and are now disconnected from the Server!" + '\n');
											lblStatus.setText("Status: Logged Out");
											btnLogin.setEnabled(true);
											btnLogout.setEnabled(false);
											enteredStudentNumber.setText("");
										} catch (IOException e1) {
											e1.printStackTrace();
										}
									}
								});
								
							} else if (loginStatus == false) {
								System.out.println("Student number not found! Log in failed - please try again.\" + '\\n");
								statusView.append("Student number not found! Log in failed - please try again." + '\n');
								btnLogin.setEnabled(true);
								btnLogout.setEnabled(false);
							}
							
						} else {
							statusView.append("Invalid Student Numbered Entered! Must Be A Number and 8 characters long (Eg: 12345678) \n");
							enteredStudentNumber.setText("");
						}
						
					} catch (IOException ex) {
						System.err.println(ex);
					}
					
				}
			});
			
			btnExit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			


		} catch (IOException ex) {
			System.out.print(ex.toString() + '\n');
		}
	}
}