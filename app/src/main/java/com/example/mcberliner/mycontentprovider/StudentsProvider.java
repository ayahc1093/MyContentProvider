package com.example.mcberliner.mycontentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.sql.SQLException;

/**
 * Created by mcberliner on 6/21/2015.
 */
public class StudentsProvider extends ContentProvider {

    private static final String PROVIDER_NAME = "com.example.mcberliner.Students";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/students");
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String DEPARTMENT = "department";
    public static final int STUDENTS = 1;
    public static final int STUDENT_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "students", STUDENTS);
        uriMatcher.addURI(PROVIDER_NAME, "students/#", STUDENT_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        if(uriMatcher.match(uri) == STUDENT_ID) {
            queryBuilder.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
        }

        if(sortOrder == null || sortOrder == "") {
            sortOrder = NAME;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long insertId = db.insert(TABLE_NAME, null, values);

        if(insertId > 0) {
            Uri insertUri = ContentUris.withAppendedId(CONTENT_URI, insertId);
            getContext().getContentResolver().notifyChange(insertUri, null);
            return insertUri;
        }
        
            throw new SQLException("Failed to insert new record");

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;

        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case STUDENT_ID:
                rowsUpdated = db.update(TABLE_NAME, values, _ID + "=" + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI provide: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case STUDENT_ID:
                rowsDeleted = db.delete(TABLE_NAME, _ID + "=" + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI provided: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                return "vnd.android.cursor.dir/vnd.mcberliner.students";
            case STUDENT_ID:
                return "vnd.android.cursor.item/vnd.mcberliner.students";
            default:
                throw new IllegalArgumentException("Unsupported URI provided: " + uri);
        }
    }

    private static final String DB_NAME = "students_db";
    private static final String TABLE_NAME = "students";
    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_SQL = "create table " + TABLE_NAME + " ( " + _ID + " integer primary key autoincrement, " + NAME + " text, " + DEPARTMENT + " text);";

    private SQLiteDatabase db;

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
