package org.keyser.anr;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Mac;


public class MacInputStream extends FilterInputStream {

    /* NOTE: This should be made a generic UpdaterInputStream */

    /* Are we on or off? */
    private boolean on = true;

    /**
     * The message digest associated with this stream.
     */
    protected Mac mac;

    /**
     * Creates a digest input stream, using the specified input stream
     * and message digest.
     *
     * @param stream the input stream.
     *
     * @param mac the message digest to associate with this stream.
     */
    public MacInputStream(InputStream stream, Mac mac) {
        super(stream);
        setMac(mac);
    }

    /**
     * Returns the message digest associated with this stream.
     *
     * @return the message digest associated with this stream.
     * @see #setMac(java.security.Mac)
     */
    public Mac getMac() {
        return mac;
    }

    /**
     * Associates the specified message digest with this stream.
     *
     * @param digest the message digest to be associated with this stream.
     * @see #getMac()
     */
    public void setMac(Mac digest) {
        this.mac = digest;
    }

    /**
     * Reads a byte, and updates the message digest (if the digest
     * function is on).  That is, this method reads a byte from the
     * input stream, blocking until the byte is actually read. If the
     * digest function is on (see {@link #on(boolean) on}), this method
     * will then call {@code update} on the message digest associated
     * with this stream, passing it the byte read.
     *
     * @return the byte read.
     *
     * @exception IOException if an I/O error occurs.
     *
     * @see Mac#update(byte)
     */
    public int read() throws IOException {
        int ch = in.read();
        if (on && ch != -1) {
            mac.update((byte)ch);
        }
        return ch;
    }

    /**
     * Reads into a byte array, and updates the message digest (if the
     * digest function is on).  That is, this method reads up to
     * {@code len} bytes from the input stream into the array
     * {@code b}, starting at offset {@code off}. This method
     * blocks until the data is actually
     * read. If the digest function is on (see
     * {@link #on(boolean) on}), this method will then call {@code update}
     * on the message digest associated with this stream, passing it
     * the data.
     *
     * @param b the array into which the data is read.
     *
     * @param off the starting offset into {@code b} of where the
     * data should be placed.
     *
     * @param len the maximum number of bytes to be read from the input
     * stream into b, starting at offset {@code off}.
     *
     * @return  the actual number of bytes read. This is less than
     * {@code len} if the end of the stream is reached prior to
     * reading {@code len} bytes. -1 is returned if no bytes were
     * read because the end of the stream had already been reached when
     * the call was made.
     *
     * @exception IOException if an I/O error occurs.
     *
     * @see Mac#update(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int result = in.read(b, off, len);
        if (on && result != -1) {
            mac.update(b, off, result);
        }
        return result;
    }

    /**
     * Turns the digest function on or off. The default is on.  When
     * it is on, a call to one of the {@code read} methods results in an
     * update on the message digest.  But when it is off, the message
     * digest is not updated.
     *
     * @param on true to turn the digest function on, false to turn
     * it off.
     */
    public void on(boolean on) {
        this.on = on;
    }

    /**
     * Prints a string representation of this digest input stream and
     * its associated message digest object.
     */
     public String toString() {
         return "[Digest Input Stream] " + mac.toString();
     }
}