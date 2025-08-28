package FirstExamples;

import static FirstExamples.NetworkUtil.getTrainingData;

import java.util.ArrayList;

public class CodeRunner {

    public static void main(String[] args) {
        System.out.println("start");


    // public NetworkTrainer(String newNetworkFilePath, String trainingDataPath, int[] nodeHeights, int numTimesToRun, int variability) {

        int[] colArr = {1,2,3,4,5};

        new NetworkTrainer("TempuratureBiases/test1.txt", "Data/weather_with_future.csv", colArr);



        // ArrayList<ArrayList<Double>> trainingData = getTrainingData("./Data/weather_with_future.csv");

        // NetworkTrainer nt = new NetworkTrainer("./NodeBiases/test0.txt", trainingData, 0, 0);

        // Thread t1 = new Thread(nt);
        // nt.run(); 
    
        

    }
}