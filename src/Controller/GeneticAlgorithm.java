package Controller;

import MotionSimulator.Command;
import MotionSimulator.State;
import MotionSimulator.TimedCommand;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class GeneticAlgorithm {

    // Writing to file
    private List<Movements> bestResults = new ArrayList<>();

    // Generic Objects
    private Random random = new Random();
    private List<Movements> population;

    // Current optimal indivduals
    private Movements lowest = null;
    private Movements currentLowest = null;

    // ===== GA parameters ========

    // Chromosome length i.e. number of commands
    private int MIN_LENGTH = 5;
    private int MAX_LENGTH = 30;

    // Selection criteria
    private boolean ELITISM = true;
    private int TOURNAMENT_SIZE = 20;

    // Mutation
    private double MUTATION_RATE = 0.5;
    private double MUTATION_MAGNITUDE = 0.5;

    // Crossover
    private double CROSSOVER_RATE = 0.3;

    // Terminating Conditions
    private double ACCURACY_THRESHOLD = 0.05;
    private double FITNESS_DELTA_THRESHOLD = 0.05;

    // Current state of GA, number of runs
    private int CURRENT_EPOCH = 1;
    private int MAX_EPOCH = 1000;

    // Fitness function
    private PathFunction<Movements> function;

    GeneticAlgorithm(PathFunction<Movements> function) {
        population = new ArrayList<>();
        this.function = function;
    }

    GeneticAlgorithm(int maxGen, PathFunction<Movements> function) {
        population = new ArrayList<>();
        this.function = function;
        this.MAX_EPOCH = maxGen;
    }

    void run() {
        System.out.println("Training GA:");
        System.out.println("EPOCH: ");
        generatePopulation();
        fitness();
        System.out.printf("%d, ", CURRENT_EPOCH);
        while (!terminate()) {
            evolve();
            fitness();
            CURRENT_EPOCH++;
            System.out.printf("%d, ", CURRENT_EPOCH);
        }
    }

    private void fitness() {
        population.stream().parallel().forEach(m -> m.setFitness(function.evaluate(m)));

        List<Movements> sorted = population.stream().parallel().sorted(Movements::compare).collect(Collectors.toList());
        population.clear();
        population.addAll(sorted);

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
                if (random.nextGaussian() <= MUTATION_RATE) {

                    // Mutating commands
                    Command c = command.getC();

                    //ISSUE multiplication instead of addition
                    c.setLeft((int) Math.round(c.getLeft() * mutationValue));
                    c.setRight((int) Math.round(c.getRight() * mutationValue));
                }
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
     * @return pair of crossover children
     */
    private Pair<Movements, Movements> crossover(Movements parent, Movements parent2) {
        if (random.nextGaussian() > CROSSOVER_RATE) {
            List<TimedCommand> genes = parent.getPopulation();
            List<TimedCommand> genes2 = parent2.getPopulation();

            int shortest = Integer.compare(genes.size(), genes2.size());

            // Find a random crossover point respecting the shortest gene size
            int xover1 = (shortest != 1) ? random.nextInt(genes.size()) : random.nextInt(genes2.size());
            int xover2 = (shortest != -1) ? random.nextInt(genes2.size()) : random.nextInt(genes.size());

            // Create genes array with original gene data cut at the specified crossover index
            List<TimedCommand> child1Genes = new ArrayList<>(genes.subList(0, xover1 - 1));
            List<TimedCommand> child2Genes = new ArrayList<>(genes2.subList(0, xover2 - 1));

            // Add additional genes from secondary parent
            child1Genes.addAll(genes2.subList(xover2, genes2.size()));
            child2Genes.addAll(genes.subList(xover1, genes.size()));

            // Iterate over children correcting time position
            IntStream.range(0, child1Genes.size()).forEach(value -> child1Genes.get(value).setTime(value + 1));
            IntStream.range(0, child2Genes.size()).forEach(value -> child2Genes.get(value).setTime(value + 1));

            // Create new children
            Movements child1 = new Movements(child1Genes);
            Movements child2 = new Movements(child2Genes);

            return new Pair<>(child1, child2);
        }

        // Return original parents as no crossover occured
        return new Pair<>(parent, parent2);
    }

    private void evolve() {
        List<Movements> selectedList = new CopyOnWriteArrayList<>();

        int startIndex = 0;
        if (ELITISM) {
            startIndex++;

            selectedList.add(population.get(0));
        }

        IntStream.range(startIndex, population.size()).parallel().forEach(value -> {

            // Select parent to mutate
            Movements firstParent = tournamentSelect();
            Movements secondParent = tournamentSelect();

            // Crossover
            Pair<Movements, Movements> crossedChildren = crossover(firstParent, secondParent);

            // Mutate children
            Movements child1 = mutate(crossedChildren.getKey());
            Movements child2 = mutate(crossedChildren.getValue());

            selectedList.add(child1);
            selectedList.add(child2);
        });

        population.clear();
        population.addAll(selectedList);
    }

    private boolean terminate() {
        if (lowest == null) {
            lowest = population.get(0).copy();
        } else {
            if (lowest.getFitness() > population.get(0).getFitness())
                lowest = population.get(0).copy();
            currentLowest = population.get(0).copy();
        }

        boolean epoch_stop = (CURRENT_EPOCH >= MAX_EPOCH);
        boolean delta_stop = false;
//        boolean accuracy_stop = (lowest.getFitness() <= ACCURACY_THRESHOLD);

        if (CURRENT_EPOCH > 1) {
            if ((currentLowest.getFitness() - lowest.getFitness()) / lowest.getFitness() <= FITNESS_DELTA_THRESHOLD)
                delta_stop = true;
        }

        return epoch_stop || delta_stop;
    }

    private void generatePopulation() {
        for (int i = 0; i < population.size(); i++) {
            Movements movement = new Movements();
            // generate each command with left/right values and add to movement.

            // Creates a variable length chromosome
            IntStream.range(0, randomBoundedInt(MIN_LENGTH, MAX_LENGTH)).forEach(index -> {
                // Motor left & right init
                Command c = new Command(randomBoundedInt(-701, 701), randomBoundedInt(-701, 701));

                // Create timed commanda nd add to movement
                movement.addCommand(new TimedCommand(c, index));
            });
        }
    }

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
