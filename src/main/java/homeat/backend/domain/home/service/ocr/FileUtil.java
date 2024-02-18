package homeat.backend.domain.home.service.ocr;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

@Component
public class FileUtil {

    // 영수증 이미지 파일 확장자
    public boolean isSupportedExtension(String extenstion) {
        return Arrays.asList("jpg", "jpeg,", "png", "pdf", "tiff").contains(extenstion);
    }

    /**
     * MultipartFile -> File
     */
    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertdFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertdFile);
        fos.write(file.getBytes());
        fos.close();
        return convertdFile;
    }
}
