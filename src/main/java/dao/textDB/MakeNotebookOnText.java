package dao.textDB;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import beans.NoteReqBean;
import util.FileMove;
import util.MakeFileLock;
import util.PrintLogger;

public class MakeNotebookOnText {

	/** Log出力用PrintLoggerを作成 */
	PrintLogger printLogger = new PrintLogger(MakeNotebookOnText.class.getName());

	public StringBuffer execute(NoteReqBean noteReqBean) {

		/** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

    	/** notebookID.csvファイル格納フォルダパス */
		final String folderPath = resource.getString("notebookDir");

		/** ファイルの最大行数 */
		final int maxRecordCount = Integer.parseInt(resource.getString("maxRecordCount"));

    	/** ファイルがロックされている時にファイル開放まで待つ際の最大待ち時間 */
		final int maxFileLockWaitTime = Integer.parseInt(resource.getString("maxFileLockWaitTime"));

		/** notebookID格納用変数 ("diary/new"から"diary"を抽出)  */
		final String notebookCategoryDir = noteReqBean.notebookID.substring(0, noteReqBean.notebookID.indexOf("/") + 1);

		/** notebookID番号格納ファイル名 */
		final String notebokIDFile = "ID";

		/** notebookIDの最新の番号格納ファイル */
        final String idFilePath = folderPath + notebookCategoryDir + notebokIDFile + ".txt";

		/** notebookID.lockファイル絶対パス */
        final String idLockfilePath = folderPath + notebookCategoryDir + notebokIDFile + ".lock";

		/** notebookID.csvテンポラリファイル絶対パス */
        final String idTempfilePath = folderPath + notebookCategoryDir + notebokIDFile + ".tmp";

        /** notebook.csvの情報を取り込む */
        StringBuffer sb = new StringBuffer();

        /** 排他制御を行い、ID.txtの読み込み、新規ID（notebookID.csvの[notebookID]に対応する値）を取得し、
         * notebookID.csvを新規作成する。
         *  */
        try(FileOutputStream out = new FileOutputStream(idLockfilePath);
        		FileChannel channel = out.getChannel()){

        	printLogger.debug("MakeNotebook Lock Start");

        	/** ロックファイルをロックする */
        	MakeFileLock makeFileLock = new MakeFileLock();
        	FileLock lock = makeFileLock.doMakeFileLock(channel, maxFileLockWaitTime, MakeNotebookOnText.class.getName());

        	/** notebookID.csvに対する読み込み及び、書き込みの処理 */
        	try {
        		String MaxNotebookID = "";

                /** ファイルを読み込む */
                try (	FileInputStream fis = new FileInputStream(idFilePath);
                    	InputStreamReader isr = new InputStreamReader(fis, resource.getString("fileCharSetName"));
                    	BufferedReader br = new BufferedReader(isr);
                		){

                    /** 最新のnotebookIDを取得 */
                	MaxNotebookID = br.readLine();

                } catch (IOException ex) {
                    /** 例外発生時処理 */
                    ex.printStackTrace();
                }

                /** 取得した最大notebookID+1を新規作成NotebookIDにする */
                String NewNotebookID = Integer.toString((Integer.parseInt(MaxNotebookID) + 1));

        		/** 新規作成notebookID.csvファイル絶対パス */
                final String filePath = folderPath + notebookCategoryDir + NewNotebookID + ".csv";

        		/** 新規作成notebookID.lockファイル絶対パス */
                final String lockfilePath = folderPath + notebookCategoryDir + NewNotebookID + ".lock";

        		/** 新規作成notebookID.csvテンポラリファイル絶対パス */
                final String tempfilePath = folderPath + notebookCategoryDir + NewNotebookID + ".tmp";

    			/** notebookIDが最大値に達していない場合、notebookID.csvの新規作成処理を行う */
    			if (Integer.parseInt(MaxNotebookID) <= maxRecordCount) {

                	/** テンポラリファイルに書き込む */
                    try(	FileOutputStream fos = new FileOutputStream(tempfilePath);
                        	OutputStreamWriter osr = new OutputStreamWriter(fos, resource.getString("fileCharSetName"));
                        	BufferedWriter filewriter = new BufferedWriter(osr);
                    		){

            			/** 日時を取得（noteの更新日時、新規登録日時に使用） */
            			LocalDateTime localDateTime = LocalDateTime.now();

            			/** index.csvに書き込むnotebook.csvのタイトル用としてContentTitleの１行目を取得 */
            			String[] ContentTitleLines = noteReqBean.ContentTitle.split("\t"); /** ContentTitleを \t でsplit */
            			String ContentTitleFirstLine = ContentTitleLines[0];               /** ContentTitleを \t でsplitした結果の[0]をContentTitleの1行目とする */

                        /** テンポラリファイルを更新する
                         * ファイル読み込み処理で取得したcsvファイル情報をテンポラリファイルに書き込む
                         * 新規登録データの親IDに指定されたデータに対しては、
                         * ChildIDTagsに新規登録データのIDを追加する。
                         *  */
            			for (int i = 0; i < 2; i++) {

            				/** 新規登録データ（noteBeanに格納された情報）をテンポラリファイルに書き込む */
            				if (i == 0) {
                			filewriter.write("\""     + i
                					  		+ "\",\"" + noteReqBean.ChildIDTags
                					  		+ "\",\"" + localDateTime	/** 作成日時を書き込む */
                					  		+ "\",\"" + noteReqBean.UpdateDate
                					  		+ "\",\"" + noteReqBean.DeleteDate
                					  		+ "\",\"" + noteReqBean.PDCAPhase
                					  		//+ "\",\"" + noteReqBean.ContentTitle.substring(0, noteReqBean.ContentTitle.indexOf("\t"))
                					  		+ "\",\"" + ContentTitleFirstLine
                					  		+ "\",\"" + noteReqBean.ContentDesc
                					  		+ "\",\"" + noteReqBean.ContentStatus
                					  		+ "\"");
        					filewriter.write("\r\n");
            				} else {
                    			filewriter.write("\""     + i
            					  		+ "\",\"" + noteReqBean.ChildIDTags
            					  		+ "\",\"" + localDateTime	/** 作成日時を書き込む */
            					  		+ "\",\"" + noteReqBean.UpdateDate
            					  		+ "\",\"" + noteReqBean.DeleteDate
            					  		+ "\",\"" + noteReqBean.PDCAPhase
            					  		+ "\",\"" + noteReqBean.ContentTitle
            					  		+ "\",\"" + noteReqBean.ContentDesc
            					  		+ "\",\"" + noteReqBean.ContentStatus
            					  		+ "\"");

            				}
            			}

            			filewriter.close();

            			/** テンポラリファイルをnotebook.csvとして移動（上書き）する */
            			FileMove filemove = new FileMove();
            			filemove.doFileMove(tempfilePath, filePath, maxFileLockWaitTime, MakeNotebookOnText.class.getName());

            		}catch(IOException e){
            			e.printStackTrace();
                    	printLogger.error("MakeNotebook End(NG) notebook.csv作成処理でnotebook.csvの新規作成に失敗しました。MakeNotebook処理を中止します。対象ファイル：" + filePath);
            		}

                	/** テンポラリファイルに書き込む */
                    try(	FileOutputStream fos = new FileOutputStream(idTempfilePath);
                        	OutputStreamWriter osr = new OutputStreamWriter(fos, resource.getString("fileCharSetName"));
                        	BufferedWriter filewriter = new BufferedWriter(osr);
                    		){

                    	filewriter.write(NewNotebookID);

            			filewriter.close();

            			/** テンポラリファイルをnotebook.csvとして移動（上書き）する */
            			FileMove filemove = new FileMove();
            			filemove.doFileMove(idTempfilePath, idFilePath, maxFileLockWaitTime, MakeNotebookOnText.class.getName());

            		}catch(IOException e){
            			e.printStackTrace();
                    	printLogger.error("MakeNotebook End(NG) idファイル更新処理でID.txtの更新に失敗しました。MakeNotebook処理を中止します。対象ファイル：" + idFilePath + " 新規作成ファイル：" + filePath);
            		}

    			} else {
    				/** TODO:最大行数が1000行を超えた場合はエラーメッセージを表示する（未実装） */
    			}

        	} finally {
        		/** 排他制御に使用したファイルロックをリリースする */
    			lock.release();
    			printLogger.debug("MakeNotebook Lock End");
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
        	printLogger.error("MakeNotebook End(NG) FileLock処理でロックファイルを取得できなかった可能性があります。MakeNotebook処理を中止します。対象ファイル：" + idFilePath);
		}

        return sb;
    }
}
