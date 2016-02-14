package nl.hu.zrb;

import nl.hu.zrb.data.MyDBHelper;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ToonLocaties extends ListActivity implements OnItemClickListener {
   
	MyDBHelper mdbh;
	Cursor theCursor;
	SimpleCursorAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
		mdbh = MyDBHelper.getInstance(this);
	
		ListView lv = getListView();
		lv.setOnItemClickListener(this);
		registerForContextMenu(lv);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View v  = inflater.inflate(R.layout.buttonpaneltoonlocaties, null);
		lv.addFooterView(v);
		
		theCursor = mdbh.getLocaties();
		String[] from = {"naam"};
		int[] to = { R.id.textView1};
		adapter = new SimpleCursorAdapter(this, R.layout.listitemlocatie, theCursor, from , to );
		this.setListAdapter(adapter);	
		
		Button b1 = (Button) findViewById(R.id.addbutton);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(ToonLocaties.this, NieuweLocatie.class);
				startActivity(i);							
			}		
		});		       
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		open(arg2);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.v("ToonLocaties", "onresume");
		theCursor = mdbh.getLocaties();
		adapter.changeCursor(theCursor);
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		theCursor.close();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo){
		  super.onCreateContextMenu(menu, v, menuInfo);  
		  MenuInflater inflater = getMenuInflater();  
		  inflater.inflate(R.menu.locationmenu, menu);		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.itemopen :
			open(info.position);
			return true; 
		case R.id.itemremove:
			onPause();
			mdbh.verwijderLocatie(info.id);
			onResume();
			return true;
		default: 
			return super.onContextItemSelected(item);
		}		
	}
	
	public void open(int pos){
		if (theCursor.moveToPosition(pos)){
			String naam = theCursor.getString(theCursor.getColumnIndex("naam"));
			double lon = theCursor.getDouble(theCursor.getColumnIndex("longitude"));
			double lat = theCursor.getDouble(theCursor.getColumnIndex("latitude"));
			Intent intent = new Intent(this, Kompas.class);
			intent.putExtra("naam", naam);
			intent.putExtra("longitude", lon);
			intent.putExtra("latitude", lat);
			startActivity(intent);	
		}
		else{
			Log.w("ToonLocaties", "positie " + pos + " niet gevonden.");
		}			
	}
     
}