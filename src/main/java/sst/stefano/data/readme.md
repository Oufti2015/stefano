# Package `sst.stefano.data`

This package contains the domain model used by the Stefano vocabulary trainer.

## Purpose

The classes in this package represent a single vocabulary item and the collection of all items that can be tested. They
also keep track of usage, success, failure, and streak statistics.

## Main classes

- `Word`: represents a French/Italian pair and stores the exercise statistics for that word.
- `WordList`: manages all vocabulary entries, tracks known vs. unknown words, and selects random words to quiz the user.

## Behavior

- A `Word` records how often it has been used, how many times the answer failed, how many times it succeeded, and its
  current straight streak.
- `WordList` maintains the full dictionary, prevents duplicate entries, and computes which words are considered learned
  or still unknown.
- The selection logic delegates to the filtering strategies in the `sst.stefano.data.filters` subpackage.

## Why it matters

This package is the core of the learning logic: it keeps the vocabulary data consistent and makes the next exercise
predictable while respecting the trainer's learning heuristics.
