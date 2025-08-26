package FirstExamples;

public class CodeRunner {

    public static void main(String[] args) {
        System.out.println("start");

        NetworkTrainer nt = new NetworkTrainer("./NodeBiases/test0.txt", "./Data/rawTrainingData.csv", 0, 0);

        Thread t1 = new Thread(nt);
        nt.run();
        
    }

}