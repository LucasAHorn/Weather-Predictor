package src;

// This is used to create new networks with minor edits
public class createNetworks {
    

    public static void main(String[] args) {
        int[] colArr = {6,4,1};

        String modelStr = "";

        for (int i = 0; i < colArr.length; i++) {
            modelStr += colArr[i];
            if (i < colArr.length - 1) {modelStr += "-";}
        }
    
        // new NetworkTrainer("Network_attempt_3/laptop/model" + modelStr + "_v4.txt", "Data/weather_cleaned.csv", colArr);

        
    }
}