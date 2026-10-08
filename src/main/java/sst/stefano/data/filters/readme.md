# Package `sst.stefano.data.filters`

This package defines the logic used to decide whether a word should be considered known or still unknown.

## Purpose

The trainer needs a strategy for choosing which vocabulary items to present next. This package encapsulates that logic
behind small, reusable filter and selector classes.

## Key types

- `WordFilter`: common contract for filters operating on a `WordList`.
- `WordSelector`: abstract selector that decides whether a specific word is still unknown.
- `WordSelectorModule`: factory that chooses the active strategy.
- `ClassicWordFilter`: the default filter that evaluates the overall exercise average and sorts words into known/unknown
  groups.
- `AverageWordSelector`: uses success rate and exercise frequency to identify weaker words.
- `SimpleWordSelector`: a simpler heuristic based on a threshold comparison.
- `HundredFirstRareWordsFilter`: prioritizes the first 100 least-used words as the unknown set when the dictionary is
  too well known.

## Workflow

1. `ClassicWordFilter` computes averages from the current vocabulary state.
2. `WordSelectorModule` chooses the active selector strategy.
3. Each word is evaluated with `isWordUnknown(...)`.
4. The dictionary is split into `knownWordList` and `unknownWordList`.
5. `WordList.random()` uses the resulting pools to offer the next exercise.

## Design note

This package follows a small strategy pattern: the filtering behavior can be swapped without changing the rest of the
application logic. That makes it easier to tune the teaching algorithm or compare different learning approaches.
