package ir.kafka.sr;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.clients.producer.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.confluent.kafka.schemaregistry.rules.cel.CelExecutor;
import io.confluent.kafka.schemaregistry.rules.RuleAction;
import io.confluent.kafka.serializers.WrapperKeySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.schemaregistry.rules.DlqAction;

public class JsonProducer
{
    public static class employee{
        @JsonProperty
        public String name;
        @JsonProperty
        public String ssn;
        @JsonProperty
        public Integer age;
        @JsonProperty
        public String designation;

        public employee(String name, String ssn, Integer age, String designation) {
            this.name=name;
            this.ssn=ssn;
            this.age=age;
            this.designation=designation;
        }
    }
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
            props.put("key.serializer", "io.confluent.kafka.serializers.WrapperKeySerializer");
            props.put("wrapped.key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", KafkaJsonSchemaSerializer.class);
            props.put("auto.register.schemas", "false");
            props.put("use.latest.version", "true");
            props.put("latest.compatibility.strict", "false");

            Producer<String, employee> producer = new KafkaProducer<String, employee>(props);
            String topic = "employee";

            final Integer numOfRecords=2;

            for (int i = 0; i < numOfRecords; i++) {
                System.out.println(i);
                // Creating Employee Record
                final String name = "Name_" + Integer.toString(i);
                final String ssn = "1000000000";
                final Integer age = 30+(i/2);
                String designation = "";
                if(i%3==0)
                {
                    designation = "Engineer";
                }
                else if (i%3==1)
                {
                    designation = "Manager";
                }else
                {
                    designation = "Architect";
                }

                employee user = new employee(name,ssn,age,designation);
                final ProducerRecord<String, employee> record = new ProducerRecord<String, employee>(topic, ssn, user);

                // Sending Record
                try{
                    producer.send(record).get();
                    System.out.println(user.name+','+user.ssn+','+user.age+','+user.designation+'\n');
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            producer.flush();
            producer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
