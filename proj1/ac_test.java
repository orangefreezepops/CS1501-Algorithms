
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ac_test {

    private static DLBTrie dictionary = new DLBTrie();
    private static DLBTrie history = new DLBTrie();

    //different type of predictions
    private static ArrayList<String> dictPredictions = new ArrayList<>();
    private static ArrayList<String> histPredictions = new ArrayList<>();
    private static ArrayList<String> prevPredictions = new ArrayList<>();
    private static ArrayList<String> predictions = new ArrayList<>();

    //for timing
    private static int wordsCompleted = 0;
    private static double start = 0.0;
    private static double finish = 0.0;
    private static double deltaT = 0.0;
    private static double total = 0.0;
    
    
    public static void predict(StringBuilder word) {

        int predCount = 5;
        DLBNode histNode = history.getRoot();
        DLBNode dictNode = dictionary.getRoot();
        char c = word.charAt(0);
        boolean flag = false;

        for (int i = 0; i < word.length(); i++) {
            c = word.charAt(i);
            while (histNode.hasKey() && c != histNode.getKey() && histNode.hasSibling()) {
                histNode = histNode.getNext();
            }
            if (histNode.hasKey() && c == histNode.getKey() && histNode.hasChild(c)) {
                histNode = histNode.getChild(c);
                if (i == word.length() - 1) {
                    flag = true;
                }
            }
        }

        if (histNode.hasKey() && flag) {
            //System.out.println("history c: " + c);
            predCount = history.findPredictions(histNode, predCount, predictions, c);
        }

        flag = false;

        for (int i = 0; i < word.length(); i++) {
            //check the dictionaryTrie for the user's word in the same way
            c = word.charAt(i);
            while (dictNode.hasKey() && c != dictNode.getKey() && dictNode.hasSibling()) {
                //System.out.println("has next: " + dictNode.hasSibling());
                dictNode = dictNode.getNext();
            }

            if (dictNode.hasKey() && c == dictNode.getKey() && dictNode.hasChild(c)) {
                dictNode = dictNode.getChild(c);
                if (i == word.length() - 1) {
                    flag = true;
                }
            }
        }
        
        //System.out.println("arrived");
        //System.out.println("hasKey: " + dictNode.hasKey());

        if (dictNode.hasKey() && flag) {
            System.out.println("dictionary c: " + c);
            predCount = dictionary.findPredictions(dictNode, predCount, predictions, c);
        }
        
//        for (int i = 0; i < predictions.size(); i ++){
//            System.out.print("Prediction " + i + " " + predictions.get(i) + " ");
//        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        File dictFile = new File("dictionary.txt"); //load in the dictionary
        Scanner dictScanner = new Scanner(dictFile);; //new scanner to read the file
        String word = "";
        while (dictScanner.hasNextLine()) {
            word = dictScanner.nextLine();
            dictionary.addWord(word); //add words from the dictionary to the dict trie
        }

        FileWriter userHistory; //new FW to write completed words to user_hstory.txt file
        Scanner histScanner; //new scanner
        File historyFile = new File("user_history.txt"); //history file
        historyFile.createNewFile(); //if it doesn't exist its created
        String historyWord = ""; //words from the history file

        histScanner = new Scanner(historyFile);
        userHistory = new FileWriter(historyFile, true);
        while (histScanner.hasNextLine()) {
            historyWord = histScanner.nextLine();
            history.addWord(historyWord); //add words from history file into history trie
        }

        Scanner inputReader = new Scanner(System.in);
        String inputString = ""; //users input
        StringBuilder userWord = new StringBuilder(); //builds users word char by char
        String completedWord = ""; //the finished word
        boolean isStartOfFirstWord = true;
        boolean isStartOfNextWord = false;
        boolean isWordCompleted = false;

        do {
            if (isStartOfFirstWord) {
                System.out.println("\nEnter the first character: ");
                isStartOfFirstWord = false;
            } else if (isStartOfNextWord) {
                System.out.println("\nEnter the first character of the next word: ");
            } else {
                System.out.println("\nEnter the next character: ");
            }

            inputString = inputReader.nextLine();
            userWord.append(inputString);

            switch (inputString) {
                case "1": //the user wants the 1st prediction
                    isWordCompleted = true;
                    isStartOfNextWord = true;
                    completedWord = predictions.get(0);
                    break;
                case "2": //the user wants the 2nd prediction
                    isWordCompleted = true;
                    isStartOfNextWord = true;
                    completedWord = predictions.get(1);
                    break;
                case "3": //the user wants the 3rd prediction
                    isWordCompleted = true;
                    isStartOfNextWord = true;
                    completedWord = predictions.get(2);
                    break;
                case "4": //the user wants the 4th prediction
                    isWordCompleted = true;
                    isStartOfNextWord = true;
                    completedWord = predictions.get(3);
                    break;
                case "5": //the user wants the 5th prediction
                    isWordCompleted = true;
                    isStartOfNextWord = true;
                    completedWord = predictions.get(4);
                    break;
                case "$": //the user finished their word
                    isWordCompleted = true;
                    isStartOfNextWord = true;
                    userWord.setLength(userWord.length() - 1);//strip the last char
                    completedWord = userWord.toString(); //make SB back into regular String
                    break;
                case "!": //exit
                    isWordCompleted = true;
                    break;
                default:
                    break;
            }

            if (isWordCompleted == false) {
                //find word predictions
                start = System.nanoTime(); // start timer
                predict(userWord);
                finish = System.nanoTime(); // stop timer
                deltaT = (finish - start) / 1000000000.0;

                System.out.print("( ");
                System.out.format("%f", deltaT);
                System.out.print(" s )\n");
                System.out.println("Predictions: ");

                //print the predictions
                for (int i = 0; i < predictions.size(); i++) {
                    if (predictions.get(i) != null) {
                        System.out.print("(" + (i + 1) + ")" + predictions.get(i) + " ");
                    }
                }

            } else if (isWordCompleted == true && !inputString.equals("!")) {
                System.out.println("WORD COMPLETED: " + completedWord + "\n");
                history.addWord(completedWord); //add to the history trie
                userHistory.write(completedWord + "\n"); //write to the history file
                isWordCompleted = false; //get ready for a new word
                userWord.setLength(0); //clear the current
            }
            
            total += deltaT;
            wordsCompleted++;

        } while (!inputString.equals("!"));

        double averageTime = total / wordsCompleted; //average search time per word
        System.out.print("Average Time: ");
        System.out.format("%f", averageTime);
        System.out.print(" s \n");
        userHistory.close();
        System.out.println("Goodbye!");
        System.exit(0);

    }

}
