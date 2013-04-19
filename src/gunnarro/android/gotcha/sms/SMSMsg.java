package gunnarro.android.gotcha.sms;

public class SMSMsg {

	private enum ActionEnum {
		TRACE, ALARM, WAKEUP, FORWARD;
	}

	private String toMobilePhoneNumber;
	private String msg;

	public SMSMsg(String toMobilePhoneNumber, String msg) {
		this.toMobilePhoneNumber = toMobilePhoneNumber;
		this.msg = msg;
	}

	public String getToMobilePhoneNumber() {
		return toMobilePhoneNumber;
	}

	public String getMsg() {
		return msg;
	}

	public boolean isTraceSMS() {
		if (msg != null) {
			return msg.trim().equalsIgnoreCase(ActionEnum.TRACE.name());
		}
		return false;
	}

	public boolean isForwardSMS() {
		if (msg != null) {
			return msg.trim().equalsIgnoreCase(ActionEnum.FORWARD.name());
		}
		return false;
	}

	@Override
	public String toString() {
		return toMobilePhoneNumber + ": " + msg;
	}

}
