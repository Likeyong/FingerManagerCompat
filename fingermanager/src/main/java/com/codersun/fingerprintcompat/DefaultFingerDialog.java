package com.codersun.fingerprintcompat;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author codersun
 * @time 2019/9/8 16:35
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class DefaultFingerDialog extends AFingerDialog implements View.OnClickListener
{

	private static final String TITLE = "title";

	private static final String DES = "des";

	private static final String NEGATIVE_TEXT = "negativeText";

	private FingerManagerController mFingerManagerController;

	private ObjectAnimator animator;

	private TextView titleTv;

	private TextView desTv;

	public static DefaultFingerDialog newInstance(FingerManagerController fingerManagerController)
	{
		DefaultFingerDialog defaultFingerDialog = new DefaultFingerDialog();
		Bundle bundle = new Bundle();
		bundle.putString(TITLE, fingerManagerController.getTitle());
		bundle.putString(DES, fingerManagerController.getDes());
		bundle.putString(NEGATIVE_TEXT, fingerManagerController.getNegativeText());
		defaultFingerDialog.setArguments(bundle);
		return defaultFingerDialog;
	}

	private DefaultFingerDialog()
	{
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_finger, null);

		titleTv = view.findViewById(R.id.finger_dialog_title_tv);
		desTv = view.findViewById(R.id.finger_dialog_des_tv);
		TextView cancelTv = view.findViewById(R.id.finger_dialog_cancel_tv);
		cancelTv.setOnClickListener(this);

		if (getArguments() != null)
		{
			titleTv.setText(getArguments().getString(TITLE));
			desTv.setText(getArguments().getString(DES));
			cancelTv.setText(getArguments().getString(NEGATIVE_TEXT));
		}

		animator = ObjectAnimator.ofFloat(desTv, View.TRANSLATION_X, 20, -20);
		animator.setRepeatCount(1);
		animator.setDuration(500);

		return view;
	}

	@Override
	public void onSucceed()
	{
		dismiss();
	}

	@Override
	public void onFailed()
	{
		titleTv.setText("请重试");
		desTv.setText("换个手指试试");
		desTv.setVisibility(View.VISIBLE);
		if (!animator.isRunning())
		{
			animator.start();
		}
	}

	@Override
	public void onHelp(String help)
	{
		titleTv.setVisibility(View.VISIBLE);
		desTv.setVisibility(View.VISIBLE);
		titleTv.setText("请重试");
		desTv.setText("换个手指试试");

		if (!TextUtils.isEmpty(help))
		{
			if (!desTv.getText().toString().trim().equals(help.trim()))
			{
				if (!animator.isRunning())
					animator.start();
			}
		}
	}

	@Override
	public void onError(String error)
	{
		dismissAllowingStateLoss();
	}

	@Override
	public void onCancelAuth()
	{

	}

	@Override
	public void onClick(View v)
	{
		dismiss();
	}
}
