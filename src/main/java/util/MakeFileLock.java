package util;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MakeFileLock {

	public FileLock doMakeFileLock(FileChannel channel, int maxFileLockWaitTime, String className) throws Exception {

		/** Log出力用PrintLoggerを作成 */
		PrintLogger printLogger = new PrintLogger(MakeFileLock.class.getName());

		/** ファイルロックを作成 */
    	FileLock lock = null;
    	for(int i = 0; i < maxFileLockWaitTime * 10; i++) {
        	try {
        		lock = channel.tryLock();
        		/** ロックを取得できた場合にforを抜ける */
        		if (lock != null) {
        			break;
        		}
        	} catch (Exception ex) {
                /** 例外発生時（ファイルロックを作成できない場合）は1秒待つ */
            	Thread.sleep(100L);
            }
    	}

    	/** ロック取得処理でロックを取得できなかった場合、Exceptionをthrowする */
    	if (lock == null) {
        	try {
        		lock = channel.tryLock();
        	} catch (Exception ex) {
            	printLogger.error(className + " - FileLockを取得できませんでした。");
        		throw ex;	//Exceptionをthrow
            }
    	}

    	return lock;
	}
}
