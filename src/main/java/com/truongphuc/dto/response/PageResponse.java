package com.truongphuc.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
@Data
@Builder
public class PageResponse <T> implements Serializable {
    int currentPage;
    long totalPages;
    int numberOfElements;
    long totalElements;
    int pageSize;
    List<T> content;
}
