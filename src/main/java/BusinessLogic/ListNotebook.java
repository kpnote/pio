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

    	/** Log�o�͗pPrintLogger���쐬 */
    	PrintLogger printLogger = new PrintLogger(InsertNoteOnText.class.getName());

    	/** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** NoteDAO�I�u�W�F�N�g���쐬 */
    	NoteDAO noteDAO = new NoteDAO();

    	/** ���N�G�X�g����t�@�C�������擾���� */
    	String notebookID = req.getParameter("notebookID");
    	printLogger.debug(notebookID);

    	/** ���X�g������f�B���N�g�������i�[ */
    	String notebookCategoryName = notebookID;

    	/** ���X�g�����ꂽ�����i�[����ϐ� */
    	StringBuffer outputText;

    	NotebookBean[] notebookBean;

    	notebookBean = noteDAO.ListNotebookDAO(notebookCategoryName.substring(0, notebookCategoryName.indexOf("/")));

    	/** response��contentType���w�� */
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