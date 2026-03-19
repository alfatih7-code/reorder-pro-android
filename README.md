# 🚀 ReOrder Pro - Chronological File Management Engine

**ReOrder Pro** is a high-performance Android utility engineered to automate the organization and batch-renaming of files based on their precise chronological metadata. Designed for power users, it streamlines the management of large directories (Lectures, Media, Documents) using an intelligent sorting and renaming algorithm.

---

## ✨ Key Features

* **Precision Chronological Sorting:** Ultra-accurate sorting of files from oldest to newest (or vice-versa) based on the `lastModified` metadata.
* **Dynamic Naming Engine:** * **Smart:** Context-aware automated naming.
    * **Origin:** Preserves original naming conventions while enforcing numerical sequence.
    * **Numbers:** Pure incremental numerical indexing.
    * **Custom:** Allows user-defined string prefixes for specialized organization.
* **Robust Undo System:** A failsafe restoration mechanism powered by **Room Database**, allowing users to revert all changes to the original state instantly.
* **Premium UI/UX:** A modern, dark-themed interface built on **Material Design 3** principles with **Glassmorphism** effects for a high-end feel.
* **Real-time Preview Architecture:** Instant visual feedback of the naming outcome via **DiffUtil**, ensuring zero mistakes before disk commits.

---

## 🛠 Tech Stack

* **Core Language:** Java (Android SDK).
* **Architecture:** Clean Architecture with **MVVM** pattern logic.
* **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room) for session state persistence and metadata tracking.
* **UI Framework:** ViewBinding, ConstraintLayout, Material Components, and Optimized RecyclerView.
* **Storage Access:** Implements [Storage Access Framework (SAF)](https://developer.android.com/guide/topics/providers/document-provider) to comply with modern **Scoped Storage** requirements.
* **Multithreading:** **ExecutorService** for high-speed, non-blocking background I/O operations.

---

## 📸 App Preview

| Dashboard UI | Naming Options | Active Task State |
| :---: | :---: | :---: |
| ![Main Screen](https://via.placeholder.com/200x400?text=Main+UI) | ![Options](https://via.placeholder.com/200x400?text=Naming+Options) | ![Undo](https://via.placeholder.com/200x400?text=Undo+State) |

---

## 🚀 Workflow Execution

1.  **Directory Selection:** Grant permission via `SELECT FOLDER` using the system picker.
2.  **Sort Definition:** Toggle the chronological order (Ascending/Descending).
3.  **Naming Strategy:** Select the desired logic (Smart, Numbers, or Custom Prefix).
4.  **Disk Commit:** Execute the batch process via `APPLY` to update file descriptors on the storage.

---

### **Developed by:** El-Fatih 💻
*Harvard-ready Project Submission*