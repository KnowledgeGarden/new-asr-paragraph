/**
 * 
 */
package org.topicquests.newasr.test;

import org.topicquests.newasr.ASRParagraphEnvironment;
import org.topicquests.newasr.api.IAsrParagraphModel;

/**
 * @author jackpark
 *
 */
public class TestingRoot {
	protected ASRParagraphEnvironment environment;
	protected IAsrParagraphModel model;

	/**
	 * 
	 */
	public TestingRoot() {
		environment = new ASRParagraphEnvironment();
		model = environment.getModel();
	}

}
