package j.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import b.exceptions.DaoExceptions.DaoException;
import b.exceptions.FacadeExceptions.CouponExpiredException;
import b.exceptions.FacadeExceptions.FacadeException;
import b.exceptions.FacadeExceptions.NoCouponsException;
import b.exceptions.FacadeExceptions.OutOfCouponsException;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import e.enums.CouponType;
import g.daoDB.CouponDaoDB;
import g.daoDB.CustomerDaoDB;

public class CustomerFacade implements CouponClientFacade {
	private Date today = new Date(System.currentTimeMillis());
	private CustomerDaoDB db = new CustomerDaoDB();
	private Customer cust = new Customer();
	private CouponDaoDB coupDb = new CouponDaoDB();

	public CustomerFacade() {
	}

	public void custLogin(Long id, String password) throws FacadeException {
		try {
			if (db.login(id, password)) {
				cust.setId(id);
			} else {
				System.out.println("Logging failed");
			}
		} catch (DaoException e) {
			throw new FacadeException("This account does not exist, please try again");
		}
	}

	public void purchaseCoupon(Coupon coup) throws FacadeException {
		if (coup.getAmount() <= 0) {
			throw new OutOfCouponsException("There are no coupons left to buy");
		}

		if (coup.getEndDate().before(today)) {
			throw new CouponExpiredException("This coupon has expired");
		}
		try {
			cust = db.getCustomer(cust);
		} catch (DaoException e) {
			throw new FacadeException("Error- try logging again");
		}
		Collection<Coupon> coupons = cust.getCoupons();
		for (Coupon coupon : coupons) {
			if (coupon.getId() == coup.getId()) {
				throw new FacadeException("You already own this coupon");
			}
		}
		try {
			coupDb.customerPurchaseCoupon(coup, cust);
			coupons.add(coup);
			cust.setCoupons(coupons);
			coup.setAmount(coup.getAmount() - 1);
			coupDb.updateCoupon(coup);
		} catch (DaoException e) {
			throw new FacadeException("You already own this coupon/Other exception");
		}
	}
// IF YOU ADD COUPHISTORY YOU NEED TO ADD THIS CODE TO PURCHASE COUPON
//		try {
//			db.getCustomer(cust).getCouponHistory().add(coup);
//		} catch (DaoException e) {
//			throw new FacadeException("Try logging in again");
//		}

	// NO NEED----------------
	// public Collection<Coupon> getAllPurchasedCoupons() {
	// return db.getCustomer(cust).getCouponHistory();
	// }

	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) throws FacadeException {
		Collection<Coupon> List = new ArrayList<>();
		try {
			for (Coupon coupon : db.getCustomer(cust).getCoupons()) {
				if (coupon.getType().equals(type)) {
					List.add(coupon);
				}
			}
		} catch (DaoException e) {
			throw new FacadeException("Try logging in again");
		}
		if (!List.isEmpty()) {
			return List;
		} else {
			throw new NoCouponsException("There are no coupons purchased yet with this type");
		}
	}

	public Collection<Coupon> getAllPurchasedCouponsByPrice(Double price) throws FacadeException {
		Collection<Coupon> List = new ArrayList<>();
		try {
			for (Coupon coupon : db.getCustomer(cust).getCouponHistory()) {
				if (coupon.getPrice() <= price) {
					List.add(coupon);
				}
			}
		} catch (DaoException e) {
			throw new FacadeException("Try logging in again");
		}
		if (!List.isEmpty()) {
			return List;
		} else {
			throw new NoCouponsException("There are no coupons with this price range");
		}
	}

	public Collection<Coupon> getAllCoupons() throws FacadeException {
		Collection<Coupon> List = new ArrayList<>();
		try {
			List = db.getCoupons(cust);
		} catch (DaoException e) {
			throw new FacadeException("Error-try logging in");
		}
		if (!List.isEmpty()) {
			return List;
		} else {
			throw new NoCouponsException("There are no coupons");
		}
	}
}
