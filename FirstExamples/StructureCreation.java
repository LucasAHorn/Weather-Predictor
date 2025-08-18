package FirstExamples;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class StructureCreation {


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
                bias.add(rand.nextDouble() * 1000);
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


    public static void fillDataNodes(ArrayList<ArrayList<Nodes>> neuralNetwork, String relativeFilePath) {

        try {
            
            Scanner scnr = new Scanner(new File(relativeFilePath));
            String[] dataVals = scnr.nextLine().split(",");
            for (int i = 0; i < dataVals.length; i++) {
                neuralNetwork.get(0).get(i).setData(Double.parseDouble(dataVals[i]));
            }

            scnr.close();
            return;

        } catch (Exception e) {
            System.err.println("Issue with loading data from file: " + relativeFilePath);
        }

        throw new RuntimeException("data nodes not loaded properly");
    }

    public static ArrayList<Double> runNetwork(ArrayList<ArrayList<Nodes>> network) {
    
        for (int i = 1; i < network.size(); i++) {
            for (int k = 0; k < network.get(i).size(); k++) {
                network.get(i).get(k).updateData(network.get(i - 1));
            }
        }

        ArrayList<Double> results = new ArrayList<>();
        for (Nodes n : network.get(network.size() - 1)) {
            results.add(n.getVal());
        }

        return results;
    }


    public static ArrayList<ArrayList<Double>> getTrainingData(String dataFilePath) {

        ArrayList<ArrayList<Double>> dataVals = new ArrayList<>();
        ArrayList<Double> dataRow;

        try {
            
            Scanner scnr = new Scanner(new File(dataFilePath));
            while (scnr.hasNextLine()) {
                dataRow = new ArrayList<>();
                String[] vals = scnr.nextLine().split(",");
                for (String str : vals) {
                    dataRow.add(Double.parseDouble(str));
                }
                dataVals.add(dataRow);
            }

            return dataVals;

        } catch (Exception e) {
            System.err.println("Failed to read file: " + dataFilePath);    
        }
        throw new RuntimeException("Data was not read properly");
    }

    public static void updateBiases(ArrayList<ArrayList<Nodes>> network) {
        
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