package com.codersun.fingerprintcompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * @author codersun
 * @time 2019/10/15 20:48
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class CipherHelper
{

	private static CipherHelper instance;

	private static final String DEFAULT_KEY_NAME = "defaultKey";

	private static final String KEYSTORE_ALIAS = "keyStoreAlias";

	private static final String HAS_FINGER_KEY = "hasFingerKey";

	private KeyGenerator mKeyGenerator;

	private KeyStore keyStore;

	private CipherHelper()
	{
		try
		{
			keyStore = KeyStore.getInstance("AndroidKeyStore");
		}
		catch (KeyStoreException e)
		{
			e.printStackTrace();
		}

		try
		{
			mKeyGenerator = KeyGenerator
					.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException e)
		{
			throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
		}
	}

	static CipherHelper getInstance()
	{
		if (instance == null)
		{
			synchronized (CipherHelper.class)
			{
				if (instance == null)
				{
					instance = new CipherHelper();
				}
			}
		}
		return instance;
	}

	/**
	 * @des 创建cipher
	 * @author codersun
	 * @date 2019/7/3 14:24
	 */

	Cipher createCipher()
	{
		try
		{
			return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @des 初始化Cipher ,并判断指纹库是否发生了变化
	 * @author codersun
	 * @date 2019/7/3 14:24
	 */

	boolean initCipher(Cipher cipher)
	{
		try
		{
			keyStore.load(null);
			SecretKey key = (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
			if (cipher == null)
			{
				cipher = createCipher();
			}
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return false;
		}
		catch (KeyPermanentlyInvalidatedException e)
		{
			return true;
		}
		catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
				| NoSuchAlgorithmException | InvalidKeyException e)
		{
			throw new RuntimeException("Failed to init Cipher", e);

		}
	}

	/**
	 * @param context
	 * @param createNewKey 是否创建新的密钥
	 * @des 根据当前指纹库创建一个密钥
	 * @author codersun
	 * @date 2019/7/3 10:35
	 */
	void createKey(Context context, boolean createNewKey)
	{
		if (context == null)
		{
			throw new RuntimeException("context can not be null");
		}
		SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_KEY_NAME, Context.MODE_PRIVATE);
		try
		{
			//首先通过标志位判断用户之前是否创建了密钥,如果已经创建过了并且不需要重新创建就跳过
			if (TextUtils.isEmpty(sharedPreferences.getString(HAS_FINGER_KEY, "")) || createNewKey)
			{
				//创建新密钥
				KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS,
						KeyProperties.PURPOSE_ENCRYPT |
								KeyProperties.PURPOSE_DECRYPT)
						.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
						.setUserAuthenticationRequired(true)
						.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
				{
					builder.setInvalidatedByBiometricEnrollment(true);
				}
				mKeyGenerator.init(builder.build());
				mKeyGenerator.generateKey();
				sharedPreferences.edit().putString(HAS_FINGER_KEY, "KEY").apply();
			}
		}
		catch (InvalidAlgorithmParameterException e)
		{
			throw new RuntimeException(e);
		}
	}
}