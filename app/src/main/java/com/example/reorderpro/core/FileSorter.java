package com.example.reorderpro.core;

import com.example.reorderpro.model.FileItem;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FileSorter {

    public enum SortMode {
        NAME,
        DATE_ASC,
        DATE_DESC,
        SIZE,
        TYPE
    }

    // ================= MAIN =================

    public static void sort(List<FileItem> files, SortMode mode) {

        if (files == null || files.size() <= 1) return;

        switch (mode) {

            case NAME:
                files.sort(NAME_COMPARATOR);
                break;

            case SIZE:
                files.sort(SIZE_COMPARATOR);
                break;

            case TYPE:
                files.sort(TYPE_COMPARATOR);
                break;

            case DATE_DESC:
                files.sort(DATE_DESC_COMPARATOR);
                break;

            case DATE_ASC:
            default:
                files.sort(DATE_ASC_COMPARATOR);
                break;
        }
    }

    // ================= COMPARATORS =================

    private static final Comparator<FileItem> NAME_COMPARATOR =
            Comparator.comparing(FileSorter::safeName);

    private static final Comparator<FileItem> DATE_ASC_COMPARATOR =
            Comparator.comparingLong(FileItem::getLastModified);

    private static final Comparator<FileItem> DATE_DESC_COMPARATOR =
            Comparator.comparingLong(FileItem::getLastModified).reversed();

    private static final Comparator<FileItem> SIZE_COMPARATOR =
            Comparator.comparingLong(FileItem::getSize);

    private static final Comparator<FileItem> TYPE_COMPARATOR =
            Comparator.comparingInt(FileSorter::getTypePriority)
                    .thenComparing(FileSorter::safeName);

    // ================= HELPERS =================

    private static String safeName(FileItem f) {
        String name = f.getName();
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    private static int getTypePriority(FileItem f) {

        if (f.isImage()) return 1;
        if (f.isVideo()) return 2;
        if (f.isDocument()) return 3;
        if (f.isAudio()) return 4;

        return 5;
    }
}