package src;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static src.NetworkUtil.*;

public class NetworkTrainer implements Runnable {

    private String nodesFilePath;
    private int AttemptsPerRound;
    private double variability;
    private double varaibilityChange;
    private double bestScore = Double.MAX_VALUE;    // note that this is a 'golf' score (lower is better)
    private ArrayList<ArrayList<Nodes>> network;
    private ArrayList<ArrayList<Double>> trainingData;

    // used to get already made networks
    public NetworkTrainer(String nodesFilePath, ArrayList<ArrayList<Double>> trainingData, int AttemptsPerRound, int variability, double varaibilityChange) {
        network = readNodesFromFile(nodesFilePath);
        this.trainingData = trainingData;
        this.AttemptsPerRound = AttemptsPerRound;
        this.variability = variability;
        this.varaibilityChange = varaibilityChange;
        this.nodesFilePath = nodesFilePath;
    }

    // used to make a new network, premakes the correct amount of nodes for data (assuming that the solution col exists in data)
    public NetworkTrainer(String newNetworkFilePath, String trainingDataPath, int[] nodeHeights) {
        trainingData = getTrainingData(trainingDataPath);

        network = new ArrayList<>();

        try {
            Scanner scnr = new Scanner(new File(trainingDataPath));
            int numDataNodes = scnr.nextLine().split(",").length - 1;
            network.add(initNodes(numDataNodes, 0));
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

    public void setAttemptsPerRound(int attemptsPerRound) {
        this.AttemptsPerRound = attemptsPerRound;
    }

    public void setVariability(double variability) {
        this.variability = variability;
    }

    public double getVariability() {
        return variability;
    }

    public ArrayList<ArrayList<Nodes>> getNetwork() {return network;}

    @Override
    public void run() {

        double lowestScore = networkGolfScore(network, trainingData);
        double lastScore = 0;

        ArrayList<ArrayList<Nodes>> networkCopy = copyNetwork(network);

        while (true) {

            for (int i = 0; i < AttemptsPerRound; i++) {

                // Then make random changes
                updateBiases(network, variability);
                // then save if new low score

                lastScore = networkGolfScore(network, trainingData);

                if (lowestScore > lastScore) {
                    lowestScore = lastScore;
                    networkCopy = copyNetwork(network);
                } else {
                    network = copyNetwork(networkCopy);
                }
            }

            String layoutString = "";
            for (int i = 1; i < network.size(); i++) {
                layoutString += network.get(i).size() + "-";
            }
            layoutString = layoutString.substring(0, layoutString.length() - 1);
            System.out.println(layoutString + ", " + lowestScore);
            saveNodes(networkCopy, nodesFilePath);
            variability /= varaibilityChange;
        }
    }
}
