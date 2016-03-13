package com.darcye.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;


/**
 * an implement of IBaseDao
 * @author Darcy
 * @param <T>
 * 
 */
class GenericDao<T> implements IBaseDao<T> {

	private Class<?> modelClazz;
	
	private String mTableName;

	private DbSqlite mDb;
	
	public GenericDao(DbSqlite db, Class<?> modelClazz) {
		this.mDb = db;
		this.modelClazz = modelClazz;
		mTableName = SqlHelper.getTableName(modelClazz);
	}
	
	@Override
	public void createTable() {
		String createTableSQL = SqlHelper.getCreateTableSQL(modelClazz,null);
		mDb.execSQL(createTableSQL);
	}

	@Override
	public long insert(T model) {
		ContentValues contentValues = new ContentValues();
		SqlHelper.parseModelToContentValues(model, contentValues);
		return mDb.insert(mTableName, contentValues);
	}

	@Override
	public boolean batchInsert(List<T> dataList) {
		List<ContentValues> listVal = new ArrayList<ContentValues>();
		for (T model : dataList) {
			ContentValues contentValues = new ContentValues();
			SqlHelper.parseModelToContentValues(model, contentValues);
			listVal.add(contentValues);
		}
		return mDb.batchInsert(mTableName, listVal);
	}

	@Override
	public int update(T model, String whereClause, String... whereArgs) {
		ContentValues contentValues = new ContentValues();
		SqlHelper.parseModelToContentValues(model, contentValues);
		return mDb.update(mTableName, contentValues, whereClause, whereArgs);
	}

	@Override
	public int delete(String whereClause, String... whereArgs) {
		return mDb.delete(mTableName, whereClause, whereArgs);
	}

	@Override
	public List<T> queryByCondition(String selection, String... selectionArgs) {
		return queryByCondition(null, selection, null, selectionArgs);
	}

	@Override
	public List<T> queryByCondition(String[] columns, String selection,
			String orderBy, String... selectionArgs) {
		return queryByCondition(columns, selection, null, null, orderBy, selectionArgs);
	}

	@Override
	public List<T> queryByCondition(String[] columns, String selection,
			String groupBy, String having, String orderBy,
			String... selectionArgs) {
		List<T> resultList = new ArrayList<T>();
		List<ResultSet> queryList = mDb.query(mTableName, columns, selection, groupBy, having, orderBy, selectionArgs);
		SqlHelper.parseQueryResultListToModelList(queryList, resultList, modelClazz);
		return resultList;
	}
	
	@Override
	public List<ResultSet> execQuerySQL(String sql, String... bindArgs){
		return mDb.execQuerySQL(sql, bindArgs);
	}
	
	@Override
	public boolean deleteAll() {
		return delete("1") == 1;
	}

	@Override
	public T queryFirstRecord(String selection, String... selectionArgs) {
		List<T> resultList = queryByCondition(selection, selectionArgs);
		if(resultList!=null&&resultList.size()==1){
			return resultList.get(0);
		}else{
			return null;
		}
	}
}
