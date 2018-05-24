package util;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileMove {

	public void doFileMove(String tempfilePath, String filePath, int maxFileLockWaitTime, String className) throws Exception {
//		public FileLock doFileMove(FileChannel channel, int maxFileLockWaitTime, String className) throws Exception {

		/** Log出力用PrintLoggerを作成 */
		PrintLogger printLogger = new PrintLogger(FileMove.class.getName());

		boolean result = false;

    	for(int i = 0; i < maxFileLockWaitTime * 10; i++) {
        	try {
				Files.move(Paths.get(tempfilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        		/** ファイルをmove出来た場合にforを抜ける */
        		result = true;
        		break;
        	} catch (Exception ex) {
                /** 例外発生時（ファイルロックを作成できない場合）は0.1秒待つ */
            	Thread.sleep(100L);
            }
    	}

    	/** ロック取得処理でロックを取得できなかった場合、Exceptionをthrowする */
    	if (result == false) {
        	try {
				Files.move(Paths.get(tempfilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        	} catch (Exception ex) {
            	printLogger.error(className + " - Fileをmoveできませんでした。");
    			ex.printStackTrace();
            	throw ex;
            }
    	}
	}
}
