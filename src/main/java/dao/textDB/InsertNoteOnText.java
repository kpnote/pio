package dao.textDB;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ResourceBundle;

import Beans.NoteReqBean;
import util.PrintLogger;

public class InsertNoteOnText {

	/** Log出力用PrintLoggerを作成 */
	PrintLogger printLogger = new PrintLogger(SelectNoteOnText.class);

    /** ファイル書き込み処理で排他制御を行う為、synchronizedメソッドとする */
	synchronized public StringBuffer execute(NoteReqBean noteReqBean) {
    	System.out.println("Insert Start:" + System.currentTimeMillis());

        /** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** notebookID.csvファイル格納フォルダパス */
		//final String folderPath = "C:\\Users\\home401\\Source\\Repos\\PDCALinkWeb40\\PDCALink\\www\\";
		final String folderPath = resource.getString("notebookDir");

		/** notebookID格納用変数 */
		final String notebookID = noteReqBean.notebookID;

		/** notebookID.csvファイル絶対パス */
        final String filePath = folderPath + notebookID + ".csv";

		/** notebookID.lockファイル絶対パス */
        final String lockfilePath = folderPath + notebookID + ".lock";

        /** notebook.csvの情報を取り込む */
        StringBuffer sb = new StringBuffer();

        /** 排他制御を行い、notebookID.csvの読み込み、書き込み処理を行う */
        try(FileOutputStream out = new FileOutputStream(lockfilePath);
        		FileChannel channel = out.getChannel()){

        	printLogger.debug("Insert Lock Start:" + System.currentTimeMillis());

        	/** ロックを取得 */
        	FileLock lock = channel.lock();

        	/** notebookID.csvに対する読み込み及び、書き込みの処理 */
        	try {

                /** ファイルを読み込む */
                try {
                    FileReader fr = new FileReader(filePath);
                    BufferedReader br = new BufferedReader(fr);

                    /** 読み込んだファイルを１行ずつ画面出力する */
                    String line;

//                    /** notebookID.csvの行数（1行目～）を格納する */
//                    int lineCount = 0;

                    while ((line = br.readLine()) != null) {
//                        System.out.println(++lineCount + "行目：" + line);
                        sb.append(line);
                        sb.append("\r\n");
                    }

                    /** 終了処理 */
                    br.close();
                    fr.close();
                } catch (IOException ex) {
                    /** 例外発生時処理 */
                    ex.printStackTrace();
                }

            	/** ファイルを書き込む */
                try{
        			File file = new File(filePath);
        			FileWriter filewriter = new FileWriter(file, false);

        			String sbLine = sb.toString();
        			String[] sbLines = sbLine.split("\r\n", 0);

        			/** 新規登録するnoteのIDを格納する(既存レコード数をIDとして格納する(0番目にはnotebookID.csvのタイトル情報を格納)) */
        			int newLineNum = sbLines.length;

                    /** csvファイルを更新する
                     * ファイル読み込み処理で取得したcsvファイル情報をcsvファイルに書き込む
                     * 新規登録データの親IDに指定されたデータに対しては、
                     * ChildIDTagsに新規登録データのIDを追加する。
                     *  */
        			for (int i = 0; i < sbLines.length; i++) {
//                        System.out.println((i+1) + "行目：" + sbLines[i] + "\r\n");

            			/** 新規登録データの親IDに指定されたデータかどうか（＝更新するデータであること）を判別する */
        				if(!noteReqBean.ParentID.equals("") //親IDが空欄ではない場合
            					&& (i == Integer.parseInt(noteReqBean.ParentID))) { //親IDと一致するデータの場合
        					/** 更新対象のデータの場合 */

            				/** 文字列をデリミタで分割し、配列に格納
            				 * splitについては、分割された後で最後の項目が空白の場合でも配列に格納されるように、
            				 * 第二引数に-1を設定
            				 *  */
            				String[] sbLineTmp = sbLines[i].substring(1, sbLines[i].length() -1).split("\",\"", -1);

            				/** 新規登録データの親IDとして指定されたデータのChildIDTagsの頭に新規登録データのIDを設定 */
            				sbLineTmp[1] = sbLineTmp[1] + " " + newLineNum;

                			/** csvの内容をファイルに書き込み */
            				filewriter.write("\""     + sbLineTmp[0]
                					+ "\",\"" + sbLineTmp[1]
                					+ "\",\"" + sbLineTmp[2]
                					+ "\",\"" + sbLineTmp[3]
                					+ "\",\"" + sbLineTmp[4]
                					+ "\",\"" + sbLineTmp[5]
                					+ "\",\"" + sbLineTmp[6]
                        			+ "\",\"" + sbLineTmp[7]
                                	+ "\",\"" + sbLineTmp[8]
        					  		+ "\"\r\n");
            			} else {
        					/** 更新対象のデータではない場合 */
                			/** csvの内容をファイルに書き込み*/
                			filewriter.write(sbLines[i] + "\r\n");
            			}
                    }

                    /** 新規登録データ（noteBeanに格納された情報）をnotebookID.csvに書き込む */
        			//filewriter.write("\""     + noteBean.ID
        			filewriter.write("\""     + newLineNum
        					  		+ "\",\"" + noteReqBean.ChildIDTags
        					  		+ "\",\"" + noteReqBean.CreateDate
        					  		+ "\",\"" + noteReqBean.UpdateDate
        					  		+ "\",\"" + noteReqBean.DeleteDate
        					  		+ "\",\"" + noteReqBean.PDCAPhase
        					  		+ "\",\"" + noteReqBean.ContentTitle
        					  		+ "\",\"" + noteReqBean.ContentDesc
        					  		+ "\",\"" + noteReqBean.ContentStatus
        					  		+ "\"\r\n");

        			filewriter.close();

        		}catch(IOException e){
        			System.out.println(e);
        		}
        	} finally {
        		/** 排他制御に使用したロックファイルをリリースする */
    			lock.release();
    			printLogger.debug("Insert Lock End:" + System.currentTimeMillis());
        	}
        } catch (FileNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

        return sb;
    }
}
