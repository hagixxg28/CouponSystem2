package i.threads;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import b.exceptions.DaoExceptions.DaoException;
import c.connectionPool.ConnectionPool;
import d.beanShells.Coupon;
import g.daoDB.CouponDaoDB;

public class DailyDeleteThreadMk2 implements Runnable {

	private Thread Deleter;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private CouponDaoDB dao = new CouponDaoDB();
	private ConnectionPool pool = ConnectionPool.getPool();

	public void start() {
		Deleter = new Thread(this);
		Deleter.start();
	}

	@Override
	public void run() {
		running.set(true);
		while (isRunning()) {
			ArrayList<Long> list = new ArrayList<Long>();
			Date date1 = new Date(System.currentTimeMillis());
			String sql = "SELECT coup_id FROM coupon WHERE end_date < '" + date1 + "'";
			Connection con = pool.getConnection();
			try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
				while (rs.next()) {
					list.add(rs.getLong("coup_id"));
				}
				if (!list.isEmpty()) {
					for (Long long1 : list) {
						Coupon coup = new Coupon();
						coup.setId(long1);
						try {
							dao.fullyRemoveCoupon(coup);
						} catch (DaoException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} else {
					System.out.println("No coupons out of date, going to sleep");
					try {
						Thread.sleep(86400);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						System.out.println("Thread has been interrupted, shutting down");
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(list.size() + " coupons has been deleted, going to sleep.");
			try {
				Thread.sleep(86400);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("Thread has been interrupted, shutting down");
			} finally {
				pool.returnConnection(con);
				System.out.println(Thread.currentThread().getName() + " is stopping");
			}
		}

	}

	public void stop() {
		running.set(false);
	}

	public void interrupt() {
		running.set(false);
		Deleter.interrupt();
	}

	public boolean isRunning() {
		return running.get();
	}
}
