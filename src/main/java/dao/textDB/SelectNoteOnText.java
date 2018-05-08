package dao.textDB;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ResourceBundle;

import util.PrintLogger;

public class SelectNoteOnText {

    public StringBuffer execute(String notebookID) {

    	/** Log�o�͗pPrintLogger���쐬 */
    	PrintLogger printLogger = new PrintLogger(SelectNoteOnText.class);

    	/** properties�t�@�C���̏����擾 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

        StringBuffer sb = new StringBuffer();

        try {
        	/** select�����J�n���Ԃ��o�� */
        	printLogger.debug("Select Start:" + System.currentTimeMillis());

            //�t�@�C����ǂݍ���
            //FileReader fr = new FileReader("C:\\Users\\home401\\Source\\Repos\\PDCALinkWeb40\\PDCALink\\www\\" + notebookID + ".csv");
            FileReader fr = new FileReader(resource.getString("notebookDir") + notebookID + ".csv");
            BufferedReader br = new BufferedReader(fr);

            //�ǂݍ��񂾃t�@�C�����P�s����ʏo�͂���
            String line;
//            int count = 0;
            while ((line = br.readLine()) != null) {
//                System.out.println(++count + "�s�ځF" + line);
                sb.append(line);
                sb.append("\r\n");
            }

            //�I������
            br.close();
            fr.close();

        	/** select�����J�n���Ԃ��o�� */
        	printLogger.debug("Select End:" + System.currentTimeMillis());

        } catch (IOException ex) {
            //��O����������
            ex.printStackTrace();
        }

        return sb;
    }
}
