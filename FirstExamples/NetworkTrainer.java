package FirstExamples;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static FirstExamples.StructureCreation.*;

public class NetworkTrainer implements Runnable {

    private String nodesFilePath;
    private int numTimesToRun;
    private double variability;
    private ArrayList<ArrayList<Nodes>> network;
    private ArrayList<ArrayList<Double>> trainingData;

    // used to get already made networks
    public NetworkTrainer(String nodesFilePath, String trainingDataPath, int numTimesToRun, int variability) {
        network = readNodesFromFile(nodesFilePath);
        trainingData = getTrainingData(trainingDataPath);
        this.numTimesToRun = numTimesToRun;
        this.variability = variability;
    }

    // used to make a new networks
    public NetworkTrainer(String newNetworkFilePath, String trainingDataPath, int[] nodeHeights, int numTimesToRun, int variability) {
        trainingData = getTrainingData(trainingDataPath);
        this.numTimesToRun = numTimesToRun;
        this.variability = variability;

        network = new ArrayList<>();

        try {
            Scanner scnr = new Scanner(new File(trainingDataPath));
            int numDataNodes = scnr.nextLine().split(",").length;
            network.add(initNodes(numDataNodes, 0));
            scnr.close();
            for (int i = 0; i < nodeHeights.length; i++) {
                if (i != 0) {
                    network.add(initNodes(nodeHeights[i], nodeHeights[i - 1]));
                } else {
                    network.add(initNodes(nodeHeights[i], numDataNodes));
                }
            }

        } catch (Exception e) {
            System.err.println("error initalizing the new network");
        }
    }

    public void setNumTimesToRun(int numTimesToRun) {
        this.numTimesToRun = numTimesToRun;
    }

    public void setVariability(double variability) {
        this.variability = variability;
    }

    @Override
    public void run() {

        System.out.println("cheese");

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
    


}
