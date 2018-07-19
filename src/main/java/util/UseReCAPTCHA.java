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

	/** Log�o�͗pPrintLogger���쐬 */
	private PrintLogger printLogger = new PrintLogger(UseReCAPTCHA.class.getName());

	/**
	 * @param reCAPTCHAToken
	 */
	public boolean doGetURLResponse(String reCAPTCHAToken){

		/** �߂�l���i�[����ϐ����쐬 */
		boolean returnval = failure;

	    /** properties�t�@�C���̏����擾 */
		ResourceBundle resource = ResourceBundle.getBundle("config");

		/** secret.properties�t�@�C���i�[�f�B���N�g���p�X */
		String secPropertiesFilePath = (String)(resource.getString("secretDir") + "secret.properties");

		/** secret.properties�I�u�W�F�N�g���쐬*/
		Properties secProperties = new Properties();

		/** secProperties�t�@�C���̏����擾 */
		try(Reader reader = new InputStreamReader(new FileInputStream(secPropertiesFilePath), resource.getString("fileCharSetName"))){
			secProperties.load(reader);
		} catch (UnsupportedEncodingException e1) {
			// TODO �����������ꂽ catch �u���b�N
			//e1.printStackTrace();
        	printLogger.error("secret.properties�t�@�C���G���R�[�f�B���O�����T�|�[�g�ł��B(UnsupportedEncodingException)");
			return failure;
		} catch (FileNotFoundException e1) {
			// TODO �����������ꂽ catch �u���b�N
			//e1.printStackTrace();
        	printLogger.error("secret.properties�t�@�C��������܂���B(FileNotFoundException)");
			return failure;
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			//e1.printStackTrace();
        	printLogger.error("secret.properties�t�@�C����ǂݍ��߂܂���B(IOException)");
			return failure;
		}

		try {
			/** reCAPTCHA�x���t�@�C�pURL���Z�b�g*/
//			URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
//			URL url = new URL(resource.getString("reCAPTCHAURL"));
			URL url = new URL(secProperties.getProperty("reCAPTCHAURL"));

			try {
				/** HTTP�R�l�N�V�������쐬
				 * �i�I�u�W�F�N�g�쐬��͎����I�ɃR�l�N�V�������쐬�����̂ŁA
				 * connect�����܂ł͎~�߂Ȃ��悤�ɂ���B
				 * ���A�ꎞ��~����ƁuIllegalStateException:Already Connected�v�G���[�ƂȂ�B */
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				/** ���̐ڑ��ŏo�͂��s���悤�ɐݒ� */
				conn.setDoOutput(true);

				/** ���N�G�X�g���\�b�h��POST�ɐݒ� */
				conn.setRequestMethod("POST");

				/** reCAPTCHA�F�؂̃��N�G�X�g�p�����[�^���Z�b�g���� */
				try(OutputStream out = conn.getOutputStream()){
					//out.write(("secret=" + resource.getString("reCAPTCHASecret")).getBytes());	//reCAPTCHA�̃V�[�N���b�g�L�[
					out.write(("secret=" + secProperties.getProperty("reCAPTCHASecret")).getBytes());	//reCAPTCHA�̃V�[�N���b�g�L�[
					out.write("&".getBytes());	//�����̃Z�p���[�^"&"
					out.write(("response=" + reCAPTCHAToken).getBytes());						//���[�U����n���ꂽreCAPTCHA�̃g�[�N��
				}

				/** Http Connect�Ŏw�肵��URL��POST */
				conn.connect();

				/** ���X�|���X�R�[�h���擾 */
				if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					try(InputStream in = conn.getInputStream();
	                    	InputStreamReader isr = new InputStreamReader(in, resource.getString("fileCharSetName"));
	                    	BufferedReader bufferedReader = new BufferedReader(isr);
	                		){

						/** ���X�|���X�R�[�h������̏ꍇ�Ƀ��X�|���X�ireCAPTCHA�F�،��ʁj���擾���� */
						String line = null;
						while((line = bufferedReader.readLine()) != null) {
							//System.out.println(line + "\r\n");
							if((line.indexOf("success") > -1) && (line.indexOf("true") > -1)) {
								//** ���X�|���X�f�[�^�� ["success": true]�̂P�s���܂ޏꍇ�� �߂�l�Ƃ���true��Ԃ�  */
								returnval = success;
								break;
							}
						}
					}
				}

				/** Http Connection��ؒf */
				conn.disconnect();

			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				//e.printStackTrace();
	        	printLogger.error("HTTP�R�l�N�V�����ŏ����擾�ł��܂���ł����B(IOException)");
				return failure;
			}
		} catch (MalformedURLException e) {
			// TODO �����������ꂽ catch �u���b�N
			//e.printStackTrace();
        	printLogger.error("URL�I�u�W�F�N�g���쐬�ł��܂���ł����B(MalformedURLException)");
			return failure;
		}

		/** ���ʂ�return���� */
		return returnval;
	}
}
