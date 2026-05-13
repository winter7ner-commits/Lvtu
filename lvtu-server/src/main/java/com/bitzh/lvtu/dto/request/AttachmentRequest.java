package com.bitzh.lvtu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AttachmentRequest {

    @NotBlank(message = "附件文件名不能为空")
    @Size(max = 100, message = "附件文件名长度不能超过100")
    private String fileName;
    @NotBlank(message = "附件地址不能为空")
    @Size(max = 255, message = "附件地址长度不能超过255")
    private String fileUrl;
    @Size(max = 255, message = "附件类型长度不能超过255")
    private String fileType;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
