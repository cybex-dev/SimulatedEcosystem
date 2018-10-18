package Controller;

import MotionSimulator.Command;

import java.util.ArrayList;
import java.util.List;

public class Movements {
    private ArrayList<Command> commands = new ArrayList<>();
    private Double fitness;

    public Movements(List<Command> commands) {
        this.commands = new ArrayList<>(commands);
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = new ArrayList<>(commands);
    }

    public Command getCommand(int index) {
        return commands.get(index);
    }

    public void setCommand(Command command, int index) {
        this.commands.set(index, command);
    }
}