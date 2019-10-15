package com.codersun.fingerprintcompat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;

/**
 * @author codersun
 * @time 2019/9/3 15:14
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public abstract class AFingerDialog extends DialogFragment
{

	private IonDismissListener mDismissListener;

	public AFingerDialog()
	{
		super();
		setCancelable(false);
	}

	/**
	 * 监听返回键
	 *
	 * @param savedInstanceState
	 * @return
	 */
	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
		{

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK)
				{
					dismiss();
					cancelFingerAuth();
					return true;
				}
				else
				{
					return false;
				}
			}
		});

		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
		if (mDismissListener != null)
			mDismissListener.onDismiss();
	}

	public void setOnDismissListener(IonDismissListener dismissListener)
	{
		this.mDismissListener = dismissListener;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		dismissAllowingStateLoss();
	}

	protected void cancelFingerAuth()
	{

	}

	public interface IonDismissListener
	{

		void onDismiss();
	}

	public abstract void onSucceed();

	public abstract void onFailed();

	public abstract void onHelp(String help);

	public abstract void onError(String error);

	public abstract void onCancelAuth();
}
