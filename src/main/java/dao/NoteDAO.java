package dao;

import java.io.IOException;

import Beans.NoteReqBean;
import dao.textDB.SelectNoteOnText;
import dao.textDB.InsertNoteOnText;

/* NoteDAO
 * 当初はデータをTextDBに格納するが、別DBへの格納も検討した結果、NoteDAOでラップすることとした。
 * （結局はDBの切り替えは１度切りの可能性が高いため、methodを直接修正してよいと思う）
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
