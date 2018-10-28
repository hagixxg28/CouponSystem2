package f.dao;

import java.util.Collection;

import b.exceptions.DaoExceptions.DaoException;
import d.beanShells.Company;
import d.beanShells.Coupon;

public interface CompanyDao {

	void createCompany(Company comp) throws DaoException;

	void removeCompany(Company comp) throws DaoException;

	void updateCompany(Company comp) throws DaoException;

	Company readCompany(Company comp) throws DaoException;

	Collection<Company> getAllCompanies() throws DaoException;

	Boolean login(Long id, String password) throws DaoException;

	Collection<Coupon> getAllCoupons(Company comp) throws DaoException;

}
