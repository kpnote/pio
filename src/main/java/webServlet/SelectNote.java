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

        /** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	StringBuffer outputText;

    	/** ���N�G�X�g����t�@�C�������擾���� */
    	//�i���N�G�X�g http://localhost:8080/pio/selectNote?filename=sample.csv�̏����擾����j
    	String notebookID = req.getParameter("notebookID");
    	java.lang.System.out.println(notebookID);

    	/** NoteDAO�I�u�W�F�N�g���쐬 */
    	NoteDAO noteDAO = new NoteDAO();

    	/** ���N�G�X�g�œn���ꂽ�t�@�C�����̓��e���擾���� */
    	// testweb.TextFileReadSample.main���Ăяo���ďo�͂��s��
    	outputText = noteDAO.SelectNoteDAO(notebookID);

    	/** response��contentType���w�� */
    	//res.setContentType("text/plain;charset=utf-8");
    	res.setContentType(resource.getString("resContentType"));

    	/** response��getWriter()���g�p���A�N���C�A���g�ɕ�����f�[�^��Ԃ�PrintWriter�I�u�W�F�N�g���쐬 */
    	PrintWriter out = res.getWriter();
    	/** StringBuffer�`����outputText��String�`����Cast�ϊ����ăN���C�A���g�ɕԂ�������f�[�^���쐬���Aprintln()�ŃZ�b�g����) */
        out.println(new String(outputText));
        /** �I�u�W�F�N�g����ă��\�[�X���J������ */
        out.close();
    }
}