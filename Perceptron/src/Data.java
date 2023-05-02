public class Data {

    private String label;
    private double [] vector;

    public Data(String label, double [] vector) {
        this.label = label;
        this.vector = vector;
    }

    public double[] getVector() {
        return vector;
    }

    public String getLabel() {
        return label;
    }

    public double calculateVectors(double[] vector) {
        double sum = 0;
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i] * this.vector[i];
        }
        return sum;
    }
}
