package com.liangyuen.pi4j_rc522;

import java.util.ArrayList;
/**
 * A container for an array of bytes.
 * 
 * @author Alec Leamas
 *
 */
public class ByteArray {
    
    /** The actual list of bytes. */
    protected ArrayList<Byte> bytes = new ArrayList<Byte>();
    
    /** Internal state in parse(). */
    protected  enum State { DELIM, NIBBLE_1, NIBBLE_2 };
    
    protected char hexdigit(int num) 
    {
        if (num >= 0 && num <= 9)
            return (char)((int)'0' + num);
        else if (num >=10 && num <= 15 )
            return (char)((int)'a' + num - 10);
        else throw new 
            IllegalArgumentException("Cannot convert " + num + " to hex");
    }
    
    protected int hexvalue(char c)
    {
        c = Character.toLowerCase(c);
        int  retval = 0;
        if (c >= '0' && c <= '9')
            retval = (int)c - (int)'0';
        else if (c >= 'a' && c <= 'f')
            retval = (int)c - (int)'a' + 10;
        else 
            return -1;
        return (byte) (retval & 0x00ff);
    }
    
    /** Return the internal list of bytes as an array. */
    public byte[] toBytes()
    {
        byte[] returnBytes = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i += 1)
            returnBytes[i] = (Byte) bytes.toArray()[i];
        return returnBytes;
    }
    
    /** Convert som bytes in list to a printable representation
     * 
     * @param delim, delimiter printed between each byte (possibly empty).
     *  */
    public String toString(String delim)
    {
        ArrayList<String> stringBytes = new ArrayList<String>();
        for (Byte b : bytes) {
            int intVal = b & 0x00ff;
            String stringByte = "";
            stringByte += (hexdigit(intVal % 16));
            stringByte += (hexdigit(intVal / 16));
            stringBytes.add(stringByte);
        }
        return String.join(delim == null ? "" : delim,  stringBytes);
    }

    /** Return printable representation of array without delimiters. */
    public String toString() 
    {
        return toString(null);
    }
    
    /**
     * Parse a text string and update internal array with parsed values.
     * 
     * The parsing is lax, anything not deemed as a hex digit isignored.
     * @param string
     */
    public void parse(String string)
    {
        State state = State.DELIM;
        int lowNibble = 0;
        for (int ix = 0; ix < string.length(); ix += 1)
        {
            int value = hexvalue(string.charAt(ix));
            switch (state) {
                case DELIM:
                    if (value == -1) {
                        break;
                    }
                    // else fall through:
                case NIBBLE_1:
                    lowNibble = value;
                    state = State.NIBBLE_2;
                    break;
                case NIBBLE_2:    
                    state = State.DELIM;
                    if (value == -1) 
                        state = State.DELIM;
                    else   
                        bytes.add((byte) (16*value + lowNibble));
                    break;
            }
        }
        if (state == State.NIBBLE_2) {
            bytes.add((byte)lowNibble);
        }
    }
    
    public ByteArray(byte[] bytes)
    {
        this.bytes = new ArrayList<Byte>();
        for (byte b: bytes) {
            this.bytes.add(b);
        }
    }
    
    public ByteArray(String bytes)
    {
        parse(bytes);
    }
    
    public static void main(String[] args) 
    {
        ByteArray bytes = new ByteArray("f1:f2:f3:f4");
        System.out.println("Parsed: " + bytes.toString("-"));
    }
}
