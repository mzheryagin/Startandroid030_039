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
      Log.d(LOG_TAG, " --- onCreate database --- ");

      String[] people_name = { "Иван", "Марья", "Петр", "Антон", "Даша",
          "Борис", "Костя", "Игорь" };
      int[] people_posid = { 2, 3, 2, 2, 3, 1, 2, 4 };

      // данные для таблицы должностей
      int[] position_id = { 1, 2, 3, 4 };
      String[] position_name = { "Директор", "Программер", "Бухгалтер",
          "Охранник" };
      int[] position_salary = { 15000, 13000, 10000, 8000 };

      ContentValues cv = new ContentValues();

      // создаем таблицу должностей
      db.execSQL("create table position (" + "id integer primary key,"
          + "name text, salary integer" + ");");

      // заполняем ее
      for (int i = 0; i < position_id.length; i++) {
        cv.clear();
        cv.put("id", position_id[i]);
        cv.put("name", position_name[i]);
        cv.put("salary", position_salary[i]);
        db.insert("position", null, cv);
      }

      // создаем таблицу людей
      db.execSQL("create table people ("
          + "id integer primary key autoincrement,"
          + "name text, posid integer);");

      // заполняем ее
      for (int i = 0; i < people_name.length; i++) {
        cv.clear();
        cv.put("name", people_name[i]);
        cv.put("posid", people_posid[i]);
        db.insert("people", null, cv);
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.d(LOG_TAG, "--- onUpgrade database from " + oldVersion + " to " + newVersion + " ---");

      if(oldVersion == 1 && newVersion == 2){
        ContentValues cv = new ContentValues();

        //Данные для таблицы должностей
        int[] positionId = { 1, 2, 3, 4 };
        String[] positionName = { "Директор", "Программер", "Бухгалтер", "Охранник" };
        int[] positionSalary = { 15000, 13000, 10000, 8000 };

        db.beginTransaction();
        try{
          //создаем таблицу должностей
          db.execSQL("CREATE TABLE position (id INTEGER PRIMARY KEY, name TEXT, salary INTEGER);");

          for(int i = 0; i < positionId.length; i++){
            cv.clear();
            cv.put("id", positionId[i]);
            cv.put("name", positionName[i]);
            cv.put("salary", positionSalary[i]);
            db.insert("position", null, cv);
          }

          db.execSQL("ALTER TABLE people ADD COLUMN posid INTEGER;");

          for(int i = 0; i < positionId.length; i++){
            cv.clear();
            cv.put("posid", positionId[i]);
            db.update("people", cv, "position = ?", new String[]{positionName[i]});
          }

          db.execSQL("CREATE TEMPORARY TABLE people_tmp (id INTEGER, name TEXT, position TEXT, posid INTEGER);");

          db.execSQL("INSERT INTO people_tmp SELECT id, name, position, posid FROM people;");
          db.execSQL("DROP TABLE people;");

          db.execSQL("CREATE TABLE people (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, posid INTEGER);");

          db.execSQL("INSERT INTO people SELECT id, name, posid FROM people_tmp;");
          db.execSQL("DROP TABLE people_tmp;");

          db.setTransactionSuccessful();
        }finally {
          db.endTransaction();
        }

      }
    }
  }
}
