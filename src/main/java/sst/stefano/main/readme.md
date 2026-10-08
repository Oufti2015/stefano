# Package `sst.stefano.main`

This package contains the JavaFX application entry point and the code that binds the vocabulary trainer to its user
interface.

## Purpose

The `main` package is responsible for the end-user experience: it creates the window, loads the dictionary, presents
vocabulary exercises, checks answers, tracks success/failure statistics, and persists the data back to the dictionary
file.

## Main classes

- `Stefano`: the main JavaFX application. It initializes the scene, loads the dictionary, displays the current word,
  handles user input, and updates the learning statistics.
- `DicoFileManager`: loads and saves the dictionary file, keeps backup copies, and writes the summary metadata used by
  the app.
- `StefanoConstants`: centralizes configuration values, formatted strings, file names, and property loading.

## Typical execution flow

1. `Stefano.main()` launches the JavaFX application.
2. `start(...)` prepares the stage and loads the dictionary.
3. The app picks a vocabulary item and asks the user to translate it.
4. User input is validated and the statistics are updated.
5. The dictionary is saved back to disk so progress is preserved.

## Notes

This package acts as the presentation and orchestration layer. It relies on the `data` and `data.filters` packages for
the vocabulary model and the selection logic that decides which words should be learned next.
