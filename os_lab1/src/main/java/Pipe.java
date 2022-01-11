import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.function.Function;


public class Pipe extends Thread {
    private final PipedOutputStream pipedOutputStream;
    private final PipedInputStream pipedInputStream;
    private final int sleepTime;
    private long startTime;
    private Integer value;
    private String failedMessage;
    public String processName;
    private final Function<Integer, Integer> func;


    public Pipe(String processName, int sleepTime, Function<Integer, Integer> func) throws IOException {
        this.pipedInputStream = new PipedInputStream();
        this.pipedOutputStream = new PipedOutputStream(pipedInputStream);
        this.sleepTime = sleepTime;
        this.value = null;
        this.processName = processName;
        this.func = func;
    }

    public void setValue(int x) {
        value = x;
    }
    @Override
    public synchronized void start() {
        startTime = System.currentTimeMillis();
        super.start();
    }

    public Integer readFromPipe() {
        Integer res = null;
        try {
            while (pipedInputStream.available() != 0) {
                int size = pipedInputStream.available();
                while (size != 0) {
                    byte[] arr = new byte[size];
                    var temp = pipedInputStream.read(arr);
                    res = (int) arr[0];
                    if (res == 0){
                        failedMessage = "Null";
                        res = null;
                    }
                    size -= temp;
                }
            }
        } catch (IOException e) {
            failedMessage = "Pipe read error";
            res = null;
        }

        if (res == null)
            return res;

        long endTime = System.currentTimeMillis();
        System.out.println(processName + " time:" + (endTime - startTime) + "msec:" + res);
        return res;
    }

    public void run() {
        try {
            Thread.sleep(sleepTime);
            Integer x = func.apply(value);
            pipedOutputStream.write(x);
        } catch (InterruptedException e) {
            failedMessage = "Interrupted work during sleepTime";
        } catch (IOException e) {
            failedMessage = "Piped error";
        } catch (IllegalArgumentException e) {
            failedMessage = "Incorrect argument";
        } catch (Exception e) {
            failedMessage = "Exception error";
        }
    }

    public boolean failed() {
        return !(failedMessage == null);
    }

    public String getFailedMessage() {
        return failedMessage;
    }
}