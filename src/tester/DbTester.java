package tester;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
	
	private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();
	
	private static final String DB_PATH = SD_PATH + "/SQLite/";
	
	private static final String PIC1 = SD_PATH + "/_rec/a.jpg";
	private static final String PIC2 = SD_PATH + "/_rec/biye.png";
	
	private static  byte[] PIC1_DATA;
	private static  byte[] PIC2_DATA;
	
	private static String LONG_TEXT = "Unless required by applicable law or agreed to in writing, software"+
										"distributed under the License is distributed on an AS IS BASIS,"+
											"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."+
												"See the License for the specific language governing permissions and"+
													"limitations under the License.";
	
	private static String[] Names = {"Darcy","SuSan","张三","坏男孩","大侠"};
	
	static{
		FileInputStream pic1InputStream = null;
		FileInputStream pic2InputStream = null;
		ByteArrayOutputStream bosPic1 = null;
		ByteArrayOutputStream bosPic2 = null;
		
		try {
			pic1InputStream = new FileInputStream(PIC1);
			pic2InputStream = new FileInputStream(PIC2);
			bosPic1 = new ByteArrayOutputStream();
			bosPic2 = new ByteArrayOutputStream();
			
			byte[] buffer = new byte[1024];
			int readCount;
			while((readCount = pic1InputStream.read(buffer)) != -1){
				bosPic1.write(buffer, 0, readCount);
			}
			
			while((readCount = pic2InputStream.read(buffer)) != -1){
				bosPic2.write(buffer, 0, readCount);
			}
			
			PIC1_DATA = bosPic1.toByteArray();
			PIC2_DATA = bosPic2.toByteArray();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(pic1InputStream != null)
					pic1InputStream.close();
				if(pic2InputStream != null)
					pic2InputStream.close();
				if(bosPic1 != null)
					bosPic1.close();
				if(bosPic2 != null)
					bosPic2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
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
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH+"lookup.db" , null);
		dbSqlite = new DbSqlite(context,db);
		userDAO = DaoFactory.createGenericDao(dbSqlite, UserModel.class);
	}
	
	public void testCreateTable(){
		userDAO.createTable();
	}
	
	public void testInsertRecord(){
		
		for(int i = 0 ; i < 100; ++i){
			UserModel user = new UserModel();
			user.userName = Names[i%Names.length];
			user.idCard = Long.MAX_VALUE;
			user.isLogin = true;
			user.weight = Double.MAX_VALUE;
			user.bornDate = new Date();
			user.pictrue = i%2 == 0 ? PIC1_DATA : PIC2_DATA;
			user.article = LONG_TEXT;
			user.newColumn = Integer.MAX_VALUE;
			userDAO.insert(user);
		}
		
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
