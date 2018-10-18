package Controller;

import MotionSimulator.Command;

public abstract class RobotController implements PathFunction, Chromosome<Command> {
    GeneticAlgorithm<Movements> geneticAlgorithm = new GeneticAlgorithm<>();

    public RobotController(GeneticAlgorithm<Movements> geneticAlgorithm) {
        this.geneticAlgorithm = geneticAlgorithm;
    }

    public void train(){

    }

    public void getSolution(){

    }
}
