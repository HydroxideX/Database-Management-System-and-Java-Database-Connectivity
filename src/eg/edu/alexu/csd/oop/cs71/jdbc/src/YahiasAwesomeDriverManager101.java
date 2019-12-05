package eg.edu.alexu.csd.oop.cs71.jdbc.src;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Properties;

public class YahiasAwesomeDriverManager101 {
    ArrayList <Driver> allDrivers = new ArrayList<>();

    public void registerDriver(eg.edu.alexu.csd.oop.cs71.jdbc.src.Driver driver) {

    }

    public Connection getConnection(String path, eg.edu.alexu.csd.oop.cs71.jdbc.src.Driver driver){
        System.out.println("I AM AMAZING");
        return new Connection(new Properties(), new eg.edu.alexu.csd.oop.cs71.jdbc.src.Driver());
    }
}
