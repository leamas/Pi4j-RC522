package com.liangyuen.pi4j_rc522;

/** A union of a 5-byte tag and an error code container. */
public class Uid extends ByteArray {

    private int errorCode;

    /**
     * Reflects if the errorCode is != 0.
     *
     * @return True if the Uid contains an error code and non-valid data.
     */
    public boolean isFailed( ) {
        return errorCode != 0;
    }

    /**
     * Return the errorCode, typically from trying to get a tag.
     *
     * @return The errorCode used to construct the Uid, 0 if constructed
     *         with valid data.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Construct a valid, no failed key form a five byte array.
     *
     * @param bytes Five bytes of tag data.
     */
    public Uid(byte[] bytes) {
        super(bytes);
        errorCode = 0;
        int crc = 0;

        if (bytes.length != 5)
            throw new IllegalArgumentException("Bad UID length: "
                                               + bytes.length);
        for (int i = 0; i < 4; i++)
            crc ^= bytes[i];
        if (crc != bytes[4])
            errorCode = RaspRC522.MI_CRC_ERR;
    }

    /**
     * Construct a failed Uid from an error code.
     *
     * @param  errorCode A pi4j error code.
    */
    public Uid(int errorCode) {
        super(new byte[0]);
        this.errorCode = errorCode;
    }

}
