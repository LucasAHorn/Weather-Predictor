import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Training {


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


    public static ArrayList<ArrayList<Nodes>> readNodes(String relativeFilePath) {

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

        } catch (Exception e) {
            System.err.println("Nodes could not be read from the file: " + relativeFilePath);
        }
        
        return readNodes;

    }


    public static void main(String[] args) {

        // ArrayList<ArrayList<Nodes>> neuralNetwork = new ArrayList<>();

        // // This creates nodes
        // neuralNetwork.add(initNodes(10, 0)); // data nodes
        // neuralNetwork.add(initNodes(5, 10));
        // neuralNetwork.add(initNodes(1,5));
        
        // saveNodes(neuralNetwork, "./NodeBiases/test0.txt");

        ArrayList<ArrayList<Nodes>> neuralNetwork = readNodes("./NodeBiases/test0.txt");
        System.out.println(neuralNetwork);
    }
}