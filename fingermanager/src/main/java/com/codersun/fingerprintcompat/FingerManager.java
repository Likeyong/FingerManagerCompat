package com.codersun.fingerprintcompat;

import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerManager
{

	private static FingerManager fingerManager;

	private static FingerManagerController mFingerManagerController;

	private CancellationSignal cancellationSignal;

	private IBiometricPromptImpl biometricPrompt;

	public enum SupportResult
	{
		DEVICE_UNSUPPORTED,//设备不支持指纹识别
		SUPPORT_WITHOUT_DATA,//设备支持指纹识别但是没有指纹数据
		SUPPORT//设备支持且有指纹数据
	}

	private static FingerManager getInstance()
	{
		if (fingerManager == null)
		{
			synchronized (FingerManager.class)
			{
				if (fingerManager == null)
				{
					fingerManager = new FingerManager();
				}
			}
		}
		return fingerManager;
	}

	static FingerManager getInstance(FingerManagerController fingerManagerController)
	{
		mFingerManagerController = fingerManagerController;
		return getInstance();
	}

	private FingerManager()
	{
	}

	private void createImp(Activity activity, AFingerDialog fingerDialog)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
		{
			biometricPrompt = new BiometricPromptImpl28(activity, mFingerManagerController);
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			biometricPrompt = new BiometricPromptImpl23(activity, fingerDialog, mFingerManagerController);
		}
	}

	/**
	 * 检查设别是否支持指纹识别
	 *
	 * @return
	 */
	public static SupportResult checkSupport(Context context)
	{
//		FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
		FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
		if (fingerprintManager.isHardwareDetected())
		{
			if (fingerprintManager.hasEnrolledFingerprints())
			{
				return SupportResult.SUPPORT;
			}
			else
			{
				return SupportResult.SUPPORT_WITHOUT_DATA;
			}
		}
		else
		{
			return SupportResult.DEVICE_UNSUPPORTED;
		}
	}

	/**
	 * 检查设备是否有指纹数据
	 *
	 * @return
	 */
	public static boolean hasFingerprintData(Context context)
	{
		FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
		return fingerprintManagerCompat.hasEnrolledFingerprints();
	}

	/**
	 * 开始监听指纹识别器
	 */
	@RequiresApi(api = Build.VERSION_CODES.M)
	private void startListener()
	{

		CipherHelper.getInstance().createKey(mFingerManagerController.getApplication(), false);

		if (cancellationSignal == null)
		{
			cancellationSignal = new CancellationSignal();
		}

		if (cancellationSignal.isCanceled())
		{
			cancellationSignal = new CancellationSignal();
		}

		biometricPrompt.authenticate(cancellationSignal);
	}

	public void startListener(Activity activity)
	{
		createImp(activity, mFingerManagerController.getFingerDialogApi23());
		startListener();
	}

	/**
	 * 同步指纹数据,解除 指纹数据变化 问题
	 *
	 * @param context
	 */
	public static void updateFingerData(Context context)
	{
		CipherHelper.getInstance().createKey(context, true);
	}

	public static FingerManagerController build()
	{
		return new FingerManagerController();
	}
}
