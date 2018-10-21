package MotionSimulatorPackage;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class TimedCommand {
    Command c;
    int time;

    public TimedCommand(Command cin, int tin) {
        this.c = cin;
        this.time = tin;
    }

    public Command getC() {
        return c;
    }

    public int getTime() {
        return time;
    }

    public void setC(Command c) {
        this.c = c;
    }

    public void setTime(int time) {
        this.time = time;
    }

}
