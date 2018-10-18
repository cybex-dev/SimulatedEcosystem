//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package MotionSimulator.NNx;

import java.util.Vector;

public class myMain {
    boolean realrec = true;
    int numhidden = 10;
    int numprod = 5;
    int numinput = 5;
    int numoutput = 1;
    Vector pop = new Vector();
    int numchrom = 1;
    int numelite = 0;
    int tsize = 1;

    public myMain() {
        this.buildPop();
    }

    public double[] getOutput(double[] in) {
        return ((Chromosome)this.pop.elementAt(0)).getOutput(in);
    }

    public void buildPop() {
        this.pop.clear();
        Vector tempx = new Vector();
        tempx.add(0.7161216233024887D);
        tempx.add(-0.15132760213806512D);
        tempx.add(0.09655493698650466D);
        tempx.add(-0.12284160207544381D);
        tempx.add(0.15061590164122535D);
        tempx.add(0.005441492694240999D);
        tempx.add(0.5086857451099421D);
        tempx.add(3.6733632156928806D);
        tempx.add(-0.5186087127619544D);
        tempx.add(0.08690467812950242D);
        tempx.add(0.5510674240138058D);
        tempx.add(-0.0574111574760201D);
        tempx.add(-0.07704212831080405D);
        tempx.add(0.4253379201767894D);
        tempx.add(-0.10055644523736726D);
        tempx.add(0.10766082836835042D);
        tempx.add(-0.24776529853960794D);
        tempx.add(-0.5189875258494355D);
        tempx.add(0.4008077409923147D);
        tempx.add(0.200940627585603D);
        tempx.add(-0.11978367220552447D);
        tempx.add(-0.22551056886360302D);
        tempx.add(-0.05504251078211876D);
        tempx.add(-0.005304148383945493D);
        tempx.add(-0.003055900902009842D);
        tempx.add(0.844197064934076D);
        tempx.add(1.0516544750340917D);
        tempx.add(0.10472165625154438D);
        tempx.add(-0.21142847189099703D);
        tempx.add(-5.774068006473334D);
        tempx.add(0.8652142690047979D);
        tempx.add(0.6737725802138846D);
        tempx.add(-0.8607927367812811D);
        tempx.add(-0.06652937681264201D);
        tempx.add(0.7165917960828068D);
        tempx.add(0.1481896004873071D);
        tempx.add(-0.05236405607151062D);
        tempx.add(-0.0547210180037796D);
        tempx.add(-0.04874566652415274D);
        tempx.add(0.37136958622855665D);
        tempx.add(0.2988951560804402D);
        tempx.add(-0.4742113415482617D);
        tempx.add(-0.2625568561377084D);
        tempx.add(-0.5745669801622442D);
        tempx.add(0.013128700092668259D);
        tempx.add(0.02779562019719814D);
        tempx.add(-0.09104431434143087D);
        tempx.add(0.1797217138006525D);
        tempx.add(0.026627467033803177D);
        tempx.add(0.09033276248755448D);
        tempx.add(0.12385036049523704D);
        tempx.add(0.8982277793722719D);
        tempx.add(0.7245897622376499D);
        tempx.add(0.2070133897988874D);
        tempx.add(-0.7751442333642613D);
        tempx.add(0.07329186958468452D);
        tempx.add(-0.31257014138434885D);
        tempx.add(1.4233481784370154D);
        tempx.add(-0.4574064019681462D);
        tempx.add(0.03388550798475362D);
        tempx.add(-0.20987940840238223D);
        tempx.add(0.5121201742901295D);
        tempx.add(-0.3131419751552176D);
        tempx.add(0.7141381379141447D);
        tempx.add(0.009445044340019176D);
        tempx.add(1.1928849136500208D);
        tempx.add(0.8541812918351942D);
        tempx.add(0.8510010455359757D);
        tempx.add(0.23806304993984984D);
        tempx.add(-0.32772276017525787D);
        tempx.add(0.10239465768999736D);
        tempx.add(1.173300325417626D);
        tempx.add(-0.4693121091278298D);
        tempx.add(-0.002897115956686548D);
        tempx.add(0.09195741361405732D);
        tempx.add(-0.421338270469102D);
        tempx.add(0.02931039926706866D);
        Chromosome thisc = new Chromosome(this);
        int index = 0;

        int i;
        int j;
        for(i = 0; i < this.numinput + 1; ++i) {
            for(j = 0; j < this.numhidden + 1; ++j) {
                thisc.weights1[i][j] = (Double)tempx.elementAt(index);
                ++index;
            }
        }

        for(i = 0; i < this.numhidden + 1; ++i) {
            for(j = 0; j < this.numoutput; ++j) {
                thisc.weights2[i][j] = (Double)tempx.elementAt(index);
                ++index;
            }
        }

        this.pop.add(thisc);
    }
}
