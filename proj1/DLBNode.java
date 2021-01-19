
import java.util.*;


public class DLBNode {

    public Nodelet front; //nodelet that is our first child alphabetically
    public int degree; //number of children of this node in the trie
    public boolean repsPrefix; //does this string represent a prefix in the dictionary
    public String value; //value of the string defined along the path
    public Character key;

    public class Nodelet {

        private char c; //character pointer for the next child of the DLBNode
        private Nodelet next; //the next possible character pointer
        private DLBNode child; //the DLBNode associated with following this character down the trie

        public Nodelet(char a, Nodelet n, DLBNode cn) {
            c = a;
            next = n;
            child = cn;
        }
    }

    public DLBNode() {
        front = null;
        degree = 0;
        repsPrefix = false;
        value = null;
        key = null;
    }

    public DLBNode(boolean e) {
        front = null;
        degree = 0;
        repsPrefix = true;
        value = null;
        key = null;
    }
    
    public DLBNode(char ch, String word){
        front = new Nodelet(ch, null, new DLBNode());
        degree = 0;
        repsPrefix = true;
        value = word;
        key = ch;
    }
    
    public boolean hasSibling(){
        return front.next != null;
    }
    
    public String getValue(){
        return value;
    }
    
     public void setValue(String v){
        value = v;
    }
    
    public boolean hasKey(){
        return key != null;
    }
    
    public Character getKey(){
        return key;
    }
    
    public void setKey(Character ch){
        key = ch;
    }
    
    public char getChar(){
        return front.c;
    }
    
    public DLBNode getNext(){
        if (front.next.child != null) {
            return front.next.child;
        }
        return null;
    }

    public void addChild(char nextC) {
        //this resets the nodelet if one already exists for the given 
        //character we are inserting
        if (front == null) {
            front = new Nodelet(nextC, null, new DLBNode());
            degree++;
        } else if (nextC < front.c) { //need to insert right of front
            front = new Nodelet(nextC, front, new DLBNode());
            degree++;
        } else {
            Nodelet cur = front.next;
            Nodelet prev = front;
            while (cur != null && nextC > cur.c) {
                prev = cur;
                cur = cur.next;
            }
            prev.next = new Nodelet(nextC, cur, new DLBNode());
            if (cur != null && nextC == cur.c) {
                degree++;
            }
        }
    }

    //returns false if nextC doesnt reperesent a child, true if it does
    public boolean hasChild(char nextC) {
        if (front == null) {
            return false;
        }
        Nodelet cur = front;
        while (cur != null && cur.c < nextC) {
            cur = cur.next;
        }
        if (cur == null || cur.c != nextC) {
            return false;
        }
        return true;
    }

    //returns DLBNode if nextC reperesents a child, null if it doesnt
    public DLBNode getChild(char nextC) {
        if (front == null) {
            return null;
        }
        Nodelet cur = front;
        while (cur != null && cur.c < nextC) {
            cur = cur.next;
        }
        if (cur == null || cur.c != nextC) {
            return null;
        }
        return cur.child;
    }

}
