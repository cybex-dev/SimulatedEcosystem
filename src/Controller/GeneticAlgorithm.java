package Controller;

import MotionSimulatorPackage.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class GeneticAlgorithm {

    Random random = new Random(new Random().nextLong());

    // Writing to file
    private List<Movements> bestResults = new ArrayList<>();

    // Generic Objects
    private List<Movements> population;

    // Current optimal individuals
    private Movements optimalSolution = null;
    private Movements currentLowest = null;

    // ===== GA parameters ========

    // Chromosome length i.e. number of commands
    private int MIN_LENGTH = 2;
    private int MAX_LENGTH = 150;

    // Selection criteria
    private boolean ELITISM = true;
    private int TOURNAMENT_SIZE = 20;

    // Mutation
    private double MUTATION_RATE = 0.2;//.3;
    private double MUTATION_MAGNITUDE = 0.1;//.25;

    // Crossover
    private double CROSSOVER_RATE = .8;

    // Terminating Conditions
    private double ACCURACY_THRESHOLD = 0.05;
    private double FITNESS_DELTA_THRESHOLD = 0.05;

    // Current state of GA, number of runs
    private int CURRENT_EPOCH = 1;
    private int MAX_EPOCH = 200;

    private int pop_size = 200;

    // Fitness function
    private PathFunction<Movements> function = new PathFunction<Movements>() {

        private MotionSimulator simulator = new MotionSimulator();
        private final State STATE_INITIAL = new State(0, 0, 0);

        @Override
        public Coordinate function(double degX) {
            // This gets the X value into a degree form usable by the sin function.

            // This scaling to degrees is done since the original X calculation was done in terms of a 700x700 grid.
            double y = Math.sin(Math.toRadians(degX));
            // Scale y to 700 to conform to the original X value
            double scaledY = Math.round(y * 700.0);
            double scaledX = Math.round(degX / 360 * 700);
            return new Coordinate(scaledX, scaledY);
        }

        @Override
        public void evaluate(Movements movements) {
            List<TimedCommand> population = movements.getPopulation();

            final State[] prevState = {STATE_INITIAL};
            ArrayList<State> pathStates = new ArrayList<>();
            IntStream.range(0, population.size()).forEach(value -> {
                TimedCommand timedCommand = population.get(value);
                ArrayList<State> path = simulator.getPath(prevState[0], new ArrayList<>(Collections.singletonList(timedCommand)));
                if (!pathStates.isEmpty())
                    path.remove(0);
                prevState[0] = path.get(0);
                pathStates.add(path.get(0));
            });
            movements.setLastState(pathStates);

            // Number of 'points' on graph.
            // 360 / numCommands (i,e. 1 -> 360, 10 -> 36)
            final double pointDegrees = 360.0 / population.size();
            List<Coordinate> ideaLocations = IntStream.range(1, population.size() + 1)
                    .parallel()
                    .boxed()
                    .map(integer -> function(pointDegrees * integer))
                    .collect(Collectors.toList());
            movements.setFitness(IntStream.range(0, population.size())
                    .parallel()
                    .boxed()
                    .mapToDouble(integer -> {
                        Coordinate idealCoordinate = ideaLocations.get(integer);
                        State state = pathStates.get(integer);
                        Coordinate current = new Coordinate(state.getX(), state.getY());
                        double distance = euclideanDistance(idealCoordinate, current);
                        return distance;
                    }).reduce(Double::sum).orElse(Double.MAX_VALUE) / population.size());
        }

        private double euclideanDistance(Coordinate differenceCoordinate) {
            double dx = differenceCoordinate.getX() * differenceCoordinate.getX();
            double dy = differenceCoordinate.getY() * differenceCoordinate.getY();
            return Math.abs(Math.sqrt(dx + dy));
        }

        private double euclideanDistance(Coordinate ideal, Coordinate current) {
            double dx = Math.abs(ideal.getX() - current.getX());
            double dy = Math.abs(ideal.getY() - current.getY());
            return euclideanDistance(new Coordinate(dx, dy));
        }
    };
    private String ID;


    GeneticAlgorithm(int max_gen, int pop_size, int tSize, double xOver, double mMag, double mRate) {
        population = new ArrayList<>();
        this.MAX_EPOCH = max_gen;
        this.pop_size = pop_size;
        this.TOURNAMENT_SIZE = tSize;
        this.CROSSOVER_RATE = xOver;
        this.MUTATION_MAGNITUDE = mMag;
        this.MUTATION_RATE = mRate;
    }

    void run() {
        System.out.println("Generating Population");
        generatePopulation();
//        System.out.println("Done!");
        System.out.println("Training GA:");
//        System.out.printfreached("[%d] ", CURRENT_EPOCH);
        fitness();
//        System.out.printf("run:135 [ %f ]\n", optimalSolution.getFitness());
        while (!terminate()) {
//            System.out.printf("run:137 [ %f ]\n", optimalSolution.getFitness());
            CURRENT_EPOCH++;
//            System.out.printf("run:139 [ %f ]\n", optimalSolution.getFitness());
//            System.out.printf("%s: [%d]\n", this.ID ,CURRENT_EPOCH);
//            System.out.printf("run:141 [ %f ]\n", optimalSolution.getFitness());
            evolve();
//            System.out.printf("run:143 [ %f ]\n", optimalSolution.getFitness());
            fitness();
//            System.out.printf("run:145 [ %f ]\n", optimalSolution.getFitness());
        }
    }

    private void fitness() {
//        if (optimalSolution != null)
//            System.out.printf("fitness:150 [ %f ]\n", optimalSolution.getFitness());
//            System.out.print("evaluating, ");
        for (int i = 0; i < population.size(); i++) {
            function.evaluate(population.get(i));
        }

//        if (optimalSolution != null)
//            System.out.printf("fitness:157 [ %f ]\n", optimalSolution.getFitness());

//        List<Movements> sorted = population.stream().parallel().sorted(Movements::compare).collect(Collectors.toList());
        if (ELITISM) {
            if (optimalSolution != null) {
                if (!population.contains(optimalSolution)) {
                    Movements movementsWorst = population.stream().reduce(Movements::comparatorWorst).orElse(null);
                    population.remove(movementsWorst);
                    population.add(optimalSolution.copy());
                }
            }
        }
//        if (optimalSolution != null)
//            System.out.printf("fitness:167 [ %f ]\n", optimalSolution.getFitness());

//        System.out.print("sorting, ");
        List<Movements> sorted = population.stream().parallel().sorted(Movements::compare).collect(Collectors.toList());
        population.clear();
        population.addAll(sorted);

        if (optimalSolution == null) {
            optimalSolution = population.get(0).copy();
//            System.out.printf("\t\tCHANGE\nfitness:166 [ %f ]\n", optimalSolution.getFitness());
        }

        // Determine global optimal
        if (optimalSolution.getFitness() > population.get(0).getFitness()) {
            optimalSolution = population.get(0).copy();
//            System.out.printf("\n\n========================\n\n\t\tCHANGE\nfitness:172 [ %f ]\n", optimalSolution.getFitness());
        }


        // Add global optimal to the list
        bestResults.add(optimalSolution.copy());
//        System.out.printf("fitness:179 [ %f ]\n", optimalSolution.getFitness());

//        new VisualFrame(lowest.getLastState()).run();
    }

    private Movements mutate(Movements movements) {
        List<TimedCommand> commands = movements.getPopulation();

        // Check if should mutate
        if (random.nextDouble() <= MUTATION_RATE) {

            // Mutate child with new random values
            for (TimedCommand command : commands) {

                // Mutating commands
                Command c = command.getC();

                c.setLeft((int) Math.round(c.getLeft() + random.nextGaussian() * MUTATION_MAGNITUDE));
                c.setRight((int) Math.round(c.getRight() + random.nextGaussian() * MUTATION_MAGNITUDE));
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
        int xover1 = (shortest != 1) ? random.nextInt(genes.size()) : random.nextInt(genes2.size());
        while (xover1 == 0) {
            xover1 = (shortest != 1) ? random.nextInt(genes.size()) : random.nextInt(genes2.size());
        }

        int xover2 = (shortest != -1) ? random.nextInt(genes2.size()) : random.nextInt(genes.size());
        while (xover2 == 0) {
            xover2 = (shortest != 1) ? random.nextInt(genes.size()) : random.nextInt(genes2.size());
        }

        // Create genes array with original gene data cut at the specified crossover index
        List<TimedCommand> child1Genes = new ArrayList<>(genes.subList(0, xover1 - 1));

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
//        System.out.printf("evolve:154 [ %f ]\n", optimalSolution.getFitness());
        while (selectedList.size() < population.size()) {
            // Select parent to mutate & Crossover
            Movements parent1 = tournamentSelect();
            Movements parent2 = tournamentSelect();

            while (parent1.equals(parent2)) {
                sameCount++;
                parent2 = tournamentSelect();
            }

            if (random.nextDouble() <= CROSSOVER_RATE) {
                Movements crossedChild = crossover(parent1.copy(), parent2.copy());
                // Mutate child & add
                selectedList.add(mutate(crossedChild.copy()));
            } else {

                // check if the selected list already contains the parent

                // Mutate parents & add
                Movements mutatedP1 = mutate(parent1.copy());
                if (!selectedList.contains(mutatedP1))
                    selectedList.add(mutatedP1);
                if (selectedList.size() != population.size()) {
                    Movements mutatedP2 = mutate(parent2.copy());

                    if (!selectedList.contains(mutatedP2))
                        selectedList.add(mutatedP2);
                }
            }
        }

        population.clear();
        population = selectedList;
//        System.out.printf("evolve:288 [ %f ]\n", optimalSolution.getFitness());
    }

    private boolean terminate() {
//        System.out.print("end? ");
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

//        if (delta_stop) {
//            System.out.printf("\nFitness Threshold reached with value: %f\n", (currentLowest.getFitness() - optimalSolution.getFitness()) / optimalSolution.getFitness());
//        }
//        if (epoch_stop) {
//            System.out.printf("\nMax epoch reached: %d / %d\n", CURRENT_EPOCH, MAX_EPOCH);
//        }

//        if (!(epoch_stop || delta_stop))
//            System.out.printf(" - %f %s\n", (optimalSolution == null) ? Double.MIN_VALUE : optimalSolution.getFitness(), optimalSolution.getLastXY());

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
        Movements bestPath = population.get(random.nextInt(population.size()));
        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            int i1 = random.nextInt(population.size());
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
        int r = random.nextInt(upper);
        while (r < lower) {
            r = random.nextInt(upper);
        }
        return r;
    }

    Movements getLowest() {
        return optimalSolution;
    }

    List<Movements> getBestResults() {
        return bestResults;
    }

    public int getTOURNAMENT_SIZE() {
        return TOURNAMENT_SIZE;
    }

    public double getMUTATION_RATE() {
        return MUTATION_RATE;
    }

    public double getMUTATION_MAGNITUDE() {
        return MUTATION_MAGNITUDE;
    }

    public double getCROSSOVER_RATE() {
        return CROSSOVER_RATE;
    }

    public int getMAX_EPOCH() {
        return MAX_EPOCH;
    }

    public int getPop_size() {
        return pop_size;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
