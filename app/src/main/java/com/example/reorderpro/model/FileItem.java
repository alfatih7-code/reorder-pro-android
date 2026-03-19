package com.example.reorderpro;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "files_table")
public class FileItem {
    @PrimaryKey @NonNull
    public String fileUri;
    public String oldName;
    public String originalName;
    public String newName;
    public String fileType;
    public long lastModified;
    public long fileSize;

    public FileItem() { this.fileUri = ""; }

    @Ignore
    public FileItem(@NonNull String fileUri, String oldName, String originalName, String newName, String fileType, long lastModified, long fileSize) {
        this.fileUri = fileUri;
        this.oldName = oldName;
        this.originalName = originalName;
        this.newName = newName;
        this.fileType = fileType;
        this.lastModified = lastModified;
        this.fileSize = fileSize;
    }
}