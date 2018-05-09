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

	/** Log�o�͗pPrintLogger���쐬 */
	PrintLogger printLogger = new PrintLogger(InsertNote.class.getName());

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        /** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	StringBuffer outputText;
    	NoteReqBean noteReqBean = new NoteReqBean();

    	//���̓`�F�b�N�p�Ƃ��ċ��ʊ֐����Ăяo��
    	Utility utility = new Utility();

    	/** �V�K�o�^����note�̏�� */
    	noteReqBean.ID 				= utility.replaceInput(req.getParameter("ID"));
    	noteReqBean.ChildIDTags		= utility.replaceInput(req.getParameter("ChildIDTags"));
    	noteReqBean.CreateDate		= utility.replaceInput(req.getParameter("CreateDate"));
    	noteReqBean.UpdateDate		= utility.replaceInput(req.getParameter("UpdateDate"));
    	noteReqBean.DeleteDate		= utility.replaceInput(req.getParameter("DeleteDate"));
    	noteReqBean.PDCAPhase		= utility.replaceInput(req.getParameter("PDCAPhase"));
    	noteReqBean.ContentTitle	= utility.replaceInput(req.getParameter("ContentTitle"));
    	noteReqBean.ContentDesc		= utility.replaceInput(req.getParameter("ContentDesc"));
    	noteReqBean.ContentStatus	= utility.replaceInput(req.getParameter("ContentStatus"));

    	/** �X�V�Ώۂ�notebookID */
    	noteReqBean.notebookID		= utility.replaceInput(req.getParameter("notebookID"));
    	/** �X�V�Ώۂ�note��ID(ParentID) */
    	noteReqBean.ParentID		= utility.replaceInput(req.getParameter("ParentID"));

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

    	//���N�G�X�g�œn���ꂽ�t�@�C�����̓��e���擾����
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