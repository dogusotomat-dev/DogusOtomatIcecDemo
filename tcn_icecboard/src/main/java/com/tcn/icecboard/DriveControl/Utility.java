package com.tcn.icecboard.DriveControl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * 作者：Jiancheng,Song on 2016/5/28 09:03
 * 邮箱：m68013@qq.com
 */
public class Utility {
	/**
	 * 计算校验位
	 * @param str
	 * @return
	 */
	public static byte getStr(String str){
		byte d=0;
		for(int i=0;i<str.length();i++){
			d=(byte) (d^(int)str.charAt(i));
		}
		return d;
	}

	/**
	 * 累加和校验码
	 *
	 * @param str 数据
	 * @return
	 */
	public static String getCheckSum(String str) {
		if (null == str || str.isEmpty()) {
			return "";
		}
		int data = 0;
		for(int i=0; i < str.length(); i++){
			data += (int)str.charAt(i);
		}

		return String.valueOf(data);
	}

	/**
	 * 判断是否是含小数
	 * @param data
	 * @return
	 */
	public static boolean isContainDeciPoint(String data){
		boolean bRet = false;
		if ((null == data) || (data.length() < 1)) {
			return bRet;
		}
		try {
			// Pattern pattern = Pattern.compile("^[0-9]+\\.{0,1}[0-9]{0,2}$");
			int indexP = data.indexOf(".");
			if (indexP > 0) {
				int lastIndexOf = data.lastIndexOf(".");
				if (lastIndexOf > indexP) {
					return bRet;
				}
				data = data.replace(".","");
				bRet = isDigital(data);
			}
		} catch (Exception e) {

		}

		return bRet;
	}

	/**
	 * 判断是否全部由数字组成
	 * @param str
	 * @return
	 */
	public static boolean isDigital(String str) {
		if ((null == str) || (str.length() < 1)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[0-9]*$");
		return pattern.matcher(str).matches();
	}

	public static String tcnLock(String content) {
		byte[] hash;
		try {
			content = YsData.KEY_LOG +content;
			hash = MessageDigest.getInstance(YsData.KEY_MEDTH).digest(content.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("NoSuchAlgorithmException",e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UnsupportedEncodingException", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10){
				hex.append("0");
			}
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
}
