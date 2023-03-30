/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.newasr.api;

import org.topicquests.support.api.IResult;

import com.google.gson.JsonObject;

/**
 * @author jackpark
 *
 */
public interface IAsrParagraphModel {

	/**
	 * Process a given {@code sentence}
	 * @param paragraph
	 * @return
	 */
	IResult processParagraph(String paragraph);
	

	
	////////////////////////////
	// Kafka handling
	////////////////////////////
	
	/**
	 * After spaCy processes a sentence
	 * @param sentence
	 * @return TODO
	 */
	boolean acceptSpacyResponse(JsonObject sentence);
	
	/**
	 * From the paragraph agent
	 * @param sentence
	 * @return TODO
	 */
	boolean acceptNewSentence(JsonObject sentence);
}
