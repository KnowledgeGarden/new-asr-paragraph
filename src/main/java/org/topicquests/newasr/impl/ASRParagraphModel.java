/**
 * 
 */
package org.topicquests.newasr.impl;


import org.topicquests.newasr.ASRParagraphEnvironment;
import org.topicquests.newasr.api.IParagraphDataProvider;
import org.topicquests.newasr.api.IAsrParagraphModel;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonObject;

/**
 * @author jackpark
 *
 */
public class ASRParagraphModel implements IAsrParagraphModel {
	private ASRParagraphEnvironment environment;
	private IParagraphDataProvider database;
	private final int CACHE_SIZE = 8192;

	/**
	 * 
	 */
	public ASRParagraphModel(ASRParagraphEnvironment e) {
		environment = e;
		database = environment.getDatabase();
	}

	///////////////////////////////
	// A sentence is broken into a word array, and from there, into WordGram instances
	// A sentence is passed to external agents to identify:
	//		DBpedia entries --> JSON structures for hits of entities
	//		Wikidata identifiers
	//		Eventually - if needed
	//			spaCy models for parse trees
	//	Individual WordGram sequences which are found to be noun or verb phrases are replaced
	//		with their phrase WordGram equivalent
	////////////////////////////////
	@Override
	public IResult processParagraph(String paragraph) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}



	@Override
	public boolean acceptSpacyResponse(JsonObject sentence) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean acceptNewSentence(JsonObject sentence) {
		// TODO Auto-generated method stub
		return false;
	}
}
