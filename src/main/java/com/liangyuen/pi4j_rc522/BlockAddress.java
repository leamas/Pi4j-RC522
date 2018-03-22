package com.liangyuen.pi4j_rc522;

/** A sector number 0..15 and a block number 0..3 encoded in a byte, */
public class BlockAddress {
	
	private byte address;

    /**
     * Get computed address.
     *
     * @return sector*4 + block
     */
    public byte toByte() {
        return address;
    }

    public void setBlock(int block) {
    	address = (byte) ((address & 0x00fc) + block);
    }
    /**
     * Construct a BlockAddress
     *
     * @param sector Card sector, in range 0..15.
     * @param block Block number, in range 0..3.
     */
    public BlockAddress(int sector, int block) {
        if (sector < 0 || sector > 15 || block < 0 || block > 3)
            throw new IllegalArgumentException("Illegal sector or block");
        this.address =  (byte) (sector * 4 + block);
    }

    /**
     * Construct a BlockAddress in sector 0.
     *
     * @param block Block number, in range 0..3.
     */
    public BlockAddress(int block) {
        this(0, block);
    }

}
