package com.xxmicloxx.NoteBlockAPI.model;

/**
 * Immutable wrapper representing a note to be played on a given tick.
 */
public record TickNote(Layer layer, Note note) {
}
