package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;

public class BinaryContent extends BaseEntity {
  private String fileName;
  private Long size;
  private String contentType;
  private byte[] bytes;

  public BinaryContent(String fileName, long length, String contentType, byte[] bytes) {
    super();
  }

  public String getFileName() { return fileName; }
  public void setFileName(String fileName) { this.fileName = fileName; }

  public Long getSize() { return size; }
  public void setSize(Long size) { this.size = size; }

  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }

  public byte[] getBytes() { return bytes; }
  public void setBytes(byte[] bytes) { this.bytes = bytes; }
}