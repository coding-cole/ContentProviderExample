package com.coding_cole.contentproviderexample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	private ListView contactNames;
	private static final int REQUEST_CODE_READ_CONTACTS = 1;
//	private static boolean READ_CONTACTS_GRANTED = false;
	FloatingActionButton fab = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		contactNames = (ListView) findViewById(R.id.contacts_name);
		int hasReadContactsPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS);

		Log.d(TAG, "onCreate: checkSelfpermission = " + hasReadContactsPermission);

//		if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
//			Log.d(TAG, "onCreate: permmision granted");
////			READ_CONTACTS_GRANTED= true;
//		} else {
//			Log.d(TAG, "onCreate: requesting permission");
//			ActivityCompat.requestPermissions(this, new String[] {READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
//		}
		if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "onCreate: requesting permission");
			ActivityCompat.requestPermissions(this, new String[] {READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
		}

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "fab onClick: starts");
//				if (READ_CONTACTS_GRANTED) {
				if (ContextCompat.checkSelfPermission(MainActivity.this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
					String[] projections =  {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
					ContentResolver contentResolver = getContentResolver();
					Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
						projections,
						null,
						null,
						ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

					if(cursor != null){
						List<String> contacts = new ArrayList<>();
						while (cursor.moveToNext()){
							contacts.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
						}
						cursor.close();
						ArrayAdapter<String> adapter = new ArrayAdapter<String>
							(MainActivity.this, R.layout.contact_detail, R.id.name, contacts);
						contactNames.setAdapter(adapter);
					}
				} else {
					Snackbar.make(view, "Dummy, you have to grant access before that works. Urgghh", Snackbar.LENGTH_INDEFINITE)
						.setAction("Grant Access", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Log.d(TAG, "Snackbar onClick: starts");
								if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, READ_CONTACTS)) {
									Log.d(TAG, "Snackbar onClick: calling requestPermissions");
									ActivityCompat.requestPermissions(MainActivity.this,
										new String[] {READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
								} else {
									// This user has permanently denied the permission, so take them to the settings
									Log.d(TAG, "Snackbar onClick: launching settings");
									Intent intent = new Intent();
									intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
									Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
									Log.d(TAG, "Snackbar onClick: Intent Uri is " + uri.toString());
									intent.setData(uri);
									MainActivity.this.startActivity(intent);
								}
								Log.d(TAG, "Snackbar onClick: ends");
							}
						}).show();
				}

				Log.d(TAG, "fab onClick: ends");
			}
		});

		Log.d(TAG, "onCreate: ends");
	}

//	@Override
//	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//		switch (requestCode) {
//			case REQUEST_CODE_READ_CONTACTS: {
//				// if request is cancelled the rest arrays are empty.
//				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//					// permission granted, do the task needed to be done
//					Log.d(TAG, "onRequestPermissionsResult: permission granted");
////					READ_CONTACTS_GRANTED = true;
//				} else {
//					// permission denied, disable the functionality
//					// that depends on this permission
//					Log.d(TAG, "onRequestPermissionsResult: permission refused");
//				}
////				fab.setEnabled(READ_CONTACTS_GRANTED );
//			}
//		}
//
//		Log.d(TAG, "onRequestPermissionsResult: ends");
//	}

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
