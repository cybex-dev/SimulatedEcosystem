package Controller;

import MotionSimulatorPackage.TimedCommand;

import java.util.ArrayList;
import java.util.List;

public class Movements implements Chromosome<TimedCommand>{
    private ArrayList<TimedCommand> commands = new ArrayList<>();
    private Double fitness;

    Movements() {
    }

    private Movements(List<TimedCommand> commands, Double fitness){
        this.commands = new ArrayList<>(commands);
        this.fitness = fitness;
    }

    Movements(List<TimedCommand> commands) {
        this.commands = new ArrayList<>(commands);
    }

    public TimedCommand getCommand(int index) {
        return commands.get(index);
    }

    public void setCommand(TimedCommand command, int index) {
        this.commands.set(index, command);
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public List<TimedCommand> getPopulation() {
        return commands;
    }

    @Override
    public void setPopulation(List<TimedCommand> popList) {
        this.commands = new ArrayList<>(popList);
    }

    @Override
    public TimedCommand getIndividual(int index) {
        return commands.get(index);
    }

    @Override
    public void setPopulation(TimedCommand individual, int index) {
        this.commands.add(index, individual);
    }

    void addCommand(TimedCommand timedCommand) {
        commands.add(timedCommand);
    }

    static int compare(Movements movements, Movements movements1) {
        return Double.compare(movements.getFitness(), movements1.getFitness());
    }

    Movements copy(){
        return new Movements(new ArrayList<>(commands), fitness);
    }
}
