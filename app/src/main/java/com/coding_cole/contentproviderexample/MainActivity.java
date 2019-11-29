package com.coding_cole.contentproviderexample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
	private ListView contactNames;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		contactNames = (ListView) findViewById(R.id.contacts_name);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "fab onClick: starts");

				showContacts();

				Log.d(TAG, "fab onClick: ends");
			}
		});
	}

	private void showContacts(){

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
			//After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) override method
		}else{
			List<String> contacts = getContacts();
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,R.layout.contact_detail,R.id.name,contacts);
			contactNames.setAdapter(arrayAdapter);
		}

	}

	private List<String> getContacts(){
		List<String> contacts = new ArrayList<>();
		String[] projections =  {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
			projections,
			null,
			null,
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

		if(cursor != null){
			while (cursor.moveToNext()){
				contacts.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
			}

			cursor.close();
		}
		return contacts;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission is granted
				showContacts();
			} else {
				Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
