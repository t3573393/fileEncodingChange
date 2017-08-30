package org.fartpig.encodingchange.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.fartpig.encodingchange.constant.GlobalConst;

/*
 * Author: beifeng600
 * 批量转换文件编码
 * // UTF-16、UTF-16BE、UTF-16LE编码方式的区别 
 * // http://jiangzhengjun.iteye.com/blog/622589
 */

public class ChangeEncodingUtil {

	public static String handleFile(String filePath, String resPath, String inEncoding, String outEncoding,
			boolean append) {
		ToolLogger.getInstance().warning("handleFile filePath:" + filePath + " - outputPath:" + resPath
				+ " - inEncoding:" + inEncoding + " - outEncoding:" + outEncoding);

		List<String> sent_list = new ArrayList<String>();

		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, inEncoding);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			if (resPath == null || "".equalsIgnoreCase(resPath)) {
				resPath = filePath.substring(0, filePath.lastIndexOf('.')) + "_output"
						+ filePath.substring(filePath.lastIndexOf('.'));
			}

			FileOutputStream fileOutputStream = new FileOutputStream(resPath, append);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, outEncoding);
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

			String strLine = null;
			int lineNum = 0;
			while ((strLine = bufferedReader.readLine()) != null) {
				lineNum += 1;
				// if("".equalsIgnoreCase(strLine.trim())){
				// continue;
				// }
				sent_list.add(strLine);
			}

			for (int index = 0; index < sent_list.size(); ++index) {
				String transform_encoding = transform_encoding(sent_list.get(index), outEncoding, index);
				bufferedWriter.write(transform_encoding);
				bufferedWriter.newLine();
			}

			bufferedWriter.flush();
			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();

			bufferedReader.close();
			inputStreamReader.close();
			fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static String transform_encoding(String UTF16BE_str, String dst_encoding, int lineNum) {

		boolean is_first_line = false;

		if (lineNum == 1) {
			is_first_line = true;
		}

		try {
			if (GlobalConst.GBK.equalsIgnoreCase(dst_encoding)) {
				return UTF16BE_str;
			} else if (GlobalConst.UTF8.equalsIgnoreCase(dst_encoding)) {

				return UTF16BE_str;

			} else if (GlobalConst.UTF16BE.equalsIgnoreCase(dst_encoding)) {
				byte[] headByte = { (byte) 0xFE, (byte) 0xFF };

				byte[] lineBytes = UTF16BE_str.getBytes("UTF-16BE");

				if (is_first_line) {
					return new String(lineBytes, "UTF-16BE");
				}

				byte[] copyArray = new byte[lineBytes.length + 2];

				System.arraycopy(lineBytes, 0, copyArray, 2, lineBytes.length);
				copyArray[0] = headByte[0];
				copyArray[1] = headByte[1];

				return new String(copyArray, "UTF-16BE");

			} else if (GlobalConst.UTF16LE.equalsIgnoreCase(dst_encoding)) {
				byte[] headByte = { (byte) 0xFF, (byte) 0xFE };

				byte[] lineBytes = UTF16BE_str.getBytes("UTF-16BE");

				for (int i = 0; i < lineBytes.length - 1; i += 2) {
					byte temp_byte = lineBytes[i];
					lineBytes[i] = lineBytes[i + 1];
					lineBytes[i + 1] = temp_byte;
				}

				if (is_first_line) {
					return new String(lineBytes, "UTF-16LE");
				}

				byte[] copyArray = new byte[lineBytes.length + 2];
				System.arraycopy(lineBytes, 0, copyArray, 2, lineBytes.length);
				copyArray[0] = headByte[0];
				copyArray[1] = headByte[1];

				return new String(copyArray, "UTF-16LE");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static void clearUTF8Mark(File file) {
		// 调整日志方式
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				clearUTF8Mark(f);
			}
		} else {
			FileInputStream fis = null;
			InputStream is = null;
			ByteArrayOutputStream baos = null;
			OutputStream out = null;
			try {
				fis = new FileInputStream(file);
				is = new BufferedInputStream(fis);
				baos = new ByteArrayOutputStream();
				byte b[] = new byte[3];
				is.read(b);
				if (-17 == b[0] && -69 == b[1] && -65 == b[2]) {
					ToolLogger.getInstance().info("file path:" + file.getAbsolutePath());
					b = new byte[1024];
					while (true) {
						int bytes = 0;
						try {
							bytes = is.read(b);
						} catch (IOException e) {
						}
						if (bytes == -1) {
							break;
						}
						baos.write(b, 0, bytes);
						b = baos.toByteArray();
					}
					file.delete();
					out = new FileOutputStream(file);
					baos.writeTo(out);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ToolLogger.getInstance().error("error:", e);
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (out != null) {
						out.close();
					}
					if (is != null) {
						is.close();
					}
					if (baos != null) {
						baos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					ToolLogger.getInstance().error("error:", e);
				}
			}
		}
	}

}
