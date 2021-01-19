
import java.util.Random;

public class HeftyInteger {

    private final byte[] ONE = {(byte) 1};
    private final byte[] ZERO = {(byte) 0};

    private byte[] val;

    /**
     * Construct the HeftyInteger from a given byte array
     *
     * @param b the byte array that this HeftyInteger should represent
     */
    public HeftyInteger(byte[] b) {
        val = b;
    }

    /**
     * Return this HeftyInteger's val
     *
     * @return val
     */
    public byte[] getVal() {
        return val;
    }

    /**
     * Return the number of bytes in val
     *
     * @return length of the val byte array
     */
    public int length() {
        return val.length;
    }

    /**
     * Add a new byte as the most significant in this
     *
     * @param extension the byte to place as most significant
     */
    public void extend(byte extension) {
        byte[] newv = new byte[val.length + 1];
        newv[0] = extension;
        for (int i = 0; i < val.length; i++) {
            newv[i + 1] = val[i];
        }
        val = newv;
    }

    /**
     * If this is negative, most significant bit will be 1 meaning most
     * significant byte will be a negative signed number
     *
     * @return true if this is negative, false if positive
     */
    public boolean isNegative() {
        return (val[0] < 0);
    }

    /**
     * Computes the sum of this and other
     *
     * @param other the other HeftyInteger to sum with this
     */
    public HeftyInteger add(HeftyInteger other) {
        byte[] a, b;
        // If operands are of different sizes, put larger first ...
        if (val.length < other.length()) {
            a = other.getVal();
            b = val;
        } else {
            a = val;
            b = other.getVal();
        }

        // ... and normalize size for convenience
        if (b.length < a.length) {
            int diff = a.length - b.length;

            byte pad = (byte) 0;
            if (b[0] < 0) {
                pad = (byte) 0xFF;
            }

            byte[] newb = new byte[a.length];
            for (int i = 0; i < diff; i++) {
                newb[i] = pad;
            }

            for (int i = 0; i < b.length; i++) {
                newb[i + diff] = b[i];
            }

            b = newb;
        }

        // Actually compute the add
        int carry = 0;
        byte[] res = new byte[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            // Be sure to bitmask so that cast of negative bytes does not
            //  introduce spurious 1 bits into result of cast
            carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

            // Assign to next byte
            res[i] = (byte) (carry & 0xFF);

            // Carry remainder over to next byte (always want to shift in 0s)
            carry = carry >>> 8;
        }

        HeftyInteger res_li = new HeftyInteger(res);

        // If both operands are positive, magnitude could increase as a result
        //  of addition
        if (!this.isNegative() && !other.isNegative()) {
            // If we have either a leftover carry value or we used the last
            //  bit in the most significant byte, we need to extend the result
            if (res_li.isNegative()) {
                res_li.extend((byte) carry);
            }
        } // Magnitude could also increase if both operands are negative
        else if (this.isNegative() && other.isNegative()) {
            if (!res_li.isNegative()) {
                res_li.extend((byte) 0xFF);
            }
        }

        // Note that result will always be the same size as biggest input
        //  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
        return res_li;
    }

    /**
     * Negate val using two's complement representation
     *
     * @return negation of this
     */
    public HeftyInteger negate() {
        byte[] neg = new byte[val.length];
        int offset = 0;

        // Check to ensure we can represent negation in same length
        //  (e.g., -128 can be represented in 8 bits using two's
        //  complement, +128 requires 9)
        if (val[0] == (byte) 0x80) { // 0x80 is 10000000
            boolean needs_ex = true;
            for (int i = 1; i < val.length; i++) {
                if (val[i] != (byte) 0) {
                    needs_ex = false;
                    break;
                }
            }
            // if first byte is 0x80 and all others are 0, must extend
            if (needs_ex) {
                neg = new byte[val.length + 1];
                neg[0] = (byte) 0;
                offset = 1;
            }
        }

        // flip all bits
        for (int i = 0; i < val.length; i++) {
            neg[i + offset] = (byte) ~val[i];
        }

        HeftyInteger neg_li = new HeftyInteger(neg);

        // add 1 to complete two's complement negation
        return neg_li.add(new HeftyInteger(ONE));
    }

    /**
     * Implement subtraction as simply negation and addition
     *
     * @param other HeftyInteger to subtract from this
     * @return difference of this and other
     */
    public HeftyInteger subtract(HeftyInteger other) {
        return this.add(other.negate());
    }

    /**
     * Compute the product of this and other
     *
     * @param other HeftyInteger to multiply by this
     * @return product of this and other
     */
    public HeftyInteger multiply(HeftyInteger other) {
        // YOUR CODE HERE (replace the return, too...)
        HeftyInteger thisInt, otherInt, result;
        boolean negative_result = false;

        result = new HeftyInteger(ZERO); //intitialize to zero

        //check if this or other are 0 bc the answer will be zero
        if (this.checkZero() || other.checkZero()) {
            return result; //bc its still zero at this point
        }

        //do the operands vary in length?
        //if this is the greater one or they're equal length
        if (val.length >= other.length()) {
            thisInt = this;
            otherInt = other;
        } else {
            //make the larger one first
            thisInt = other;
            otherInt = this;
        }

        //check to see if other operand is negative
        if (otherInt.isNegative() && !thisInt.isNegative()) {
            otherInt = otherInt.negate();
            negative_result = true;
        }
        //check to see if this operand is negative
        if (thisInt.isNegative() && !otherInt.isNegative()) {
            thisInt = thisInt.negate();
            negative_result = true;
        }
        //check to see if both are negative
        if (otherInt.isNegative() && thisInt.isNegative()) {
            otherInt = otherInt.negate();
            thisInt = thisInt.negate();
        }

        result = gs_algo(thisInt, otherInt); //run gradeschool algorithm 
        return negative_result ? result.negate() : result;
    }

    public boolean checkZero() {
        //if any byte in the val array is 1, the number is not zero
        for (byte b : val) {
            if (b != 0) {
                return false;
            }
        }
        //otherwise zero 
        return true;
    }

    //gradeschool multiplication algorithm
    public HeftyInteger gs_algo(HeftyInteger a, HeftyInteger b) {
        HeftyInteger result = new HeftyInteger(ZERO); //result of the multiplication
        byte[] valArr = b.getVal(); //val array for the second operator, who will be shifted each addition
        String valString = toBitString(valArr); //cast to a string for easier manipulation
        int len = valString.length() - 1; //number of positions to shift for the multiplication

        //starting from the lowest order bit
        for (int i = len; i >= 0; i--) {
            //if the char at this position in the bitstring is equal to 1 
            if (valString.charAt(i) == '1') {
                result = result.add(a);
            }
            //add to itself
            a = a.add(a);
        }
        //donezo
        return result;
    }

    //convert the byte string into a String
    public String toBitString(byte[] b) {
        //new character array to change into string
        char[] bitstring = new char[8 * b.length];
        //for each position in the byte array
        for (int pos = 0; pos < b.length; pos++) {
            //current byte is 
            byte curbyte = b[pos];
            int posbyte = pos << 3; // left shift 3 positions to pad bits
            int mask = 0x1; //to isolate the bits
            //starting from least significant bit work your way up the byte string
            for (int j = 7; j >= 0; j--) {
                int value = curbyte & mask; //isolate bit
                //set the index in the bitstring to the corresponding bit value 
                if (value == 0) {
                    bitstring[posbyte + j] = '0';
                } else {
                    bitstring[posbyte + j] = '1';
                }
                //shift the mask to the left one so it can isolate the next bit
                mask <<= 1;
            }
        }
        //return the string representation
        return String.valueOf(bitstring);
    }

    /**
     * Run the extended Euclidean algorithm on this and other
     *
     * @param other another HeftyInteger
     * @return an array structured as follows: 0: the GCD of this and other 1: a
     * valid x value 2: a valid y value such that this * x + other * y == GCD in
     * index 0
     */
    public HeftyInteger[] XGCD(HeftyInteger other) {
        // YOUR CODE HERE (replace the return, too...)
        //don't gotta worry about negatories 
        HeftyInteger thisInt, otherInt, gcd, x, y;
        HeftyInteger[] result = new HeftyInteger[3];
        thisInt = this;
        otherInt = other;

        //XGCD base case
        //make sure other is not zero
        if (otherInt.checkZero()) {
            //if it is return [this, 1, 0]
            return new HeftyInteger[]{thisInt, new HeftyInteger(ONE), new HeftyInteger(ZERO)};
        }

        //otherwise recurse
        result = otherInt.XGCD(thisInt.modulo(otherInt));

        //grab the individual pieces
        gcd = result[0];
        x = result[2];
        y = result[1].subtract((thisInt.divide(thisInt, otherInt)).multiply(result[2]));

        //set the result
        return new HeftyInteger[]{gcd, x, y};
    }

    public HeftyInteger modulo(HeftyInteger other) {
        HeftyInteger thisInt = this;
        HeftyInteger otherInt = other;
        HeftyInteger quot; //quotient for division

        //divide the two
        quot = thisInt.divide(thisInt, otherInt);

        //return the difference of this and quotient*other
        //e.g. the remainder after division
        return thisInt.subtract((quot.multiply(otherInt)));
    }

    public HeftyInteger divide(HeftyInteger dividend, HeftyInteger divisor) {
        int count;
        HeftyInteger quot = new HeftyInteger(ZERO);
        //subtract the divisor from the dividend as many times as possible 
        //before it becomes negative
        while (!(dividend.subtract(divisor)).isNegative()) {
            HeftyInteger newDivisor = divisor; //initialize new divisor
            count = 0; //start shift count
            while (true) {
                //if the current subtraction will make the quotient go negative
                if ((dividend.subtract(newDivisor.leftShift(1))).isNegative()) {
                    break;
                }
                //otherwise continue shifting
                newDivisor = newDivisor.leftShift(1);
                //incriment count
                count++;
            }
            //after the loop the new divisor will be shifted as far as it can
            dividend = dividend.subtract(newDivisor);
            //add 1 << count to the quotient for this iteration
            quot = quot.add(new HeftyInteger(ONE).leftShift(count));
        }
        //finito
        return quot;
    }

    public HeftyInteger leftShift(int amount) {
        HeftyInteger thisInt = this;
        for (int i = 0; i < amount; i++) {
            thisInt = thisInt.add(thisInt);
        }
        return thisInt;
    }
}
