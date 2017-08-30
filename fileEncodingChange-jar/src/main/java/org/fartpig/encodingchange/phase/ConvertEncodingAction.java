package org.fartpig.encodingchange.phase;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.fartpig.encodingchange.constant.GlobalConst;
import org.fartpig.encodingchange.entity.FileEncodingInfo;
import org.fartpig.encodingchange.util.ChangeEncodingUtil;
import org.fartpig.encodingchange.util.CharacterSetUtil;
import org.fartpig.encodingchange.util.ToolLogger;

public class ConvertEncodingAction {

	private static final String CURRENT_PHASE = GlobalConst.PHASE_CONVERT_ENCODING;

	public ConvertEncodingAction() {
		ToolLogger.getInstance().setCurrentPhase(CURRENT_PHASE);
	}

	public void convertFileEncodings(Map<String, String> fileEncodings, String inputPath, String outputPath,
			String localEncoding, String targetEncoding) {

		Map<String, FileEncodingInfo> fileConvertMap = resolveFileEncoding(fileEncodings, localEncoding,
				targetEncoding);
		// TODO 这里需要好好优化一下 路径处理的问题， 找一个库，或者是 找个方法
		File inputFile = new File(inputPath);
		File outputFile = new File(outputPath);

		for (Map.Entry<String, FileEncodingInfo> encodingEntry : fileConvertMap.entrySet()) {
			// convert original file to temp file
			String temp = UUID.randomUUID().toString();

			String relativePath = "";
			String handleFilePath = encodingEntry.getKey();
			int relativeIndex = handleFilePath.indexOf(inputFile.getAbsolutePath());
			if (relativeIndex != -1) {
				int parentFileIndex = handleFilePath.lastIndexOf(File.separatorChar);
				if (parentFileIndex != -1) {
					int offset = inputFile.getAbsolutePath().length();
					if (offset < (parentFileIndex + 1)) {
						relativePath = handleFilePath.substring(offset + 1, parentFileIndex + 1);
						ToolLogger.getInstance().info("relativePath:" + relativePath);
					}
				}
			}

			String tempOutputDir = null;
			if (relativePath.length() == 0 || File.separator.equals(relativePath) || ".".equals(relativePath)) {
				tempOutputDir = outputPath;
			} else {
				tempOutputDir = String.format("%s%s", outputPath, relativePath);
			}

			String tempOutputPath = String.format("%s%s", tempOutputDir, temp);
			if (inputFile.isFile()) {
				tempOutputPath = String.format("%s%s%s", outputFile.getParent(), File.separator, temp);
			}

			File tempDir = new File(tempOutputDir);
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}

			FileEncodingInfo fileEncodingInfo = encodingEntry.getValue();
			if (fileEncodingInfo.isLocalEncoding()) {
				ChangeEncodingUtil.handleFile(encodingEntry.getKey(), tempOutputPath, localEncoding, targetEncoding,
						false);
			} else {
				// get the first no target encoding one, just for simple
				String[] encodings = fileEncodingInfo.getFileEncoding().split(GlobalConst.CHARSET_SEPARATOR);

				for (String aEncoding : encodings) {
					if (!CharacterSetUtil.isUnionSet(aEncoding, targetEncoding)) {
						ChangeEncodingUtil.handleFile(encodingEntry.getKey(), tempOutputPath, aEncoding, targetEncoding,
								false);
						break;
					}
				}
			}

			// verify the temp file
			String encoding = CharacterSetUtil.detectFile(new File(tempOutputPath));
			ToolLogger.getInstance().info("change result encoding:" + encoding);
			// use the super set model
			if (encoding != null && CharacterSetUtil.isCharacterContain(encoding, targetEncoding)) {
				// if right then rename the temp file
				File origFile = new File(encodingEntry.getKey());
				File tempFile = new File(tempOutputPath);

				String fileName = origFile.getName();
				String targetFilePath = null;
				if (relativePath.length() == 0 || File.separator.equals(relativePath) || ".".equals(relativePath)) {
					targetFilePath = String.format("%s%s", outputPath, fileName);
				} else {
					targetFilePath = String.format("%s%s%s%s%s", outputPath, File.separator, relativePath,
							File.separator, fileName);
				}

				if (inputFile.isFile()) {
					targetFilePath = outputPath;
				}

				ToolLogger.getInstance().warning("file:" + encodingEntry.getKey() + " - rename to:" + targetFilePath);

				File targetFile = new File(targetFilePath);
				if (targetFile.exists()) {
					targetFile.delete();
				}
				tempFile.renameTo(targetFile);

			} else {
				ToolLogger.getInstance().info("verify false remove temp file:" + tempOutputPath);

				File tempFile = new File(tempOutputPath);
				if (tempFile.exists()) {
					tempFile.delete();
				}
			}
		}

	}

	private Map<String, FileEncodingInfo> resolveFileEncoding(Map<String, String> fileEncodings, String localEncoding,
			String targetEncoding) {
		// filter the right encoding
		Map<String, String> workMap = new HashMap<String, String>();
		for (Map.Entry<String, String> encodingEntry : fileEncodings.entrySet()) {
			if (!encodingEntry.getValue().equalsIgnoreCase(targetEncoding)) {
				workMap.put(encodingEntry.getKey(), encodingEntry.getValue());
			}
		}

		Map<String, FileEncodingInfo> fileConvertMap = new HashMap<String, FileEncodingInfo>();
		for (Map.Entry<String, String> encodingEntry : workMap.entrySet()) {
			String[] encodings = encodingEntry.getValue().split(GlobalConst.CHARSET_SEPARATOR);

			// local set in arrays to handle
			boolean isLocal = false;
			for (String aEncoding : encodings) {
				if (CharacterSetUtil.isUnionSet(aEncoding, localEncoding)) {
					isLocal = true;
					ToolLogger.getInstance()
							.warning("local charset:" + encodingEntry.getKey() + " - " + encodingEntry.getValue());
					FileEncodingInfo fileEncodingInfo = new FileEncodingInfo();
					fileEncodingInfo.setFileEncoding(encodingEntry.getValue());
					fileEncodingInfo.setFileName(encodingEntry.getKey());
					fileEncodingInfo.setLocalEncoding(true);

					fileConvertMap.put(encodingEntry.getKey(), fileEncodingInfo);
					break;
				}
			}

			// other encoding set
			if (!isLocal && !Arrays.asList(encodings).contains(targetEncoding)) {
				ToolLogger.getInstance()
						.warning("other charset:" + encodingEntry.getKey() + " - " + encodingEntry.getValue());
				FileEncodingInfo fileEncodingInfo = new FileEncodingInfo();
				fileEncodingInfo.setFileEncoding(encodingEntry.getValue());
				fileEncodingInfo.setFileName(encodingEntry.getKey());
				fileEncodingInfo.setLocalEncoding(false);

				fileConvertMap.put(encodingEntry.getKey(), fileEncodingInfo);
			}
		}

		return fileConvertMap;
	}

}
