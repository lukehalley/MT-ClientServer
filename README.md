# MT-ClientServer
## Multithreaded Client/Server - Student Database - Dist Systems

### Client part of the Multithreaded Client/Server Java application
The client connects to the Server on startup which is placed in a new thread. The use can then enter a 8 digit student number
which is sent over to Server where it validates that the Student number sent exists in a database table.
The user is sent messages based on their login status and can also logout and sign in again as a different user or the same again

### Server part of the Multithreaded Client/Server Java application

The server only accepts requests from registered students. The client (code for this in the other class ClientA2.java) enters their StudentID and submits request to the Server. The server creates a new thread for the client and validates that the
Student exists in a database table. Invalid logins will result in an appropriate message being sent to the client and the socket is closed.
