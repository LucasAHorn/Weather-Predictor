package src;

import static src.NetworkUtil.fillDataNodes;
import static src.NetworkUtil.getTrainingData;
import static src.NetworkUtil.runNetwork;

import java.util.ArrayList;

public class CodeTester {
    public static void main(String[] args) {
    
        int[] colArr = {1,2,3,4,5};

        // NetworkTrainer nt = new NetworkTrainer("TempuratureBiases/test1.txt", "Data/weather_with_future.csv", colArr);

        ArrayList<ArrayList<Double>> trainingData = new ArrayList<>();
        ArrayList<Double> dataRow = new ArrayList<>();
        trainingData = getTrainingData("./Data/TempTrainingData.txt");
        dataRow = trainingData.get(0);

        NetworkTrainer nt = new NetworkTrainer("./TempuratureBiases/model1.txt", trainingData, 0, 0);

        fillDataNodes(nt.getNetwork(), dataRow);

        System.out.println(runNetwork(nt.getNetwork()));
    }
}
