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

public class DailyDeleteThread implements Runnable {

	private CouponDaoDB dao;
	private volatile boolean quit = false;
	private ConnectionPool pool = ConnectionPool.getPool();

	public DailyDeleteThread() {
	}

	@Override
	public void run() {
		while (!quit) {
			ArrayList<Long> list = new ArrayList<Long>();
			Date d1 = new Date(System.currentTimeMillis());
			String sql = "SELECT coup_id FROM coupon WHERE end_date < '" + d1 + "'";
			Connection con = pool.getConnection();
			try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
				while (rs.next()) {
					list.add(rs.getLong("id"));
				}
				for (Long id : list) {
					Coupon coup = new Coupon();
					coup.setId(id);
					try {
						dao.fullyRemoveCoupon(coup);
					} catch (DaoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Thread.sleep(86400);
			} catch (SQLException e) {
				System.err.println(sql);
				System.err.println("WHYYY MEE??");
			} catch (InterruptedException e) {

			} finally {
				pool.returnConnection(con);
				System.out.println(list.size() + " coupons have been deleted");
				System.out.println(Thread.currentThread().getName() + " is stopping");
			}
		}
	}

	public void stopTask() {
		Thread.currentThread().interrupt();
		quit = true;
	}
}
