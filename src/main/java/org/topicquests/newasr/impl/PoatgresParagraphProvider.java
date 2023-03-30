/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.newasr.impl;

import org.topicquests.newasr.ASRParagraphEnvironment;
import org.topicquests.newasr.api.IParagraph;
import org.topicquests.newasr.api.IParagraphDataProvider;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class PoatgresParagraphProvider implements IParagraphDataProvider {
	private ASRParagraphEnvironment environment;

	/**
	 * 
	 */
	public PoatgresParagraphProvider(ASRParagraphEnvironment env) {
		environment  = env;
	}

	@Override
	public IResult putParagraph(IParagraph para) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult updateParagraph(IParagraph para) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult getParagraph(long paraId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResult removeParagraph(long paraId) {
		// TODO Auto-generated method stub
		return null;
	}

}
