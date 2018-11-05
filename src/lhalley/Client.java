package lhalley;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Client extends JFrame {

	// IO streams
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private JTextField enteredStudentNumber;

	String add = "localhost";
	int port = 8000;

	public static void main(String[] args) {
		new Client();
	}

	@SuppressWarnings("resource")
	public Client() {
		getContentPane().setLayout(null);
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setBounds(0, 0, 484, 0);
		p.setLayout(new BorderLayout());
		getContentPane().add(p);

		enteredStudentNumber = new JTextField();
		enteredStudentNumber.setBounds(78, 11, 95, 20);
		getContentPane().add(enteredStudentNumber);
		enteredStudentNumber.setColumns(10);

		JLabel lblStudentId = new JLabel("Student ID:");
		lblStudentId.setBounds(10, 14, 58, 14);
		getContentPane().add(lblStudentId);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 42, 754, 14);
		getContentPane().add(separator);

		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(183, 10, 95, 23);
		getContentPane().add(btnLogin);

		JTextArea statusView = new JTextArea();
		statusView.setBounds(10, 79, 414, 171);
		getContentPane().add(statusView);

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(10, 54, 46, 14);
		getContentPane().add(lblStatus);
		
		JButton btnExit = new JButton("Exit");
		btnExit.setBounds(329, 11, 95, 23);
		getContentPane().add(btnExit);

		setTitle("Client");
		setSize(450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		try {
			// Create a socket to connect to the server
			Socket socket = new Socket(add, port);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());

			btnExit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});

			btnLogin.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						// Get the radius from the text field
						int sentStudentNumber = Integer.parseInt(enteredStudentNumber.getText().trim());

						statusView.append("Checking if: " + sentStudentNumber + " is a valid and exsisting student number... \n");
						
						// Send the radius to the server
						toServer.writeInt(sentStudentNumber);
						toServer.flush();

						// Get the log in status from the server
						Boolean loginStatus = fromServer.readBoolean();
						
						String userName = fromServer.readUTF();

						// Tell the user if they are logged in or not
						if (loginStatus) {
							statusView.append("Welcome " + userName + " you are now logged in " + '\n');
						} else {
							statusView.append("Log in failed please try again " + '\n');
						}

					} catch (IOException ex) {
						System.err.println(ex);
					}
				}
			});

		} catch (IOException ex) {
			System.out.print(ex.toString() + '\n');
		}
	}
}