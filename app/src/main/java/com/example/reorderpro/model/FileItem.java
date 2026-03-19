package com.example.reorderpro.model;

import androidx.documentfile.provider.DocumentFile;

import java.util.Locale;

public class FileItem {

    private final String name;
    private final long lastModified;
    private final DocumentFile file;

    public FileItem(String name, long lastModified, String type, DocumentFile file) {
        this.name = name != null ? name : "";
        this.lastModified = lastModified;
        this.file = file;
    }

    // ================= BASIC =================

    public String getName() {
        return name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public DocumentFile getFile() {
        return file;
    }

    // ================= EXTENSION =================

    public String getExtension() {

        int dot = name.lastIndexOf(".");

        if (dot > 0 && dot < name.length() - 1) {
            return name.substring(dot).toLowerCase(Locale.ROOT);
        }

        return "";
    }

    public String getBaseName() {

        int dot = name.lastIndexOf(".");

        if (dot > 0) {
            return name.substring(0, dot);
        }

        return name;
    }

    // ================= SIZE =================

    public long getSize() {
        try {
            return file != null ? file.length() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // ================= TYPE DETECTION (FIXED) =================

    public boolean isImage() {
        String ext = getExtension();
        return ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png");
    }

    public boolean isVideo() {
        String ext = getExtension();
        return ext.equals(".mp4") || ext.equals(".mkv") || ext.equals(".avi");
    }

    public boolean isAudio() {
        String ext = getExtension();
        return ext.equals(".mp3") || ext.equals(".wav");
    }

    public boolean isDocument() {
        String ext = getExtension();
        return ext.equals(".pdf")
                || ext.equals(".doc")
                || ext.equals(".docx")
                || ext.equals(".txt");
    }

    // ================= SMART PREFIX =================

    public String getSmartPrefix() {

        if (isImage()) return "IMG_";
        if (isVideo()) return "VID_";
        if (isAudio()) return "AUD_";
        if (isDocument()) return "DOC_";

        return "FILE_";
    }

    // ================= NAME BUILDER =================

    public String buildSmartName(int index) {
        return getSmartPrefix()
                + String.format(Locale.ROOT, "%03d", index + 1)
                + getExtension();
    }
}