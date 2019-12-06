package eg.edu.alexu.csd.oop.cs71.jdbc.src;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.sql.SQLException;
import java.sql.Time;
import java.util.concurrent.*;

public class TimeoutBlock {
    Future<Object> future ;
    ExecutorService executor;
    public Object addTask(Callable c , int time) throws SQLException {
        Object x ;
        executor = Executors.newSingleThreadExecutor();
        future = executor.submit(c);
        try {
            x=future.get(time, TimeUnit.SECONDS); //timeout is in 2 seconds
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new SQLException("Query TimedOut");
        } catch (InterruptedException e) {
            future.cancel(true);
            throw new SQLException(e.getMessage());
        }
        catch (ExecutionException e) {
            future.cancel(true);
            throw new SQLException(e.getMessage());
        }
        executor.shutdownNow();
        return  x;
    }
}
