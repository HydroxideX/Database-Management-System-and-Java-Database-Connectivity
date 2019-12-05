package eg.edu.alexu.csd.oop.cs71.jdbc.src;

import java.sql.SQLException;
import java.util.ArrayList;

public class ResultSetMetaData implements java.sql.ResultSetMetaData{
    private String tableName ;
    private ArrayList <String> colTypes ;  
    private Object[] colNames;
    public ResultSetMetaData(String tableName,Object[] colNames,ArrayList <String> colTypes ) {
    	this.colNames = colNames;
    	this.colTypes= colTypes;
    	this.tableName = tableName;
	}
	@Override
    public int getColumnCount() throws SQLException {
        return colNames.length;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public boolean isSearchable(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public boolean isCurrency(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public int isNullable(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public boolean isSigned(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return colNames[column-1].toString();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return colNames[column-1].toString();
    }

    @Override
    public String getSchemaName(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public int getPrecision(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public int getScale(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public String getTableName(int column) throws SQLException {
        return tableName;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public int getColumnType(int column) throws SQLException {
    	String type = colTypes.get(column).toLowerCase();
        return type.equals("varchar")?2:type.equals("int")?1:-1;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return colTypes.get(column).toLowerCase();
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public boolean isWritable(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public String getColumnClassName(int column) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {		throw new UnsupportedOperationException();}

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {		throw new UnsupportedOperationException();}
}
