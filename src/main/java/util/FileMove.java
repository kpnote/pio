package util;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileMove {

	public void doFileMove(String tempfilePath, String filePath, int maxFileLockWaitTime, String className) throws Exception {
//		public FileLock doFileMove(FileChannel channel, int maxFileLockWaitTime, String className) throws Exception {

		/** Log�o�͗pPrintLogger���쐬 */
		PrintLogger printLogger = new PrintLogger(FileMove.class.getName());

		boolean result = false;

    	for(int i = 0; i < maxFileLockWaitTime * 10; i++) {
        	try {
				Files.move(Paths.get(tempfilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        		/** �t�@�C����move�o�����ꍇ��for�𔲂��� */
        		result = true;
        		break;
        	} catch (Exception ex) {
                /** ��O�������i�t�@�C�����b�N���쐬�ł��Ȃ��ꍇ�j��0.1�b�҂� */
            	Thread.sleep(100L);
            }
    	}

    	/** ���b�N�擾�����Ń��b�N���擾�ł��Ȃ������ꍇ�AException��throw���� */
    	if (result == false) {
        	try {
				Files.move(Paths.get(tempfilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        	} catch (Exception ex) {
            	printLogger.error(className + " - File��move�ł��܂���ł����B");
    			ex.printStackTrace();
            	throw ex;
            }
    	}
	}
}
