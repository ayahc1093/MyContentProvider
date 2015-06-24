package com.example.mcberliner.mycontentprovider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addStudent(View v) {
        ContentValues values = new ContentValues();
        values.put(StudentsProvider.NAME, "Adam Smith");
        values.put(StudentsProvider.DEPARTMENT, "EECS");

        ContentResolver cr  = getContentResolver();

        Uri uri = cr.insert(StudentsProvider.CONTENT_URI, values);
        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_SHORT).show();
    }

    public void updateStudent(View v) {
        ContentValues values = new ContentValues();
        values.put(StudentsProvider.DEPARTMENT, "CS");

        ContentResolver cr = getContentResolver();

        int numRows = cr.update(StudentsProvider.CONTENT_URI, values, StudentsProvider._ID + "=?", new String[]{"1"});

        Toast.makeText(getBaseContext(), "# of rows affected: " + numRows, Toast.LENGTH_SHORT).show();
    }

    public void deleteStudent(View v) {
        ContentResolver cr = getContentResolver();
        int numRows = cr.delete(StudentsProvider.CONTENT_URI, StudentsProvider._ID + "=?", new String[]{"1"});

        Toast.makeText(getBaseContext(), "# of rows affected: " + numRows, Toast.LENGTH_SHORT).show();
    }

    public void showAll(View v) {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(StudentsProvider.CONTENT_URI, null, null, null, null);

        if(cursor != null && cursor.getCount() > 0) {
            do{
                String name = cursor.getString(cursor.getColumnIndex(StudentsProvider.NAME));
                String department = cursor.getString(cursor.getColumnIndex(StudentsProvider.DEPARTMENT));

                Toast.makeText(getBaseContext(), "Name: " + name + ", Dept: " + department, Toast.LENGTH_SHORT).show();
            } while (cursor.moveToNext());
        }
        cursor.close();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
