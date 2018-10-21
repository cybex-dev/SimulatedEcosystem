package Controller;

import MotionSimulatorPackage.State;
import MotionSimulatorPackage.VisualFrame;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private Main(){
        System.out.println("Generating controllers");
        int count = 0;
        for (int epoch: Arrays.asList(100, 200, 400)){
            for (int popSize: Arrays.asList(50, 100, 200, 400)){
                for (int tSize: Arrays.asList(5, 10, 20)){
                    for (double xOver: Arrays.asList(0.2, 0.3, 0.5, 0.6, 0.8)){
                        for (double mRate: Arrays.asList(0.1, 0.2, 0.4)){
                            for (double mMag: Arrays.asList(0.05, 0.1, 0.2)){
                                count++;
                                int finalCount = count;
                                new Thread(() -> {
                                    new RobotController(epoch, popSize, tSize, xOver, mMag, mRate).train();
                                    System.out.print("Done: %i\n" + finalCount);
                                }).start();
                            }
                        }
                    }
                }
            }
        }
        System.out.println("All threads started!");
    }
}
