package nl.hu.zrb;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

public class Kompas extends Activity implements LocationListener, SensorEventListener  {
	
	LocationManager lmanager;
	SensorManager smanager;
	PowerManager.WakeLock wl;
	
	String locationprovider = "gps";
	//private Sensor mAccelerometer;
	//private Sensor mMagnetometer;
	private Sensor orientatiesensor; 
	
	Location targetLocation;
	float targetRichting;
	float deviceRichting;
	
	KompasView kv;
	TextView tv;
	TextView rv;
	
	NumberFormat nf ;
		
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.kompas);
        kv = (KompasView) findViewById(R.id.viewkompas);
        tv = (TextView)findViewById(R.id.textViewDistance);
        rv = (TextView)findViewById(R.id.textViewDirection);
        
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Kompas");
        
        lmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationprovider = lmanager.getBestProvider(criteria, true);
        Log.v("Kompas", locationprovider);
        
        smanager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        orientatiesensor = smanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //mAccelerometer = smanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mMagnetometer = smanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        Intent intent = getIntent();     
        String naam = intent.getStringExtra("naam");        
        TextView tv = (TextView)findViewById(R.id.textViewLocation);
        tv.setText(naam);        

        double lon = intent.getDoubleExtra("longitude", 0.0);
        double lat = intent.getDoubleExtra("latitude", 90.0);
		targetLocation = new Location(locationprovider);
		targetLocation.setLatitude(lat);
		targetLocation.setLongitude(lon);  
		
		Location hier = lmanager.getLastKnownLocation(locationprovider);
		if (hier != null) onLocationChanged(hier);
	 }
	 
    public void onResume(){
    	super.onResume();
    	wl.acquire();
    	lmanager.requestLocationUpdates(locationprovider, 0,0, this);
    	//smanager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL); 
    	//smanager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    	smanager.registerListener(this, orientatiesensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    public void onPause(){
    	lmanager.removeUpdates(this);
    	smanager.unregisterListener(this);
    	wl.release();
    	super.onPause();
    }

	@Override
	public void onLocationChanged(Location arg0) {
		Location currentLocation = arg0;
		targetRichting = currentLocation.bearingTo(targetLocation);	
		Log.d("Kompas", "onlocationchanged");
		Log.d("Kompas", "targetRichting: " + targetRichting);
		kv.setRichting(targetRichting - deviceRichting);

		float afstand = currentLocation.distanceTo(targetLocation);
		if(afstand < 3000)
			tv.setText(" " + nf.format(afstand) + " m");
		else {
			afstand /= 1000f;
			tv.setText(" " + afstand + " km");			
		}
		
		
		rv.setText("koers  " + nf.format(targetRichting) + "°");
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.v("Kompas", provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		int type = event.sensor.getType();
		/* werkt niet op Samsung Galaxy Ace
		float[] mags = {0f,0f,0f};		
		if(type == Sensor.TYPE_MAGNETIC_FIELD){
			for(int i = 0; i < 3; i++){mags[i] = event.values[i];}
		}
		float[] accels = {0f,0f,0f};
		if(type == Sensor.TYPE_ACCELEROMETER){
			for(int i = 0; i < 3; i++){accels[i] = event.values[i];}
		}
		float[] R = new float[9];
		float[] I = new float[9];
		boolean b = SensorManager.getRotationMatrix(R, I, accels, mags);
		Log.d("Kompas", "getRotationMatrixSucces: " + b); //false
		float[] attitude = {0f, 0f, 0f};
		SensorManager.getOrientation(R, attitude);
		deviceRichting = attitude[0];
		kv.setRichting(targetRichting - deviceRichting);
		*/
		if (type == Sensor.TYPE_ORIENTATION){
			float[] x = event.values;
			deviceRichting = x[0];
			Log.d("Kompas", "onsensorchanged");
			Log.d("Kompas", "deviceRichting: " + deviceRichting);
			kv.setRichting(targetRichting - deviceRichting);	
		}	
			
	}	 

}
