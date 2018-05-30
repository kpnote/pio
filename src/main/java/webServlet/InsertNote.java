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
import beans.NotebookBean;
import dao.NoteDAO;
import dao.textDB.InsertNoteOnText;
import util.GetURLResponse;
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

    	/** reCAPTCHA�̃g�[�N�� */
    	noteReqBean.RecaptchaResponse = req.getParameter("RecaptchaResponse");

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
		    	+ "," + noteReqBean.RecaptchaResponse
		    	);

    	/** reCAPTCHA�F�،��ʎ擾�p�I�u�W�F�N�g���쐬 */
    	GetURLResponse getUrlResponse = new GetURLResponse();

    	/** reCAPTCHA�F�،��ʂ𔻒� */
    	if(getUrlResponse.doGetURLResponse(noteReqBean.RecaptchaResponse) == getUrlResponse.success) {
        	/** reCAPTCHA�F�،��ʂ����������ꍇ */

    		/** NoteDAO�I�u�W�F�N�g���쐬 */
        	NoteDAO noteDAO = new NoteDAO();

        	/** ���N�G�X�g�œn���ꂽ�t�@�C�����̓��e���擾���� */
        	// testweb.TextFileReadSample.main���Ăяo���ďo�͂��s��
        	outputText = noteDAO.InsertNoteDAO(noteReqBean);

        	/**
        	 * �C���f�b�N�X���쐬 �iListAndMakeIndex.java���甲�����Ĉꕔ�ύX�j
        	 * */

        	/** Log�o�͗pPrintLogger���쐬 */
        	PrintLogger printLogger = new PrintLogger(InsertNoteOnText.class.getName());

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
        	//res.setContentType("text/plain;charset=utf-8");
        	res.setContentType(resource.getString("resContentType"));

        	/** �������ʂƂ��āA�֋X�I�ł͂��邪�A��ʂɕ\�� */
        	PrintWriter out = res.getWriter();
            out.println(new String(outputText));
            out.close();

    	} else {
        	/** reCAPTCHA�F�؂����s�����ꍇ */

    		/** response��contentType���w�� */
        	res.setContentType(resource.getString("resContentType"));

        	/** reCAPTCHA�F�؂����s����������ʂɕ\�� */
        	PrintWriter out = res.getWriter();
            out.println(new String("reCAPTCHA verification Error"));
            out.close();
    	}

    }
}