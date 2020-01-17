# Database Management System and Java Database Connectivity
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/95459e46281f406993e8fd1de404becd)](https://www.codacy.com/manual/HydroxideX/JDBC-API?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HydroxideX/JDBC-API&amp;utm_campaign=Badge_Grade)

-   implemented using Java, JavaFX.

-   SQL Database Management System and Java Database Connectivity.

-   Check The Application Here [Link](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/releases).

## Features ##

-   Java Database Connectivity API implementation.
-   Graphical User Interface.
-   Command Line Interface.
-   Timeout For Queries.
-   Batching Queries.
-   Full Logger Of User Actions.

## Supported SQL statements ##
> ### CREATE
>> -   CREATE DATABASE dbname.
>> -   CREATE TABLE table_name (column_name1 data_type, column_name2 data_type).

> ### DROP
>> -   DROP TABLE table_name.
>> -   DROP DATABASE database_name.

> ### INSERT
>> -   INSERT INTO table_name (ID,Name) VALUES (1,'youssef').
>> -   INSERT INTO table_name VALUES (1,'youssef', ...).

> ### SELECT
>> -   SELECT col1, col2, col3 FROM table_name WHERE col1 < 8.
>> -   SELECT * FROM table_name WHERE col1 = 5 or not col2 in ('yahia', 'omar') and col3 != 7.
>> -   SELECT * FROM table_name where col1 <= col2.
>> -   SELECT * FROM table_name where col1 between 5 and 7.
>> -   SELECT * FROM table_name order by col1 ASC, col2, col3 DESC.

> ### DELETE
>> -   DELETE FROM table_name WHERE some_column=some_value.
>> -   DELETE FROM table_name.
>> -   DELETE * FROM table_name.

> ### UPDATE
>> -   UPDATE table_name SET column1= 'value1',column2='value2' WHERE column3 ='value'.
>> -   UPDATE tableName SET column1 ='someValue', column2 ='someValue'.

## Supported Data Types
### int, varchar

## Contribution Guidelines ##
This project is public, Feel free to create pull requests.

## Screenshots ##
### Graphical User Interface

![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_7.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_8.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_9.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_10.png)

### Error Messages When Applying Wrong Queries
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_11.png)

### Command Line Interface
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_1.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_2.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_3.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_4.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_5.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_6.png)

## Diagrams
### Use Case Diagram
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_12.png)

### State Diagram
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_13.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_14.png)
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_15.png)

### Sequence Diagram
![picture alt](https://github.com/HydroxideX/Database-Management-System-and-Java-Database-Connectivity/blob/master/screenshots/screenshot_16.png)
