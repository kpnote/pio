package util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** Log出力用class PrintLogger */
public class PrintLogger {

	/** classNameを保存する */
	private String className;

	/** PrintLoggerコンストラクタ */
	public PrintLogger(Class targetClass){

		/** 引数で渡されたclassNameの名前をメンバー変数に保存する */
		this.className = targetClass.getName();
	}

	/** ログ取得用Loggerオブジェクトを作成 */
	private final Logger logger = LogManager.getLogger(className);

	/** PrintLogger.logger.debug（debugを出力) */
	public void debug(String outputLine) {

		logger.debug(outputLine);
	}

	public void error(String outputLine) {

		logger.error(outputLine);
	}
}
