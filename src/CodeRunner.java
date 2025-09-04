package src;

import static src.NetworkUtil.*;

import java.io.*;
import java.lang.reflect.Array;
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
        ArrayList<String> filePaths = getRelativeFilePaths("Network_attempt_2/pc");
        int roundNum = 0;

        try {
//                Saving logging information into a file
            PrintStream consoleOut = System.out;

            FileOutputStream fos = new FileOutputStream("Network_attempt_2/PCoutput1.txt", true);
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

            Thread[] threads = new Thread[filePaths.size()];

            for (int i = 0; i < filePaths.size(); i++) {
                NetworkTrainer nt = new NetworkTrainer(filePaths.get(i), trainingData, 100000, 50000, 1.5);
                Thread t = new Thread(nt);
                t.start();
                threads[i] = t;
            }
            filePaths = null;


//            to keep the jvm alive
            Thread.currentThread().join();


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("There is an error within the forever loop");
        }
    }
}