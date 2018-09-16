$(function () {

    function createTableIfNotExists() {
        var db = app.db.openDB();

        //        db.transaction(function (trans) {
        //            trans.executeSql('CREATE TABLE IF NOT EXISTS meibo '
        //                + '( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, '
        //                + ' name TEXT NOT NULL,' + ' email TEXT NOT NULL);');
        var strQueryString = 'CREATE TABLE IF NOT EXISTS DataRelationManage '
            + '( ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, '
            + ' ChildIDTags TEXT,'
            + ' CreateDate TEXT,'
            + ' UpdateDate TEXT,'
            + ' DeleteDate TEXT,'
            + ' PDCAPhase TEXT,'
            + ' ContentTitle TEXT,'
            + ' ContentDesc TEXT,'
            + ' ContentStatus TEXT'
            + ');';

        DbTransaction(db, strQueryString);

    }

    function DbTransaction(db, strQueryString) {
        if (dbType == 'WEBSQL') {
            db.transaction(function (trans) {
                trans.executeSql(strQueryString);
            });
        } else if (dbType == 'SQLite') {
            db.sqlBatch([strQueryString]);
        } else { }
    }

    var validCHK = function (strOrigin) {
        var strReturn;
        strReturn = strOrigin.replace(/"/g, '').replace(/'/g, '').replace(/\t/g, '\n');
        return strReturn;
    }

    function insertPlusData(l_this) {
        var db = app.db.openDB();
        var closestdom = $(l_this).closest('#insertitem');
        var maxid;
        var defer = $.Deferred();

        db.transaction(function (trans) {
            //trans.executeSql('INSERT INTO meibo (name, email) ' + 'VALUES ("Ichiro","ichiro@example.com");');
            trans.executeSql('INSERT INTO DataRelationManage ('
                + 'ChildIDTags, CreateDate, UpdateDate, DeleteDate, PDCAPhase, ContentTitle, ContentDesc, ContentStatus'
                + ') VALUES ('
                + '"' + validCHK(closestdom.find(".entry_ChildIDTags").val()) + '"'
                + ',"' + validCHK(closestdom.find(".entry_CreateDate").val()) + '"'
                + ',"' + validCHK(closestdom.find(".entry_UpdateDate").val()) + '"'
                + ',"' + validCHK(closestdom.find(".entry_DeleteDate").val()) + '"'
                //                    + ',"' + closestdom.find(".entry_PDCAPhase").val() + '"'
                + ',"' + closestdom.find("[name=entry_PDCAPhase]").val() + '"'
                + ',"' + validCHK(closestdom.find(".entry_ContentTitle").val()) + '"'
                + ',"' + validCHK(closestdom.find(".entry_ContentDesc").val()) + '"'
                + ',"' + validCHK(closestdom.find(".entry_ContentStatus").val()) + '"'
                + ');', [], function () {
                });
            db.transaction(function (trans) {
                trans.executeSql('SELECT MAX(ID) as MAXID FROM DataRelationManage;', [], function (trans, r) {
                    for (var i = 0; i < r.rows.length; i++) {
                        maxid = r.rows.item(i).MAXID;

                        db.transaction(function (trans) {

                            trans.executeSql('UPDATE DataRelationManage SET '
                                + 'ChildIDTags="' + $('#selecteditem').find('.entry_ChildIDTags').val() + ' ' + maxid + '"'
                                + ' WHERE ID="' + $('#selecteditem').find('.entry_ID').val() + '";', [], function (trans, r) {
                                    defer.resolve(l_this);
                                })
                        });
                    }
                });
            });
        });

        return defer.promise();
    }

    function addChildIdTags(selectedid) {
        var db = app.db.openDB();
        var defer = $.Deferred();

        db.transaction(function (trans) {
            trans.executeSql('UPDATE DataRelationManage SET '
                + 'ChildIDTags="' + $('#selecteditem').find('.entry_ChildIDTags').val() + ' ' + selectedid + '"'
                + ' WHERE ID="' + $('#selecteditem').find('.entry_ID').val() + '";', [], function (trans, r) {
                    defer.resolve();
                })
        });

        return defer.promise();
    }

    function modifyChildIdTags(l_baseId, l_beseIdsChildIdTags) {
        var db = app.db.openDB();
        var defer = $.Deferred();

        db.transaction(function (trans) {
            trans.executeSql('UPDATE DataRelationManage SET '
                + 'ChildIDTags="' + l_beseIdsChildIdTags + '"'
                + ' WHERE ID="' + l_baseId + '";', [], function (trans, r) {
                    defer.resolve();
                })
        });

        return defer.promise();
    }

    function updateIdData(l_this) {
        var db = app.db.openDB();
        var closestdom = $(l_this).closest('.ui-field-contain');

        var strQueryString = 'UPDATE DataRelationManage SET '
            + 'ChildIDTags="' + validCHK(closestdom.find(".entry_ChildIDTags").val()) + '"'
            + ', CreateDate="' + validCHK(closestdom.find(".entry_CreateDate").val()) + '"'
            + ', UpdateDate="' + validCHK(closestdom.find(".entry_UpdateDate").val()) + '"'
            + ', DeleteDate="' + validCHK(closestdom.find(".entry_DeleteDate").val()) + '"'
            //                + ', PDCAPhase="' + closestdom.find(".entry_PDCAPhase").val() + '"'
            + ', PDCAPhase="' + closestdom.find("[name=entry_PDCAPhase]").val() + '"'
            + ', ContentTitle="' + validCHK(closestdom.find(".entry_ContentTitle").val()) + '"'
            + ', ContentDesc="' + validCHK(closestdom.find(".entry_ContentDesc").val()) + '"'
            + ', ContentStatus="' + validCHK(closestdom.find(".entry_ContentStatus").val()) + '"'
            + ' WHERE ID="' + closestdom.find(".entry_ID").val() + '";';

        DbTransaction(db, strQueryString);

        //ToDo:戻るボタンで戻れるようにする。今は以下の関数では戻れない。
        //window.history.back();
    }

    function deleteIdData(l_deleteId) {

        var defer = $.Deferred();

        var db = app.db.openDB();

        db.transaction(function (trans) {

            trans.executeSql('DELETE from DataRelationManage '
                //                    + ' WHERE ID="' + closestdom.find(".entry_ID").val() + '";', [], function () {
                + ' WHERE ID="' + l_deleteId + '";', [], function () {
                    //alert("test");
                    defer.resolve();
                });
        });

        return defer.promise();
    }

    /*
    var getWBSListData = function (selectedid, dispType) {

        var defer = $.Deferred();
        var db = app.db.openDB();
        var tmplist = "";
        var str_SqlWhere = "";

        if ((selectedid != undefined)) {
            str_SqlWhere = "where id in ('" + selectedid.replace(/ /g, '\',\'') + "')";
        }

        if (dispType == "ContentTitle") {
            tmplist = tmplist + '<a href="#three" data-rel="dialog" data-transition="popup" class="ui-btn ui-corner-all ui-shadow ui-screen-hidden" data-entry_id="" style="white-space:normal;white-space:normal;padding:+5% +1%;"></a>';
            db.transaction(function (trans) {
                trans.executeSql('SELECT * FROM DataRelationManage ' + str_SqlWhere + ';', [], function (trans, r) {
                    for (var i = 0; i < r.rows.length; i++) {
                        //tmplist = tmplist + '<li data-entry_id="' + r.rows.item(i).ID + '"><a href="#three" data-transition="slide">id=' + r.rows.item(i).ID + '</br> ChildIDTags=' + r.rows.item(i).ChildIDTags + '</a> <input type="radio" class="button_class" value="' + r.rows.item(i).ID + '"/></li>';
                        tmplist = tmplist + '<a href="#three" data-rel="dialog" data-transition="popup" class="ui-btn ui-corner-all ui-shadow ui-screen-hidden" data-entry_id="' + r.rows.item(i).ID + '" style="white-space:normal;white-space:normal;padding:+5% 1%;">(' + r.rows.item(i).ID + ')' + r.rows.item(i).ContentTitle + '</a>';
                    }
                    //                    tmplist = tmplist + '</ul>';
                    defer.resolve(tmplist);
                });
            });
        } else {
            tmplist = tmplist + '<ul data-role="listview" data-filter="false">';
            db.transaction(function (trans) {

                /** 2018/05/20 list viewの並びを変更
                 * updateDateの降順で表示するもの
                 *  ・index.htmlページで表示する一覧 かつ、__info/1」等、カテゴリ名に「__」を含まない
                * /
                //trans.executeSql('SELECT * FROM DataRelationManage ' + str_SqlWhere + ';', [], function (trans, r) {
                if (location.href.indexOf(ViewIndexPageName) > -1 && $('#notebook_id').val().indexOf("__") == -1) {
                    str_SqlWhere = str_SqlWhere + " order by UpdateDate desc"
                };

                trans.executeSql('SELECT * FROM DataRelationManage ' + str_SqlWhere + ' ;', [], function (trans, r) {
                    for (var i = 0; i < r.rows.length; i++) {
                        if (r.rows.item(i).PDCAPhase == "P") {
                            tdBgColor = TitleColorP;
                        } else if (r.rows.item(i).PDCAPhase == "D") {
                            tdBgColor = TitleColorD;
                        } else if (r.rows.item(i).PDCAPhase == "C") {
                            tdBgColor = TitleColorC;
                        } else if (r.rows.item(i).PDCAPhase == "A") {
                            tdBgColor = TitleColorA;
                        }

                        //tmplist = tmplist + '<li data-entry_id="' + r.rows.item(i).ID + '"><a href="#three" data-transition="slide">id=' + r.rows.item(i).ID + '</br> ChildIDTags=' + r.rows.item(i).ChildIDTags + '</a> <input type="radio" class="button_class" value="' + r.rows.item(i).ID + '"/></li>';
                        tmplist = tmplist + '<li data-entry_id="' + r.rows.item(i).ID + '" style="border-left:10px solid ' + tdBgColor + '; white-space:normal;padding:+3% +1%"><font size ="-1">' + r.rows.item(i).UpdateDate + '</font><br />(' + r.rows.item(i).ID + ') (' + r.rows.item(i).PDCAPhase + ') ' + r.rows.item(i).ContentTitle + '</li>';
                    }
                    tmplist = tmplist + '</ul>';
                    defer.resolve(tmplist);
                });
            });
        }

        return defer.promise();
    }
    */

    var getWBSListData = function (selectedid, dispType) {

        var defer = $.Deferred();

        var IDData = "";

        var tmplist = "";

        if (selectedid != undefined) {

            //使用しないはず

        } else {
            IDData = inMemoryDB;
        }

        if (dispType == "ContentTitle") {
            tmplist = tmplist + '<a href="#three" data-rel="dialog" data-transition="popup" class="ui-btn ui-corner-all ui-shadow ui-screen-hidden" data-entry_id="" style="white-space:normal;white-space:normal;padding:+5% +1%;"></a>';

            for (i = 0; i < IDData.length; i++) {
                //tmplist = tmplist + '<li data-entry_id="' + r.rows.item(i).ID + '"><a href="#three" data-transition="slide">id=' + r.rows.item(i).ID + '</br> ChildIDTags=' + r.rows.item(i).ChildIDTags + '</a> <input type="radio" class="button_class" value="' + r.rows.item(i).ID + '"/></li>';
                tmplist = tmplist + '<a href="#three" data-rel="dialog" data-transition="popup" class="ui-btn ui-corner-all ui-shadow ui-screen-hidden" data-entry_id="' + IDData[i].ID + '" style="white-space:normal;white-space:normal;padding:+5% 1%;">(' + IDData[i].ID + ')' + IDData[i].ContentTitle + '</a>';
            }

        } else {
            tmplist = tmplist + '<ul data-role="listview" data-filter="false">';

            if (IDData.length > 0) { /** IDData.lengthが空の場合に、IDData.sort is not a function エラーになる為、当判定を追加 */
                if (location.href.indexOf(ViewIndexPageName) > -1 && $('#notebook_id').val().indexOf("__") == -1) {
                    IDData = IDData.sort(CompareForSortOrderByUpdateDate);
                } else {
                    /** __info/index 等はIDでソートする */
                    IDData = IDData.sort(CompareForSortOrderByID);
                }
            }

            for (i = 0; i < IDData.length; i++) {
                if (IDData[i].PDCAPhase == "P") {
                    tdBgColor = TitleColorP;
                } else if (IDData[i].PDCAPhase == "D") {
                    tdBgColor = TitleColorD;
                } else if (IDData[i].PDCAPhase == "C") {
                    tdBgColor = TitleColorC;
                } else if (IDData[i].PDCAPhase == "A") {
                    tdBgColor = TitleColorA;
                }

                //tmplist = tmplist + '<li data-entry_id="' + r.rows.item(i).ID + '"><a href="#three" data-transition="slide">id=' + r.rows.item(i).ID + '</br> ChildIDTags=' + r.rows.item(i).ChildIDTags + '</a> <input type="radio" class="button_class" value="' + r.rows.item(i).ID + '"/></li>';
                tmplist = tmplist + '<li data-entry_id="' + IDData[i].ID + '" style="border-left:10px solid ' + tdBgColor + '; white-space:normal;padding:+3% +1%"><font size ="-1">' + IDData[i].UpdateDate + '</font><br />(' + IDData[i].ID + ') (' + IDData[i].PDCAPhase + ') ' + IDData[i].ContentTitle + '</li>';
            }
            tmplist = tmplist + '</ul>';
        }

        defer.resolve(tmplist);

        return defer.promise();
    }

    function CompareForSortOrderByUpdateDate(first, second) {

        if (first.UpdateDate == second.UpdateDate) { return 0; }
        else if (first.UpdateDate > second.UpdateDate) { return -1; }
        else { return 1;}
    }

    function CompareForSortOrderByID(first, second) {

        if (first.ID == second.ID) { return 0; }
        else if (first.ID > second.ID) { return 1; }
        else { return -1; }
    }

    var getIDData = function (selectedid) {
        var defer = new $.Deferred();

        /*
        var db = app.db.openDB();
        var IDData = "";
        var str_SqlWhere = "";

        str_SqlWhere = "where id ='" + selectedid + "'";

        db.transaction(function (trans) {
            trans.executeSql('SELECT * FROM DataRelationManage ' + str_SqlWhere + ';', [], function (trans, r) {
                for (var i = 0; i < r.rows.length; i++) {
                    //tmplist = tmplist + '<li data-entry_id="' + r.rows.item(i).ID + '"><a href="#three" data-transition="slide">id=' + r.rows.item(i).ID + '</br> ChildIDTags=' + r.rows.item(i).ChildIDTags + '</a> <input type="radio" class="button_class" value="' + r.rows.item(i).ID + '"/></li>';
                    IDData = r.rows.item(i);
                }
                defer.resolve(IDData);
            });
        });
        */

        var IDData = "";

        for (var i = 0; i < inMemoryDB.length; i++) {
            if (selectedid == inMemoryDB[i].ID) {
                IDData = inMemoryDB[i];
                break;
            }
        }
        defer.resolve(IDData);

        return defer.promise();
    }

    var dispIdData = function (selectedIdArray, setPlaceName) {
        var setPlace = $(setPlaceName);
        setPlace.show();
        if (selectedIdArray == "") {
            setPlace.hide();
        } else {
            setPlace.find('.entry_ID').val(selectedIdArray.ID);
            setPlace.find('.entry_ChildIDTags').val(selectedIdArray.ChildIDTags);
            setPlace.find('.entry_CreateDate').val(selectedIdArray.CreateDate);
            setPlace.find('.entry_UpdateDate').val(selectedIdArray.UpdateDate);
            setPlace.find('.entry_DeleteDate').val(selectedIdArray.DeleteDate);
            //            $('#selecteditem').find('.entry_PDCAPhase').val(selectedIdArray.PDCAPhase);
            setPlace.find('select[name=entry_PDCAPhase]').val(selectedIdArray.PDCAPhase);
//2018.04.24 notebook一覧から直接wordrobeページへ進み、noteをクリックした場合に、下記の処理でエラーが起きて、編集画面に遷移しない為、selectmenu()を追加した。
//    Uncaught Error: cannot call methods on selectmenu prior to initialization; attempted to call method 'refresh'
            setPlace.find('select[name=entry_PDCAPhase]').selectmenu();
            setPlace.find('select[name=entry_PDCAPhase]').selectmenu("refresh", true);

            setPlace.find('.entry_ContentTitle').val(selectedIdArray.ContentTitle);
            setPlace.find('.entry_ContentDesc').val(selectedIdArray.ContentDesc);
            setPlace.find('.entry_ContentStatus').val(selectedIdArray.ContentStatus);

            setPlace.show();
        }
    }

    var dispChangeListTitle = function (selectedIdArray) {
        var setPageOneListTitle1 = $("#pageone_list_title1");
        var setPageOneListTitle2 = $("#pageone_list_title2");

        if (selectedIdArray == "") {
            setPageOneListTitle1.show();
            setPageOneListTitle2.hide();
        } else {
            setPageOneListTitle1.hide();
            setPageOneListTitle2.show();
        }
    }

    $('.button_class').on("click", function () {
        alert("button_class");
    })

    //jquerymobileのバグ対応（footerボタンをクリックするとactiveの状態のままになってしまう為、active時のclassを削除
    //footerの中にある「li 直下の a」
    $('.footer li > a').on("click", function () {
        //alert($(this).attr('class'));
        $(this).removeClass('ui-btn-active');
    })

    var dispWBSPage = function (selectedid, strIdDataSetAtrName, strWBSListSetAtrName) {
        //selectedid ・・・ 選択されたIDを指定。（初期表示の場合はIDは選択されない）
        //strIdDataSetAtrName　・・・　選択されたIDの情報表示欄（2か所）のうちどちらかを指定

        var defer = $.Deferred();

        //引数が渡されない場合の初期値を指定
        if (strIdDataSetAtrName == undefined) {
            strIdDataSetAtrName = '#selecteditem';
        }
        if (strWBSListSetAtrName == undefined) {
            strWBSListSetAtrName = '#wbslist_on_page_one';
        }

        $.when(getIDData(selectedid))                                   //選択されたIDのデータを取得して配列に格納
            .done(function (fromgetIDDataArg) {
                dispIdData(fromgetIDDataArg, strIdDataSetAtrName);      //選択されたIDのデータをstrIdDataSetAtrNameで指定された更新用フォームにセット（配列のデータ有⇒page id="one"に入力画面を表示させる）
                dispChangeListTitle(fromgetIDDataArg);                  //(page id="one"のタイトル表示を変更する)（配列のデータ有⇒page id="one"のタイトルを入力画面用の表示に変更）

                $.when(getWBSListData(fromgetIDDataArg.ChildIDTags))    //<div class="wbslist" id="wbslist_on_page_one"> <ul data-role="listview"・・・・等で指定されたlistviewに値をセット
                    .done(function (tmplist) {
                        $(strWBSListSetAtrName).text("");
                        $(strWBSListSetAtrName).append(tmplist);
                        $("ul").listview().listview('refresh');
                    });

                $.when(getWBSListData(undefined, "ContentTitle"))       //新規登録用フォームのContent Title欄の入力サジェスト機能に使うデータ用として、ui-controlgroup-controlsに値をセット
                    .done(function (tmplist) {
                        $('.ui-controlgroup-controls').text("");
                        $('.ui-controlgroup-controls').append(tmplist);
                        //                $(".ui-controlgroup-controls").trigger("reflesh");
                        //                $('.ui-controlgroup-controls').append('<a href="#" class="ui-btn ui-corner-all ui-shadow ui-first-child ui-shadow ui-last-child">Renault</a>');
                        defer.resolve();
                    });
            })
        return defer.promise();
    }


    var dispContentTitles = function () {
        var defer = $.Deferred();

        $.when(getWBSListData(undefined, "ContentTitle"))
            .done(function (tmplist) {
                $('.ui-controlgroup-controls').text("");
                $('.ui-controlgroup-controls').append(tmplist);
            });
    }

    // index.html -> WBSList更新
    $('.outputwbslist').bind('click', function () {
        dispWBSPage('');
        $('#WBSPageHistory').val("");

        $('#selecteditem').find('.entry_ID').val("");

        //        dispContentTitles();

        location.href = "#one"; // jquerymobile のfooter bug対応。これがないとfixされないバグがある。

    });


    $(document).ready(function () {

        if (WebSiteFlag == true) {
            onDeviceReadySqlite();
        } else {
            document.addEventListener("deviceready", onDeviceReadySqlite, false); //sqlite

            // chromeデバッグの場合、admob pluginが動かない為、コメントアウト
            if (useAdmobFlag == true) {
                document.addEventListener('deviceready', onDeviceReadyAdmob, false);  //admob
                document.addEventListener('deviceready', showTestBanner, false);
            };

            document.addEventListener("deviceready", onDeviceReadyFastClick, false); //fastclick
        }

        // backbutton関連
        // backbuttonを無効化している
        document.addEventListener("backbutton",
            //            function () { } //backbutton無効化
            //navBackPage   //backbutton有効化

            //            navBackPage();
            //            function () { navigator.app.exitApp(); }
            function () {
                if (confirm("Exit app?")) {
                    navigator.app.exitApp();
                }
            }
            , false); //android backbuttonを制御


        /** URLのGETパラメータから、前ページから渡されたnotebookIDを取得する */
        var getParam = location.search.substring(1);

        /** Paramを配列に格納（ParamはnotebookIDのみ渡されていると仮定） */
        var aryParam = location.search.substring(1).split('=');

        /** NotebookIDを格納 */
        var strNotebookID = aryParam[1];

        var textData = null;

        //ローカルDBの情報をtruncateして空にする
        deleteAllData();

        //notebook一覧でクリックされたnotebookIDを取得
        //var selectedNotebookID = $(this).data('entry_notebook_id');

        //notebook一覧でクリックされたnotebookIDを#notebook_idにセット（notebookID保持に使用）
        $('#notebook_id').val(strNotebookID);

        /** notebookIDがページ引数として渡された場合（渡されない場合＝スレ立ての場合は移行の処理を行わない） */
        if (strNotebookID != undefined) {

            /** 2018/05/20 notebookを表示する場合とnotebookのindexを表示する場合で処理を分けた
             * ブラウザの戻るボタンで画面を遷移すると、pageidでの制御が難しいため、location.hrefで遷移せずに
             * initページを捨て、htmlファイルを分けて、それぞれを最初に表示するように変更した。
            */
            if (strNotebookID.indexOf("index") > -1) {

                //クリックされたnotebookのnotebookIDの情報をサーバから取得し、ローカルDBへ保存する
                //textData = insertLDBFromSVR(strNotebookID);

                //inMemoryDBへ格納（DBが不要になった場合insertLDBFromSVRは不要になる）
                inMemoryDB = getTextDataFromSVR(strNotebookID);

                /** テキストの長さをform項目に保存する */
                $('#notebook_length').val(inMemoryDB.length);

                //PDCA Link画面に遷移
                $.when(dispWBSPage('')).done(function () {

                    //"category/index" の場合
                    //#oneに遷移して一覧を表示する >>> htmlページを分けた為不要
                    //location.href = "#one";
                })
            } else {

                /** note書き込み画面に直接遷移したかどうかを判定 */
                if (location.href.indexOf('#') > -1) {
                    /** 直接遷移した場合(.entry_IDが空欄の場合)は、URLから#XXXを省いた形のURL(note一覧（notebook.html?notebookID=test/7）)に戻る */
                    location.href = location.href.substr(0, location.href.indexOf('#'));
                    return 0;
                } else {
                    //inMemoryDBへ格納（DBが不要になった場合insertLDBFromSVRは不要になる）
                    inMemoryDB = getTextDataFromSVR(strNotebookID);

                    /** テキストの長さをform項目に保存する */
                    $('#notebook_length').val(inMemoryDB.length);

                    //"category/5" 等の場合
                    //noteID"1"を起点にしたWordrobeページを表示する
                    var entry_ID = "1";
                    dispListChildData(this, entry_ID);
                }
            }
        }
    });

    function onDeviceReadySqlite() {
        createTableIfNotExists(); //データ保存テーブルを作成
        $('.adminColumn').hide();
        if (WebSiteFlag == false) {
            alert("onDeviceReadySqlite");
            $.when(dispWBSPage('')).done(function () {
                location.href = "#one";
            })
        }
    }

    function onDeviceReadyFastClick() {
        FastClick.attach(document.body);
    }

    $('#one > .wbslist').on('click', 'li', function () {

        //admob.hideBanner();
        //showTestBanner();

        //alert($(this).find('input').val());
        var selectedid = $(this).data('entry_id');
        //        alert("before this click");

        /** 2018/05/20 page="one"でクリックされた際の処理を変更
         * 前ページからの遷移でセットされたnotebookidからディレクトリ名を取得。
         * クリックされた項目のidを取得。
         * notebookid.html?notebookid=directory名/selectedid へ遷移
         */
        //notebook一覧でクリックされたnotebookIDを取得
        var categoryName = $('#notebook_id').val().split("/")[0];

        var hrefString = "../client/notebook.html" + "?notebookID=" + categoryName + "/" + selectedid;

        location.href = hrefString;

        /** 2018/05/20 コメントアウト
        var IdBeforeMove = $('#selecteditem').find('.entry_ID').val();
        var HistoryList = $('#WBSPageHistory').val();

        $('html,body').animate({ opacity: 0 }, "fast", function () {
            if (IdBeforeMove != "") {
                $('#WBSPageHistory').val(HistoryList + ' ' + IdBeforeMove);
            };
            $.when(dispWBSPage(selectedid)).done(function () {
                $('html,body').animate({ opacity: 1 }, "fast");
                $('html,body').animate({ scrollTop: 0 }, 0);
            })

        })
         */
    });

    $('#updateIdData').on('click', function () {
        updateIdData(this);
    });

    $('#ListChildData').on('click', function () {
        var entry_ID = $('#selecteditem').find('.entry_ID').val();

        dispListChildData(this, entry_ID);
    });

    var dispListChildData = function (l_this, entry_ID) {

        //var entry_ID = $('#selecteditem').find('.entry_ID').val();
        var memoMatrixX = [""];
        var memoMatrixYX = [memoMatrixX.concat()];
        var addToAxisWay = "";

        var targetAxisX = 0;
        var targetAxisY = 0;

        var rtnAddToMatrix;

//        $.when(getIDsDataAll())
//            .then(function (IDsDataAll) {
        var IDsDataAll = inMemoryDB;


                for (var i = 0; i < IDsDataAll.length; ++i) {
                    if (entry_ID == IDsDataAll[i].ID) {
                        //IDのデータをアレイに保存

                        rtnAddToMatrix = addToMatrix(memoMatrixYX, memoMatrixX, targetAxisY, targetAxisX, addToAxisWay, $.extend(true, {}, IDsDataAll[i]));
                        memoMatrixYX = rtnAddToMatrix[0];
                        memoMatrixX = rtnAddToMatrix[1];
                        break;
                    }
                }

                for (var y = 0; y < memoMatrixYX.length; y++) {
                    for (var x = 0; x < memoMatrixX.length; x++) {
                        //alert("memoMatrixYX.length:" + memoMatrixYX.length + " memoMatrixX.length:" + memoMatrixX.length);
                        //alert("IDsDataAll[i].ChildIDTags:" + IDsDataAll[i].ChildIDTags);

                        if (memoMatrixYX[y][x].ID != undefined) {

                            /**
                             *  ChilDIDTagsに親IDの情報を持たせる方式に変更した事に合わせて処理を変更
                             */
                            var StringChildIDTags = "";
                            var AryChildIDTags = [];

                            AryChildIDTags = getChildIDTags(memoMatrixYX[y][x].ID);

                            StringChildIDTags = AryChildIDTags.join(' ');

                            //if (memoMatrixYX[y][x].ChildIDTags != "") {
                            if (StringChildIDTags != "") {
                                //rtnAddToMatrix = getIDDatafromAry(IDsDataAll, memoMatrixYX[y][x].PDCAPhase, memoMatrixYX[y][x].ChildIDTags, y, x, memoMatrixYX, memoMatrixX);
                                rtnAddToMatrix = getIDDatafromAry(IDsDataAll, memoMatrixYX[y][x].PDCAPhase, StringChildIDTags, y, x, memoMatrixYX, memoMatrixX);
                                memoMatrixYX = rtnAddToMatrix[0];
                                memoMatrixX = rtnAddToMatrix[1];
                            }

                            /**
                            * 変更終わり
                            */
                        }
                        if (x > 40) {
                            break;
                        }
                    }
                    if (y > 40) {
                        break;
                    }
                }

                //            alert("描画する");
                disppage4html(memoMatrixYX, "#nametable");
//            })
    }

    /**
     * データベースの全データのChildIDTagsを検索し、対象IDのChildIDTagsを返す
     * @param {any} ParentID
     */
    var getChildIDTags = function (ParentID) {

        /** データベースからデータを取得 */
        var IDsDataAll = inMemoryDB;

        /** 取得したデータを配列で返す */
        var AryChildIDTags = [];

        for (var i = 0; i < IDsDataAll.length; i++) {
            if (IDsDataAll[i].ChildIDTags.indexOf("#" + ParentID + "#") > -1) {
                AryChildIDTags.push(IDsDataAll[i].ID);
            }
        }

        return AryChildIDTags;
    }

    var getIDDatafromAry = function (IDsDataAll, PDCAPhase, ChildIDTags, targetAxisY, targetAxisX, memoMatrixYX, memoMatrixX) {

        /** 2018/05/21 ChildIDTagsに子IDではなく、親IDをセットするように変更したため、
         *  これに合わせて、元々AtryEntry_ID配列に対して、
         *  ChildIDTagsのsplitした値をセットしていたものを、
         *  自IDを元に、全データのChildIDTagsフィールドを検索して一致したレコードのIDを
         *  子IDとしてセットする処理に変更した。
         */
        AryEntry_ID = ChildIDTags.trim().split(" ");
        //var AryEntry_ID = []; //20 35 40番とあった場合に、1:40 2:35 3:20 と降順に入れないと、表組の際に逆に並んでしまうので、
        //                      //item(i) ではなく、item(IDsDataAll.length - 1 - i)という指定に修正している。
        //for (var i = 0; i < IDsDataAll.length; i++) {
        //    //もしChildIDTagsの中に自分のIDがあれば、AryEntry_ID配列にセットする。
        //    //if (IDsDataAll[IDsDataAll.length  - 1 - i].ChildIDTags.indexOf("#" + memoMatrixYX[targetAxisY][targetAxisX].ID + "#") > -1) {
        //    //    console.log(IDsDataAll[IDsDataAll.length - 1 - i].ID);
        //    //    AryEntry_ID.push(IDsDataAll[IDsDataAll.length - 1 - i].ID);
        //    //}
        //    if (IDsDataAll[i].ChildIDTags.indexOf("#" + memoMatrixYX[targetAxisY][targetAxisX].ID + "#") > -1) {
        //        console.log(IDsDataAll[i].ID);
        //        AryEntry_ID.push(IDsDataAll[i].ID);
        //    }
        //}

        var rtnAddToMatrix = "";

        for (var j = 0; j < AryEntry_ID.length; j++) {
            for (var i = 0; i < IDsDataAll.length; i++) {
                if (AryEntry_ID[j] == IDsDataAll[i].ID) {

                    //配列に保存
                    if (PDCAPhase == IDsDataAll[i].PDCAPhase) {
                        addToAxisWay = "Y";
                    } else {
                        addToAxisWay = "X";
                    }

                    //memoMatrixYX[targetAxisY][targetAxisX] = $.extend(true, {}, IDsDataAll[i]);

                    //IDのデータをアレイに保存
                    rtnAddToMatrix = addToMatrix(memoMatrixYX, memoMatrixX, targetAxisY, targetAxisX, addToAxisWay, $.extend(true, {}, IDsDataAll[i]));
                    memoMatrixYX = rtnAddToMatrix[0];
                    memoMatrixX = rtnAddToMatrix[1];
                    break;
                }
            }
        }
        return [memoMatrixYX, memoMatrixX];
    }

    var getIDsDataAll = function () {
        var defer = new $.Deferred();
        var db = app.db.openDB();
        var getIDsDataAll = "";

        db.transaction(function (trans) {
            trans.executeSql('SELECT * FROM DataRelationManage;', [], function (trans, r) {
                getIDsDataAll = r.rows;
                defer.resolve(getIDsDataAll);
            });
        });

        return defer.promise();
    }

    var addToMatrix = function (memoAryYX, memoAryX, targetAxisY, targetAxisX, addToAxisWay, fromgetIDDataArg) {
        var addToAxisX;
        var addToAxisY;

        memoAryX = memoAryX.concat();

        if (addToAxisWay == "") {
            addToAxisX = targetAxisX;
            addToAxisY = targetAxisY;
            fromgetIDDataArg.dispIndentLevel = 0;
        } else if (addToAxisWay == "X") {
            addToAxisX = targetAxisX + 1;
            addToAxisY = targetAxisY;
            fromgetIDDataArg.dispIndentLevel = 0;
        } else if (addToAxisWay == "Y") {
            addToAxisX = targetAxisX;
            addToAxisY = targetAxisY + 1;
            fromgetIDDataArg.dispIndentLevel = memoAryYX[targetAxisY][targetAxisX].dispIndentLevel + 1;
        }

        if ((addToAxisY == 0) && (addToAxisX == 0)) {
            memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
        } else {

            var wflag = 0;  //ループを抜ける場合に1を立てる
            while (wflag == 0) {

                if (addToAxisY > targetAxisY) {
                    //Y軸方向にメモを追加する場合
                    if (memoAryYX[addToAxisY] == undefined) {
                        //セルがない場合
                        //行がない場合は一行追加
                        memoAryYX.splice(addToAxisY, 0, memoAryX.concat());
                        memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                        wflag = 1;
                    } else {
                        //セルがある場合
                        if (memoAryYX[addToAxisY][addToAxisX] != undefined) {
                            if (memoAryYX[addToAxisY][addToAxisX] == "") {
                                //登録しようとしているセルが空の場合

                                var RecordExistFlag = 0; //登録しようとしている行にメモが登録済みの場合、1を立てる。
                                for (var ii = 0; ii < memoAryX.length; ii++) {
                                    if ((memoAryYX[addToAxisY][ii].ID != undefined) && (memoAryYX[addToAxisY][ii].ID != "")) {
                                        if (ii < addToAxisX) {
                                            //追加するセルの左側（<-）にレコードがある場合、行を追加してメモを登録
                                            RecordExistFlag = 1;
                                            memoAryYX.splice(addToAxisY, 0, memoAryX.concat());
                                            memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                                            wflag = 1;
                                            break;
                                        } else if (ii > addToAxisX) {
                                            //追加するセルの右側(->)にレコードがある場合、一行下げてループ
                                            RecordExistFlag = 1;
                                            addToAxisY = addToAxisY + 1;
                                            break;
                                        }
                                    }
                                }

                                if (RecordExistFlag == 0) {
                                    //登録しようとしているセルが空で、同じ行に登録されているメモがない場合、メモを登録
                                    memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                                    wflag = 1;
                                }

                            } else {
                                //登録しようとしているセルにメモが登録済みの場合(IDが登録済みの場合）

                                if (addToAxisX == targetAxisX) {
                                    //親メモと同じカテゴリのメモ（子メモ）を追加する場合

                                    //登録しようとしているセルに格納されているメモのIDが、今回登録しようとしているメモの親メモの子メモに含むかどうかを確認
                                    /** ChilDIDTagsに親IDの情報を持たせる方式に変更した事に合わせて処理を変更 */
                                    //var targetMemoChildID = memoAryYX[targetAxisY][targetAxisX].ChildIDTags.trim().split(" ");
                                    var targetMemoChildID = "";
                                    var AryChildIDTags = [];
                                    targetMemoChildID = getChildIDTags(memoAryYX[targetAxisY][targetAxisX].ID);

                                    var isChildIDFlag = 0; ///
                                    for (var iii = 0; iii < targetMemoChildID.length; iii++) {
                                        if (targetMemoChildID[iii] == memoAryYX[addToAxisY][addToAxisX].ID) {
                                            isChildIDFlag = 1;
                                            break;
                                        }
                                    }

                                    if (isChildIDFlag == 1) {
                                        //登録しようとしているセルに格納されているメモが、今回登録しようとしているメモの親メモに対しての子メモの場合
                                        //一行下げてループ
                                        addToAxisY = addToAxisY + 1;
                                    } else {
                                        //登録しようとしているセルに格納されているメモが、今回登録しようとしているメモの親メモに対しての子メモではない場合
                                        //行を追加してメモを登録
                                        memoAryYX.splice(addToAxisY, 0, memoAryX.concat());
                                        memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                                        wflag = 1;
                                    }
                                } else {
                                    //親メモと同じカテゴリのメモ（子メモ）を追加する場合ではない場合
                                    //一行下げてループ
                                    addToAxisY = addToAxisY + 1;
                                }
                            }
                        }
                    }
                } else if (addToAxisY <= targetAxisY) {
                    if (addToAxisX > targetAxisX) {
                        //X軸方向にメモを追加する場合

                        if (memoAryYX[addToAxisY][addToAxisX] == undefined) {
                            //セルがない場合
                            //列がない場合は一列追加
                            memoAryX.splice(addToAxisX, 0, "");
                            for (var i = 0; i < memoAryYX.length; ++i) {
                                memoAryYX[i].splice(addToAxisX, 0, "");
                            }
                            memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                            wflag = 1;
                        } else {
                            //セルがある場合
                            if (memoAryYX[addToAxisY][addToAxisX] == "") {
                                //登録しようとしているセルが空の場合メモを登録する
                                memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                                wflag = 1;
                            } else {
                                //登録しようとしているセルにメモが登録済みの場合(IDが登録済みの場合）
                                //X軸方向に追加する流れの場合、1行下げる（ループしてY軸方向に登録する流れでメモを登録）
                                addToAxisY = addToAxisY + 1;
                            }
                        }
                    }
                }
            }
        }

        return [memoAryYX, memoAryX];
    }

    var disppage4html = function (memoAryYX, targetNametable) {

        //        temphtml = "<table color=red><tr><td>P</td><td>D</td><td>C</td><td>A</td></tr>";
        if (targetNametable == "#selectedNametable") {
            var temphtml = "<table style=\"table-layout: fixed; \" width=" + memoAryYX[0].length * 100 + "%>";
        } else {
            var temphtml = "<table style=\"table-layout: fixed; \" width=" + memoAryYX[0].length * 45 + "%>";
        }
        //        alert(memoAryYX[0].length * 20);

        var dispPaddingLeftLevel;
        var dispTextIndentLevel;
        var dispTextIndentAsterix;

        for (var y = 0; y < memoAryYX.length; ++y) {

            temphtml = temphtml + "<tr>";
            for (var x = 0; x < memoAryYX[y].length; ++x) {
                //配列要素がundefinedの場合（memoデータがない場合があるため）
                if ((memoAryYX[y][x] == undefined) || (memoAryYX[y][x] == "")) {
                    temphtml = temphtml + "<td style=background-color:LightGray>";
                    temphtml = temphtml + "</td>";
                    continue;
                } else {
                    if (memoAryYX[y][x].PDCAPhase == "P") {
                        tdBgColor = TitleColorP;
                    } else if (memoAryYX[y][x].PDCAPhase == "D") {
                        tdBgColor = TitleColorD;
                    } else if (memoAryYX[y][x].PDCAPhase == "C") {
                        tdBgColor = TitleColorC;
                    } else if (memoAryYX[y][x].PDCAPhase == "A") {
                        tdBgColor = TitleColorA;
                    }
                }

                //インデント設定
                dispPaddingLeftLevel = (memoAryYX[y][x].dispIndentLevel * 1);
                dispTextIndentLevel = (memoAryYX[y][x].dispIndentLevel * 1);
                dispTextIndentAsterix = "";
                for (var i = 0; i < memoAryYX[y][x].dispIndentLevel; i++) {
                    dispTextIndentAsterix = dispTextIndentAsterix + "⇒";
                }
                //                temphtml = temphtml + "<td style=\"width:30%; background-color:" + tdBgColor + ";\">"; //tr側でも幅を指定するとレイアウトが崩れるのでtableタグのみ指定
                temphtml = temphtml + "<td style=\"word-wrap:break-word; background-color:" + tdBgColor + "; vertical-align: top; padding-left:" + dispPaddingLeftLevel + "em; text-indent:-" + dispTextIndentLevel + "em; \" data-entry_id=\"" + memoAryYX[y][x].ID + "\">";
                temphtml = temphtml + dispTextIndentAsterix + "(" + memoAryYX[y][x].PDCAPhase + ")" + "(" + memoAryYX[y][x].ID + ") (" + memoAryYX[y][x].CreateDate + ")";
                temphtml = temphtml + "<a href=\"#\" data-entry_id=\"" + memoAryYX[y][x].ID + "\">[投稿画面]</a><BR />";
                temphtml = temphtml + memoAryYX[y][x].ContentTitle.replace(/https?:\/\/[\w.\-_@:/~?%&;=+#',()*!]+/g, "<a href='#' val=\"$&\">$&</a >").replace(/\n/g, '<BR />');
                temphtml = temphtml + "</td>";
            }
            temphtml = temphtml + "</tr>";
        }

        temphtml = temphtml + "</table>";

        /** 描画時のフェードアウト、フェードアウトを省略 */
//        $.when($('#nametable').html(temphtml)).done(function () {
//            $('html,body').animate({ opacity: 0 }, "fast", function () {
////                location.href = "#four";
//                $('html,body').animate({ opacity: 1 }, "fast", function () {
//                    // backbutton関連
//                    //        document.removeEventListener("backbutton", navBackPage, false); //android backbuttonを制御
//                    //        document.addEventListener("backbutton", backButtonRenew, false);
//                })
//            })
//        })

        //$('#nametable').html(temphtml);
        $(targetNametable).html(temphtml);

    }


    var clickConfirmAndOpenURL = function (strURL) {

        var res = confirm("URLを開きますか？");

        if (res) {
            open.window(strURL);
        }

    }


    $('#four > #nametable').on('click', 'td', function () {
        /**
        var selectedid = $(this).data('entry_id');

        var notebook_length = $('#notebook_length').val();

        /** スレッドの書き込み数を確認する * /
        if (notebook_length > notebookIDMaxRecord) {
        /** スレッドの書き込み数が最大行数を超えた場合、警告メッセージを表示して処理を終了する * /
            alert(msgOverMaxRecord);
        } else {
        /** スレッドの書き込み数が最大行数を超えない場合、クリックされたコメントのIDを書き込み画面にセットし、書き込み画面に遷移する * /
            if (selectedid != undefined) {
                var IdBeforeMove = $('#selecteditem').find('.entry_ID').val();
                var HistoryList = $('#WBSPageHistory').val();

                if (selectedid != IdBeforeMove) {
                    if (IdBeforeMove != "") {
                        $('#WBSPageHistory').val(HistoryList + ' ' + IdBeforeMove);
                    };
                }

                $.when(dispWBSPage(selectedid)).done(function () {
                    $('html,body').animate({ opacity: 1 }, "fast");
                    $('html,body').animate({ scrollTop: 0 }, 0);

                    //2018.04.24 修正画面ではなく、新規登録画面へ遷移するように修正
                    //location.href = "#one";
                    location.href = "#two";
                })
            }
        }
         * 
         */
    })

    /** wordrobeページで 書き込まれたURL（aタグ）をクリックされた時の処理、「投稿画面」をクリックされた時の処理 */
    $('#four > #nametable').on('click', 'td > a', function (e) {

        var selectedid = $(this).data('entry_id');

        var selectedURL = $(this).attr('val');

        dispListChildDataForSelectedNametable(selectedid);

        if (selectedid == undefined) {

            if (selectedURL != undefined && selectedURL != "") {
                var res = confirm("このURLを開きますか？（別ウィンドウで開きます。）\n" + selectedURL);

                if (res) {
                    window.open(selectedURL, "_blank");
                }
            }
            e.stopPropagation();
        } else {
            var notebook_length = $('#notebook_length').val();

            /** スレッドの書き込み数を確認する */
            if (notebook_length > notebookIDMaxRecord) {
                /** スレッドの書き込み数が最大行数を超えた場合、警告メッセージを表示して処理を終了する */
                alert(msgOverMaxRecord);
            } else {
                /** スレッドの書き込み数が最大行数を超えない場合、クリックされたコメントのIDを書き込み画面にセットし、書き込み画面に遷移する */
                if (selectedid != undefined) {
                    var IdBeforeMove = $('#selecteditem').find('.entry_ID').val();
                    var HistoryList = $('#WBSPageHistory').val();

                    if (selectedid != IdBeforeMove) {
                        if (IdBeforeMove != "") {
                            $('#WBSPageHistory').val(HistoryList + ' ' + IdBeforeMove);
                        };
                    }

                    $.when(dispWBSPage(selectedid)).done(function () {
                        $('html,body').animate({ opacity: 1 }, "fast");
                        $('html,body').animate({ scrollTop: 0 }, 0);

                        //2018.04.24 修正画面ではなく、新規登録画面へ遷移するように修正
                        //location.href = "#one";
                        $(":mobile-pagecontainer").pagecontainer("change", "#two", { transition: "slide" });
                        //location.href = "#two";
                    })
                }
            }
        }

    })

    /** コメント書き込み画面で 選択されたコメント枠で書き込まれたURL（aタグ）をクリックされた時の処理 */
    $('#selectedNametable').on('click', 'td > a', function (e) {

        var selectedid = $(this).data('entry_id');

        var selectedURL = $(this).attr('val');

        if (selectedid == undefined) {

            if (selectedURL != undefined && selectedURL != "") {
                var res = confirm("このURLを開きますか？（別ウィンドウで開きます。）\n" + selectedURL);

                if (res) {
                    window.open(selectedURL, "_blank");
                }
            }
            //e.stopPropagation();
        }

    })

    var dispListChildDataForSelectedNametable = function (entry_ID) {

        var memoMatrixX = [""];
        var memoMatrixYX = [memoMatrixX.concat()];
        var addToAxisWay = "";

        var targetAxisX = 0;
        var targetAxisY = 0;

        var rtnAddToMatrix;

        var IDsDataAll = inMemoryDB;

        for (var i = 0; i < IDsDataAll.length; ++i) {
            if (entry_ID == IDsDataAll[i].ID) {

                rtnAddToMatrix = addToMatrix(memoMatrixYX, memoMatrixX, targetAxisY, targetAxisX, addToAxisWay, $.extend(true, {}, IDsDataAll[i]));
                memoMatrixYX = rtnAddToMatrix[0];
                memoMatrixX = rtnAddToMatrix[1];
                break;
            }
        }

        disppage4html(memoMatrixYX, "#selectedNametable");
    }

    $('#CopyWordrobe').on('click', function () {
        var range = document.createRange();
        var element = document.getElementById('nametable');
        range.selectNodeContents(element);

        var selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(range);

        document.execCommand('copy');

        alert("クリップボードにコピーしました");
    })


    // backbutton関連
    //    var backButtonRenew = function () {
    //        window.history.back();
    ////        document.addEventListener("backbutton", navBackPage, false); //android backbuttonを制御
    ////        document.removeEventListener("backbutton", backButtonRenew, false);
    //    }

    var navBackPage = function () {
        var historyLine = $('#WBSPageHistory').val();
        var lastId;

        $('html,body').animate({ opacity: 0 }, "fast", function () {
            //history文字列の最後のデリミタ' '以降の文字を取得
            lastId = historyLine.substring(historyLine.lastIndexOf(' ') + 1);

            $('#WBSPageHistory').val(historyLine.substring(0, historyLine.lastIndexOf(' ')));
            $('#selecteditem').find('.entry_ID').val("");
            $.when(dispWBSPage(lastId))
                .done(function () {
                    $('html,body').animate({ opacity: 1 }, "fast");
                    location.href = "#one"; // jquerymobile のfooter bug対応。これがないとfixされないバグがある。
                });

        })
        //$('html,body').animate({ scrollTop: 0 }, 5000);
    }

    $('#navBackPage').on('click', function () {
        navBackPage();
    })

    //// backbutton関連
    //$('#navBackPage4').on('click', backButtonRenew)

    $('#deleteIdData').on('click', function () {
        var deleteid = $('#selecteditem').find('.entry_ID').val();
        $.when(deleteIdData(deleteid)).done(function () { navBackPage() })
    })

    var clearInsertPlusData = function (l_this) {
        var closestdom = $(l_this).closest('#insertitem');

        closestdom.find(".entry_ChildIDTags").val("");
        closestdom.find(".entry_CreateDate").val("");
        closestdom.find(".entry_UpdateDate").val("");
        closestdom.find(".entry_DeleteDate").val("");
        //同じPHASEを入力し続ける場合が多いと考えて、PDCAPhaseの値はリセットしない
        //        closestdom.find("[name=entry_PDCAPhase]").val('P');
        //        closestdom.find("[name=entry_PDCAPhase]").selectmenu("refresh", true);

        closestdom.find(".entry_ContentTitle").val("");
        closestdom.find(".entry_ContentDesc").val("");
        closestdom.find(".entry_ContentStatus").val("");
    }


    //insertPlusData page
    $('#insertPlusData').on('click', function () {
        //$('#insertitem').find('select[name=entry_PDCAPhase]').val("A");
        //alert($('#insertitem').find('select[name=entry_PDCAPhase]').val());

        $('#insertPlusData').hide();
        $.when(insertPlusData(this)).then(function (l_this) {
            dispWBSPage($('#selecteditem').find('.entry_ID').val());
            $('#insertPlusData').show();

            window.history.back();
            clearInsertPlusData(l_this);
        })
    });

    $('.wbs_selectbox_setValue').on('click', function () {
        //        alert($(this).val());
        var selectedval = $(this).val();
        if (selectedval != ' ') {
            $('.ui-filterable').find('input').val(selectedval);
        } else {
            $('.ui-filterable').find('input').val("");
        }
        $("ul").listview().listview('refresh');
        $('.ui-filterable').find('input').focus();
        $('.ui-filterable').find('input').blur();
    })


    $('.ui-controlgroup-controls').on('click', 'a', function () {

        var selectedid = $(this).data('entry_id');

        var wbslistdomname = $('#wbslist_on_page_three')

        var defer = $.Deferred();
        $.when(getIDData(selectedid))
            .done(function (fromgetIDDataArg) {
                //dispIdData(fromgetIDDataArg, '#selecteditemWithCT');
                //TODO:add to listの一覧も表示できるようにする。
                //このまま呼び出すとトップ側も変わり、自分自身にaddされてしまう。
                dispWBSPage(selectedid, '#selecteditemWithCT', '#wbslist_on_page_three');
            })
    })

    $('#AddToList').on('click', function () {

        var selectedid = $(this).closest('#selecteditemWithCT').find('.entry_ID').val();

        var parentid = $('#selecteditem').find('.entry_ID').val();
        //var selectedid = $(this).data('entry_id');

        addChildIdTags(selectedid);

        dispWBSPage(parentid);

        window.history.back();
        window.history.back();
    })


    $('.adminMode').on('click', function () {
        $('.toggleColumn').slideToggle();

    })

    $('#one > .wbslist').on('taphold', 'li', function () {

        if ($('#selecteditem').find('.entry_ID').val() != "") {
            $('#popupLeaveOutDialog').find('.entry_LeavesID').val($(this).data('entry_id'));
            $('#popupLeaveOutDialog').find('.entry_ContentTitle').val($(this).html());
            $('#popupLeaveOutDialog').popup('open', { transition: 'pop' });
        }
    })

    $('#leaveOutIdData').on('click', function () {

        var baseID = $('#selecteditem').find('.entry_ID').val();
        var baseIDChildIdTags = $('#selecteditem').find('.entry_ChildIDTags').val();
        var leaveOutId = $('#popupLeaveOutDialog').find('.entry_LeavesID').val();
        var regExpStr = leaveOutId;
        var leavedBaseIDChildIdTags = baseIDChildIdTags.replace(new RegExp(regExpStr, "g"), "").replace(/  /g, " ");

        $('html,body')
            .animate({ opacity: 0 }, "fast", function () {
                $.when(modifyChildIdTags(baseID, leavedBaseIDChildIdTags)).done(dispWBSPage(baseID));
                $('html,body').animate({ opacity: 1 }, "fast")
            })
    })

    $('.threadList_on_page_one').on('click', 'li', function () {

        ////ローカルDBの情報をtruncateして空にする
        //deleteAllData();

        ////notebook一覧でクリックされたnotebookIDを取得
        var selectedNotebookID = $(this).data('entry_notebook_id');

        ////notebook一覧でクリックされたnotebookIDを#notebook_idにセット（notebookID保持に使用）
        //$('#notebook_id').val(selectedNotebookID);

        ////クリックされたnotebookのnotebookIDの情報をサーバから取得し、ローカルDBへ保存する
        //insertLDBFromSVR(selectedNotebookID);

        ////PDCA Link画面に遷移
        //$.when(dispWBSPage('')).done(function () {

        //    //noteID"1"を起点にしたWordrobeページを表示する
        //    var entry_ID = "1";
        //    dispListChildData(this, entry_ID);

        //    //#oneに遷移して一覧を表示する
        //    //location.href = "#one";
        //})

        var hrefString = "../client/index.html" + "?notebookID=" + selectedNotebookID;

        location.href = hrefString;

    })

    /*
        説明:クリックされたnotebookのnotebookIDの情報をサーバから取得し、ローカルDBへ保存する
         INPUT:notebookID （notebookの情報を保存したファイル名から拡張子を除いた部分）
         RETURN:TextData
    */
    var insertLDBFromSVR = function (l_selectedNotebookID) {

        //var selectedNotebookID = $(l_this).data('entry_notebook_id');

        //$('#notebook_id').val(selectedNotebookID);

        //alert($('#notebook_id').val());

        //var targetFile = "sample.csv";
        var TextData;

        TextData = getTextDataFromSVR(l_selectedNotebookID);

        for (var i = 0; i < TextData.length; i++) {
            insertData(TextData[i]);
        }

        outputDebuglog(TextData);

        return TextData;
    }

    //テキストデータのフォーマット（DataRelationManageテーブルの項目と同じ）
    //notebookのレコードについては、このフォーマットに合わせて入出力を行う
    var TextRowDataFormat = {
        ID:"",
        ChildIDTags:"",
        CreateDate: "",
        UpdateDate: "",
        DeleteDate: "",
        PDCAPhase: "",
        ContentTitle: "",
        ContentDesc: "",
        ContentStatus: ""
    }

    //テキストデータのサーバ側へ送信する際のフォーマット
    var TextRowDataToSVRFormat = {
        ID: "",
        ChildIDTags: "",
        CreateDate: "",
        UpdateDate: "",
        DeleteDate: "",
        PDCAPhase: "",
        ContentTitle: "",
        ContentDesc: "",
        ContentStatus: "",

        notebookID: "",  // 新規登録時、更新時にnotebookID(notebookID.csv)の情報をサーバへ送る処理で追加する
        ParentID: "",  // 新規登録時、更新時にサーバへ送る処理で使用する

        gRecaptchaResponse: "" //reCAPTCHA用トークン
    }

    /*
        説明:サーバからnotebookIDで指定したnotebookレコード情報を取得する
         INPUT:notebookID （notebookの情報を保存したファイル名から拡張子を除いた部分）
         RETURN:allData[] notebookレコード配列
    */
    function getTextDataFromSVR(l_notebookID) {

        //var targetFile = "sample.csv";
        var targetNotebookID = "http://" + sourceDomain + ":" + sourcePort + pioAbusolutePath + "SelectNote?notebookID=" + l_notebookID;

        // 読み込んだデータを1行ずつ格納する配列
        var allData = [];

        /** get処理（XMLHttpRequestを使用した場合） */
        //// XMLHttpRequestを用意
        //var request = new XMLHttpRequest();
        //// XMLHttpRequest.openを実行し、notebookID.csvをgetで取得する
        //request.open("get", targetNotebookID, false);
        //request.send(null);
        //
        //// 読み込んだCSVデータをcsvDataに格納する
        //var csvData = request.responseText;

        /** get処理（XMLHttpRequestを使用した場合） */

        $.ajaxSetup({ cache: false });

        var csvData = $.ajax({
            type: 'GET',
            url: targetNotebookID,
            async: false,
            timeout:10000
        }).responseText;

        outputDebuglog(csvData);

        /** CSVの全行を取得し、改行コードでsplitし、配列に格納する */
        var lines = csvData.split("\r\n");

        /** 読み込んだcsvデータ配列を読み込み、配列に格納する */
        for (var i = 0; i < lines.length; i++) {

            /** 取得した行が空欄でない場合 */
            if ((lines[i] != "") && (lines[i] != "\n")) {

                //両サイドの""を省いた上で、"."でsplitし、配列に格納する
                var TextRowDataSet = lines[i].slice(1, -1).split('","');

                var TextRowData = TextRowDataFormat;

                TextRowData = {
                    ID: validCHK(TextRowDataSet[0]),
                    ChildIDTags: validCHK(TextRowDataSet[1]),
                    CreateDate: validCHK(TextRowDataSet[2]),
                    UpdateDate: validCHK(TextRowDataSet[3]),
                    DeleteDate: validCHK(TextRowDataSet[4]),
                    PDCAPhase: validCHK(TextRowDataSet[5]),
                    ContentTitle: validCHK(TextRowDataSet[6]),
                    ContentDesc: validCHK(TextRowDataSet[7]),
                    ContentStatus: validCHK(TextRowDataSet[8])
                };

                allData.push(TextRowData);
            }
        }

        return allData;
    }

    var deleteSideDQuot = function (strOrigin) {
        console.log(strOrigin);
//        return strOrigin.replace(/'/g, '');
        return strOrigin;
    }

    /**
     *  説明:TextRowDataFormat形式のDataRelationManageテーブルデータをDataRelationManageテーブルにINSERTする
     *   NPUT:TextRowDataFormat形式の DataRelationManageテーブルデータ（１レコード）
     *  OUTPUT:defer.promise()
    */
    function insertData(l_TextData) {
        var db = app.db.openDB();
        var defer = $.Deferred();

        db.transaction(function (trans) {
            trans.executeSql('INSERT INTO DataRelationManage ('
                + 'ID, ChildIDTags, CreateDate, UpdateDate, DeleteDate, PDCAPhase, ContentTitle, ContentDesc, ContentStatus'
                + ') VALUES ('
                + '"' + l_TextData.ID + '"'
                + ',"' + l_TextData.ChildIDTags + '"'
                + ',"' + l_TextData.CreateDate + '"'
                + ',"' + l_TextData.UpdateDate + '"'
                + ',"' + l_TextData.DeleteDate + '"'
                + ',"' + l_TextData.PDCAPhase + '"'
                + ',"' + l_TextData.ContentTitle + '"'
                + ',"' + l_TextData.ContentDesc + '"'
                + ',"' + l_TextData.ContentStatus + '"'
                + ');', [], function () {
                });
        });

        return defer.promise();
    }

    /*
        説明:DataRelationManageテーブルをtruncateする
         NPUT: -
        OUTPUT:-
    */
    function deleteAllData() {
        var db = app.db.openDB();
        var defer = $.Deferred();

        //db.transaction(function (trans) {
        //    trans.executeSql('DELETE FROM DataRelationManage ;', [], function () {});
        //});

        var strQueryString = 'DELETE FROM DataRelationManage ';
        DbTransaction(db, strQueryString);

        return defer.promise();
    }

    //insertPlusData page
    $('#insertPlusDataToSVR').on('click', function () {

        var TextData;
        var notebookID = $('#notebook_id').val();

        /** reCAPTCHA用設定 */
        var recaptchaRes = $('#g-recaptcha-response').val();

        if (reCAPTCHAUse == true) {
            /** reCAPTCHA認証のトークンをセット（トークンがundefinedか空の場合はメッセージを出力して処理を終了する）*/
            if (recaptchaRes == undefined || recaptchaRes == "") {
                alert("reCAPTCHA認証の四角をクリックしてチェックを入れて下さい。");
                return 0;
            }
        }

        /** 新規登録情報（を含む全データ(this)）とnotebookIDを渡して、新規登録データを取得する */
        TextData = getPlusData(this, notebookID, recaptchaRes);

        /** 選択したコメントのID */
        var parentId = $('#selecteditem').find('.entry_ID').val();

        /** 新規登録画面のPDCA Phaseにセットされた値を取得して格納 */
        var entry_PDCAPhase = $('#insertitem').find('[name=entry_PDCAPhase]').val();

        $.when(getIDData(parentId))                                   //選択されたIDのデータを取得（してPDCA Phaseを取得）
            .done(function (fromgetIDDataArg) {

                /** 選択したコメントのPDCA Phaseと、新規登録するデータのPDCA Phaseの組み合わせを確認 */
                if (fromgetIDDataArg.PDCAPhase == "P" && !(entry_PDCAPhase == "P" || entry_PDCAPhase == "D")) {
                    alert("選択したコメントのPDCA Phaseが (P)Plan なので、PDCA Phaseは (P)Plan か (D)Do を選択して下さい。");

                } else if (fromgetIDDataArg.PDCAPhase == "D" && !(entry_PDCAPhase == "D" || entry_PDCAPhase == "C")) {
                    alert("選択したコメントのPDCA Phaseが (D)Do なので、PDCA Phaseは (D)Do か (C)Check を選択して下さい。");

                } else if (fromgetIDDataArg.PDCAPhase == "C" && !(entry_PDCAPhase == "C" || entry_PDCAPhase == "A")) {
                    alert("選択したコメントのPDCA Phaseが (C)Check なので、PDCA Phaseは (C)Check か (A)Action を選択して下さい。");

                } else if (fromgetIDDataArg.PDCAPhase == "A" && !(entry_PDCAPhase == "A" || entry_PDCAPhase == "P")) {
                    alert("選択したコメントのPDCA Phaseが (A)Action なので、PDCA Phaseは (Action) か (P)Plan を選択して下さい。");

                } else {
                    /**
                    * PDCA Phaseの組み合わせが正しい場合
                    */

                    /** 記事投稿前の確認画面 */
                    var res = confirm(msgConfirm);

                    /** 投稿前画面でYesがクリックされた場合の処理 */
                    if (res == true) {

                        /** 新規登録データをサーバのinsert APIにPOST送信する。 */
                        for (var i = 0; i < TextData.length; i++) {
                            insertPlusDataToSVR(TextData[i], notebookID);
                        }

                        /** wordrobeページに画面を遷移する */
                        //ローカルDBの情報をtruncateして空にする
                        deleteAllData();

                        /**
                        //クリックされたnotebookのnotebookIDの情報をサーバから取得し、ローカルDBへ保存する
                        insertLDBFromSVR(notebookID);
            
                        //PDCA Link画面に遷移
                        $.when(dispWBSPage('')).done(function () {
            
                            //noteID"1"を起点にしたWordrobeページを表示する
                            var entry_ID = "1";
                            dispListChildData(this, entry_ID);
            
                            //            //#oneに遷移して一覧を表示する
                            //            location.href = "#one";
                            //#fourに遷移して一覧を表示する
                            location.href = "./notebook.html?notebookID=" + notebookID;
                        })*/

                        location.href = "./notebook.html?notebookID=" + notebookID;
                    }
                }
            })
    });

    //insertPlusData page
    $('#makeNewNotebookToSVR').on('click', function () {

        /** 記事投稿前の確認画面 */
        var res = confirm(msgConfirm);

        /** reCAPTCHA用設定 */
        var recaptchaRes = $('#g-recaptcha-response').val();

        /** reCAPTCHA認証のトークンをセット（トークンがundefinedか空の場合はメッセージを出力して処理を終了する）*/
        if (reCAPTCHAUse == true) {
            if (recaptchaRes == undefined || recaptchaRes == "") {
                alert("reCAPTCHA認証の四角をクリックしてチェックを入れて下さい。");
                exit;
            }
        }

        /** 投稿前画面でYesがクリックされた場合の処理 */
        if (res == true) {

            /** notebook作成画面の登録カテゴリの情報を格納 */
            var category = $('#entry_notebookCategory').val();

            var TextData;
            var notebookID = category + "/new";

            /** 新規登録情報（を含む全データ(this)）とnotebookIDを渡して、新規登録データを取得する */
            TextData = getPlusData(this, notebookID, recaptchaRes);

            /** 新規登録データをサーバのinsert APIにPOST送信する。 */
            for (var i = 0; i < TextData.length; i++) {
                makeNewNotebookToSVR(TextData[i], notebookID);
            }

            location.href = "../menu/index.html";
        }
    });


    function getPlusData(l_this, l_notebookID, recaptchaRes) {

        // 読み込んだデータを1行ずつ格納するMAP配列を用意
        var allData = [];

        // 入力画面の情報をオブジェクトにセットする
        var closestdom = $(l_this).closest('#insertitem');

        // TextRowDataToSVRFormatフォーマットの配列を用意
        var TextRowDataToSVR = TextRowDataToSVRFormat;

        // TextRowDataToSVRに入力画面の情報をセットする
        TextRowDataToSVR = {
            ID:            "",
            ChildIDTags: validCHK(closestdom.find(".entry_ChildIDTags").val()),
            CreateDate: validCHK(closestdom.find(".entry_CreateDate").val()),
            UpdateDate: validCHK(closestdom.find(".entry_UpdateDate").val()),
            DeleteDate: validCHK(closestdom.find(".entry_DeleteDate").val()),
            PDCAPhase: validCHK(closestdom.find("[name=entry_PDCAPhase]").val()),
            ContentTitle: validCHK(closestdom.find(".entry_ContentTitle").val()),
            ContentDesc: validCHK(closestdom.find(".entry_ContentDesc").val()),
            ContentStatus: validCHK(closestdom.find(".entry_ContentStatus").val()),

            // TextDataにnotebookID(csvのファイル名)をappendする
            notebookID: l_notebookID,
            // TextDataに親IDをappendする
            ParentID: $('#selecteditem').find('.entry_ID').val(),

            // TextDataにreCAPTCHA認証のトークンをセットする
            RecaptchaResponse: recaptchaRes
        };

        //allDataにTextRowDataToSVRの情報をmap配列としてpushする
        allData.push(TextRowDataToSVR);

        // 入力画面の情報をセットしたmap配列を返却する
        outputDebuglog(allData);
        return allData;
    }

    function outputDebuglog(targetObject) {
        console.log(outputDebuglog.caller.name + ":");
        console.log(targetObject);
    }
    
    function insertPlusDataToSVR(TextData, notebookID) {
        outputDebuglog(TextData);
        outputDebuglog(notebookID);

        var targetNotebookID = "http://" + sourceDomain + ":" + sourcePort + pioAbusolutePath + "InsertNote";

        /** XMLHttpRequestを使用した場合 */
        /*
        // XMLHttpRequestの用意
        var request = new XMLHttpRequest();
        request.open("post", targetNotebookID, false);

        //サーバに対して解析方法を指定
        request.setRequestHeader('Content-Type',
            "application/x-www-form-urlencoded;charset=UTF-8");

        //データをリクエスト ボディに含めて送信する
        request.send(EncodeHTMLForm(TextData));
        */

        /** jqueryを使用した場合 */
        var result = $.ajax({
            type: 'POST',
            url: targetNotebookID,
            async: false,
            data: TextData,
            timeout:10000
        }).responseText;

        outputDebuglog(result);

    }

    function makeNewNotebookToSVR(TextData, notebookID) {
        outputDebuglog(TextData);
        outputDebuglog(notebookID);

        var targetNotebookID = "http://" + sourceDomain + ":" + sourcePort + pioAbusolutePath + "MakeNotebook";

        var result = $.ajax({
            type: 'POST',
            url: targetNotebookID,
            async: false,
            data: TextData,
            timeout: 10000
        }).responseText;

        outputDebuglog(result);

    }

    // HTMLフォームの形式にデータを変換する
    function EncodeHTMLForm(data) {
        //https://so-zou.jp/web-app/tech/programming/javascript/ajax/post.htm

        var params = [];

        for (var name in data) {
            var value = data[name];
            var param = encodeURIComponent(name) + '=' + encodeURIComponent(value);

            params.push(param);
        }

        return params.join('&').replace(/%20/g, '+');
    }

    $('.toCategory').on('click', function () {

        var categoryName = $('#notebook_id').val().split("/")[0];

        var hrefString = "../client/index.html" + "?notebookID=" + categoryName + "/index";

        location.href = hrefString;
    });

    $('.toNotebook').on('click', function () {

        /** 親IDが空欄の場合は戻るボタンを生成して戻る */
        if ($('#selecteditem').find('.entry_ID').val() == "") {
            location.href = location.href.substr(0, location.href.indexOf('#'));
        } else {
        /** 親IDが空欄ではない場合はブラウザの戻る履歴から戻る */
            window.history.back();
        }
    });

});
