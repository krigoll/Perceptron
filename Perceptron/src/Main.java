
public class Main {
    public static void main(String[] args) {

        Perceptron perceptron = new Perceptron(0.01,"src\\Data\\perceptron.data",
                "src\\Data\\perceptron.test.data",0.9);
        perceptron.enterVector();

    }
}
