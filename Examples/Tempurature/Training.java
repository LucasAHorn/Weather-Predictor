import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;


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
                bias.add(rand.nextDouble());
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
                fileWriter.write("\n"); // used to make another column of nodes

                for (int k = 0; k < nodes.get(i).size(); k++) {

                    concatBiases = "";
                    for (Double d : nodes.get(i).get(k).getBias()) {
                        concatBiases += d + "_";
                    }
                    concatBiases = concatBiases.substring(0, concatBiases.length() - 1);

                    fileWriter.write(" ");
                }
            }

            fileWriter.close();
            return true;
        } catch (Exception e) {
            System.err.println("Could not save to filepath: " + relativeFilePath);
            return false;
        }
    }



    public static void main(String[] args) {

    


    
    }
}