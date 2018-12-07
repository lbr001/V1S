package com.sunmi.helper.nfcutil;

import android.nfc.tech.MifareClassic;

/**
* This class stands for mifare key A or key B.
* <hr>
* <b>&copy; Copyright 2011 Guidebee, Inc. All Rights Reserved.</b>
* 
* @version 1.00, 13/09/11
* @author Guidebee Pty Ltd.
*/
public class MifareKey {

	/**
	 * Constructor.
	 * @param keyValue the key value.
	 */
	public MifareKey(byte[] keyValue) {
		if (keyValue == null || keyValue.length != 6) {
			throw new IllegalArgumentException("Invaid key");
		}
		key=new byte[6];
		System.arraycopy(keyValue, 0,key, 0, key.length);

	}

	/**
	 * Default constructor.
	 * 
	 */
	public MifareKey() {
		key=MifareClassic.KEY_DEFAULT;

	}

	/**
	 * Get the key value.
	 * @return the byte array of the key.
	 */
	public byte[] getKey(){
		return key;
	}

	/**
	 * set the new key.
	 * @param keyValue the key value.
	 */
	public void setKey(byte[] keyValue){
		if (keyValue == null || keyValue.length != 6) {
			throw new IllegalArgumentException("Invaid key");
		}
		key=new byte[6];
		System.arraycopy(keyValue, 0,key, 0, key.length);
	}

	/**
	 * this is the key values (6 bytes)
	 */
	private byte[] key;
}
