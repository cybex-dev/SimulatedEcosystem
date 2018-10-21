package MotionSimulatorPackage;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import MotionSimulatorPackage.NNa.myMain;

import java.util.ArrayList;

public class MotionSimulator {
    private double[][] transform = new double[2][2];
    private double oldx = 0.0D;
    private myMain myA;
    private MotionSimulatorPackage.NNx.myMain myX;
    private MotionSimulatorPackage.NNy.myMain myY;

    public MotionSimulator() {
        this.buildNNs();
    }

    private void buildNNs() {
        this.myA = new  myMain();
        this.myX = new MotionSimulatorPackage.NNx.myMain();
        this.myY = new MotionSimulatorPackage.NNy.myMain();
    }

    private ArrayList<State> getStates(State curstate, TimedCommand prevcom, TimedCommand newcom) {
        ArrayList<State> ret = new ArrayList<>();

        State cur = new State(curstate.x, curstate.y, curstate.a);
        this.setInvTransform(cur.a - 90.0D);

        // Create previous and new commands
        Command prev = new Command(prevcom.c.left, prevcom.c.right);
        Command newc = new Command(newcom.c.left, newcom.c.right);

        // Create new state position and angle
        State pos = this.getNewXandY(cur, prev, newc);
        double ang = this.getNewA(cur, prev, newc);
        cur = new State(pos.x, pos.y, ang);
        // Add to list
        ret.add(new State(pos.x, pos.y, ang));

        // Set new previous command to current state
        prev = new Command(newc.left, newc.right);

        for(int i = 2; i <= newcom.time; ++i) {
            this.setInvTransform(cur.a - 90.0D);
            pos = this.getNewXandY(cur, prev, newc);
            ang = this.getNewA(cur, prev, newc);
            cur = new State(pos.x, pos.y, ang);
            ret.add(new State(pos.x, pos.y, ang));
        }

        return ret;
    }

    /**
     * Returns the path travelled by the drone/robot.
     * @param initial initial state
     * @param coms list of commands
     * @return list of complete states
     */
    public ArrayList<State> getPath(State initial, ArrayList<TimedCommand> coms) {
        this.oldx = 0.0D;
        ArrayList<State> ret = new ArrayList<>();
        ret.add(initial);
        State curState = initial;
        TimedCommand prevcom = new TimedCommand(new Command(0, 0), 1);

        for (TimedCommand com : coms) {
            ArrayList<State> newStates = this.getStates(curState, prevcom, com);

            ret.addAll(newStates);

            State finals = newStates.get(newStates.size() - 1);
            curState = new State(finals.x, finals.y, finals.a);
            prevcom = new TimedCommand(new Command(com.c.left, com.c.right), com.time);
        }

        return ret;
    }

    private void setInvTransform(double ang) {
        double rad = 0.017453292519943295D * ang;
        this.transform[0][0] = Math.cos(rad);
        this.transform[1][1] = Math.cos(rad);
        this.transform[0][1] = -Math.sin(rad);
        this.transform[1][0] = Math.sin(rad);
    }

    private State getNewXandY(State curstate, Command prevcom, Command newcom) {
        double[] in = new double[]{(double)(newcom.left + 701) / 100.0D, (double)(newcom.right + 701) / 100.0D, (double)(prevcom.left + 701) / 100.0D, (double)(prevcom.right + 701) / 100.0D, this.oldx + 15.0D};
        double[] out = this.myX.getOutput(in);
        double localx = out[0];
        this.oldx = localx;
        double[] iny = new double[]{(double)(newcom.left + 701) / 100.0D, (double)(newcom.right + 701) / 100.0D, (double)(prevcom.left + 701) / 100.0D, (double)(prevcom.right + 701) / 100.0D};
        double[] outy = this.myY.getOutput(iny);
        double localy = outy[0];
        double dxprime = this.transform[0][0] * localx + this.transform[0][1] * localy;
        double dyprime = this.transform[1][0] * localx + this.transform[1][1] * localy;
        return new State(curstate.x + dxprime, curstate.y + dyprime, -1.0D);
    }

    private double getNewA(State curstate, Command prevcom, Command newcom) {
        double[] in = new double[]{(double)(newcom.left + 701) / 100.0D, (double)(newcom.right + 701) / 100.0D, (double)(prevcom.left + 701) / 100.0D, (double)(prevcom.right + 701) / 100.0D};
        double[] out = this.myA.getOutput(in);
        double globa = curstate.a + out[1] * 100.0D;
        if (globa < 0.0D) {
            globa += 360.0D;
        }

        if (globa >= 360.0D) {
            globa -= 360.0D;
        }

        return globa;
    }
}
