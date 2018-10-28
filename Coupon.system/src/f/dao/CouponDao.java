package f.dao;

import java.util.Collection;

import b.exceptions.DaoExceptions.DaoException;
import d.beanShells.Company;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import e.enums.CouponType;

public interface CouponDao {

	void createCoupon(Coupon coup, Company comp) throws DaoException;

	void customerPurchaseCoupon(Coupon coup, Customer cust) throws DaoException;

	void fullyRemoveCoupon(Coupon coup) throws DaoException;

	void removeCouponComp(Coupon coup) throws DaoException;

	void removeCouponCust(Coupon coup) throws DaoException;

	void removeCouponCoup(Coupon coup) throws DaoException;

	void updateCoupon(Coupon coup) throws DaoException;

	Coupon getCoupon(Coupon coup) throws DaoException;

	Collection<Coupon> getAllCoupons() throws DaoException;

	Collection<Coupon> getCouponByType(CouponType type) throws DaoException;
}