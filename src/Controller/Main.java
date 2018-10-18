package Controller;

import MotionSimulator.MotionSimulator;
import MotionSimulator.VisualFrame;

public class Main {
    public static void main(String[] args) {
        MotionSimulator motionSimulator = new MotionSimulator();
        VisualFrame frame = new VisualFrame(motionSimulator.getPath());
        motionSimulator.getPath();
    }
}
