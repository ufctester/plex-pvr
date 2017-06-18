/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.askcloud.plex.pvr.utorrent.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author finkel
 */
public class FilesByHash {

    private String hash;
    private ArrayList<SingleFile> filesList = new ArrayList<>();

    public FilesByHash(Collection collection) {
//        List filesListWithHash = List.iterableList(collection);
        hash = ((List) collection).get(0).toString();
        final List list = (List) ((List) collection).get(1);
        for (Iterator it = list.iterator(); it.hasNext();) {
            List singleFileList = (List) it.next();
            SingleFile singleFile = new SingleFile();
            singleFile.setName(singleFileList.get(0));
            singleFile.setFileSize(((Double) singleFileList.get(1)).intValue());
            singleFile.setDownloaded(((Double) singleFileList.get(2)).intValue());
            singleFile.setPriority(SingleFile.Priority.byIntValue(((Double) singleFileList.get(3)).intValue()));
            filesList.add(singleFile);
        }
        Collections.sort(filesList, new Comparator<SingleFile>() {
            @Override
            public int compare(SingleFile o1, SingleFile o2) {
                return o1.getName().compareTo(o1.getName());
            }
        });
    }

    public String getHash() {
        return hash;
    }

    public ArrayList<SingleFile> getFilesList() {
        return filesList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
