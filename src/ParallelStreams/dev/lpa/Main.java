package ParallelStreams.dev.lpa;

import java.util.Arrays;
import java.util.Random;

public class Main {
	public static void main(String[] args) {
		int numberLength = 100_000_000;
		long[] numbers = new Random()
				.longs(numberLength, 1, numberLength)
				.toArray();

		int delta = 0;
		int iterations = 25;
		for(int i = 0; i < iterations; i++) {
			long start = System.nanoTime();
			double averageSerial = Arrays.stream(numbers).average().orElseThrow();
			long elapsedSerial = System.nanoTime() - start;
//		System.out.printf("Ave = %.2f, elapsed = %d nanos or %.2f %n", averageSerial,
//				elapsedSerial, elapsedSerial / 1000000.0);

			start = System.nanoTime();
			double averageParallel = Arrays.stream(numbers).parallel().average().orElseThrow();
			long elapsedParallel = System.nanoTime() - start;
			delta += (elapsedSerial - elapsedParallel);
//		System.out.printf("Ave parallel = %.2f, elapsed parallel = %d nanos or %.2f ms%n", averageParallel,
//				elapsedParallel, elapsedParallel / 1000000.0);
		}

		System.out.printf("Parallel is [%d] nanos or [%.2f] ms faster on average %n", delta/iterations,
				delta/1000000.0);
	}
}
