package it.vkod.utils;


import org.bouncycastle.crypto.CryptoException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {

	private CryptoUtils() {

	}


	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";


	public static void encrypt( String key, File inputFile, File outputFile ) throws CryptoException {

		doCrypto( Cipher.ENCRYPT_MODE, key, inputFile, outputFile );
	}


	public static void decrypt( String key, File inputFile, File outputFile )
			throws CryptoException {

		doCrypto( Cipher.DECRYPT_MODE, key, inputFile, outputFile );
	}


	private static void doCrypto( int cipherMode, String key, File inputFile,
	                              File outputFile ) throws CryptoException {

		try ( FileInputStream inputStream = new FileInputStream( inputFile ); FileOutputStream outputStream = new FileOutputStream( outputFile ); ) {
			Key secretKey = new SecretKeySpec( key.getBytes(), ALGORITHM );
			Cipher cipher = Cipher.getInstance( TRANSFORMATION );
			cipher.init( cipherMode, secretKey );

			byte[] inputBytes = new byte[ ( int ) inputFile.length() ];
			inputStream.read( inputBytes );

			byte[] outputBytes = cipher.doFinal( inputBytes );

			outputStream.write( outputBytes );

		} catch ( NoSuchPaddingException | NoSuchAlgorithmException
				| InvalidKeyException | BadPaddingException
				| IllegalBlockSizeException | IOException ex ) {
			throw new CryptoException( "Error encrypting/decrypting file", ex );
		}
	}

}