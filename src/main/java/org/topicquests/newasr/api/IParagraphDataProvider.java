/**
 * 
 */
package org.topicquests.newasr.api;

import org.topicquests.support.api.IResult;


/**
 * @author jackpark
 *
 */
public interface IParagraphDataProvider {

	IResult putParagraph(IParagraph para);
	
	IResult updateParagraph(IParagraph para);

	IResult getParagraph(long paraId);
	
	IResult removeParagraph(long paraId);


}
