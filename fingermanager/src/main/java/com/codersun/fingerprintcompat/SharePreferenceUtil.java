package com.codersun.fingerprintcompat;

import android.content.Context;
import android.content.SharedPreferences;

import static android.text.TextUtils.isEmpty;

class SharePreferenceUtil
{
	private static final String DEFAULT_NAME = "finger";


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

/*	public static String getValue(String key) {
		if (null == key)
			return "";
		return getValue(key, null);
	}*/



}
