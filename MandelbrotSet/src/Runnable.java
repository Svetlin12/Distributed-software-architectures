import org.apache.commons.math3.complex.Complex;

public class Runnable implements java.lang.Runnable {
    private final int threadIndex;
    private final boolean IS_QUIET;

    public Runnable(int threadIndex, boolean isQuiet) {
        this.threadIndex = threadIndex;
        IS_QUIET = isQuiet;
    }

    private Complex calcNewValue(Complex z, Complex c) {
        return z.multiply(z).add(c);
    }

    private short getIndex(Complex c) {
        Complex z = new Complex(0, 0);

        short curr_iter = -128;

        while (z.abs() <= 2 && curr_iter < Mandelbrot.MAX_ITERATIONS) {
            z = calcNewValue(z, c);
            curr_iter++;
        }

        return curr_iter;
    }

    @Override
    public void run() {
        if (!IS_QUIET) {
            System.out.println("Thread-" + threadIndex + " started.");
        }

        long startTime = Mandelbrot.getTimeInMillis();

        if (!Mandelbrot.isByRows) {
            for (int rowNum = threadIndex; rowNum < Mandelbrot.rows; rowNum += Mandelbrot.numThreads) {
                int startFrom = rowNum * Mandelbrot.colWidth;
                int endTo = rowNum * Mandelbrot.colWidth + Mandelbrot.colWidth;
                for (int x = startFrom; x < endTo && x < Mandelbrot.WIDTH; x++) {
                    for (int y = 0; y < Mandelbrot.HEIGHT; y++) {
                        double pixel_x = Mandelbrot.RE_START + ((double) x / Mandelbrot.WIDTH) * (Mandelbrot.RE_END - Mandelbrot.RE_START);
                        double pixel_y = Mandelbrot.IM_START + ((double) y / (Mandelbrot.HEIGHT)) * (Mandelbrot.IM_END - Mandelbrot.IM_START);
                        Complex c = new Complex(pixel_x, pixel_y);

                        // Compute number of iterations
                        Mandelbrot.indices[x][y] = (byte)getIndex(c);
                    }
                }
            }
        }
        else {
            for (int colNum = threadIndex; colNum < Mandelbrot.cols; colNum += Mandelbrot.numThreads) {
                int startFrom = colNum * Mandelbrot.rowWidth;
                int endTo = colNum * Mandelbrot.rowWidth + Mandelbrot.rowWidth;
                for (int y = startFrom; y < endTo && y < Mandelbrot.HEIGHT; y++) {
                    for (int x = 0; x < Mandelbrot.WIDTH; x++) {
                        double pixel_x = Mandelbrot.RE_START + ((double) x / Mandelbrot.WIDTH) * (Mandelbrot.RE_END - Mandelbrot.RE_START);
                        double pixel_y = Mandelbrot.IM_START + ((double) y / (Mandelbrot.HEIGHT)) * (Mandelbrot.IM_END - Mandelbrot.IM_START);
                        Complex c = new Complex(pixel_x, pixel_y);

                        // Compute number of iterations
                        Mandelbrot.indices[x][y] = (byte)getIndex(c);
                    }
                }
            }
        }

        long endTime = Mandelbrot.getTimeInMillis();

        if (!IS_QUIET) {
            System.out.println("Thread-" + threadIndex + " finished. Execution time: " + (endTime - startTime) + " ms.");
        }
    }
}