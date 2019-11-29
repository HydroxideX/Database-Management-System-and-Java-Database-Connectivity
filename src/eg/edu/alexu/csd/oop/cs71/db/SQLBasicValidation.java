package eg.edu.alexu.csd.oop.cs71.db;

public class SQLBasicValidation implements ValidationInterface {
    public boolean validateQuery(String q)
    {
        q=q.trim();
        q=q.replaceAll("\\s+"," ");
        q=q.replaceAll(";","");
        q=q.toLowerCase();
        String[] dataSplit=q.split("\\(");
        String[] command=q.split(" ");
        if(command.length<2)return false;
        switch (command[0])
        {
            case "create": case "drop":{
            switch (command[1])
            {
                case "database": case "schema":{
                if(command.length>3)
                    return false;
            }
            break;
                case "table":
                {
                    boolean regex= q.matches("create\\stable\\s\\w+\\s?\\((\\s?\\w+\\s(int|varchar)+\\s?\\,\\s?)*(\\s?\\w+\\s(int|varchar)+\\s?\\))");
                    boolean regex1= q.matches("drop\\stable\\s\\w+");
                    if(!regex&&!regex1)
                    {
                        return false;
                    }
                }
                break;
                default:return false;
            }
        }
        break;
            case "use":
            {
                if(command.length>2)
                    return false;
            }
            break;
            case "alter":
            {
                boolean regex=q.matches("(alter)\\s(table)\\s\\w+\\s(((add)\\s\\w+\\s(int|varchar))|((modify)(column)?\\s\\w+\\s(int|varchar))|((drop)\\s(column)\\s\\w+))");
                if(!regex)
                    return false;
            }
            break;
            case "delete":
            {
                boolean regex=q.matches("(delete\\sfrom)\\s\\w+(\\s*where\\s*(not\\s+)?\\w+\\s*((in\\s*\\(((\\s*(\\'\\w+\\')|(\\d+))\\s*\\,?\\s*)+\\s*\\))|(between\\s*((\\'\\w+\\')|(\\d+))\\s+and\\s+((\\'\\w+\\')|(\\d+))\\s*)|((=|<\\s*=|>\\s*=|<\\s*>|!=|>|<)\\s*((\\'\\w+\\')|(\\d+)))\\s*)(((and|or)\\s+((not\\s+)?\\w+\\s*((in\\s*\\(((\\s*(\\'\\w+\\')|(\\d+))\\s*\\,?\\s*)+\\s*\\))|(between\\s*((\\'\\w+\\')|(\\d+))\\s+and\\s+((\\'\\w+\\')|(\\d+))\\s*)|((=|<\\s*=|>\\s*=|<\\s*>|!\\s*=|>|<)\\s*((\\'\\w+\\')|(\\d+))))))\\s*)*)*\\;?\\s*");
                if(!regex)
                    return false;
            }
            break;
            case "update":
            {
                boolean regex=q.matches("(update)\\s\\w+\\s(set)(\\s\\w+\\s?\\=\\s?(\\'\\s?\\w+\\s?\\'|\\-?\\d+)\\s?\\,\\s?)*(\\s\\w+\\s?\\=\\s?(\\'\\s?\\w+\\s?\\'|\\-?\\d+))(\\s*where\\s*(not\\s+)?\\w+\\s*((in\\s*\\(((\\s*(\\'\\w+\\')|(\\d+))\\s*\\,?\\s*)+\\s*\\))|(between\\s*((\\'\\w+\\')|(\\d+))\\s+and\\s+((\\'\\w+\\')|(\\d+))\\s*)|((=|<\\s*=|>\\s*=|<\\s*>|!=|>|<)\\s*((\\'\\w+\\')|(\\d+)))\\s*)(((and|or)\\s+((not\\s+)?\\w+\\s*((in\\s*\\(((\\s*(\\'\\w+\\')|(\\d+))\\s*\\,?\\s*)+\\s*\\))|(between\\s*((\\'\\w+\\')|(\\d+))\\s+and\\s+((\\'\\w+\\')|(\\d+))\\s*)|((=|<\\s*=|>\\s*=|<\\s*>|!\\s*=|>|<)\\s*((\\'\\w+\\')|(\\d+))))))\\s*)*)*\\;?\\s*");
                if(!regex)
                    return false;
            }
            break;
            case "insert":
            {
                boolean regex=q.matches("(insert\\sinto)\\s\\w+\\s?(\\((\\s?\\w+\\s?\\,\\s?)*(\\w+\\s?)\\))?\\s?(values)\\s?\\(\\s?(((\\'\\s?\\w+\\s?\\')|(\\-?\\d+))\\s?\\,\\s?)*((\\'\\s?\\w+\\s?\\')|(\\-?\\d+))\\s?\\)");
                if(!regex)
                    return false;

            }
            break;
            case "select":
            {
                boolean regex=q.matches("(select)\\s(\\*\\s|(\\w+\\s?\\,\\s?)*(\\w+\\s))(from)\\s\\w+(\\s*where\\s*(not\\s+)?\\w+\\s*((in\\s*\\(((\\s*(\\'\\w+\\')|(\\d+))\\s*\\,?\\s*)+\\s*\\))|(between\\s*((\\'\\w+\\')|(\\d+))\\s+and\\s+((\\'\\w+\\')|(\\d+))\\s*)|((=|<\\s*=|>\\s*=|<\\s*>|!=|>|<)\\s*((\\'\\w+\\')|(\\d+)))\\s*)(((and|or)\\s+((not\\s+)?\\w+\\s*((in\\s*\\(((\\s*(\\'\\w+\\')|(\\d+))\\s*\\,?\\s*)+\\s*\\))|(between\\s*((\\'\\w+\\')|(\\d+))\\s+and\\s+((\\'\\w+\\')|(\\d+))\\s*)|((=|<\\s*=|>\\s*=|<\\s*>|!\\s*=|>|<)\\s*((\\'\\w+\\')|(\\d+))))))\\s*)*)*\\;?\\s*");
                if(!regex)
                    return false;
            }
            break;
            default:return false;
        }
        return true;
    }
}
//leave it here
//\s(where)\s(not\s)?\w+\s?(\=|\>\=|\<\=|\<\>|\<|\>|\!\=|between|like|in)\s?(\'\s?\w+\s?\'|\-?\d+)(\sand\s(not\s)?\w+\s?(\=|\>\=|\<\=|\<\>|\<|\>|\!\=|between|like|in)\s?(\'\s?\w+\s?\'|\-?\d+))*(\sor\s(not\s)?\w+\s?(\=|\>\=|\<\=|\<\>|\<|\>|\!\=|between|like|in)\s?(\'\s?\w+\s?\'|\-?\d+))*)?