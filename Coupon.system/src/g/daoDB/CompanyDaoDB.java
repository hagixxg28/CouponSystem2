package g.daoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import b.exceptions.DaoExceptions.DaoException;
import c.connectionPool.ConnectionPool;
import d.beanShells.Company;
import d.beanShells.Coupon;
import e.enums.CouponType;
import f.dao.CompanyDao;

public class CompanyDaoDB implements CompanyDao {
	private ConnectionPool pool = ConnectionPool.getPool();

	public CompanyDaoDB() {
		super();
	}

	@Override
	public void createCompany(Company comp) throws DaoException {
		String sql1 = "INSERT INTO company  (comp_id,name,password,email) VALUES (?,?,?,?)";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql1);) {
			stmt.setLong(1, comp.getId());
			stmt.setString(2, comp.getCompName());
			stmt.setString(3, comp.getPassword());
			stmt.setString(4, comp.getEmail());
			stmt.executeUpdate();
			System.out.println(comp + " has been added");
		} catch (SQLException e) {
			throw new DaoException("Id is already in use or null variables has been placed");
		} finally {
			pool.returnConnection(con);

		}

	}

	@Override
	public void removeCompany(Company comp) throws DaoException {
		String sql = ("DELETE FROM company WHERE comp_id=?");
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.setLong(1, comp.getId());
			stmt.executeUpdate();
			comp.getCoupons().clear();
			System.out.println(comp + " was removed");
		} catch (SQLException e) {
			throw new DaoException("Company was not found");
		} finally {
			pool.returnConnection(con);
		}
	}

	@Override
	public void updateCompany(Company comp) throws DaoException {
		String sql = "UPDATE company SET password=?, email=?  WHERE comp_id=?";
		Connection con = pool.getConnection();
		try (PreparedStatement stmt = con.prepareStatement(sql);) {
			stmt.setString(1, comp.getPassword());
			stmt.setString(2, comp.getEmail());
			stmt.setLong(3, comp.getId());
			stmt.executeUpdate();
			System.out.println(comp + " was updated");

		} catch (SQLException e) {
			throw new DaoException("Company was not found");
		} finally {
			pool.returnConnection(con);
		}

	}

	@Override
	public Company readCompany(Company comp) throws DaoException {
		Company other = new Company();
		String sql = "SELECT * FROM company WHERE comp_id=" + comp.getId();
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {

			while (rs.next()) {
				other.setId(rs.getLong("comp_id"));
				other.setCompName(rs.getString("name"));
				other.setPassword(rs.getString("password"));
				other.setEmail(rs.getString("email"));
				other.setCoupons(getAllCoupons(comp));
			}

		} catch (SQLException e) {
			throw new DaoException("No company was found");
		} finally {
			pool.returnConnection(con);
		}
		return other;
	}

	@Override
	public Collection<Company> getAllCompanies() throws DaoException {
		Collection<Company> collection = new ArrayList<Company>();
		String sql = "SELECT * FROM company";
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
				Company other = new Company(rs.getLong("comp_id"), rs.getString("name"), rs.getString("password"),
						rs.getString("email"));
				collection.add(other);
			}
			for (Company company : collection) {
				company.setCoupons(getAllCoupons(company));
			}

		} catch (SQLException e) {
			throw new DaoException("There are no companies");
		} finally {
			pool.returnConnection(con);
		}
		return collection;
	}

	@Override
	public Boolean login(Long id, String password) throws DaoException {
		String sql = "SELECT password FROM company WHERE comp_id=" + id;
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			if (!rs.next()) {
				System.out.println("Wrong pass");
				return false;
			}
			String str = rs.getString("password");
			if (password.equals(str)) {
				return true;
			} else {
				System.out.println("Wrong pass");
				return false;
			}
		} catch (SQLException e) {
			throw new DaoException("Unable to login-Wrong password or Id");
		} finally {
			pool.returnConnection(con);
		}
	}

	@Override
	public Collection<Coupon> getAllCoupons(Company comp) throws DaoException {
		Collection<Coupon> collection = new ArrayList<Coupon>();
		String sql = "SELECT company_coupon.coup_id FROM company_coupon INNER JOIN coupon"
				+ " ON coupon.coup_id=company_coupon.coup_id" + " WHERE comp_id=" + comp.getId();
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
				Coupon other = new Coupon();
				other.setId(rs.getLong("coup_id"));
				collection.add(other);
			}
			for (Coupon coupon : collection) {
				String sql2 = "Select *  FROM coupon where coup_id=" + coupon.getId();
				try (ResultSet rss = stmt.executeQuery(sql2);) {
					while (rss.next()) {
						coupon.setId(rss.getLong("coup_id"));
						coupon.setTitle(rss.getString("title"));
						coupon.setStartDate(rss.getDate("start_date"));
						coupon.setEndDate(rss.getDate("end_date"));
						coupon.setAmount(rss.getInt("amount"));
						coupon.setType(CouponType.typeSort(rss.getString("type")));
						coupon.setMessage(rss.getString("message"));
						coupon.setPrice(rss.getDouble("price"));
						coupon.setImage(rss.getString("image"));
					}
				}
			}
		} catch (SQLException e) {
			throw new DaoException("No coupons were found under this company");
		} finally {
			pool.returnConnection(con);
		}
		return collection;
	}

	public boolean companyExists(Company comp) throws DaoException {
		ArrayList<Long> list = new ArrayList<>();
		String sql = "SELECT comp_id FROM company WHERE comp_id=" + comp.getId();
		Connection con = pool.getConnection();
		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {

			while (rs.next()) {
				list.add((rs.getLong("comp_id")));
			}
			for (Long long1 : list) {
				if (comp.getId() == long1) {
					return true;
				}
			}

		} catch (SQLException e) {
			throw new DaoException("Error occurred at companyExists method");
		} finally {
			pool.returnConnection(con);
		}
		return false;
	}

}
