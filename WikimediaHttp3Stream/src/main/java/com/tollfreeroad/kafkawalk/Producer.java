package com.tollfreeroad.kafkawalk;

import com.launchdarkly.eventsource.MessageEvent;
import okhttp3.HttpUrl;
import org.apache.kafka.clients.producer.KafkaProducer;
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
        log.info("All went well");
        log.info(url.toString());
        for(MessageEvent m: builder.messages()) {
            log.info(m.getData());
        }
    }
}