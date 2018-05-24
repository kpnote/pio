package beans;

public class NotebookBean {

	public String ID;
	public String ChildIDTags;
	public String CreateDate;
	public String UpdateDate;
	public String DeleteDate;
	public String PDCAPhase;
	public String ContentTitle;
	public String ContentDesc;
	public String ContentStatus;
	public String Directory;
	public NoteBean[] Note;

	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getChildIDTags() {
		return ChildIDTags;
	}
	public void setChildIDTags(String childIDTags) {
		ChildIDTags = childIDTags;
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
	public String getPDCAPhase() {
		return PDCAPhase;
	}
	public void setPDCAPhase(String pDCAPhase) {
		PDCAPhase = pDCAPhase;
	}
	public String getContentTitle() {
		return ContentTitle;
	}
	public void setContentTitle(String contentTitle) {
		ContentTitle = contentTitle;
	}
	public String getContentDesc() {
		return ContentDesc;
	}
	public void setContentDesc(String contentDesc) {
		ContentDesc = contentDesc;
	}
	public String getContentStatus() {
		return ContentStatus;
	}
	public void setContentStatus(String contentStatus) {
		ContentStatus = contentStatus;
	}
	public String getDirectory() {
		return Directory;
	}
	public void setDirectory(String directory) {
		Directory = directory;
	}
	public NoteBean[] getNote() {
		return Note;
	}
	public void setNote(NoteBean[] note) {
		Note = note;
	}
}