package eg.edu.alexu.csd.oop.cs71.test.db;

import eg.edu.alexu.csd.oop.cs71.db.Database;
import eg.edu.alexu.csd.oop.cs71.test.TestRunner;
import org.junit.Assert;
import org.junit.Test;

public class IntegrationTest {

    public static Class<?> getSpecifications(){
        return Database.class;
    }
    
    @Test
    public void test() {
        Assert.assertNotNull("Failed to create DBMS implemenation",  (Database) TestRunner.getImplementationInstanceForInterface(Database.class));
    }

}
