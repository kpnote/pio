package webServlet;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.NoteReqBean;
import beans.NotebookBean;
import dao.NoteDAO;
import dao.textDB.InsertNoteOnText;
import util.GetURLResponse;
import util.PrintLogger;
import util.ReplaceInput;

@WebServlet(name = "InsertNote", urlPatterns = { "/InsertNote" })
public class InsertNote extends HttpServlet {

	/** Log出力用PrintLoggerを作成 */
	PrintLogger printLogger = new PrintLogger(InsertNote.class.getName());

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        /** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** リクエストで渡されたファイル名の内容を格納する変数を作成 */
    	StringBuffer outputText;

    	/** 新規登録するnoteの情報を格納するbeanを作成 */
    	NoteReqBean noteReqBean = new NoteReqBean();

    	/** 入力チェック用として共通関数を呼び出す */
    	ReplaceInput replaceInput = new ReplaceInput();

    	/** 新規登録するnoteの情報 */
    	noteReqBean.ID 				= replaceInput.doReplaceInput(req.getParameter("ID"));
    	noteReqBean.ChildIDTags		= replaceInput.doReplaceInput(req.getParameter("ChildIDTags"));
    	noteReqBean.CreateDate		= replaceInput.doReplaceInput(req.getParameter("CreateDate"));
    	noteReqBean.UpdateDate		= replaceInput.doReplaceInput(req.getParameter("UpdateDate"));
    	noteReqBean.DeleteDate		= replaceInput.doReplaceInput(req.getParameter("DeleteDate"));
    	noteReqBean.PDCAPhase		= replaceInput.doReplaceInput(req.getParameter("PDCAPhase"));
    	noteReqBean.ContentTitle	= replaceInput.doReplaceInput(req.getParameter("ContentTitle"));
    	noteReqBean.ContentDesc		= replaceInput.doReplaceInput(req.getParameter("ContentDesc"));
    	noteReqBean.ContentStatus	= replaceInput.doReplaceInput(req.getParameter("ContentStatus"));

    	/** 更新対象のnotebookID */
    	noteReqBean.notebookID		= replaceInput.doReplaceInput(req.getParameter("notebookID"));
    	/** 更新対象のnoteのID(ParentID) */
    	noteReqBean.ParentID		= replaceInput.doReplaceInput(req.getParameter("ParentID"));

    	/** reCAPTCHAのトークン */
    	noteReqBean.RecaptchaResponse = req.getParameter("RecaptchaResponse");

    	/** request情報をログに出力 */
    	printLogger.info(req.getRemoteAddr()	/** IPアドレス */
				+ "," + noteReqBean.ID
				+ "," + noteReqBean.ChildIDTags
		    	+ "," + noteReqBean.CreateDate
		    	+ "," + noteReqBean.UpdateDate
		    	+ "," + noteReqBean.DeleteDate
		    	+ "," + noteReqBean.PDCAPhase
		    	+ "," + noteReqBean.ContentTitle
		    	+ "," + noteReqBean.ContentDesc
		    	+ "," + noteReqBean.ContentStatus
		    	+ "," + noteReqBean.notebookID
		    	+ "," + noteReqBean.ParentID
		    	+ "," + noteReqBean.RecaptchaResponse
		    	);

    	/** reCAPTCHA認証結果取得用オブジェクトを作成 */
    	GetURLResponse getUrlResponse = new GetURLResponse();

    	/** reCAPTCHA認証結果を判定 */
    	if(getUrlResponse.doGetURLResponse(noteReqBean.RecaptchaResponse) == getUrlResponse.success) {
        	/** reCAPTCHA認証結果が成功した場合 */

    		/** NoteDAOオブジェクトを作成 */
        	NoteDAO noteDAO = new NoteDAO();

        	/** リクエストで渡されたファイル名の内容を取得する */
        	// testweb.TextFileReadSample.mainを呼び出して出力を行う
        	outputText = noteDAO.InsertNoteDAO(noteReqBean);

        	/**
        	 * インデックスを作成 （ListAndMakeIndex.javaから抜粋して一部変更）
        	 * */

        	/** Log出力用PrintLoggerを作成 */
        	PrintLogger printLogger = new PrintLogger(InsertNoteOnText.class.getName());

         	/** リクエストからnotebookIDを取得する */
        	String notebookID = req.getParameter("notebookID");
        	printLogger.debug(notebookID);

        	/** リスト化するnotebookカテゴリ名を格納 */
        	String notebookCategoryName = notebookID;

        	/** リスト化されたnotebook情報を格納するBean */
        	NotebookBean[] notebookBean;

        	/** リスト化されたnotebook情報を取得し、beanに格納 */
        	notebookBean = noteDAO.ListNotebookDAO(notebookCategoryName.substring(0, notebookCategoryName.indexOf("/")));

        	/** notebookカテゴリ名のディレクトリ配下に、リスト化されたnotebook情報を格納したインデックスファイル（"index.csv"）を作成 */
        	noteDAO.MakeIndexDAO(notebookCategoryName.substring(0, notebookCategoryName.indexOf("/")), notebookBean);


        	/** responseのcontentTypeを指定 */
        	//res.setContentType("text/plain;charset=utf-8");
        	res.setContentType(resource.getString("resContentType"));

        	/** 処理結果として、便宜的ではあるが、画面に表示 */
        	PrintWriter out = res.getWriter();
            out.println(new String(outputText));
            out.close();

    	} else {
        	/** reCAPTCHA認証が失敗した場合 */

    		/** responseのcontentTypeを指定 */
        	res.setContentType(resource.getString("resContentType"));

        	/** reCAPTCHA認証が失敗した事を画面に表示 */
        	PrintWriter out = res.getWriter();
            out.println(new String("reCAPTCHA verification Error"));
            out.close();
    	}

    }
}