package dao.textDB;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ResourceBundle;

import beans.NotebookBean;
import util.FileMove;
import util.MakeFileLock;
import util.PrintLogger;

public class MakeIndexOnText {

	/** Log�o�͗pPrintLogger���쐬 */
	PrintLogger printLogger = new PrintLogger(MakeIndexOnText.class.getName());

	public void execute(String notebookDirectoryName, NotebookBean[] notebookBean) {

		/** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** notebookID.csv�t�@�C���i�[�t�H���_�p�X */
		final String folderPath = resource.getString("notebookDir");

    	/** �t�@�C�������b�N����Ă��鎞�Ƀt�@�C���J���܂ő҂ۂ̍ő�҂����� */
		final int maxFileLockWaitTime = Integer.parseInt(resource.getString("maxFileLockWaitTime"));

		/** index�t�@�C���� */
		final String indexFileName = "index";

		/** index.csv�t�@�C����΃p�X */
        final String filePath = folderPath + notebookDirectoryName + "/" + indexFileName + ".csv";

		/** notebookID.lock�t�@�C����΃p�X */
        final String lockfilePath = folderPath + notebookDirectoryName + "/" + indexFileName + ".lock";

		/** notebookID.csv�e���|�����t�@�C����΃p�X */
        final String tempfilePath = folderPath + notebookDirectoryName + "/" + indexFileName + ".tmp";

        /** �r��������s���AnotebookID.csv�̓ǂݍ��݁A�������ݏ������s��
         *  �t�@�C���ǂݍ��ݎ����烍�b�N���|����̂́AInsert�������d�Ȃ����ꍇ��
         *  �����^�C�~���O�Ńt�@�C���̎Q�Ƃ����Ă��܂��ƁA
         *  noteID���t�@�C���s������擾���Ă�̂ŁA�����s�����擾���A
         *  ����noteID��ǉ����Ă��܂��ׁB
         *  */
        try(FileOutputStream out = new FileOutputStream(lockfilePath);
        		FileChannel channel = out.getChannel()){

        	printLogger.debug("MakeIndex Lock Start");

        	/** ���b�N�t�@�C�������b�N���� */
        	MakeFileLock makeFileLock = new MakeFileLock();
        	FileLock lock = makeFileLock.doMakeFileLock(channel, maxFileLockWaitTime, MakeIndexOnText.class.getName());

        	/** index.csv�ɑ΂���ǂݍ��݋y�сA�������݂̏��� */
        	try {

                	/** �e���|�����t�@�C���ɏ������� */
                    try(	FileOutputStream fos = new FileOutputStream(tempfilePath);
                        	OutputStreamWriter osr = new OutputStreamWriter(fos, resource.getString("fileCharSetName"));
                        	BufferedWriter filewriter = new BufferedWriter(osr);
                    		){

                        /** �e���|�����t�@�C�����X�V����
                         * �t�@�C���ǂݍ��ݏ����Ŏ擾����csv�t�@�C�������e���|�����t�@�C���ɏ�������
                         * �V�K�o�^�f�[�^�̐eID�Ɏw�肳�ꂽ�f�[�^�ɑ΂��ẮA
                         * ChildIDTags�ɐV�K�o�^�f�[�^��ID��ǉ�����B
                         *  */
            			for (int i = 0; i < notebookBean.length; i++) {
            				if (!notebookBean[i].ID.equals(indexFileName)) {

                				/** �V�K�o�^�f�[�^�inoteBean�Ɋi�[���ꂽ���j���e���|�����t�@�C���ɏ������� */
                    			filewriter.write("\"" + notebookBean[i].ID
            					  		+ "\",\"" + notebookBean[i].ChildIDTags
            					  		+ "\",\"" + notebookBean[i].CreateDate
            					  		+ "\",\"" + notebookBean[i].UpdateDate
            					  		+ "\",\"" + notebookBean[i].DeleteDate
            					  		+ "\",\"" + notebookBean[i].PDCAPhase
            					  		+ "\",\"" + notebookBean[i].ContentTitle
            					  		+ "\",\"" + notebookBean[i].ContentDesc
            					  		+ "\",\"" + notebookBean[i].ContentStatus
            					  		+ "\"");
                				if(i < notebookBean.length - 1) {
                        			filewriter.write("\r\n");
                				}
            				}
                        }

            			filewriter.close();

            			/** �e���|�����t�@�C����notebook.csv�Ƃ��Ĉړ��i�㏑���j���� */
            			FileMove filemove = new FileMove();
            			filemove.doFileMove(tempfilePath, filePath, maxFileLockWaitTime, MakeIndexOnText.class.getName());

            		}catch(IOException e){
            			e.printStackTrace();
                    	printLogger.error("MakeIndex End(NG) MakeIndex������index.csv�̍X�V�Ɏ��s���܂����BMakeIndex�����𒆎~���܂��BMakeIndex�Ώۃt�@�C���F" + filePath);
            		}

        	} finally {
        		/** �r������Ɏg�p�����t�@�C�����b�N�������[�X���� */
    			lock.release();
    			printLogger.debug("MakeIndex Lock End");
        	}
        } catch (FileNotFoundException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
        	printLogger.error("MakeIndex End(NG) FileLock�����Ń��b�N�t�@�C�����擾�ł��Ȃ������\��������܂��BMakeIndex�����𒆎~���܂��BMakeIndex�Ώۃt�@�C���F" + filePath);
		}
    }
}
