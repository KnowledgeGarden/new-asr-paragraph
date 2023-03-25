/**
 * 
 */
package org.topicquests.newasr;

import java.util.Map;

import org.topicquests.backside.kafka.consumer.api.IMessageConsumerListener;
import org.topicquests.newasr.api.IAsrDataProvider;
import org.topicquests.newasr.api.IAsrParagraphModel;
import org.topicquests.newasr.api.IKafkaDispatcher;
import org.topicquests.newasr.impl.ASRParagraphModel;
import org.topicquests.newasr.impl.SentenceListener;
import org.topicquests.newasr.impl.SpacyListener;
import org.topicquests.newasr.impl.PostgresWordGramGraphProvider;
import org.topicquests.newasr.kafka.KafkaHandler;
import org.topicquests.newasr.kafka.SentenceProducer;
import org.topicquests.os.asr.driver.sp.SpacyDriverEnvironment;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.support.RootEnvironment;
import org.topicquests.support.config.Configurator;


/**
 * @author jackpark
 *
 */
public class ASRParagraphEnvironment extends RootEnvironment {
	private PostgresConnectionFactory dbDriver = null;
	private IAsrParagraphModel model;
	private IAsrDataProvider database;
	private KafkaHandler sentenceConsumer;
	//private KafkaHandler spacyConsumer;
	private Map<String,Object>kafkaProps;
	private IKafkaDispatcher sentenceListener;
	private IKafkaDispatcher spacyListener;
	private SpacyDriverEnvironment spacyServerEnvironment;
	private ParagraphEngine paragraphEngine;
	private SentenceProducer sentenceProducer;
	public static final String AGENT_GROUP = "Sentence";

	/**
	 * 
	 */
	public ASRParagraphEnvironment() {
		super("asr-paragraph-config.xml", "logger.properties");
		String schemaName = getStringProperty("DatabaseSchema");
		String dbName = getStringProperty("DatabaseName");
		dbDriver = new PostgresConnectionFactory(dbName, schemaName);
		database = new PostgresWordGramGraphProvider(this);
		model = new ASRParagraphModel(this);
		kafkaProps = Configurator.getProperties("kafka-topics.xml");
		sentenceListener = new SentenceListener(this);
		spacyListener = new SpacyListener(this);
		String cTopic = (String)kafkaProps.get("ParagraphConsumerTopic");
		sentenceConsumer = new KafkaHandler(this, (IMessageConsumerListener)sentenceListener, cTopic, AGENT_GROUP);
		//cTopic = (String)kafkaProps.get("SentenceSpacyConsumerTopic");
		//pTopic = (String)kafkaProps.get("SentenceSpacyProducerTopic");
		//spacyConsumer = new KafkaHandler(this, (IMessageConsumerListener)spacyListener, cTopic, AGENT_GROUP);
		sentenceProducer = new SentenceProducer(this, AGENT_GROUP);
		spacyServerEnvironment = new SpacyDriverEnvironment();
		paragraphEngine = new ParagraphEngine(this);
		// firing up WordGramUtil bootstraps punctuation wordgram if not already there
//		booter = new BootstrapEngine(this);
//		predImporter = new PredicateImporter(this);
	}
	
	public ParagraphEngine getParagraphEngine() {
		return paragraphEngine;
	}
	
	public SentenceProducer getSentenceProducer() {
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
	//public KafkaHandler getSpacyConsumer () {
	//	return spacyConsumer;
	//}
	public Map<String, Object> getKafkaTopicProperties() {
		return kafkaProps;
	}

	public IAsrDataProvider getDatabase() {
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
