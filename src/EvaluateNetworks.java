package src;

import static src.NetworkUtil.fillDataNodes;
import static src.NetworkUtil.getTrainingData;
import static src.NetworkUtil.runNetwork;

import java.io.File;
import java.util.ArrayList;

public class EvaluateNetworks {
    
    public static void main(String[] args) {
        
        ArrayList<ArrayList<Double>> trainingData = new ArrayList<>();
        ArrayList<Double> dataRow = new ArrayList<>();
        trainingData = getTrainingData("./Data/weather_cleaned.csv");
        dataRow = trainingData.get(0);

        String dirName = "Network_attempt_2/pc";

        File[] filesToTest = new File(dirName).listFiles();

        for (File file : filesToTest) {

            NetworkTrainer nt = new NetworkTrainer(dirName + "/" + file.getName(), trainingData, 10000, 0, 0);
            Thread t = new Thread(nt);
            t.start();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            nt.stop();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}