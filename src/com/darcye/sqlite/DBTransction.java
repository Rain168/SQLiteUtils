package com.darcye.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * a transction support class
 * 
 * @author Darcy
 */
public class DBTransction {

	private DBTransctionInterface mTransctionInterface;
	private SQLiteDatabase mSQLiteDatabase;
	
	public DBTransction(DbSqlite db, DBTransctionInterface mTransctionInterface) {
		this.mSQLiteDatabase = db.getSQLiteDatabase();
		this.mTransctionInterface = mTransctionInterface;
	}
	
	/**
	 * executes sqls in a transction
	 */
	public void process(){
		if(mTransctionInterface!=null){
			mSQLiteDatabase.beginTransaction();
			try{
				mTransctionInterface.onTransction();
				mSQLiteDatabase.setTransactionSuccessful();
			}finally{
				mSQLiteDatabase.endTransaction();
			}
		}
	}
	
	public interface DBTransctionInterface{
   	 void onTransction();
   }
}
