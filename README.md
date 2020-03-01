# base库使用方式
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  	dependencies {
	        implementation 'com.github.finalwy:MyCommon:v1.0.9'
	}

# library Module：

·base (base)

	BaseActivity.java
	
	BaseApplication.java

	BaseFragment.java

	BasePresenter.java

	BasePresenterImpl.java

	BaseView.java

	LazyBaseFragment.java

	RxBus.java

	RxEvent.java

·constant(content) 

	ConstantHttpCode.java
	
	RxEventConstant.java
	
·entity(entity) 

	BaseResponse.java
	
·http/utils

	interceptor
		
		ExceptionConverter.java
		
		RxSchedulers.java
		
		ServerException.java
		
	ExceptionConverter.java
	
	RxSchedulers.java
	
	ServerException.java
	
·utils(工具类)
	
	AndroidTools.java
	
	AppUtils.java
	
	BitmapUtils.java
	
	CommonUtils.java
	
	ContinuationClickUtils.java
	
	Countdown.java
	
	CropUtils.java
	
	DateUtils.java
	
	DeviceUuidFactory.java
	
	DialogUtils.java
	
	DisplayUtil.java
	
	FastBlur.java
	
	FileUtils.java
	
	InputMethodUtils.java
	
	LogUtil.java
	
	MD5Util.java
	
	MyCountDownTimer.java
	
	NetworkUtils.java
	
	PreferenceUtils.java
	
	RASUtil.java
	
	RegexUtils.java
	
	SaveImage.java
	
	StatusBarUtil.java
	
	TextColorChangeUtils.java
	
	ToastUtil.java
	
	UploadPicturesHelper.java
	
·view(自定义view)

	banner
	
	commonwebview
	
	update
	
	ClearEditText.java
	
	ViewVisibilityOrGone.java
	
	WaveView.java
	
		
	

	
