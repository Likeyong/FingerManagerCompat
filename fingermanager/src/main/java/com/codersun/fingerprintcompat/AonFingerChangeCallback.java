package com.codersun.fingerprintcompat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * @author codersun
 * @time 2019/9/8 16:36
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public abstract class AonFingerChangeCallback
{

	void onChange(Context context)
	{
		SharePreferenceUtil.saveData(context, SharePreferenceUtil.KEY_IS_FINGER_CHANGE, "1");
		onFingerDataChange();
	}

	protected abstract void onFingerDataChange();
}
