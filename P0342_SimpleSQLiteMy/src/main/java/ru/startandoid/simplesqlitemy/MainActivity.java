package ru.startandoid.simplesqlitemy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

  final String LOG_TAG = "myLogs";

  Button btnAdd, btnRead, btnClear, btnUpd, btnDel;
  EditText etName, etEmail, etId;

  DBHelper dbHelper;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    btnAdd = (Button)findViewById(R.id.btnAdd);
    btnRead = (Button)findViewById(R.id.btnRead);
    btnClear = (Button)findViewById(R.id.btnClear);
    btnUpd = (Button)findViewById(R.id.btnUpd);
    btnDel = (Button)findViewById(R.id.btnDel);
    btnAdd.setOnClickListener(this);
    btnRead.setOnClickListener(this);
    btnClear.setOnClickListener(this);
    btnUpd.setOnClickListener(this);
    btnDel.setOnClickListener(this);

    etName = (EditText)findViewById(R.id.etName);
    etEmail = (EditText)findViewById(R.id.etEmail);
    etId = (EditText)findViewById(R.id.etId);

    dbHelper = new DBHelper(this);

  }

  @Override
  public void onClick(View view) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues cv = new ContentValues();

    String id = etId.getText().toString();
    String name = etName.getText().toString();
    String email = etEmail.getText().toString();

    switch (view.getId()){
      case R.id.btnAdd:
        if(etName.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty()){
          Log.d(LOG_TAG, "Name or Email field is empty");
          break;
        }

        cv.put("name", name);
        cv.put("email", email);
        Long ID = db.insert("myTable", null, cv);
        Log.d(LOG_TAG, "Row added, ID = " + ID);
        break;
      case R.id.btnRead:
        Cursor c = db.query("myTable", null,null,null,null,null,null);

        if(c.moveToFirst()){
          int colId = c.getColumnIndex("id");
          int colName = c.getColumnIndex("name");
          int colEmail = c.getColumnIndex("email");

          do{
            Log.d(LOG_TAG, "ID = " + c.getInt(colId) + ", name = " +
            c.getString(colName) + ", email = " + c.getString(colEmail));
          }while (c.moveToNext());
        }else{
          Log.d(LOG_TAG, "Row count = 0");
          c.close();
        }
        break;
      case R.id.btnUpd:
        if(id.equalsIgnoreCase("")){
          break;
        }
        cv.put("name", name);
        cv.put("email", email);
        db.update("myTable", cv, "id = ?", new String[]{id});
        Log.d(LOG_TAG, "id = " + id + " updated");
        break;
      case R.id.btnDel:
        if(id.equalsIgnoreCase("")){
          break;
        }
        db.delete("myTable", "id = " + id, null);
        Log.d(LOG_TAG, "id = " + id + " deleted");
        break;
      case R.id.btnClear:
        int delCount = db.delete("myTable", null, null);
        Log.d(LOG_TAG, "Deleted " + delCount + " items");
        break;
    }
    dbHelper.close();
  }

  class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
      super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
      sqLiteDatabase.execSQL("create table mytable (id integer primary key autoincrement, " +
          "name text, email text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
  }//end DBHelper

}
