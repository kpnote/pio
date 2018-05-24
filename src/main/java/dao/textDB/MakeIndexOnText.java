package dao.textDB;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ResourceBundle;

import beans.NotebookBean;
import util.FileMove;
import util.MakeFileLock;
import util.PrintLogger;

public class MakeIndexOnText {

	/** Log出力用PrintLoggerを作成 */
	PrintLogger printLogger = new PrintLogger(MakeIndexOnText.class.getName());

	public void execute(String notebookDirectoryName, NotebookBean[] notebookBean) {

		/** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** notebookID.csvファイル格納フォルダパス */
		final String folderPath = resource.getString("notebookDir");

    	/** ファイルがロックされている時にファイル開放まで待つ際の最大待ち時間 */
		final int maxFileLockWaitTime = Integer.parseInt(resource.getString("maxFileLockWaitTime"));

		/** indexファイル名 */
		final String indexFileName = "index";

		/** index.csvファイル絶対パス */
        final String filePath = folderPath + notebookDirectoryName + "/" + indexFileName + ".csv";

		/** notebookID.lockファイル絶対パス */
        final String lockfilePath = folderPath + notebookDirectoryName + "/" + indexFileName + ".lock";

		/** notebookID.csvテンポラリファイル絶対パス */
        final String tempfilePath = folderPath + notebookDirectoryName + "/" + indexFileName + ".tmp";

        /** 排他制御を行い、notebookID.csvの読み込み、書き込み処理を行う
         *  ファイル読み込み時からロックを掛けるのは、Insert処理が重なった場合に
         *  同じタイミングでファイルの参照をしてしまうと、
         *  noteIDをファイル行数から取得してるので、同じ行数を取得し、
         *  同じnoteIDを追加してしまう為。
         *  */
        try(FileOutputStream out = new FileOutputStream(lockfilePath);
        		FileChannel channel = out.getChannel()){

        	printLogger.debug("MakeIndex Lock Start");

        	/** ロックファイルをロックする */
        	MakeFileLock makeFileLock = new MakeFileLock();
        	FileLock lock = makeFileLock.doMakeFileLock(channel, maxFileLockWaitTime, MakeIndexOnText.class.getName());

        	/** index.csvに対する読み込み及び、書き込みの処理 */
        	try {

                	/** テンポラリファイルに書き込む */
                    try(	FileOutputStream fos = new FileOutputStream(tempfilePath);
                        	OutputStreamWriter osr = new OutputStreamWriter(fos, resource.getString("fileCharSetName"));
                        	BufferedWriter filewriter = new BufferedWriter(osr);
                    		){

                        /** テンポラリファイルを更新する
                         * ファイル読み込み処理で取得したcsvファイル情報をテンポラリファイルに書き込む
                         * 新規登録データの親IDに指定されたデータに対しては、
                         * ChildIDTagsに新規登録データのIDを追加する。
                         *  */
            			for (int i = 0; i < notebookBean.length; i++) {
            				if (!notebookBean[i].ID.equals(indexFileName)) {

                				/** 新規登録データ（noteBeanに格納された情報）をテンポラリファイルに書き込む */
                    			filewriter.write("\"" + notebookBean[i].ID
            					  		+ "\",\"" + notebookBean[i].ChildIDTags
            					  		+ "\",\"" + notebookBean[i].CreateDate
            					  		+ "\",\"" + notebookBean[i].UpdateDate
            					  		+ "\",\"" + notebookBean[i].DeleteDate
            					  		+ "\",\"" + notebookBean[i].PDCAPhase
            					  		+ "\",\"" + notebookBean[i].ContentTitle
            					  		+ "\",\"" + notebookBean[i].ContentDesc
            					  		+ "\",\"" + notebookBean[i].ContentStatus
            					  		+ "\"");
                				if(i < notebookBean.length - 1) {
                        			filewriter.write("\r\n");
                				}
            				}
                        }

            			filewriter.close();

            			/** テンポラリファイルをnotebook.csvとして移動（上書き）する */
            			FileMove filemove = new FileMove();
            			filemove.doFileMove(tempfilePath, filePath, maxFileLockWaitTime, MakeIndexOnText.class.getName());

            		}catch(IOException e){
            			e.printStackTrace();
                    	printLogger.error("MakeIndex End(NG) MakeIndex処理でindex.csvの更新に失敗しました。MakeIndex処理を中止します。MakeIndex対象ファイル：" + filePath);
            		}

        	} finally {
        		/** 排他制御に使用したファイルロックをリリースする */
    			lock.release();
    			printLogger.debug("MakeIndex Lock End");
        	}
        } catch (FileNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
        	printLogger.error("MakeIndex End(NG) FileLock処理でロックファイルを取得できなかった可能性があります。MakeIndex処理を中止します。MakeIndex対象ファイル：" + filePath);
		}
    }
}
