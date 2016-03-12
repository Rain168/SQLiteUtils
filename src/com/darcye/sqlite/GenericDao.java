package com.darcye.sqlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

import com.darcye.sqlite.Table.Column;


/**
 * 
 * @author Darcy
 * @param <T>
 * 
 */
class GenericDao<T> implements IBaseDao<T> {

	private Class<?> modelClazz;
	
	private String mTableName;

	private String primaryKey;
	
	private DbSqlite mDb;
	
	public GenericDao(DbSqlite db, Class<?> modelClazz) {
		this.mDb = db;
		this.modelClazz = modelClazz;
		mTableName = SqlHelper.getTableName(modelClazz);
	}
	
	@Override
	public void createTable() {
		String createTableSQL = SqlHelper.getCreateTableSQL(modelClazz,new SqlHelper.OnPrimaryKeyListener() {
			@Override
			public void onGetPrimaryKey(String keyName) {
				primaryKey = keyName;
			}
		});
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
		List<QueryResult> queryList = mDb.query(mTableName, columns, selection, groupBy, having, orderBy, selectionArgs);
		SqlHelper.parseQueryResultListToModelList(queryList, resultList, modelClazz);
		return resultList;
	}
	
	@Override
	public List<QueryResult> execQuerySQL(String sql, String... bindArgs){
		return mDb.execQuerySQL(sql, bindArgs);
	}
	
	@Override
	public boolean execUpdateSQL(String sql, Object... bindArgs) {
		return mDb.execSQL(sql, bindArgs);
	}
	
	@Override
	public boolean deleteAll() {
		return delete("1") == 1;
	}

	@Override
	public T queryUniqueRecord(String selection, String... selectionArgs) {
		List<T> resultList = queryByCondition(selection, selectionArgs);
		if(resultList!=null&&resultList.size()==1){
			return resultList.get(0);
		}else if(resultList!=null&&resultList.size()>1){
			StringBuilder  args = new StringBuilder();
			for (String arg : selectionArgs) {
				args.append(arg);
				args.append(",");
			}
			throw new RuntimeException("表名:"+mTableName+",条件为:selection="+selection+",selectionArgs="+args+"超过一条数据!");
		}else{
			return null;
		}
	}

	@Override
	public long insertOrUpdate(T model,String... bindColumnNames) {
		String[] bindColumnValues;
		String selection;
		T modelInDb;
		if(bindColumnNames==null||bindColumnNames.length==0){
			if(primaryKey==null)
				reGainPrimaryKey();
			bindColumnValues = new String[1];
			selection = primaryKey+"=?";
			bindColumnValues[0] = getValueByColumnName(model, primaryKey);
		}else{
			bindColumnValues = new String[bindColumnNames.length];
			StringBuilder selectionBuidler = new StringBuilder();
			String columnName;
			String columnValue;
			for(int index = 0; index< bindColumnNames.length; ++index){
				columnName = bindColumnNames[index];
				columnValue = getValueByColumnName(model, columnName);
				if(columnValue == null){
					selectionBuidler.append(" "+columnName + " is null ");
				}else{
					bindColumnValues[index]  = columnValue;
					selectionBuidler.append(" "+columnName + "=? ");
				}
				selectionBuidler.append("and");
			}
			selection = selectionBuidler.substring(0, selectionBuidler.length()-3);
		}
		modelInDb = queryUniqueRecord(selection, bindColumnValues);
		ContentValues contentValues = new ContentValues();
		SqlHelper.parseModelToContentValues(model, contentValues);
		if(modelInDb==null){
			return mDb.insert(mTableName, contentValues);
		}else{
			return mDb.update(mTableName, contentValues, selection, bindColumnValues);
		}
	}
	
	
	
	/**
	 * 根据列名获取对象中的值
	 * 
	 * @param model
	 * @param columnName
	 * @return
	 */
	private String getValueByColumnName(T model,String columnName){
		Class<?> mClazz = model.getClass();
		Field[] fields = mClazz.getDeclaredFields();
		
		Column column;
		for (Field field : fields) {
			field.setAccessible(true);
			column = field.getAnnotation(Column.class);
			if(column!=null&&columnName.equalsIgnoreCase(column.name())){
				try {
					return field.get(model)==null?"":field.get(model).toString();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
	
	/**
	 * 重新获取primaryKey
	 */
	private void reGainPrimaryKey(){
		createTable();
	}
}
