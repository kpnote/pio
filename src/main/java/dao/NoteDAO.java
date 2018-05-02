package dao;

import java.io.IOException;

import Beans.NoteReqBean;
import dao.textDB.SelectNoteOnText;
import dao.textDB.InsertNoteOnText;

/* NoteDAO
 * �����̓f�[�^��TextDB�Ɋi�[���邪�A��DB�ւ̊i�[�������������ʁANoteDAO�Ń��b�v���邱�ƂƂ����B
 * �i���ǂ�DB�̐؂�ւ��͂P�x�؂�̉\�����������߁Amethod�𒼐ڏC�����Ă悢�Ǝv���j
 * */
public class NoteDAO {

	public StringBuffer SelectNoteDAO(String notebookID) throws IOException{
		StringBuffer text;

		SelectNoteOnText selectNoteOnText = new SelectNoteOnText();
		text = selectNoteOnText.execute(notebookID);

		return text;
	}

	public StringBuffer InsertNoteDAO(NoteReqBean noteReqBean) throws IOException{
		StringBuffer text;

		InsertNoteOnText insertNoteOnText = new InsertNoteOnText();
		text = insertNoteOnText.execute(noteReqBean);

		return text;
	}
}
