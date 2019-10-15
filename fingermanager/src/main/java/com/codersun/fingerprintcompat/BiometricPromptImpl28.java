package com.codersun.fingerprintcompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import javax.crypto.Cipher;

/**
 * @author codersun
 * @time 2019/10/15 20:48
 */
@RequiresApi(Build.VERSION_CODES.P)
public class BiometricPromptImpl28 implements IBiometricPromptImpl {

	private  Activity mActivity;
	private BiometricPrompt mBiometricPrompt;
	private CancellationSignal mCancellationSignal;
	private Cipher cipher;
	private IonFingerCallback mCallback;
	private boolean userCancel;

	private AonFingerChangeCallback mFingerChangeCallback;

	@RequiresApi(Build.VERSION_CODES.P)
	BiometricPromptImpl28(Activity activity,
			FingerManagerController fingerManagerController) {
		this.mActivity = activity;
		mCallback = fingerManagerController.getFingerCheckCallback();
		mFingerChangeCallback = fingerManagerController.getFingerChangeCallback();
		mBiometricPrompt = new BiometricPrompt
				.Builder(activity)
				.setTitle(fingerManagerController.getTitle())
				.setDescription(fingerManagerController.getDes())
				.setNegativeButton(fingerManagerController.getNegativeText(),
						activity.getMainExecutor(), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								mCallback.onCancel();
								userCancel = true;
								mCancellationSignal.cancel();
							}
						})
				.build();


		cipher = CipherHelper.getInstance().createCipher();
	}

	@RequiresApi(Build.VERSION_CODES.P)
	@Override
	public void authenticate(@Nullable CancellationSignal cancel) {
		userCancel = false;
		mCancellationSignal = cancel;

		if (CipherHelper.getInstance().initCipher(cipher)) {
			mFingerChangeCallback.onChange(mActivity);
			return;
		}

		mBiometricPrompt.authenticate(new BiometricPrompt.CryptoObject(cipher),
				mCancellationSignal, mActivity.getMainExecutor(), new BiometricPromptCallbackImpl());
	}

	@RequiresApi(Build.VERSION_CODES.P)
	private class BiometricPromptCallbackImpl extends BiometricPrompt.AuthenticationCallback {
		@Override
		public void onAuthenticationError(int errorCode, CharSequence errString) {
			super.onAuthenticationError(errorCode, errString);
			mCancellationSignal.cancel();
			if (!userCancel) {
				mCallback.onError(errString.toString());
			}
		}

		@Override
		public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
			super.onAuthenticationHelp(helpCode, helpString);
		}

		@Override
		public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
			super.onAuthenticationSucceeded(result);
			mCancellationSignal.cancel();
			mCallback.onSucceed();
		}

		@Override
		public void onAuthenticationFailed() {
			super.onAuthenticationFailed();
			mCallback.onFailed();
		}
	}
}
