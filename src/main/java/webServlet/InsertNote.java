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
import dao.NoteDAO;
import util.PrintLogger;
import util.ReplaceInput;

@WebServlet(name = "InsertNote", urlPatterns = { "/InsertNote" })
public class InsertNote extends HttpServlet {

	/** Log�o�͗pPrintLogger���쐬 */
	PrintLogger printLogger = new PrintLogger(InsertNote.class.getName());

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        /** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** ���N�G�X�g�œn���ꂽ�t�@�C�����̓��e���i�[����ϐ����쐬 */
    	StringBuffer outputText;

    	/** �V�K�o�^����note�̏����i�[����bean���쐬 */
    	NoteReqBean noteReqBean = new NoteReqBean();

    	/** ���̓`�F�b�N�p�Ƃ��ċ��ʊ֐����Ăяo�� */
    	ReplaceInput replaceInput = new ReplaceInput();

    	/** �V�K�o�^����note�̏�� */
    	noteReqBean.ID 				= replaceInput.doReplaceInput(req.getParameter("ID"));
    	noteReqBean.ChildIDTags		= replaceInput.doReplaceInput(req.getParameter("ChildIDTags"));
    	noteReqBean.CreateDate		= replaceInput.doReplaceInput(req.getParameter("CreateDate"));
    	noteReqBean.UpdateDate		= replaceInput.doReplaceInput(req.getParameter("UpdateDate"));
    	noteReqBean.DeleteDate		= replaceInput.doReplaceInput(req.getParameter("DeleteDate"));
    	noteReqBean.PDCAPhase		= replaceInput.doReplaceInput(req.getParameter("PDCAPhase"));
    	noteReqBean.ContentTitle	= replaceInput.doReplaceInput(req.getParameter("ContentTitle"));
    	noteReqBean.ContentDesc		= replaceInput.doReplaceInput(req.getParameter("ContentDesc"));
    	noteReqBean.ContentStatus	= replaceInput.doReplaceInput(req.getParameter("ContentStatus"));

    	/** �X�V�Ώۂ�notebookID */
    	noteReqBean.notebookID		= replaceInput.doReplaceInput(req.getParameter("notebookID"));
    	/** �X�V�Ώۂ�note��ID(ParentID) */
    	noteReqBean.ParentID		= replaceInput.doReplaceInput(req.getParameter("ParentID"));

    	/** request�������O�ɏo�� */
    	printLogger.info(req.getRemoteAddr()	/** IP�A�h���X */
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

    	/** NoteDAO�I�u�W�F�N�g���쐬 */
    	NoteDAO noteDAO = new NoteDAO();

    	/** �N�G�X�g�œn���ꂽ�t�@�C�����̓��e���擾���� */
    	// testweb.TextFileReadSample.main���Ăяo���ďo�͂��s��
    	outputText = noteDAO.InsertNoteDAO(noteReqBean);

    	/** response��contentType���w�� */
    	//res.setContentType("text/plain;charset=utf-8");
    	res.setContentType(resource.getString("resContentType"));

    	PrintWriter out = res.getWriter();
        out.println(new String(outputText));
        out.close();
    }
}