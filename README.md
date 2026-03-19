# ReOrder Pro: A Professional Chronological File Organizer
#### Video Demo: https://youtu.be/S9nIH0Ib-9Q

## Project Overview
**ReOrder Pro** is a comprehensive Android application built to address a fundamental challenge in digital file management: the lack of intuitive chronological organization. Many users face issues where files—ranging from university lectures and work documents to personal media—are saved with non-descriptive or random names. This makes it impossible to find them based on the order they were created or modified. 

I developed this app to empower users to regain control over their file systems. By utilizing the latest Android APIs, the app can read file metadata and rename entire directories in seconds, ensuring that files are sorted exactly as they were produced in time.

## Design Philosophy & Choices
During the development process, I debated several design choices. Initially, I considered a simple file-renaming script. However, I decided to build a full-scale Android app to provide a graphical interface that is accessible to non-technical users. 

I chose **Java** as the primary programming language because of its robustness and long-standing support within the Android ecosystem. To manage the user interface, I adopted the **MVVM (Model-View-ViewModel)** architectural pattern. This choice was crucial for maintaining a clean separation between the business logic (renaming files) and the UI (displaying them).

## Technical Implementation and File Structure

### 1. Database and Undo System (`AppDatabase.java` and `FileDao.java`)
One of the most complex features of ReOrder Pro is the **Undo Mechanism**. I implemented the **Room Persistence Library** to create a local SQLite database. Before any renaming operation occurs, the app records the original filename and the new filename. This ensures that if a user renames a folder by mistake, they can revert every single file to its original state with a single tap. This feature required careful handling of database transactions to ensure data integrity.

### 2. User Interface (`MainActivity.java` and Layout files)
The UI is built using **Material Design 3** principles. I used a **RecyclerView** and a custom **FileAdapter.java** to provide a smooth scrolling experience. The app supports a dynamic Dark Mode, which I implemented to reduce eye strain for users who manage files late at night. The use of `CardView` and glassmorphism-inspired backgrounds gives the app a premium, modern feel. I also focused on **Responsive Design** to ensure the app looks good on different screen sizes.

### 3. File Logic (`NamingHelper.java`)
The core algorithm resides in `NamingHelper.java`. This class handles the complexity of retrieving the "Last Modified" timestamp from the Android File System. It then applies a sorting algorithm to arrange the files chronologically and generates the new names based on user preferences (Smart numbering, Original preservation, or Custom prefixes).

### 4. Security and Permissions
To comply with Google’s **Scoped Storage** requirements, I implemented the **Storage Access Framework (SAF)**. This ensures that the app only accesses directories that the user explicitly selects, maintaining high privacy and security standards. Handling these permissions was one of the most challenging parts of the project, as it required deep knowledge of Android's modern security architecture.

## Conclusion
This project was a journey of learning how to integrate complex database systems with low-level file system operations on Android. It reflects the skills I acquired during **CS50x**, from logical problem-solving to creating user-centric software. ReOrder Pro is not just a utility; it is a tool designed to bring order to digital chaos. I am proud to submit this as my final contribution to the course.

Developed by: **Elfatih Babiker Elhassan Mohamed**
Location: **Riyadh, Saudi Arabia**
Course: **CS50x 2026**
