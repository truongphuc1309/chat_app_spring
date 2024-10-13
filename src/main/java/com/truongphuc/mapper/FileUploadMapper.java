package com.truongphuc.mapper;

import com.truongphuc.dto.FileUploadDto;
import com.truongphuc.entity.FileUploadEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileUploadMapper {
    FileUploadEntity toFileUploadEntity(FileUploadDto fileUpload);
}
