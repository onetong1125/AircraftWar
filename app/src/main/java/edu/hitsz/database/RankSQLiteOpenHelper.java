package edu.hitsz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class RankSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private String tableName;
    private Context context;

    public RankSQLiteOpenHelper (@Nullable Context context, String tableName){
        super(context, tableName, null, VERSION);
        this.context = context;
        this.tableName = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_sql =
                "CREATE TABLE IF NOT EXISTS "+ getTableName() + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "name VARCHAR NOT NULL,"
                + "score INTEGER NOT NULL,"
                + "time VARCHAR NOT NULL"
                + ");";
        db.execSQL(create_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getTableName() {
        return tableName;
    }
}
