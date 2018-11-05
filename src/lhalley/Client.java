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
	private JTextField txtId;
	
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
		
		txtId = new JTextField();
		txtId.setText("ID");
		txtId.setBounds(78, 11, 143, 20);
		getContentPane().add(txtId);
		txtId.setColumns(10);
		
		JLabel lblStudentId = new JLabel("Student ID:");
		lblStudentId.setBounds(10, 14, 58, 14);
		getContentPane().add(lblStudentId);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 42, 754, 14);
		getContentPane().add(separator);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(231, 10, 95, 23);
		getContentPane().add(btnLogin);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(448, 11, 296, 20);
		getContentPane().add(textPane);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(402, 14, 46, 14);
		getContentPane().add(lblStatus);

		setTitle("Client");
		setSize(770, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		try {
			// Create a socket to connect to the server
			Socket socket = new Socket(add, port);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());

		} catch (IOException ex) {
			System.out.print(ex.toString() + '\n');
		}
	}
}