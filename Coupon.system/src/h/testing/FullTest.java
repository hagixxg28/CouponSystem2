package h.testing;

import java.sql.Date;

import b.exceptions.CouponSystemException;
import b.exceptions.DaoExceptions.CompanyAlreadyExistsException;
import b.exceptions.DaoExceptions.CustomerAlreadyExistsException;
import d.beanShells.Company;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import e.enums.CouponType;
import j.facade.CompanyFacade;
import j.facade.CustomerFacade;

public class FullTest {
	public static void main(String[] args)
			throws CustomerAlreadyExistsException, CompanyAlreadyExistsException, CouponSystemException {
		System.out.println("STARTING");
//		CouponSystem sys = CouponSystem.getInstance();
//		AdminFacade admin = new AdminFacade();
		CustomerFacade facade = new CustomerFacade();
		CompanyFacade comp = new CompanyFacade();

		Date date = new Date(System.currentTimeMillis());
		Customer cock = new Customer(995, "Shlol", "321");
		Coupon coup = new Coupon(555, "dafdsa", date, date, 12, CouponType.TRAVELING, "buy me", 234, "sjakldj");
		Company compeny = new Company(8574987, "The big saddle company", "09808", "34kl;k");

		facade.custLogin(cock.getId(), cock.getPassword());
		System.out.println("logged to cust");
		comp.custLogin(compeny.getId(), compeny.getPassword());
		System.out.println("logged to comp");
		System.out.println(comp.getCoupon(coup));
//		System.out.println(comp.getAllCoupon());
//		System.out.println(comp.getAllCouponByType(CouponType.TRAVELING));
//		System.out.println(admin.getAllCustomer());
//		System.out.println(admin.getAllCompanies());
//		admin.removeCompany(compeny);
//		System.out.println(admin.getAllCompanies());

	}
}
