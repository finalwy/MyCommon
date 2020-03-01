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
