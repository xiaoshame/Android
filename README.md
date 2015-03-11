# Android 
音乐播放器 
Android/music/app-release.apk为安装包

一个activity中包含三个fragment,程序分成本地音乐，网络音乐，下载列表三部分，activity作为中心通过本地广播进行通信,activity控制fragment中的行为

本地音乐读取数据库中已下载的歌曲进行播放,使用service绑定activity来控制服务播放音乐

网络歌曲使用百度的接口获取排行榜和排行榜中的歌曲，排行榜和歌曲的信息包含载xml中，使用Sax解析xml

其中使用到AsyncTask编写歌曲下载功能，通过数据保存数据，支持断点续传

通知栏通知使用广播控制音乐的播放

还需要添加下载的歌曲更新到多媒体数据库中

更换获取本地歌曲列表的方式(现在的方式为读取下载歌曲数据库中的信息)

更换为从多媒体数据库中获取本地歌曲信息



一键锁屏
Android/LockScreen/app-release.apk 为安装包

使用DevicePolicyManager管理设备，实现屏幕锁定，通过管理ComponentName来启动一个DeviceAdminReceiver

第一次运行时激活componentName，后面componentName 就会一直是激活状态



AppExplorer

使用app管理器获取app信息，使用GridViewh和ListView两种方式进行展示

通过包名和活动名，打开指定的应用程序

卸载应用程序，只能卸载用户安装的app，无法卸载系统app

对话框的基本用法

还需要添加卸载系统app和备份系统app功能


Calendar

使用GridView来显示

阴历和阳历之间的关系使用网络上的开源代码
