# Schema-Registry-Demo
This repo is a playground for testing Producer, Consumer Java Clients in context of Schema-Registry features.

# How to register the schema
The schemas are defined in the src/main/avro and src/main/json folder. 
In this version of the demo, I am registering employee.json schema ( in src/main/json ), with 5 rules of which 3 are field transformations and 2 are data quality rules.

I am using kafka-schema-registry-maven-plugin to register the schemas. As such, the schema string ( properties like schemaType, ruleSet etc ) is defined in the pom.xml file.

To register the schema, use the following command:
mvn io.confluent:kafka-schema-registry-maven-plugin:register

# How to create java avro classes from avro schema
I am using avro-maven-plugin to create java avro classes from my defined avro schema. It allows us to specify schema source directory. Find more information here: http://grepalex.com/2013/05/24/avro-maven/
By default, the class will be generated in the target folder. To ensure that your applications can access the generated class, ensure that you target folder is not part of your excluded folder list in your IDE.

Using the following command: 
mvn.org.apache.avro:avro-maven-plugin.schema
