package com.codersun.fingerprintcompat;

import android.app.Application;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

/**
 * @author codersun
 * @time 2019/9/3 17:41
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerManagerController
{

	private Application mApplication;

	//弹窗标题
	private String mTitle;

	//弹窗描述
	private String mDes;

	//取消按钮话术
	private String mNegativeText;

	//Android P 以下版本的指纹识别弹窗（如需自定义样式就设置）
	private AFingerDialog mFingerDialogApi23;

	//指纹识别回调
	private IonFingerCallback mFingerCheckCallback;

	//指纹库发生变化时的回调
	private AonFingerChangeCallback mFingerChangeCallback;

	public AonFingerChangeCallback getFingerChangeCallback()
	{
		return mFingerChangeCallback;
	}

	public FingerManagerController setFingerChangeCallback(AonFingerChangeCallback fingerChangeCallback)
	{
		this.mFingerChangeCallback = fingerChangeCallback;
		return this;
	}

	public IonFingerCallback getFingerCheckCallback()
	{
		return mFingerCheckCallback;
	}

	public FingerManagerController setFingerCheckCallback(IonFingerCallback fingerCheckCallback)
	{
		this.mFingerCheckCallback = fingerCheckCallback;
		return this;
	}

	public FingerManagerController setDes(String des)
	{
		this.mDes = des;
		return this;
	}

	public FingerManagerController setNegativeText(String negativeText)
	{
		this.mNegativeText = negativeText;
		return this;
	}

	public FingerManagerController setTitle(String title)
	{
		mTitle = title;
		return this;
	}

	public FingerManagerController setApplication(Application application)
	{
		mApplication = application;
		return this;
	}

	public FingerManagerController setFingerDialogApi23(@Nullable AFingerDialog fingerDialogApi23)
	{
		this.mFingerDialogApi23 = fingerDialogApi23;
		return this;
	}

	public Application getApplication()
	{
		return mApplication;
	}

	public String getTitle()
	{
		return mTitle;
	}

	public String getDes()
	{
		return mDes;
	}

	public String getNegativeText()
	{
		return mNegativeText;
	}

	public AFingerDialog getFingerDialogApi23()
	{
		return mFingerDialogApi23;
	}

	public FingerManager create()
	{
		if (mFingerCheckCallback == null){
			throw new RuntimeException("CompatFingerManager : FingerCheckCallback can not be null");
		}

		return FingerManager.getInstance(this);
	}

}
