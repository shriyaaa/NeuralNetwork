import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

public class  neural {
    public static int hiddenLayerSize = 60; // variable to store hidden layer size for easier adjustments
    public static double successCounter = 0; // counter for successful matches
    public static int[][] dataset = new int[2810][65]; // 2d array storing the complete dataset
    public static double[][] inputWeights = new double[hiddenLayerSize][64]; // 2d array storing the input layer weights
    public static double[] dataRow = new double[64]; // 1d array storing the current dataset
    public static double[][] hiddenWeights = new double[10][hiddenLayerSize]; // 2d array storing hidden layer weights
    public static double[] hiddenLayer = new double[hiddenLayerSize]; // 1d array storing the hidden layer
    public static double[] outputLayer = new double[10]; // 1d array storing the output layer generated by the neural network
    public static double[] innerSummation = new double[hiddenLayerSize]; // 1d array storing the sum of weighted products
    public static double[] outerSummation = new double[10];// 1d array storing the sum of weighted products
    public static int optimalOutput = 0; // storing the 65th column of the dataset that holds the optimal output that should be generated
    public static double desiredOutput[][] ={
            {1,0,0,0,0,0,0,0,0,0},
            {0,1,0,0,0,0,0,0,0,0},
            {0,0,1,0,0,0,0,0,0,0},
            {0,0,0,1,0,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0},
            {0,0,0,0,0,1,0,0,0,0},
            {0,0,0,0,0,0,1,0,0,0},
            {0,0,0,0,0,0,0,1,0,0},
            {0,0,0,0,0,0,0,0,1,0},
            {0,0,0,0,0,0,0,0,0,1}

    } ; // 2d array hardcoded to store the desired outputs
    public static boolean flag = true; // boolean value to check if desired output array and generated output match
    public static double outputError = 0; // variable storing error for the output layer
    public static  double hiddenLayerError = 0; // variable storing the error for the hidden layer
    public static double[] currentDesiredOutput = new double[10]; // stores the desired output for the current row
    public static double failCounter = 0; // counter for unsuccessful matches




    public static void main(String[] args) throws Exception {

        generateInputWeight();
        generateHiddenWeight(); // generating input and hidden weights before cycles start so that they don't get reset in every cycle


        int cycle = 0;
        while (cycle < 200){ // denotes the number of cycles
            int row = 0; // denotes the number of row in the dataset

            failCounter = 0;
            successCounter= 0; // resetting the success and fail counters in the start of the cycle

            // code for reading from the csv file
            File file = new File("/Users/RiaNarang/IdeaProjects/NeuralNetwork/src/cw2DataSet2.csv"); // locating the file
            Scanner sc = new Scanner(file);
            String st;
            String[] data ;
            int m = 0;

            while (sc.hasNextLine()) {
                st = sc.nextLine();
                data = st.split(",");
                for (m = 0; m < 65; m++) {
                    dataset[row][m] = Integer.parseInt(data[m]); // storing values from each row by incrementing columns
                } // end of for loop

                optimalOutput = dataset[row][64]; // storing the 65th column value of the dataset as the optimal output for each row

                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ #" + row + "Training;  Cycle: "+cycle+ " " + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                storingRowData(row); // calling the method to get input layer

                gethiddenLayer(); // calling the method to generate hidden layer

                System.out.println("");
                System.out.println("Output Layer: " + Arrays.toString(outputLayer)); //printing the output layer
                getOutputLayer(); // generate output layer


                System.out.println("");
                System.out.println("Desired Output: " + Arrays.toString(desiredOutput[optimalOutput])); // printing the desired output based on the optimal output from the 65th column


                compareOutputs(); // calling the method to compare match between output layer with the desire layer and do backpropogation if no match



                row++; // incrementing rows after getting each value from each column from the previous row

                System.out.println("success counter: " + successCounter +", Fail counter: "+failCounter); // printing success and fail counters
            } // end of scanner while loop


            cycle++; //incrementing cycle
            System.out.println("Accuracy Percentage: " + (successCounter * 100) / 2810 + "%"); // calculating ad printing accuracy percentage



        } // end of outer cycle while loop


    } // end of main method



    public static void compareOutputs() {


        for (int y = 0; y < 10; y++){

            currentDesiredOutput[y] = desiredOutput[optimalOutput][y]; //populating 1d currentDesiredOutput using the 2d array and variable storing the 65th column values

        } // end of for loop

        if (Arrays.equals(currentDesiredOutput, outputLayer)){ // checking if output layer matches the output layer
            System.out.println("Match: Yes");
            successCounter++;                      //incrementing successCounter if match is yes
        }
        else
        {
            System.out.println("Match: No");
            backPropagation();          //going to backpropagation if there is no match
            failCounter++;              // incrementing failCounter
        }


    }

    public static void backPropagation (){
        double learningRate = 0.02; //declaring learning rate to train, it is kept lower to increase effectiveness

        for (int l = 0; l < 10; l++){
            outputError = currentDesiredOutput[l] - outputLayer[l]; // calculating output layer error by the difference between desired output and output layer (delta)
            for (int m = 0; m < hiddenLayerSize; m++) {
                hiddenWeights[l][m] = hiddenWeights[l][m] + (learningRate * hiddenLayer[m] * outputError); // adjusting hidden weights
                hiddenLayerError = hiddenLayer[m] * (1 - hiddenLayer[m]) * hiddenWeights[l][m] * outputError; // calculating hidden layer error
            }

            for(int k = 0; k < hiddenLayerSize; k++) {
                for (int n = 0; n < 64; n++) {
                    inputWeights[k][n] = inputWeights[k][n] + (learningRate * dataRow[n] * hiddenLayerError); // adjusting input weights
                } // loop 3 ends

            }// loop 2 ends

        } // loop 1 ends


    } // end of backpropagation





    public static double[][] generateInputWeight(){

        Random randomInt = new Random(); // initializing

        for (int i = 0; i < hiddenLayerSize; i++) {
            for (int j = 0; j < 64; j++) {

                inputWeights[i][j] = randomInt.nextInt(2 + 2) - 2; // generating weights randomly between -2 and 2

            }// end of inner for
        } // end of outer for
        return inputWeights;

    } // end of generateInputWeight method


    public static double[][] generateHiddenWeight(){

        Random randomInt = new Random();

        for (int k = 0; k < 10; k++) {
            for (int l = 0; l < hiddenLayerSize; l++) {
                hiddenWeights[k][l] = randomInt.nextInt(2 + 2) - 2; // generating weights randomly between -2 and 2
            }// end of inner for
        }// end of outer for

        return hiddenWeights;
    } // end of generateHiddenWeight method



    //code for pushing row into 1d array
    public static double[] storingRowData(int currentRow){

        try{
            for (int r = 0; r < 64; r++){
                dataRow[r] = dataset[currentRow][r]; // populating dataRow array

            } // end of for loop

        }// end of try

        catch (Exception e){
            System.out.println(e);

        } // end of catch

        return dataRow;
    } // end of storingRowData method




    // multiplying weights with input method

    public static double[] gethiddenLayer (){
        double product = 0;

        for (int a = 0; a < hiddenLayerSize; a++){
            innerSummation[a] = 0; // initializing innerSummation to 0

            for(int b = 0; b < 64; b++ ) {

                product = (inputWeights[a][b] * dataRow[b]) + 1; // multiplying weights with input layer
                innerSummation[a] += product; //adding the weighted products and populating innerSummation array

            }  //end of inner for

            hiddenLayer[a] = Math.round(sigmoid(-1 * innerSummation[a]) * 100000d) / 100000d; // applying sigmoid function to get 0 or 1 as an output

        }
        return hiddenLayer;

    }// end of getInnerHiddenayer method


    public static double[] getOutputLayer (){
        double product = 0; //initializing  product to 0

        for (int a = 0; a < 10; a++){
            outerSummation[a] = 0; // initializing outerSummation to 0

            for (int b = 0; b < hiddenLayerSize; b++) {
                product = (hiddenWeights[a][b] * hiddenLayer[b]) + 1;  // multiplying weights with hidden layer
                outerSummation[a] += product; // adding the weighted products and populating outerSummation array
            }

            // threshold function for neuron activation
            if(outerSummation[a] < 0){  //checking if value is less or more than 0
                outputLayer[a] = 0;
            }

            else {
                outputLayer[a] = 1;
            }

        } // end of outer for loop


        return outputLayer;

    }// end of getoutputLayer

    public static double sigmoid ( double x){
        return 1d/(1 + Math.exp(-x));
    } // sigmoid method



} // end of class