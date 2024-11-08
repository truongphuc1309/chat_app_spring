package com.truongphuc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FileUploadDto implements Serializable {
    private String originalName;
    private String assetId;
    private String downloadUrl;
    private long size;
    private String publicId;
    private String url;
    private String type;
}
