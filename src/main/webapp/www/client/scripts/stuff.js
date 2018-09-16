
var testfanc372 = function (entry_ID, mxy, x, y, addway, BeforePDCAPhase) {

    //        var promise = getIDsData(entry_ID);
    var tmpChildIDTags;
    var matrix_Y;
    var matrix_X;

    //        promise.then(function (IDsData) {
    $.when(getIDsData(entry_ID))
    .then(function (IDsData) {

        //ココから

        //CID毎に処理
        for (var i = 0; i < IDsData.length; i++) {
            alert(IDsData[i].ID);
            //配列に保存
            if (BeforePDCAPhase == "") {
                addToAxisWay = "";
                matrix_Y = x;
                matrix_X = y;
            }
            else if (BeforePDCAPhase == fromgetIDDataArg[0].PDCAPhase) {
                addToAxisWay = "Y";
            } else {
                addToAxisWay = "X";
            }
            //alert("BeforePDCAPhase:" + BeforePDCAPhase);

            mxy = addToMatrix($.extend(true, {}, mxy), matrix_Y, matrix_X, addToAxisWay, $.extend(true, {}, IDsData[i]));

            tmpChildIDTags = IDsData[i].ChildIDTags;
            testfanc372(tmpChildIDTags, mxy, x, y, addway, $.extend(true, {}, IDsData[i].PDCAPhase));
        }
        //ココまで


    }).then(function () {
        location.href = "#four";

        temphtml = "";

        $('#nametable').html(temphtml);
    })

}


var testfunc382 = function (abc) {

    var m_x = ["", "", "", ""];
    var m_yx = [m_x.concat()];
    var ChildID;
    var BeforePDCAPhase = "";

    ChildID = $('#selecteditem').find('.entry_ID').val();

    for (var y = 0; y < m_yx.length; y++) {
        for (var x = 0; x < m_x.length; x++) {
            alert("ChildID:" + ChildID);
            alert("1m_yx.length:" + m_yx.length + " m_x.length:" + m_x.length);
            m_yx = ListChildData(ChildID, y, x, m_yx, BeforePDCAPhase);

            alert("エンド1");
            alert("エンド2");
            alert("m_x.length:" + m_x.length);
            alert("m_yx.length:" + m_yx.length);
            alert("エンド3");

            BeforePDCAPhase = m_yx[y][x].PDCAPhase;
            alert("2m_yx.length:" + m_yx.length + " m_x.length:" + m_x.length);

            ChildID = m_yx.ChildIDTags;

            alert("3m_yx.length:" + m_yx.length + " m_x.length:" + m_x.length);

        }
    }
}


var ListChildData = function (entry_ID, matrix_Y, matrix_X, memoAryYX, BeforePDCAPhase) {

    if (entry_ID == "") {
        entry_ID = $('#selecteditem').find('.entry_ID').val();
    }

    var defer = $.Deferred;
    //entry_idの情報を取得し、配列[0][0]に登録
    $.when(getIDsData(entry_ID))
        .then(function (fromgetIDDataArg) {

            if (BeforePDCAPhase == "") {
                addToAxisWay = "";
            }
            else if (BeforePDCAPhase == fromgetIDDataArg[0].PDCAPhase) {
                addToAxisWay = "Y";
            } else {
                addToAxisWay = "X";
            }

            alert("ID" + fromgetIDDataArg[0].ID);

            memoAryYX = addToMatrix(memoAryYX, matrix_Y, matrix_X, addToAxisWay, $.extend(true, {}, fromgetIDDataArg[0]));
            return $.extend(true, {}, memoAryYX);

            alert(memoAryYX[matrix_Y][matrix_X].ID);
        })
        .then(function (fromgetIDDataArg) {
            alert("6 表示");
            // 表示処理

            var temphtml;
            var tdBgColor;

            temphtml = "<table color=red><tr><td>P</td><td>D</td><td>C</td><td>A</td></tr>";

            for (var y = 0; y < memoAryYX.length; ++y) {

                temphtml = temphtml + "<tr>";
                for (var x = 0; x < memoAryYX[y].length; ++x) {
                    //配列要素がundefinedの場合（memoデータがない場合があるため）
                    if (memoAryYX[y][x] == undefined) {
                        temphtml = temphtml + "<td style=background-color:white>";
                        temphtml = temphtml + "</td>";
                        continue;
                    };

                    if (memoAryYX[y][x].PDCAPhase == "P") {
                        tdBgColor = "red";
                    } else if (memoAryYX[y][x].PDCAPhase == "D") {
                        tdBgColor = "yellow";
                    } else if (memoAryYX[y][x].PDCAPhase == "C") {
                        tdBgColor = "blue";
                    } else if (memoAryYX[y][x].PDCAPhase == "A") {
                        tdBgColor = "green";
                    }
                    temphtml = temphtml + "<td style=background-color:" + tdBgColor + ">";
                    temphtml = temphtml + "(" + memoAryYX[y][x].PDCAPhase + ") " + "(" + memoAryYX[y][x].ID + ") " + memoAryYX[y][x].ContentTitle;
                    temphtml = temphtml + "</td>";
                }
                temphtml = temphtml + "</tr>";
            }

            temphtml = temphtml + "</table>";

            $('#nametable').html(temphtml);

        })
    .then(function () {
        location.href = "#four";
        alert("222222");
    })

}

var getIDsData = function (selectedid) {
    var defer = new $.Deferred();
    var db = app.db.openDB();
    var IDsData = "";
    var str_SqlWhere = "";

    if ((selectedid != undefined)) {
        str_SqlWhere = "where id in ('" + selectedid.replace(/ /g, '\',\'') + "')";
    }

    db.transaction(function (trans) {
        trans.executeSql('SELECT * FROM DataRelationManage ' + str_SqlWhere + ';', [], function (trans, r) {
            getIDsData = r.rows;
            defer.resolve(getIDsData);
        });
    });

    return defer.promise();
}


var getIDDataRecursive = function (fromgetID) {

    $.when(getIDData(fromgetID))
        .then(function (fromgetIDDataArg) {
        })
}


var addToMatrix = function (memoAryYX, memoAryX, targetAxisY, targetAxisX, addToAxisWay, fromgetIDDataArg) {
    var addToAxisX;
    var addToAxisY;

    memoAryX = memoAryX.concat();

    if (addToAxisWay == "") {
        addToAxisX = targetAxisX;
        addToAxisY = targetAxisY;
    } else if (addToAxisWay == "X") {
        addToAxisX = targetAxisX + 1;
        addToAxisY = targetAxisY;
    } else if (addToAxisWay == "Y") {
        addToAxisX = targetAxisX;
        addToAxisY = targetAxisY + 1;
    }

    //追加するセル（行または列）がない場合
    //if (memoAryYX[addToAxisY] == undefined) {
    //    memoAryYX.splice(addToAxisY, 0, memoAryX);
    //    memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
    //} else if (memoAryYX[addToAxisY][addToAxisX] == undefined) {
    //    if (addToAxisWay == "X") {
    //        //列がない場合は一列追加
    //        memoAryX.splice(addToAxisX, 0, "");
    //        for (var i = 0; i < memoAryYX.length; ++i) {
    //            memoAryYX[i].splice(addToAxisX, 0, "");
    //        }
    //    } else if (addToAxisWay == "Y") {
    //        //行がない場合は一行追加
    //        memoAryYX.splice(addToAxisY, 0, memoAryX);
    //    }
    //    memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
    //} else if (memoAryYX[addToAxisY][addToAxisX] == "") {
    //    memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
    //} else if (memoAryYX[addToAxisY][addToAxisX].ID == "") {
    //    memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
    //} else {

    if ((addToAxisY == 0) && (addToAxisX == 0)) {
        memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
    } else {

        var wflag = 0;
        while (wflag == 0) {

            if (memoAryYX[addToAxisY] == undefined) {
                //セルがない場合①
                //行がない場合は一行追加
                memoAryYX.splice(addToAxisY, 0, memoAryX.concat());
                memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                wflag = 1;
            } else if (memoAryYX[addToAxisY][addToAxisX] == undefined) {
                //セルがない場合②
                //列がない場合は一列追加
                memoAryX.splice(addToAxisX, 0, "");
                for (var i = 0; i < memoAryYX.length; ++i) {
                    memoAryYX[i].splice(addToAxisX, 0, "");
                }
                memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                wflag = 1;
            } else {
                //セルがある場合
                if ((memoAryYX[addToAxisY][addToAxisX].ID != undefined) && (memoAryYX[addToAxisY][addToAxisX].ID != "")) {
                    //登録しようとしているセルにメモが登録済みの場合(IDが登録済みの場合）

                    if (addToAxisWay == "X") {
                        //X軸方向に追加する流れの場合、1行下げた場所に登録する（子メモの概念が不要のため）
                        addToAxisY = addToAxisY + 1;
                    } else if (addToAxisWay == "Y") {
                        //Y軸方向に追加する流れの場合
                        //登録したい場所に登録されているメモが子メモかどうかを確認
                        var temporary = memoAryYX[targetAxisY][targetAxisX].ChildIDTags.trim().split(" ");
                        var komemoflag = 0;
                        for (var k = 0; k < temporary.length; k++) {
                            if (memoAryYX[addToAxisY][addToAxisX].ID == temporary[k]) {
                                //子メモだった場合
                                komemoflag = 1;
                            }
                        }
                        if (komemoflag == 1) {
                            //子メモだった場合は1行下げてループ
                            addToAxisY = addToAxisY + 1;
                        } else {
                            //子メモじゃない場合は1行追加して、メモを新規登録
                            memoAryYX.splice(addToAxisY, 0, memoAryX.concat());
                            memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                            wflag = 1;
                        }
                    }

                } else {
                    //登録しようとしているセルにメモが登録されていない場合（空の場合）

                    if ((targetAxisY == addToAxisY) && ((targetAxisX + 1) == addToAxisX)) {
                        memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                        wflag = 1;
                    } else {
                        for (var ii = 0; ii < addToAxisX; ii++) {
                            if ((memoAryYX[addToAxisY][ii].ID != undefined) && (memoAryYX[addToAxisY][ii].ID != "")) {
                                memoAryYX.splice(addToAxisY, 0, memoAryX.concat());
                                memoAryYX[addToAxisY][addToAxisX] = $.extend(true, {}, fromgetIDDataArg);
                                wflag = 1;
                            }
                        }
                        if (wflag == 0) {
                            alert("Exception Error...No problem. But ID:" + fromgetIDDataArg.ID + " not be displayed.");
                            wflag = 1;
                        }
                    }
                }
            }
        }
    }

    return [memoAryYX, memoAryX];
}




var backButtonRenew = function () {
    window.history.back();
    document.addEventListener("backbutton", navBackPage, false); //android backbuttonを制御
    document.removeEventListener("backbutton", backButtonRenew, false);
}












// backbutton関連
var backButtonRenewPageFour = function () {
    window.history.back();
    document.addEventListener("backbutton", navBackPage, false); //android backbuttonを制御
    document.removeEventListener("backbutton", backButtonRenewPageFour, false);
}

var backButtonRenewPageTwo = function () {
    window.history.back();
    document.addEventListener("backbutton", navBackPage, false); //android backbuttonを制御
    document.removeEventListener("backbutton", backButtonRenewPageTwo, false);
}


//    $('.plusWbsData').on('click', plusWbsDataRenew)
//    $('.plusWbsData').on('click', function () { alert("test2"); })

//var plusWbsData = function () {

//    document.removeEventListener("backbutton", navBackPage, false); //android backbuttonを制御
//    document.addEventListener("backbutton", backButtonRenewPageTwo, false);

//    location.href = "#two";
//}

$('#plusWbsData').on('click', plusWbsData)

$('#navBackPage4').on('click', backButtonRenewPageTwo)


var plusWbsDataRenew = function () { alert("test23"); }

var backButtonRenewTwo = function () {
    window.history.back();
    document.addEventListener("backbutton", navBackPage, false); //android backbuttonを制御
    document.removeEventListener("backbutton", plusWbsDataRenew, false);
}
