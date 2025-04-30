D:.
├───.idea
├───.mvn
│   └───wrapper
├───src
│   ├───main
│   │   ├───java
│   │   │   └───aammo
│   │   │       └───ppv
│   │   │           ├───controller   # Handles HTTP requests (e.g., UserController, PostController)
│   │   │           ├───model        # Data models (e.g., User, Post, Comment)
│   │   │           ├───service      # Business logic (e.g., UserService, PostService)
│   │   │           ├───repository   # Data access layer (e.g., UserRepository, PostRepository)
│   │   │           └───util         # Utility classes (e.g., helpers, constants)
│   │   ├───resources
│   │   │   ├───META-INF             # Configuration files (e.g., persistence.xml)
│   │   │   └───sql                  # SQL scripts (e.g., schema.sql, data.sql)
│   │   └───webapp
│   │       ├───WEB-INF
│   │       │   ├───views            # JSP/HTML files for the UI
│   │       │   │   ├───common       # Shared components (e.g., header.jsp, footer.jsp)
│   │       │   │   ├───user         # User-related views (e.g., register.jsp, login.jsp)
│   │       │   │   │
│   │       │   │   └───error        # Error pages (e.g., 404.jsp, 500.jsp)
│   │       │   └───lib              # Libraries (if needed)
│   │       └───static               # Static resources (e.g., CSS, JS, images)
│   │           ├───css              # Stylesheets (e.g., main.css)
│   │           ├───js               # JavaScript files (e.g., main.js)
│   │           ├───fonts            # Font files (e.g., Roboto.ttf)
│   │           ├───images           # Image files (e.g., logo.png)
│   │           └───vendor           # Third-party libraries (e.g., jQuery, Bootstrap)
│   └───test
│       ├───java
│       │   └───aammo
│       │           └───ppv
│       │               ├───controller
│       │               ├───model
│       │               ├───service
│       │               └───repository
│       └───resources
└───target