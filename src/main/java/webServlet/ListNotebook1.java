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
import util.ReplaceInput;

@WebServlet(name = "ListNotebook1", urlPatterns = { "/ListNotebook1" })
public class ListNotebook1 extends HttpServlet {

	/** Log出力用PrintLoggerを作成 */
	PrintLogger printLogger = new PrintLogger(ListNotebook1.class.getName());

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        /** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** NoteDAOオブジェクトを作成 */
    	NoteDAO noteDAO = new NoteDAO();

    	/** リクエストからファイル名を取得する */
    	String notebookID = req.getParameter("notebookID");
    	java.lang.System.out.println(notebookID);


    	/** リスト化するディレクトリ名を格納 */
    	String notebookCategoryName = notebookID;

    	/** リスト化された情報を格納 */
    	StringBuffer outputText;

    	/** クエストで渡されたファイル名の内容を取得する */
    	// testweb.TextFileReadSample.mainを呼び出して出力を行う
    	//outputText = noteDAO.ListNotebookDAO(notebookCategoryName);

    	/** responseのcontentTypeを指定 */
    	//res.setContentType("text/plain;charset=utf-8");
    	res.setContentType(resource.getString("resContentType"));

    	PrintWriter out = res.getWriter();
        //out.println(new String(outputText));
        out.close();
    }
}