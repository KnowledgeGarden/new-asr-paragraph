/**
 * 
 */
package org.topicquests.newasr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.topicquests.newasr.api.IAsrParagraphModel;
import org.topicquests.newasr.api.ISentence;
import org.topicquests.newasr.impl.ASRSentence;
import org.topicquests.newasr.kafka.SentenceProducer;
import org.topicquests.newasr.util.JsonUtil;
import org.topicquests.os.asr.driver.sp.SpacyDriverEnvironment;
import org.topicquests.os.asr.pd.api.ISentenceParser;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author jackpark
 *
 */
public class ParagraphEngine {
	private ASRParagraphEnvironment environment;
	private IAsrParagraphModel model;

	private JsonUtil util;
	private List<String> paragraphs;
	private boolean IS_RUNNING = true;
	private ParagraphThread runner;
	private SpacyDriverEnvironment spacyServerEnvironment;
	private SentenceProducer sentenceProducer;
	
	private final String SENTENCE_TOPIC, SPACY_TOPIC;

	/**
	 * 
	 */
	public ParagraphEngine(ASRParagraphEnvironment env) {
		environment =env;
		model = environment.getModel();
		paragraphs = new ArrayList<String>();
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
		runner = new ParagraphThread();
		runner.start();
	}
	
	
	/**
	 * Process a {@code paragraph}
	 * @param paragrapjh
	 * @return
	 */
	public IResult processParagraph(String paragraph) {
		IResult result = new ResultPojo();
		System.out.println("Processing\n"+paragraph);
		IResult r = spacyServerEnvironment.processParagraph(paragraph);
		System.out.println("Processed "+r.getErrorString()+"\n"+r.getResultObject());
		environment.logError("Processed\n"+r.getResultObject(), null);
		List<String> stuff = (List<String>)r.getResultObject();
		String json;
		Set<JsonObject> allConcepts = new HashSet<JsonObject>();
		List<JsonArray> allSentences = new ArrayList<JsonArray>();
		if (stuff != null && !stuff.isEmpty()) {
			int len = stuff.size();

			Set<String> keys;
			JsonObject jo =null;
			JsonObject temp;
			JsonArray ja;
			for (int i=0;i<len;i++) {
				json = stuff.get(i);
				try {
					jo = util.parse(json);
				} catch (Exception e) {
					environment.logError(e.getMessage(), e);
					e.printStackTrace();
				}

				keys = jo.keySet();
				//[concepts, markup, model, sentences]
				// we are interested in concepts and sentences
				//environment.logError(keys.toString(), null);
				if (jo.get("concepts") != null) {
					temp = jo.get("concepts").getAsJsonObject();
					allConcepts.add(temp);
				}
				if (jo.get("sentences") != null) {
					ja = jo.get("sentences").getAsJsonArray();
					allSentences.add(ja);
				}
				environment.logError("ALLCONCEPTS\n"+allConcepts, null);
				environment.logError("ALLSENTENCES\n"+allSentences, null);
System.exit(0);
			}
		}
		return result;
				
	}
	
	class ParagraphThread extends Thread {
		
		public void run() {
			String para = null;
			while (IS_RUNNING) {
				synchronized(paragraphs) {
					while (paragraphs.isEmpty()) {
						try {
							paragraphs.wait();
						} catch (Exception e) {}
					}
					para = paragraphs.remove(0);
				}
				if (para != null) {
					processParagraph(para);
				}
			}
		}
	}
	public void shutDown() {
		synchronized(paragraphs)  {
			IS_RUNNING = false;
			paragraphs.notify();
		}
	}
}
