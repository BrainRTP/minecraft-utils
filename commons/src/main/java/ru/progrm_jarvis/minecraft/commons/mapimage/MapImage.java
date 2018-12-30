package ru.progrm_jarvis.minecraft.commons.mapimage;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;

/**
 * Image on a map.
 */
public interface MapImage {

    /**
     * Maximal width of a map allowed by Minecraft.
     */
    int WIDTH = 128,
    /**
     * Maximal height of a map allowed by Minecraft.
     */
    HEIGHT = 128,
    /**
     * Maximal (and only possible) amount of pixels on a map allowed by Minecraft.
     */
    PIXELS = WIDTH * HEIGHT;

    /**
     * Gets the width of this map image.
     *
     * @return this map image's width
     */
    int getWidth();

    /**
     * Gets the height of this map image.
     *
     * @return this map image's height
     */
    int getHeight();

    /**
     * Gets the non-buffered drawer for this image.
     *
     * @return non-buffered drawer for this image
     */
    Drawer drawer();

    /**
     * Gets the buffered drawer for this image.
     *
     * @return buffered drawer for this image
     */
    BufferedDrawer bufferedDrawer();

    /**
     * Makes all pixels of the specified array blank ({@link MapImageColor#NO_COLOR_CODE}).
     *
     * @param pixels 2-dimensional array of pixels to make blank
     * @return passed array of pixels made blank
     */
    static byte[][] blankPixels(final byte[][] pixels) {
        for (val bytes : pixels) Arrays.fill(bytes, MapImageColor.NO_COLOR_CODE);

        return pixels;
    }

    /**
     * An object responsible for changing map's content.
     *
     * @apiNote most non-primitive methods have default implementations based on primitive ones
     */
    interface Drawer {

        /**
         * Assures that the X-coordinate is inside the allowed bounds [{@code 0}; {@link MapImage#WIDTH}).
         *
         * @param x X-coordinate to check
         * @throws IllegalArgumentException if the X-coordinate is not between the bounds
         */
        static void checkX(final int x) {
            if (x < 0) throw new IllegalArgumentException("X-coordinate should be non-negative");
            if (x >= WIDTH) throw new IllegalArgumentException("X-coordinate should be less than " + WIDTH);
        }

        /**
         * Assures that the Y-coordinate is inside the allowed bounds [{@code 0}; {@link MapImage#HEIGHT}).
         *
         * @param y Y-coordinate to check
         * @throws IllegalArgumentException if the Y-coordinate is not between the bounds
         */
        static void checkY(final int y) {
            if (y < 0) throw new IllegalArgumentException("Y-coordinate should be non-negative");
            if (y >= HEIGHT) throw new IllegalArgumentException("Y-coordinate should be less than " + HEIGHT);
        }

        /**
         * Makes the specified X-coordinate surely be inside the allowed bounds [{@code 0}; {@link MapImage#WIDTH}).
         *
         * @param x X-coordinate to bound
         * @return bounded X-coordinate
         */
        static int boundX(final int x) {
            if (x < 0) return 0;
            if (x >= WIDTH) return WIDTH - 1;
            return x;
        }

        /**
         * Makes the specified Y-coordinate surely be inside the allowed bounds [{@code 0}; {@link MapImage#HEIGHT}).
         *
         * @param y Y-coordinate to bound
         * @return bounded Y-coordinate
         */
        static int boundY(final int y) {
            if (y < 0) return 0;
            if (y >= HEIGHT) return HEIGHT - 1;
            return y;
        }

        /**
         * Draws a pixel of the specified color at given coordinates.
         *
         * @param x X-coordinate to draw the pixel at
         * @param y Y-coordinate to draw the pixel at
         * @param color color of the pixel
         * @return this drawer for chaining
         */
        @Contract("_, _, _ -> this")
        Drawer px(int x, int y, final byte color);

        /**
         * Draws a line between two points.
         *
         * @param x1 the first point's X-coordinate
         * @param y1 the first point's Y-coordinate
         * @param x2 the second point's X-coordinate
         * @param y2 the second point's Y-coordinate
         * @param color color of the line
         * @return this drawer for chaining
         */
        @Contract("_, _, _, _, _ -> this")
        @SuppressWarnings("Duplicates") // swapping
        default Drawer line(int x1, int y1, int x2, int y2, final byte color) {
            if (x1 > x2) { // swap x's
                val oldX2 = x2;
                x2 = x1;
                x1 = oldX2;
            }

            if (y1 > y2) { // swap y's
                val oldY2 = y2;
                y2 = y1;
                y1 = oldY2;
            }

            val dX = x2 - x1;
            val dY = y2 - y1;

            // stepping should happen by the biggest delta to affect all rows / columns on it
            if (dX > dY) {
                // dX is bigger, step by it
                val stepY = dY / (float) dX;
                float y = y1;
                for (/* use x1 for x */; x1 <= x2; x1++, y += stepY) px(x1, (int) y, color);
            } else {
                // dY is bigger or same, step by it
                val stepX = dX / (float) dY;
                float x = x1;
                for (/* use y1 for y */; y1 <= y2; y1++, x += stepX) px((int) x, y1, color);
            }

            return this;
        }

        /**
         * Draws a rectangle by given coordinates and color.
         *
         * @param x1 X-coordinate of the first rectangle point
         * @param y1 Y-coordinate of the first rectangle point
         * @param x2 X-coordinate of the second rectangle point
         * @param y2 T-coordinate of the second rectangle point
         * @param color color of the round
         * @return this drawer for chaining
         */
        @Contract("_, _, _, _, _ -> this")
        @SuppressWarnings("Duplicates") // swapping
        default Drawer rect(int x1, int y1, int x2, int y2, final byte color) {
            checkX(x1);
            checkY(y1);
            checkX(x2);
            checkY(y2);

            if (x1 > x2) { // swap x's
                val oldX2 = x2;
                x2 = x1;
                x1 = oldX2;
            }

            if (y1 > y2) { // swap y's
                val oldY2 = y2;
                y2 = y1;
                y1 = oldY2;
            }

            for (var x = x1; x <= x2; x++) for (var y = y1; y <= y2; y++) px(x, y, color);

            return this;
        }

        /**
         * Draws a round with the center specified of given radius and color.
         *
         * @param centerX X-coordinate of the round's center
         * @param centerY Y-coordinate of the round's center
         * @param radius radius of the round
         * @param color color of the round
         * @return this drawer for chaining
         */
        @Contract("_, _, _, _ -> this")
        default Drawer round(final int centerX, final int centerY, final int radius, final byte color) {
            final int
                    minX = boundX(centerX - radius), maxX = boundX(centerX + radius),
                    minY = boundY(centerY - radius), maxY = boundY(centerY + radius);

            val squaredRadius = radius * radius;

            for (var x = minX; x <= maxX; x++) {
                var squaredDX = x - centerX; // find delta
                squaredDX *= squaredDX; // dy now stores a squared value

                for (var y = minY; y <= maxY; y++) {
                    var squaredDY = y - centerY; // find delta
                    squaredDY *= squaredDY; // dy now stores a squared value

                    // Pythagoras theorem с² = a² + b² ~~> radius² = Δx² + Δy²
                    if (squaredRadius <= squaredDX + squaredDY) px(x, y, color);
                }
            }

            return this;
        }

        /**
         * Fills the image with the specified color.
         *
         * @param color color to fill the image with
         * @return this drawer for chaining
         *
         * @apiNote this method doesn't provide default implementation because it is suboptmal in most cases
         * to set similar pixels one by one.
         * There is also no guarantees (although it is in most cases )
         */
        @Contract("_ -> this")
        Drawer fill(byte color);
    }

    /**
     * A drawer which stores all changes releasing them only when required.
     */
    interface BufferedDrawer extends Drawer {

        /**
         * Gets the least X-coordinate of changed image segment.
         *
         * @return the least X-coordinate of changed image segment or {@link Delta#NONE} if no pixels were changed
         */
        int getLeastChangedX();

        /**
         * Gets the least Y-coordinate of changed image segment.
         *
         * @return the least Y-coordinate of changed image segment or {@link Delta#NONE} if no pixels were changed
         */
        int getLeastChangedY();

        /**
         * Gets the most X-coordinate of changed image segment.
         *
         * @return the most X-coordinate of changed image segment or {@link Delta#NONE} if no pixels were changed
         */
        int getMostChangedX();

        /**
         * Gets the most Y-coordinate of changed image segment.
         *
         * @return the most Y-coordinate of changed image segment or {@link Delta#NONE} if no pixels were changed
         */
        int getMostChangedY();

        /**
         * Gets the delta of the image which this drawer is having.
         *
         * @return delta of the image
         */
        Delta getDelta();

        /**
         * Disposes the image. Disposal means applying all changes to the source Map image.
         *
         * @return this drawer for chaining
         */
        @Contract(" -> this")
        Drawer dispose();

        /**
         * Gets the delta of the image which this drawer is having disposing the image.
         *
         * @return delta of the image
         */
        default Delta pickDelta() {
            val delta = getDelta();
            dispose();

            return delta;
        }
    }

    /**
     * Object containing data about changed image part.
     *
     * @see BufferedDrawer most common use-case of delta
     */
    interface Delta {

        /**
         * The value returned by {@link #leastX()} and {@link #leastY()} whenever there are no changes.
         */
        int NONE = -1;

        /**
         * The value returned by {@link #pixels()} whenever there are no changes.
         */
        byte[][] NO_PIXELS = new byte[0][0];

        /**
         * An empty delta. This should be used whenever there were no changes to the image.
         */
        Empty EMPTY = new Empty();

        /**
         * Retrieves whether the delta is empty (there were no changes to the image) or not.
         *
         * @return {@code false} if at least one pixel differs from the image and {@code true} otherwise
         */
        boolean isEmpty();

        /**
         * Gets the pixels changed (columns, rows).
         *
         * @return pixels changed or {@link #NO_PIXELS} if none were changed
         */
        byte[][] pixels();

        /**
         * Gets the least X-coordinate of the changed segment
         *
         * @return the least X-coordinate of the changed segment or {@link #NONE} if none were changed
         */
        int leastX();

        /**
         * Gets the least Y-coordinate of the changed segment
         *
         * @return the least Y-coordinate of the changed segment or {@link #NONE} if none were changed
         */
        int leastY();

        /**
         * Creates new delta.
         *
         * @param pixels pixels changed
         * @param leastX least X-coordinate of the affected segment
         * @param leastY least Y-coordinate of the affected segment
         * @return empty delta if {@code pixels} is empty or {@code leastX} or {@code leastY} is {@link Delta#NONE}
         * and non-empty delta otherwise
         */
        static Delta of(final byte[][] pixels, final int leastX, final int leastY) {
            if (pixels.length == 0 || leastX == NONE || leastY == NONE) return EMPTY;
            return new NonEmpty(pixels, leastX, leastY);
        }

        /**
         * Empty delta. There is no need to instantiate it for each empty delta, use {@link Delta#EMPTY} instead.
         */
        @Value
        class Empty implements Delta {

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public byte[][] pixels() {
                return NO_PIXELS;
            }

            @Override
            public int leastX() {
                return -1;
            }

            @Override
            public int leastY() {
                return -1;
            }
        }

        /**
         * Non-empty delta. Usage of this class guarantees that it is not empty
         * (its constructor <b>does not perform checks</b> of {@code pixels} emptiness
         * and {@code leastX}, {@code leastY} ranges).
         */
        @Value
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE)
        class NonEmpty implements Delta {

            byte[][] pixels;

            int leastX, leastY;

            @Override
            public boolean isEmpty() {
                return false;
            }
        }
    }
}