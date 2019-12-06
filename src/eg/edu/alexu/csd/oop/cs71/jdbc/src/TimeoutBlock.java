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
            throw new SQLException();
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
    /*private final long timeoutMilliSeconds;
    private long timeoutInteval=100;

    public TimeoutBlock(long timeoutMilliSeconds){
        this.timeoutMilliSeconds=timeoutMilliSeconds;
    }

    public void addBlock(Callable runnable) throws Throwable{
        long collectIntervals=0;
        Thread timeoutWorker=new Thread(runnable);
        timeoutWorker.start();
        do{
            if(collectIntervals>=this.timeoutMilliSeconds){
                timeoutWorker.stop();
                throw new Exception("<<<<<<<<<<****>>>>>>>>>>> Timeout Block Execution Time Exceeded In "+timeoutMilliSeconds+" Milli Seconds. Thread Block Terminated.");
            }
            collectIntervals+=timeoutInteval;
            Thread.sleep(timeoutInteval);

        }while(timeoutWorker.isAlive());
        System.out.println("<<<<<<<<<<####>>>>>>>>>>> Timeout Block Executed Within "+collectIntervals+" Milli Seconds.");
    }


    public long getTimeoutInteval() {
        return timeoutInteval;
    }


    public void setTimeoutInteval(long timeoutInteval) {
        this.timeoutInteval = timeoutInteval;
    }*/
}
