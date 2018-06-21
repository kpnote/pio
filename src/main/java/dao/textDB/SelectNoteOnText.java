package dao.textDB;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ResourceBundle;

import util.PrintLogger;

public class SelectNoteOnText {

    public StringBuffer execute(String notebookID) {

    	/** Log出力用PrintLoggerを作成 */
    	PrintLogger printLogger = new PrintLogger(SelectNoteOnText.class.getName());

    	/** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

        StringBuffer sb = new StringBuffer();

        try {
        	//TODO:読み込み処理の途中で書き込み処理が行われ、読み込み対象ファイルが差し変わった場合にどのような処理になるかを検討する（今はアイディアがない）
            //     懸念
        	//     １．読み込みエラー
        	//     ２．データ不整合　現在は、ファイル末尾に追加するだけだが、途中のレコードを更新する処理を追加した場合に、不整合が起きる可能性がある

        	/** select処理開始時間を出力 */
        	printLogger.debug("Select Start");

            //ファイルを読み込む
            //FileReader fr = new FileReader("C:\\Users\\home401\\Source\\Repos\\PDCALinkWeb40\\PDCALink\\www\\" + notebookID + ".csv");
            FileReader fr = new FileReader(resource.getString("notebookDir") + notebookID + ".csv");
            BufferedReader br = new BufferedReader(fr);

            //読み込んだファイルを１行ずつ画面出力する
            String line;
//            int count = 0;
            while ((line = br.readLine()) != null) {
//                System.out.println(++count + "行目：" + line);
                sb.append(line);
                sb.append("\r\n");
            }

            //終了処理
            br.close();
            fr.close();

        	/** select処理開始時間を出力 */
        	printLogger.debug("Select End");

        } catch (IOException ex) {
            //例外発生時処理
            ex.printStackTrace();
        }

        return sb;
    }
}
