package dao.textDB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import beans.NoteReqBean;
import util.FileMove;
import util.MakeFileLock;
import util.PrintLogger;

public class MakeNotebookOnText {

	/** Log�o�͗pPrintLogger���쐬 */
	PrintLogger printLogger = new PrintLogger(MakeNotebookOnText.class.getName());

	public StringBuffer execute(NoteReqBean noteReqBean) {

		/** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** notebookID.csv�t�@�C���i�[�t�H���_�p�X */
		final String folderPath = resource.getString("notebookDir");

		/** �t�@�C���̍ő�s�� */
		final int maxRecordCount = Integer.parseInt(resource.getString("maxRecordCount"));

    	/** �t�@�C�������b�N����Ă��鎞�Ƀt�@�C���J���܂ő҂ۂ̍ő�҂����� */
		final int maxFileLockWaitTime = Integer.parseInt(resource.getString("maxFileLockWaitTime"));

		/** notebookID�i�[�p�ϐ� ("diary/new"����"diary"�𒊏o)  */
		final String notebookCategoryDir = noteReqBean.notebookID.substring(0, noteReqBean.notebookID.indexOf("/") + 1);

		/** notebookID�ԍ��i�[�t�@�C���� */
		final String notebokIDFile = "ID";

		/** notebookID�̍ŐV�̔ԍ��i�[�t�@�C�� */
        final String idFilePath = folderPath + notebookCategoryDir + notebokIDFile + ".txt";

		/** notebookID.lock�t�@�C����΃p�X */
        final String idLockfilePath = folderPath + notebookCategoryDir + notebokIDFile + ".lock";

		/** notebookID.csv�e���|�����t�@�C����΃p�X */
        final String idTempfilePath = folderPath + notebookCategoryDir + notebokIDFile + ".tmp";

        /** notebook.csv�̏�����荞�� */
        StringBuffer sb = new StringBuffer();

        /** �r��������s���AID.txt�̓ǂݍ��݁A�V�KID�inotebookID.csv��[notebookID]�ɑΉ�����l�j���擾���A
         * notebookID.csv��V�K�쐬����B
         *  */
        try(FileOutputStream out = new FileOutputStream(idLockfilePath);
        		FileChannel channel = out.getChannel()){

        	printLogger.debug("MakeNotebook Lock Start");

        	/** ���b�N�t�@�C�������b�N���� */
        	MakeFileLock makeFileLock = new MakeFileLock();
        	FileLock lock = makeFileLock.doMakeFileLock(channel, maxFileLockWaitTime, MakeNotebookOnText.class.getName());

        	/** notebookID.csv�ɑ΂���ǂݍ��݋y�сA�������݂̏��� */
        	try {
        		String MaxNotebookID = "";

                /** �t�@�C����ǂݍ��� */
                try (	FileInputStream fis = new FileInputStream(idFilePath);
                    	InputStreamReader isr = new InputStreamReader(fis, resource.getString("fileCharSetName"));
                    	BufferedReader br = new BufferedReader(isr);
                		){

                    /** �ŐV��notebookID���擾 */
                	MaxNotebookID = br.readLine();

                } catch (IOException ex) {
                    /** ��O���������� */
                    ex.printStackTrace();
                }

                /** �擾�����ő�notebookID+1��V�K�쐬NotebookID�ɂ��� */
                String NewNotebookID = Integer.toString((Integer.parseInt(MaxNotebookID) + 1));

        		/** �V�K�쐬notebookID.csv�t�@�C����΃p�X */
                final String filePath = folderPath + notebookCategoryDir + NewNotebookID + ".csv";

        		/** �V�K�쐬notebookID.lock�t�@�C����΃p�X */
                final String lockfilePath = folderPath + notebookCategoryDir + NewNotebookID + ".lock";

        		/** �V�K�쐬notebookID.csv�e���|�����t�@�C����΃p�X */
                final String tempfilePath = folderPath + notebookCategoryDir + NewNotebookID + ".tmp";

    			/** notebookID���ő�l�ɒB���Ă��Ȃ��ꍇ�AnotebookID.csv�̐V�K�쐬�������s�� */
    			if (Integer.parseInt(MaxNotebookID) <= maxRecordCount) {

                	/** �e���|�����t�@�C���ɏ������� */
                    try(	FileOutputStream fos = new FileOutputStream(tempfilePath);
                        	OutputStreamWriter osr = new OutputStreamWriter(fos, resource.getString("fileCharSetName"));
                        	BufferedWriter filewriter = new BufferedWriter(osr);
                    		){

            			/** �������擾�inote�̍X�V�����A�V�K�o�^�����Ɏg�p�j */
            			LocalDateTime localDateTime = LocalDateTime.now();

            			/** index.csv�ɏ�������notebook.csv�̃^�C�g���p�Ƃ���ContentTitle�̂P�s�ڂ��擾 */
            			String[] ContentTitleLines = noteReqBean.ContentTitle.split("\t"); /** ContentTitle�� \t ��split */
            			String ContentTitleFirstLine = ContentTitleLines[0];               /** ContentTitle�� \t ��split�������ʂ�[0]��ContentTitle��1�s�ڂƂ��� */

                        /** �e���|�����t�@�C�����X�V����
                         * �t�@�C���ǂݍ��ݏ����Ŏ擾����csv�t�@�C�������e���|�����t�@�C���ɏ�������
                         * �V�K�o�^�f�[�^�̐eID�Ɏw�肳�ꂽ�f�[�^�ɑ΂��ẮA
                         * ChildIDTags�ɐV�K�o�^�f�[�^��ID��ǉ�����B
                         *  */
            			for (int i = 0; i < 2; i++) {

            				/** �V�K�o�^�f�[�^�inoteBean�Ɋi�[���ꂽ���j���e���|�����t�@�C���ɏ������� */
            				if (i == 0) {
                			filewriter.write("\""     + i
                					  		+ "\",\"" + noteReqBean.ChildIDTags
                					  		+ "\",\"" + localDateTime	/** �쐬�������������� */
                					  		+ "\",\"" + noteReqBean.UpdateDate
                					  		+ "\",\"" + noteReqBean.DeleteDate
                					  		+ "\",\"" + noteReqBean.PDCAPhase
                					  		//+ "\",\"" + noteReqBean.ContentTitle.substring(0, noteReqBean.ContentTitle.indexOf("\t"))
                					  		+ "\",\"" + ContentTitleFirstLine
                					  		+ "\",\"" + noteReqBean.ContentDesc
                					  		+ "\",\"" + noteReqBean.ContentStatus
                					  		+ "\"");
        					filewriter.write("\r\n");
            				} else {
                    			filewriter.write("\""     + i
            					  		+ "\",\"" + noteReqBean.ChildIDTags
            					  		+ "\",\"" + localDateTime	/** �쐬�������������� */
            					  		+ "\",\"" + noteReqBean.UpdateDate
            					  		+ "\",\"" + noteReqBean.DeleteDate
            					  		+ "\",\"" + noteReqBean.PDCAPhase
            					  		+ "\",\"" + noteReqBean.ContentTitle
            					  		+ "\",\"" + noteReqBean.ContentDesc
            					  		+ "\",\"" + noteReqBean.ContentStatus
            					  		+ "\"");

            				}
            			}

            			filewriter.close();

            			/** �e���|�����t�@�C����notebook.csv�Ƃ��Ĉړ��i�㏑���j���� */
            			FileMove filemove = new FileMove();
            			filemove.doFileMove(tempfilePath, filePath, maxFileLockWaitTime, MakeNotebookOnText.class.getName());

            		}catch(IOException e){
            			e.printStackTrace();
                    	printLogger.error("MakeNotebook End(NG) notebook.csv�쐬������notebook.csv�̐V�K�쐬�Ɏ��s���܂����BMakeNotebook�����𒆎~���܂��B�Ώۃt�@�C���F" + filePath);
            		}

                	/** �e���|�����t�@�C���ɏ������� */
                    try(	FileOutputStream fos = new FileOutputStream(idTempfilePath);
                        	OutputStreamWriter osr = new OutputStreamWriter(fos, resource.getString("fileCharSetName"));
                        	BufferedWriter filewriter = new BufferedWriter(osr);
                    		){

                    	filewriter.write(NewNotebookID);

            			filewriter.close();

            			/** �e���|�����t�@�C����notebook.csv�Ƃ��Ĉړ��i�㏑���j���� */
            			FileMove filemove = new FileMove();
            			filemove.doFileMove(idTempfilePath, idFilePath, maxFileLockWaitTime, MakeNotebookOnText.class.getName());

            		}catch(IOException e){
            			e.printStackTrace();
                    	printLogger.error("MakeNotebook End(NG) id�t�@�C���X�V������ID.txt�̍X�V�Ɏ��s���܂����BMakeNotebook�����𒆎~���܂��B�Ώۃt�@�C���F" + idFilePath + " �V�K�쐬�t�@�C���F" + filePath);
            		}

    			} else {
    				/** TODO:�ő�s����1000�s�𒴂����ꍇ�̓G���[���b�Z�[�W��\������i�������j */
    			}

        	} finally {
        		/** �r������Ɏg�p�����t�@�C�����b�N�������[�X���� */
    			lock.release();
    			printLogger.debug("MakeNotebook Lock End");
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
        	printLogger.error("MakeNotebook End(NG) FileLock�����Ń��b�N�t�@�C�����擾�ł��Ȃ������\��������܂��BMakeNotebook�����𒆎~���܂��B�Ώۃt�@�C���F" + idFilePath);
		}

        return sb;
    }
}
