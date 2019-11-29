package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface ConditionalParser {
    public void operationPerformer(String colName, ArrayList<HashMap<String, Object>> table,ArrayList<String> pp,ArrayList<String> answers,String operation);
}
