# Non-Functional Requirements (NFR)
## Project: Tetris OOSD

## 1. Usability

| ID | Requirement | Description |
|---|---|---|
| NFR-001 | User Interface Simplicity | The game interface shall provide clear navigation between Menu, Configuration, High Score, and Gameplay screens. |
| NFR-002 | Learnability | A new user should understand the basic controls of the game without requiring external documentation. |
| NFR-003 | Accessibility | Buttons, controls, and displayed information should remain readable and understandable during gameplay. |

### Rationale
The Tetris application requires an intuitive interface because users interact directly with menus, configuration options, and keyboard controls. Clear navigation reduces user confusion and improves gameplay experience.

---

# 2. Reliability

| ID | Requirement | Description |
|---|---|---|
| NFR-004 | Stable Execution | The application shall run without crashing during normal gameplay sessions. |
| NFR-005 | Configuration Persistence | User-selected configuration values such as board size, speed, music and SFX settings should remain consistent during gameplay. |
| NFR-006 | Score Accuracy | The system shall correctly calculate and update scores after completed lines. |

### Rationale
Reliability ensures that gameplay functions correctly and that user progress, settings, and scores are handled consistently.

---

# 3. Performance

| ID | Requirement | Description |
|---|---|---|
| NFR-007 | Responsive Controls | Keyboard inputs shall respond immediately during gameplay. |
| NFR-008 | Smooth Rendering | The game shall maintain smooth movement of falling tetromino pieces without noticeable delays. |
| NFR-009 | Efficient Resource Usage | The application should minimise unnecessary CPU and memory consumption while running. |

### Rationale
A real-time game requires fast response times because delays in movement, rotation, or collision detection negatively affect gameplay.

---

# 4. Supportability

| ID | Requirement | Description |
|---|---|---|
| NFR-010 | Maintainable Code Structure | The codebase should follow object-oriented principles with clear separation of responsibilities. |
| NFR-011 | Documentation | Important classes, methods, and design decisions should be documented for future developers. |
| NFR-012 | Testing Support | Components should be structured so that individual features can be tested independently. |

### Rationale
Supportability improves future modification, debugging, and extension of the Tetris application.

---

# 5. Design Constraints (+)

| ID | Requirement | Description |
|---|---|---|
| NFR-013 | Programming Language Constraint | The system shall be implemented using Java. |
| NFR-014 | Framework Constraint | The graphical interface shall use JavaFX. |
| NFR-015 | Object-Oriented Design Constraint | The system shall use object-oriented programming concepts including classes, interfaces, inheritance and abstraction. |

### Rationale
These constraints ensure consistency with the requirements of the Object-Oriented Software Development course.