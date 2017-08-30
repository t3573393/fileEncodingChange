package org.fartpig.encodingchange.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.fartpig.encodingchange.constant.GlobalConst;
import org.mozilla.universalchardet.UniversalDetector;

public class CharacterSetUtil {

	private static String[] GBSets = { "GB18030", "GB2312", "GBK" };

	public static boolean isUnionSet(String srcCharset, String targetCharset) {
		if (srcCharset.equalsIgnoreCase(targetCharset)) {
			return true;
		}

		if (Arrays.asList(GBSets).contains(srcCharset) && Arrays.asList(GBSets).contains(targetCharset)) {
			return true;
		}

		return false;
	}

	public static boolean isCharacterContain(String superCharset, String subCharset) {
		if (superCharset == null) {
			return false;
		}
		String[] strArray = superCharset.split(GlobalConst.CHARSET_SEPARATOR);
		for (String aStr : strArray) {
			if (isUnionSet(aStr, subCharset)) {
				return true;
			}
		}

		return false;

	}

	public static String detectFile(File aFile) {
		String encodingA = detectFileWithNsDector(aFile);
		String encodingB = detectFileWithUniversalchardet(aFile);
		// TODO 增加新的库，提高识别率

		// choose the valid one
		if (encodingA == null) {
			return encodingB;
		}

		if (encodingB == null) {
			return encodingA;
		}

		// the same one
		if (encodingA.equalsIgnoreCase(encodingB)) {
			return encodingB;
		}

		String[] charArrays = encodingA.split("[,]");
		// only one use the B
		if (charArrays.length == 1) {
			if (!encodingA.equalsIgnoreCase(encodingB)) {
				return encodingB;
			}
		} else {
			// A contains B, use A
			for (String aChar : charArrays) {
				if (aChar.equalsIgnoreCase(encodingB)) {
					return encodingA;
				}
			}
		}

		ToolLogger.getInstance().warning("fileA:--" + aFile.getAbsolutePath() + " - encoding:" + encodingA);
		ToolLogger.getInstance().warning("fileB:--" + aFile.getAbsolutePath() + " - encoding:" + encodingB);

		return null;
	}

	public static String detectFileWithUniversalchardet(File aFile) {
		UniversalDetector detector = new UniversalDetector(null);

		byte[] buf = new byte[4096];
		FileInputStream fis = null;

		int nread;
		try {
			fis = new FileInputStream(aFile);
			while (((nread = fis.read(buf)) > 0 && !detector.isDone())) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();
			String encoding = detector.getDetectedCharset();
			detector.reset();
			ToolLogger.getInstance()
					.info("detectFileWithUniversalchardet file:" + aFile.getAbsolutePath() + " - encoding:" + encoding);
			return encoding;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				IOUtils.closeQuietly(fis);
			}
		}

		return null;
	}

	public static String detectFileWithNsDector(File aFile) {
		try {
			String encoding = new FileCharsetDetector().guessFileEncoding(aFile);
			ToolLogger.getInstance()
					.info("detectFileWithNsDector file:" + aFile.getAbsolutePath() + " - encoding:" + encoding);
			return encoding;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
