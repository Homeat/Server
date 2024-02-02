package homeat.backend.domain.home.service.ocr;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class FileUtil {

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
