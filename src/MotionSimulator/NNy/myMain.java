//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package MotionSimulator.NNy;

import java.util.Vector;

public class myMain {
    int numhidden = 10;
    int numprod = 5;
    int numinput = 4;
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
        Vector tempy = new Vector();
        tempy.add(0.8196518124392127D);
        tempy.add(0.7029175569831372D);
        tempy.add(0.1574122149041819D);
        tempy.add(-0.11800658982415273D);
        tempy.add(-0.08159878744874342D);
        tempy.add(-0.003353328007313801D);
        tempy.add(-0.22966349163031569D);
        tempy.add(-0.12523085940727352D);
        tempy.add(0.024591471057153976D);
        tempy.add(0.3162216823594436D);
        tempy.add(-10.154392795783805D);
        tempy.add(-0.15911061533478363D);
        tempy.add(-0.09829927191745337D);
        tempy.add(0.0344641694186646D);
        tempy.add(1.1540404285286747D);
        tempy.add(-0.07577787487903084D);
        tempy.add(-1.0018551533553914D);
        tempy.add(-0.0421224964924873D);
        tempy.add(-0.11444726226798124D);
        tempy.add(0.1405713009974292D);
        tempy.add(0.24466243707615434D);
        tempy.add(18.502713898896907D);
        tempy.add(-0.21762746686544585D);
        tempy.add(0.4386096833576023D);
        tempy.add(0.09335247271730396D);
        tempy.add(-0.0470590143095547D);
        tempy.add(-0.018114406723735817D);
        tempy.add(-0.16137734051003239D);
        tempy.add(-0.04438929299980152D);
        tempy.add(0.17957515424997061D);
        tempy.add(0.004655526358941717D);
        tempy.add(0.6803076643685336D);
        tempy.add(-5.044314037746849D);
        tempy.add(-0.023253381460489296D);
        tempy.add(-0.07456353218537085D);
        tempy.add(-0.06549604791739269D);
        tempy.add(0.6302926419884531D);
        tempy.add(0.022121598227325453D);
        tempy.add(1.2357051141519213D);
        tempy.add(-0.07424921998919094D);
        tempy.add(-0.013391599995396523D);
        tempy.add(-0.02375832350633219D);
        tempy.add(-0.3388593027098597D);
        tempy.add(-0.8111722609886359D);
        tempy.add(1.2082121137918864D);
        tempy.add(-1.8803118349508667D);
        tempy.add(-0.24401510503541488D);
        tempy.add(2.0052909257117126D);
        tempy.add(0.1158073806938582D);
        tempy.add(-2.6085583527037133D);
        tempy.add(0.23193799668465817D);
        tempy.add(-2.048406653034066D);
        tempy.add(0.2356830249576716D);
        tempy.add(1.461400135761554D);
        tempy.add(-4.284902963237663D);
        tempy.add(-0.7278228377294358D);
        tempy.add(0.6352593045534831D);
        tempy.add(-4.827460179832922D);
        tempy.add(0.06478113553447656D);
        tempy.add(-8.498699265716214D);
        tempy.add(-0.03542208427768565D);
        tempy.add(-1.1480213426567631D);
        tempy.add(0.6452296157768583D);
        tempy.add(3.7897476196183533D);
        tempy.add(-0.13235616093023167D);
        tempy.add(-0.7503967667194189D);
        Chromosome thisc = new Chromosome(this);
        int index = 0;

        int i;
        int j;
        for(i = 0; i < this.numinput + 1; ++i) {
            for(j = 0; j < this.numhidden + 1; ++j) {
                thisc.weights1[i][j] = (Double)tempy.elementAt(index);
                ++index;
            }
        }

        for(i = 0; i < this.numhidden + 1; ++i) {
            for(j = 0; j < this.numoutput; ++j) {
                thisc.weights2[i][j] = (Double)tempy.elementAt(index);
                ++index;
            }
        }

        this.pop.add(thisc);
    }
}
