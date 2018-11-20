package h.testing;

import java.sql.Date;

import b.exceptions.CouponSystemException;
import b.exceptions.DaoExceptions.CompanyAlreadyExistsException;
import b.exceptions.DaoExceptions.CustomerAlreadyExistsException;
import d.beanShells.Company;
import d.beanShells.Coupon;
import d.beanShells.Customer;
import e.enums.ClientType;
import e.enums.CouponType;
import g.daoDB.CompanyDaoDB;
import g.daoDB.CouponDaoDB;
import g.daoDB.CustomerDaoDB;
import j.facade.AdminFacade;
import j.facade.CompanyFacade;
import j.facade.CustomerFacade;
import k.couponSystem.CouponSystem;

public class FullTest {
	public static void main(String[] args)
			throws CustomerAlreadyExistsException, CompanyAlreadyExistsException, CouponSystemException {
		// CREATION:
//		System.out.println("Creating tables phase:");
//		TableCreation creator = new TableCreation();
//		creator.createAll();
		// LOGIN PHASE
		System.out.println("------------LOGIN PHASE-----------");
		System.out.println("Starting logging test:");
		CouponSystem sys = CouponSystem.getInstance();
		AdminFacade admin = (AdminFacade) sys.login((long) 11, "1234", ClientType.ADMIN);
		if (admin instanceof AdminFacade) {
			System.out.println("admin worked");
		}
		System.out.println("Logging as Customer with password nice and ID 12345: ");
		CustomerFacade custFacade = (CustomerFacade) sys.login((long) 12345, "nice", ClientType.CUSTOMER);
		if (custFacade instanceof CustomerFacade) {
			System.out.println("custoemr worked");
		}
		System.out.println("Logging as company with password 1234 and id 45: ");
		CompanyFacade company = (CompanyFacade) sys.login((long) 45, "1234", ClientType.COMPANY);
		if (company instanceof CompanyFacade) {
			System.out.println("company worked");
		}
		// CRUDE STEPS:
		// STEP 1 Create + Read:
		System.out.println("----------------CRUDE PHASE- CREATE+ READ: --------------");
		System.out.println("Starting with Customer creation:");
		Customer c = new Customer(8935, "Elad", "ell8935");
//		admin.createCustomer(c);
		System.out.println("Using a READ to see if it exists:");
		System.out.println(admin.getCustomer(c));
		System.out.println("Creating a company: ");
		Company comp = new Company(54667, "Bob Nice Buety Education", "wecool", "contact@bobnice.com");
//		admin.createCompany(comp);
		System.out.println("Using a READ to see if it exists:");
		System.out.println(admin.getCompany(comp));
		System.out.println("Creating a coupon:");
		java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
		Coupon coup = new Coupon();
		coup.setId(3444);
		coup.setAmount(54);
		coup.setPrice(499);
		coup.setStartDate(date);
		coup.setEndDate(date);
		coup.setType(CouponType.CAMPING);
		coup.setTitle("I like it");
//		company.createCoupon(coup);
		System.out.println("Using a READ to see if it exists:");
		System.out.println(company.getCoupon(coup));
		// STEP 2 Update:
		System.out.println("------------ CRUDE PHASE: UPDATE-------------");
		System.out.println("Starting with Customer update:");
		Customer updatedCust = new Customer(c.getId(), "NewName", "NewPassword");
		System.out.println("Current cust:");
		System.out.println(admin.getCustomer(c));
		admin.updateCustomer(updatedCust);
		System.out.println("After update:");
		System.out.println(admin.getCustomer(c));
		System.out.println("Updating a company:");
		Company updatedComp = new Company(comp.getId(), "Better name", "weaker password", "besmail");
		System.out.println("Current comp:");
		System.out.println(admin.getCompany(comp));
		admin.updateCompany(updatedComp);
		System.out.println("After update:");
		System.out.println(admin.getCompany(comp));
		System.out.println("Updating a coupon:");
		Date laterDate = new Date(System.currentTimeMillis());
		System.out.println("Changing coupon:");
		coup.setTitle("Different");
		System.out.println(company.getCoupon(coup));
		company.updateCoupon(coup);
		System.out.println("After update:");
		System.out.println(company.getCoupon(coup));
		// STEP 3 Delete:
		System.out.println("------------ CRUDE PHASE: DELETE--------------");
		System.out.println("Deleting a customer:");
		System.out.println("Cust before deleting");
		System.out.println(admin.getCustomer(c));
		System.out.println("Cust after deleting");
		admin.removeCustomer(c);
		CustomerDaoDB custDb = new CustomerDaoDB();
		System.out.println(custDb.customerExists(c));
		System.out.println("Deleting a company:");
		System.out.println("Comp before deleting");
		System.out.println(admin.getCompany(comp));
		System.out.println("Comp after deleting");
		admin.removeCompany(comp);
		CompanyDaoDB compDb = new CompanyDaoDB();
		System.out.println(compDb.companyExists(comp));
		System.out.println("Deleting a coupon");
		System.out.println("Coup before deleting");
		System.out.println(company.getCoupon(coup));
		company.removeCoupon(coup);
		CouponDaoDB coupDb = new CouponDaoDB();
		System.out.println("After deleting:(This method returns null if it dosen't find anything)");
		System.out.println(coupDb.getCoupon(coup));
		System.out.println("------END OF CRUDE--------");
		// Purchasing a coupon:
		System.out.println("---------- Purchase example------------");
		System.out.println("We will recreate our happy customer, coupon and company:");
		admin.createCustomer(c);
		admin.createCompany(comp);
		company.createCoupon(coup);
		System.out.println("we will now buy a coupon with our customer Facade:");
		System.out.println("Lets see the coupon's amount before purchase:");
		System.out.println(company.getCoupon(coup));
		custFacade.purchaseCoupon(coup);
		System.out.println("Amount after purhcase:");
		System.out.println(company.getCoupon(coup));
		System.out.println("lets see if the customer owns this coupon:");
		System.out.println(custFacade.getAllCoupons());
	}
}
