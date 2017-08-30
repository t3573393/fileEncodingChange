package org.fartpig.encodingchange;

import org.fartpig.encodingchange.constant.GlobalConst;
import org.fartpig.encodingchange.util.ToolException;
import org.fartpig.encodingchange.util.ToolLogger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	@Override
	public void setUp() {
		ToolLogger.getInstance().setCurrentPhase(GlobalConst.PHASE_INIT_PARAMS);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testAppEncoding1() {
		App app = new App();
		String[] args = { "-e", "UTF-8", "D:\\main\\src" };
		app.main(args);
	}

	public void testAppEncoding2() {
		App app = new App();
		String[] args = { "-e", "UTF-8", "D:\\main\\src" };
		app.main(args);
	}

	public void testAppEncoding3() {
		App app = new App();
		String[] args = { "-encoding", "UTF-8", "D:\\main\\src\\main\\java" };
		app.main(args);
	}

	public void testAppEncoding4() {
		App app = new App();
		String[] args = { "-encoding", "UTF-8", "D:\\main\\src", "D:\\linfeng_test\\" };
		app.main(args);
	}

	public void testAppExtensions1() {
		App app = new App();
		String[] args = { "-exts", "java,jsp",
				"D:\\workspace-my\\incrementArchive\\incrementArchive-jar\\target\\classes",
				"D:\\workspace-my\\incrementArchive\\target\\incrementArchive-target" };
		app.main(args);
	}

	public void testAppExtensions2() {
		App app = new App();
		String[] args = { "-extensions", "java,jsp",
				"D:\\workspace-my\\incrementArchive\\incrementArchive-jar\\target\\classes",
				"D:\\workspace-my\\incrementArchive\\target\\incrementArchive-target" };
		app.main(args);
	}

	public void testAppArgs1() {
		App app = new App();
		String[] args = { "D:\\workspace-my\\incrementArchive\\incrementArchive-jar\\target\\classes",
				"D:\\workspace-my\\incrementArchive\\target\\incrementArchive-target" };
		app.main(args);
	}

	public void testAppArgs2() {
		App app = new App();
		String[] args = { "D:\\workspace-my\\incrementArchive\\target\\incrementArchive-target" };
		app.main(args);
	}

	public void testAppArgs3() {
		App app = new App();
		try {
			String[] args = {};
			app.argsResolve(args);
			fail("Expected a ToolException to be throw");
		} catch (ToolException e) {
			e.printStackTrace();
			assertEquals(GlobalConst.PHASE_INIT_PARAMS, e.getPhase());
			assertEquals("please set the right args", e.getMessage());
		}
	}

}
