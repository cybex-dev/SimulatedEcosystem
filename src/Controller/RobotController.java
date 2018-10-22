package Controller;

import MotionSimulatorPackage.MotionSimulator;
import MotionSimulatorPackage.State;
import MotionSimulatorPackage.TimedCommand;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class RobotController {
    private static final State STATE_INITIAL = new State(0, 0, 0);
    int max_gen;
    int pop_size;
    int tSize;
    double xOver;
    double mMag;
    double mRate;
    private GeneticAlgorithm geneticAlgorithm;
    private MotionSimulator simulator;
    // Logging
    private PrintWriter writer;
    private String CARAT = ";";
    private String ID = "default";

    public RobotController(int max_gen, int pop_size, int tSize, double xOver, double mMag, double mRate) {
        this.max_gen = max_gen;
        this.pop_size = pop_size;
        this.tSize = tSize;
        this.xOver = xOver;
        this.mMag = mMag;
        this.mRate = mRate;
        simulator = new MotionSimulator();
        geneticAlgorithm = new GeneticAlgorithm(max_gen, pop_size, tSize, xOver, mMag, mRate);
        ID = "max_" + max_gen + "_pop" + pop_size + "_tour" + tSize + "_xover" + xOver + "_mag" + mMag + "_rate" + mRate;
    }

    void train() {
        geneticAlgorithm.setID(ID);
        geneticAlgorithm.run();

        try {
            File logger = new File("results//controller_" + ID + ".csv");
            if (logger.exists())
                logger.delete();
            logger.createNewFile();

            writer = new PrintWriter(logger);

            List<Movements> bestResults = geneticAlgorithm.getBestResults();

            File file = new File("results");
            file.mkdirs();

            // Write details
            StringBuilder builder = new StringBuilder();
            builder.append("POP").append(CARAT).append(geneticAlgorithm.getPop_size()).append(CARAT).append("\n")
                    .append("MAX_EPOCH").append(CARAT).append(geneticAlgorithm.getMAX_EPOCH()).append(CARAT).append("\n")
                    .append("tour Size").append(CARAT).append(geneticAlgorithm.getTOURNAMENT_SIZE()).append(CARAT).append("\n")
                    .append("X Rate").append(CARAT).append(geneticAlgorithm.getCROSSOVER_RATE()).append(CARAT).append("\n")
                    .append("mut Rate").append(CARAT).append(geneticAlgorithm.getMUTATION_RATE()).append(CARAT).append("\n")
                    .append("mut Mag").append(CARAT).append(geneticAlgorithm.getMUTATION_MAGNITUDE()).append(CARAT).append("\n\n");

            // Write fitness; x; y
            IntStream.range(0, bestResults.size()).forEach(value -> {
                Movements movements = bestResults.get(value);
                State state = movements.getLastState().get(movements.getLastState().size() - 1);
                builder.append(String.valueOf(movements.getFitness())).append(CARAT).append(state.getX()).append(CARAT).append(state.getY()).append(CARAT).append("\n");
            });
            builder.append("\n");

            // Write all last states to file
            bestResults.get(bestResults.size() - 1).getLastState().forEach(state -> {
                builder.append(state.getX()).append(CARAT).append(state.getY()).append(CARAT).append("\n");
            });

            writer.write(builder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write results");
        }

//        VisualFrame frame = new VisualFrame(getBestSolutionStates());
//        frame.setTitle(ID);
//        frame.run();
//        BufferedImage image = frame.getImage();
//        frame.hide();
//        try {
//            ImageIO.write(image, "png", new File("results//plot" + ID + ".png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * Gets the best solution of the algorithm
     *
     * @return best movement / /individual
     */
    private Movements getBestSolution() {
        return geneticAlgorithm.getLowest();
    }

    /**
     * Returns the corresponding state list of the best solution
     *
     * @return best individual states
     */
    ArrayList<State> getBestSolutionStates() {
        Movements bestSolution = getBestSolution();
        ArrayList<State> lastState = bestSolution.getLastState();
        List<TimedCommand> population = getBestSolution().getPopulation();
        ArrayList<State> path = simulator.getPath(STATE_INITIAL, new ArrayList<>(population));
        return lastState;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


}
