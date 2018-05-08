package dao.textDB;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ResourceBundle;

import Beans.NoteReqBean;
import util.PrintLogger;

public class InsertNoteOnText {

	/** Log�o�͗pPrintLogger���쐬 */
	PrintLogger printLogger = new PrintLogger(SelectNoteOnText.class);

    /** �t�@�C���������ݏ����Ŕr��������s���ׁAsynchronized���\�b�h�Ƃ��� */
	synchronized public StringBuffer execute(NoteReqBean noteReqBean) {
    	System.out.println("Insert Start:" + System.currentTimeMillis());

        /** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** notebookID.csv�t�@�C���i�[�t�H���_�p�X */
		//final String folderPath = "C:\\Users\\home401\\Source\\Repos\\PDCALinkWeb40\\PDCALink\\www\\";
		final String folderPath = resource.getString("notebookDir");

		/** notebookID�i�[�p�ϐ� */
		final String notebookID = noteReqBean.notebookID;

		/** notebookID.csv�t�@�C����΃p�X */
        final String filePath = folderPath + notebookID + ".csv";

		/** notebookID.lock�t�@�C����΃p�X */
        final String lockfilePath = folderPath + notebookID + ".lock";

        /** notebook.csv�̏�����荞�� */
        StringBuffer sb = new StringBuffer();

        /** �r��������s���AnotebookID.csv�̓ǂݍ��݁A�������ݏ������s�� */
        try(FileOutputStream out = new FileOutputStream(lockfilePath);
        		FileChannel channel = out.getChannel()){

        	printLogger.debug("Insert Lock Start:" + System.currentTimeMillis());

        	/** ���b�N���擾 */
        	FileLock lock = channel.lock();

        	/** notebookID.csv�ɑ΂���ǂݍ��݋y�сA�������݂̏��� */
        	try {

                /** �t�@�C����ǂݍ��� */
                try {
                    FileReader fr = new FileReader(filePath);
                    BufferedReader br = new BufferedReader(fr);

                    /** �ǂݍ��񂾃t�@�C�����P�s����ʏo�͂��� */
                    String line;

//                    /** notebookID.csv�̍s���i1�s�ځ`�j���i�[���� */
//                    int lineCount = 0;

                    while ((line = br.readLine()) != null) {
//                        System.out.println(++lineCount + "�s�ځF" + line);
                        sb.append(line);
                        sb.append("\r\n");
                    }

                    /** �I������ */
                    br.close();
                    fr.close();
                } catch (IOException ex) {
                    /** ��O���������� */
                    ex.printStackTrace();
                }

            	/** �t�@�C������������ */
                try{
        			File file = new File(filePath);
        			FileWriter filewriter = new FileWriter(file, false);

        			String sbLine = sb.toString();
        			String[] sbLines = sbLine.split("\r\n", 0);

        			/** �V�K�o�^����note��ID���i�[����(�������R�[�h����ID�Ƃ��Ċi�[����(0�Ԗڂɂ�notebookID.csv�̃^�C�g�������i�[)) */
        			int newLineNum = sbLines.length;

                    /** csv�t�@�C�����X�V����
                     * �t�@�C���ǂݍ��ݏ����Ŏ擾����csv�t�@�C������csv�t�@�C���ɏ�������
                     * �V�K�o�^�f�[�^�̐eID�Ɏw�肳�ꂽ�f�[�^�ɑ΂��ẮA
                     * ChildIDTags�ɐV�K�o�^�f�[�^��ID��ǉ�����B
                     *  */
        			for (int i = 0; i < sbLines.length; i++) {
//                        System.out.println((i+1) + "�s�ځF" + sbLines[i] + "\r\n");

            			/** �V�K�o�^�f�[�^�̐eID�Ɏw�肳�ꂽ�f�[�^���ǂ����i���X�V����f�[�^�ł��邱�Ɓj�𔻕ʂ��� */
        				if(!noteReqBean.ParentID.equals("") //�eID���󗓂ł͂Ȃ��ꍇ
            					&& (i == Integer.parseInt(noteReqBean.ParentID))) { //�eID�ƈ�v����f�[�^�̏ꍇ
        					/** �X�V�Ώۂ̃f�[�^�̏ꍇ */

            				/** ��������f���~�^�ŕ������A�z��Ɋi�[
            				 * split�ɂ��ẮA�������ꂽ��ōŌ�̍��ڂ��󔒂̏ꍇ�ł��z��Ɋi�[�����悤�ɁA
            				 * ��������-1��ݒ�
            				 *  */
            				String[] sbLineTmp = sbLines[i].substring(1, sbLines[i].length() -1).split("\",\"", -1);

            				/** �V�K�o�^�f�[�^�̐eID�Ƃ��Ďw�肳�ꂽ�f�[�^��ChildIDTags�̓��ɐV�K�o�^�f�[�^��ID��ݒ� */
            				sbLineTmp[1] = sbLineTmp[1] + " " + newLineNum;

                			/** csv�̓��e���t�@�C���ɏ������� */
            				filewriter.write("\""     + sbLineTmp[0]
                					+ "\",\"" + sbLineTmp[1]
                					+ "\",\"" + sbLineTmp[2]
                					+ "\",\"" + sbLineTmp[3]
                					+ "\",\"" + sbLineTmp[4]
                					+ "\",\"" + sbLineTmp[5]
                					+ "\",\"" + sbLineTmp[6]
                        			+ "\",\"" + sbLineTmp[7]
                                	+ "\",\"" + sbLineTmp[8]
        					  		+ "\"\r\n");
            			} else {
        					/** �X�V�Ώۂ̃f�[�^�ł͂Ȃ��ꍇ */
                			/** csv�̓��e���t�@�C���ɏ�������*/
                			filewriter.write(sbLines[i] + "\r\n");
            			}
                    }

                    /** �V�K�o�^�f�[�^�inoteBean�Ɋi�[���ꂽ���j��notebookID.csv�ɏ������� */
        			//filewriter.write("\""     + noteBean.ID
        			filewriter.write("\""     + newLineNum
        					  		+ "\",\"" + noteReqBean.ChildIDTags
        					  		+ "\",\"" + noteReqBean.CreateDate
        					  		+ "\",\"" + noteReqBean.UpdateDate
        					  		+ "\",\"" + noteReqBean.DeleteDate
        					  		+ "\",\"" + noteReqBean.PDCAPhase
        					  		+ "\",\"" + noteReqBean.ContentTitle
        					  		+ "\",\"" + noteReqBean.ContentDesc
        					  		+ "\",\"" + noteReqBean.ContentStatus
        					  		+ "\"\r\n");

        			filewriter.close();

        		}catch(IOException e){
        			System.out.println(e);
        		}
        	} finally {
        		/** �r������Ɏg�p�������b�N�t�@�C���������[�X���� */
    			lock.release();
    			printLogger.debug("Insert Lock End:" + System.currentTimeMillis());
        	}
        } catch (FileNotFoundException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		}

        return sb;
    }
}
