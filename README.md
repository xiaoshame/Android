# Android 
音乐播放器 
Android/music/app-release.apk为安装包

一个activity中包含三个fragment,程序分成本地音乐，网络音乐，下载列表三部分，activity作为中心通过本地广播进行通信,activity控制fragment中的行为

本地音乐读取数据库中已下载的歌曲进行播放,使用service绑定activity来控制服务播放音乐

网络歌曲使用百度的接口获取排行榜和排行榜中的歌曲，排行榜和歌曲的信息包含载xml中，使用Sax解析xml

其中使用到AsyncTask编写歌曲下载功能，通过数据保存数据，支持断点续传

通知栏通知使用广播控制音乐的播放





一键锁屏
Android/LockScreen/app-release.apk 为安装包

使用DevicePolicyManager管理设备，实现屏幕锁定，通过管理ComponentName来启动一个DeviceAdminReceiver

第一次运行时激活componentName，后面componentName 就会一直是激活状态

