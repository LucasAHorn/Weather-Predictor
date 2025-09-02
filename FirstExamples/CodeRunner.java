package FirstExamples;

import static FirstExamples.NetworkUtil.*;

import java.util.ArrayList;

public class CodeRunner {

    public static void main(String[] args) {
        System.out.println("start");


    // public NetworkTrainer(String newNetworkFilePath, String trainingDataPath, int[] nodeHeights, int numTimesToRun, int variability) {

        int[] colArr = {1,2,3,4,5};

        // NetworkTrainer nt = new NetworkTrainer("TempuratureBiases/test1.txt", "Data/weather_with_future.csv", colArr);

        ArrayList<ArrayList<Double>> trainingData = getTrainingData("./Data/weather_with_future.csv");


        NetworkTrainer nt = new NetworkTrainer("./TempuratureBiases/model1.txt", trainingData, 20000, 200000000);

        Thread exampleThread = new Thread(nt);

        exampleThread.run();

        try {
            exampleThread.join();
            System.out.println("Finished");
        } catch (Exception e) {
            // TODO: handle exception
        }


            //    TODO: Make it so that the network is reset to the most recent best network
        

        

    }
}