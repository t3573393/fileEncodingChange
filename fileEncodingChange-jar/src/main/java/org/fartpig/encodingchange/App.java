package org.fartpig.encodingchange;

import java.io.File;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fartpig.encodingchange.constant.GlobalConfig;
import org.fartpig.encodingchange.constant.GlobalConst;
import org.fartpig.encodingchange.phase.ClearBOMAction;
import org.fartpig.encodingchange.phase.ConvertEncodingAction;
import org.fartpig.encodingchange.phase.EncodingDetectAction;
import org.fartpig.encodingchange.util.ToolException;
import org.fartpig.encodingchange.util.ToolLogger;

/**
 * application entry
 *
 */
public class App {

	public static GlobalConfig argsResolve(String[] args) {
		ToolLogger log = ToolLogger.getInstance();
		GlobalConfig config = GlobalConfig.instance();

		String intputPath = config.getInputPath();
		String outputPath = config.getOutputPath();
		String targetEncoding = config.getTargetEncoding();
		String localEncoding = config.getLocal();
		boolean needClearBOM = config.isNeedClearBOM();
		String[] extensions = config.getExtensions();

		Options options = new Options();

		Option encoding = new Option("e", "encoding", true, "target encoding");
		encoding.setRequired(false);
		options.addOption(encoding);

		Option local = new Option("l", "local", true, "local encoding");
		local.setRequired(false);
		options.addOption(local);

		Option exts = new Option("exts", "extensions", true, "file extensions");
		exts.setRequired(false);
		options.addOption(exts);

		Option noBom = new Option("nb", "nobom", false, "clean the bom header");
		noBom.setRequired(false);
		options.addOption(noBom);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			ToolLogger.getInstance().error("error", e);
			formatter.printHelp("utility-name", options);
			throw new ToolException(log.getCurrentPhase(), "please set the right args");
		}

		String[] argArray = cmd.getArgs();

		targetEncoding = cmd.getOptionValue("encoding", "UTF-8");
		config.setTargetEncoding(targetEncoding);

		String extsStr = cmd.getOptionValue("exts");
		config.fillExtensions(extsStr);

		localEncoding = cmd.getOptionValue("local", "GBK");
		config.setLocal(localEncoding);

		needClearBOM = Boolean.valueOf(cmd.getOptionValue("nobom", "true"));
		config.setNeedClearBOM(needClearBOM);

		if (argArray.length == 1) {
			intputPath = argArray[0];
			config.setInputPath(intputPath);
		} else if (argArray.length == 2) {
			intputPath = argArray[0];
			config.setInputPath(intputPath);

			outputPath = argArray[1];
			config.setOutputPath(outputPath);
		} else {
			throw new ToolException(log.getCurrentPhase(), "please set the right args");
		}

		return config;
	}

	public static void main(String[] args) {
		try {
			String phase = GlobalConst.PHASE_INIT_PARAMS;
			ToolLogger log = ToolLogger.getInstance();
			log.setCurrentPhase(phase);
			GlobalConfig config = argsResolve(args);
			invokeByGlobalConfig(config);

		} catch (Exception e) {
			ToolLogger.getInstance().error("error:", e);
		}
	}

	public static void invokeByGlobalConfig(GlobalConfig config) {

		String inputPath = config.getInputPath();
		String outputPath = config.getOutputPath();
		String targetEncoding = config.getTargetEncoding();
		String[] extensions = config.getExtensions();
		String localEncoding = config.getLocal();
		boolean needClearBOM = config.isNeedClearBOM();

		// match the input the output
		if (outputPath == null) {
			outputPath = inputPath;
		} else {
			File inputFile = new File(inputPath);
			File outputFile = new File(outputPath);
			if (!inputFile.isDirectory()) {
				if (outputFile.isDirectory()) {
					outputPath = String.format("%s%s%s", outputFile, File.separator, inputFile.getName());
				}
			}
		}

		// add dir suffix
		File inputFile = new File(inputPath);
		File outputFile = new File(outputPath);
		if (inputFile.isDirectory() && inputPath.charAt(inputPath.length() - 1) != File.separatorChar) {
			inputPath = inputPath + File.separator;
		}

		if (outputFile.isDirectory() && outputPath.charAt(outputPath.length() - 1) != File.separatorChar) {
			outputPath = outputPath + File.separator;
		}

		ToolLogger.getInstance().warning("match inputPath:" + inputPath + " - outputPath:" + outputPath);

		// detact file encoding
		EncodingDetectAction encodingDetact = new EncodingDetectAction();
		Map<String, String> fileEncodings = encodingDetact.detectFiles(inputPath, extensions);

		// clear the bom header
		if (needClearBOM) {
			ClearBOMAction clearBomAction = new ClearBOMAction();
			clearBomAction.clearBom(fileEncodings.keySet());
		}

		// convert files
		ConvertEncodingAction convertEncoding = new ConvertEncodingAction();
		convertEncoding.convertFileEncodings(fileEncodings, inputPath, outputPath, localEncoding, targetEncoding);
	}

}
