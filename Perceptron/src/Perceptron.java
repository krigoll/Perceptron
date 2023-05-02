import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Perceptron {

    private double a;
    private double threshold;
    private double [] weightVector;
    private int epoch;

    private String trainFile;
    private String testFile;
    private BufferedReader trainBuffer;
    private BufferedReader testBuffer;
    private Map<String,Integer> perceptronReplies;

    private List<Data> trainData;
    private List<Data> testData;

    public Perceptron(double a, String trainFile, String testFile, int epoch) {
        this.a = a;
        this.trainFile = trainFile;
        this.epoch = epoch <= 0 ? 1 : epoch;
        this.testFile = testFile;
        this.perceptronReplies = new HashMap<>();
        this.trainData = new ArrayList<>();
        this.testData = new ArrayList<>();
        try {
            readData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.weightVector = new double [trainData.get(0).getVector().length];
        setupVariables();
        trainPerceptron();
        testPerceptron();
    }
    public Perceptron(double a, String trainFile, String testFile,double correctness) {
        this.a = a;
        this.trainFile = trainFile;
        this.testFile = testFile;
        this.epoch = 1;
        this.perceptronReplies = new HashMap<>();
        this.trainData = new ArrayList<>();
        this.testData = new ArrayList<>();
        try {
            readData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.weightVector = new double [trainData.get(0).getVector().length];
        setupVariables();
        trainPerceptron();

        int numberOfIterations = 1;
        while(testPerceptron() < correctness) {
            trainPerceptron();
            numberOfIterations++;
        }
        System.out.println("Ilosc iteracji uczenia jest rowna = " + numberOfIterations);
    }

    public void enterVector() {
        boolean isActive = true;
        Scanner scannerVector = new Scanner(System.in);
        Scanner scanner = new Scanner(System.in);
        int option;
        while (isActive) {
            testData.clear();
            System.out.println("Czy chcesz sprawdzic inny wektor?");
            System.out.println("1.Tak\n2.Nie");
            option = scanner.nextInt();
            if (option == 1) {
                System.out.println("Podaj wektor o wielkosci " + weightVector.length + ". Oddziel pojednycze liczby spacja.");
                String[] vectorString = scannerVector.nextLine().split(" ");
                if (vectorString.length != weightVector.length) {
                    System.out.println("Zly wektor!");
                } else {
                    double[] vector = new double[vectorString.length];
                    for (int i = 0; i < vector.length; i++) {
                        vector[i] = Double.parseDouble(vectorString[i]);
                    }
                    testUserVector(vector);
                }
            } else if (option == 2) {
                isActive = false;
            } else {
                System.out.println("Zla opcja!");
            }
        }
    }

    public void testUserVector(double[] x) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * this.weightVector[i];
        }
        double net = sum - threshold;
        int y = net >= 0 ? 1 : 0;
        for(Map.Entry<String,Integer> entry : perceptronReplies.entrySet()) {
            if(entry.getValue() == y) {
                System.out.println("Wektor zakwalifikowany do: " + entry.getKey());
                break;
            }
        }
    }

    private void setupVariables() {
        for (int i = 0; i < this.weightVector.length; i++) {
            weightVector[i] = Math.random();
        }
        this.threshold = (int)(Math.random()*10)+1;
    }

    private double testPerceptron() {
        int poprawne = 0;
        int niepoprawne = 0;
        for (Data dataTest : testData) {
            double net = dataTest.calculateVectors(this.weightVector) - threshold;
            if(net >= 0 && perceptronReplies.get(dataTest.getLabel()) == 1 || net < 0 && perceptronReplies.get(dataTest.getLabel()) == 0) {
                poprawne++;
            }
            else {
                niepoprawne++;
            }
        }
        System.out.println("Zakwalifikowano poprawnie: " + poprawne);
        System.out.println("Zakwalifikowano niepoprawnie: " + niepoprawne);
        System.out.println("Procent: " + (double)(poprawne)/(double)(poprawne+niepoprawne));
        return (double)(poprawne)/(double)(poprawne+niepoprawne);
    }


    private void trainPerceptron() {
        for (int i = 0; i < epoch; i++) {
            for (Data data : trainData) {
                double net = data.calculateVectors(this.weightVector) - threshold;
                if (net >= 0 && perceptronReplies.get(data.getLabel()) == 0) {
                    deltaRule(data.getVector(), 0, 1);
                } else if (net < 0 && perceptronReplies.get(data.getLabel()) == 1) {
                    deltaRule(data.getVector(), 1, 0);
                }
            }
        }
    }

    private void deltaRule(double[] x,int d,int y) {
        for (int i = 0; i < x.length; i++) {
            weightVector[i] += a*(d-y)*x[i];
        }
        threshold -= a*(d-y);
    }

    private void readData() throws IOException {
        FileReader frTrain = new FileReader(this.trainFile);
        FileReader frTest = new FileReader(this.testFile);
        trainBuffer = new BufferedReader(frTrain);
        testBuffer = new BufferedReader(frTest);

        while (trainBuffer.ready()) {
            String [] args = trainBuffer.readLine().split(",");
            double [] vector = new double[args.length-1];
            String label = args[args.length-1];

            for (int i = 0; i < args.length-1; i++) {
                vector[i] = Double.parseDouble(args[i]);
            }
            if(!perceptronReplies.containsKey(label)) {
                perceptronReplies.put(label,perceptronReplies.size());
            }
            trainData.add(new Data(label,vector));
        }
        while (testBuffer.ready()) {
            String [] args = testBuffer.readLine().split(",");
            double [] vector = new double[args.length-1];
            String label = args[args.length-1];

            for (int i = 0; i < args.length-1; i++) {
                vector[i] = Double.parseDouble(args[i]);
            }
            testData.add(new Data(label,vector));
        }
    }
}
