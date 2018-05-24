package dao.textDB;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

import beans.NotebookBean;
import util.PrintLogger;

public class ListNotebookOnText {

    public NotebookBean[] execute(String notebookCategoryName) {

    	/** Log�o�͗pPrintLogger���쐬 */
    	PrintLogger printLogger = new PrintLogger(ListNotebookOnText.class.getName());

    	/** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

		/** notebookID.csv�t�@�C���i�[�t�H���_�p�X */
		final String folderPath = resource.getString("notebookDir");

    	/** list�Ώۂ̃f�B���N�g���i�[�p�ϐ����쐬 */
		String targetFolderPath = null;

		/** �Ώۃf�B���N�g���ɂ���t�B���^�����ƈ�v����t�@�C�������i�[ */
		File[] files = null;

		/** �t�@�C�������i�[����ϐ����쐬 */
		NotebookBean[] notebookBean = null;

    	/** �����Ƃ��ēn���ꂽnotebookCategoryName�ɒl���Z�b�g����Ă���ꍇ�A
    	 * targetFolderPath��notebookCategoryName��append����B */
		if(notebookCategoryName.equals(null)) {
    		targetFolderPath = folderPath;
    	} else {
    		targetFolderPath = folderPath + notebookCategoryName;
    	}

        StringBuffer sb = new StringBuffer();

        try {
        	/** list�����J�n���Ԃ��o�� */
        	printLogger.debug("Select Start:" + System.currentTimeMillis());

        	/** �Ώۃf�B���N�g����File�I�u�W�F�N�g���쐬 */
        	File dir = new File(targetFolderPath);

        	/** �Ώۃf�B���N�g���Ńt�@�C������".csv"�ŏI���t�@�C���̈ꗗ���擾���Ċi�[ */
        	files = dir.listFiles(new FileFilter() {
        		@Override
        		public boolean accept(File pathname) {
        			return pathname.getAbsoluteFile().getName().endsWith(".csv");
        		}
        	});

        	if (notebookCategoryName.indexOf("__") < 0) {
        		/** �t�@�C���ꗗ�̏��ɑ΂��āA�X�V�������č~���ɕ��ёւ� */
            	Arrays.sort(files, new Comparator<File>() {
            		public int compare(File f1, File f2) {
            			if ((f1.lastModified() - f2.lastModified()) < 0) {
            				return 1;
            			} else if ((f1.lastModified() - f2.lastModified()) > 0) {
            				return -1;
            			} else {
            				return 0;
            			}
            		}
            	} );
        	}

        	/** notebookBean�̃T�C�Y��files.length���w�� */
        	notebookBean = new NotebookBean[files.length];

        	/** �����o�͗p�t�H�[�}�b�g���쐬 */
        	SimpleDateFormat jsJpSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    		/** csv�ꗗ�ɑ΂��Ĉꃌ�R�[�h���Q�Ƃ��AnotebookBean��notebookID.csv�̏����Z�b�g���� */
        	for(int i = 0; i < files.length; i++) {

        		/** notebookBean�̃C���X�^���X���쐬���āAnotebookBean��i�ԖڂɊi�[ */
        		notebookBean[i] = new NotebookBean();

        		/** Directory���Z�b�g���� */
        		notebookBean[i].setDirectory(notebookCategoryName);

        		/** NotebookID���Z�b�g���� */
        		int from = 0;
        		int to = files[i].getPath().lastIndexOf(".");
        		/** �f�B���N�g����path�Ŏg���Ă���Z�p���[�^�����āALinux���i/�j��Windows���i\\�j�����m�F���� */
        		if( files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorLinux")) > 0 ) {
            		from = files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorLinux"));
        		} else {
            		from = files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorWindows")) + 1;
        		}
        		/** getPath�Ńf�B���N�g���t���p�X�̍Ō�̃Z�p���[�^������Ɓu.�v�̊Ԃɂ��镶������擾���āANotebookID�Ƃ��Ċi�[ */
        		notebookBean[i].setID(files[i].getPath().substring(from, to));

        		/** UpdateDate���Z�b�g���� �i�ŏI�X�V�������~���b�ŃZ�b�g����j */
        		Date date = new Date(files[i].lastModified());
        		notebookBean[i].setUpdateDate(jsJpSdf.format(date).toString());

        		/** notebook.csv��1�s�ڂ��擾���A���ڂ��擾���ăZ�b�g���� */
                try (	/** �t�@�C����ǂݍ��� */
                		FileInputStream fis = new FileInputStream(files[i].getPath()); //
                    	InputStreamReader isr = new InputStreamReader(fis, resource.getString("fileCharSetName"));
                    	BufferedReader br = new BufferedReader(isr);
                	){

                    /** �ǂݍ��񂾃t�@�C������P�s�ǂݍ��� */
    				String line = br.readLine();

    				/** ��������f���~�^�ŕ������A�z��Ɋi�[
    				 * split�ɂ��ẮA�������ꂽ��ōŌ�̍��ڂ��󔒂̏ꍇ�ł��z��Ɋi�[�����悤�ɁA
    				 * ��������-1��ݒ�
    				 *  */
    				String[] sbLineTmp = line.substring(1, line.length() -1).split("\",\"", -1);

            		/** ChildIDTags���Z�b�g����*/
            		notebookBean[i].setChildIDTags(sbLineTmp[1]);

            		/** CreateDate���Z�b�g����*/
            		notebookBean[i].setCreateDate(sbLineTmp[2]);

            		///** UpdateDate���Z�b�g����*/
            		//notebookBean[i].setUpdateDate(sbLineTmp[3]);

            		/** DeleteDate���Z�b�g����*/
            		notebookBean[i].setDeleteDate(sbLineTmp[4]);

            		/** PDCAPhase���Z�b�g����*/
            		notebookBean[i].setPDCAPhase(sbLineTmp[5]);

            		/** NotebookTitle�i�Z�Z�̌��j���Z�b�g����*/
            		notebookBean[i].setContentTitle(sbLineTmp[6]);

            		/** ContentDesc���Z�b�g����*/
            		notebookBean[i].setContentDesc(sbLineTmp[7]);

            		/** ContentStatus���Z�b�g����*/
            		notebookBean[i].setContentStatus(sbLineTmp[8]);

                } catch (IOException ex) {
                    /** ��O���������� */
                    ex.printStackTrace();
                }
        	}

        	/** select�����J�n���Ԃ��o�� */
        	printLogger.debug("Select End:" + System.currentTimeMillis());

        } catch (Exception ex) {
            //��O����������
            ex.printStackTrace();
        }

        return notebookBean;
    }
}
