package util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** Log出力用class PrintLogger */
public class PrintLogger {

	/** ログ取得用Loggerオブジェクトを作成 */
	private Logger logger;

	/** PrintLoggerコンストラクタ
	 * @param ClassName クラス名
	 * */
	public PrintLogger(String ClassName){

		/** 引数で渡されたclassNameの名前でLoggerを作成する */
		this.logger = LogManager.getLogger(ClassName);
	}

	/** PrintLogger.logger.debug（debugを出力) */
	public void debug(String outputLine) {
		logger.debug(outputLine);
	}
	public void info(String outputLine) {
		logger.info(outputLine);
	}
	public void warn(String outputLine) {
		logger.warn(outputLine);
	}
	public void error(String outputLine) {
		logger.error(outputLine);
	}
	public void fatal(String outputLine) {
		logger.fatal(outputLine);
	}
}
