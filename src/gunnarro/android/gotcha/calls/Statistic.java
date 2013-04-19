package gunnarro.android.gotcha.calls;

import java.util.Date;

public class Statistic
// implements Comparable<Statistic>
{

	private Date callDate;
	private PhoneNumber phoneNumber;
	private int numberOfOutgoing;
	private int numberOfIncoming;
	private int numberOfMissed;
	private int durationIncoming;
	private int durationOutgoing;

	public Statistic(Date callDate, PhoneNumber phoneNumber) {
		this.callDate = callDate;
		this.phoneNumber = phoneNumber;
	}

	public Statistic(Date callDate, String phoneNumber) {
		this.callDate = callDate;
		this.phoneNumber = new PhoneNumber(phoneNumber);
	}

//	public Statistic(Date callDate, String phoneNumber, int numberOfOutgoing, int numberOfIncoming, int numberOfMissed, int durationIncoming,
//			int durationOutgoing) {
//		this.callDate = callDate;
//		this.phoneNumber = new PhoneNumber(phoneNumber);
//		this.numberOfOutgoing = numberOfOutgoing;
//		this.numberOfIncoming = numberOfIncoming;
//		this.numberOfMissed = numberOfMissed;
//		this.durationIncoming = durationIncoming;
//		this.durationOutgoing = durationOutgoing;
//	}

	public Date getCallDate() {
		return callDate;
	}

	public PhoneNumber getPhoneNumber() {
		return phoneNumber;
	}

	public int getNumberOfOutgoing() {
		return numberOfOutgoing;
	}

	public int getNumberOfIncoming() {
		return numberOfIncoming;
	}

	public int getNumberOfMissed() {
		return numberOfMissed;
	}

	public void increaseIncomingCalls() {
		this.numberOfIncoming++;
	}

	public void increaseOutgoingCalls() {
		this.numberOfOutgoing++;
	}

	public void increaseMissedCalls() {
		this.numberOfMissed++;
	}

	public void updateMissedCalls(int calls) {
		this.numberOfMissed += calls;
	}

	public void updateIncomingCalls(int calls) {
		this.numberOfIncoming += calls;
	}

	public void updateOutgoingCalls(int calls) {
		this.numberOfOutgoing += calls;
	}

	public void updateIncomingDuration(int duration) {
		durationIncoming += duration;
	}

	public void updateOutgoingDuration(int duration) {
		durationOutgoing += duration;
	}

	public int getTotalNumberOfCalls() {
		return numberOfIncoming + numberOfOutgoing + numberOfMissed;
	}

	public int getDurationIncoming() {
		return durationIncoming;
	}

	public int getDurationOutgoing() {
		return durationOutgoing;
	}

	@Override
	public String toString() {
		return phoneNumber + " " + numberOfIncoming + ", " + numberOfOutgoing + ", " + numberOfMissed;
	}

	// @Override
	// public int compareTo(Statistic another) {
	// return sortByNumberOfIncoming(another);
	// }

	public int sortByNumberOfIncoming(Statistic another) {
		return compare(numberOfIncoming, another.getNumberOfIncoming());
	}

	public int sortByNumberOfOutgoing(Statistic another) {
		return compare(numberOfOutgoing, another.getNumberOfOutgoing());
	}

	public int sortByNumberOfMissed(Statistic another) {
		return compare(numberOfMissed, another.getNumberOfMissed());
	}

	public int sortByDurationOutgoing(Statistic another) {
		return compare(durationOutgoing, another.getDurationOutgoing());
	}

	public int sortByDurationIncoming(Statistic another) {
		return compare(durationIncoming, another.getDurationIncoming());
	}

	public int compare(int value1, int value2) {
		if (value1 > value2) {
			return -1;
		} else if (value1 < value2) {
			return 1;
		} else {
			return 0;
		}
	}
}
