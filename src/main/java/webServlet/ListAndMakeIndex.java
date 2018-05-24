package webServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.NotebookBean;
import dao.NoteDAO;
import dao.textDB.InsertNoteOnText;
import dao.textDB.MakeIndexOnText;
import util.PrintLogger;

@WebServlet(name = "ListAndMakeIndex", urlPatterns = { "/ListAndMakeIndex" })
public class ListAndMakeIndex extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

    	/** Log�o�͗pPrintLogger���쐬 */
    	PrintLogger printLogger = new PrintLogger(InsertNoteOnText.class.getName());

    	/** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** NoteDAO�I�u�W�F�N�g���쐬 */
    	NoteDAO noteDAO = new NoteDAO();

    	/** ���N�G�X�g����notebookID���擾���� */
    	String notebookID = req.getParameter("notebookID");
    	printLogger.debug(notebookID);

    	/** ���X�g������notebook�J�e�S�������i�[ */
    	String notebookCategoryName = notebookID;

    	/** ���X�g�����ꂽnotebook�����i�[����Bean */
    	NotebookBean[] notebookBean;

    	/** ���X�g�����ꂽnotebook�����擾���Abean�Ɋi�[ */
    	notebookBean = noteDAO.ListNotebookDAO(notebookCategoryName.substring(0, notebookCategoryName.indexOf("/")));

    	/** notebook�J�e�S�����̃f�B���N�g���z���ɁA���X�g�����ꂽnotebook�����i�[�����C���f�b�N�X�t�@�C���i"index.csv"�j���쐬 */
    	noteDAO.MakeIndexDAO(notebookCategoryName.substring(0, notebookCategoryName.indexOf("/")), notebookBean);

    	/** response��contentType���w�� */
    	res.setContentType(resource.getString("resContentType"));

    	/** index����������web�o�� */
    	PrintWriter out = res.getWriter();
    	for(int i = 0; i < notebookBean.length; i++) {
            out.println(notebookBean[i].getID() + " ");
            out.println(notebookBean[i].getUpdateDate() + " ");
            out.println(notebookBean[i].getContentTitle() + " ");
            out.println(notebookBean[i].getPDCAPhase() + "\n");
    	}
        //out.println(new String(outputText));
        out.close();
    }
}