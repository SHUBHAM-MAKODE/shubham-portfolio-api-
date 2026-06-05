package com.portfolio.media;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class MediaService {

    @Autowired
    private Cloudinary cloudinary;

    public Map<String, Object> uploadFile(MultipartFile file, String folderName) throws IOException {
        // 🌟 FORCE: Standardize configuration parameters directly
        Map<String, Object> options = ObjectUtils.asMap(
            "folder", "portfolio/" + folderName,
            "use_filename", true,
            "unique_filename", true,
            
            // 🌟 THE CRITICAL FIX: "image" type tells Cloudinary to handle the file 
            // as an image/document hybrid, preserving its pristine .pdf extension signature link!
            "resource_type", "image" 
        );

        return cloudinary.uploader().upload(file.getBytes(), options);
    }

    // ==========================================================================
    // 🌟 FIXED: Clean Synchronized Deletion Handler
    // ==========================================================================
    public Map<String, Object> deleteFile(String publicId) throws IOException {
        // Since uploads are standardized as "image" now, deletes must match exactly 
        // to prevent orphan file clutter or silent "not_found" errors.
        Map<String, Object> options = ObjectUtils.asMap(
            "resource_type", "image"
        );

        return cloudinary.uploader().destroy(publicId, options);
    }
}