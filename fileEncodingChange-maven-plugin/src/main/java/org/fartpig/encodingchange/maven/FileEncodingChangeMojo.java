package org.fartpig.encodingchange.maven;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.fartpig.encodingchange.constant.GlobalConfig;
import org.fartpig.encodingchange.maven.util.Constants;
import org.fartpig.encodingchange.maven.util.StringUtils;

import com.google.common.base.Throwables;

@Mojo(name = "convert", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDirectInvocation = true, threadSafe = false)
public class FileEncodingChangeMojo extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info(Constants.PLUGIN_ID + " - resolve");
		try {

			GlobalConfig globalConfig = GlobalConfig.instanceByFile(globalConfigPropertyFile);

			globalConfig.setInputPath(inputPath);

			if (!StringUtils.isEmpty(outputPath)) {
				globalConfig.setOutputPath(outputPath);
			}

			globalConfig.setTargetEncoding(targetEncoding);
			globalConfig.fillExtensions(extensionsStr);

			if (!StringUtils.isEmpty(local)) {
				globalConfig.setLocal(local);
			}

			getLog().info(Constants.PLUGIN_ID + " - convert - end");

		} catch (Throwable t) {
			if (failOnError) {
				throw new MojoFailureException("execute fail ", t);
			} else {
				getLog().error("##############  Exception occurred during resolve libs  ###############");
				getLog().error(Throwables.getStackTraceAsString(t));
			}
		}
	}

	@Parameter(defaultValue = "${project}", readonly = true)
	protected MavenProject project;

	@Component
	protected MavenProjectHelper projectHelper;

	@Parameter(defaultValue = "${plugin.artifacts}")
	protected List<Artifact> pluginArtifacts;

	@Parameter(defaultValue = "${project.basedir}/tools.properties")
	protected File globalConfigPropertyFile;

	@Parameter(required = true)
	protected String inputPath;

	@Parameter
	protected String outputPath;

	@Parameter(required = true)
	protected String targetEncoding;

	@Parameter
	protected String extensionsStr;

	@Parameter(defaultValue = "GBK")
	protected String local;

	@Parameter
	protected boolean needClearBOM = true;

	@Parameter(defaultValue = "false")
	protected boolean failOnError;

}
