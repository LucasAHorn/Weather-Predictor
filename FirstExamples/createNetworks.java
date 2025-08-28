package FirstExamples;

// This is used to create new networks with minor edits
public class createNetworks {
    

    public static void main(String[] args) {
        int[] colArr = {1};
    
        new NetworkTrainer("TempuratureBiases/model1.txt", "Data/weather_with_future.csv", colArr);
    
    }


}
