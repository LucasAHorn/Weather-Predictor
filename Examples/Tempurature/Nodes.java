import java.util.ArrayList;

/**
 * This class stores rows of equations
 */
public class Nodes {

    private ArrayList<Double> bias;
    private double val;

    public Nodes(ArrayList<Double> bias) {
        this.bias = bias;
    }

    public void updateBias(ArrayList<Double> bias) {
        this.bias = bias;
    }

    public ArrayList<Double> getBias() {
        return bias;
    }

    public void updateData(ArrayList<Nodes> nodes) {
        val = 0;
        for (int i = 0; i < nodes.size(); i++) {
            val += nodes.get(i).getVal() * bias.get(i);
        }
    }

    public double getVal() {
        return val;
    }
}
