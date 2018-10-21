package Controller;

import MotionSimulatorPackage.*;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class GeneticAlgorithm {

    // Writing to file
    private List<Movements> bestResults = new ArrayList<>();

    // Generic Objects
    private List<Movements> population;

    // Current optimal individuals
    private Movements optimalSolution = null;
    private Movements currentLowest = null;

    // ===== GA parameters ========

    // Chromosome length i.e. number of commands
    private int MIN_LENGTH = 1;
    private int MAX_LENGTH = 100;

    // Selection criteria
    private boolean ELITISM = false;
    private int TOURNAMENT_SIZE = 10;

    // Mutation
    private double MUTATION_RATE = 0.1;
    private double MUTATION_MAGNITUDE = 0.2;

    // Crossover
    private double CROSSOVER_RATE = 0.6;

    // Terminating Conditions
    private double ACCURACY_THRESHOLD = 0.05;
    private double FITNESS_DELTA_THRESHOLD = 0.05;

    // Current state of GA, number of runs
    private int CURRENT_EPOCH = 1;
    private int MAX_EPOCH = 100;

    private int pop_size = 200;

    // Fitness function
    private PathFunction<Movements> function = new PathFunction<Movements>() {

        private MotionSimulator simulator = new MotionSimulator();
        private final State STATE_INITIAL = new State(0, 0, 0);

        @Override
        public Coordinate function(double x) {
            // This gets the X value into a degree form usable by the sin function.

            // This scaling to degrees is done since the original X calculation was done in terms of a 700x700 grid.
            double degX = (x * 360.0) / 700.0;
            double y = Math.sin(Math.toRadians(degX));
            // Scale y to 700 to conform to the original X value
            double scaledY = y * 700.0;
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

            movements.setLastState(pathStates);

            Map<TimedCommand, State> commandStateMap = new HashMap<>();
            for (int i = 0; i < pathStates.size(); i++) {
                commandStateMap.put(population.get(i), pathStates.get(i));
            }

            commandStateMap.forEach((timedCommand, value) -> fitnessList.add(determineFitness(value)));

            // Add distance from last point to end of function. Can be both sided offset
            State state = pathStates.get(pathStates.size() - 1);
            double xOffSet = 360 - state.getX();
            Double dy = fitnessList.stream().reduce((d1, d2) -> d1 + d2).orElse(Double.MAX_VALUE);

            double ln_dy = Math.log(dy);
            double dx = Math.abs(xOffSet) * Math.E;
            double v = dx / ln_dy;

            movements.setyOffset(dy);
            movements.setxOffset(xOffSet);
            movements.setFitness(v);

//            double v = offSet * Math.E/ ln_dy;

            return v;
        }

        private double determineFitness(State state) {
            Coordinate idealLocation = function(Math.round(state.getX()));
            Coordinate currentLocation = new Coordinate(Math.round(state.getX()), state.getY());
            Coordinate difference = idealLocation.difference(currentLocation);

            // Get Y difference as the X component will be the same for current and ideal location when correlating the current position to the function
            double abs = Math.abs(difference.getY());
            return abs;
        }
    };


    GeneticAlgorithm(int pop_size, int maxGen/*, PathFunction<Movements> function*/) {
        population = new ArrayList<>();
//        this.function = function;
//        this.MAX_EPOCH = maxGen;
//        this.pop_size = pop_size;
    }

    void run() {
        System.out.println("Generating Population");
        generatePopulation();
        System.out.println("Done!");
        System.out.println("Training GA:");
        System.out.printf("[%d] ", CURRENT_EPOCH);
        fitness();
        while (!terminate()) {
            CURRENT_EPOCH++;
            System.out.printf("[%d] ", CURRENT_EPOCH);
            evolve();
            fitness();
        }
    }

    private void fitness() {
        System.out.print("evaluating, ");
        for (int i = 0; i < population.size(); i++) {
            function.evaluate(population.get(i));
        }

        System.out.print("sorting, ");
        List<Movements> sorted = population.stream().parallel().sorted(Movements::compare).collect(Collectors.toList());
        population.clear();
        population.addAll(sorted);

        if (optimalSolution == null)
            optimalSolution = population.get(0).copy();

        // Determine global optimal
        if (optimalSolution.getFitness() < population.get(0).getFitness())
            optimalSolution = population.get(0).copy();


        // Add global optimal to the list
        bestResults.add(optimalSolution.copy());

//        new VisualFrame(lowest.getLastState()).run();
    }

    private Movements mutate(Movements movements) {
        List<TimedCommand> commands = movements.getPopulation();

        // Check if should mutate
        if (new Random(new Random().nextLong()).nextGaussian() <= MUTATION_RATE) {

            // Mutate child with new random values
            double mutationValue = new Random(new Random().nextLong()).nextGaussian() * MUTATION_MAGNITUDE;

            // mutate
            for (TimedCommand command : commands) {

                // Mutating commands
                Command c = command.getC();

                c.setLeft((int) Math.round(c.getLeft() + mutationValue));
                c.setRight((int) Math.round(c.getRight() + mutationValue));
            }
        }

        movements.setPopulation(commands);

        // Movement with mutation
        return movements;
    }

    /**
     * Using binary crossover from a specific point.
     *
     * @param parent  first parent
     * @param parent2 second parent
     * @return crossed child
     */
    private Movements crossover(Movements parent, Movements parent2) {
        List<TimedCommand> genes = parent.getPopulation();
        List<TimedCommand> genes2 = parent2.getPopulation();

        int shortest = Integer.compare(genes.size(), genes2.size());

        // Find a random crossover point respecting the shortest gene size
        int xover1 = (shortest != 1) ? new Random(new Random().nextLong()).nextInt(genes.size()) : new Random(new Random().nextLong()).nextInt(genes2.size());
        int xover2 = (shortest != -1) ? new Random(new Random().nextLong()).nextInt(genes2.size()) : new Random(new Random().nextLong()).nextInt(genes.size());

        // Create genes array with original gene data cut at the specified crossover index
        List<TimedCommand> child1Genes = new ArrayList<>(genes.subList(0, (xover1 == 0) ? 0 : xover1 - 1));

        // Add additional genes from secondary parent
        child1Genes.addAll(genes2.subList(xover2, genes2.size()));

        // Iterate over children correcting time position
        IntStream.range(0, child1Genes.size()).forEach(value -> child1Genes.get(value).setTime(1));

        // Create new children
        Movements child1 = new Movements(child1Genes);

        return child1;
    }

    private void evolve() {
        List<Movements> selectedList = new ArrayList<>();

        int sameCount = 0;

        while (selectedList.size() < population.size()) {
            // Select parent to mutate & Crossover
            Movements parent1 = tournamentSelect();
            Movements parent2 = tournamentSelect();

            while (parent1.hashCode() == parent2.hashCode()) {
                sameCount++;
                parent2 = tournamentSelect();
            }

            if (new Random(new Random().nextLong()).nextGaussian() < CROSSOVER_RATE) {
                Movements crossedChild = crossover(parent1, parent2);
                // Mutate child & add
                selectedList.add(mutate(crossedChild));
            } else {

                // check if the selected list already contains the parent

                // Mutate parents & add
                Movements mutatedP1 = mutate(parent1);
                if (!selectedList.contains(mutatedP1))
                    selectedList.add(mutatedP1);
                if (selectedList.size() != population.size()) {
                    Movements mutatedP2 = mutate(parent2);

                    if (!selectedList.contains(mutatedP2))
                        selectedList.add(mutatedP2);
                }
            }
        }

        if (ELITISM) {
            selectedList.remove(0);
            selectedList.add(0, population.get(0));
        }

        population.clear();
        population = selectedList;
    }

    private boolean terminate() {
        System.out.print("end? ");
        boolean epoch_stop = (CURRENT_EPOCH >= MAX_EPOCH);
        boolean delta_stop = false;
//        boolean accuracy_stop = (lowest.getFitness() <= ACCURACY_THRESHOLD);

//        if (currentLowest != null) {
//            if (lowest != null) {

//                // Determine stopping condition
//                if (CURRENT_EPOCH > 1) {
//                    // Current lowest is in this case the previous iteration lowest. i.e. current - previous / current
//                    if ((currentLowest.getFitness() - population.get(0).getFitness()) / currentLowest.getFitness() <= FITNESS_DELTA_THRESHOLD)
//                        delta_stop = true;
//                }

//                lowest = population.get(0).copy();
//
//                // Determine global optimal
//                if (lowest.getFitness() > population.get(0).getFitness()) {
//                    lowest = population.get(0).copy();
//                }
//
//                // Add global optimal to the list
//                bestResults.add(lowest);
//
//            } else {
//                lowest = currentLowest;
//            }
//        }
//        currentLowest = population.get(0).copy();

        if (delta_stop) {
            System.out.printf("\nFitness Threshold reached with value: %f\n", (currentLowest.getFitness() - optimalSolution.getFitness()) / optimalSolution.getFitness());
        }
        if (epoch_stop) {
            System.out.printf("\nMax epoch reached: %d / %d\n", CURRENT_EPOCH, MAX_EPOCH);
        }

        if (!(epoch_stop || delta_stop))
            System.out.printf(" - %f %s\n", (optimalSolution == null) ? Double.MIN_VALUE : optimalSolution.getFitness(), optimalSolution.getLastXY());

        return epoch_stop || delta_stop;
    }

    private void generatePopulation() {
        for (int i = 0; i < pop_size; i++) {
            Movements movement = new Movements();
            // generate each command with left/right values and add to movement.

            // Creates a variable length chromosome
            IntStream.range(0, randomBoundedInt(MIN_LENGTH, MAX_LENGTH)).forEach(index -> {
                // Motor left & right init
                Command c = new Command(randomBoundedInt(-701, 701), randomBoundedInt(-701, 701));

                // Create timed commanda nd add to movement
                movement.addCommand(new TimedCommand(c, 1));
            });
            population.add(movement);
        }
    }

    /**
     * Using a maximum size of <code>TOURNAMENT_SIZE</code>, select random individuals from the population. From these, select the fittest individual.
     *
     * @return
     */
    private Movements tournamentSelect() {
        Movements bestPath = population.get(new Random(new Random().nextLong()).nextInt(population.size()));
        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            int i1 = new Random(new Random().nextLong()).nextInt(population.size());
            Movements randomMovementPath = population.get(i1);
            if (randomMovementPath.getFitness() < bestPath.getFitness()) {
                bestPath = randomMovementPath;
            }
        }
        return bestPath;
    }

    /**
     * Returns random integer bounded by a lower and upper limit
     *
     * @param lower lower bound value
     * @param upper upper vound value
     * @return bounded integer
     */
    private int randomBoundedInt(int lower, int upper) {
        int r = new Random(new Random().nextLong()).nextInt(upper);
        while (r < lower) {
            r = new Random(new Random().nextLong()).nextInt(upper);
        }
        return r;
    }

    Movements getLowest() {
        return optimalSolution;
    }

    List<Movements> getBestResults() {
        return bestResults;
    }
}
