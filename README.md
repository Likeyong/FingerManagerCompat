
## 前言
先说一下为什么会发布出这个库吧。很多没做过指纹相关功能肯定和我一开始一样认为：指纹支付很简单官方封装好的Api调用一下就好了，熟悉几个Api的事情。但是呢，这只是识别指纹，真正的指纹识别应用设计很多问题。这里就以指纹支付应用场景举例。<br><br>
我们的期望:<BR>
　　（1）指纹支付可以和手指绑定，比和微信支付一样：开启时输入一个指纹，每次支付的时候只能用当时绑定的指纹支付<br>
　　（2）如果上面的走不通的话，那就只能和招商银行指纹登录一样：开启指纹登录时验证指纹，验证通过之后，以后每次登录都可以通过验证输入的指纹是否是录入系统中的任何一个指纹。 如果你开通指纹后，又在系统中录入了新的指纹，下次用指纹登录招商银行的时候就会被提示指纹发生了变化。<BR><BR>
　　
## 重点
**你不用关心版本适配,你不用关心任何接口,你只需要处理你自己业务就行.**
<br>
本库 

> api 'com.codersun:fingermanager:1.0.0'

* 博文 链接


> https://blog.csdn.net/maxcion/article/details/102581499

　　<br>
## 指纹支付已有方案
- 使用微信开源Soter库
<br>&ensp; 这个方案的优点就是稳,据说微信的指纹支付就是使用的这个方案,和国产设备厂商合作的.可以定位的具体的哪个手指,可以获取到指纹Id. 不足之处就是不支持华为手机和国外部分厂商(没有和Soter合作的). 虽然微信客户端是支持华为指纹的.但是这个框架是暂时不支持的.(很久之前就说要支持,截至目前仍未支持)这其中的恩怨,大家百度一下就可以了解了.反正我是支持华为的

<br>

- 使用系统官方Api
<br>&ensp;&ensp; 优点：支持所有android 6.0 以上的指纹设备(招商银行80%可能性使用的就是这个方案)
<br>&ensp;&ensp; 缺点：
<br>&ensp;&ensp; &ensp;&ensp; &ensp;&ensp; 1. 不能获取指纹Id,不能和手指绑定,同能通过判断指纹库是否变化保证安全
<br>&ensp;&ensp; &ensp;&ensp; &ensp;&ensp; 2. 需要针对android 6.0 和android 9.0 适配 : android 9.0 以下需要自己实现指纹识别弹窗样式 ,但是android 9.0 开始统一由系统弹窗实现(不同厂商可能还不一样)<BR><BR>

## 自己实现的坑
这里列一下通过官方api实现招商银行的指纹登录会有那些问题，也就是本库的有点：
1. 要判断手机指纹库是否发生了变化
2. 需要适配android版本，在android版本大于6.0 小于9.0 的情况下要自己实现指纹识别弹窗。在android P上要使用最新Api调用指纹识别统一弹窗
3. 因为指纹识别回调中的识别失败会在两种情况下回调，分别是：真的指纹识别失败了，还有一种是用户取消了指纹识别这时候也会回调指纹识别失败。所以要对两种情况进行区分，并且官方api并没有直接的借口。
<BR><BR>
## 指纹识别api解读
这里我就不写了，因为有人在官方api解读上已经写的非常好了，下面的链接写了怎么使用api，写的非常详细，以至于在我写好我自己的库之后再看他的文章还是觉得很有收获。但是他的库貌似也没解决上面的三个问题（手动狗头）<br>
https://mp.weixin.qq.com/s/IhNdod3rBmhkYwJ6XuTIYg
<BR><BR>
## 真枪实弹
使用这个库，通过下面这段代码，你可以实现上面所说的所有功能：<br>
　1. 检查设备是否支持指纹：分为三种支持，分别是（1）设备没有指纹识别器 （2）设备有指纹识别器但是没有指纹数据（3）设备有识别器并且有指纹数据，可以进行指纹验证<br>
　2. 相应手机指纹库数据发生变化的情况<br>
　3. 能够对取消指纹识别和指纹识别失败分别进行处理<br>
　4. 针对android M 、P 进行识别

```
switch (FingerManager.checkSupport(MainActivity.this))
					{
						case DEVICE_UNSUPPORTED:
							showToast("您的设备不支持指纹");
							break;
						case SUPPORT_WITHOUT_DATA:
							showToast("请在系统录入指纹后再验证");
							break;
						case SUPPORT:
							FingerManager.build().setApplication(getApplication())
									.setTitle("指纹验证")
									.setDes("请按下指纹")
									.setNegativeText("取消")
									.setFingerDialogApi23(new MyFingerDialog())//如果你需要自定义android P 一下系统弹窗就设置,不设置会使用默认弹窗
									.setFingerCheckCallback(new SimpleFingerCheckCallback()
									{

										@Override
										public void onSucceed()
										{
											showToast("验证成功");
										}

										@Override
										public void onError(String error)
										{
											showToast("验证失败");
										}

										@Override
										public void onCancel()
										{
											showToast("您取消了识别");
										}
									})
									.setFingerChangeCallback(new AonFingerChangeCallback()
									{

										@Override
										protected void onFingerDataChange()
										{
											showToast("指纹数据发生了变化");
										}
									})
									.create()
									.startListener(MainActivity.this);
							break;
					}
```
<BR><BR>

### 自定义android M 弹窗
  如果你想自定义android M 的指纹识别弹窗,很简单,你只需要:
  1. 继承AFingerDialog类
  2. 在onCreateView中初始化你自己的布局
  3. 实现onSucceed()、onFailed（）、onHelp（）、onError（）四个回调就好了，这四个回调建议只做UI相关操作，逻辑操作已经在外部提供了回调接口。

### 弹窗回调
1. onSucceed ：指纹识别成功，可以直接关闭弹窗
2. onFailed ： 当识别的手指没有注册时回调,但是可以继续验证
3. onHelp ： 指纹识别不对,会提示,手指不要大范围移动等信息,可以继续验证
4. onError ：指纹识别彻底失败,不能继续验证

一个指纹识别事件序列是这样的：
开始识别 ---> (onHelp / onFaild) (0个或多个) ---> onSucceed / onError

#### 代码

```
public class MyFingerDialog extends AFingerDialog implements View.OnClickListener
{

	private TextView titleTv;

	private TextView desTv;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.my_dialog_finger, null);

		titleTv = view.findViewById(com.codersun.fingerprintcompat.R.id.finger_dialog_title_tv);
		desTv = view.findViewById(com.codersun.fingerprintcompat.R.id.finger_dialog_des_tv);
		TextView cancelTv = view.findViewById(com.codersun.fingerprintcompat.R.id.finger_dialog_cancel_tv);
		cancelTv.setOnClickListener(this);
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
		titleTv.setText("我是失败标题,继续验证");
		desTv.setText("连按个手指都不会,去屎吧");
	}

	@Override
	public void onHelp(String help)
	{
		titleTv.setText("我是失败标题,继续验证");
		desTv.setText("连按个手指都不会,去屎吧");
	}

	@Override
	public void onError(String error)
	{
		titleTv.setText("客官,下次再来");
		desTv.setText("这都能失败,你还能干啥,不消失,代表我是自定义弹窗");
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

// 调起指纹识别得时候,将自定义的弹窗设置进去,代码如下,如果你不设置自定义弹窗会使用默认的android M 弹窗
FingerManager.build().setApplication(getApplication())
									.setTitle("指纹验证")
									.setDes("请按下指纹")
									.setNegativeText("取消")
									.setFingerDialogApi23(new MyFingerDialog())
									.setFingerCheckCallback()

```

## 演示

### android M

* 指纹识别成功
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105326122.gif)<br><br>



* 设备没有指纹数据
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105408782.gif)<br><br>
* 指纹验证失败
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105433247.gif)<br><br>
* 取消指纹识别
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019101611003222.gif) <br><br>
* 自定义指纹识别弹窗
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105523539.gif)<br><br>
* 指纹数据发生了改变
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105555786.gif)<br><br>
<br><br>
### android P
>* 指纹识别成功
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105637869.gif)<br><br>
* 指纹识别失败
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105707844.gif)<br><br>
* 指纹识别取消
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105944587.gif)<br><br>

* 指纹库发生改变
<br><br>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191016105813176.gif)<br><br>

## 使用方式

> api 'com.codersun:fingermanager:1.0.0'
