package BusinessLogic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Beans.NotebookBean;
import dao.NoteDAO;
import dao.textDB.InsertNoteOnText;
import util.PrintLogger;

@WebServlet(name = "ListNotebook", urlPatterns = { "/ListNotebook" })
public class ListNotebook extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

    	/** Log出力用PrintLoggerを作成 */
    	PrintLogger printLogger = new PrintLogger(InsertNoteOnText.class.getName());

    	/** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** NoteDAOオブジェクトを作成 */
    	NoteDAO noteDAO = new NoteDAO();

    	/** リクエストからファイル名を取得する */
    	String notebookID = req.getParameter("notebookID");
    	printLogger.debug(notebookID);

    	/** リスト化するディレクトリ名を格納 */
    	String notebookCategoryName = notebookID;

    	/** リスト化された情報を格納する変数 */
    	StringBuffer outputText;

    	NotebookBean[] notebookBean;

    	notebookBean = noteDAO.ListNotebookDAO(notebookCategoryName.substring(0, notebookCategoryName.indexOf("/")));

    	/** responseのcontentTypeを指定 */
    	res.setContentType(resource.getString("resContentType"));

    	PrintWriter out = res.getWriter();
    	for(int i = 0; i < notebookBean.length; i++) {
            out.println(notebookBean[i].getNotebookID() + "\n");
            out.println(notebookBean[i].getUpdateDate() + "\n");
    	}
        //out.println(new String(outputText));
        out.close();
    }
}