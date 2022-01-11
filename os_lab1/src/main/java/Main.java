import os.lab1.compfuncs.basic.IntOps;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        var processManager = new ProcessManager();
        var input = new Scanner(System.in);

        for(;;) {
            System.out.println("Input value:");
            int value;
            try {
                value = input.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("The application stop working, wrong input");
                break;
            }
            long startTime = System.currentTimeMillis();
            try {
                Pipe[] pipes = {
                        new Pipe("f", 0, (integer -> {
                            try {
                                return IntOps.trialF(integer).orElse(0);
                            }  catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        })),
                        new Pipe("g", 0, (integer -> {
                            try {
                                return IntOps.trialG(integer).orElse(0);
                            }  catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        })),
                };
                var res = processManager.run(value, pipes);
                System.out.println(res);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Total time:" + (endTime - startTime) + "msec.");
        }
    }
}