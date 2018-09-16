//reCAPTCHAを使用するかどうか true⇒使用する(合わせてnotebook.htmlのinsertPlusDataToSVRDiv、makeNotebook.htmlに対して、display:none; or display:displayとする)
//var reCAPTCHAUse = true;
var reCAPTCHAUse = true;

var pioAbusolutePath = "/pio/";

var WebSiteFlag = true;

//admobを使用するかどうか true>使用する（webサービスなので使用しない為、常にfalseになる）
//var useAdmobFlag = true;
var useAdmobFlag = false;

//false>>>inMemoryDB
var dbClientDBUse = false;

//index-db.js
//DB関連のconfig
var dbName = 'Test1';
var version = '1.0';
var displayName = 'Test1';
var estimatedSize = 65536;
var dbType = 'SQLite'; // WEBSQL or SQLiteで使用DBを切り替え

if (dbClientDBUse == true) {
    if (debugFlag == true) {
        dbType = 'WEBSQL';  // chromeデバッグ時はSQLite pluginが動かない為、WEBSQLにする
    }
} else {
    dbType = 'inMemoryDB';
}

//ColorName
var TitleColorP = "LightPink";
var TitleColorD = "LightYellow";
var TitleColorC = "LightBlue";
var TitleColorA = "LightGreen";

//xmlHttpRequest接続時の接続元情報
var sourceDomain = location.hostname;
var sourcePort = location.port;

var ViewMatrixPageName = "notebook.html";
var ViewIndexPageName = "index.html";

//Confirmボックス
var msgConfirm = "投稿前確認\n";
//msgConfirm = msgConfirm + "・投稿者は、投稿に関して発生する責任が全て投稿者に帰すことを承諾します。\n";
//msgConfirm = msgConfirm + "・投稿者は、投稿された内容及びこれに含まれる知的財産権、（著作権法第21条ないし 第28条に規定される権利も含む）その他の権利につき（第三者に対して再許諾する権利を含みます。）、";
//msgConfirm = msgConfirm + "掲示板運営者に対し、無償で譲渡することを承諾します。ただし、運営者は、投稿者に対して日本国内外において無償で非独占的に複製、公衆送信、頒布及び翻訳する権利を投稿者に許諾します。";
//msgConfirm = msgConfirm + "また、投稿者は掲示板運営者が指定する第三者に対して、一切の権利（第三者に対して再許諾する権利を含みます）を許諾しないことを承諾します。";
//msgConfirm = msgConfirm + "・投稿者は、掲示板運営者あるいはその指定する者に対して、著作者人格権を一切行使しないことを承諾します。";
msgConfirm = msgConfirm + "・投稿者は、投稿する内容に対して、法律、道徳、倫理、公序良俗に反しない、迷惑行為ではない、利用者及び投稿元のコメントに対して最良の案を投稿することを承諾します。\n";
msgConfirm = msgConfirm + "・投稿者は、PDCAちゃんねる以外のサイトのコンテンツの転載については、転載元サイトの規約を遵守することを承諾します。\n";
msgConfirm = msgConfirm + "・投稿者は、当サイトに対して著作者人格権を行使しないことを承諾します。";

//1000件を超えた時のメッセージ
var notebookIDMaxRecord = 1000;
var msgOverMaxRecord = "1スレッドのコメントが" + notebookIDMaxRecord + "件を超えました。これ以上は書き込みできません。";

var inMemoryDB = null;

// DB util
var app;

app = {
    init: function () {
    }
}

app.db = {

    openDB: function () {
        var rtnStrOpenDB;

        if (dbType == 'WEBSQL') {
            rtnStrOpenDB = openDatabase(dbName, version, displayName, estimatedSize);
        } else if (dbType == 'SQLite') {
            rtnStrOpenDB = window.sqlitePlugin.openDatabase({ name: "my.db" });
        } else {
            //inMemoryDBの場合
            rtnStrOpenDB = "";
        }

        return rtnStrOpenDB;
    }

};

