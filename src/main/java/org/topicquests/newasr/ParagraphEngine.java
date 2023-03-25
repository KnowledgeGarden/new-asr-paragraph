/**
 * 
 */
package org.topicquests.newasr;

import java.util.ArrayList;
import java.util.List;

import org.topicquests.newasr.api.IAsrParagraphModel;
import org.topicquests.newasr.api.ISentence;
import org.topicquests.newasr.impl.ASRSentence;
import org.topicquests.newasr.kafka.SentenceProducer;
import org.topicquests.newasr.util.JsonUtil;
import org.topicquests.os.asr.driver.sp.SpacyDriverEnvironment;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonObject;

/**
 * @author jackpark
 *
 */
public class ParagraphEngine {
	private ASRParagraphEnvironment environment;
	private IAsrParagraphModel model;
	private JsonUtil util;
	private List<JsonObject> sentences;
	private boolean IS_RUNNING = true;
	private SentenceThread runner;
	private SpacyDriverEnvironment spacyServerEnvironment;
	private SentenceProducer sentenceProducer;
	
	private final String SENTENCE_TOPIC, SPACY_TOPIC;

	/**
	 * 
	 */
	public ParagraphEngine(ASRParagraphEnvironment env) {
		environment =env;
		model = environment.getModel();
		sentences = new ArrayList<JsonObject>();
		util = new JsonUtil();
		spacyServerEnvironment = environment.getSpacyServerEnvironment();
		sentenceProducer = environment.getSentenceProducer();
		String pTopic = (String)environment.getKafkaTopicProperties().get("SentenceProducerTopic");
		SENTENCE_TOPIC = pTopic;
		pTopic = (String)environment.getKafkaTopicProperties().get("SentenceSpacyProducerTopic");
		SPACY_TOPIC = pTopic;
	}

	public void startProcessing() {
		IS_RUNNING = true;
		runner = new SentenceThread();
		runner.start();
	}
	
	
	/**
	 * Process a {@code sentence}
	 * @param sentence
	 * @return
	 */
	public IResult processSentence(ISentence sentence) {
		IResult result = new ResultPojo();
		//In theory, sentence arrives as a string and sentenceId
		//First, send it to the spacy Server
		return result;
				
	}
	
	class SentenceThread extends Thread {
		
		public void run() {
			JsonObject sent = null;
			while (IS_RUNNING) {
				synchronized(sentences) {
					while (sentences.isEmpty()) {
						try {
							sentences.wait();
						} catch (Exception e) {}
					}
					sent = sentences.remove(0);
				}
				if (sent != null) {
					ISentence s = new ASRSentence(sent);
					processSentence(s);
				}
			}
		}
	}
	public void shutDown() {
		synchronized(sentences)  {
			IS_RUNNING = false;
			sentences.notify();
		}
	}
}
