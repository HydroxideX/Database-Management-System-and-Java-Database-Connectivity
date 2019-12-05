package eg.edu.alexu.csd.oop.cs71.jdbc.src;

import java.io.File;
import java.sql.*;
//import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class SQLDriver implements java.sql.Driver {
    ArrayList <Connection> connections = new ArrayList<>();
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if(!acceptsURL(url)) throw new SQLException("Wrong URL");
        Connection connection = new Connection(info, this);
        connections.add(connection);
        return connection;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return "jdbc:xmldb://localhost".equals(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        DriverPropertyInfo[] driverPropertyInfo = new DriverPropertyInfo[info.size()];
        int i = 0;
        Iterator var4 = info.entrySet().iterator();
        while(var4.hasNext()) {
            Map.Entry s = (Map.Entry)var4.next();
            DriverPropertyInfo temp = new DriverPropertyInfo(s.getKey().toString(),s.getValue().toString());
            driverPropertyInfo[i++] = temp;
        }
        return driverPropertyInfo;
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
