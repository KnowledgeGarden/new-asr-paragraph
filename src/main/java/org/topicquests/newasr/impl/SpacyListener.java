/**
 * 
 */
package org.topicquests.newasr.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.topicquests.backside.kafka.consumer.api.IMessageConsumerListener;
import org.topicquests.newasr.ASRParagraphEnvironment;
import org.topicquests.newasr.api.IAsrParagraphModel;
import org.topicquests.newasr.api.IKafkaDispatcher;
import org.topicquests.newasr.util.JsonUtil;

import com.google.gson.JsonObject;

/**
 * @author jackpark
 *
 */
public class SpacyListener implements IMessageConsumerListener, IKafkaDispatcher {
	private ASRParagraphEnvironment environment;
	private IAsrParagraphModel model;
	private JsonUtil util;

	/**
	 * 
	 */
	public SpacyListener(ASRParagraphEnvironment env) {
		environment =env;
		model = environment.getModel();
		util = new JsonUtil();
	}

	@Override
	public boolean acceptRecord(ConsumerRecord record) {
		String json = (String)record.value();
		environment.logDebug("SpacyListener.acceptRecord "+json);
		boolean result = false;
		if (json == null)
			return result;
		try {
			JsonObject data = util.parse(json);
			result = model.acceptSpacyResponse(data);
		} catch (Exception e) {
			environment.logError("SpacyListener: "+e.getMessage(), e);
			e.printStackTrace();
		}

		return result;	
	}

}
