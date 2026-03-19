package com.example.reorderpro;

import androidx.documentfile.provider.DocumentFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileSorter {

    /**
     * 🔥 محرك ذكاء الأنواع: يتعرف على الملفات بدقة عالية
     * لتسهيل الفرز البصري للمستخدم في الـ Adapter
     */
    public static String getType(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "FILE";

        String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // فئة الصور
        if (ext.matches(".jpg|.png|.jpeg|.webp|.heic|.gif")) return "IMAGE";

        // فئة الفيديو (المحاضرات والدروس)
        if (ext.matches(".mp4|.mkv|.avi|.mov|.webm|.3gp")) return "VIDEO";

        // فئة المستندات
        if (ext.matches(".pdf|.docx|.doc|.txt|.pptx|.ppt|.xlsx")) return "DOCUMENT";

        // فئة الصوتيات
        if (ext.matches(".mp3|.wav|.m4a|.ogg|.aac")) return "AUDIO";

        return "OTHER";
    }

    /**
     * 🔥 دالة الترتيب الزمني الاحترافية
     * @param files المصفوفة الخام من المجلد
     * @param oldestFirst true للترتيب (أقدم -> أحدث)، false للعكس
     * @return قائمة مرتبة ومنقحة (ملفات فقط بدون مجلدات فرعية)
     */
    public static List<DocumentFile> sortFilesByDate(DocumentFile[] files, boolean oldestFirst) {
        List<DocumentFile> list = new ArrayList<>();
        if (files == null) return list;

        // تنقية القائمة: استبعاد المجلدات الفارغة أو المخفية
        for (DocumentFile f : files) {
            if (f.isFile() && !f.getName().startsWith(".")) {
                list.add(f);
            }
        }

        // الترتيب باستخدام Comparator ذكي يدعم الاتجاهين
        Collections.sort(list, (f1, f2) -> {
            int result = Long.compare(f1.lastModified(), f2.lastModified());
            return oldestFirst ? result : -result; // الضربة القاضية: عكس الترتيب بضغطة زر
        });

        return list;
    }
}