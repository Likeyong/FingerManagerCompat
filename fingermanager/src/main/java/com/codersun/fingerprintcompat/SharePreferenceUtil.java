package com.codersun.fingerprintcompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import static android.text.TextUtils.isEmpty;

class SharePreferenceUtil
{
	private static final String DEFAULT_NAME = "finger";

	public static String KEY_IS_FINGER_CHANGE = "is_finger_change";//指纹是否变化了


	private static SharedPreferences.Editor getSharePreferenceEditor(Context context, String fileName) {
		return getSharedPreferences(context,fileName).edit();
	}

	private static SharedPreferences getSharedPreferences(Context context, String fileName) {
		return context.getSharedPreferences(isEmpty(fileName) ? DEFAULT_NAME : fileName, Context.MODE_PRIVATE);
	}

	public static void saveData(Context context, String key, String value) {
		saveData(context, key, value, null);
	}

	public static void saveData(Context context, String key, String value, String fileName) {
		SharedPreferences.Editor editor = getSharePreferenceEditor(context, fileName);
		editor.putString(key, value);
		editor.commit();
	}

	public static String getValue(Context context,String key) {
		if (null == key)
			return "";
		SharedPreferences sharedPreferences = getSharedPreferences(context, "");
		return sharedPreferences.getString(key, "");
	}

	public static boolean isFingerDataChange(Context context){
		String value = getValue(context, KEY_IS_FINGER_CHANGE);
		boolean result = false;
		if (TextUtils.isEmpty(value)){
			result = false;
		}else {
			result = Integer.parseInt(value) == 1;
		}
		return result;
	}



}
