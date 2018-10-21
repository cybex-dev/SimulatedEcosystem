package Controller;

import MotionSimulatorPackage.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.IntStream;

class RobotController {
    private GeneticAlgorithm geneticAlgorithm;
    private MotionSimulator simulator;
    private static final State STATE_INITIAL = new State(0, 0, 0);

    // Logging
    private PrintWriter writer;
    private String CARAT = ";";
    private String ID = "default";


    RobotController() {
        simulator =  new MotionSimulator();
        geneticAlgorithm = new GeneticAlgorithm(100, 200);
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initLogger() {
        try {
            File logger = new File("controller_" + ID + ".csv");
            if (logger.exists())
                logger.delete();
            logger.createNewFile();

            writer = new PrintWriter(logger);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void train(){
        geneticAlgorithm.run();

//        initLogger();
        List<Movements> bestResults = geneticAlgorithm.getBestResults();
        ArrayList<State> bestSolutionStates = getBestSolutionStates();

//        IntStream.range(0, bestResults.size()).forEach(value -> {
//            writer.write(String.valueOf(bestResults.get(value).getFitness()) + CARAT);
//            String s = bestSolutionStates.stream().map(State::toString).reduce((s1, s2) -> s1.concat(CARAT).concat(s2).concat(CARAT).concat("\n")).orElse("[]");
//            writer.write(s);
//        });
//        writer.close();
    }

    /**
     * Gets the best solution of the algorithm
     * @return best movement / /individual
     */
    private Movements getBestSolution(){
        return geneticAlgorithm.getLowest();
    }

    /**
     * Returns the corresponding state list of the best solution
     * @return best individual states
     */
    ArrayList<State> getBestSolutionStates(){
        Movements bestSolution = getBestSolution();
        ArrayList<State> lastState = bestSolution.getLastState();
        List<TimedCommand> population = getBestSolution().getPopulation();
        ArrayList<State> path = simulator.getPath(STATE_INITIAL, new ArrayList<>(population));
        return lastState;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
