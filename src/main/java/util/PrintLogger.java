package util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** Log�o�͗pclass PrintLogger */
public class PrintLogger {

	/** ���O�擾�pLogger�I�u�W�F�N�g���쐬 */
	private Logger logger;

	/** PrintLogger�R���X�g���N�^
	 * @param ClassName �N���X��
	 * */
	public PrintLogger(String ClassName){

		/** �����œn���ꂽclassName�̖��O��Logger���쐬���� */
		this.logger = LogManager.getLogger(ClassName);
	}

	/** PrintLogger.logger.debug�idebug���o��) */
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
