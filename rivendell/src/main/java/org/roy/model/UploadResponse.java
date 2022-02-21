package org.roy.model;

import lombok.Data;

@Data
public class UploadResponse {
    public boolean uploadStatus;
    public String recordId;
    public String message;

    public UploadResponse setRecordId(String recordId) {
        this.recordId = recordId;
        return this;
    }

    public UploadResponse setUploadStatus(boolean uploadStatus) {
        this.uploadStatus = uploadStatus;
        return this;
    }

    public UploadResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
