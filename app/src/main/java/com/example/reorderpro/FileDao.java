package com.example.reorderpro;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import java.util.List;

@Dao
public interface FileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<FileItem> files);

    /**
     * ⚡ البحث عن ملف عن طريق بصمته (الوقت + الحجم)
     * دي اللي بتخلي التطبيق "يتذكر" الملف حتى لو اسمه اتغير أو مساره اختلف
     */
    @Query("SELECT * FROM files_table WHERE lastModified = :time AND fileSize = :size LIMIT 1")
    FileItem findByFingerprint(long time, long size);

    /**
     * تحديث المعاينة اللحظية للاسم المقترح
     */
    @Query("UPDATE files_table SET newName = :newName WHERE fileUri = :uri")
    void updateNewNameOnly(String uri, String newName);

    /**
     * تحديث البيانات بعد نجاح عملية التسمية الفعلية
     * بنحدث الـ URI الجديد والاسم الحالي (oldName بيبقى هو الاسم اللي شغال الآن)
     */
    @Query("UPDATE files_table SET fileUri = :newUri, oldName = :newName WHERE fileUri = :oldUri")
    void updateAfterRename(String oldUri, String newUri, String newName);

    /**
     * 🔥 الدالة الأسطورية: فحص هل الملفات الحالية تختلف عن أصلها (Original)؟
     * لو العدد أكبر من صفر، الزر هيتحول لـ Undo تلقائياً
     */
    @Query("SELECT COUNT(*) FROM files_table WHERE originalName != oldName")
    int getRenamedFilesCount();

    @Query("SELECT * FROM files_table ORDER BY lastModified ASC")
    LiveData<List<FileItem>> getAllFiles();

    @Query("SELECT * FROM files_table ORDER BY lastModified ASC")
    List<FileItem> getAllFilesList();

    @Update
    void updateFile(FileItem file);

    @Query("DELETE FROM files_table")
    void deleteAllFiles();

    /**
     * التراجع الكلي: بنخلي الاسم الجديد يرجع للاسم "الأسطوري" الأصلي
     */
    @Query("UPDATE files_table SET newName = originalName")
    void resetToOriginal();

    @Transaction
    default void smartSave(List<FileItem> files) {
        // في النسخة الأسطورية، ما بنمسح الكل (deleteAllFiles)
        // بنخلي الـ MainActivity هو اللي يقرر يضيف الجديد أو يحدث القديم
        insertAll(files);
    }
}