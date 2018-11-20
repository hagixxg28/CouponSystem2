package g.daoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import b.exceptions.DaoExceptions.CouponAlreadyExistsException;
import b.exceptions.DaoExceptions.CouponDoesNotExistException;
import b.exceptions.DaoExceptions.CustomerDoesNotExistException;
import b.exceptions.DaoExceptions.DaoException;
import c.connectionPool.ConnectionPool;
import d.beanShells.Company;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import e.enums.CouponType;
import f.dao.CouponDao;

public class CouponDaoDB implements CouponDao {
	private ConnectionPool pool = ConnectionPool.getPool();
	private CustomerDaoDB custDb = new CustomerDaoDB();

	public CouponDaoDB() {
		super();
	}

	@Override
	public void createCoupon(Coupon coup, Company comp) throws DaoException {
		String sql = "INSERT INTO coupon VALUES (?, ?, ?, ? ,?, ?, ?, ?, ?)";
		String sql2 = "INSERT INTO company_coupon VALUES(?, ?)";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);
				PreparedStatement stmt2 = con.prepareStatement(sql2);) {
			stmt.setLong(1, coup.getId());
			stmt.setString(2, coup.getTitle());
			stmt.setDate(3, coup.getStartDate());
			stmt.setDate(4, coup.getEndDate());
			stmt.setInt(5, coup.getAmount());
			stmt.setString(6, CouponType.typeToString(coup.getType()));
			stmt.setString(7, coup.getMessage());
			stmt.setDouble(8, coup.getPrice());
			stmt.setString(9, coup.getImage());
			stmt.executeUpdate();
			stmt2.setLong(1, comp.getId());
			stmt2.setLong(2, coup.getId());
			stmt2.executeUpdate();
			System.out.println(coup + " has been added");
		} catch (SQLException e) {
			throw new CouponAlreadyExistsException("This coupon already exists");
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void fullyRemoveCoupon(Coupon coup) throws DaoException {
		String sql = String.format("DELETE FROM coupon WHERE coup_id=%d", coup.getId());
		String sql2 = String.format("DELETE FROM company_coupon WHERE coup_id=%d", coup.getId());
		String sql3 = String.format("DELETE FROM customer_coupon WHERE coup_id=%d", coup.getId());
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);
				PreparedStatement stmt2 = con.prepareStatement(sql2);
				PreparedStatement stmt3 = con.prepareStatement(sql3);) {
			stmt.executeUpdate();
			stmt2.executeUpdate();
			stmt3.executeUpdate();
			Collection<Customer> custList = custDb.getAllCustomer();
			for (Customer customer : custList) {
				Collection<Coupon> coupList = customer.getCoupons();
				for (Coupon coup2 : coupList) {
					if (coup2.getId() == coup.getId()) {
						coupList.remove(coup2);
					}
				}
			}
			System.out.println("Coupon with id " + coup.getId() + " has been removed");
		} catch (SQLException e) {
			throw new CouponDoesNotExistException("This coupon does not exist");
		} finally {
			pool.returnConnection(con);
		}
	}

// NOTICE THAT YOU MIGHT NEED TO REMOVE START DATE, HOW CAN YOU UPDATE THAT SHIT?
	@Override
	public void updateCoupon(Coupon coup) throws DaoException {
		String sql = "UPDATE coupon SET title=?, start_date=?, end_date=?, amount=?, type=?, message=?, price=?, image=? WHERE coup_id=?";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.setString(1, coup.getTitle());
			stmt.setDate(2, coup.getStartDate());
			stmt.setDate(3, coup.getEndDate());
			stmt.setInt(4, coup.getAmount());
			stmt.setString(5, CouponType.typeToString(coup.getType()));
			stmt.setString(6, coup.getMessage());
			stmt.setDouble(7, coup.getPrice());
			stmt.setString(8, coup.getImage());
			stmt.setLong(9, coup.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponDoesNotExistException("This coupon does not exist");
		} finally {
			pool.returnConnection(con);
		}
	}

	@Override
	public Coupon getCoupon(Coupon coup) throws DaoException {
		Coupon otherCoup = new Coupon();
		String sql = "SELECT * FROM coupon WHERE coup_id=" + coup.getId();
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery();) {
			while (rs.next()) {
				otherCoup.setId(rs.getLong("coup_id"));
				otherCoup.setTitle(rs.getString("title"));
				otherCoup.setStartDate(rs.getDate("start_date"));
				otherCoup.setEndDate(rs.getDate("end_date"));
				otherCoup.setAmount(rs.getInt("amount"));
				otherCoup.setType(CouponType.typeSort(rs.getString("type")));
				otherCoup.setMessage(rs.getString("message"));
				otherCoup.setPrice(rs.getDouble("price"));
				otherCoup.setImage(rs.getString("image"));
			}

		} catch (SQLException e) {
			throw new CouponDoesNotExistException("This coupon does not exist");
		} finally {
			pool.returnConnection(con);
		}
		return otherCoup;
	}

	@Override
	public Collection<Coupon> getAllCoupons() throws DaoException {
		Collection<Coupon> collection = new ArrayList<Coupon>();
		String sql = "SELECT * FROM coupon";
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
				Coupon coup = new Coupon(rs.getLong("coup_id"), rs.getString("title"), rs.getDate("start_date"),
						rs.getDate("end_date"), rs.getInt("amount"), CouponType.typeSort(rs.getString("type")),
						rs.getString("message"), rs.getDouble("price"), rs.getString("image"));
				collection.add(coup);
			}
		} catch (SQLException e) {
			throw new CouponDoesNotExistException("There are no coupons at the database");
		} finally {
			pool.returnConnection(con);
		}
		if (!collection.isEmpty()) {
			return collection;
		} else {
			throw new CouponDoesNotExistException("There are no coupons at the database");
		}
	}

	@Override
	public Collection<Coupon> getCouponByType(CouponType type) throws DaoException {
		Collection<Coupon> collection = new ArrayList<Coupon>();
		String sql = "SELECT * FROM coupon WHERE type='" + CouponType.typeToString(type) + "'";
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
				Coupon coup = new Coupon(rs.getLong("coup_id"), rs.getString("title"), rs.getDate("start_date"),
						rs.getDate("end_date"), rs.getInt("amount"), CouponType.typeSort(rs.getString("type")),
						rs.getString("message"), rs.getDouble("price"), rs.getString("image"));
				collection.add(coup);
			}
		} catch (SQLException e) {
			throw new CouponDoesNotExistException("There are no coupons with that type at the database");
		} finally {
			pool.returnConnection(con);
		}
		if (!collection.isEmpty()) {
			return collection;
		} else {
			throw new CouponDoesNotExistException("There are no coupons with that type");
		}
	}

	@Override
	public void customerPurchaseCoupon(Coupon coup, Customer cust) throws DaoException {
		String sql2 = "INSERT INTO customer_coupon VALUES(?, ?)";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt2 = con.prepareStatement(sql2);) {
			stmt2.setLong(1, cust.getId());
			stmt2.setLong(2, coup.getId());
			stmt2.executeUpdate();
			System.out.println(coup + " has been purchased");
		} catch (SQLException e) {
			throw new CouponAlreadyExistsException("This coupon already exists for this customer");
		}
	}

	@Override
	public void removeCouponComp(Coupon coup) throws DaoException {
		String sql2 = String.format("DELETE FROM company_coupon WHERE coup_id=%d", coup.getId());
		Connection con = pool.getConnection();
		try (PreparedStatement stmt2 = con.prepareStatement(sql2);) {
			stmt2.executeUpdate();
			System.out.println("Coupon with id " + coup.getId() + " has been removed");
		} catch (SQLException e) {
			throw new CouponDoesNotExistException("There is no coupon to remove");
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeCouponCust(Coupon coup) throws DaoException {
		String sql2 = String.format("DELETE FROM customer_coupon WHERE coup_id=%d", coup.getId());
		Connection con = pool.getConnection();
		try (PreparedStatement stmt2 = con.prepareStatement(sql2);) {
			stmt2.executeUpdate();
			Collection<Customer> custList = custDb.getAllCustomer();
			for (Customer customer : custList) {
				Collection<Coupon> coupList = customer.getCoupons();
				for (Coupon coup2 : coupList) {
					if (coup2.getId() == coup.getId()) {
						coupList.remove(coup2);
					}
				}
			}
			System.out.println("Coupon with id " + coup.getId() + " has been removed");
		} catch (SQLException e) {
			throw new CustomerDoesNotExistException("This customer does not exist");
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public void removeCouponCoup(Coupon coup) throws DaoException {
		String sql = String.format("DELETE FROM coupon WHERE coup_id=%d", coup.getId());
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.executeUpdate();
			System.out.println("Coupon with id " + coup.getId() + " has been removed");
		} catch (SQLException e) {
			throw new CouponDoesNotExistException("This cuopon does not exist");
		} finally {
			pool.returnConnection(con);
		}

	}

//	NOT USED AT THE MOMENT
//	public boolean customerOwnsCoupon(Coupon coup, Customer cust) throws DaoException {
//		String sql2 = "SELECT * customer_coupon WHERE cust_id=" + cust.getId() + " AND coup_id=" + coup.getId();
//		Connection con = pool.getConnection();
//		Collection<Object> collection = new ArrayList<>();
//		try (PreparedStatement stmt2 = con.prepareStatement(sql2); ResultSet rs = stmt2.executeQuery();) {
//			while (rs.next()) {
//				rs.getLong("cust_id");
//				rs.getLong("coup_id");
//			}
//			if (collection.isEmpty()) {
//				System.out.println("passed own check");
//				return false;
//			} else {
//				System.out.println("failed own check");
//				return true;
//			}
//		} catch (SQLException e) {
//			throw new CouponAlreadyExistsException("No coupons were found");
//		}
//	}

}
