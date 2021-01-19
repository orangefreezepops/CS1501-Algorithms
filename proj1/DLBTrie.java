
import java.util.*;

public class DLBTrie {

    private DLBNode root;
    private static final char TERMINATOR = '^';

    public DLBTrie() {

    }

    public DLBNode getRoot() {
        return root;
    }

    public static int findPredictions(DLBNode n, int predCount, ArrayList<String> predictions, char c) {

        if (n.hasKey() && predCount > 0) {
            if (n.hasChild(c)) {
                predCount = findPredictions(n.getChild(c), predCount, predictions, c);
                if (predCount == 0) {
                    return predCount;
                }
                if (n.getKey().equals(TERMINATOR)) {
                    System.out.println("inside");
                    predictions.add(n.getValue());
                    predCount--;
                }

                if (n.hasSibling()) {
                    predCount = findPredictions(n.getNext(), predCount, predictions, c);
                    if (predCount == 0) {
                        return predCount;
                    }
                }
                return predCount;
            }
        }
        return predCount;
    }

    public void addWord(String word) {
        DLBNode cur, temp; //current node and root node
        if (root == null) {
            root = new DLBNode();
            cur = root;
            for (int i = 1; i < word.length(); i++) {
                char c = word.charAt(i);
                temp = cur.getChild(c);
                if (temp == null) {
                    cur.addChild(c);
                    temp = cur.getChild(c);
                }
                cur = temp;
            }
        } else {
            cur = root;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                temp = cur.getChild(c);
                if (temp == null) {
                    cur.addChild(c);
                    temp = cur.getChild(c);
                }
                cur = temp;
            }
        }
        cur.setKey(TERMINATOR);
        cur.setValue(word);
        cur.repsPrefix = true;
        //System.out.println(cur.getValue());
    }
}
