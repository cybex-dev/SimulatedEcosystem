package Controller;

import MotionSimulatorPackage.VisualFrame;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    int MAX_CORES = 8;

    List<RobotController> robotControllers = new ArrayList<>();
    List<Runnable> runnables = new ArrayList<>();

    public static void main(String[] args) {
        new Main();
    }

    private Main(){
        System.out.println("Generating controllers");
        final ExecutorService pool = Executors.newFixedThreadPool(MAX_CORES);

        int count = -1;
        for (int epoch: Collections.singletonList(200)){
            for (int popSize: Arrays.asList(100, 200)){
                for (int tSize: Collections.singletonList(10)){
                    for (double xOver: Arrays.asList(0.2, 0.3, 0.5, 0.6, 0.8)){
                        for (double mRate: Arrays.asList(0.1, 0.2, 0.4)){
                            for (double mMag: Arrays.asList(0.05, 0.1, 0.2)){
                                count++;
                                int finalCount = count;
                                RobotController robotController = new RobotController(epoch, popSize, tSize, xOver, mMag, mRate);
                                robotControllers.add(finalCount, robotController);
                                runnables.add(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("Starting " + finalCount);
                                        robotController.train();
                                        System.out.printf("Done: %d\n", finalCount);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
        RobotController fittestController = robotControllers.stream().reduce(RobotController::compare).orElse(null);
        if (fittestController == null) {
            System.out.println("Error in getting fittest controller");
            return;
        }

        VisualFrame frame = new VisualFrame(fittestController.getStates());
        frame.run();

    }
}
