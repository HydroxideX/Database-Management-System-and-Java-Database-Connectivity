package eg.edu.alexu.csd.oop.cs71.jdbc.src;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.sql.SQLException;
import java.sql.Time;
import java.util.concurrent.*;

public class TimeoutBlock {
    Future<Object> future ;
    ExecutorService executor;
    public Object addTask(Callable c , long time) throws SQLException {
        Object x ;
        executor = Executors.newSingleThreadExecutor();
        future = executor.submit(c);
        try {
            x= (int)future.get(time, TimeUnit.SECONDS); //timeout is in 2 seconds
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new SQLException();
        } catch (InterruptedException e) {
            future.cancel(true);
            throw new SQLException();
        }
        catch (ExecutionException e) {
            future.cancel(true);
            throw new SQLException(e.getMessage());
        }
        executor.shutdownNow();
        return  x;

        /*if (future.isCancelled()){System.out.println("sorry");return false;}
        else {System.out.println("good");
            return true;}*/

    }

    public static void main(String[] args) throws SQLException {
        TimeoutBlock t = new TimeoutBlock();
        System.out.println((int) t.addTask(new Callable() {

            public Object call() throws Exception {
                int a = 0;
                for (int i = 0; i < 10000000; i++) {a++;}
                System.out.print("finished 1\n");
                return a;
            }
        },500));

        System.out.println((int)t.addTask(new Callable() {

            public Object call() throws Exception {
                int a = 0;
                for (int i = 0; i < 5000000; i++) {a++;}
                System.out.print("finished 2\n");
                return a;
            }
        },500));

    }
}
