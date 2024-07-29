package com.tollfreeroad.kafkawalk;

import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.StreamException;
import okhttp3.HttpUrl;
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
import com.launchdarkly.eventsource.EventSource;

import java.util.Properties;

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

        String topic = "wikimedia.recentchange";
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("stream.wikimedia.org")
                .addPathSegment("v2")
                .addPathSegment("stream")
                .addPathSegment("recentchange")
                .build();
        EventSource builder = new EventSource.Builder(url).build();
        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();
                log.info("Shutdown buttons(cmd+c) were pressed");
                producer.close();
                builder.close();
                try {
                    mainThread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            builder.start();
            for(MessageEvent m: builder.messages()) {
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, "oct-2024", m.getData());
                log.info(m.getData());
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if(e != null) {
                            e.printStackTrace();
                        }
                        else {
                            log.info("Partition = "+recordMetadata.partition());
                            log.info("Offset = "+recordMetadata.offset());
                            log.info("topic = "+recordMetadata.topic());
                        }
                    }
                });
            }
        }
        catch(ProducerFencedException | AuthorizationException | OutOfOrderSequenceException e) {
            log.info("Kafka exception occurred ==============");
            e.printStackTrace();
        }
        catch(KafkaException e) {
            e.printStackTrace();
        } catch (StreamException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Shutting down the stream");
            builder.close();
            producer.close();
        }
    }
}