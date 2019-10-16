package com.codersun.fingerprintcompat;

import android.app.Activity;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import javax.crypto.Cipher;

/**
 * @author codersun
 * @time 2019/10/15 20:48
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class BiometricPromptImpl23 implements IBiometricPromptImpl
{

	private Cipher mCipher;

	private Activity mActivity;

	private boolean mSelfCanceled;

	private AFingerDialog mFingerDialog;

	private IonFingerCallback mCallback;

	private AonFingerChangeCallback mFingerChangeCallback;

	private FingerManagerController mFingerManagerController;

	BiometricPromptImpl23(Activity activity, AFingerDialog fingerDialog,
			FingerManagerController fingerManagerController)
	{
		this.mActivity = activity;
		mFingerManagerController = fingerManagerController;
		mCipher = CipherHelper.getInstance().createCipher();
		mFingerChangeCallback = fingerManagerController.getFingerChangeCallback();
		this.mFingerDialog = fingerDialog == null ? DefaultFingerDialog.newInstance(fingerManagerController) : fingerDialog;
	}

	@Override
	public void authenticate(@NonNull final CancellationSignal cancel)
	{
		this.mCallback = mFingerManagerController.getFingerCheckCallback();
		mSelfCanceled = false;
		if (CipherHelper.getInstance().initCipher(mCipher) || SharePreferenceUtil.isFingerDataChange(mActivity))
		{
			mFingerChangeCallback.onChange(mActivity);
			return;
		}

		mFingerDialog.setOnDismissListener(new AFingerDialog.IonDismissListener()
		{

			@Override
			public void onDismiss()
			{
				mSelfCanceled = !cancel.isCanceled();
				if (mSelfCanceled)
				{
					cancel.cancel();
					//如果使用的是默认弹窗,就使用cancel回调,否则交给开发者自行处理
					if (mFingerDialog.getClass() == DefaultFingerDialog.class)
					{
						mCallback.onCancel();
					}

				}
			}
		});

		if (!mFingerDialog.isAdded())
			mFingerDialog.show(mActivity.getFragmentManager(), mFingerDialog.getClass().getSimpleName());

		FingerprintManager fingerprintManager = (FingerprintManager) mActivity.getSystemService(FingerprintManager.class);
		fingerprintManager.authenticate(new FingerprintManager.CryptoObject(mCipher), cancel, 0,
				new FingerprintManager.AuthenticationCallback()
				{

					@Override
					public void onAuthenticationError(int errMsgId, CharSequence errString)
					{
						super.onAuthenticationError(errMsgId, errString);
						if (!mSelfCanceled)
						{
							//当指纹识别结算手动调用cancel,用于后面判断是手动取消还是自动取消识别的
							cancel.cancel();
							mFingerDialog.onError(errString.toString());
							mCallback.onError(errString.toString());
						}
					}

					@Override
					public void onAuthenticationHelp(int helpMsgId, CharSequence helpString)
					{
						super.onAuthenticationHelp(helpMsgId, helpString);
						mFingerDialog.onHelp(helpString.toString());
						mCallback.onHelp(helpString.toString());
					}

					@Override
					public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
					{
						super.onAuthenticationSucceeded(result);
						Cipher cipher = result.getCryptoObject().getCipher();
						if (cipher != null)
						{
							try
							{
								byte[] bytes = cipher.doFinal();
								////当指纹识别结算手动调用cancel,用于后面判断是手动取消还是自动取消识别的
								cancel.cancel();
								mFingerDialog.onSucceed();
								mCallback.onSucceed();
							}
							catch (Exception e)
							{
								e.printStackTrace();
								mFingerChangeCallback.onChange(mActivity);
							}
						}
					}

					@Override
					public void onAuthenticationFailed()
					{
						super.onAuthenticationFailed();
						mFingerDialog.onFailed();
						mCallback.onFailed();
					}
				}, null);

	}
}
