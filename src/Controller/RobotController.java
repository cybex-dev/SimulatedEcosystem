package Controller;

import MotionSimulatorPackage.Command;
import MotionSimulatorPackage.MotionSimulator;
import MotionSimulatorPackage.State;
import MotionSimulatorPackage.TimedCommand;

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

    private PathFunction<Movements> fitnessFunction = new PathFunction<Movements>() {
        @Override
        public Coordinate function(int x) {
            // This gets the X value into a degree form usable by the sin function.

            // This scaling to degrees is done since the original X calculation was done in terms of a 700x700 grid.
            double degX = (x * 360.0) / 700.0;
            double y = Math.sin(Math.toRadians(degX));
            // Scale y to 700 to conform to the original X value
            int scaledY = (int) (y * 700.0);
            return new Coordinate(x, scaledY);
        }

        @Override
        public double evaluate(Movements movements) {
            // The inverse of the error
            final ArrayList<Double> fitnessList = new ArrayList<>();
            List<TimedCommand> population = movements.getPopulation();

            final State[] prevState = {STATE_INITIAL};
            ArrayList<State> pathStates = new ArrayList<>();
            IntStream.range(0, population.size()).forEach(value -> {
                TimedCommand timedCommand = population.get(value);
                ArrayList<State> path = simulator.getPath(prevState[0], new ArrayList<>(Collections.singletonList(timedCommand)));
                path.remove(0);
                prevState[0] = path.get(0);
                pathStates.add(path.get(0));
            });


            Map<TimedCommand, State> commandStateMap = new HashMap<>();
            for (int i = 0; i < pathStates.size(); i++) {
                commandStateMap.put(population.get(i), pathStates.get(i));
            }

            commandStateMap.forEach((timedCommand, value) -> fitnessList.add(determineFitness(value)));

            // Add distance from last point to end of function. Can be both sided offset
            State state = pathStates.get(pathStates.size() - 1);
            double offSet = 360 - state.getX();
            Double dy = fitnessList.stream().reduce((d1, d2) -> d1 + d2).orElse(Double.MAX_VALUE);

            double ln_dy = Math.log(dy);

            double dx = Math.abs(offSet) * 2;

            double v = dx / ln_dy;
            System.out.printf("Fitness = %f\n", v);

            return v;
        }

        private double determineFitness(State state) {
            Coordinate idealLocation = function((int) Math.round(state.getX()));
            Coordinate currentLocation = new Coordinate((int) Math.round(state.getX()), state.getY());
            Coordinate difference = idealLocation.difference(currentLocation);

            // Get Y difference as the X component will be the same for current and ideal location when correlating the current position to the function
            double abs = Math.abs(difference.getY());
            return abs;
        }
    };

    RobotController() {
        simulator =  new MotionSimulator();
        geneticAlgorithm = new GeneticAlgorithm(100, 200, fitnessFunction);
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
        List<TimedCommand> population = getBestSolution().getPopulation();
        ArrayList<State> path = simulator.getPath(STATE_INITIAL, new ArrayList<>(population));
        return path;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
