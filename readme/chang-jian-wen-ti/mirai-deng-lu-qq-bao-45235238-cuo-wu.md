# Mirai 登录QQ报45,235,238错误

## 方法一:

提示：不一定成功，请按照步骤依次执行&#x20;

1. &#x20;请确保你的手机能登录你的bot
2. 下载fix-protocol-version插件到mirai的plugins文件夹中&#x20;
3. 在你的手机安装第三方登录软件&#x20;
4. 打开你安装的第三方登录软件，输入Bot账号密码，选择协议，协议建议选择平板/MACOS协议&#x20;
5. 点击登录，如果登录成功，点击 打包发送到….. 你可以利用QQ或者别的方法将压缩包弄到服务器上去，我是利用QQ发送到电脑然后利用电脑发送到服务器。
6. 打开你mirai的文件夹，在bots目录中删掉原来的你bot账号的那个文件夹（注意是在bots文件夹里面，别把dice文件夹删了），然后解压你刚刚上传到服务器的压缩包到当前位置（即bots文件夹内）&#x20;
7. 运行你的bot，利用你刚刚在Aoki(群文件有)上选择的协议登录机器人 比如你刚刚选择了推荐的平板协议，则输入 login 账号 密码 ANDROID\_PAD

## 方法二:(扫描登录)

下载群文件中代扫描登录字样的压缩包,有无java版和含Java(win)版,注意使用Java11

1. 运行mcl并等待加载完成
2. 如出现类似字样请在参数末尾加上-uu
3.

    <figure><img src="../../.gitbook/assets/image.png" alt=""><figcaption></figcaption></figure>
4. 之后的步骤和配置自动登录一致,只是命令发生变化,参考如下

{% code lineNumbers="true" %}
```
/qrLogin <qq> [protocol]    # 扫码登录，协议可用 ANDROID_WATCH 和 MACOS，默认 MACOS
/qrAutoLogin add <account>    # 添加(扫码登录)自动登录
/qrAutoLogin clear    # 清除(扫码登录)自动登录的所有配置
/qrAutoLogin list    # 查看(扫码登录)自动登录账号列表
/qrAutoLogin remove <account>    # 删除一个(扫码登录)自动登录账号
/qrAutoLogin removeConfig <account> <configKey>    # 删除一个账号(扫码登录)自动登录的一个配置项
/qrAutoLogin setConfig <account> <configKey> <value>    # 设置一个账号(扫码登录)自动登录的一个配置项
```
{% endcode %}

{% hint style="info" %}
配置自动登录如果需要更改协议请到config\top.mrxiaom.qrlogin\AutoLogin.yml文件中
{% endhint %}

