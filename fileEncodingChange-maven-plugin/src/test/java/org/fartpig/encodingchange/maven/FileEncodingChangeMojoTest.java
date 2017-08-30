package org.fartpig.encodingchange.maven;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class FileEncodingChangeMojoTest extends AbstractMojoTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAppRun() throws Exception {
		File pom = getTestFile("src/test/resources/test-pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		FileEncodingChangeMojo mojo = (FileEncodingChangeMojo) lookupMojo("convert", pom);
		assertNotNull(mojo);
		mojo.execute();

	}
}
