package com.liangyuen.pi4j_rc522;

/** A block of data and some bits. */
public class Block extends ByteArray {

    protected int backBits;

    /**
     * Construct a block with just some bytes.
     *
     * @param bytes Block content
     */
    public Block(byte[] bytes) {
        super(bytes);
        backBits = 0;
    }

    /** @return The bits part */
    public int getBackBits() {
        return backBits;
    }

    /**
     * Construct a block with some bytes and some bits.
     *
     * @param bytes The complete bytes.
     * @param backBits Extra bits.
     */
    public Block(byte[] bytes, int backBits) {
        super(bytes);
        this.backBits = backBits;
    }
}
