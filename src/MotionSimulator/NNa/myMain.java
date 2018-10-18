//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package MotionSimulator.NNa;

import java.util.Vector;

public class myMain {
    int numhidden = 10;
    int numprod = 5;
    int numinput = 4;
    int numoutput = 2;
    Vector pop = new Vector();
    int numchrom = 1;
    int numelite = 0;
    int tsize = 1;
    boolean run = true;

    public myMain() {
        this.buildPop();
    }

    public double[] getOutput(double[] in) {
        return ((Chromosome)this.pop.elementAt(0)).getOutput(in);
    }

    public void buildPop() {
        this.pop.clear();
        Vector tempa = new Vector();
        tempa.add(-0.04229582507979141D);
        tempa.add(-0.02862485681190653D);
        tempa.add(-0.1796614673230843D);
        tempa.add(-0.19735009325832553D);
        tempa.add(0.34565701980736047D);
        tempa.add(0.2909919956822115D);
        tempa.add(0.2316180782293202D);
        tempa.add(0.10469543298366243D);
        tempa.add(-0.014394534116343088D);
        tempa.add(0.20327370153791038D);
        tempa.add(-0.14184394156412056D);
        tempa.add(0.09344294278839693D);
        tempa.add(0.15387005866070164D);
        tempa.add(-0.08396437822562791D);
        tempa.add(0.13187163848050173D);
        tempa.add(-0.14662898410550695D);
        tempa.add(-0.3057831170949613D);
        tempa.add(-0.08678530439030592D);
        tempa.add(-0.004963067830277039D);
        tempa.add(-0.07866334088003779D);
        tempa.add(-0.06332807706835442D);
        tempa.add(0.049930076287457416D);
        tempa.add(-0.023858730868476688D);
        tempa.add(0.00474071506644784D);
        tempa.add(0.19568807015879855D);
        tempa.add(0.11173217607363974D);
        tempa.add(-0.3207041712975932D);
        tempa.add(0.1077559122991214D);
        tempa.add(0.022098597570276803D);
        tempa.add(-0.05442216051204262D);
        tempa.add(-0.019222329837230483D);
        tempa.add(0.14614685574772793D);
        tempa.add(-0.04711405611369972D);
        tempa.add(0.01968123149973496D);
        tempa.add(-0.31065286595003866D);
        tempa.add(-0.03698663924550069D);
        tempa.add(0.018162990494097205D);
        tempa.add(-0.15916530435090065D);
        tempa.add(-0.09073233040009876D);
        tempa.add(0.01796257060109615D);
        tempa.add(-0.015364270608399655D);
        tempa.add(0.06590254560933556D);
        tempa.add(0.1407676707994543D);
        tempa.add(0.059425090702989646D);
        tempa.add(0.1577177850870703D);
        tempa.add(-0.03428880834305382D);
        tempa.add(-0.06793412147703157D);
        tempa.add(-0.1520973469087311D);
        tempa.add(0.13023173446379274D);
        tempa.add(0.029025061257873547D);
        tempa.add(-0.10578333243827541D);
        tempa.add(-0.020003838870626844D);
        tempa.add(0.07056334878375452D);
        tempa.add(0.09014937496941496D);
        tempa.add(-0.05812258355950434D);
        tempa.add(-0.01232723101223171D);
        tempa.add(0.025914740552418693D);
        tempa.add(-0.03356636564642486D);
        tempa.add(-0.016300162850941314D);
        tempa.add(-0.008290031171113368D);
        tempa.add(3.5576784990830936E-4D);
        tempa.add(0.030490157970246387D);
        tempa.add(0.0027475949694077463D);
        tempa.add(0.0246326104313047D);
        tempa.add(0.023267247444186758D);
        tempa.add(-0.13039442541140267D);
        tempa.add(-0.16307784395297742D);
        tempa.add(0.019901810811747685D);
        tempa.add(0.015049667396098766D);
        tempa.add(0.012969231554129834D);
        tempa.add(-0.02350802660039361D);
        tempa.add(0.03455788865301714D);
        tempa.add(0.004984519899494895D);
        tempa.add(-0.006240506694621205D);
        tempa.add(-0.0040504703039808895D);
        tempa.add(0.0014913252851774786D);
        tempa.add(0.027392866948212524D);
        int index = 0;
        Chromosome thisc = new Chromosome(this);

        int i;
        int j;
        for(i = 0; i < this.numinput + 1; ++i) {
            for(j = 0; j < this.numhidden + 1; ++j) {
                thisc.weights1[i][j] = (Double)tempa.elementAt(index);
                ++index;
            }
        }

        for(i = 0; i < this.numhidden + 1; ++i) {
            for(j = 0; j < this.numoutput; ++j) {
                thisc.weights2[i][j] = (Double)tempa.elementAt(index);
                ++index;
            }
        }

        this.pop.add(thisc);
    }
}
