# ReOrder Pro
#### Video Demo: https://youtu.be/S9nIH0Ib-9Q

#### Description:
**ReOrder Pro** is an advanced Android utility designed to solve the common problem of disorganized digital file systems. In many cases, files such as academic lectures, camera photos, or downloaded documents are assigned cryptic or random filenames that do not reflect their chronological order. This makes manual organization a tedious and time-consuming task.

This project implements a professional solution using **Modern Android Development (MAD)** practices. The core functionality allows users to select a specific directory and automatically rename all contained files based on their precise "Last Modified" metadata. By leveraging the **Storage Access Framework (SAF)**, the app ensures secure and user-sanctioned access to the file system, complying with Google's latest Scoped Storage requirements.

#### Key Features and Technical Implementation:
* **MVVM Architecture:** The project is built using the Model-View-ViewModel pattern to ensure a clean separation of concerns, making the code maintainable and testable.
* **Room Persistence Library:** I implemented a local database to track all renaming operations. This allows the app to provide a robust **Undo Mechanism**, where users can revert files to their original names instantly if they make a mistake.
* **Asynchronous Processing:** File I/O operations are handled off the main UI thread to ensure a smooth, lag-free user experience, even when processing hundreds of files.
* **Customizable Naming Styles:** Users can choose between "Smart" chronological numbering, preserving original names, or adding custom prefixes.
* **Material Design 3:** The UI features a modern dark-themed interface with dynamic components, ensuring accessibility and a premium feel.

#### File Structure:
* `MainActivity.java`: The primary controller handling UI logic and permission requests.
* `FileAdapter.java`: Manages the RecyclerView for displaying the file list preview.
* `AppDatabase.java` & `FileDao.java`: Handles the Room database logic for the undo system.
* `NamingHelper.java`: Contains the core logic for the chronological sorting and string manipulation algorithms.

This project was developed as the Final Project for **CS50x 2026**.
