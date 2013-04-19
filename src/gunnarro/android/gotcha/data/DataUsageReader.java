package gunnarro.android.gotcha.data;

import android.content.Context;
import android.net.TrafficStats;

public class DataUsageReader {

	private Context context;
	
	public DataUsageReader(Context context) {
		this.context = context;
	}
	
	public DataUsageReader() {
		
		
		long mobileRxBytes = TrafficStats.getMobileRxBytes();
		long mobileTxBytes = TrafficStats.getMobileTxBytes();
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		
		if (mobileRxBytes == TrafficStats.UNSUPPORTED || mobileTxBytes == TrafficStats.UNSUPPORTED) {
			// Alert 
		}
		
		if (totalRxBytes == TrafficStats.UNSUPPORTED || totalTxBytes == TrafficStats.UNSUPPORTED) {
			// Alert 
		}
	}
}
