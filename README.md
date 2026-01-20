# 🧮 Math Learning Game

A feature-rich, interactive desktop application built with **Java Swing** designed to gamify math practice. It supports **Single Player** and **Multiplayer** competition modes, featuring a custom dark-themed GUI, dynamic game logic, and real-time score tracking.

![Java](https://img.shields.io/badge/Language-Java-orange) ![GUI](https://img.shields.io/badge/Interface-Swing%20%2F%20AWT-blue) ![License](https://img.shields.io/badge/License-MIT-green)

## 📖 Overview

This is not just a standard calculator game. It is a fully event-driven application that utilizes **Java's CardLayout** to manage multiple screens (Start, Setup, Game, Summary) seamlessly. The project demonstrates advanced **Object-Oriented Programming (OOP)** by separating game state (`Game.java`), player data (`Player.java`), and the user interface (`Main.java`).

## ✨ Key Features

### 🎮 Multiple Game Modes
*   **Make a Wish:** Set a custom number of questions to answer at your own pace.
*   **No Mistakes:** "Sudden Death" mode — the game ends immediately upon the first wrong answer.
*   **Take Chances:** You start with **3 Lives**. Lose a life for every mistake; survive as long as you can!
*   **Time Trial:** Race against the clock! Solve as many problems as possible within a custom time limit (e.g., 60 seconds).

### 🏆 Multiplayer & Leaderboards
*   Supports **local multiplayer** for competitive play.
*   Automatically sorts and displays a **Leaderboard** at the end of the session to declare the winner.
*   Detailed post-game summary showing every question, user answer, and correct answer for review.

### 🎨 Custom "Dark Mode" GUI
*   Designed with a modern **Dark Theme** (Charcoal background with Cyan accents) using `javax.swing`.
*   Custom-styled buttons with hover cursors and borders.
*   Visual feedback: Labels turn **Green** for correct answers and **Red** for errors or low time.

## 🛠️ Technical Implementation

*   **Architecture:** Modular design splitting Logic (`Game`), Data (`Player`), and UI (`Main`).
*   **Event Handling:** Uses `ActionListener` and `KeyListeners` for responsive button clicks and input.
*   **State Management:** Tracks complex game states (current player turn, active game mode, lives remaining).
*   **Concurrency:** Implements `javax.swing.Timer` for the "Time Trial" mode to update the UI every second without freezing the application.

## 🚀 How to Run

### Prerequisites
*   **Java Development Kit (JDK) 8** or higher installed.

### Installation
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/tharungopinath/java_math_game.git
    ```
2.  **Compile the source code:**
    ```bash
    javac Main.java Game.java Player.java
    ```
3.  **Run the application:**
    ```bash
    java Main
    ```

## 🔮 Future Improvements
*   [ ] Add database connectivity (SQL) to save high scores permanently.
*   [ ] Implement network socket programming for online multiplayer.
*   [ ] Add sound effects for correct answers and "Game Over" events.

---

**Developed by [Tharunkaarthik Gopinath](https://github.com/tharungopinath)**
