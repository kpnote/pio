package Beans;

public class NotebookBean {

	public String NotebookID;
	public String NotebookTitle;
	public String Directory;
	public String CreateDate;
	public String UpdateDate;
	public String DeleteDate;
	public NoteBean[] Note;

	public String getNotebookID() {
		return NotebookID;
	}
	public void setNotebookID(String notebookID) {
		NotebookID = notebookID;
	}
	public String getNotebookTitle() {
		return NotebookTitle;
	}
	public void setNotebookTitle(String notebookTitle) {
		NotebookTitle = notebookTitle;
	}
	public String getDirectory() {
		return Directory;
	}
	public void setDirectory(String directory) {
		Directory = directory;
	}
	public String getCreateDate() {
		return CreateDate;
	}
	public void setCreateDate(String createDate) {
		CreateDate = createDate;
	}
	public String getUpdateDate() {
		return UpdateDate;
	}
	public void setUpdateDate(String updateDate) {
		UpdateDate = updateDate;
	}
	public String getDeleteDate() {
		return DeleteDate;
	}
	public void setDeleteDate(String deleteDate) {
		DeleteDate = deleteDate;
	}
	public NoteBean[] getNote() {
		return Note;
	}
	public void setNote(NoteBean[] note) {
		Note = note;
	}
}