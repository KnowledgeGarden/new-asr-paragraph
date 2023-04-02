/*
 * Copyright 2023 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.newasr;

import java.util.Map;

import org.topicquests.backside.kafka.consumer.api.IMessageConsumerListener;
import org.topicquests.newasr.api.IParagraphDataProvider;
import org.topicquests.newasr.api.IAsrParagraphModel;
import org.topicquests.newasr.api.IKafkaDispatcher;
import org.topicquests.newasr.impl.ASRBaseEnvironment;
import org.topicquests.newasr.impl.ASRParagraphModel;
import org.topicquests.newasr.impl.ParagraphListener;
import org.topicquests.newasr.impl.PoatgresParagraphProvider;
import org.topicquests.newasr.kafka.KafkaHandler;
import org.topicquests.newasr.kafka.ParagraphProducer;
import org.topicquests.os.asr.driver.sp.SpacyDriverEnvironment;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.support.config.Configurator;


/**
 * @author jackpark
 *
 */
public class ASRParagraphEnvironment extends ASRBaseEnvironment {
	private PostgresConnectionFactory dbDriver = null;
	private IAsrParagraphModel model;
	private IParagraphDataProvider database;
	private KafkaHandler sentenceConsumer;
	private Map<String,Object>kafkaProps;
	private IKafkaDispatcher sentenceListener;
	private SpacyDriverEnvironment spacyServerEnvironment;
	private ParagraphEngine paragraphEngine;
	private ParagraphProducer sentenceProducer;

	public static final String AGENT_GROUP = "Sentence"; //TODO "Paragraph" ???

	/**
	 * 
	 */
	public ASRParagraphEnvironment() {
		super("asr-paragraph-config.xml", null, "logger.properties");
		String schemaName = getStringProperty("DatabaseSchema");
		String dbName = getStringProperty("DatabaseName");
		dbDriver = new PostgresConnectionFactory(dbName, schemaName);
		database = new PoatgresParagraphProvider(this);
		model = new ASRParagraphModel(this);
		kafkaProps = Configurator.getProperties("kafka-topics.xml");
		sentenceListener = new ParagraphListener(this);
		String cTopic = (String)kafkaProps.get("ParagraphConsumerTopic");
		sentenceConsumer = new KafkaHandler(this, (IMessageConsumerListener)sentenceListener, cTopic, AGENT_GROUP);
		sentenceProducer = new ParagraphProducer(this, AGENT_GROUP);
		spacyServerEnvironment = new SpacyDriverEnvironment();
		paragraphEngine = new ParagraphEngine(this);

		paragraphEngine.startProcessing();
		// shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread()
	    {
	      public void run()
	      {
	        shutDown();
	      }
	    });

	}
	
	public ParagraphEngine getParagraphEngine() {
		return paragraphEngine;
	}
	
	public ParagraphProducer getSentenceProducer() {
		return sentenceProducer;
	}
	/**
	 * There are two spaCy systems in the present code:
	 * one is on an http service, the other is over kafka
	 * @return
	 */
	public SpacyDriverEnvironment getSpacyServerEnvironment() {
		return spacyServerEnvironment;
	}
	
	public KafkaHandler getSentenceConsumer () {
		return sentenceConsumer;
	}

	public Map<String, Object> getKafkaTopicProperties() {
		return kafkaProps;
	}

	public IParagraphDataProvider getDatabase() {
		return database;
	}
	public IAsrParagraphModel getModel() {
		return model;
	}
	public PostgresConnectionFactory getDatabaseDriver() {
		return dbDriver;
	}
	
	@Override
	public void shutDown() {
		System.out.println("Shutting down");
		paragraphEngine.shutDown();
		sentenceConsumer.shutDown();
		//spacyConsumer.shutDown();

	}

}
