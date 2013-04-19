package gunnarro.android.gotcha;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MyLocationManager {

	private static final int TWO_MINUTES = 1000 * 60 * 2;


	/**
	 * google maps lookup:
	 * http://maps.googleapis.com/maps/api/geocode/json?latlng
	 * =40.714224,-73.961452&sensor=true_or_false
	 * 
	 * Doc: http://code.google.com/apis/maps/documentation/geocoding/
	 * 
	 * @return
	 */
	public String getLocation(Context context) {
		Location gpsLastKnownLocation = null;
		Location networkLastKnownLocation = null;
		Location lastKnownLocation = null;
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager == null) {
			return "Location manager not found!";
		}
		// Request for updates from the GPS
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, new MyLocationListener());
		// Define the criteria how to select the location provider -> use
		// default
		// Criteria criteria = new Criteria();
		// String provider = locationManager.getBestProvider(criteria, false);
		// lastKnownLocation = locationManager.getLastKnownLocation(provider);
		//
		// if (lastKnownLocation == null) {
		// return "Last kown Location is not available from " + provider;
		// }
		try {
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				gpsLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				networkLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Last kown Location is not available from GPS or Network!";
		}
		// Pick the most accurate location information
		if (gpsLastKnownLocation != null && networkLastKnownLocation != null) {
			if (isBetterLocation(gpsLastKnownLocation, networkLastKnownLocation)) {
				lastKnownLocation = gpsLastKnownLocation;
			} else {
				lastKnownLocation = networkLastKnownLocation;
			}
		} else if (gpsLastKnownLocation != null) {
			lastKnownLocation = gpsLastKnownLocation;
		} else if (networkLastKnownLocation != null) {
			lastKnownLocation = networkLastKnownLocation;
		}
		if (lastKnownLocation == null) {
			return "Last kown Location is not available from GPS or Network!";
		}
		// if (lastKnownLocation.hasAccuracy()) {
		// lastKnownLocation =
		// locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		// }
		return getAddress(lastKnownLocation, context);
	}

	private String getAddress(Location location, Context context) {
		if (location == null) {
			Log.e("getAddress", "location is null");
			return null;
		}
		// GeoPoint p = null;
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		// List<Address> addresses = geoCoder.getFromLocation(p.getLatitudeE6()
		// / 1E6, p.getLongitudeE6() / 1E6, 1);
		try {
			List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			StringBuffer address = new StringBuffer();
			address.append("Used ").append(location.getProvider()).append(" provider:\n");
			address.append("Last known address:\n");
			SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy hh:ss:mm");
			address.append(sd.format(location.getTime())).append("\n");
			if (addresses.size() > 0) {
				for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
					address.append(addresses.get(0).getAddressLine(i)).append("\n");
				}
			}
			return address.toString();
		} catch (IOException e) {
			Log.e("getAddress", "Error getting address:" + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	private boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether two providers are the same
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	
	private void determineAddress(Location location) {
		
	}
	
	/**
	 * 
	 * @author gunnarro
	 */
	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			Log.i("",
					String.format(location.getProvider() + " new Location \n Longitude: %1$s \n Latitude: %2$s", location.getLongitude(),
							location.getLatitude()));
			// A new location updates has arrived
			 determineAddress(location);
			// Remove the listener for location updates
			// locationManager.removeUpdates(this);
		}
		@Override
		public void onStatusChanged(String s, int i, Bundle b) {
			Log.i("", s + " provider status changed");
		}
		@Override
		public void onProviderDisabled(String s) {
			Log.i("", "Provider disabled by the user." + s + " turned off.");
		}
		@Override
		public void onProviderEnabled(String s) {
			Log.i("", "Provider enabled by the user." + s + " GPS turned on.");
		}

	}

}
