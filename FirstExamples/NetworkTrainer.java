package FirstExamples;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static FirstExamples.NetworkUtil.*;

public class NetworkTrainer implements Runnable {

    private String nodesFilePath;
    private int numTimesToRun;
    private double variability;
    private double bestScore = Double.MAX_VALUE;    // note that this is a 'golf' score (lower is better)
    private ArrayList<ArrayList<Nodes>> network;
    private ArrayList<ArrayList<Double>> trainingData;

    // used to get already made networks
    public NetworkTrainer(String nodesFilePath, ArrayList<ArrayList<Double>> trainingData, int numTimesToRun, int variability) {
        network = readNodesFromFile(nodesFilePath);
        this.trainingData = trainingData;
        this.numTimesToRun = numTimesToRun;
        this.variability = variability;
    }

    // used to make a new network, premakes the correct amount of nodes for data (assuming that the solution col exists in data)
    public NetworkTrainer(String newNetworkFilePath, String trainingDataPath, int[] nodeHeights) {
        trainingData = getTrainingData(trainingDataPath);

        network = new ArrayList<>();

        try {
            Scanner scnr = new Scanner(new File(trainingDataPath));
            int numDataNodes = scnr.nextLine().split(",").length;
            network.add(initNodes(numDataNodes - 1, 0));
            scnr.close();
            for (int i = 0; i < nodeHeights.length; i++) {
                if (i != 0) {
                    network.add(initNodes(nodeHeights[i], nodeHeights[i - 1]));
                } else {
                    network.add(initNodes(nodeHeights[i], numDataNodes));
                }
            }
            
            saveNodes(network, newNetworkFilePath);
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

        double lowestScore = networkGolfScore(network, trainingData);

        System.out.println(lowestScore);
        System.out.println(Double.MAX_VALUE);
        
        for (int i = 0; i < numTimesToRun; i++) {

            // Then make random changes
            // then save if new low score
            // then loop

        }

        // TODO: make sure there is an extra 'bias' in each node so that they can be offset

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
    

    public ArrayList<ArrayList<Nodes>> getNetwork() {
        return network;
    }

}
