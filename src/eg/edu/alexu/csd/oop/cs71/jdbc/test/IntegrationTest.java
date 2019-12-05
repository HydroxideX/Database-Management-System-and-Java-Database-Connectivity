package eg.edu.alexu.csd.oop.cs71.jdbc.test;
import java.sql.Driver;

import org.junit.Assert;
import org.junit.Test;


public class IntegrationTest {

    public static Class<?> getSpecifications(){
        return Driver.class;
    }
    
    @Test
    public void test() {
        Assert.assertNotNull("Failed to create Driver implemenation",  (Driver) eg.edu.alexu.csd.oop.cs71.jdbc.src.TestRunner.getImplementationInstanceForInterface(Driver.class));
    }

}
