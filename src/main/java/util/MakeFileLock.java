package util;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MakeFileLock {

	public FileLock doMakeFileLock(FileChannel channel, int maxFileLockWaitTime, String className) throws Exception {

		/** Log�o�͗pPrintLogger���쐬 */
		PrintLogger printLogger = new PrintLogger(MakeFileLock.class.getName());

		/** �t�@�C�����b�N���쐬 */
    	FileLock lock = null;
    	for(int i = 0; i < maxFileLockWaitTime * 10; i++) {
        	try {
        		lock = channel.tryLock();
        		/** ���b�N���擾�ł����ꍇ��for�𔲂��� */
        		if (lock != null) {
        			break;
        		}
        	} catch (Exception ex) {
                /** ��O�������i�t�@�C�����b�N���쐬�ł��Ȃ��ꍇ�j��1�b�҂� */
            	Thread.sleep(100L);
            }
    	}

    	/** ���b�N�擾�����Ń��b�N���擾�ł��Ȃ������ꍇ�AException��throw���� */
    	if (lock == null) {
        	try {
        		lock = channel.tryLock();
        	} catch (Exception ex) {
            	printLogger.error(className + " - FileLock���擾�ł��܂���ł����B");
        		throw ex;	//Exception��throw
            }
    	}

    	return lock;
	}
}
