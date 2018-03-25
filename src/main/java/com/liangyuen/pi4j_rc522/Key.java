package com.liangyuen.pi4j_rc522;

/** A six-byte key used to encrypt/decrypt tag data. */
public class Key extends ByteArray {
	
	public byte[] toBytes() throws RC522Exception {
		if (bytes.size() != 6)
			throw new RC522Exception(RaspRC522.MI_ERR, 
					                 "ILlegal keylength (must be 6)");
		return super.toBytes();
    }

	/**
	 * Construct a Key from a text string.
	 * 
	 * @param bytes String with groups of two hexadecimal digits, one 
	 *              per byte, and possible delimiters in between. 
	 */
	public Key(String bytes) {
		super(bytes);
	}
}
