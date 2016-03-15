package com.darcye.sqlite;

import java.util.ArrayList;

/**
 * A List For Paging QueryResult
 * @author Darcy
 *
 * @param <T>
 */
public class PagingList<T> extends ArrayList<T>{

	private static final long serialVersionUID = 5526933443772285251L;
	
	private int mTotalSize;
	
	public int getTotalSize(){
		return mTotalSize;
	}
	
	void setTotalSize(int totalSize){
		this.mTotalSize = totalSize;
	}
}
