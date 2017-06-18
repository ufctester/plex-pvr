/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.askcloud.plex.pvr.utorrent.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author finkel
 */
class SingleFile {

    private String name;
    private int fileSize;
    private int downloaded;
    private Priority priority;

    public int getFileSize() {
        return fileSize;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    void setName(Object name) {
        this.name = name.toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    void setFileSize(int intValue) {
        this.fileSize = intValue;
    }

    void setDownloaded(int intValue) {
        downloaded = intValue;
    }

    public enum Priority {

        SKIPPED(0), LOW(1), NORMAL(2), HIGH(3);

        public static Priority byIntValue(int value) {
            for (Priority priority : Priority.values()) {
                if (priority.intValue == value) {
                    return priority;
                }
            }
            throw new RuntimeException(new IllegalArgumentException("Invalid int value"));
        }
        private int intValue;

        private int intValue() {
            return intValue;
        }

        private Priority(int value) {
            intValue = value;
        }
    }
}
