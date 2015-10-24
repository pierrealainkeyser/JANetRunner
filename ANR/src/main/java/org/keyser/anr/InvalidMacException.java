package org.keyser.anr;

public class InvalidMacException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1942126013840146412L;

	private byte[] expectedMac;

	private byte[] calculatedMac;

	public InvalidMacException(byte[] expectedMac, byte[] calculatedMac) {
		this.expectedMac = expectedMac;
		this.calculatedMac = calculatedMac;
	}

	public byte[] getExpectedMac() {
		return expectedMac;
	}

	public byte[] getCalculatedMac() {
		return calculatedMac;
	}
}
