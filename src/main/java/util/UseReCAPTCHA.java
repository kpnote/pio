/**
 *
 */
package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author home401
 *
 */
public class UseReCAPTCHA {

	public boolean success = true;
	public boolean failure = false;

	/** Log出力用PrintLoggerを作成 */
	private PrintLogger printLogger = new PrintLogger(UseReCAPTCHA.class.getName());

	/**
	 * @param reCAPTCHAToken
	 */
	public boolean doGetURLResponse(String reCAPTCHAToken){

		/** 戻り値を格納する変数を作成 */
		boolean returnval = failure;

	    /** propertiesファイルの情報を取得 */
		ResourceBundle resource = ResourceBundle.getBundle("config");

		/** secret.propertiesファイル格納ディレクトリパス */
		String secPropertiesFilePath = (String)(resource.getString("secretDir") + "secret.properties");

		/** secret.propertiesオブジェクトを作成*/
		Properties secProperties = new Properties();

		/** secPropertiesファイルの情報を取得 */
		try(Reader reader = new InputStreamReader(new FileInputStream(secPropertiesFilePath), resource.getString("fileCharSetName"))){
			secProperties.load(reader);
		} catch (UnsupportedEncodingException e1) {
			// TODO 自動生成された catch ブロック
			//e1.printStackTrace();
        	printLogger.error("secret.propertiesファイルエンコーディングが未サポートです。(UnsupportedEncodingException)");
			return failure;
		} catch (FileNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			//e1.printStackTrace();
        	printLogger.error("secret.propertiesファイルがありません。(FileNotFoundException)");
			return failure;
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			//e1.printStackTrace();
        	printLogger.error("secret.propertiesファイルを読み込めません。(IOException)");
			return failure;
		}

		try {
			/** reCAPTCHAベリファイ用URLをセット*/
//			URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
//			URL url = new URL(resource.getString("reCAPTCHAURL"));
			URL url = new URL(secProperties.getProperty("reCAPTCHAURL"));

			try {
				/** HTTPコネクションを作成
				 * （オブジェクト作成後は自動的にコネクションが作成されるので、
				 * connect処理までは止めないようにする。
				 * 尚、一時停止すると「IllegalStateException:Already Connected」エラーとなる。 */
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				/** この接続で出力も行うように設定 */
				conn.setDoOutput(true);

				/** リクエストメソッドをPOSTに設定 */
				conn.setRequestMethod("POST");

				/** reCAPTCHA認証のリクエストパラメータをセットする */
				try(OutputStream out = conn.getOutputStream()){
					//out.write(("secret=" + resource.getString("reCAPTCHASecret")).getBytes());	//reCAPTCHAのシークレットキー
					out.write(("secret=" + secProperties.getProperty("reCAPTCHASecret")).getBytes());	//reCAPTCHAのシークレットキー
					out.write("&".getBytes());	//引数のセパレータ"&"
					out.write(("response=" + reCAPTCHAToken).getBytes());						//ユーザから渡されたreCAPTCHAのトークン
				}

				/** Http Connectで指定したURLにPOST */
				conn.connect();

				/** レスポンスコードを取得 */
				if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					try(InputStream in = conn.getInputStream();
	                    	InputStreamReader isr = new InputStreamReader(in, resource.getString("fileCharSetName"));
	                    	BufferedReader bufferedReader = new BufferedReader(isr);
	                		){

						/** レスポンスコードが正常の場合にレスポンス（reCAPTCHA認証結果）を取得する */
						String line = null;
						while((line = bufferedReader.readLine()) != null) {
							//System.out.println(line + "\r\n");
							if((line.indexOf("success") > -1) && (line.indexOf("true") > -1)) {
								//** レスポンスデータに ["success": true]の１行を含む場合に 戻り値としてtrueを返す  */
								returnval = success;
								break;
							}
						}
					}
				}

				/** Http Connectionを切断 */
				conn.disconnect();

			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				//e.printStackTrace();
	        	printLogger.error("HTTPコネクションで情報を取得できませんでした。(IOException)");
				return failure;
			}
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
        	printLogger.error("URLオブジェクトを作成できませんでした。(MalformedURLException)");
			return failure;
		}

		/** 結果をreturnする */
		return returnval;
	}
}
