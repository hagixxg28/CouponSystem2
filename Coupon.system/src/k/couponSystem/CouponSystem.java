package k.couponSystem;

import b.exceptions.CouponSystemException;
import b.exceptions.InvalidLogging;
import b.exceptions.DaoExceptions.NoCompaniesException;
import b.exceptions.DaoExceptions.NoCustomersException;
import c.connectionPool.ConnectionPool;
import e.enums.ClientType;
import g.daoDB.CompanyDaoDB;
import g.daoDB.CustomerDaoDB;
import i.threads.DailyCouponExpirationTask;
import j.facade.AdminFacade;
import j.facade.CompanyFacade;
import j.facade.CouponClientFacade;
import j.facade.CustomerFacade;

public class CouponSystem {
	private CompanyDaoDB compDb;
	private CustomerDaoDB custDb;
	private CouponClientFacade client;
	private static CouponSystem coupSys = null;
	private DailyCouponExpirationTask task = new DailyCouponExpirationTask();
	private Thread thread = new Thread(task);

	private CouponSystem() {
		System.out.println("Creating a new system");
		compDb = new CompanyDaoDB();
		custDb = new CustomerDaoDB();
		System.out.println("Loaded DaoDB");
		thread.start();
		System.out.println("Loaded thread");
	}

	public static CouponSystem getInstance() {
		if (coupSys == null) {
			coupSys = new CouponSystem();
			return coupSys;
		} else {
			return coupSys;
		}
	}

	public CouponClientFacade login(Long id, String password, ClientType type) throws CouponSystemException {
		switch (type) {
		case CUSTOMER:
			if (custDb.login(id, password)) {
				CustomerFacade facade = new CustomerFacade();
				System.out.println("Welcome customer");
				facade.custLogin(id, password);
				client = facade;
				return client;
			} else {
				throw new NoCustomersException("There is no such customer in our Database");
			}
		case COMPANY:
			if (compDb.login(id, password)) {
				CompanyFacade facade = new CompanyFacade();
				System.out.println("Welcome company");
				facade.custLogin(id, password);
				client = facade;
				return client;
			} else {
				throw new NoCompaniesException("There is no such company in our Database");
			}
		case ADMIN:
			if (id == 11 && password == "1234") {
				AdminFacade facade = new AdminFacade();
				System.out.println("Welcome admin");
				client = facade;
				return client;
			} else {
				throw new InvalidLogging("Wrong id or password");
			}
		default:
			throw new InvalidLogging("Wrong id or password");
		}
	}

	public void shutDown() {
		task.stop();
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConnectionPool.getPool().closeConnections();
		System.exit(0);
	}
}
