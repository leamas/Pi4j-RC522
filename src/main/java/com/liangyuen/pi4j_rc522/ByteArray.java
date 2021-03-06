package com.liangyuen.pi4j_rc522;

import java.util.ArrayList;
/**
 * A container for an array of bytes.
 *
 * @author Alec Leamas
 *
 */
public class ByteArray {

    protected static final String HEXDIGITS = "01234567890abcdef";

    /** The actual list of bytes. */
    protected ArrayList<Byte> bytes = new ArrayList<Byte>();

    /** Internal state in parse(). */
    protected  enum ParseState {NIBBLE_1, NIBBLE_2};

    /** Return the internal list of bytes as an array. */
    public byte[] toBytes()
    {
        byte[] returnBytes = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i += 1)
            returnBytes[i] = (Byte) bytes.toArray()[i];
        return returnBytes;
    }

    /**
     * Convert bytes in list to a printable representation
     *
     * @param delim Delimiter printed between each byte, possibly empty
     *              or null.
     */
    public String toString(String delim)
    {
        ArrayList<String> stringBytes = new ArrayList<String>();
        for (Byte b : bytes) {
            int intVal = b & 0x00ff;
            String s =  "" + HEXDIGITS.charAt(intVal % 16);
            s += HEXDIGITS.charAt(intVal / 16);
            stringBytes.add(s);
        }
        return String.join(delim == null ? "" : delim, stringBytes);
    }

    /** Return printable representation of array without delimiters. */
    public String toString()
    {
        return toString(null);
    }

    /**
     * Parse a text string and update internal array with parsed values.
     * The parsing is lax, anything not deemed as a hex digit is ignored.
     *
     * @param string The parsed data, hex digits and other characters used
     *               as delimiters.
     */
    public void parse(String string)
    {
        ParseState state = ParseState.NIBBLE_1;
        int lowNibble = 0;
        for (char ch: string.toCharArray()) {
            int value = HEXDIGITS.indexOf(Character.toLowerCase(ch));
            switch (state) {
                case NIBBLE_1:
                    if (value == -1)
                        break;
                    lowNibble = value;
                    state = ParseState.NIBBLE_2;
                    break;
                case NIBBLE_2:
                    state = ParseState.NIBBLE_1;
                    if (value == -1)
                        bytes.add((byte) lowNibble);
                    else
                        bytes.add((byte) (lowNibble + 16*value));
                    break;
            }
        }
        if (state == ParseState.NIBBLE_2) {
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
