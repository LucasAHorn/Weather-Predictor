package FirstExamples;

import java.util.ArrayList;

/**
 * This class stores rows of equations
 */
public class Nodes implements Cloneable {

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

    public void setData(Double val) {
        this.val = val;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        // Start with shallow copy
        Nodes cloned = (Nodes) super.clone();

        if (this.bias != null) {
            cloned.bias = new ArrayList<>(this.bias);
        }

        return cloned;
    }
}
