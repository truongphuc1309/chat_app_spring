package com.truongphuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@Entity(name = "fileUpload")
@Table(name = "file_upload")
public class FileUploadEntity extends GenericEntity{

    @Column(name = "asset_id")
    String assetId;

    @Column(name = "original_name")
    String originalName;

    @Column(name = "public_id")
    String publicId;

    @Column (name = "url")
    String url;

    @Column (name="download_url")
    String downloadUrl;

    @Column (name = "size")
    Long size;

    @Column (name = "type")
    String type;
}
