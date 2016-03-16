package tester;

import java.io.File;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.darcye.sqlite.DaoFactory;
import com.darcye.sqlite.DbSqlite;
import com.darcye.sqlite.IBaseDao;
import com.darcye.sqlite.PagingList;

public class DbTester extends InstrumentationTestCase{
	
	private static final String TAG = "DbTester";
	
	private static final String DB_PATH = Environment.getExternalStorageDirectory().getPath() + "/SQLite/";
	
	static{
		File dbPath = new File(DB_PATH);
		if(!dbPath.exists())
			dbPath.mkdirs();
	}
	
	private IBaseDao<UserModel> userDAO;
	private DbSqlite dbSqlite;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Context context = getInstrumentation().getContext();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH+"test.db" , null);
		dbSqlite = new DbSqlite(context,db);
		userDAO = DaoFactory.createGenericDao(dbSqlite, UserModel.class);
	}
	
	public void testCreateTable(){
		userDAO.createTable();
	}
	
	public void testInsertRecord(){
		UserModel user = new UserModel();
		user.userName = "darcy";
		user.idCard = System.currentTimeMillis();
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
		Log.i(TAG, user.toString());
	}
	
	public void testQueryList(){
		List<UserModel> userList = userDAO.query("user_name=? and weight > ?", new String[]{"darcy" , "60"});
		Log.i(TAG, userList.toString());
	}
	
	public void testPagingQuery(){
		PagingList<UserModel> pagingList = userDAO.pagingQuery(null, null, 1, 3);
		Log.i(TAG, "total size:"+pagingList.getTotalSize());
	}
	
	public void testAddColumn(){
		userDAO.updateTable();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dbSqlite.closeDB();
	}
}
