package eg.edu.alexu.csd.oop.cs71.jdbc.src;


import eg.edu.alexu.csd.oop.cs71.db.FileManagementInterface;
import eg.edu.alexu.csd.oop.cs71.db.SQLBasicValidation;
import eg.edu.alexu.csd.oop.cs71.db.ValidationInterface;

import java.io.File;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TransferQueue;

import static java.lang.Math.max;

public class CLI {

    public static void main(String [] args){
        Scanner input = new Scanner(System.in);
        Driver sqldriver = new SQLDriver();
        String query = "jdbc:xmldb://localhost";
        Object object;
        try {
            if (sqldriver.acceptsURL(query)) {
                Properties info = new Properties();
                File dbDir = new File("");
                info.put("path", dbDir.getAbsoluteFile());
                Connection c = (Connection) sqldriver.connect(query, info);
                Statement finalStatement = c.createStatement();
                while (true) {
                    System.out.printf("SQL Command: ");
                    query = input.nextLine();
                    if(query.contains("select"))
                    {
                        try {
                            assert finalStatement != null;
                            Resultset rs = finalStatement.executeQuery(query);
                            Object[][] table = convertResultSetTOArray(rs);
                            printTable(table);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }else if(query.contains("create")||query.contains("drop"))
                    {
                        try {
                            object = finalStatement.execute(query);

                            if ((boolean) object) {
                                System.out.println("Success");
                            } else {
                                System.out.println("Success");
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }else if(query.contains("update")||query.contains("insert")||query.contains("delete"))
                    {
                        try {
                            int x = finalStatement.executeUpdate(query);
                            System.out.println("Changed Number of Columns: " + x );
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            }
        } catch (SQLException e) {
            System.out.println("Wrong Database");
        }

    }

    public static void printTable (Object[][] table) {
        if(table.length == 0) return;
        int [] maxDistArray = new int[table[0].length];
        int mx;
        int sum = 0;
        for(int j = 0;j <table[0].length;j++){
            mx = 0;
            for (int i = 0;i <table.length;i++) {
                mx = max(mx, table[i][j].toString().length());
            }
            maxDistArray[j] = mx;
            sum += mx;
        }
        sum+=4;
        sum+= 3 * table[0].length-2;
        printRow(maxDistArray,0);
        final String CYAN_BOLD_BRIGHT = "\033[1;96m";
        final String RESET = "\033[0m";
        for(int i = 0;i<table.length;i++){
            System.out.printf("█ ");
            for(int j = 0;j <table[i].length;j++){
                if(i == 0) System.out.printf(CYAN_BOLD_BRIGHT);
                else System.out.printf(RESET);
                System.out.printf(table[i][j].toString());
                for(int k = 0; k < maxDistArray[j] - table[i][j].toString().length();k++){
                    System.out.printf(" ");
                }
                if(j == table[i].length - 1) break;
                System.out.printf(RESET);
                System.out.printf(" █ ");
            }
            System.out.printf(RESET);
            System.out.printf(" █\n");
            printRow(maxDistArray,1);
        }

    }

    public static void printRow (int[] maxDistArray ,int big) {
        if(big == 0) System.out.printf("▄▄");
        else System.out.printf("█▄");
        for(int i = 0;i<maxDistArray.length;i++){
            for(int j = 0;j <maxDistArray[i];j++){
                System.out.printf("▄");
            }
            if(i == maxDistArray.length - 1) break;
            if(big == 0) System.out.printf("▄▄▄");
            else System.out.printf("▄█▄");
        }
        if(big == 0) System.out.printf("▄▄\n");
        else System.out.printf("▄█\n");
    }


    private static Object[][] convertResultSetTOArray(Resultset rs) {
        try {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int cols = resultSetMetaData.getColumnCount();
            int rows = 0;
            while(!rs.isAfterLast())  {
                rows++;
                rs.next();
            }
            String[][] table = new String[rows+1][cols];
            while(!rs.isFirst())  {
                rows--;
                rs.previous();
            }
            for(int i = 0; i < cols; i++){
                table[rows][i] = resultSetMetaData.getColumnLabel(i+1);
            }
            rows++;
            while(true){
                if(rs.isAfterLast()) break;
                for(int i = 0; i < cols;i++){
                    Object temp = rs.getObject(i+1);
                    if(temp.toString().toLowerCase().equals("null")) table[rows][i] = "";
                    table[rows][i] = temp.toString();
                }
                rs.next();
                rows++;
            }
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[5][5];
    }
}



