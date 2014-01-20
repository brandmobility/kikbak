package com.kikbak.client.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CryptoUtils {

	/** Base 64 Encoder/Decoder */
	private static final int NO_LINE_BREAKS = -1;
	private static final byte[] NO_END_OF_LINE_CHARS = new byte[0];

	/** Part of the key */
	private static final byte[] TA = { 32, 12, 67, 20, 19, 111, 9, 13 };

	/** Default Initialization Vector for AES */
	private static final byte[] DEFAULT_IV = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	/** The algorithm used to encrypt and decrypt */
	private static final String SYMMETRIC_ALGORITHM = "AES";
	
	/** Cache UTF-8 character set **/
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");	

	@SuppressWarnings("serial")
	private static final Set<String> algorithmsWhichUseInitializationVector = new HashSet<String>() {{
		add("AES/CBC/PKCS5Padding");
	}};
	
	@SuppressWarnings("serial")
	private static final Map<String, String> keySpecAlgorithmNames = new HashMap<String, String>(){{
		put("AES/CBC/PKCS5Padding", "AES");
	}};

	
	public static String toUTF8String(byte[] bytes) {
	    return new String(bytes, UTF8_CHARSET);
	}

	public static byte[] fromUTF8String(String string) {
	    return string.getBytes(UTF8_CHARSET);
	}
	
	/** Returns an encrypted long integer as a base64 encoded string */
	public static String symetricEncrypt(final long clear)
			throws GeneralSecurityException {
		final ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putLong(clear);
		return symetricEncrypt(buf.array());
	}

	/** Returns a long integer decrypted from a base64 encoded string */
	public static long symetricDecryptToLong(final String base64)
			throws GeneralSecurityException {
		if (null == base64) {
			throw new IllegalArgumentException("base64 must not be null");
		}
		final byte[] clear = symetricDecrypt(base64, makeKey());
		final ByteBuffer buf = ByteBuffer.wrap(clear);
		return buf.getLong();
	}

	/** Returns an encrypted byte array as a base64 encoded string */
	public static String symetricEncrypt(final byte[] clear)
			throws GeneralSecurityException {
		return (null == clear) ? null : symetricEncrypt(clear, makeKey());
	}

	/** Returns a byte array decrypted from a base64 encoded string */
	public static byte[] symetricDecrypt(final String base64)
			throws GeneralSecurityException {
		return (null == base64) ? null : symetricDecrypt(base64, makeKey());
	}

	/**
	 * Returns an encrypted byte array as a base64 encoded string using the
	 * provided key
	 */
	public static String symetricEncrypt(final byte[] clear, final byte[] key)
			throws GeneralSecurityException {
		if (null == clear) {
			throw new IllegalArgumentException("clear must not be null");
		}
		if (null == key) {
			throw new IllegalArgumentException("key must not be null");
		}
		return symetricEncrypt(SYMMETRIC_ALGORITHM, clear, key);
	}

	/**
	 * Returns an encrypted byte array as a base64 encoded string using the
	 * provided key
	 */
	public static String symetricEncrypt(final String algorithm,
			final byte[] clear, final byte[] key)
			throws GeneralSecurityException {
		if (null == clear) {
			throw new IllegalArgumentException("clear must not be null");
		}
		if (null == key) {
			throw new IllegalArgumentException("key must not be null");
		}
		try {
			SecretKey secretkey = new SecretKeySpec(key, makeKeySpecAlgorithmName(algorithm));
			return symetricEncrypt(algorithm, secretkey, clear);
		} catch (final GeneralSecurityException exc) {
			throw exc;
		}
	}

	public static String symetricEncrypt(final String algorithm,
			SecretKey secretkey, final byte[] clear) throws GeneralSecurityException {
		final byte[] encrypted = symetricEncryptToBytes(algorithm, secretkey, clear);
                return base64Encode(encrypted);
	}
	
	/** Returns a base64 encoded string from the provided byte array */
    public static String base64Encode(final byte[] bytes) {
        final Base64 encoder = new Base64(NO_LINE_BREAKS, NO_END_OF_LINE_CHARS, true);
        return encoder.encodeAsString(bytes);
    }
    
    public static byte[] base64Decode(final String base64Str) {
        final Base64 decoder = new Base64(NO_LINE_BREAKS,
                NO_END_OF_LINE_CHARS, true);
        return decoder.decode(base64Str);
    }
	
    /**
     * Returns an encrypted byte array after encrypting the provided clear bytes using the provided algorithm and key.
     * 
     * @param algorithm the algorithm used to encrypt the clear bytes
     * @param clear the clear bytes to encrypt
     * @param key the key used to encrypt the clear bytes
     * @return an encrypted byte array after encrypting the provided clear bytes using the provided algorithm and key
     * @throws GeneralSecurityException thrown when there is an error encrypting the clear bytes
     */
    public static byte[] symetricEncryptToBytes(final String algorithm, final byte[] clear, final byte[] key)
            throws GeneralSecurityException {
        if (null == clear) {
            throw new IllegalArgumentException("clear must not be null");
        }
        if (null == key) {
            throw new IllegalArgumentException("key must not be null");
        }
        try {
            SecretKey secretkey = new SecretKeySpec(key, makeKeySpecAlgorithmName(algorithm));
            return symetricEncryptToBytes(algorithm, secretkey, clear);
        } catch (final GeneralSecurityException exc) {
            throw exc;
        }
    }

	/**
	 * Returns an encrypted byte array after encrypting the provided clear bytes using the provided algorithm and key.
	 * 
	 * @param algorithm the algorithm used to encrypt the clear bytes
	 * @param secretkey the secret key used to encrypt the clear bytes
	 * @param clear the clear bytes to encrypt
	 * @return an encrypted byte array after encrypting the provided clear bytes using the provided algorithm and key
	 * @throws GeneralSecurityException thrown when there is an error encrypting the clear bytes
	 */
    public static byte[] symetricEncryptToBytes(final String algorithm, SecretKey secretkey, final byte[] clear)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(algorithm);
        if (usesInitializationVector(algorithm)) {
            IvParameterSpec ivspec = new IvParameterSpec(DEFAULT_IV);
            cipher.init(Cipher.ENCRYPT_MODE, secretkey, ivspec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretkey);
        }
        return cipher.doFinal(clear);
    }
	
	/**
	 * Returns a byte array decrypted from a base64 encoded string using the
	 * provided key
	 */
	public static byte[] symetricDecrypt(final String base64, final byte[] key)
			throws GeneralSecurityException {
		return symetricDecrypt(SYMMETRIC_ALGORITHM, CryptoUtils.fromUTF8String(base64), key);
	}

    /**
     * Returns a byte array decrypted from a base64 encoded string using the
     * provided key
     */
    public static byte[] symetricDecrypt(final byte[] base64, final byte[] key)
            throws GeneralSecurityException {
        return symetricDecrypt(SYMMETRIC_ALGORITHM, base64, key);
    }


	/**
	 * Returns a byte array decrypted from a base64 encoded string using the
	 * provided key
	 */
	public static byte[] symetricDecrypt(final String algorithm,
			final String base64, final byte[] key)
			throws GeneralSecurityException {
        return symetricDecrypt(algorithm, CryptoUtils.fromUTF8String(base64), key);
	}

    /**
     * Returns a byte array decrypted from a base64 encoded string using the
     * provided key
     */
    public static byte[] symetricDecrypt(final String algorithm,
                                         final byte[] base64, final byte[] key)
            throws GeneralSecurityException {
        if (null == algorithm) {
            throw new IllegalArgumentException("algorithm must not be null");
        }

        try {
            SecretKey secretkey = new SecretKeySpec(key, makeKeySpecAlgorithmName(algorithm));
            return symmetricDecrypt(algorithm, secretkey, base64);
        } catch (final GeneralSecurityException exc) {
            throw exc;
        }
    }

    /**
     * Perform the actual decrypt
     */

	public static byte[] symmetricDecrypt(final String algorithm, SecretKey secretkey, final String encrypted) throws GeneralSecurityException {
        return symmetricDecrypt(algorithm, secretkey, CryptoUtils.fromUTF8String(encrypted));
	}

    public static byte[] symmetricDecrypt(final String algorithm, SecretKey secretkey, final byte[] encrypted) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(algorithm);
        if(usesInitializationVector(algorithm)) {
            IvParameterSpec ivspec = new IvParameterSpec(DEFAULT_IV);
            cipher.init(Cipher.DECRYPT_MODE, secretkey, ivspec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretkey);
        }
        final Base64 decoder = new Base64(NO_LINE_BREAKS,
                NO_END_OF_LINE_CHARS, true);
        final byte[] secret = decoder.decode(encrypted);
        return cipher.doFinal(secret);
    }

	/**
	 * Converts a key into a form appropriate for use by a cryptographic algorithm
	 *
	 * @param  algorithm    Algorithm to use to compute digest of key
	 * @param  key          Arbitrary key
	 * @param  len          Size of returned byte array desired
	 * @return              byte array of length len
	 */
	public static byte[] keyDigest(String algorithm, final byte[] key, int len) throws GeneralSecurityException {
		if ((null == algorithm) || (null == key)) {
			throw new IllegalArgumentException("input must not be null");
		}
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(key);
		byte[] digest = md.digest();
		if(len > 0) {
			digest = Arrays.copyOf(digest, len);
		}
		return digest;
	}
	
	/**
	 * Computes a digest (hash) of an input message
	 *
	 * @param  algorithm    Algorithm to use to compute digest of key
	 * @param  msg          Input message
	 * @param  key          Secret key to produce unique digest
	 * @param  salt         Optional public bytes to yield more digest uniqueness
	 * @return              Base64 digest of message
	 */
	public static String messageDigest(final String algorithm, final byte[] msg, final byte[] key) throws GeneralSecurityException {
		if ((null == algorithm) || (null == msg) || (null == key)) {
			throw new IllegalArgumentException("input must not be null");
		}
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(key);
		md.update(msg);
		final byte[] digest = md.digest();
		//final Base64 encoder = new Base64(NO_LINE_BREAKS, NO_END_OF_LINE_CHARS, true);
		final Base64 encoder = new Base64();
		String retval = encoder.encodeAsString(digest);
		return retval;
	}
	
	/**
	 * Generate the key using an algorithm to slow down hackers who manage to
	 * get this class file
	 */
	private static final byte[] makeKey() {
		final byte[] ngo = { 92, 7, 84, 3, 76, 47, 2, 99 };
		final byte[] key = new byte[16];
		int j = 0;
		for (int i = 0; i < 8; i++) {
			key[j] = TA[7 - i];
			j++;
			key[j] = ngo[i];
			j++;
		}
		return key;
	}

	/**
	 * Whether or not an encryption algorithm requires an initialization vector
	 * 
	 * @param algorithm
	 * @return
	 */
	private static boolean usesInitializationVector(String algorithm) {
		boolean retval = false;
		if(algorithmsWhichUseInitializationVector.contains(algorithm)) {
			retval = true;
		}
		return retval;
	}
	
	/**
	 * Algorithm name for SecretKeySpec is dervied from algorithm name but is not necessarily the same
	 * @param algorithm
	 * @return
	 */
	private static String makeKeySpecAlgorithmName(String algorithm) {
		String retval = algorithm;
		if(keySpecAlgorithmNames.keySet().contains(algorithm)) {
			retval = keySpecAlgorithmNames.get(algorithm);
		}
		return retval;
	}
	
	private CryptoUtils() {
		// empty
	}
}
