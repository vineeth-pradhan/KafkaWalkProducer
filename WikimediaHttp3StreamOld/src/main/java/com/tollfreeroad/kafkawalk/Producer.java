package com.tollfreeroad.kafkawalk;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import okhttp3.HttpUrl;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        // Server properties
        props.setProperty("bootstrap.servers", "localhost:9094");

        // Producer properties
        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Get producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String topic = "wikimedia.recentchange";
        EventHandler eventHandler = new WikimediaRecentChangeStreamerImpl(producer, topic);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";

//        HttpUrl url = new HttpUrl.Builder()
//                .scheme("https")
//                .host("stream.wikimedia.org")
//                .addPathSegment("v2")
//                .addPathSegment("stream")
//                .addPathSegment("recentchange")
//                .build();
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();
        eventSource.start();
        TimeUnit.MINUTES.sleep(10);
    }
}