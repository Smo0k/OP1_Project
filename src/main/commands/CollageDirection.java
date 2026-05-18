package main.commands;

/**
 * Represents the direction in which a collage is created.
 * <p>
 * The direction determines how two images are combined:
 * </p>
 * <ul>
 *     <li>{@link #HORIZONTAL} - images are placed side by side</li>
 *     <li>{@link #VERTICAL} - images are placed one above the other</li>
 * </ul>
 */
public enum CollageDirection {

    /**
     * Places images next to each other horizontally.
     */
    HORIZONTAL,

    /**
     * Places images one above the other vertically.
     */
    VERTICAL
}