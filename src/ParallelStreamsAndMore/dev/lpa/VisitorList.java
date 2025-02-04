package ParallelStreamsAndMore.dev.lpa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VisitorList {
	private static final ArrayBlockingQueue<Person> newVisitor =
			new ArrayBlockingQueue<>(5);

	public static void main(String[] args) {
		Runnable producer = () -> {
			Person visitor = new Person();
			System.out.println("Adding " + visitor);
			boolean queued = false;
			try {
//				queued = newVisitor.add(visitor);

//				newVisitor.put(visitor);
//				queued = true;

				queued = newVisitor.offer(visitor, 5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception ");
			}
			if (queued) {
				System.out.println(newVisitor);
			} else {
				System.out.println("Queue is full, cannot add " + visitor);
				List<Person> tempList = new ArrayList<>();
				newVisitor.drainTo(tempList);
				List<String> lines = new ArrayList<>();
				tempList.forEach((customer) -> lines.add(customer.toString()));
				lines.add(visitor.toString());

				try {
					Files.write(Path.of("DrainedQueue.txt"), lines, StandardOpenOption.CREATE,
							StandardOpenOption.APPEND);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};

		ScheduledExecutorService producerExecutor =
				Executors.newSingleThreadScheduledExecutor();

		producerExecutor.scheduleWithFixedDelay(producer, 0, 1, TimeUnit.SECONDS);

		while (true) {
			try {
				if (!producerExecutor.awaitTermination(20, TimeUnit.SECONDS)) {
					break;
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		producerExecutor.shutdown();
	}
}
