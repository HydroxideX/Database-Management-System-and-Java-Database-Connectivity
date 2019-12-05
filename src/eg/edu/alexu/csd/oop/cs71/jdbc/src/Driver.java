package eg.edu.alexu.csd.oop.cs71.jdbc.src;

import java.io.File;
import java.sql.*;
//import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

public class Driver implements java.sql.Driver {
    ArrayList <Connection> connections = new ArrayList<>();
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if(!acceptsURL(url)) throw new SQLException("Wrong URL");
        Connection connection = new Connection(info, this);
        return connection;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return "jdbc:xmldb://localhost".equals(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
