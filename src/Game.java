//game logic class
public class Game {
    public int score = 0;
    public String summary="";

    //store the current question details for the gui to validate the answer
    private int currentNum1;
    private int currentNum2;
    private int currentOpChoice;
    private double currentActualAnswer;

    //methods to support the gui flow
    public String generateQuestionForGUI(int opChoice) {
        currentOpChoice = opChoice;
        currentNum1 = (int)(Math.random() * 20);
        currentNum2 = (int)(Math.random() * 20);

        String questionString = "";

        if (opChoice == 1) { //addition
            currentActualAnswer = currentNum1 + currentNum2;
            questionString = "What is "+currentNum1+" + "+currentNum2+" ?";
        }
        else if (opChoice == 2) { //subtraction
            //make sure num1 >= num2 for positive results
            if (currentNum1 < currentNum2) {
                int temp = currentNum1;
                currentNum1 = currentNum2;
                currentNum2 = temp;
            }
            currentActualAnswer = currentNum1 - currentNum2;
            questionString = "What is "+currentNum1+" - "+currentNum2+" ?";
        }
        else if (opChoice == 3) { //multiplication
            currentActualAnswer = currentNum1 * currentNum2;
            questionString = "What is "+currentNum1+" * "+currentNum2+" ?";
        }
        else if (opChoice == 4) { //division
            //m ake sure division gives int result
            currentNum1 = (int)(Math.random() * 10) + 1; //num1 up to 10
            currentNum2 = (int)(Math.random() * 5) + 1; //num2 up to 5
            currentActualAnswer = currentNum1 * currentNum2; //dividend
            int temp = (int)currentActualAnswer;
            currentActualAnswer = (double)temp / currentNum2;
            currentNum1 = temp;
            questionString = "What is "+currentNum1+" / "+currentNum2+" ?";
        }

        return questionString;
    }

    public boolean checkAnswer(double userAnswer) {
        //compare user answer to actual answer, allowing a small tolerance for doubles
        boolean correct = Math.abs(userAnswer - currentActualAnswer) < 0.001;

        //log question/result to the summary string
        String op = "";
        if (currentOpChoice == 1) op = "+";
        else if (currentOpChoice == 2) op = "-";
        else if (currentOpChoice == 3) op = "*";
        else if (currentOpChoice == 4) op = "/";

        summary += "\n"+currentNum1+op+currentNum2+" = User:"+userAnswer+" | Actual:"+currentActualAnswer+" | Correct:"+correct;

        if(correct){
            ++score;
        }
        return correct;
    }

    //helper to get the actual answer for display after an incorrect attempt
    public double getActualAnswer() {
        return currentActualAnswer;
    }
}