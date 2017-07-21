package com.pdfkns.pdfcreator.Model;

import com.orm.SugarRecord;

/**
 * Created by sanjaypatel on 2017-06-02.
 */

public class MediaCell  extends SugarRecord {

    private  String  url;
    private  String txtContent;
    private  int sequence;
    private MediaCellEnum mediaCellEnum;


    public MediaCellEnum getMediaCellEnum() {
        return mediaCellEnum;
    }

    public void setMediaCellEnum(MediaCellEnum mediaCellEnum) {
        this.mediaCellEnum = mediaCellEnum;
    }

    public MediaCell() {
    }

    public MediaCell(String url, String content, int sequence,  MediaCellEnum mediaCellEnum) {
        this.url = url;
        this.txtContent = content;
        this.sequence = sequence;
        this.mediaCellEnum = mediaCellEnum;
    }

    public MediaCell(String url, String content, int sequence) {
        this.url = url;
        this.txtContent = content;
        this.sequence = sequence;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTxtContent() {
        return txtContent;
    }

    public void setTxtContent(String txtContent) {
        this.txtContent = txtContent;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
