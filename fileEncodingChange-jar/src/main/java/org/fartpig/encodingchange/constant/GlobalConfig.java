package org.fartpig.encodingchange.constant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.fartpig.encodingchange.util.PathUtil;
import org.fartpig.encodingchange.util.StringUtil;
import org.fartpig.encodingchange.util.ToolLogger;

public class GlobalConfig {

	private static final String appRootPath = PathUtil.getProjectPath();
	private static GlobalConfig globalConfig = null;

	public static GlobalConfig instance() {
		if (globalConfig == null) {
			globalConfig = new GlobalConfig("tools.properties");
		}
		return globalConfig;
	}

	public static GlobalConfig instanceByFile(File file) {
		if (globalConfig == null) {
			if (file != null) {
				globalConfig = new GlobalConfig(file);
			} else {
				globalConfig = new GlobalConfig();
			}
		}
		return globalConfig;
	}

	private String inputPath = null;
	private String outputPath = null;
	private String targetEncoding = null;
	private boolean needClearBOM = true;
	private String[] extensions = { "java", "jsp", "html" };
	private String local = null;

	private GlobalConfig() {
	}

	private GlobalConfig(File configFile) {
		Properties configProperties = new Properties();
		try {
			configProperties.load(new BufferedInputStream(new FileInputStream(configFile)));

		} catch (FileNotFoundException e) {
			ToolLogger.getInstance().error("error:", e);
		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}
		fillDataByProperties(configProperties);
	}

	private GlobalConfig(String configName) {
		Properties configProperties = new Properties();
		try {
			configProperties
					.load(new BufferedInputStream(new FileInputStream(appRootPath + File.separator + configName)));

		} catch (FileNotFoundException e) {
			ToolLogger.getInstance().error("error:", e);
		} catch (IOException e) {
			ToolLogger.getInstance().error("error:", e);
		}
		fillDataByProperties(configProperties);
	}

	private void fillDataByProperties(Properties configProperties) {

		inputPath = configProperties.getProperty("intputPath", inputPath);
		outputPath = configProperties.getProperty("outputPath", outputPath);
		targetEncoding = configProperties.getProperty("targetEncoding", targetEncoding);
		String extensionsStr = configProperties.getProperty("extensions", StringUtil.join(",", extensions));
		extensions = extensionsStr.split("[,]");
		local = configProperties.getProperty("local", local);
		needClearBOM = Boolean.valueOf(configProperties.getProperty("needClearBOM", "true"));

		ToolLogger log = ToolLogger.getInstance();
		log.info("intputPath:" + inputPath);
		log.info("outputPath:" + outputPath);
		log.info("targetEncoding:" + targetEncoding);
		log.info("extensions:" + extensionsStr);
		log.info("needClearBOM:" + needClearBOM);

	}

	public static GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public static void setGlobalConfig(GlobalConfig globalConfig) {
		GlobalConfig.globalConfig = globalConfig;
	}

	public static String getApprootpath() {
		return appRootPath;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getTargetEncoding() {
		return targetEncoding;
	}

	public void setTargetEncoding(String targetEncoding) {
		this.targetEncoding = targetEncoding;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public void fillExtensions(String extensionsStr) {
		if (extensionsStr == null || extensionsStr.length() == 0) {
			return;
		}

		extensions = extensionsStr.split("[,]");
	}

	public String[] getExtensions() {
		return extensions;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	public boolean isNeedClearBOM() {
		return needClearBOM;
	}

	public void setNeedClearBOM(boolean needClearBOM) {
		this.needClearBOM = needClearBOM;
	}

}
