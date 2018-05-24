package dao.textDB;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import beans.NoteReqBean;
import util.FileMove;
import util.MakeFileLock;
import util.PrintLogger;

public class InsertNoteOnText2 {

	/** Log�o�͗pPrintLogger���쐬 */
	PrintLogger printLogger = new PrintLogger(InsertNoteOnText2.class.getName());

	public StringBuffer execute(NoteReqBean noteReqBean) {

		/** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** notebookID.csv�t�@�C���i�[�t�H���_�p�X */
		//final String folderPath = "C:\\Users\\home401\\Source\\Repos\\PDCALinkWeb40\\PDCALink\\www\\";
		final String folderPath = resource.getString("notebookDir");

		/** �t�@�C���̍ő�s�� */
		final int maxRecordCount = Integer.parseInt(resource.getString("maxRecordCount"));

    	/** �t�@�C�������b�N����Ă��鎞�Ƀt�@�C���J���܂ő҂ۂ̍ő�҂����� */
		final int maxFileLockWaitTime = Integer.parseInt(resource.getString("maxFileLockWaitTime"));

		/** notebookID�i�[�p�ϐ� */
		final String notebookID = noteReqBean.notebookID;

		/** notebookID.csv�t�@�C����΃p�X */
        final String filePath = folderPath + notebookID + ".csv";

		/** notebookID.lock�t�@�C����΃p�X */
        final String lockfilePath = folderPath + notebookID + ".lock";

		/** notebookID.csv�e���|�����t�@�C����΃p�X */
        final String tempfilePath = folderPath + notebookID + ".tmp";

        /** notebook.csv�̏�����荞�� */
        StringBuffer sb = new StringBuffer();

        /** �r��������s���AnotebookID.csv�̓ǂݍ��݁A�������ݏ������s��
         *  �t�@�C���ǂݍ��ݎ����烍�b�N���|����̂́AInsert�������d�Ȃ����ꍇ��
         *  �����^�C�~���O�Ńt�@�C���̎Q�Ƃ����Ă��܂��ƁA
         *  noteID���t�@�C���s������擾���Ă�̂ŁA�����s�����擾���A
         *  ����noteID��ǉ����Ă��܂��ׁB
         *  */
        try(FileOutputStream out = new FileOutputStream(lockfilePath);
        		FileChannel channel = out.getChannel()){

        	printLogger.debug("Insert Lock Start");

        	/** ���b�N�t�@�C�������b�N���� */
        	MakeFileLock makeFileLock = new MakeFileLock();
        	FileLock lock = makeFileLock.doMakeFileLock(channel, maxFileLockWaitTime, InsertNoteOnText2.class.getName());

        	/** notebookID.csv�ɑ΂���ǂݍ��݋y�сA�������݂̏��� */
        	try {

                /** �t�@�C����ǂݍ��� */
                try (	FileInputStream fis = new FileInputStream(filePath);
                    	InputStreamReader isr = new InputStreamReader(fis, resource.getString("fileCharSetName"));
                    	BufferedReader br = new BufferedReader(isr);
                		){

                    //�����ύX
                    //FileReader fr = new FileReader(filePath);
                    //BufferedReader br = new BufferedReader(fr);

                    /** �ǂݍ��񂾃t�@�C�����P�s����ʏo�͂��� */
                    String line;

                    while ((line = br.readLine()) != null) {
//                        System.out.println(++lineCount + "�s�ځF" + line);
                        sb.append(line);
                        sb.append("\r\n");
                    }

                    /** �I������ */
                    //�����ύX�ɂ��s�v
                    //br.close();
                    //fr.close();
                } catch (IOException ex) {
                    /** ��O���������� */
                    ex.printStackTrace();
                }

    			String sbLine = sb.toString();
    			String[] sbLines = sbLine.split("\r\n", 0);

    			/** �t�@�C���s����1000�s�ȉ��̏ꍇ�A�V�K�o�^�����̏����֐i�� */
    			if (sbLines.length <= maxRecordCount) {

                	/** �e���|�����t�@�C���ɏ������� */
                    try(	FileOutputStream fos = new FileOutputStream(tempfilePath);
                        	OutputStreamWriter osr = new OutputStreamWriter(fos, resource.getString("fileCharSetName"));
                        	BufferedWriter filewriter = new BufferedWriter(osr);
                    		){
//            			File tempfile = new File(tempfilePath);
//            			FileWriter filewriter = new FileWriter(tempfile, false);

            			/** �V�K�o�^����note��ID���i�[����(�������R�[�h����ID�Ƃ��Ċi�[����(0�Ԗڂɂ�notebookID.csv�̃^�C�g�������i�[)) */
            			int newLineNum = sbLines.length;

            			/** �������擾�inote�̍X�V�����A�V�K�o�^�����Ɏg�p�j */
            			LocalDateTime localDateTime = LocalDateTime.now();

                        /** �e���|�����t�@�C�����X�V����
                         * �t�@�C���ǂݍ��ݏ����Ŏ擾����csv�t�@�C�������e���|�����t�@�C���ɏ�������
                         * �V�K�o�^�f�[�^�̐eID�Ɏw�肳�ꂽ�f�[�^�ɑ΂��ẮA
                         * ChildIDTags�ɐV�K�o�^�f�[�^��ID��ǉ�����B
                         *  */
            			for (int i = 0; i < sbLines.length; i++) {
//                            System.out.println((i+1) + "�s�ځF" + sbLines[i] + "\r\n");

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
                    					//+ "\",\"" + sbLineTmp[3]
                            			+ "\",\"" + localDateTime	/** �X�V�������������� */
                    					+ "\",\"" + sbLineTmp[4]
                    					+ "\",\"" + sbLineTmp[5]
                    					+ "\",\"" + sbLineTmp[6]
                            			+ "\",\"" + sbLineTmp[7]
                                    	+ "\",\"" + sbLineTmp[8]
            					  		+ "\"\r\n");
                			} else {
            					/** �X�V�Ώۂ̃f�[�^�ł͂Ȃ��ꍇ */
                    			/** csv�̓��e���e���|�����t�@�C���ɏ�������*/
                    			filewriter.write(sbLines[i] + "\r\n");
                			}
                        }

                        /** �V�K�o�^�f�[�^�inoteBean�Ɋi�[���ꂽ���j���e���|�����t�@�C���ɏ������� */
            			//filewriter.write("\""     + noteBean.ID
            			filewriter.write("\""     + newLineNum
            					  		+ "\",\"" + noteReqBean.ChildIDTags
            					  		//+ "\",\"" + noteReqBean.CreateDate
            					  		+ "\",\"" + localDateTime	/** �쐬�������������� */
            					  		+ "\",\"" + noteReqBean.UpdateDate
            					  		+ "\",\"" + noteReqBean.DeleteDate
            					  		+ "\",\"" + noteReqBean.PDCAPhase
            					  		+ "\",\"" + noteReqBean.ContentTitle
            					  		+ "\",\"" + noteReqBean.ContentDesc
            					  		+ "\",\"" + noteReqBean.ContentStatus
            					  		+ "\"");

            			filewriter.close();

            			/** �e���|�����t�@�C����notebook.csv�Ƃ��Ĉړ��i�㏑���j���� */
            			//Files.move(Paths.get(tempfilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            			FileMove filemove = new FileMove();
            			filemove.doFileMove(tempfilePath, filePath, maxFileLockWaitTime, InsertNoteOnText2.class.getName());

            		}catch(IOException e){
            			e.printStackTrace();
                    	printLogger.error("Insert End(NG) Insert������notebook.csv�̍X�V�Ɏ��s���܂����BInsert�����𒆎~���܂��BInsert�Ώۃt�@�C���F" + filePath);
            		}
    			} else {
    				/** TODO:�ő�s����1000�s�𒴂����ꍇ�̓G���[���b�Z�[�W��\������i�������j */
    			}

        	} finally {
        		/** �r������Ɏg�p�����t�@�C�����b�N�������[�X���� */
    			lock.release();
    			printLogger.debug("Insert Lock End");
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
        	printLogger.error("Insert End(NG) FileLock�����Ń��b�N�t�@�C�����擾�ł��Ȃ������\��������܂��BInsert�����𒆎~���܂��BInsert�Ώۃt�@�C���F" + filePath);
		}

        return sb;
    }
}
