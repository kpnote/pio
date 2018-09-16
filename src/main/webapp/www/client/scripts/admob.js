//// admob start ////////
function showTestBanner() {
    var admobParam = new admob.Params();
    //admobParam.extra={'keyword':"admob phonegame"};
    //admobParam.isForChild=true;
    admobParam.isTesting = false;
    //admob.showBanner(admob.BannerSize.BANNER, admob.Position.BOTTOM_APP, admobParam);
    admob.showBanner(admob.BannerSize.BANNER, admob.Position.TOP_APP, admobParam);
}
function showInterstitial() {
    admob.isInterstitialReady(function (isReady) {
        if (isReady) {
            admob.showInterstitial();
        }
    });
}
function onInterstitialReceive(message) {
    //alert(message.type + " ,you can show it now");
    //admob.showInterstitial();//show it when received
}
function onReceiveFail(message) {
    var msg = admob.Error[message.data];
    if (msg == undefined) {
        msg = message.data;
    }
    //alert(msg);
    //        document.getElementById("alertdiv").innerHTML = "load fail: " + message.type + "  " + msg;
}
function onDeviceReadyAdmob() {
    admob.initAdmob("ca-app-pub-2381112301819113/2257698582", "ca-app-pub-93103xxxxxxx/xxxxxxxxxxx");
    document.addEventListener(admob.Event.onInterstitialReceive, onInterstitialReceive, false);
    document.addEventListener(admob.Event.onInterstitialFailedReceive, onReceiveFail, false);
    document.addEventListener(admob.Event.onBannerFailedReceive, onReceiveFail, false);
}
//// admob end ////////
