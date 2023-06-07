package ir.kafka.sr;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.Duration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

public class AvroConsumer {
    private static final String TOPIC = "Customers";

    public static Properties loadConfig(final String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        return cfg;
    }
    public static void main( String[] args )
    {
        try {
            final Properties props = loadConfig("client.properties");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-customer");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
            props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

            final KafkaConsumer<String, Customer> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (true) {
                final ConsumerRecords<String, Customer> records = consumer.poll(Duration.ofMillis(100));
                for (final ConsumerRecord<String, Customer> record : records) {
                    final String key = record.key();
                    final Customer value = record.value();
                    System.out.printf("key = %s, value = %s%n", key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
