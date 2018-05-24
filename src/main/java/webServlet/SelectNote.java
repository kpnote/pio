package BusinessLogic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.NoteDAO;

@WebServlet(name = "SelectNote", urlPatterns = { "/SelectNote" })
public class SelectNote extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        /** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	StringBuffer outputText;

    	/** リクエストからファイル名を取得する */
    	//（リクエスト http://localhost:8080/pio/selectNote?filename=sample.csvの情報を取得する）
    	String notebookID = req.getParameter("notebookID");
    	java.lang.System.out.println(notebookID);

    	/** NoteDAOオブジェクトを作成 */
    	NoteDAO noteDAO = new NoteDAO();

    	/** リクエストで渡されたファイル名の内容を取得する */
    	// testweb.TextFileReadSample.mainを呼び出して出力を行う
    	outputText = noteDAO.SelectNoteDAO(notebookID);

    	/** responseのcontentTypeを指定 */
    	//res.setContentType("text/plain;charset=utf-8");
    	res.setContentType(resource.getString("resContentType"));

    	/** responseのgetWriter()を使用し、クライアントに文字列データを返すPrintWriterオブジェクトを作成 */
    	PrintWriter out = res.getWriter();
    	/** StringBuffer形式のoutputTextをString形式にCast変換してクライアントに返す文字列データを作成し、println()でセットする) */
        out.println(new String(outputText));
        /** オブジェクトを閉じてリソースを開放する */
        out.close();
    }
}