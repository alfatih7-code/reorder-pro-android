package com.example.reorderpro;

import android.util.Log;

public class NamingHelper {

    /**
     * 🔥 محرك التسمية العالمي المطور - ReOrder Pro Engine
     * تم تحسينه ليكون أسرع وأكثر أماناً مع أنظمة الملفات المختلفة.
     */
    public static String generateName(String originalName, int index, int totalFiles, int optionId, String extension, String customPrefix) {

        // 1. الترقيم الديناميكي الذكي (Dynamic Padding)
        String format;
        if (totalFiles >= 10000) format = "%05d"; // دعم حتى 99 ألف ملف
        else if (totalFiles >= 1000) format = "%04d";
        else if (totalFiles >= 100) format = "%03d";
        else format = "%02d";

        String formattedIndex = String.format(format, index);

        // 2. معالجة الامتداد (Lower Case & Clean)
        String cleanExt = (extension == null) ? "" : extension.toLowerCase().trim();
        String extWithDot = cleanExt.isEmpty() ? "" : "." + cleanExt;

        // 3. استخراج الاسم الأساسي وتنظيفه من الرموز الممنوعة في الأندرويد
        String baseName = getBaseName(originalName);

        // --- محرك اتخاذ القرار (Renaming Logic) ---

        // خيار (Custom): Prefix_01.ext
        if (optionId == R.id.radio_custom) {
            String prefix = (customPrefix == null || customPrefix.trim().isEmpty()) ? "Item" : customPrefix.trim();
            return prefix + "_" + formattedIndex + extWithDot;
        }

        // خيار (Smart): IMG_01.jpg, VID_01.mp4 ...
        else if (optionId == R.id.radio_smart) {
            String prefix = "FILE";
            if (isImage(cleanExt)) prefix = "IMG";
            else if (isVideo(cleanExt)) prefix = "VID";
            else if (isAudio(cleanExt)) prefix = "AUD";
            else if (isDocument(cleanExt)) prefix = "DOC";

            return prefix + "_" + formattedIndex + extWithDot;
        }

        // خيار (Original): الاسم_الأصلي_01.ext
        else if (optionId == R.id.radio_original) {
            return baseName + "_" + formattedIndex + extWithDot;
        }

        // خيار (Numbers): 01.ext
        else if (optionId == R.id.radio_numbers) {
            return formattedIndex + extWithDot;
        }

        // الافتراضي في حال لم يتطابق أي ID
        return originalName;
    }

    private static String getBaseName(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "File";

        int dotIndex = fileName.lastIndexOf('.');
        String name = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);

        // تنظيف الاسم من الرموز الممنوعة التي تسبب فشل الـ renameTo
        // إزالة: \ / : * ? " < > |
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    // استخدام Regex محسن يدعم البحث الجزئي ولا يتأثر بحالة الأحرف
    private static boolean isImage(String ext) {
        return ext.matches("^(jpg|jpeg|png|gif|webp|heic|bmp|svg|tiff)$");
    }

    private static boolean isVideo(String ext) {
        return ext.matches("^(mp4|mkv|mov|avi|3gp|webm|flv|wmv|ts|m4v)$");
    }

    private static boolean isAudio(String ext) {
        return ext.matches("^(mp3|wav|ogg|m4a|aac|flac|amr|opus|mid)$");
    }

    private static boolean isDocument(String ext) {
        return ext.matches("^(pdf|doc|docx|txt|ppt|pptx|xls|xlsx|csv|epub|zip|rar|7z)$");
    }
}