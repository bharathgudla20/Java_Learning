# What is Git?

Git is a popular version control system.

It was created by Linus Torvalds in 2005, and has been maintained by Junio Hamano since then.

It is used for:
- Tracking code changes
- Tracking who made changes
- Coding collaboration

## Key Git Concepts

- **Repository:** A folder where Git tracks your project and its history.
- **Clone:** Make a copy of a remote repository on your computer.
- **Stage:** Tell Git which changes you want to save next.
- **Commit:** Save a snapshot of your staged changes.
- **Branch:** Work on different versions or features at the same time.
- **Merge:** Combine changes from different branches.
- **Pull:** Get the latest changes from a remote repository.
- **Push:** Send your changes to a remote repository.

## Working with Git

1. Initialize Git on a folder, making it a repository.
2. Git creates a hidden folder to keep track of changes in that folder.
3. When a file is changed, added, or deleted, it is considered modified.
4. You select the modified files you want to stage.
5. The staged files are committed, which prompts Git to store a permanent snapshot of the files.
6. Git allows you to see the full history of every commit.
7. You can revert back to any previous commit.
8. Git does not store a separate copy of every file in every commit, but keeps track of changes made in each commit.