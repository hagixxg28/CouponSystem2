package c.connectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;

public class ConnectionPool {

	private Stack<Connection> conStack = new Stack<>();
	private static ConnectionPool instance;
	public static final int MAX = 10;
	String driverName = "org.apache.derby.jdbc.ClientDriver";
	String url = "jdbc:derby://localhost:1527/coupon_db";

	private ConnectionPool() {
		super();
		for (int i = 0; i < MAX; i++) {
			try {
				Connection con = DriverManager.getConnection(url);
				conStack.push(con);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	public static ConnectionPool getPool() {
		if (instance == null) {
			instance = new ConnectionPool();
		}
		return instance;

	}

	public synchronized Connection getConnection() {
		while (conStack.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		notifyAll();
		Connection con = null;
		con = conStack.pop();
		return con;
	}

	public synchronized void returnConnection(Connection Connection) {
		while (conStack.size() == MAX) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		conStack.push(Connection);
		notifyAll();
	}

	public synchronized void closeConnections() {
		System.out.println("ConnectionPool shutting down");
		for (Connection connection : conStack) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println("unable to close connection");
				e.printStackTrace();
			}
		}
		conStack.removeAll(conStack);
	}

}
