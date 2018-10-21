package Controller;

import MotionSimulatorPackage.Command;
import MotionSimulatorPackage.TimedCommand;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class GeneticAlgorithm {

    // Writing to file
    private List<Movements> bestResults = new ArrayList<>();

    // Generic Objects
    private Random random = new Random();
    private List<Movements> population;

    // Current optimal individuals
    private Movements lowest = null;
    private Movements currentLowest = null;

    // ===== GA parameters ========

    // Chromosome length i.e. number of commands
    private int MIN_LENGTH = 10;
    private int MAX_LENGTH = 80;

    // Selection criteria
    private boolean ELITISM = true;
    private int TOURNAMENT_SIZE = 40;

    // Mutation
    private double MUTATION_RATE = 0.2;
    private double MUTATION_MAGNITUDE = 0.1;

    // Crossover
    private double CROSSOVER_RATE = 0.3;

    // Terminating Conditions
    private double ACCURACY_THRESHOLD = 0.05;
    private double FITNESS_DELTA_THRESHOLD = 0.05;

    // Current state of GA, number of runs
    private int CURRENT_EPOCH = 1;
    private int MAX_EPOCH = 50;

    private int pop_size = 500;

    // Fitness function
    private PathFunction<Movements> function;

    GeneticAlgorithm(PathFunction<Movements> function) {
        population = new ArrayList<>();
        this.function = function;
    }

    GeneticAlgorithm(int pop_size, int maxGen, PathFunction<Movements> function) {
        population = new ArrayList<>();
        this.function = function;
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
        for (Movements m : population) {
            m.setFitness(function.evaluate(m));
        }

        System.out.print("sorting, ");
        List<Movements> sorted = population.stream().parallel().sorted(Movements::compare).collect(Collectors.toList());
        population.clear();
        population.addAll(sorted);

        if (lowest == null)
            lowest = population.get(0).copy();

        // Determine global optimal
        if (lowest.getFitness() > population.get(0).getFitness()) {
            lowest = population.get(0).copy();
        }

        // Add global optimal to the list
        bestResults.add(lowest);
    }

    private Movements mutate(Movements movements) {
        List<TimedCommand> commands = movements.getPopulation();

        // Check if should mutate
        if (random.nextGaussian() <= MUTATION_RATE) {

            // Mutate child with new random values
            double mutationValue = random.nextGaussian() * MUTATION_MAGNITUDE;

            // mutate
            for (TimedCommand command : commands) {

                // Mutating commands
                Command c = command.getC();

                c.setLeft((int) Math.round(c.getLeft() + mutationValue));
                c.setRight((int) Math.round(c.getRight() + mutationValue));
            }
        }

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
        if (random.nextGaussian() > CROSSOVER_RATE) {
            List<TimedCommand> genes = parent.getPopulation();
            List<TimedCommand> genes2 = parent2.getPopulation();

            int shortest = Integer.compare(genes.size(), genes2.size());

            // Find a random crossover point respecting the shortest gene size
            int xover1 = (shortest != 1) ? random.nextInt(genes.size()) : random.nextInt(genes2.size());
            int xover2 = (shortest != -1) ? random.nextInt(genes2.size()) : random.nextInt(genes.size());

            // Create genes array with original gene data cut at the specified crossover index
            List<TimedCommand> child1Genes = new ArrayList<>(genes.subList(0, (xover1 == 0) ? 0 : xover1 - 1));

            // Add additional genes from secondary parent
            child1Genes.addAll(genes2.subList(xover2, genes2.size()));

            // Iterate over children correcting time position
            IntStream.range(0, child1Genes.size()).forEach(value -> child1Genes.get(value).setTime(1));

            // Create new children
            Movements child1 = new Movements(child1Genes);

            return child1;
        } else return (random.nextGaussian() > 0.5) ? parent : parent2;
    }

    private void evolve() {
        List<Movements> selectedList = new ArrayList<>();

        if (ELITISM) {
            selectedList.add(population.get(0));
        }

        IntStream.range((ELITISM) ? 1 : 0, pop_size).forEach(value -> {
            // Select parent to mutate & Crossover
            Movements crossedChild = crossover(tournamentSelect(), tournamentSelect());
            // Mutate children & add
            selectedList.add(mutate(crossedChild));
        });

        population.clear();
        population.addAll(selectedList);
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
            System.out.printf("\nFitness Threshold reached with value: %f\n", (currentLowest.getFitness() - lowest.getFitness()) / lowest.getFitness());
        }
        if (epoch_stop) {
            System.out.printf("\nMax epoch reached: %d / %d\n", CURRENT_EPOCH, MAX_EPOCH);
        }

        if (!(epoch_stop || delta_stop))
            System.out.printf(" - %f\n", (lowest == null) ? Double.MIN_VALUE : lowest.getFitness());

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
            Movements randomMovementPath = population.get(random.nextInt(population.size()));
            if (randomMovementPath.getFitness() < bestPath.getFitness())
                bestPath = randomMovementPath;
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
        return lowest;
    }

    List<Movements> getBestResults() {
        return bestResults;
    }
}
