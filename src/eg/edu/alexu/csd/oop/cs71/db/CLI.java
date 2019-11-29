package eg.edu.alexu.csd.oop.cs71.db;

public class CLI {

    public static void main(String [] args){
        String query= "";
        ValidationInterface SQLvalidation = new SQLBasicValidation();
        Facade facade = new Facade();
        query = query.replaceAll(";","");
        if(SQLvalidation.validateQuery(query))
        {
            Object object;
            object=facade.parse(query);
        }
    }
}

