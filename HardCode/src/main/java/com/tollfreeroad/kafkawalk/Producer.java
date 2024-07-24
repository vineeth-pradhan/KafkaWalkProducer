package com.tollfreeroad.kafkawalk;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static java.lang.Thread.sleep;

public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class.getSimpleName());

    public static void main(String[] args) {
        Properties props = new Properties();

        // Server properties
        props.setProperty("bootstrap.servers", "localhost:9094");

        // Producer properties
        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Get producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record;
        try {
            for(int i = 0; i < 30; i++){
                record = new ProducerRecord<>("vin_topic", "key_9_"+i, "Vineeth_15_"+i);
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if(e != null) {
                            e.printStackTrace();
                        }
                        else {
                            log.info("Partition: "+recordMetadata.partition());
                            log.info("Offset: "+recordMetadata.offset());
                        }
                    }
                });
            }
        }
        catch(ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            log.info("ProducerFencedException exception was raised");
            producer.close();
        }
        catch (KafkaException e) {
            log.info("KafkaException exception was raised");
        }
        finally {
            producer.close();
        }
    }
}