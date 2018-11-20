package a.creatingTables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TableCreation {

	public void createAll() {
		String driverName = "org.apache.derby.jdbc.ClientDriver";
		String url = "jdbc:derby://localhost:1527/coupon_db";
		String sql1 = "CREATE TABLE company(comp_id BIGINT PRIMARY KEY, name VARCHAR(50), password VARCHAR(50), email VARCHAR(50))";
		String sql2 = "CREATE TABLE customer(cust_id BIGINT PRIMARY KEY, name VARCHAR(50), password VARCHAR(50))";
		String sql3 = "CREATE TABLE coupon(coup_id BIGINT PRIMARY KEY, title VARCHAR(50), start_date DATE, end_date DATE, amount INTEGER, "
				+ "type VARCHAR(50), message VARCHAR(150), price FLOAT, image VARCHAR(50) )";
		String sql4 = "CREATE TABLE customer_coupon(cust_id BIGINT, coup_id BIGINT)";
		String sql5 = "CREATE TABLE company_coupon(comp_id BIGINT, coup_id BIGINT)";
		try {
			Class.forName(driverName);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try (Connection con = DriverManager.getConnection(url + ";create=true");
				Statement stmt = con.createStatement();) {
			stmt.executeUpdate(sql1);
			System.out.println(sql1);
			stmt.executeUpdate(sql2);
			System.out.println(sql2);
			stmt.executeUpdate(sql3);
			System.out.println(sql3);
			stmt.executeUpdate(sql4);
			System.out.println(sql4);
			stmt.executeUpdate(sql5);
			System.out.println(sql5);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
