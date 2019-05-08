package com.longyoung.authalipaydemo;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
/**
 * Created by longyoung on 2019/4/28.
 */
public class SignUtils {

	private static final String ALGORITHM = "RSA";
	private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";
	private static final String DEFAULT_CHARSET = "UTF-8";

	public static String sign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, "BC");
			PrivateKey priKey = keyFactory.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_SHA256RSA_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
