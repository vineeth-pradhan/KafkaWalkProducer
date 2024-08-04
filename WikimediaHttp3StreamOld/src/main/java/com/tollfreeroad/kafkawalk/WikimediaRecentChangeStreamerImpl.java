package com.tollfreeroad.kafkawalk;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikimediaRecentChangeStreamerImpl implements EventHandler {
    private static final Logger log = LoggerFactory.getLogger(WikimediaRecentChangeStreamerImpl.class);
    KafkaProducer<String, String> producer;
    String topic;

    public WikimediaRecentChangeStreamerImpl(KafkaProducer<String, String> producer, String topic){
        this.producer = producer;
        this.topic = topic;
    }

    @Override
    public void onOpen() throws Exception {
        log.info("Opened =====================");
        // Do nothing
    }

    @Override
    public void onClosed() throws Exception {
        // Producer close
        this.producer.close();
    }

    @Override
    public void onMessage(String s, MessageEvent messageEvent) {
        log.info("At the interface impl ==============");
        // Steam it to kafka producer
        ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, messageEvent.getData());
        try {
            log.info("About to send");
            log.info(messageEvent.getData());
            this.producer.send(record);
        }
        catch(KafkaException e) {
            e.printStackTrace();
            this.producer.close();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onComment(String s) {
        // Do nothing
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
