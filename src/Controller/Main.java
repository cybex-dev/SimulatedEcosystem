package Controller;

import MotionSimulatorPackage.State;
import MotionSimulatorPackage.VisualFrame;

import java.util.ArrayList;

public class Main {

    private RobotController robotController = new RobotController();

    public static void main(String[] args) {
        new Main();
    }

    private Main(){
        System.out.println("Generating controller");
        robotController.train();
        System.out.println("Done!");
        ArrayList<State> bestSolutionStates = robotController.getBestSolutionStates();
        VisualFrame frame = new VisualFrame(bestSolutionStates);
        System.out.println("Displaying results");
        frame.run();
        System.out.println("Done!");
    }
}
