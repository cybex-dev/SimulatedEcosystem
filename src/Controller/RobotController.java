package Controller;

import MotionSimulator.MotionSimulator;
import MotionSimulator.State;
import MotionSimulator.TimedCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class RobotController {
    private GeneticAlgorithm geneticAlgorithm;
    private MotionSimulator simulator;
    private static final State STATE_INITIAL = new State(0, 0, 0);

    private PathFunction<Movements> fitnessFunction = new PathFunction<Movements>() {
        @Override
        public Coordinate function(int x) {
            // This gets the X value into a degree form usable by the sin function.

            // ISSUE: check this goes fil sin wave
            // Limited to 360 since anything further, a penalty should be incurred.
            int degX = (x * 6 > 360) ? 360 : x * 6;
            double y = Math.sin(Math.toRadians(degX));
            return new Coordinate(x, (int) Math.round(y));
        }

        @Override
        public double evaluate(Movements movements) {
            // The inverse of the error
            final CopyOnWriteArrayList<Double> fitnessList = new CopyOnWriteArrayList<>();

            List<TimedCommand> commands = movements.getPopulation();
            ArrayList<State> pathStates = simulator.getPath(STATE_INITIAL, new ArrayList<>(movements.getPopulation()));
            Map<TimedCommand, State> commandStateMap = IntStream.range(0, movements.getPopulation().size())
                    .boxed()
                    .collect(Collectors.toMap(commands::get, pathStates::get));

            commandStateMap.entrySet().stream().parallel().forEach(entry -> {
                TimedCommand timedCommand = entry.getKey();
                State state = entry.getValue();

                Coordinate idealLocation= function(timedCommand.getTime());
                Coordinate currentLocation = new Coordinate(state.getX(), state.getY());
                Coordinate difference = idealLocation.difference(currentLocation);

                double commandFitness = Math.abs(Math.sqrt( (difference.getX() * difference.getX()) + difference.getY() * difference.getY()));
                fitnessList.add(commandFitness);
            });

            // ISSUE possible issue with returning Double.MAX_VALUE
            return fitnessList.stream().reduce((d1, d2) -> d1 + d2).orElse(Double.MAX_VALUE);
        }
    };

    RobotController() {
        simulator =  new MotionSimulator();
        geneticAlgorithm = new GeneticAlgorithm(1000, fitnessFunction);
    }

    void train(){
        geneticAlgorithm.run();
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
        return simulator.getPath(STATE_INITIAL, new ArrayList<>(getBestSolution().getPopulation()));
    }

}
