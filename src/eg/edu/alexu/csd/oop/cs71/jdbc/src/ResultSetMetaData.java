package eg.edu.alexu.csd.oop.cs71.jdbc.src;

import java.sql.SQLException;
import java.util.ArrayList;

public class ResultSetMetaData implements java.sql.ResultSetMetaData{
    public String tableName ;
    public ArrayList <String> colTypes , colNames , colLabels ;  
    public ResultSetMetaData(String tableName,ArrayList <String> colNames ,ArrayList <String> colTypes ,  ArrayList <String> colLabels) {
    	this.colTypes= colTypes;
    	this.colNames = colNames;
    	this.colLabels= colLabels;
    	this.tableName = tableName;
    	
	}
    public ResultSetMetaData () {
    	this("", new ArrayList<String>() ,new ArrayList<String>() ,new ArrayList<String>() );
    }
    public ResultSetMetaData(String tableName,ArrayList <String> colNames ,ArrayList <String> colTypes) {
    	this(tableName,colNames ,colTypes ,new  ArrayList <String>());
    	
	}
	@Override
    public int getColumnCount() throws SQLException {
        return colTypes.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return 0;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return null;
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return null;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return null;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return null;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return null;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return null;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
