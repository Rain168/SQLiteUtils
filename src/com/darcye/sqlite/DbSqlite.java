package com.darcye.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * a simple class to encapsulate the db operations, remember :
 * <strong >don't </strong>forget to {@link #closeDB()} when you don't need to connect to database.
 * @author Darcy
 * 
 */
public class DbSqlite {
	
	private SQLiteDatabase mSQLiteDatabase;

	private Context mContext;

	private String dbName;

	/**
	 * constructor would create or open the database
	 * @param context
	 * @param dbName
	 */
	public DbSqlite(Context context, String dbName) {
		this.mContext = context;
		this.dbName = dbName;
		mSQLiteDatabase = mContext.openOrCreateDatabase(this.dbName, Context.MODE_PRIVATE,null);
	}

	public SQLiteDatabase getSQLiteDatabase(){
		return mSQLiteDatabase;
	}
	
	/**
	 * update a record
	 * 
	 * @param table the table to update in
	 * @param values a map from column names to new column values. null is a valid value that will be translated to NULL.
	 * @param whereClause the optional WHERE clause to apply when updating. Passing null will update all rows.
	 * @param whereArgs You may include ?s in the where clause, which will be replaced by the values from whereArgs. The values will be bound as Strings.
	 * @return the number of rows affected , or -1 if an error occurred
	 */
	public int update(String table, ContentValues values,String whereClause, String[] whereArgs) {
		try {
			openDB();
			return mSQLiteDatabase.update(table, values, whereClause, whereArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * insert a record
	 * 
	 * @param table
	 * @param values
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insert(String table, ContentValues values) {
		try {
			openDB();
			return mSQLiteDatabase.insertOrThrow(table, null, values);
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * 
	 * insert or replace a record by if its value of primary key has exsits 
	 * 
	 * @param table
	 * @param values
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insertOrReplace(String table, ContentValues values){
		try {
			openDB();
			return mSQLiteDatabase.replaceOrThrow(table, null, values);
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * insert mutil records at one time
	 * @param table
	 * @param listVal
	 * @return
	 */
	public boolean batchInsert(final String table,final List<ContentValues> listVal){
		try {
			openDB();
			new DBTransction(this , new DBTransction.DBTransctionInterface(){
				@Override
				public void onTransction() {
					for (ContentValues contentValues : listVal) {
						mSQLiteDatabase.insertOrThrow(table, null, contentValues);
					}
				}
			}).process();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * delele by the condition
	 * 
	 * @param table table the table to delete from
	 * @param whereClause whereClause the optional WHERE clause to apply when deleting. Passing null will delete all rows.
	 * @param whereArgs whereArgs You may include ?s in the where clause, which will be replaced by the values from whereArgs. The values will be bound as Strings.
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		try {
			openDB();
			return mSQLiteDatabase.delete(table, whereClause, whereArgs);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	/**
	 * a more flexible query by condition
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param selectionArgs
	 * @return if exceptions 
	 */
	public List<QueryResult> query(String table, String[] columns,
			String selection, String groupBy, String having, String orderBy,
			String... selectionArgs) {
		Cursor cursor = null;
		try {
			openDB();
			cursor = mSQLiteDatabase.query(table, columns, selection,
					selectionArgs, groupBy, having, orderBy);
			List<QueryResult>  resultList = new ArrayList<QueryResult>();
			parseCursorToResult(cursor, resultList);
			return resultList;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if(cursor!=null)
				cursor.close();
		}
	}

	/**
	 * a simple query by condition
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public List<QueryResult> query(String table, String[] columns,
			String selection, String... selectionArgs) {
		return query(table, columns, selection, null, null, null, selectionArgs);
	}

	/**
	 * Execute a single SQL statement that is NOT a SELECT/INSERT/UPDATE/DELETE. 
	 * 
	 * @param sql
	 * @param bindArgsonly
	 *            byte[], String, Long and Double are supported in bindArgs.
	 * @return
	 */
	public boolean execSQL(String sql, Object... bindArgs) {
		try {
			openDB();
			mSQLiteDatabase.execSQL(sql, bindArgs);
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * execute raw query sql
	 * 
	 * @param sql
	 * @param bindArgs
	 * @return 
	 */
	public List<QueryResult> execQuerySQL(String sql, String... bindArgs){
		Cursor cursor = null;
		try{
			List<QueryResult>  resultList = new ArrayList<QueryResult>();
			openDB();
			cursor = mSQLiteDatabase.rawQuery(sql, bindArgs);
			parseCursorToResult(cursor, resultList);
			return resultList;
		}catch(SQLException ex) {
			ex.printStackTrace();
			return null;
		}finally{
			if(cursor!=null)
				cursor.close();
		}
		
	}
	
	/**
	 * open database
	 */
	public void openDB() {
		if (mSQLiteDatabase.isOpen() == false)
			mSQLiteDatabase = mContext.openOrCreateDatabase(this.dbName,
					Context.MODE_PRIVATE, null);
	}

	/**
	 * close database
	 */
	public void closeDB() {
		if (mSQLiteDatabase.isOpen()) {
			mSQLiteDatabase.close();
		}
	}

	public Context getContext() {
		return mContext;
	}

	public String getDbName() {
		return dbName;
	}

	/**
	 * set data in cursor to QueryResult List
	 * @param cursor
	 * @param resultList the data will set in it
	 */
	private void parseCursorToResult(Cursor cursor,List<QueryResult> resultList){
		int columnCount;
		int columnType;
		Object columnVal = null;
		while (cursor.moveToNext()) {
			columnCount = cursor.getColumnCount();
			QueryResult result = new QueryResult();
			for (int index = 0; index < columnCount; ++index) {
				columnType = cursor.getType(index);
				switch (columnType) {
				case Cursor.FIELD_TYPE_BLOB:
					columnVal = cursor.getBlob(index);
					break;
				case Cursor.FIELD_TYPE_FLOAT:
					columnVal = cursor.getFloat(index);
					break;
				case Cursor.FIELD_TYPE_INTEGER:
					columnVal = cursor.getInt(index);
					break;
				case Cursor.FIELD_TYPE_STRING:
					columnVal = cursor.getString(index);
					break;
				default:
					columnVal = cursor.getString(index);
					break;
				}
				result.setProperty(cursor.getColumnName(index), columnVal);
			}
			resultList.add(result);
		}
	}
}
