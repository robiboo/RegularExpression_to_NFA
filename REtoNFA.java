import java.io.*;
import java.util.*;

public class REtoNFA {
    //global variables
    public static int count;                    //tracks the current state number

    static class Node{
        char alph;                              //holds the alphabet character
        int state;                              //holds the state number
        boolean start;                          //set to true if the state is the start state
        boolean end;                            //set to true if the state is the end/accept state

        //connects nodes
        Node next;
        Node next2;

        //creates the Node or the state of the NFA
        public Node(char data, int state, boolean start, boolean end){
            this.alph = data;
            this.state = state;
            this.start = start;
            this.end = end;
            this.next = null;
            this.next2 = null;
        }

    }
    public static LinkedList<Node> list = new LinkedList<>();          //for printing in order
    public static  Stack<Node> stack = new Stack<>();               //stack that handles the NFA

    public static Node kleene(Node x){
        //creates a start state connected by an epsilon transition to the previous start state.
        //an epsilon transition is also connected from the previous accept state to the new start state

        Node n1 = new Node('E', ++count, true, true); //creates a node with epsilon transition and as the new start and new accept state
        Node ptr = x;                                                 //handles traversing
        x.start = false;                                                //setting the previous start state to false
        while(ptr != null){
            if(ptr.end){
                //checks which pointer to be used to connect the states needed to be connected
                if(ptr.next == null) {
                    ptr.next = n1;
                    if(ptr.next.next == null){
                        ptr.next.next = x;
                    }
                    else{
                        ptr.next.next2 = x;
                    }
                }
                else{
                    ptr.next2 = n1;
                    if(ptr.next2.next == null){
                        ptr.next2.next = x;
                    }
                    else{
                        ptr.next2.next2 = x;
                    }
                }
                ptr.alph = 'E';
                ptr.end = false;                                      //setting the previous end state to false
                break;
            }
            //determines where to traverse, either through .next or the .next2
            ptr = reTraverse(ptr);
        }
        list.add(n1);                                                   //adds the new Node in linkedlist for printing
        return n1;
    }

    public static Node concatenate(Node x, Node y){
        //connects two NFA by concatenating using epsilon transition

        Node ptr = x;
        while(ptr != null){

            //checks which pointer to be used to connect the states needed to be connected
            if(ptr.end){
                ptr.end = false;
                ptr.alph = 'E';
                if(ptr.next == null) {
                    ptr.next = y;
                }
                else{
                    ptr.next2 = y;
                }
                y.start = false;
                break;
            }
            //determines where to traverse, either through .next or the .next2
            ptr = reTraverse(ptr);
        }
        return x;
    }

    public static Node union(Node x, Node y){
        //connects two NFA by union using epsilon transition

        //creates a new start state and new end state
        Node start = new Node('E', ++count, true, false);
        Node end = new Node(' ', ++count, false, true);

        //add to linkedlist for printing
        list.add(start);
        list.add(end);

        Node ptr = x; //handles traversing

        //traversing through the NFA then connect the new end state
        while(ptr != null){

            //checks which pointer to be used to connect the states needed to be connected
            if(ptr.end){
                ptr.alph = 'E';
                if(ptr.next == null){
                    ptr.next = end;
                }
                else{
                    ptr.next2 = end;
                }
                ptr.end = false;              //setting the previous end state to false
                break;
            }
            //determines where to traverse, either through .next or the .next2
            ptr = reTraverse(ptr);
        }
        Node ptr2 = y;                        //handles traversing

        //traversing through the NFA then connect the new end state
        while(ptr2 != null){
            if(ptr2.end){
                ptr2.end = false;             //setting the previous end state to false
                ptr2.alph = 'E';

                //determine which pointer will be used to connect the new end state
                if (ptr2.next == null) {
                    ptr2.next = end;
                }
                else{
                    ptr2.next2 = end;
                }
                break;
            }
            ptr2 = reTraverse(ptr2);
        }
        //setting the previous start states to false
        x.start = false;
        y.start = false;

        //connecting the new start state to the previous start state by an epsilon transition
        start.next = x;
        start.next2 = y;
        return start;
    }
    public static void printing(){
        //prints the states in ascending order (including ties) using a Linked list
        Node findStart = stack.pop();
        if(findStart.start){
            System.out.println("Start: q" + findStart.state);       //prints the start state
        }
        stack.push(findStart);
        System.out.println("Accept: q" + count);                    //prints the accept state; the last state appended in the NFA should be the accept state of the whole NFA


        LinkedList<Node> newList = list;
        Iterator<Node> it = newList.iterator();                     //an iterator for the linked list
        for(int i = 0; i < newList.size() -1; i ++){                //iterates through the linked list
            Node prNode = it.next();

            //printing the state label, the alphabet it contains, and to what state it is connected
            System.out.println( "(q" + prNode.state + ", " + prNode.alph + ") ->" + " q" + prNode.next.state);
            if(prNode.next2 != null){
                System.out.println( "(q" + prNode.state + ", " + prNode.alph + ") ->" + " q" + prNode.next2.state);
            }
        }
        System.out.println(" ");
    }
    public static Node reTraverse(Node n){
        //determines where to traverse, either through .next or through the .next2
        //checks if the next state has lower state number than the current one; if so, it means it traversing back to the start state and needs to redirected

        Node ptr = n;
        if(ptr.state > ptr.next.state){
            ptr = ptr.next2;
        }
        else {
            ptr = ptr.next;
        }
        return ptr;
    }
    public static void ifValidPop(int x, char y) {
        //checks if popping is valid
        if (x < 2) {
            if (y != '*' || x < 1){
                System.err.println("Error: Nothing to pop from stack or Invalid order of operand/operator");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args){
        try {
            Scanner input = new Scanner(new File(args[0]));         //grabs the file from the command line
            while(input.hasNextLine()){
                String s = input.nextLine();
                System.out.println("RE: "+ s);                      //prints the post fixed regular expression
                count = 0;

                for(int i = 0; i < s.length(); i ++){               //for each character in the string

                    //The Alphabet is {a,b,c,d,e} and E represents the Epsilon transition
                    if(s.charAt(i) == 'a' || s.charAt(i) == 'b' || s.charAt(i) == 'c' || s.charAt(i) == 'd'|| s.charAt(i) == 'e' || s.charAt(i) == 'E'){      //if the alphabet read is valid
                        //creates initial start state and accept state
                        Node newN = new Node(s.charAt(i), ++count, true, false);
                        Node newN2 = new Node(' ', ++count, false, true);

                        newN.next = newN2;                      //connects the start and accept state
                        stack.push(newN);                       //pushing the NFA in stack

                        //adding the Nodes to the linked list; keeps track of the order of states
                        list.add(newN);
                        list.add(newN2);
                        //break;
                    }
                    else if(s.charAt(i) == '|'){                //if finds | for union
                        ifValidPop(stack.size(),s.charAt(i));
                        Node temp1 = stack.pop();               //pop NFA
                        Node temp2 = stack.pop();
                        Node rslt = union(temp2, temp1);
                        stack.push(rslt);                       //pushing NFA to stack
                    }
                    else if(s.charAt(i) == '&'){                //if finds & for concatenating
                        ifValidPop(stack.size(),s.charAt(i));
                        Node temp1 = stack.pop();               //pop NFA
                        Node temp2 = stack.pop();               //pop NFA
                        Node rslt = concatenate(temp2, temp1);
                        stack.push(rslt);                       //push NFA to stack

                    }
                    else if(s.charAt(i) == '*') {               //if finds start for kleene star
                        ifValidPop(stack.size(),s.charAt(i));
                        Node temp = stack.pop();                //pop NFA
                        Node rslt = kleene(temp);
                        stack.push(rslt);                       //push NFA
                    }
                    else{
                        //error check if the input is valid or not
                        System.err.println("Error Invalid Alphabet or Symbol Input: " + s.charAt(i) + "\n");
                        stack.clear();
                        System.exit(0);
                    }
                }
                if(stack.size() == 1) {                           //checks if there an NFA inside the stack
                    printing();                                 //prints the NFA
                    stack.clear();                              //clear stack
                    list.clear();                               //clear list
                }
            }
            input.close();
        }
        catch(Exception e){
            //catches error for the input file
            System.err.println("Error: " + e.getMessage());
        }
    }
}
