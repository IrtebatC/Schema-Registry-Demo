package ir.kafka.sr;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;


public class AvroProducer
{
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
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

            KafkaProducer<String, Customer> producer = new KafkaProducer<String, Customer>(props);

            for (long i = 0; i < 10; i++) {
                final String name = "name:" + Long.toString(i);
                final Customer Customer = new Customer(name, 24);
                final ProducerRecord<String, Customer> record = new ProducerRecord<String, Customer>(TOPIC, Customer.getName().toString(), Customer);
                producer.send(record).get();
                Thread.sleep(1000L);
            }

            producer.flush();
            System.out.printf("Successfully produced 10 messages to a topic called %s%n", TOPIC);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
}
