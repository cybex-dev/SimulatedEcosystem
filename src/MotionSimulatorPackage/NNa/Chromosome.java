//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package MotionSimulatorPackage.NNa;

import java.io.Serializable;
import java.util.Random;

public class Chromosome implements Serializable {
    Random myRandom = new Random();
    myMain calling;
    int numinput;
    int numhidden;
    int numprod;
    int numoutput;
    double[][] weights1;
    double[][] weights2;
    double fitness = 0.0D;

    public Chromosome(myMain in) {
        this.calling = in;
        this.numinput = this.calling.numinput;
        this.numhidden = this.calling.numhidden;
        this.numprod = this.calling.numprod;
        this.numoutput = this.calling.numoutput;
        this.weights1 = new double[this.numinput + 1][this.numhidden + 1];
        this.weights2 = new double[this.numhidden + 1][this.numoutput];
    }

    public Chromosome(boolean random, myMain in) {
        this.calling = in;
        this.numinput = this.calling.numinput;
        this.numhidden = this.calling.numhidden;
        this.numprod = this.calling.numprod;
        this.numoutput = this.calling.numoutput;
        this.weights1 = new double[this.numinput + 1][this.numhidden + 1];
        this.weights2 = new double[this.numhidden + 1][this.numoutput];

        int i;
        int j;
        for(i = 0; i < this.numinput + 1; ++i) {
            for(j = 0; j < this.numprod; ++j) {
                this.weights1[i][j] = this.genRandom(-15.0D, 15.0D);
            }
        }

        for(i = 0; i < this.numinput + 1; ++i) {
            for(j = this.numprod; j < this.numhidden + 1; ++j) {
                this.weights1[i][j] = this.genRandom(-15.0D, 15.0D);
            }
        }

        for(i = 0; i < this.numhidden + 1; ++i) {
            for(j = 0; j < this.numoutput; ++j) {
                this.weights2[i][j] = this.genRandom(-15.0D, 15.0D);
            }
        }

    }

    public Chromosome cloneMe() {
        Chromosome ret = new Chromosome(this.calling);
        ret.fitness = this.fitness;
        ret.weights1 = (double[][])this.weights1.clone();
        ret.weights2 = (double[][])this.weights2.clone();
        return ret;
    }

    public double activationFunc1(double x) {
        return x;
    }

    public double activationFunc1Trig(double x) {
        return Math.cos(x);
    }

    public double activationFunc2(double x) {
        return x;
    }

    public double activationFunc2Trig(double x) {
        return Math.cos(x);
    }

    public double getHidden(int i, double[] input) {
        double sum;
        int j;
        if (i < this.numprod) {
            sum = 1.0D;

            for(j = 0; j < this.numinput; ++j) {
                sum *= Math.pow(input[j], this.weights1[j][i]);
            }

            sum += -1.0D * this.weights1[this.numinput][i];
            return this.activationFunc1(sum);
        } else if (i >= this.numhidden) {
            return -1.0D;
        } else {
            sum = 0.0D;

            for(j = 0; j < this.numinput; ++j) {
                sum += input[j] * this.weights1[j][i];
            }

            sum += -1.0D * this.weights1[this.numinput][i];
            return this.activationFunc2(sum);
        }
    }

    public double[] getOutput(double[] input) {
        double[] output = new double[this.numoutput];

        for(int i = 0; i < this.numoutput; ++i) {
            double sum = 0.0D;

            for(int j = 0; j < this.numhidden + 1; ++j) {
                sum += this.getHidden(j, input) * this.weights2[j][i];
            }

            output[i] = this.activationFunc2(sum);
        }

        return output;
    }

    public double genRandom(double min, double max) {
        return this.myRandom.nextDouble() * (max - min) + min;
    }
}
