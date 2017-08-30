package org.fartpig.encodingchange.phase;

import java.io.File;
import java.util.Set;

import org.fartpig.encodingchange.constant.GlobalConst;
import org.fartpig.encodingchange.util.ChangeEncodingUtil;
import org.fartpig.encodingchange.util.ToolLogger;

public class ClearBOMAction {

	private static final String CURRENT_PHASE = GlobalConst.PHASE_CLEAR_BOM;

	public ClearBOMAction() {
		ToolLogger.getInstance().setCurrentPhase(CURRENT_PHASE);
	}

	public void clearBom(Set<String> filePaths) {
		// clear BOM header for original UTF-8 file
		for (String aFilePath : filePaths) {
			ChangeEncodingUtil.clearUTF8Mark(new File(aFilePath));
		}
	}

}
