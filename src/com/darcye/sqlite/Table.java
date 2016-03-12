package com.darcye.sqlite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * ��ע���ʽ
 * @author Darcy
 *
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
public @interface Table {
	/**
	 * ����
	 * @return
	 */
   public String name();
   
   
   /**
    * ���ֶ�ע��
    * 
    * @author Darcy
    * 
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   public @interface Column {
   	
   	//����
   	public static final String TYPE_INTEGER = "INTEGER";
   	public static final String TYPE_STRING = "TEXT";
   	public static final String TYPE_TIMESTAMP ="TIMESTAMP";
   	public static final String TYPE_BOOLEAN = "BOOLEAN";
   	public static final String TYPE_FLOAT = "FLOAT";
   	public static final String TYPE_DOUBLE = "DOUBLE";
   	public static final String TYPE_BLOB = "BLOB";
   	
   	/**
   	 * Ĭ��ֵ
   	 */
   	public static final class DEFAULT_VALUE{
   		public static final String TRUE = "1";
   		public static final String FALSE = "0";
   		public static final String CURRENT_TIMESTAMP = "(datetime(CURRENT_TIMESTAMP,'localtime'))";
   	}
   	
   	/**
   	 * ����
   	 * @return
   	 */
   	public String name();
   	
   	/**
   	 * �ֶ�����
   	 * @return
   	 */
   	public String type();
   	
   	/**
   	 * Ĭ��ֵ
   	 * @return
   	 */
   	public String defaultValue()default "null";
   	
   	/**
   	 * ���� ,Ĭ�ϲ���
   	 * @return
   	 */
   	public boolean isPrimaryKey()default false;
   	
   	/**
   	 * �Ƿ����ΪNull ,Ĭ�Ͽ���ΪNull
   	 * @return
   	 */
   	public boolean isNull()default true;
   	
   	
   }

}
