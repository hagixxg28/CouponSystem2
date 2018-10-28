package d.beanShells;

import java.util.ArrayList;
import java.util.Collection;

public class Customer {
	private long id;
	private String custName;
	private String password;
	private Collection<Coupon> coupons = new ArrayList<>();
	private Collection<Coupon> couponHistory = new ArrayList<>();

	public Collection<Coupon> getCouponHistory() {
		return couponHistory;
	}

	public void setCouponHistory(Collection<Coupon> couponHistory) {
		this.couponHistory = couponHistory;
	}

	public Customer() {
		super();
	}

	public Customer(long id, String custName, String password) {
		super();
		setId(id);
		setCustName(custName);
		setPassword(password);
		coupons = new ArrayList<>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", custName=" + custName + ", password=" + password + ", coupons=" + coupons
				+ "]";
	}

}
