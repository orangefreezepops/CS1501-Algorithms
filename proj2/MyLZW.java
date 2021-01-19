
/**
 * ***********************************************************************
 *  Compilation:  javac MyLZW.java
 *  Execution:    java MyLZW - (choose mode r, n, or m) < input.txt > input.lzw
 * (compress) Execution: java MyLZW + < input.lzw > input.txt (expand)
 * Dependencies: BinaryIn.java BinaryOut.java TSTSB.java (TST modified for
 * string builders)
 *
 *
 *
 ************************************************************************
 */
public class MyLZW {

    private static final int R = 256;                   //size of Radix

    private static final int MIN_L = 512; 		//minimum codewords 2^9
    private static final int MAX_L = 65536;             //maximum codewords 2^16
    private static int L = MIN_L; 			//start at lower bound

    private static final int MIN_W = 9; 		//minimum width
    private static final int MAX_W = 16; 		//maximum width
    private static int W = MIN_W; 			//start at lower

    private static String mode = "n"; 			//compression mode
    private static char decompressMode = 'n';           //firts char read at beginning of file

    private static double compressionRatio = 0.0;       //ratio of uncompressed/compressed (-) or compressed/uncompressed (+)
    private static double prevRatio = 0.0;              //place holder for previous compression ratio
    private static int uncompressedFileSize = 0;        //uncompressed file size
    private static int compressedFileSize = 0;          //compressed file size
    private static final double THRESHHOLD = 1.1;       //monitor compression ratio

    public static void compress() {
        BinaryStdOut.write(mode, 8); //write the compression mode
        System.err.println("mode = " + mode);
        StringBuilder pattern; //working prefix
        StringBuilder input = new StringBuilder();
        TSTSB<Integer> st = new TSTSB<>(); //symbol table

        //initialize the symbol table to all possible ascii chars
        for (int i = 0; i < R; i++) {
            st.put(new StringBuilder("" + (char) i), i);
        }
        int codeWords = R + 1;  // # of current codewords
        pattern = retrieveNext(); //reading the next character and putting it in a string builder

        while (pattern != null) {
            StringBuilder s = st.longestPrefixOf(pattern);  //Find max prefix match s.
            StringBuilder prevPat = null;                   //previous pattern
            StringBuilder prevS = null;                     //previous string

            while (s.length() == pattern.length()) {
                input = retrieveNext();             //get next char read
                prevPat = pattern;                  //assign prev patter to current pattern
                prevS = s;                          //assign prev string to current max prefix match
                pattern.append(input);              //append the new char to the pattern
                s = st.longestPrefixOf(pattern);    //get longest prefix of new pattern
            }
            uncompressedFileSize += s.length() * 8; //+= each character * 8 bits per char
            compressedFileSize += W;                //+= compressed codeword width
            compressionRatio = uncompressedFileSize / compressedFileSize;
            BinaryStdOut.write(st.get(prevS), W);   // Print s's encoding.

            int t = s.length();
            //if length of the current prefix is less than the input and 
            //the number of codewords is < the allowed number
            if (t < pattern.length() && codeWords < L) {
                // Add s to symbol table.
                st.put(pattern, codeWords++);
            }

            //check the width of the codeword and increase if codeWords is 
            //equal to the possible number of codewords for that width
            if ((W < MAX_W) && codeWords == L) {
                W++;                            //increment to the next width
                L = (int) Math.pow(2, W);       //update L
                st.put(pattern, codeWords++);
            }

            //if you've reached the max codewords and max width
            if (W == MAX_W && codeWords == MAX_L) {

                if (mode.equals("r")) {  //check the reset flag
                    System.err.println("codebook filled up, resetting dict");
                    st = new TSTSB<>(); //reset the symbol table
                    for (int i = 0; i < R; i++) {
                        st.put(new StringBuilder().append((char) i), i); //initialize it
                    }
                    codeWords = R + 1;  //reset codeWords, width and # of cw's allowed 
                    W = MIN_W;
                    L = MIN_L;
                }

                if (mode.equals("m")) {  //monitor mode
                    if (prevRatio == 0.0) {
                        prevRatio = compressionRatio; //make the old ratio = to current, bc its the first
                    } else if ((prevRatio / compressionRatio) > THRESHHOLD) { //if the compression has degraded past threshhold
                        System.err.println(prevRatio / compressionRatio);
                        System.err.println("monitor mode passed compression ratio threshhold of 1.1, resetting symbol table");
                        st = new TSTSB<>(); //reset the symbol table
                        for (int i = 0; i < R; i++) {
                            st.put(new StringBuilder().append((char) i), i); //initialize it
                        }
                        codeWords = R + 1;  //reset codeWords, width and # of cw's allowed 
                        W = MIN_W;
                        L = MIN_L;
                        prevRatio = 0.0;
                        compressionRatio = 0.0;
                    }
                }
            }
            pattern = input;// Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
        
        System.err.println(uncompressedFileSize);
        System.err.println(compressedFileSize);
        System.err.println((double) prevRatio / compressionRatio);
    }

    private static StringBuilder retrieveNext() {
        char pat;
        StringBuilder list = new StringBuilder();
        try {
            pat = BinaryStdIn.readChar();
            list.append(pat);
        } catch (Exception e) {
            list = null;
        }
        return list;
    }

    public static void expand() {
        decompressMode = BinaryStdIn.readChar(8); //read how the file was compressed
        System.err.println("was compressed using " + decompressMode + " mode.");
        String[] symbol_table = new String[MAX_L];
        int i; // next available codeword value

        for (i = 0; i < R; i++) {
            symbol_table[i] = "" + (char) i;    //initialize symbol table with all 1-character strings
        }
        symbol_table[i++] = "";                 //lookahead for EOF
        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) {
            return; //at the end of the Radix == empty string
        }
        String value = symbol_table[codeword];

        while (true) {
            BinaryStdOut.write(value);
            codeword = BinaryStdIn.readInt(W);

            //for monitoring the compression
            uncompressedFileSize = value.length() * 8;
            compressedFileSize += W;
            compressionRatio = uncompressedFileSize / compressedFileSize;

            if (codeword == R) {
                break;
            }
            String s = symbol_table[codeword];
            if (i == codeword) {
                s = value + value.charAt(0);   // special case hack
            }
            if (i < L - 1) {
                symbol_table[i++] = value + s.charAt(0);

            }
            if (W < MAX_W && i == (L - 1)) {
                symbol_table[i++] = value + s.charAt(0);
                W++;
                L = (int) Math.pow(2, W);
            }
            if (i == MAX_L) {
                if (decompressMode == 'r') { //file was compressed using reset
                    L = MIN_L;
                    W = MIN_W;
                    symbol_table = new String[MAX_L];
                    for (i = 0; i < R; i++) {
                        symbol_table[i] = "" + (char) i;
                    }
                    symbol_table[i++] = "";
                    codeword = BinaryStdIn.readInt(W);
                    if (codeword == R) {
                        break;
                    }
                    value = s;
                } else if (decompressMode == 'm') { //file was compressed using monitor
                    if (prevRatio == 0.0) {
                        prevRatio = compressionRatio;
                    }
                    if ((compressionRatio / prevRatio) > THRESHHOLD) {
                        W = MIN_W;
                        L = MIN_L;
                        symbol_table = new String[MAX_L];
                        for (i = 0; i < R; i++) {
                            symbol_table[i] = "" + (char) i;
                        }
                        symbol_table[i++] = "";
                        codeword = BinaryStdIn.readInt(W);
                        if (codeword == R) {
                            break;
                        }
                        value = s;

                        prevRatio = 0.0;
                        compressionRatio = 0.0;
                    }
                }
            }
            value = s;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args[0].equals("-")) {
            if (args[1].equals("r")) {		//reset
                mode = "r";
                compress();
            } else if (args[1].equals("n")) {	//do nothing
                mode = "n";
                compress();
            } else if (args[1].equals("m")) {	//monitor
                mode = "m";
                compress();
            } else {
                throw new RuntimeException("Illegal command line argument 2..."
                        + "must be either an 'n' (standard mode), an 'r' (reset mode) or an 'm' (monitor mode)");
            }
        } else if (args[0].equals("+")) {
            expand();
        } else {
            throw new RuntimeException("Illegal command line argument 1..."
                    + "must either be a '-' (compress) or a '+' (expand)");
        }
    }
}
