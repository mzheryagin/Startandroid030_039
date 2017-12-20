package ru.startandoid.sqliteonupgradedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

  final String LOG_TAG = "myLogs";

  final String DB_NAME = "staff";
  final int DB_VERSION = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    DBHelper dbHelper = new DBHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    Log.d(LOG_TAG, "--- Staff db v." + db.getVersion() + " ---");
    writeStaff(db);
    dbHelper.close();
  }

  private void writeStaff(SQLiteDatabase db){
    Cursor c = db.rawQuery("SELECT * FROM people", null);
    logCursor(c, "people");
    c.close();

    c = db.rawQuery("SELECT * FROM position", null);
    logCursor(c, "position");
    c.close();

    String sqlQuery = "SELECT PL.name AS Name, PS.name AS Position, salary AS Salary " +
        "FROM people AS PL INNER JOIN position AS PS ON PL.posid = PS.id";
    c = db.rawQuery(sqlQuery, null);
    logCursor(c, "inner join");
    c.close();
  }

  void logCursor(Cursor c, String title){
    if(c != null){
      if(c.moveToFirst()){
        Log.d(LOG_TAG, title + ". rows = " + c.getCount());
        StringBuilder sb = new StringBuilder();
        do{
          sb.setLength(0);
          for(String cn : c.getColumnNames()){
            sb.append(cn + " = " + c.getString(c.getColumnIndex(cn))+ "; ");
          }
          Log.d(LOG_TAG, sb.toString());
        }while (c.moveToNext());
      }
    } else {
      Log.d(LOG_TAG, title + ". Cursor is null");
    }
  }


  class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
      super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.d(LOG_TAG, "--- onCreate database ---");

      String[] people_name = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис", "Костя", "Игорь" };
      String[] people_positions = { "Программер", "Бухгалтер", "Программер", "Программер",
          "Бухгалтер", "Директор", "Программер", "Охранник" };

      ContentValues cv = new ContentValues();

      db.execSQL("CREATE TABLE people (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, position TEXT);");

      for(int i = 0; i < people_name.length; i++){
        cv.clear();
        cv.put("name", people_name[i]);
        cv.put("position", people_positions[i]);
        db.insert("people", null, cv);
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
  }
}
