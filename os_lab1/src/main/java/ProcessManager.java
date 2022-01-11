import java.util.Arrays;

public class ProcessManager {

    public Integer run(int x, Pipe[] pipeArr) throws InterruptedException {
        Runnable interruptAll = () -> {
            for (int i = 0; i != pipeArr.length; i++)
                pipeArr[i].interrupt();
        };

        for (int i = 0; i != pipeArr.length; i++) {
            pipeArr[i].setValue(x);
            pipeArr[i].start();
        }

        Integer[] res = new Integer[pipeArr.length];
        while (Arrays.asList(res).contains(null)){
            for (int i = 0; i < pipeArr.length; i++) {
                var temp = pipeArr[i].readFromPipe();
                if (temp != null)
                    res[i] = temp;
            }

            for (int i = 0; i != pipeArr.length; i++)
                if (pipeArr[i].failed()) {
                    var error = pipeArr[i].getFailedMessage();
                    interruptAll.run();
                    throw new InterruptedException(pipeArr[i].processName + " failed: " + error);
                }
        }

        for (int i = 0; i != pipeArr.length; i++)
            pipeArr[i].join();

        return operation(res);
    }

    private Integer operation(Integer[] res) {
        Integer result = 0;

        for (int i = 0; i != res.length; i++)
            result += res[i];

        return result;
    }
}