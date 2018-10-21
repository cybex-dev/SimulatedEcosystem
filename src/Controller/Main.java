package Controller;

import MotionSimulatorPackage.State;
import MotionSimulatorPackage.VisualFrame;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    int MAX_CORES = 8;

    public static void main(String[] args) {
        new Main();
    }

    private Main(){
        Queue<RunnableThread> list = new PriorityQueue<>();
        System.out.println("Generating controllers");
        final ExecutorService pool = Executors.newFixedThreadPool(MAX_CORES);

        int count = 0;
        for (int epoch: Arrays.asList(100, 200, 400)){
            for (int popSize: Arrays.asList(50, 100, 200, 400)){
                for (int tSize: Arrays.asList(5, 10, 20)){
                    for (double xOver: Arrays.asList(0.2, 0.3, 0.5, 0.6, 0.8)){
                        for (double mRate: Arrays.asList(0.1, 0.2, 0.4)){
                            for (double mMag: Arrays.asList(0.05, 0.1, 0.2)){
                                count++;
                                int finalCount = count;

                                pool.execute(() -> {
                                    System.out.println("Starting " + finalCount);
                                    new RobotController(epoch, popSize, tSize, xOver, mMag, mRate).train();
                                    System.out.printf("Done: %d\n", finalCount);
                                });
                            }
                        }
                    }
                }
            }
        }
        System.out.println("All threads created!");

    }
}

class RunnableThread implements Comparable{
    private Runnable runnable;
    private int index;

    public RunnableThread(Runnable runnable, int index) {
        this.runnable = runnable;
        this.index = index;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public int getIndex() {
        return index;
    }
}
