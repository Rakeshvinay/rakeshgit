

package com.brihathi.Multi_Tenant.service;

import com.brihathi.Multi_Tenant.entity.Chapter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ChapterService {

    List<Chapter> uploadChaptersFromExcel(
            MultipartFile file,
            String requestSubdomain) throws IOException;

    Chapter createAllChapter();
}
