package Controller;

import MotionSimulator.VisualFrame;

public class Main {

    private RobotController robotController = new RobotController();

    public static void main(String[] args) {
        new Main();
    }

    private Main(){
        System.out.println("Generating controller");
        robotController.train();
        System.out.println("Done!");
        VisualFrame frame = new VisualFrame(robotController.getBestSolutionStates());
        System.out.println("Displaying results");
        frame.run();
        System.out.println("Done!");
    }


}
