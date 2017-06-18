/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.askcloud.plex.pvr.utorrent.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author finkel
 */
public class FilesRequestResult extends HashMap<String, Object> {

    private int build;
    private FilesByHash filesByHash;

    @Override
    public Object put(String key, Object value) {
        final Object put = super.put(key, value);
        switch (key) {
            case "build":
                build = BigDecimal.valueOf((Double) value).intValue();
                break;
            case "files":
                filesByHash = new FilesByHash((Collection) value);
                break;
        }
        return put;
    }

    public int getBuild() {
        return build;
    }

    public FilesByHash getFilesByHash() {
        return filesByHash;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
