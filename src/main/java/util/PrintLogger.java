package util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** Log�o�͗pclass PrintLogger */
public class PrintLogger {

	/** className��ۑ����� */
	private String className;

	/** PrintLogger�R���X�g���N�^ */
	public PrintLogger(Class targetClass){

		/** �����œn���ꂽclassName�̖��O�������o�[�ϐ��ɕۑ����� */
		this.className = targetClass.getName();
	}

	/** ���O�擾�pLogger�I�u�W�F�N�g���쐬 */
	private final Logger logger = LogManager.getLogger(className);

	/** PrintLogger.logger.debug�idebug���o��) */
	public void debug(String outputLine) {

		logger.debug(outputLine);
	}

	public void error(String outputLine) {

		logger.error(outputLine);
	}
}
