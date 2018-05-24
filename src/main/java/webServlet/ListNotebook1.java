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

	/** Log�o�͗pPrintLogger���쐬 */
	PrintLogger printLogger = new PrintLogger(ListNotebook1.class.getName());

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        /** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** NoteDAO�I�u�W�F�N�g���쐬 */
    	NoteDAO noteDAO = new NoteDAO();

    	/** ���N�G�X�g����t�@�C�������擾���� */
    	String notebookID = req.getParameter("notebookID");
    	java.lang.System.out.println(notebookID);


    	/** ���X�g������f�B���N�g�������i�[ */
    	String notebookCategoryName = notebookID;

    	/** ���X�g�����ꂽ�����i�[ */
    	StringBuffer outputText;

    	/** �N�G�X�g�œn���ꂽ�t�@�C�����̓��e���擾���� */
    	// testweb.TextFileReadSample.main���Ăяo���ďo�͂��s��
    	//outputText = noteDAO.ListNotebookDAO(notebookCategoryName);

    	/** response��contentType���w�� */
    	//res.setContentType("text/plain;charset=utf-8");
    	res.setContentType(resource.getString("resContentType"));

    	PrintWriter out = res.getWriter();
        //out.println(new String(outputText));
        out.close();
    }
}