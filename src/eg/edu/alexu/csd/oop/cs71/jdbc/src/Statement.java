package eg.edu.alexu.csd.oop.cs71.jdbc.src;

//import java.sql.Connection;
//import java.sql.ResultSet;
import eg.edu.alexu.csd.oop.cs71.db.*;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Properties;

public class Statement implements java.sql.Statement {

    Facade facade=new Facade();
    FileManagement fm =new FileManagement();
    Connection connection;
    ArrayList<String> Queries = new ArrayList<>();
    Properties info;
    boolean closeState = false;
    public Statement(Connection connection,Properties info){
        this.connection = connection;
        this.info=info;
    }


    @Override
    public Resultset executeQuery(String sql) throws SQLException {
        if (isClosed()){
            throw new SQLException();
        }
        Object[][] data = (Object[][]) facade.parse(sql);
        data = facade.getFullTable(data);
        ArrayList<String> types = facade.getColumnTypes();
        String tableName = fm.getTableName(sql);
        Resultset rs=new Resultset(tableName,data,types);
        return rs;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        if (isClosed()){
            throw new SQLException();
        }
        return (int) facade.parse(sql);
    }

    @Override
    public void close() throws SQLException {
        connection=null;
        closeState=true;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        if (isClosed()){
            throw new SQLException();
        }
        ValidationInterface SQLvalidation = new SQLBasicValidation();
        if (SQLvalidation.validateQuery(sql)) {
            String query2 = sql;
            query2 = query2.toLowerCase();
            String[] command = query2.split(" ");
            String checker = query2.substring(0, 8);
            checker = checker.toUpperCase();
            String secondChecker = command[1].toUpperCase();
            if (checker.contains("SELECT")) {
                return true;
            } else if (checker.contains("UPDATE") || checker.contains("INSERT")
                    || checker.contains("DELETE") || checker.contains("ALTER")) {
                return false;
            }else if (checker.contains("CREATE")||checker.contains("DROP")){
                if(query2.contains("database")) {
                    String[] temp = info.get("path").toString().split("JDBC-API");
                    String sql2;
                    if (checker.contains("CREATE"))
                        sql2 = "create database " + temp[1] + "\\" + command[2];
                    else
                        sql2 = "drop database " + temp[1] + "\\" + command[2];
                    facade.parse(sql2);
                }
                else facade.parse(sql);
                return true;
            }
            return false;
        }else {
            System.out.println("Invalid Query");
            return false;
        }
    }

    @Override
    public Resultset getResultSet() throws SQLException {
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        if (isClosed()){
            throw new SQLException();
        }
        Queries.add(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        if (isClosed()){
            throw new SQLException();
        }
        Queries.clear();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public Resultset getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closeState;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
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