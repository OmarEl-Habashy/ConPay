-- Create Schema (or Database)
CREATE SCHEMA IF NOT EXISTS ConPay;
USE ConPay; -- Select the schema to use

-- Create User Table
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(255) NOT NULL UNIQUE, -- Added UNIQUE constraint
    Email VARCHAR(255) NOT NULL UNIQUE,    -- Added UNIQUE constraint
    PassW VARCHAR(255) NOT NULL,           -- Consider storing a hash, not plain text
    Bio TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Post Table
CREATE TABLE Posts (
    PostID INT AUTO_INCREMENT PRIMARY KEY, -- Changed from SERIAL
    UserID INT NOT NULL,
    ContentURL VARCHAR(255),
    Caption TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE -- Added ON DELETE CASCADE
);

-- Create Comment Table
CREATE TABLE Comments (
    CommentID INT AUTO_INCREMENT PRIMARY KEY, -- Changed from SERIAL
    PostID INT NOT NULL,
    UserID INT NOT NULL,
    Content TEXT NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PostID) REFERENCES Posts(PostID) ON DELETE CASCADE, -- Added ON DELETE CASCADE
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE  -- Added ON DELETE CASCADE
);

-- Create Like Table
CREATE TABLE Likes (
    LikeID INT AUTO_INCREMENT PRIMARY KEY, -- Changed from SERIAL
    PostID INT NOT NULL,
    UserID INT NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PostID) REFERENCES Posts(PostID) ON DELETE CASCADE, -- Added ON DELETE CASCADE
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE, -- Added ON DELETE CASCADE
    UNIQUE KEY `unique_like` (`PostID`,`UserID`) -- Prevent duplicate likes
);

-- Create Follow Table
CREATE TABLE Follows (
    FollowerID INT NOT NULL,
    FolloweeID INT NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (FollowerID, FolloweeID),
    FOREIGN KEY (FollowerID) REFERENCES Users(UserID) ON DELETE CASCADE, -- Added ON DELETE CASCADE
    FOREIGN KEY (FolloweeID) REFERENCES Users(UserID) ON DELETE CASCADE  -- Added ON DELETE CASCADE
);