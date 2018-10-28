package i.threads;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import b.exceptions.DaoExceptions.DaoException;
import c.connectionPool.ConnectionPool;
import d.beanShells.Coupon;
import g.daoDB.CouponDaoDB;

public class DailyCouponExpirationTask implements Runnable {

	private CouponDaoDB coupDb = new CouponDaoDB();
	private boolean quit = false;

	public DailyCouponExpirationTask() {
	}

	@Override
	public void run() {

		while (!quit) {
			ArrayList<Long> list = new ArrayList<>();
			Date date1 = new Date(System.currentTimeMillis());
			String sql = "SELECT coup_id FROM coupon WHERE end_date < '" + date1 + "'";
			Connection con = ConnectionPool.getPool().getConnection();
			try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
				while (rs.next()) {
					list.add(rs.getLong("coup_id"));
				}
				if (!list.isEmpty()) {
					System.out.println("Found " + list.size() + " coupons to delete, deleting.");
					for (Long long1 : list) {
						Coupon coup = new Coupon();
						coup.setId(long1);
						try {
							coupDb.fullyRemoveCoupon(coup);
						} catch (DaoException e) {
							System.err.println("Failed to delete existing coupon at thread");
							e.printStackTrace();
						}
					}
					System.out.println("Finished deleting, going to sleep");
					Thread.sleep(86400000);
				} else {
					System.out.println("No coupons to delete, going to sleep");
					Thread.sleep(86400000);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("Thread has been interrupted, shutting down");
			} finally {
				ConnectionPool.getPool().returnConnection(con);
				System.out.println(Thread.currentThread().getName() + " is closing");
			}
		}

	}

	public void stop() {
		quit = true;
		Thread.currentThread().interrupt();
	}

}
