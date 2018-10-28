package j.facade;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import b.exceptions.DaoExceptions.DaoException;
import b.exceptions.FacadeExceptions.FacadeException;
import b.exceptions.FacadeExceptions.NoCouponsException;
import d.beanShells.Company;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import e.enums.CouponType;
import g.daoDB.CompanyDaoDB;
import g.daoDB.CouponDaoDB;
import g.daoDB.CustomerDaoDB;

public class CompanyFacade implements CouponClientFacade {
	private CompanyDaoDB db = new CompanyDaoDB();
	private Company currentComp = new Company();
	private CouponDaoDB coupDb = new CouponDaoDB();
	private CustomerDaoDB custDb = new CustomerDaoDB();

	public CompanyFacade() {

	}

	public void createCoupon(Coupon coup) throws FacadeException {
		Collection<Coupon> List;
		try {
			List = db.getAllCoupons(currentComp);
			for (Coupon coupon : List) {
				if (coupon.getTitle().equals(coup.getTitle())) {
					coupDb.createCoupon(coup, currentComp);
					List.add(coup);
					Company comp = db.readCompany(currentComp);
					comp.setCoupons(List);
				}
			}
		} catch (DaoException e) {
			throw new FacadeException("A coupon with this title already exists");
		}
	}

	public void custLogin(Long id, String password) throws FacadeException {
		try {
			if (db.login(id, password)) {
				currentComp.setId(id);
			} else {
				throw new FacadeException("Logging failed");

			}
		} catch (DaoException e) {
			throw new FacadeException("Logging failed");
		}
	}

	public void removeCoupon(Coupon coup) throws FacadeException {
		try {
			coupDb.fullyRemoveCoupon(coupDb.getCoupon(coup));
			Company comp;
			comp = db.readCompany(currentComp);
			comp.getCoupons().remove(coup);
		} catch (DaoException e) {
			throw new FacadeException("Coupon not found");
		}
		Collection<Customer> List;
		try {
			List = custDb.getAllCustomer();
		} catch (DaoException e) {
			throw new FacadeException("There are no customers on the Database");
		}
		for (Customer customer : List) {
			if (customer.getCoupons().contains(coup)) {
				customer.getCoupons().remove(coup);
			}
		}
	}

	public void updateCoupon(Coupon coup) throws FacadeException {
		Double newPrice = coup.getPrice();
		Date newDate = coup.getEndDate();
		Coupon updatedCoup;
		try {
			updatedCoup = coupDb.getCoupon(coup);
		} catch (DaoException e) {
			throw new NoCouponsException("There is no such coupon in the Database");
		}
		updatedCoup.setPrice(newPrice);
		updatedCoup.setEndDate(newDate);
		try {
			coupDb.updateCoupon(updatedCoup);
		} catch (DaoException e) {
			throw new NoCouponsException("There is no such coupon in the Database");
		}

	}

	public Coupon getCoupon(Coupon coup) throws FacadeException {
		try {
			return coupDb.getCoupon(coup);
		} catch (DaoException e) {
			throw new NoCouponsException("There is no such coupon in the Database");
		}
	}

	public Collection<Coupon> getAllCoupon() throws FacadeException {
		Company comp;
		try {
			comp = db.readCompany(currentComp);
		} catch (DaoException e) {
			throw new FacadeException("Error- try logging again");
		}
		if (!comp.getCoupons().isEmpty()) {
			return comp.getCoupons();
		} else {
			throw new NoCouponsException("There are no coupons for this company");
		}
	}

	public Collection<Coupon> getAllCouponByType(CouponType type) throws FacadeException {
		Company comp;
		try {
			comp = db.readCompany(currentComp);
		} catch (DaoException e) {
			throw new FacadeException("We have encountered an error, try logging in first");
		}
		Collection<Coupon> List = comp.getCoupons();
		Collection<Coupon> TypeList = new ArrayList<>();
		for (Coupon coupon : List) {
			if (coupon.getType().equals(type)) {
				TypeList.add(coupon);
			}
		}
		return TypeList;
	}

	public Collection<Coupon> getAllCouponByPrice(double price) throws FacadeException {
		Company comp;
		try {
			comp = db.readCompany(currentComp);
		} catch (DaoException e) {
			throw new FacadeException("We have encountered an error, try logging in");
		}
		Collection<Coupon> List = comp.getCoupons();
		Collection<Coupon> priceList = new ArrayList<>();
		for (Coupon coupon : List) {
			if (coupon.getPrice() <= price) {
				priceList.add(coupon);
			}
		}
		return priceList;
	}

	public Collection<Coupon> getAllCouponByDate(Date date) throws FacadeException {
		Company comp;
		try {
			comp = db.readCompany(currentComp);
		} catch (DaoException e) {
			throw new FacadeException("We have encountered an error, try logging in");
		}
		Collection<Coupon> List = comp.getCoupons();
		Collection<Coupon> dateList = new ArrayList<>();
		for (Coupon coupon : List) {
			if (coupon.getEndDate().before(date)) {
				dateList.add(coupon);
			}
		}
		return dateList;
	}
}
