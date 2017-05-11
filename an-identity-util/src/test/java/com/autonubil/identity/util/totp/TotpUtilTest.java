package com.autonubil.identity.util.totp;

import org.junit.Assert;
import org.junit.Test;

public class TotpUtilTest {
	
	public static int[] knownBytes_1 = 
			new int[] {
					
					0xf0, 0x8b, 0x5f, 0x9f, 
					0xe7, 0xea, 0x74, 0x73, 
					0x0a, 0x33, 0x06, 0xc5,
					0x1c, 0x72, 0x51, 0x70,     
					0xd8, 0x7d, 0x54, 0x99 
					
			};
	
	
	public static String knownBase32_1 = "6CFV7H7H5J2HGCRTA3CRY4SRODMH2VEZ"; 
	
	@Test
	public void testEncodeDecodeEtc() {
		String s = TotpUtil.generateSecret();
		byte[] bytes = TotpUtil.toBytes(s);
		String s2 = TotpUtil.toString(bytes);
		Assert.assertEquals(s, s2);
	}
	

	@Test
	public void testEncodeDecodeKnown() {
		byte[] b = TotpUtil.toBytes(knownBase32_1);
		for(int i=0;i < b.length;i++) {
			System.err.println(Byte.toUnsignedInt(b[i])+" / "+knownBytes_1[i]);
			Assert.assertEquals(Byte.toUnsignedInt(b[i]),knownBytes_1[i]);
			
		}

		byte[] bytes = TotpUtil.toBytes(knownBase32_1);
		String s2 = TotpUtil.toString(bytes);
		
		Assert.assertEquals(knownBase32_1, s2);

		
		System.err.println(b.length+" / "+knownBytes_1.length);
		long x = System.currentTimeMillis();
		System.err.println("xxxxxxxxx");
		System.err.println(x);
		try {
			System.err.println(" --- "+TotpUtil.generateTOTP(knownBase32_1, x+"", "6"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("xxxxxxxxx");

	}
	
	
	

}
