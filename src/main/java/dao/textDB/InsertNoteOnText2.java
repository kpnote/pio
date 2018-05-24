package dao.textDB;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import beans.NoteReqBean;
import util.FileMove;
import util.MakeFileLock;
import util.PrintLogger;

public class InsertNoteOnText2 {

	/** Log出力用PrintLoggerを作成 */
	PrintLogger printLogger = new PrintLogger(InsertNoteOnText2.class.getName());

	public StringBuffer execute(NoteReqBean noteReqBean) {

		/** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** notebookID.csvファイル格納フォルダパス */
		//final String folderPath = "C:\\Users\\home401\\Source\\Repos\\PDCALinkWeb40\\PDCALink\\www\\";
		final String folderPath = resource.getString("notebookDir");

		/** ファイルの最大行数 */
		final int maxRecordCount = Integer.parseInt(resource.getString("maxRecordCount"));

    	/** ファイルがロックされている時にファイル開放まで待つ際の最大待ち時間 */
		final int maxFileLockWaitTime = Integer.parseInt(resource.getString("maxFileLockWaitTime"));

		/** notebookID格納用変数 */
		final String notebookID = noteReqBean.notebookID;

		/** notebookID.csvファイル絶対パス */
        final String filePath = folderPath + notebookID + ".csv";

		/** notebookID.lockファイル絶対パス */
        final String lockfilePath = folderPath + notebookID + ".lock";

		/** notebookID.csvテンポラリファイル絶対パス */
        final String tempfilePath = folderPath + notebookID + ".tmp";

        /** notebook.csvの情報を取り込む */
        StringBuffer sb = new StringBuffer();

        /** 排他制御を行い、notebookID.csvの読み込み、書き込み処理を行う
         *  ファイル読み込み時からロックを掛けるのは、Insert処理が重なった場合に
         *  同じタイミングでファイルの参照をしてしまうと、
         *  noteIDをファイル行数から取得してるので、同じ行数を取得し、
         *  同じnoteIDを追加してしまう為。
         *  */
        try(FileOutputStream out = new FileOutputStream(lockfilePath);
        		FileChannel channel = out.getChannel()){

        	printLogger.debug("Insert Lock Start");

        	/** ロックファイルをロックする */
        	MakeFileLock makeFileLock = new MakeFileLock();
        	FileLock lock = makeFileLock.doMakeFileLock(channel, maxFileLockWaitTime, InsertNoteOnText2.class.getName());

        	/** notebookID.csvに対する読み込み及び、書き込みの処理 */
        	try {

                /** ファイルを読み込む */
                try (	FileInputStream fis = new FileInputStream(filePath);
                    	InputStreamReader isr = new InputStreamReader(fis, resource.getString("fileCharSetName"));
                    	BufferedReader br = new BufferedReader(isr);
                		){

                    //方式変更
                    //FileReader fr = new FileReader(filePath);
                    //BufferedReader br = new BufferedReader(fr);

                    /** 読み込んだファイルを１行ずつ画面出力する */
                    String line;

                    while ((line = br.readLine()) != null) {
//                        System.out.println(++lineCount + "行目：" + line);
                        sb.append(line);
                        sb.append("\r\n");
                    }

                    /** 終了処理 */
                    //方式変更により不要
                    //br.close();
                    //fr.close();
                } catch (IOException ex) {
                    /** 例外発生時処理 */
                    ex.printStackTrace();
                }

    			String sbLine = sb.toString();
    			String[] sbLines = sbLine.split("\r\n", 0);

    			/** ファイル行数が1000行以下の場合、新規登録処理の処理へ進む */
    			if (sbLines.length <= maxRecordCount) {

                	/** テンポラリファイルに書き込む */
                    try(	FileOutputStream fos = new FileOutputStream(tempfilePath);
                        	OutputStreamWriter osr = new OutputStreamWriter(fos, resource.getString("fileCharSetName"));
                        	BufferedWriter filewriter = new BufferedWriter(osr);
                    		){
//            			File tempfile = new File(tempfilePath);
//            			FileWriter filewriter = new FileWriter(tempfile, false);

            			/** 新規登録するnoteのIDを格納する(既存レコード数をIDとして格納する(0番目にはnotebookID.csvのタイトル情報を格納)) */
            			int newLineNum = sbLines.length;

            			/** 日時を取得（noteの更新日時、新規登録日時に使用） */
            			LocalDateTime localDateTime = LocalDateTime.now();

                        /** テンポラリファイルを更新する
                         * ファイル読み込み処理で取得したcsvファイル情報をテンポラリファイルに書き込む
                         * 新規登録データの親IDに指定されたデータに対しては、
                         * ChildIDTagsに新規登録データのIDを追加する。
                         *  */
            			for (int i = 0; i < sbLines.length; i++) {
//                            System.out.println((i+1) + "行目：" + sbLines[i] + "\r\n");

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
                    					//+ "\",\"" + sbLineTmp[3]
                            			+ "\",\"" + localDateTime	/** 更新日時を書き込む */
                    					+ "\",\"" + sbLineTmp[4]
                    					+ "\",\"" + sbLineTmp[5]
                    					+ "\",\"" + sbLineTmp[6]
                            			+ "\",\"" + sbLineTmp[7]
                                    	+ "\",\"" + sbLineTmp[8]
            					  		+ "\"\r\n");
                			} else {
            					/** 更新対象のデータではない場合 */
                    			/** csvの内容をテンポラリファイルに書き込み*/
                    			filewriter.write(sbLines[i] + "\r\n");
                			}
                        }

                        /** 新規登録データ（noteBeanに格納された情報）をテンポラリファイルに書き込む */
            			//filewriter.write("\""     + noteBean.ID
            			filewriter.write("\""     + newLineNum
            					  		+ "\",\"" + noteReqBean.ChildIDTags
            					  		//+ "\",\"" + noteReqBean.CreateDate
            					  		+ "\",\"" + localDateTime	/** 作成日時を書き込む */
            					  		+ "\",\"" + noteReqBean.UpdateDate
            					  		+ "\",\"" + noteReqBean.DeleteDate
            					  		+ "\",\"" + noteReqBean.PDCAPhase
            					  		+ "\",\"" + noteReqBean.ContentTitle
            					  		+ "\",\"" + noteReqBean.ContentDesc
            					  		+ "\",\"" + noteReqBean.ContentStatus
            					  		+ "\"");

            			filewriter.close();

            			/** テンポラリファイルをnotebook.csvとして移動（上書き）する */
            			//Files.move(Paths.get(tempfilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            			FileMove filemove = new FileMove();
            			filemove.doFileMove(tempfilePath, filePath, maxFileLockWaitTime, InsertNoteOnText2.class.getName());

            		}catch(IOException e){
            			e.printStackTrace();
                    	printLogger.error("Insert End(NG) Insert処理でnotebook.csvの更新に失敗しました。Insert処理を中止します。Insert対象ファイル：" + filePath);
            		}
    			} else {
    				/** TODO:最大行数が1000行を超えた場合はエラーメッセージを表示する（未実装） */
    			}

        	} finally {
        		/** 排他制御に使用したファイルロックをリリースする */
    			lock.release();
    			printLogger.debug("Insert Lock End");
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
        	printLogger.error("Insert End(NG) FileLock処理でロックファイルを取得できなかった可能性があります。Insert処理を中止します。Insert対象ファイル：" + filePath);
		}

        return sb;
    }
}
