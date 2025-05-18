package com.arkhamusserver.arkhamus.playground

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread

class PlayWithKafka {

}

fun main() {
    val bootstrapServers = "localhost:9092"
    val topicName = "demo-topic"

    // --- 1. Check or create topic ---
    val adminProps = Properties().apply {
        put("bootstrap.servers", bootstrapServers)
    }

    AdminClient.create(adminProps).use { admin ->
        val existingTopics = admin.listTopics().names().get()
        if (topicName !in existingTopics) {
            println("[Admin] Topic '$topicName' does not exist. Creating...")
            val topic = NewTopic(topicName, 1, 1.toShort())
            admin.createTopics(listOf(topic)).all().get()
            println("[Admin] Topic created.")
        } else {
            println("[Admin] Topic '$topicName' already exists.")
        }
    }

    // --- 2. Start Producer thread ---
    thread(name = "producer-thread") {
        val producerProps = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        }

        KafkaProducer<String, String>(producerProps).use { producer ->
            var counter = 0
            println("[Producer] Started.")
            while (true) {
                val message = "Hello Kafka #$counter"
                val record = ProducerRecord(topicName, "key", message)
                println("[Producer] Sending message: $message")
                producer.send(record) { metadata, exception ->
                    if (exception == null) {
                        println("[Producer] Sent: $message to ${metadata.topic()}-${metadata.partition()}@${metadata.offset()}")
                    } else {
                        println("[Producer] Failed to send: ${exception.message}")
                    }
                }
                counter++
                Thread.sleep(1000)
            }
        }
    }

    thread(name = "consumer-thread") {
        val consumerProps = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-group-${UUID.randomUUID()}")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        }

        KafkaConsumer<String, String>(consumerProps).use { consumer ->
            println("[Consumer] Subscribing to topic: $topicName")
            consumer.subscribe(listOf(topicName))
            println("[Consumer] Subscribed. Forcing assignment by poll(0)...")
            consumer.poll(Duration.ofMillis(0)) // trigger partition assignment
            println("[Consumer] Starting polling loop...")

            while (true) {
                println("[Consumer] Polling for messages...")
                val records = consumer.poll(Duration.ofMillis(1000))
                if (records.isEmpty) {
                    println("[Consumer] No messages received.")
                } else {
                    for (record in records) {
                        println("[Consumer] Received: key=${record.key()}, value=${record.value()}, partition=${record.partition()}, offset=${record.offset()}")
                    }
                }
            }
        }
    }

//    // --- 3. Start Consumer thread ---
//    thread(name = "consumer-thread") {
//        val consumerProps = Properties().apply {
//            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
//            put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-group")
//            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
//            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
//            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
//            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
//        }
//
//        KafkaConsumer<String, String>(consumerProps).use { consumer ->
//            println("[Consumer] Subscribing to topic: $topicName")
//            consumer.subscribe(listOf(topicName))
//            consumer.poll(Duration.ofMillis(0))
//            println("[Consumer] Started and subscribed.")
//            while (true) {
//                println("[Consumer] Polling...")
//                val records = consumer.poll(Duration.ofMillis(1000))
//                if (records.isEmpty) {
//                    println("[Consumer] No messages received.")
//                } else {
//                    for (record in records) {
//                        println("[Consumer] Received: key=${record.key()}, value=${record.value()}, partition=${record.partition()}, offset=${record.offset()}")
//                    }
//                }
//            }
//        }
//    }
}
