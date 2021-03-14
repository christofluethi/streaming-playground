package ch.puzzle.devtre.kafka.binance.boundary;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class BinanceKafkaProducer {
    private KafkaProducer<String, String> producer;

    private String id;

    private Logger logger = LoggerFactory.getLogger(BinanceKafkaProducer.class.getName());

    @ConfigProperty(name = "topic")
    String topic;

    @ConfigProperty(name = "bootstrap.servers")
    String bootstrapServer;

    @PostConstruct
    public void init() {
        this.id = UUID.randomUUID().toString();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServer);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "binance-producer-" + id);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(props);

        logger.info("Started KafkaProducer");
    }

    public void sendMessage(String symbol, String price) {
        try {
            RecordMetadata rm = this.producer.send(new ProducerRecord<>(topic, symbol, price)).get();
            this.logger.debug("Producer(id={},partition={}) wrote message: {}", id, rm.partition(), "Symbol "+symbol+" Price "+price);
        } catch (ExecutionException | InterruptedException e) {
            logger.warn("Exception while sending price message symbol={} price={}. ", symbol, price, e);
        }
    }
}
