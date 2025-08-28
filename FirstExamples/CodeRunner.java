package FirstExamples;

import static FirstExamples.NetworkUtil.*;

import java.util.ArrayList;

public class CodeRunner {

    public static void main(String[] args) {
        System.out.println("start");


    // public NetworkTrainer(String newNetworkFilePath, String trainingDataPath, int[] nodeHeights, int numTimesToRun, int variability) {

        int[] colArr = {1,2,3,4,5};

        // NetworkTrainer nt = new NetworkTrainer("TempuratureBiases/test1.txt", "Data/weather_with_future.csv", colArr);

        ArrayList<ArrayList<Double>> trainingData = new ArrayList<>();
        ArrayList<Double> dataRow = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            dataRow.add(0d);
        }
        trainingData.add(dataRow);


        NetworkTrainer nt = new NetworkTrainer("./TempuratureBiases/model1.txt", trainingData, 2, 0);

        // System.out.println(networkGolfScore()); TODO: find out how to get the score

        // Thread t = new Thread(nt);
        // t.run();

        // ArrayList<ArrayList<Double>> trainingData = getTrainingData("./Data/weather_with_future.csv");


        // Thread t1 = new Thread(nt);
        // nt.run(); 
    
        

    }
}