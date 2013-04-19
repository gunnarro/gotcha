package gunnarro.android.gotcha.calls;

public class PhoneNumber {
	private String countryCode;
	private String localNumber;

	public PhoneNumber(String phoneNumber) {
		if (phoneNumber.startsWith("+")) {
			this.countryCode = phoneNumber.substring(0, 3).trim();
			this.localNumber = phoneNumber.substring(3).trim();
		} else if (phoneNumber.startsWith("00")) {
			this.countryCode = phoneNumber.substring(0, 4).trim();
			this.localNumber = phoneNumber.substring(4).trim();
		} else {
			this.countryCode = null;
			this.localNumber = phoneNumber.trim();
		}
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getLocalNumber() {
		return localNumber;
	}

	@Override
	public String toString() {
		return countryCode + localNumber;
	}

	public boolean hasCountryCode() {
		return countryCode != null;
	}

	public String getKey() {
		return localNumber;
	}
}
