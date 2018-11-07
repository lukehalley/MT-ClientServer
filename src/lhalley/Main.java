package lhalley;

public class Main {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		Server server = new Server();
		server.main(args);
		Client client = new Client();
		client.main(args);
	}

}
