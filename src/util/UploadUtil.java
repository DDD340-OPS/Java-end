package util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Part;

public class UploadUtil {
    private UploadUtil() {
    }

    public static String saveDishImage(ServletContext context, Part part) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        String submittedName = getSubmittedFileName(part);
        if (submittedName == null || submittedName.trim().length() == 0) {
            return null;
        }
        String ext = "";
        int dotIndex = submittedName.lastIndexOf('.');
        if (dotIndex >= 0) {
            ext = submittedName.substring(dotIndex).toLowerCase();
        }
        if (!".jpg".equals(ext) && !".jpeg".equals(ext) && !".png".equals(ext) && !".gif".equals(ext) && !".webp".equals(ext)) {
            throw new IOException("Only jpg, png, gif and webp images are supported.");
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String relativeDir = "/uploads/dish";
        String realDir = context.getRealPath(relativeDir);
        File dir = new File(realDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Cannot create upload directory: " + realDir);
        }
        part.write(new File(dir, fileName).getAbsolutePath());
        return relativeDir + "/" + fileName;
    }

    private static String getSubmittedFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header == null) {
            return null;
        }
        String[] items = header.split(";");
        for (String item : items) {
            String trimmed = item.trim();
            if (trimmed.startsWith("filename")) {
                return trimmed.substring(trimmed.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
