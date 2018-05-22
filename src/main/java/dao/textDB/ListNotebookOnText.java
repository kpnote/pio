package dao.textDB;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import Beans.NotebookBean;
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

        	/** �Ώۃf�B���N�g����".csv"�ŏI���t�@�C�����̏����i�[ */
        	files = dir.listFiles(new FileFilter() {
        		@Override
        		public boolean accept(File pathname) {
        			return pathname.getAbsoluteFile().getName().endsWith(".csv");
        		}
        	});

        	/** notebookBean�̃T�C�Y��files.length���w�� */
        	notebookBean = new NotebookBean[files.length];

        	/** �����o�͗p�t�H�[�}�b�g���쐬 */
        	SimpleDateFormat jsJpSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    		/** �擾����files�̏���notebookBean�ɃZ�b�g���� */
        	for(int i = 0; i < files.length; i++) {

        		/** notebookBean�̃C���X�^���X���쐬���� */
        		notebookBean[i] = new NotebookBean();

        		/** �f�B���N�g����path�Ŏg���Ă���Z�p���[�^�����āALinux���i/�j��Windows���i\\�j�����m�F����notebookID(1�A2�A�E�E�E�j���Z�b�g���� */
        		int from = 0;
        		int to = files[i].getPath().lastIndexOf(".");
        		if( files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorLinux")) > 0 ) {
            		from = files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorLinux"));
        		} else {
            		from = files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorWindows")) + 1;
        		}
        		notebookBean[i].setNotebookID(files[i].getPath().substring(from, to));

        		/** Directory���Z�b�g���� */
        		notebookBean[i].setDirectory(notebookCategoryName);

        		/** �ŏI�X�V�������~���b�ŃZ�b�g���� */
        		Date date = new Date(files[i].lastModified());
        		notebookBean[i].setUpdateDate(jsJpSdf.format(date).toString());

        		/** notebook.csv��1�s�ڂ��擾���A�^�C�g���i�Z�Z�̌��j���擾���� */

        		/** notebook.csv���J���i�G���[�̏ꍇ�͏������I������j */
                /** �t�@�C����ǂݍ��� */
                try (	FileInputStream fis = new FileInputStream(files[i].getPath());
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
