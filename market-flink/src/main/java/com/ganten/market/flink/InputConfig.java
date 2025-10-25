package com.ganten.market.flink;

import lombok.Data;

@Data
public class InputConfig {
    private String topic;
    private String jobName;
    private String groupId;
    private String checkpointDir;
    private String checkpointInterval;

    public static InputConfig build(String topic, String jobName) {
        InputConfig config = new InputConfig();
        config.setTopic(topic);
        config.setJobName(jobName);
        config.setGroupId("flink-" + jobName + "-group");
        config.setCheckpointDir("file:///tmp/flink-checkpoints/" + jobName);
        config.setCheckpointInterval("10000");
        return config;
    }
}
