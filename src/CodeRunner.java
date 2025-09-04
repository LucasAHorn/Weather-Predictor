package src;

import static src.NetworkUtil.*;

import java.io.*;
import java.util.ArrayList;

public class CodeRunner {

    public static ArrayList<String> getRelativeFilePaths(String relativeDirPath) {
        ArrayList<String> fileList = new ArrayList<>();
        for (File f : new File(relativeDirPath).listFiles()) {
            fileList.add(relativeDirPath + "/" + f.getName());
        }
        return fileList;
    }

    public static void main(String[] args) {

        System.out.println("Running");

        ArrayList<ArrayList<Double>> trainingData = getTrainingData("./Data/weather_cleaned.csv");
        ArrayList<String> filePaths = getRelativeFilePaths("Network_attempt_3/laptop");

        try {
//                Saving logging information into a file
            PrintStream consoleOut = System.out;

            FileOutputStream fos = new FileOutputStream("Network_attempt_3/LAPTOPoutput1.txt", true);
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

            NetworkTrainer[] networkTrainers = new NetworkTrainer[filePaths.size()];
            Thread[] threads = new Thread[filePaths.size()];

            for (int i = 0; i < filePaths.size(); i++) {
                networkTrainers[i] = new NetworkTrainer(filePaths.get(i), trainingData, 1_000_000, 10_000, 1.5);
                Thread thread = new Thread(networkTrainers[i]);
                thread.start();
                threads[i] = thread;
            }
            filePaths = null;


            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (NetworkTrainer trainer : networkTrainers) {
                    trainer.stop();
                }
                for (Thread t : threads) {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }));

//            to keep the jvm alive
            Thread.currentThread().join();


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("There is an error within the forever loop");
        }
    }
}