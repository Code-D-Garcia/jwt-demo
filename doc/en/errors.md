# Error Handling

When something goes wrong (for example, if you enter a short password), the server usually throws a very long and hard-to-read error. To avoid this, we've created a system that "catches" those errors and explains them better.

### 1. ErrorResponse.java
It's a class that defines how we want the error to look. Instead of a giant text, we send an organized JSON with:
*   The time of the error.
*   The code (e.g., 400 or 401).
*   A simple, easy-to-understand message.

### 2. GlobalExceptionHandler.java
This file is like a "listener" for problems.
*   If data is missing from a form, it notices and tells you exactly which field is wrong.
*   If you enter the password incorrectly, it catches the error and tells you "Incorrect username or password".

This is very useful because anyone using your API (like a mobile app or a website) will know exactly what happened.
