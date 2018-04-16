# android-demo
android demo using IMI SDK

#### 使用说明

- 请扫描二维码并下载应用商城对应的正式版本进行测试和使用（只支持Android 5.0及以上）  
  (http://a.app.qq.com/o/simple.jsp?pkgname=com.wifire.vport)  
  <img src="https://github.com/imiapp/imi/blob/master/download_QR.png" width="660" />
  
#### 常见问题
- 运行demo时需要根据需求，修改ReservationActivity.java文件的testThirdAccredit方法中scope的值。

  如果只需要登录用户信息，scope = Constants.TYPE_THIRD_LOGIN;

  如果只需要身份证实名信息，scope = Constants.TYPE_THIRD_ACCREDIT_IDCARD;

  如果两者都需要，scope = Constants.TYPE_THIRD_LOGIN + "," + Constants.TYPE_THIRD_ACCREDIT_IDCARD;

  ReservationActivity.java文件目录为：android-demo/MySdkDemo/app/src/main/java/com/bo/mysdkdemo/ReservationActivity.java

- 如果运行时发现可以获取到用户信息但获取不到身份证实名信息，在保证IMI账号已经实名认证的前提下，可能是void reqAuthorize(String var1, String var2, CreateChannelService var3)函数没有调用。
