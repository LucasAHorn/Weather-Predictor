package Examples.Tempurature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Training {


    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("trainingData.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                List<InputNodes> rowNodes = new ArrayList<>();
                
                for (String value : values) {
                    rowNodes.add(new InputNodes(Integer.parseInt(value.trim())));
                }

                // Do something with this row of nodes
                System.out.println("Loaded row:");
                for (InputNodes node : rowNodes) {
                    System.out.print(node.getData() + " ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
    }


    
}
}