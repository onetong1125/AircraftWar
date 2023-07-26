package edu.hitsz.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ScoreDAOImpl {
    private static final String TAG = "SQLite";
    public void doAdd(RankSQLiteOpenHelper helper, String name, int score) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("score", score);
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        cv.put("time", dateTime.format(formatter));
        db.insert(helper.getTableName(), "", cv);
        db.close();
    }

    public ArrayList<Score> getRankList(RankSQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Score> rankList = new ArrayList<>();
        String sql = String.format("select id, name, score, time from %s order by score desc;", helper.getTableName());
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            for (;; cursor.moveToNext()) {
                Score sco = new Score(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3));
                rankList.add(sco);
                if (cursor.isLast()) {
                    break;
                }
            }
        }
        cursor.close();
        db.close();
        return rankList;
    }

    public void doDelete(RankSQLiteOpenHelper helper, String id) {
        SQLiteDatabase db = helper.getWritableDatabase();

        int i = db.delete(helper.getTableName(), "id = ?", new String[]{id});
        if(i==0){
            Log.d(TAG, "delete fail");
        } else {
            Log.d(TAG, "deleted "+i+" rows");
        }
        db.close();

    }
}
