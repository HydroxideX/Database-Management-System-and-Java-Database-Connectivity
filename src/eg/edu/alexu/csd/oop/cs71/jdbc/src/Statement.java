package eg.edu.alexu.csd.oop.cs71.jdbc.src;

//import java.sql.Connection;
//import java.sql.ResultSet;

import eg.edu.alexu.csd.oop.cs71.db.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.*;

public class Statement implements java.sql.Statement {

    Facade facade = new Facade();
    FileManagement fm = new FileManagement();
    Connection connection;
    ArrayList<String> Queries = new ArrayList<>();
    Properties info;
    boolean closeState = false;
    int timeout;
    DBLogger dbLogger = DBLogger.getInstance();
    TimeoutBlock timeoutBlock = new TimeoutBlock();

    public Statement(Connection connection, Properties info) {
        this.connection = connection;
        this.info = info;
    }


    @Override
    public Resultset executeQuery(String sql) throws SQLException {
        if (isClosed()) {
            throw new SQLException();
        }
        ValidationInterface SQLvalidation = new SQLBasicValidation();
        if (SQLvalidation.validateQuery(sql)) {
            Object[][] data = (Object[][]) facade.parse(sql);
            if (data != null) {
                data = facade.getFullTable(data);
                ArrayList<String> types = facade.getColumnTypes();
                String tableName = fm.getTableName(sql);
                Resultset rs = new Resultset(tableName, data, types, this);
                dbLogger.addLog("fine", "Select Query executed");
                return rs;
            }
            dbLogger.addLog("severe", "Select Query failed");
            throw new SQLException("Table doesn't exist");
        }
        dbLogger.addLog("severe", "Select Query failed");
        throw new SQLException("Invalid Query");
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        if (isClosed()) {
            throw new SQLException();
        }
        ValidationInterface SQLvalidation = new SQLBasicValidation();
        if (SQLvalidation.validateQuery(sql)) {
            dbLogger.addLog("fine","Update Query executed");
            return (int) facade.parse(sql);
        }
        dbLogger.addLog("Severe","Update Query Failed!");
        throw new SQLException("Invalid Query");
    }

    @Override
    public void close() throws SQLException {
        connection = null;
        closeState = true;
        dbLogger.addLog("fine", "Statement Closed");
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxRows() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        if (isClosed()) {
            throw new SQLException();
        }
        return timeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        if (isClosed()) {
            throw new SQLException();
        }
        if (seconds < 0) {
            throw new SQLException();
        }
        timeout = seconds;
    }

    @Override
    public void cancel() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        if (isClosed()) {
            throw new SQLException();
        }
        ValidationInterface SQLvalidation = new SQLBasicValidation();
        if (SQLvalidation.validateQuery(sql)) {
            String query2 = sql;
            query2 = query2.toLowerCase();
            String[] command = query2.split(" ");
            query2 += "NullValueToPassUse";
            String checker = query2.substring(0, 8);
            checker = checker.toUpperCase();
            String secondChecker = command[1].toUpperCase();
            if (checker.contains("SELECT")) {
                Resultset resultset = executeQuery(sql);
                if (resultset.tableData.length > 1)
                    return true;
                else return false;
            } else if (checker.contains("UPDATE") || checker.contains("INSERT")
                    || checker.contains("DELETE") || checker.contains("ALTER")) {
                executeUpdate(sql);
                return true;
            } else if (checker.contains("CREATE") || checker.contains("DROP") || checker.contains("USE")) {
                if (query2.contains("database")) {
                    String a=info.get("path").toString()+command[2];
                    if(!a.contains("JDBC-API"))
                    {
                        facade.parse(sql);
                        return true;
                    }
                    String[] temp = a.split("JDBC-API");
                    if (temp[1].charAt(0) == '\\') {
                        String tempo = temp[1].substring(1, temp[1].length() - 1);
                        temp[1] = tempo;
                    }
                    if (!temp[1].contains("\\")) {
                        facade.parse(sql);
                        return true;
                    }
                    String sql2;
                    if (checker.contains("CREATE"))
                        sql2 = "create database " + temp[1] + "\\" + command[2];
                    else
                        sql2 = "drop database " + temp[1] + "\\" + command[2];
                    facade.parse(sql2);
                    dbLogger.addLog("fine", "Create|Drop Query executed");
                } else if (checker.contains("USE")) {
                    if (facade.parse(sql) != null)
                        dbLogger.addLog("fine", "Use Query executed");
                    else {
                        dbLogger.addLog("Severe", "Use Query failed!");
                        throw new SQLException("Database doesn't exist");
                    }
                } else return (boolean) facade.parse(sql);
                return true;
            }
            return false;
        } else {
            dbLogger.addLog("Severe", "Invalid Query");
            throw new SQLException("Invalid Query");
        }
    }

    @Override
    public Resultset getResultSet() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetType() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        if (isClosed()) {
            throw new SQLException();
        }

        if (sql.contains("update") || sql.contains("insert") || sql.contains("delete")) {
            Queries.add(sql);
            dbLogger.addLog("fine", "Query Added to Batch");
        } else {
            dbLogger.addLog("Severe", "Failed to add to batch");
            throw new SQLException("can't add a non-update query");
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        if (isClosed()) {
            throw new SQLException();
        }
        Queries.clear();
        dbLogger.addLog("fine", "Batch cleared");
    }

    @Override
    public int[] executeBatch() throws SQLException {
        ArrayList<Integer> rowsAffected = new ArrayList<Integer>();
        if (Queries.size() == 0) {
            dbLogger.addLog("Severe", "Failed to execute batch");
            throw new SQLException("Batch is empty");
        }
        for (int i = 0; i < Queries.size(); i++) {
            if (Queries.get(i).contains("update") || Queries.get(i).contains("insert") || Queries.get(i).contains("delete"))
                rowsAffected.add(executeUpdate(Queries.get(i)));
        }
        int[] rowsAffected1 = new int[rowsAffected.size()];
        for (int i = 0; i < rowsAffected.size(); i++) rowsAffected1[i] = rowsAffected.get(i);
        dbLogger.addLog("fine", "Batch Executed");
        return rowsAffected1;

    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resultset getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closeState;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
}