package tester;

import java.util.Arrays;
import java.util.Date;

import com.darcye.sqlite.Table;
import com.darcye.sqlite.Table.Column;

@Table(name="t_user")
public class UserModel {
	@Table.Column(name="user_id",type=Column.TYPE_INTEGER,isPrimaryKey=true)
	public Integer userId;
	
	@Table.Column(name="user_name",type=Column.TYPE_STRING,isNull=false)
	public String userName;
	
	@Table.Column(name="born_date",type=Column.TYPE_TIMESTAMP)
	public Date bornDate;
	
	@Table.Column(name="pictrue",type=Column.TYPE_BLOB)
	public byte[] pictrue;
	
	@Table.Column(name="is_login",type=Column.TYPE_BOOLEAN)
	public Boolean isLogin;
	
	@Table.Column(name="weight",type=Column.TYPE_DOUBLE)
	public Double weight;

	@Override
	public String toString() {
		return "UserModel [userId=" + userId + ", userName=" + userName
				+ ", bornDate=" + bornDate + ", pictrue="
				+ Arrays.toString(pictrue) + ", isLogin=" + isLogin
				+ ", weight=" + weight + "]";
	}
}
