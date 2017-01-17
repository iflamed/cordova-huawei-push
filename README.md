# cordova-huawei-push
The huawei push for cordova, hms sdk version, now only support android.

## Install
```shell
cordova plugin add cordova-huawei-push --variable APPID=YOURAPPID --variable  PACKAGENAME=YOURPACKAGENAME --save
```

## How to use

### Init the hms connection

```javascript
cordova.plugins.huaweipush.init();
```

### Token Registered

```javascript
document.addEventListener('huaweipush.receiveRegisterResult', function (event) {
    console.log(event) // event will contain the device token value
}.bind(this), false);
```
You can get the token value by `event.token`

### Stop the push service

```javascript
cordova.plugins.huaweipush.stop();
```

### When notification clicked to open the app

```javascript
document.addEventListener('huaweipush.notificationOpened', function (event) {
    console.log(event) // the event will contain a extras key, which contain the data what you send
}.bind(this), false)
```

### When push message arrived at the app open status
```javascript
document.addEventListener('huaweipush.pushMsgReceived', function (event) {
    console.log(event) // the event will contain a extras key, which contain the data what you send
}.bind(this), false)
```
