package Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

public class GeneticAlgorithm {

    private Random random = new Random();

    private List<Movements> population;

    private boolean ELITISM = true;

    private int TOURNAMENT_SIZE = 20;

    private double FITNESS_DELTA_THRESHOLD = 0.05;

    private double CROSSOVER_RATE = 0.3;

    private double MUTATION_RATE = 0.5;

    private double MUTATION_MAGNITUDE = 0.5;

    private int UPPERBOUND = 5;

    private int LOWERBOUND = -5;

    private int CURRENT_EPOCH = 1;

    private int MAX_EPOCH = 1000;

    private PathFunction<Movements> function;

    private Movements lowest = null;

    private Movements currentLowest = null;

    public GeneticAlgorithm() {
        population = new ArrayList<>();
    }

    public GeneticAlgorithm(PathFunction<Movements> function) {
        population = new ArrayList<>();
        this.function = function;
    }

    public GeneticAlgorithm(int maxGen, PathFunction<Movements> function) {
        population = new ArrayList<>();
        this.function = function;
        this.MAX_EPOCH = maxGen;
    }

    public void run() {
        // Population is set in superclass
        generatePopulation();
        fitness();
        while (!terminate()) {
            evolve();
            fitness();
            CURRENT_EPOCH++;
        }
    }

    public void setFunction(PathFunction<Movements> function) {
        this.function = function;
    }

    public void fitness() {
        population.stream().parallel().forEach(chromosome -> chromosome.setFitness(function.evaluate(new ArrayList<>(Arrays.asList(chromosome.getGenes())))));
        Chromosome[] chromosomes = Arrays.stream(population).parallel().sorted(Chromosome::compare).toArray(Chromosome[]::new);
        System.arraycopy(chromosomes, 0, population, 0, chromosomes.length);
    }

    public Chromosome mutate(Chromosome chromosome) {
        Double[] genes = chromosome.getGenes();
        // Check if should mutate
        if (random.nextGaussian() <= MUTATION_RATE) {
            // Mutate child with new random values
            double mutationValue = random.nextGaussian() * MUTATION_MAGNITUDE;

            for (int i = 0; i < numDimensions; i++) {
                if (random.nextGaussian() <= MUTATION_RATE) {
                    genes[i] += mutationValue;
                }
            }
        }

        return chromosome;
    }

    public Chromosome crossover(Chromosome parent, Chromosome parent2) {
        Double[] genes = parent.getGenes();
        Double[] genes2 = parent2.getGenes();

        Double[] child = new Double[numDimensions];
        for (int i = 0; i < numDimensions; i++) {
            child[i] = (random.nextGaussian() > CROSSOVER_RATE) ? genes[i] : genes2[i];
        }
        return new Chromosome(child);
    }

    public void evolve(double maxPopPercentage) {
        int lastIndex = (int) Math.round(maxPopPercentage * population.length);

        Chromosome[] chromosomeSubset = Arrays.copyOfRange(population, 0, lastIndex);
        List<Chromosome> selectedList = new CopyOnWriteArrayList<>();


        int startIndex = 0;
        if (ELITISM) {
            startIndex++;

            selectedList.add(population[0]);
        }

        IntStream.range(startIndex, population.size()).parallel().forEach(value -> {
            Chromosome firstParent = tournamentSelect(chromosomeSubset);
            Chromosome secondParent = tournamentSelect(chromosomeSubset);
            Chromosome crossover = crossover(firstParent, secondParent);
            Chromosome mutatedChild = mutate(crossover);

            selectedList.add(mutatedChild);
        });

        Chromosome[] newPop = selectedList.toArray(new Chromosome[0]);
        System.arraycopy(newPop, 0, population, 0, population.length);
    }

    public boolean terminate() {
        if (lowest == null) {
            lowest = population[0].clone();
        } else {
            if (lowest.getFitness() > population[0].getFitness())
                lowest = population[0].clone();
            currentLowest = population[0].clone();
        }

        boolean epoch_stop = (CURRENT_EPOCH >= MAX_EPOCH);
        boolean delta_stop = false;

        if (CURRENT_EPOCH > 1) {
            if ((currentLowest.getFitness() - lowest.getFitness()) / lowest.getFitness() <= FITNESS_DELTA_THRESHOLD)
                delta_stop = true;
        }

        return epoch_stop || delta_stop;
    }

    public void generatePopulation() {
        for (int i = 0; i < population.size(); i++) {
            Movements movement = new Movements();
            // generate each command with left/right values and add to movement.
            // all movements need to be added to the population
            // this is a controller, where there can be many.
        }
    }

    private Movements tournamentSelect() {
        Movements t = getPopulation[p];
        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            Chromosome c = selectedPopulation[pos];
            if (c.getFitness() < chromosome.getFitness())
                chromosome = c;
        }
        return chromosome;
    }
}
