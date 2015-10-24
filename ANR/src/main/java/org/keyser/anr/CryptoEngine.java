package org.keyser.anr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.output.CloseShieldOutputStream;

public class CryptoEngine {

	public static class Uncipherer {
		private final Cipher cipher;

		private final Mac mac;

		private final SecretKey secretKey;

		private Uncipherer(Cipher cipher, Mac mac, SecretKey secretKey) {
			this.cipher = cipher;
			this.mac = mac;
			this.secretKey = secretKey;
		}

		public void copy(InputStream input, long streamLength, OutputStream output) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidMacException {

			int macLength = mac.getMacLength();
			int blockSize = cipher.getBlockSize();

			// limitation de la taille lu pour lire le HMAC en dernier
			long limit = streamLength - (long) macLength;
			MacInputStream macInputStream = new MacInputStream(new BoundedInputStream(input, limit), mac);

			// les premiers bytes sont les vecteur d'initaliastion
			byte[] iv = new byte[blockSize];
			IOUtils.readFully(macInputStream, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

			CipherInputStream cipherInputStream = new CipherInputStream(macInputStream, cipher);
			IOUtils.copy(cipherInputStream, output);
			output.flush();

			// vérification du HMAc
			byte[] expectedMacBytes = new byte[macLength];
			IOUtils.readFully(input, expectedMacBytes);

			byte[] calculatedMac = macInputStream.getMac().doFinal();

			if (!Arrays.equals(expectedMacBytes, calculatedMac)) {
				throw new InvalidMacException(expectedMacBytes, calculatedMac);
			}
		}
	}

	public static class Cipherer {
		private final Cipher cipher;

		private final byte[] iv;

		private final Mac mac;

		private final SecretKey secretKey;

		private Cipherer(Cipher cipher, Mac mac, byte[] iv, SecretKey secretKey) {
			this.cipher = cipher;
			this.mac = mac;
			this.iv = iv;
			this.secretKey = secretKey;
		}

		public void copy(InputStream input, OutputStream output) throws IOException {

			// ecriture de l'IV (couvert par le MAC)
			MacOutputStream macOutputSteam = new MacOutputStream(output, mac);
			macOutputSteam.write(iv);
			macOutputSteam.flush();

			// chiffrement des données
			CipherOutputStream cipherOutputStream = new CipherOutputStream(new CloseShieldOutputStream(macOutputSteam), cipher);
			IOUtils.copy(input, cipherOutputStream);
			cipherOutputStream.close();

			// rajout du HMAC en fin de stream
			byte[] hmac = macOutputSteam.getMac().doFinal();
			output.write(hmac);
			output.flush();
		}

		public SecretKey getSecretKey() {
			return secretKey;
		}
	}

	public static void main(String[] args) throws Exception {
		CryptoEngine ce = new CryptoEngine("AES/CBC/PKCS5Padding", "HmacSHA256");

		Cipherer cipherer = ce.initEncrypt(128);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String clearText = "yeah trop cool";
		cipherer.copy(new ByteArrayInputStream(clearText.getBytes()), baos);
		SecretKey secretKey = cipherer.getSecretKey();

		byte[] ciphered = baos.toByteArray();

		// ciphered[ciphered.length - 1] = 0x12;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ce.initDecrypt(secretKey).copy(new ByteArrayInputStream(ciphered), ciphered.length, output);

		System.out.println(ciphered.length + " " + output.size());

		System.out.println(new String(output.toByteArray()));

	}

	private final String hmacAlgorithm;

	private final String transformation;

	public CryptoEngine(String transformation, String hmacAlgorithm) {
		this.transformation = transformation;
		this.hmacAlgorithm = hmacAlgorithm;
	}

	public Uncipherer initDecrypt(SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance(transformation);
		Mac mac = Mac.getInstance(hmacAlgorithm);
		mac.init(secretKey);

		return new Uncipherer(cipher, mac, secretKey);
	}

	public Cipherer initEncrypt(int keyLen) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException {

		Cipher cipher = Cipher.getInstance(transformation);
		Mac mac = Mac.getInstance(hmacAlgorithm);

		AlgorithmParameters parameters = cipher.getParameters();
		KeyGenerator keyGenerator = KeyGenerator.getInstance(parameters.getAlgorithm());
		keyGenerator.init(keyLen);

		SecretKey secretKey = keyGenerator.generateKey();
		IvParameterSpec parameterSpec = parameters.getParameterSpec(IvParameterSpec.class);

		cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

		mac.init(secretKey);

		return new Cipherer(cipher, mac, parameterSpec.getIV(), secretKey);

	}
}
