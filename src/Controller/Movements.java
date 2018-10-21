package Controller;

import MotionSimulatorPackage.State;
import MotionSimulatorPackage.TimedCommand;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Movements implements Chromosome<TimedCommand> {
    private ArrayList<TimedCommand> commands = new ArrayList<>();
    private Double fitness;
    private ArrayList<State> lastState = new ArrayList<>();

    private double yOffset, xOffset;

    Movements() {
    }

    private Movements(List<TimedCommand> commands, Double fitness) {
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
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < lastState.size(); i++) {
//            builder.append(commands.get(i).toString()).append("; ").append(lastState.get(i).toString()).append(";\n");
//        }
        State firstPos = (lastState.size() == 0) ? new State(0, 0, 0) : lastState.get(0);
        State lastPos = (lastState.size() == 0) ? new State(0, 0, 0) : lastState.get(lastState.size() - 1);

        return
                "[" +
                        firstPos.getX() +
                        ", " + firstPos.getY() +
                        "] => [" +
                        lastPos.getX() +
                        ", " + lastPos.getY() +
                        "]" +
                        "{" +
                        "Fit =" + fitness +
                        ", HASH = " + this.hashCode() +
                        '}';
    }

    public String getLastXY() {
        State firstPos = (lastState.size() == 0) ? new State(0, 0, 0) : lastState.get(0);
        State lastPos = (lastState.size() == 0) ? new State(0, 0, 0) : lastState.get(lastState.size() - 1);
        return "[" +
                firstPos.getX() +
                ", " + firstPos.getY() +
                "] => [" +
                lastPos.getX() +
                ", " + lastPos.getY() +
                "]";
    }

    public double getyOffset() {
        return yOffset;
    }

    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public double getxOffset() {
        return xOffset;
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    /**
     * Single axis xoffset distance measurement
     *
     * @return distance to value
     */
    public double distanceTo0FromXOffset() {
        return Math.abs(xOffset) - 0.0;
    }

    /**
     * Single axis yoffset distance measurement
     *
     * @return distance to value
     */
    public double distanceTo0FromYOffset() {
        return Math.abs(yOffset) - 0.0;
    }
}
