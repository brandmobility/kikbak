package com.kikbak.barcode;

import org.junit.Test;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.kikbak.KikbakBaseTest;

//ADVG688200001

public class BarcodeTest  extends KikbakBaseTest{

	@Test
	public void code128() throws WriterException{
		String barcode = "ADVG688200001";
        BitMatrix result = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, 250, 120);

	}
}
