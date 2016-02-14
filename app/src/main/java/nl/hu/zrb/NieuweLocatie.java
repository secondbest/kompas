package nl.hu.zrb;

import nl.hu.zrb.data.MyDBHelper;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NieuweLocatie extends Activity implements LocationListener {
	
	MyDBHelper mdbh;
	EditText naamVeld;
	LocationManager manager;
	String locationprovider;
	Location location;
		
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nieuwelocatie);
        
        mdbh = MyDBHelper.getInstance(this);
        
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationprovider = manager.getBestProvider(criteria, true);
        
        
        naamVeld = (EditText) findViewById(R.id.editText1);
                
        Button save = (Button)findViewById(R.id.button1);
        save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editable naam = naamVeld.getText();
				String nm = naam.toString();
				location = manager.getLastKnownLocation(locationprovider);
				if(location == null){
					Toast.makeText(NieuweLocatie.this, "locatie niet gevonden", Toast.LENGTH_SHORT);
				}
				Log.v("nieuwe locatie-OK", "location: " + location);
				mdbh.insertLocatie(nm, location.getLongitude(), location.getLatitude());
				finish();
			}       	
        });
        
        Button cancel = (Button) findViewById(R.id.button2);
        cancel.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();				
			}   	
        });     
    }
    
    public void onResume(){
    	super.onResume();
    	manager.requestLocationUpdates(locationprovider, 0,0, this);   	
    }
    
    public void onPause(){
    	manager.removeUpdates(this);
    	super.onPause();
    }

	@Override
	public void onLocationChanged(Location arg0) {
		location = arg0;		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}