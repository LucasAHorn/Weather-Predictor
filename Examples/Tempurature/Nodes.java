package Examples.Tempurature;

import java.util.LinkedList;

/**
 * This class stores rows of equations
 */
public class Nodes {

    private LinkedList<Integer> bias;
    private int data;

    public Nodes(LinkedList<Integer> bias) {
        this.bias = bias;
    }

    public void updateBias(LinkedList<Integer> bias) {
        this.bias = bias;
    }

    public void updateData(LinkedList<InputNodes> nodes) {
        data = 0;
        for (int i = 0; i < nodes.size(); i++) {
            data += nodes.get(i).getData() * bias.get(i);
        }
    }

    public int getData() {
        return data;
    }
}
