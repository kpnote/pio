package BusinessLogic;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Beans.NoteReqBean;
import dao.NoteDAO;
import util.PrintLogger;
import util.Utility;

@WebServlet(name = "InsertNote", urlPatterns = { "/InsertNote" })
public class InsertNote extends HttpServlet {

	/** Log出力用PrintLoggerを作成 */
	PrintLogger printLogger = new PrintLogger(InsertNote.class.getName());

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        /** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	StringBuffer outputText;
    	NoteReqBean noteReqBean = new NoteReqBean();

    	//入力チェック用として共通関数を呼び出す
    	Utility utility = new Utility();

    	/** 新規登録するnoteの情報 */
    	noteReqBean.ID 				= utility.replaceInput(req.getParameter("ID"));
    	noteReqBean.ChildIDTags		= utility.replaceInput(req.getParameter("ChildIDTags"));
    	noteReqBean.CreateDate		= utility.replaceInput(req.getParameter("CreateDate"));
    	noteReqBean.UpdateDate		= utility.replaceInput(req.getParameter("UpdateDate"));
    	noteReqBean.DeleteDate		= utility.replaceInput(req.getParameter("DeleteDate"));
    	noteReqBean.PDCAPhase		= utility.replaceInput(req.getParameter("PDCAPhase"));
    	noteReqBean.ContentTitle	= utility.replaceInput(req.getParameter("ContentTitle"));
    	noteReqBean.ContentDesc		= utility.replaceInput(req.getParameter("ContentDesc"));
    	noteReqBean.ContentStatus	= utility.replaceInput(req.getParameter("ContentStatus"));

    	/** 更新対象のnotebookID */
    	noteReqBean.notebookID		= utility.replaceInput(req.getParameter("notebookID"));
    	/** 更新対象のnoteのID(ParentID) */
    	noteReqBean.ParentID		= utility.replaceInput(req.getParameter("ParentID"));

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
		    	);

    	/** NoteDAOオブジェクトを作成 */
    	NoteDAO noteDAO = new NoteDAO();

    	//リクエストで渡されたファイル名の内容を取得する
    	// testweb.TextFileReadSample.mainを呼び出して出力を行う
    	outputText = noteDAO.InsertNoteDAO(noteReqBean);

    	/** responseのcontentTypeを指定 */
    	//res.setContentType("text/plain;charset=utf-8");
    	res.setContentType(resource.getString("resContentType"));

    	PrintWriter out = res.getWriter();
        out.println(new String(outputText));
        out.close();
    }
}