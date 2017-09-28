package org.fartpig.encodingchange.phase;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.fartpig.encodingchange.constant.GlobalConst;
import org.fartpig.encodingchange.util.CharacterSetUtil;
import org.fartpig.encodingchange.util.ToolLogger;

public class EncodingDetectAction {

	private static final String CURRENT_PHASE = GlobalConst.PHASE_CHANGE_LOG_FETCH;

	public EncodingDetectAction() {
		ToolLogger.getInstance().setCurrentPhase(CURRENT_PHASE);
	}

	public Map<String, String> detectFiles(String inputPath, String[] extensions, String targetEncoding) {
		Map<String, String> fileEncodings = new HashMap<String, String>();

		File file = new File(inputPath);
		if (file.isDirectory()) {
			Collection<File> allFiles = FileUtils.listFiles(file, extensions, true);
			for (File aFile : allFiles) {
				String encoding = CharacterSetUtil.detectFile(aFile, targetEncoding);
				if (encoding != null) {
					fileEncodings.put(aFile.getAbsolutePath(), encoding);
					ToolLogger.getInstance().warning("file:" + aFile.getAbsolutePath() + " - encoding:" + encoding);
				}
			}
		} else {
			String encoding = CharacterSetUtil.detectFile(file, targetEncoding);
			if (encoding != null) {
				fileEncodings.put(file.getAbsolutePath(), encoding);
				ToolLogger.getInstance().warning("file:" + file.getAbsolutePath() + " - encoding:" + encoding);
			}
		}

		return fileEncodings;
	}

}
