package tester;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.darcye.sqlite.DaoFactory;
import com.darcye.sqlite.DbSqlite;
import com.darcye.sqlite.IBaseDao;
import com.darcye.sqlite.PagingList;

public class DbTester extends InstrumentationTestCase{
	
	private static final String TAG = "DbTester";
	
	private IBaseDao<UserModel> userDAO;
	private DbSqlite dbSqlite;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Context context = getInstrumentation().getContext();
		SQLiteDatabase db = context.openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
		dbSqlite = new DbSqlite(db);
		userDAO = DaoFactory.createGenericDao(dbSqlite, UserModel.class);
	}
	
	public void testCreateTable(){
		userDAO.createTable();
	}
	
	public void testInsertRecord(){
		UserModel user = new UserModel();
		user.userName = "darcy";
		user.isLogin = true;
		user.weight = 60.5;
		user.bornDate = new Date();
		byte[] picture = {0x1,0x2,0x3,0x4};
		user.pictrue = picture;
		userDAO.insert(user);
	}
	
	public void testUpdateRecord(){
		UserModel user = new UserModel();
		user.weight = 88.0;
		userDAO.update(user, "user_name=?", "darcy");
	}
	
	public void testQueryRecord(){
		UserModel user = userDAO.queryFirstRecord("user_name=?", "darcy");
		Assert.assertEquals(user.weight, 88.0);
	}
	
	public void testQueryList(){
		List<UserModel> userList = userDAO.query("user_name=? and weight > ?", new String[]{"darcy" , "60"});
		Log.i(TAG, userList.toString());
	}
	
	public void testPagingQuery(){
		PagingList<UserModel> pagingList = userDAO.pagingQuery(null, null, 1, 3);
		Log.i(TAG, "total size:"+pagingList.getTotalSize());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dbSqlite.closeDB();
	}
}
