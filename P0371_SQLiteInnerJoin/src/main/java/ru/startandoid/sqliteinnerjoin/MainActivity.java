package ru.startandoid.sqliteinnerjoin;

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

  int[] positionId = {1, 2, 3, 4};
  String[] positionName = {"Директор", "Программер", "Бухгалтер", "Охранник"};
  int[] positionSalary = {15000, 13000, 10000, 8000};

  String[] peopleName = {"Иван", "Марья", "Петр", "Антон", "Даша", "Борис", "Костя", "Игорь"};
  int[] peoplePosId = {2, 3, 2, 2, 3, 1, 2, 4};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    DBHelper dbHelper = new DBHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    Cursor c;

    Log.d(LOG_TAG, "--- Table position ---");
    c = db.query("position", null, null, null, null, null, null);
    logCursor(c);
    c.close();
    Log.d(LOG_TAG, "--- ---");

    Log.d(LOG_TAG, "--- Table people ---");
    c = db.query("people", null, null, null, null, null, null);
    logCursor(c);
    c.close();
    Log.d(LOG_TAG, "--- ---");

    Log.d(LOG_TAG, "--- INNER JOIN with rawQuery ---");
    String sqlQuery = "SELECT PL.name AS Name, PS.name AS Position, salary AS Salary " +
        "FROM people AS PL INNER JOIN position AS PS ON PL.posid = PS.id WHERE salary > ?";
    c = db.rawQuery(sqlQuery, new String[]{"12000"});
    logCursor(c);
    c.close();
    Log.d(LOG_TAG, "--- ---");

    //Выводим результат объединения
    Log.d(LOG_TAG, "--- INNER JOIN with query ---");
    String table = "people AS PL INNER JOIN position AS PS ON PL.posid = PS.id";
    String[] columns = {"PL.name AS Name", "PS.name AS Position", "salary AS Salary"};
    String selection = "salary < ?";
    String[] selectionArgs = {"12000"};
    c = db.query(table, columns, selection, selectionArgs, null, null, null);
    logCursor(c);
    c.close();
    Log.d(LOG_TAG, "--- ---");

    dbHelper.close();
  }

  void logCursor(Cursor c){
    if(c != null){
      if(c.moveToFirst()){
        String str;
        do{
          str = "";
          for(String cn: c.getColumnNames()){
            str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
          }
          Log.d(LOG_TAG, str);
        }while (c.moveToNext());
      }
    }else {
      Log.d(LOG_TAG, "Cursor is null");
    }
  }

  class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context){
      super(context, "myDB", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.d(LOG_TAG, "--- onCreate database ---");

      ContentValues cv = new ContentValues();

      //Создаем таблицу должностей
      db.execSQL("CREATE TABLE position (id INTEGER PRIMARY KEY, name TEXT, salary INTEGER);");

      for(int i=0; i < positionId.length; i++){
        cv.clear();
        cv.put("id", positionId[i]);
        cv.put("name", positionName[i]);
        cv.put("salary", positionSalary[i]);
        db.insert("position", null, cv);
      }

      //Создаем табилицу людей
      db.execSQL("CREATE TABLE people (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, posid INTEGER);");

      for(int i=0; i < peopleName.length; i++){
        cv.clear();
        cv.put("name", peopleName[i]);
        cv.put("posid", peoplePosId[i]);
        db.insert("people", null, cv);
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
  }
}
