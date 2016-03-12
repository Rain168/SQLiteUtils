package com.darcye.sqlite;

import java.util.List;

/**
 * data access interface
 * 
 * @author Darcy
 *
 * @param <T>
 */
public interface IBaseDao<T> {
	
	/**
	 * create table
	 */
	void createTable();

	/**
	 * insert an object
	 * 
	 * @param model the model to insert
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	long insert(T model);

	/**
	 * batch insert
	 * 
	 * @param dataList
	 * @return
	 */
	boolean batchInsert(List<T> dataList);

	/**
	 * update an object by the condition you set
	 * 
	 * @param model the model to update
	 * @param whereClause the optional WHERE clause to apply when updating. Passing null will update all rows.
	 * @param whereArgs You may include ?s in the where clause, which will be replaced by the values from whereArgs. The values will be bound as Strings.
	 * @return the number of rows affected , or -1 if an error occurred
	 */
	int update(T model, String whereClause, String... whereArgs);

	/**
	 * insert or update , it will check the primary key as default
	 * @param model the model to insert
	 * @param bindColumnNames 
	 * @return
	 */
	long insertOrUpdate(T model,String... bindColumnNames);
	
	/**
	 * delete by condition
	 * 
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	int delete(String whereClause, String... whereArgs);


	/**
	 * delete all rows
	 * @return success return true, else return false
	 */
	boolean deleteAll();
	
	/**
	 * ����������ѯ
	 * 
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	List<T> queryByCondition(String selection, String... selectionArgs);

	/**
	 *  ����������ѯ
	 * @param columns
	 * @param selection
	 * @param orderBy
	 * @param selectionArgs
	 * @return
	 */
	List<T> queryByCondition(String[] columns, String selection,
			String orderBy, String... selectionArgs);

	/**
	 * ����������ѯ
	 * @param columns
	 * @param selection
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param selectionArgs
	 * @return
	 */
	List<T> queryByCondition(String[] columns, String selection,
			String groupBy, String having, String orderBy,
			String... selectionArgs);
	
	/**
	 * ֻ��Ψһһ����¼�Ĳ�ѯ
	 * 
	 * @return ���û���򷵻�null
	 */
	T queryUniqueRecord(String selection,String... selectionArgs);
	
	/**
	 * �Զ����ѯ
	 * @param sql
	 * @param bindArgs
	 * @return
	 */
	List<QueryResult> execQuerySQL(String sql, String... bindArgs);
	
	
	/**
	 * ִ��Insert/Update/Delete�������ǲ�ѯSQL
	 * @param sql
	 * @param bindArgs
	 * @return
	 */
	boolean execUpdateSQL(String sql, Object... bindArgs);
}
