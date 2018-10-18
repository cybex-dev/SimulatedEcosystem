package Controller;

import MotionSimulator.Command;

public abstract class RobotController implements PathFunction, Chromosome<Command> {
    GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

    public RobotController(GeneticAlgorithm geneticAlgorithm) {
        this.geneticAlgorithm = geneticAlgorithm;
    }

    public void train(){

    }

    public void getSolution(){

    }
}
