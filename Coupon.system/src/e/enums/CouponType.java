package e.enums;

public enum CouponType {
	RESTURANTS, ELECTRICITY, FOOD, HEALTH, SPORTS, CAMPING, TRAVELING, OTHER;

	public static CouponType typeSort(String type) {
		CouponType c = null;
		switch (type) {
		case "resturants":
			c = CouponType.RESTURANTS;
			return c;
		case "electricity":
			c = CouponType.ELECTRICITY;
			return c;
		case "food":
			c = CouponType.FOOD;
			return c;

		case "health":
			c = CouponType.HEALTH;
			return c;

		case "sports":
			c = CouponType.SPORTS;
			return c;

		case "camping":
			c = CouponType.CAMPING;
			return c;

		case "traveling":
			c = CouponType.TRAVELING;
			return c;

		default:
			c = CouponType.OTHER;
			return c;
		}

	}

	public static String typeToString(CouponType type) {
		String str;
		switch (type) {
		case RESTURANTS:
			str = "resturants";
			return str;

		case ELECTRICITY:
			str = "electricity";
			return str;

		case FOOD:
			str = "food";
			return str;

		case HEALTH:
			str = "health";
			return str;

		case SPORTS:
			str = "sports";
			return str;

		case CAMPING:
			str = "camping";
			return str;

		case TRAVELING:
			str = "traveling";
			return str;

		default:
			str = "other";
			return str;

		}
	}

}
