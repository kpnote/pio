$(function () {

    $('.threadList_on_page_one').on('click', 'li', function () {
    
        //notebook一覧でクリックされたnotebookIDを取得
        var selectedNotebookID = $(this).data('entry_notebook_id');

        //notebook一覧でクリックされた項目のdata-role項目を取得
        var selectedRoleVal = $(this).data('role');
        
        //notebook一覧でクリックされた項目のdata-role値に従い処理を行う
        if (selectedRoleVal == "list-divider") {
            //list-dividerの場合は何もしない
        } else {
            var hrefString = "../client/index.html" + "?notebookID=" + selectedNotebookID;

            location.href = hrefString;
        }

    })

});
