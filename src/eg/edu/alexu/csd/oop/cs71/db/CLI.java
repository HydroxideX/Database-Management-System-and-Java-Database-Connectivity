package eg.edu.alexu.csd.oop.cs71.db;


import java.util.Scanner;

public class CLI {

    public static void main(String [] args){
        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.printf("SQL Command: ");
            String query = input.nextLine();
            ValidationInterface SQLvalidation = new SQLBasicValidation();
            Facade facade = new Facade();
            query = query.replaceAll(";", "");
            if (SQLvalidation.validateQuery(query)) {
                Object object = facade.parse(query);
                query = query.toLowerCase();
                if (query.contains("select") && object != null) {
                    Object[][] x = facade.getFullTable((Object[][]) object);
                    printTable(x);
                } else if (!(query.contains("create") || query.contains("drop") || query.contains("use")) && object != null) {
                    if ((int) object != -1) {
                        System.out.println("Rows Number: " + object.toString());
                    }
                }
            }
        }
    }

    static void printTable(Object[][] table){
        for(int i = 0;i<table.length;i++){
            for(int j = 0;j<table[i].length;j++){
                System.out.printf(table[i][j].toString()+" ");
            }
            System.out.printf("\n");
        }
    }
}

