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

	/**
	 * 当识别的手指没有注册时回调,但是可以继续验证
	 *
	 * @author codersun
	 * @time 2019/10/16 10:36
	 */
	public abstract void onFailed();

	/**
	 * 指纹识别不对,会提示,手指不要大范围移动等信息,可以继续验证
	 *
	 * @author codersun
	 * @time 2019/10/16 10:37
	 */
	public abstract void onHelp(String help);

	/**
	 * 指纹识别彻底失败,不能继续验证
	 *
	 * @author codersun
	 * @time 2019/10/16 10:37
	 */
	public abstract void onError(String error);

	public abstract void onCancelAuth();
}
