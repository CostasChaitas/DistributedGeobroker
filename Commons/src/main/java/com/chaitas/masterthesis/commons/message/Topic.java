package com.chaitas.masterthesis.commons.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Topic {

    private final String topic;
    @JsonIgnore
    public static final String TOPIC_LEVEL_SEPARATOR = "/";
    @JsonIgnore
    private final String[] levelSpecifiers;

    public Topic(String topic) {
        this.topic = topic;
        this.levelSpecifiers = topic.split(TOPIC_LEVEL_SEPARATOR);
    }

    public String getLevelSpecifier(int levelIndex) {
        if (levelIndex >= getNumberOfLevels()) {
            throw new RuntimeException(
                    "Look what you did, you killed the server by not checking the number of levels beforehand!");
        }
        return levelSpecifiers[levelIndex];
    }

    /**
     * @return the number of available levels
     */
    public int getNumberOfLevels() {
        return levelSpecifiers.length;
    }


    public String getTopic() {
        return topic;
    }

    public String[] getLevelSpecifiers() {
        return levelSpecifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Topic)) {
            return false;
        }
        Topic topic1 = (Topic) o;
        return Objects.equals(getTopic(), topic1.getTopic());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getTopic());
    }

    @Override
    public String toString() {
        return getTopic();
    }
}
