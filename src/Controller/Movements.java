package Controller;

import MotionSimulatorPackage.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Movements implements Chromosome<TimedCommand> {
    private ArrayList<TimedCommand> commands = new ArrayList<>();
    private Double fitness;
    private ArrayList<State> lastState = new ArrayList<>();
    private double yOffset, xOffset;

    Movements() {}

    private Movements(List<TimedCommand> commands, Double fitness) {
        this.commands = new ArrayList<>(commands);
        this.fitness = fitness;
    }

    Movements(List<TimedCommand> commands) {
        this.commands = new ArrayList<>(commands);
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

    Movements copy() {
        Movements m = new Movements(new ArrayList<>(commands), fitness);
        m.setLastState(new ArrayList<>(lastState));
        m.setyOffset(yOffset);
        m.setxOffset(xOffset);
        return m;
    }

    public ArrayList<State> getLastState() {
        return lastState;
    }

    public void setLastState(ArrayList<State> lastState) {
        this.lastState = lastState;
    }

    @Override
    public String toString() {
        State firstPos = (lastState.size() == 0) ? new State(0, 0, 0) : lastState.get(0);
        State lastPos = (lastState.size() == 0) ? new State(0, 0, 0) : lastState.get(lastState.size() - 1);
        return "[" + firstPos.getX() + ", " + firstPos.getY() + "] => [" + lastPos.getX() + ", " + lastPos.getY() + "]" + "{" + "Fit =" + fitness + ", HASH = " + this.hashCode() + '}';
    }

    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public static Movements comparatorWorst(Movements movements, Movements movements1) {
        return compare(movements, movements1) > -1 ? movements : movements1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movements)) return false;
        Movements movements = (Movements) o;
        return Double.compare(movements.yOffset, yOffset) == 0 &&
                Double.compare(movements.xOffset, xOffset) == 0 &&
                Objects.deepEquals(commands, movements.commands) &&
                Objects.equals(fitness, movements.fitness) &&
                Objects.deepEquals(lastState, movements.lastState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commands, fitness, lastState, yOffset, xOffset);
    }
}
