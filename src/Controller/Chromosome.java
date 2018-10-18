package Controller;

import java.util.List;

public interface Chromosome<T> {
    double getFitness();
    void setFitness(double fitness);
    List<T> getPopulation();
    void setPopulation(List<T> popList);

    T getIndividual(int index);
    void setPopulation(T individual, int index);
}
