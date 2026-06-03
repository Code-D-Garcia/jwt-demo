# Security and JWT 🔐

Security can be the most confusing part when you are learning. Here we try to make it simple using JWT (JSON Web Token).

### What is JWT?
Think of it like an access card. When you log in and your credentials are correct, the server gives you this card. In every request you make after that, you send that card to prove who you are without having to send your password every time.

### Key Files:

1.  **[`SecurityConfig.java`](../security-docs/en/security-config.en.md)**: This is where we configure the rules. Here we define which routes are public (like registration) and which ones require the user to be identified.
****
2.  **[`JwtAuthenticationFilter.java`](../security-docs/en/jwt-authenticationFilter.en.md)**: This is a "filter" that checks every incoming request. If the request brings the ID (the token), the filter reads it and tells Spring: "Hey, this user is valid, let them through."
****
3.  **[`JwtService.java`](../security-docs/en/jwt-service.en.md)**: This is the file that knows how to create and read tokens. Here we configure how long they last (Example: 24 hours for normal access and 7 days for the refresh token).
****
### Access Token vs Refresh Token
*   **Access Token**: This is the one you use for everything. It lasts a short time for security.
*   **Refresh Token**: If the first one expires, you use this one to request a new one without having to log in again.
