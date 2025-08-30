package FirstExamples;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class NetworkUtil {


    /**
     * This will create the nodes in a column
     * @param nodes
     * @param dataNodes
     * @param middleNodes
     * @return
     */
    public static ArrayList<Nodes> initNodes(int numNewNodes, int numPreviousNodes) {

        ArrayList<Nodes> nodes = new ArrayList<>(numNewNodes); 
        Random rand = new Random();

        for (int i = 0; i  < numNewNodes; i++) {
            ArrayList<Double> bias = new ArrayList<>(numPreviousNodes);
            for (int k = 0; k < numPreviousNodes; k++) {
                bias.add(rand.nextDouble() * 1000 * (rand.nextBoolean() ? -1 : 1));
            }
            if (numPreviousNodes > 0) {
                bias.add(rand.nextDouble() * 1000); // this is to add a offset for the node
            }
            nodes.add(new Nodes(bias));
        }
        return nodes;
    }


    /**
     * This will save the current nodes to a file
     */
    public static boolean saveNodes(ArrayList<ArrayList<Nodes>> nodes, String relativeFilePath) {

        try {
            FileWriter fileWriter = new FileWriter(relativeFilePath, false);
            String concatBiases;

            for (int i = 0; i < nodes.size(); i++) {
                if (i != 0) {
                    fileWriter.write("\n");
                }

                for (int k = 0; k < nodes.get(i).size(); k++) {

                    concatBiases = "n";
                    for (Double d : nodes.get(i).get(k).getBias()) {
                        concatBiases += d + "_";
                    }
                    if (concatBiases.length() > 1) {
                        concatBiases = concatBiases.substring(0, concatBiases.length() - 1);
                    }
                    fileWriter.write(concatBiases + " ");
                }
            }

            fileWriter.close();
            return true;
        } catch (Exception e) {
            System.err.println("Could not save to filepath: " + relativeFilePath);
            return false;
        }
    }


    public static ArrayList<ArrayList<Nodes>> readNodesFromFile(String relativeFilePath) {

        ArrayList<ArrayList<Nodes>> readNodes = new ArrayList<>();
        ArrayList<Nodes> column;
        ArrayList<Double> bias;

        try {
            
            Scanner scnr = new Scanner(new File(relativeFilePath));
            while (scnr.hasNextLine()) {
                String[] nodeStrs = scnr.nextLine().split(" ");
                column = new ArrayList<>();
                readNodes.add(column);

                for (String  nodeString : nodeStrs) {
                    nodeString = nodeString.substring(1);
                    bias = new ArrayList<>();
                    
                    for (String biasStr : nodeString.split("_")) {
                        if (biasStr.length() > 0) {
                            bias.add(Double.parseDouble(biasStr));
                        }
                    }
                    column.add(new Nodes(bias));
                }            
            }
            scnr.close();
            return readNodes;

        } catch (Exception e) {
            System.err.println("Nodes could not be read from the file: " + relativeFilePath);
        }
        throw new RuntimeException("nodes not read correctly");
    }

    // This will fill the data nodes with new information from the relative file path
    public static void fillDataNodes(ArrayList<ArrayList<Nodes>> neuralNetwork, ArrayList<Double> dataWithAnswer) {
        for (int i = 0; i < dataWithAnswer.size() - 1; i++) {
            neuralNetwork.get(0).get(i).setData(dataWithAnswer.get(i));
        }
    }


    // evaluates network based on data currently in nodes, returning only the first element in the last column
    public static Double runNetwork(ArrayList<ArrayList<Nodes>> network) {
    
        double runningDataVal;

        for (int i = 1; i < network.size(); i++) {  // col
            for (Nodes curNode : network.get(i)) {   // row
                runningDataVal = 0;

                for (int k = 0; k < curNode.getBias().size(); k++) {

                    try {
                        runningDataVal += curNode.getBias().get(k) * network.get(i - 1).get(k).getVal();
                    } catch (IndexOutOfBoundsException e) {
                        runningDataVal += curNode.getBias().get(k);
                    }

                }
                curNode.setData(runningDataVal);
            }
        }

        ArrayList<Double> results = new ArrayList<>();
        for (Nodes n : network.get(network.size() - 1)) {
            results.add(n.getVal());
        }

        return results.get(0);
    }


    public static ArrayList<ArrayList<Double>> getTrainingData(String dataFilePath) {

        ArrayList<ArrayList<Double>> dataVals = new ArrayList<>();
        ArrayList<Double> dataRow;

        try {
            
            Scanner scnr = new Scanner(new File(dataFilePath));
            scnr.nextLine();
            while (scnr.hasNextLine()) {
                dataRow = new ArrayList<>();
                String[] vals = scnr.nextLine().split(",");
                for (String str : vals) {
                    
                    try {                        
                        dataRow.add(Double.parseDouble(str));
                    } catch (NumberFormatException e) {

                        if (str.isEmpty()) {
                            dataRow.add(0d);
                        } else {
                            System.err.println("attempted to read string: " + str);
                        }
                    }


                }
                dataVals.add(dataRow);
            }

            return dataVals;

        } catch (Exception e) {
            System.err.println("Failed to read file: " + dataFilePath);    
        }
        throw new RuntimeException("Data was not read properly");
    }

    public static void updateBiases(ArrayList<ArrayList<Nodes>> network, double variability) {
        
        Random rand = new Random();
        ArrayList<Double> arrBias;

        for (int i = 1; i < network.size(); i++){
            
            for (Nodes node : network.get(i)) {
                arrBias = node.getBias();
                for (int j = 0; j < arrBias.size(); j++) {
                    // TODO: make sure that this provides a good change
                    arrBias.set(j, arrBias.get(j) + (variability * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1)));
                }
            }
            
        }


    }

    // scores the network based on the data and correct answer (which is the last element of the array)
    public static double networkGolfScore(ArrayList<ArrayList<Nodes>> network, ArrayList<ArrayList<Double>> trainingData) {

        double score = Double.MIN_VALUE;

        for (ArrayList<Double> dataSet : trainingData) {
            fillDataNodes(network, dataSet);
            score += Math.pow(Math.abs(runNetwork(network) - dataSet.get(dataSet.size() - 1)), 0.1);
        }

        return score;
    }


    // public static void main(String[] args) {

    //     ArrayList<ArrayList<Nodes>> neuralNetwork = new ArrayList<>();

    //     int[] columnHeights = {10,8,8,5,1}; // This is the column heights of the network

    //     neuralNetwork.add(initNodes(columnHeights[0], 0));

    //     for (int i = 1; i < columnHeights.length; i++) {
    //         neuralNetwork.add(initNodes(columnHeights[i], columnHeights[i - 1]));
    //     }

    //     saveNodes(neuralNetwork, "./NodeBiases/test0.txt");        
        
        
    //     // ArrayList<ArrayList<Nodes>> neuralNetwork = readNodesFromFile("./NodeBiases/test0.txt");


    //     fillDataNodes(neuralNetwork, "./Data/trainingData.txt");
        
    //     System.out.println(runNetwork(neuralNetwork).get(0));

    // }
}