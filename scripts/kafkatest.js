import { check } from "k6";
import {
    Writer,
    Reader,
    Connection,
    SchemaRegistry,
    CODEC_SNAPPY,
    SCHEMA_TYPE_JSON,
} from "k6/x/kafka";

const brokers = ["broker:9094"];
const topic1 = "topic1";
const topic2 = "topic2";

const writer = new Writer({
    brokers: brokers,
    topic: topic1,
    autoCreateTopic: true,
    compression: CODEC_SNAPPY,
});
const connection = new Connection({
    address: brokers[0],
});
const schemaRegistry = new SchemaRegistry();

if (__VU == 0) {
    connection.createTopic({
        topic: topic1,
        numPartitions: 2,
        configEntries: [
            {
                configName: "compression.type",
                configValue: CODEC_SNAPPY,
            },
        ],
    });

    connection.createTopic({
        topic: topic2,
        numPartitions: 2,
        configEntries: [
            {
                configName: "compression.type",
                configValue: CODEC_SNAPPY,
            },
        ],
    });
}

export const options = {
    scenarios: {
        fiverps: {
            executor: "constant-arrival-rate",
            rate: 5,
            timeUnit: "1s",
            duration: "10m",
            startTime: '0s',
            preAllocatedVUs: 0,
            maxVUs: 100,
        },
        plusfiverps: {
            executor: "constant-arrival-rate",
            rate: 5,
            timeUnit: "1s",
            duration: "5m",
            startTime: '5m',
            preAllocatedVUs: 0,
            maxVUs: 100,
        },
    },
    thresholds: {
        kafka_writer_error_count: ["count == 0"],
    },
    summaryTrendStats: ["avg", "min", "med", "max", "p(95)", "p(99)", "count"],
};

export default function () {
    for (let index = 0; index < 100; index++) {
        let partition = index % 2;

        let messages = [
            {
                key: schemaRegistry.serialize({
                    data: {
                        correlationId: index,
                    },
                    schemaType: SCHEMA_TYPE_JSON,
                }),
                value: schemaRegistry.serialize({
                    data: {
                        id: Math.floor(Math.random() * 100),
                    },
                    schemaType: SCHEMA_TYPE_JSON,
                }),
                headers: {
                    mykey: "myvalue",
                },
                offset: index,
                partition: partition,
                time: new Date(),
            },
        ];

        writer.produce({ messages: messages });
    }
}

export function teardown(data) {
    writer.close();

    connection.close();
}