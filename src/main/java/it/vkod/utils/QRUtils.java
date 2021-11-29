package it.vkod.utils;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRUtils {

	private QRUtils() {

	}


	/**
	 * This method takes the text to be encoded, the width and height of the QR Code,
	 * and returns the QR Code in the form of a byte array.
	 */
	public static byte[] generateQR( String text, int width, int height ) throws WriterException, IOException {

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode( text, BarcodeFormat.QR_CODE, width, height );

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream( bitMatrix, "PNG", pngOutputStream );
		return pngOutputStream.toByteArray();
	}

}
