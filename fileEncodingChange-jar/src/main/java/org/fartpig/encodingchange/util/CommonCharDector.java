package org.fartpig.encodingchange.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class CommonCharDector {

	private static final String urlPrefix = "common-char/";
	private Map<String, List<Byte[]>> commonCharBytes = new HashMap<String, List<Byte[]>>();

	public void loadCommonChars() {
		SortedMap<String, Charset> availableCharsets = Charset.availableCharsets();
		for (String charsetName : availableCharsets.keySet()) {
			String lowerCharsetName = charsetName.toLowerCase();
			String resourceName = String.format("%s%s.txt", urlPrefix, lowerCharsetName);
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
			if (inputStream != null) {
				try {
					List<String> contents = IOUtils.readLines(inputStream, charsetName);
					List<Byte[]> byteLists = new ArrayList<Byte[]>();
					for (String content : contents) {
						byte[] bytes = content.getBytes(charsetName);
						Byte[] tempBytes = new Byte[bytes.length];
						int i = 0;
						for (byte b : bytes) {
							tempBytes[i] = b;
							i++;
						}
						System.out.print(content + "-" + charsetName);
						printBytes(bytes);
						byteLists.add(tempBytes);
					}
					commonCharBytes.put(charsetName, byteLists);
				} catch (IOException e) {
					ToolLogger.getInstance().error("error", e);
				}
			}
		}
	}

	public boolean detactEncoding(File file, String charsetName) {
		// 全部载入到内存中进行比对
		List<Byte[]> byteContents = commonCharBytes.get(charsetName);
		byte[] allBytes = null;
		try {
			allBytes = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ToolLogger.getInstance().error("error", e);
			return false;
		}
		int length = allBytes.length;
		for (Byte[] aBytes : byteContents) {
			int lengthB = aBytes.length;
			if (lengthB > length) {
				continue;
			}

			for (int i = 0; i < (length - lengthB + 1); i++) {
				boolean isMath = true;
				int k = i;
				for (int j = 0; j < lengthB; j++) {
					if (allBytes[k] != aBytes[j].byteValue()) {
						isMath = false;
						break;
					}
					k++;
				}

				if (isMath) {
					return true;
				}
			}
		}

		return false;
	}

	private void printBytes(byte[] bytes) {
		System.out.print("[");
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(bytes[i]);
		}
		System.out.println("]");
	}

}
