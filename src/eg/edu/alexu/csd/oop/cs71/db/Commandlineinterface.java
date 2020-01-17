package eg.edu.alexu.csd.oop.cs71.db;


import java.sql.SQLException;
import java.util.Scanner;

import static java.lang.Math.max;

public class Commandlineinterface {

    public static void main(String [] args){
        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.printf("SQL Command: ");
            String query = input.nextLine();
            ValidationInterface SQLvalidation = new SQLBasicValidation();
            Facade facade = new Facade();
            query = query.replaceAll(";", "");
            if (SQLvalidation.validateQuery(query)) {
                Object object = null;
                try {
                    object = facade.parse(query);
                } catch (SQLException e) {
                    String s = e.getMessage();
                    System.out.println(s);
                }
                query = query.toLowerCase();
                if (query.contains("select") && object != null) {
                    Object[][] x = facade.getFullTable((Object[][]) object);
                    printTable(x);
                } else if (!(query.contains("create") || query.contains("drop") || query.contains("use")) && object != null && (int) object != -1) {
                        System.out.println("Rows Number: " + object.toString());
                }
            } else {
                System.out.println("Invalid Query");
            }
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
        for(int i = 0;i<table.length;i++){
            System.out.printf("█ ");
            for(int j = 0;j <table[i].length;j++){
                System.out.printf(table[i][j].toString());
                for(int k = 0; k < maxDistArray[j] - table[i][j].toString().length();k++){
                    System.out.printf(" ");
                }
                if(j == table[i].length - 1) break;
                System.out.printf(" █ ");
            }
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
}

