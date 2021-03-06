package com.yss.util;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 赢时胜加密解密类
 * 采用用户密码安全机制相同算法 : 张旭春
 * @author shashijie 2012-08-02 BUG 4864 代码北京提供
 *
 */
public class YssEncrypt {

	public YssEncrypt(String key){
		setKey(key);
	}
	private String key = "";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * 解密
	 * @param pass
	 * 		密文--字符串
	 * @return
	 * 		明文--字符串
	 */
	public String decryptStr(String pass) {
		
		return decryptByte(getByteArray(pass));
	}

	/**
	 * 加密
	 * @param pass
	 * 		明文--字符串
	 * @return
	 * 		密文--字符串
	 */
	public String encryptStr(String pass) {
		
		return getString(encryptByte(pass));
	}

	/**
	 * 加密
	 * @param pass
	 * 		明文--字符串
	 * @return
	 * 		密文--byte[]
	 */
	public byte[] encryptByte(String pass) {
		
		byte[] bpass;
		int ll = pass.getBytes().length;
		int ii = this.getKey().length();
		byte[] bpass1 = new byte[ll + ii];
		int ltmp, i;

		//应该先用随即数填充bpass1
		bpass = pass.getBytes();
		ltmp = bpass.length - 1;
		for (i = 0; i <= ltmp; i++) {
			bpass1[ltmp + 2 - i] = bpass[i];
		}

		bpass1[1] = (byte) (ltmp / 2);
		bpass1[ll + ii - 2] = (byte) (ltmp + 1 - bpass1[1]);

		ltmp = 0;
		for (i = 0; i <= ll + ii - 2; i++) {
			ltmp += bpass1[i];
			if ((i % 2) == 0) {
				bpass1[i] = (byte) (255 - bpass1[i]);
				ltmp += bpass1[i];
			}
		}
		bpass1[i] = (byte) ((ltmp + bpass1[0] + bpass1[i - 1]) % 256);

		return bpass1;
	}

	/**
	 * 解密
	 * @param pass
	 * 		密文--byte[]
	 * @return
	 * 		明文--字符串
	 */
	public String decryptByte(byte[] bpass) {
		
		byte[] bpass1;
		int i, ltmp;

		ltmp = (int) bpass[0] + bpass[bpass.length - 2];
		for (i = 0; i <= bpass.length - 2; i++) {
			if ((i % 2) == 0) {
				ltmp += bpass[i];
				bpass[i] = (byte) (255 - bpass[i]);
			}
			ltmp += bpass[i];
		}
//		if ((byte) (ltmp % 256) != bpass[i])
//			return ""; //校验错误

		ltmp = bpass.length - getKey().length() - 1;
		bpass1 = new byte[ltmp + 1];
		for (i = 0; i <= ltmp; i++)
			bpass1[ltmp - i] = bpass[i + 2];

		return new String(bpass1); //不能用bpass1.toString！
	}

	/**
	 * 把String类型转换成byte[]
	 * @param str 
	 * 			字符串类型的数据
	 * @return 
	 * 			byte[]数据
	 */
	public byte[] getByteArray(String str) {

		BASE64Decoder dec = new BASE64Decoder();
		byte[] data = null;
		try {
			data = dec.decodeBuffer(str);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return data;

	}

	/**
	 * 把byte[]类型转换成String
	 * @param bt
	 * 		 byte数组
	 * @return String
	 * 		
	 */
	public String getString(byte[] bt) {

		BASE64Encoder enc = new BASE64Encoder();
		return enc.encode(bt);

	}

}
