package FirstExamples;

import static FirstExamples.NetworkUtil.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class CodeRunner {

    public static void main(String[] args) {
        System.out.println("Init trainers");


        // NetworkTrainer nt = new NetworkTrainer("TempuratureBiases/test1.txt", "Data/weather_with_future.csv", colArr);

        ArrayList<ArrayList<Double>> trainingData = getTrainingData("./Data/weather_with_future.csv");

        NetworkTrainer[] networkTrainers = {
            new NetworkTrainer("./TempuratureBiases/model1.txt", trainingData, 50000, 20000),
            new NetworkTrainer("./TempuratureBiases/model8-4.txt", trainingData, 50000, 20000),
            new NetworkTrainer("./TempuratureBiases/model8-7-3.txt", trainingData, 50000, 20000),
            new NetworkTrainer("./TempuratureBiases/model8-8.txt", trainingData, 50000, 20000),
            new NetworkTrainer("./TempuratureBiases/model10-5-4.txt", trainingData, 50000, 20000),
            new NetworkTrainer("./TempuratureBiases/model10-6.txt", trainingData, 50000, 20000),
            new NetworkTrainer("./TempuratureBiases/model10-8-3.txt", trainingData, 50000, 20000),
            new NetworkTrainer("./TempuratureBiases/model10-8-4.txt", trainingData, 50000, 20000),
            new NetworkTrainer("./TempuratureBiases/model10-8-6.txt", trainingData, 50000, 20000),
        };

        int roundNum = 0;

        try {
            System.out.println("Start");


//                Saving logging information into a file
            PrintStream consoleOut = System.out;

            FileOutputStream fos = new FileOutputStream("output1.txt", true);
            PrintStream fileOut = new PrintStream(fos);

            PrintStream teeOut = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    consoleOut.write(b);
                    fileOut.write(b);
                }

                @Override
                public void flush() throws IOException {
                    consoleOut.flush();
                    fileOut.flush();
                }
            }, true);

            System.setOut(teeOut);


            while (true) {

                Thread[] threads = new Thread[networkTrainers.length];
                for (int i = 0; i < networkTrainers.length; i++) {
                    threads[i] = new Thread(networkTrainers[i]);
                    threads[i].start();
                }

                for (Thread t : threads) {
                    t.join();
                }

                for (NetworkTrainer nTrainer : networkTrainers) {
                    nTrainer.setVariability(Math.max(3, nTrainer.getVariability() / 2));
                }
                
                System.out.println("Finished Round Number: " + roundNum++ + ", Total networks tested: " + roundNum * 9 * 50000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("There is an error within the forever loop");
        }
    }
}