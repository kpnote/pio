package dao.textDB;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import Beans.NotebookBean;
import util.PrintLogger;

public class ListNotebookOnText {

    public NotebookBean[] execute(String notebookCategoryName) {

    	/** Log出力用PrintLoggerを作成 */
    	PrintLogger printLogger = new PrintLogger(ListNotebookOnText.class.getName());

    	/** propertiesファイルの情報を取得 */
    	ResourceBundle resource = ResourceBundle.getBundle("config");

		/** notebookID.csvファイル格納フォルダパス */
		final String folderPath = resource.getString("notebookDir");

    	/** list対象のディレクトリ格納用変数を作成 */
		String targetFolderPath = null;

		/** 対象ディレクトリにあるフィルタ条件と一致するファイル情報を格納 */
		File[] files = null;

		/** ファイル情報を格納する変数を作成 */
		NotebookBean[] notebookBean = null;

    	/** 引数として渡されたnotebookCategoryNameに値がセットされている場合、
    	 * targetFolderPathにnotebookCategoryNameをappendする。 */
		if(notebookCategoryName.equals(null)) {
    		targetFolderPath = folderPath;
    	} else {
    		targetFolderPath = folderPath + notebookCategoryName;
    	}

        StringBuffer sb = new StringBuffer();

        try {
        	/** list処理開始時間を出力 */
        	printLogger.debug("Select Start:" + System.currentTimeMillis());

        	/** 対象ディレクトリのFileオブジェクトを作成 */
        	File dir = new File(targetFolderPath);

        	/** 対象ディレクトリの".csv"で終わるファイル名の情報を格納 */
        	files = dir.listFiles(new FileFilter() {
        		@Override
        		public boolean accept(File pathname) {
        			return pathname.getAbsoluteFile().getName().endsWith(".csv");
        		}
        	});

        	/** notebookBeanのサイズにfiles.lengthを指定 */
        	notebookBean = new NotebookBean[files.length];

        	/** 日時出力用フォーマットを作成 */
        	SimpleDateFormat jsJpSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    		/** 取得したfilesの情報をnotebookBeanにセットする */
        	for(int i = 0; i < files.length; i++) {

        		/** notebookBeanのインスタンスを作成する */
        		notebookBean[i] = new NotebookBean();

        		/** ディレクトリのpathで使われているセパレータを見て、Linux環境（/）かWindows環境（\\）かを確認してnotebookID(1、2、・・・）をセットする */
        		int from = 0;
        		int to = files[i].getPath().lastIndexOf(".");
        		if( files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorLinux")) > 0 ) {
            		from = files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorLinux"));
        		} else {
            		from = files[i].getPath().lastIndexOf(resource.getString("serverPathSeparetorWindows")) + 1;
        		}
        		notebookBean[i].setNotebookID(files[i].getPath().substring(from, to));

        		/** Directoryをセットする */
        		notebookBean[i].setDirectory(notebookCategoryName);

        		/** 最終更新日時をミリ秒でセットする */
        		Date date = new Date(files[i].lastModified());
        		notebookBean[i].setUpdateDate(jsJpSdf.format(date).toString());

        		/** notebook.csvの1行目を取得し、タイトル（〇〇の件）を取得する */

        		/** notebook.csvを開く（エラーの場合は処理を終了する） */
                /** ファイルを読み込む */
                try (	FileInputStream fis = new FileInputStream(files[i].getPath());
                    	InputStreamReader isr = new InputStreamReader(fis, resource.getString("fileCharSetName"));
                    	BufferedReader br = new BufferedReader(isr);
                		){

                    /** 読み込んだファイルから１行読み込む */
    				String line = br.readLine();

    				/** 文字列をデリミタで分割し、配列に格納
    				 * splitについては、分割された後で最後の項目が空白の場合でも配列に格納されるように、
    				 * 第二引数に-1を設定
    				 *  */
    				String[] sbLineTmp = line.substring(1, line.length() -1).split("\",\"", -1);

                } catch (IOException ex) {
                    /** 例外発生時処理 */
                    ex.printStackTrace();
                }

        	}

        	/** select処理開始時間を出力 */
        	printLogger.debug("Select End:" + System.currentTimeMillis());

        } catch (Exception ex) {
            //例外発生時処理
            ex.printStackTrace();
        }

        return notebookBean;
    }
}
