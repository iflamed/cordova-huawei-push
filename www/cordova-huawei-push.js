var exec = require('cordova/exec');

var HuaweiPush = function () {}
HuaweiPush.prototype.isAndroidDevice = function(){
    return device.platform == 'Android';
}
// 获取到token
HuaweiPush.prototype.tokenRegistered = function (token) {
    try {
        this.receiveRegisterResult = token;
        cordova.fireDocumentEvent('huaweipush.receiveRegisterResult', this.receiveRegisterResult);
    } catch(exception) {
        console.log('HuaweiPush:tokenRegistered ' + exception);
    }
}
// 透传消息
HuaweiPush.prototype.pushMsgReceived = function (msg) {
    try {
        msg.extras = JSON.parse(msg.extras)
        this.receiveRegisterResult = msg;
        cordova.fireDocumentEvent('huaweipush.pushMsgReceived', this.receiveRegisterResult);
    } catch(exception) {
        console.log('HuaweiPush:pushMsgReceived ' + exception);
    }
}
HuaweiPush.prototype.notificationOpened = function (msg) {
    try {
        msg.extras = JSON.parse(msg.extras)
        this.receiveRegisterResult = msg;
        cordova.fireDocumentEvent('huaweipush.notificationOpened', this.receiveRegisterResult);
    } catch(exception) {
        console.log('HuaweiPush:notificationOpened ' + exception);
    }
}
HuaweiPush.prototype.init = function(success, error) {
    if (this.isAndroidDevice()) {
        exec(success, error, "CordovaHuaweiPush", "init", []);
    }
};
HuaweiPush.prototype.stop = function(success, error) {
    if (this.isAndroidDevice()) {
        exec(success, error, "CordovaHuaweiPush", "stop", []);
    }
};

module.exports = new HuaweiPush();
