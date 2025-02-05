package ParallelStreamsAndMore.dev.lpa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class VisitorList {
	private static final CopyOnWriteArrayList<Person> masterList;
	static {
		masterList = Stream.generate(Person::new)
				.distinct()
				.limit(2500)
				.collect(CopyOnWriteArrayList::new,
						CopyOnWriteArrayList::add,
						CopyOnWriteArrayList::addAll);
	}

	private static final ArrayBlockingQueue<Person> newVisitor =
			new ArrayBlockingQueue<>(5);

	public static void main(String[] args) {
		Runnable producer = () -> {
			Person visitor = new Person();
			System.out.println("Queueing " + visitor);
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
//				System.out.println(newVisitor);
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

		Runnable consumer = () -> {
			String threadName = Thread.currentThread().getName();
			System.out.println(threadName + " polling queue " + newVisitor.size());
			Person visitor = newVisitor.poll();
			if(visitor != null) {
				System.out.println(threadName + " " + visitor);
				if(!masterList.contains(visitor)) {
					masterList.add(visitor);
					System.out.println("---> New visitor gets coupon " + visitor);
				}
			}
			System.out.println(threadName + " done " + newVisitor.size());
		};

		ScheduledExecutorService producerExecutor =
				Executors.newSingleThreadScheduledExecutor();
		producerExecutor.scheduleWithFixedDelay(producer, 0, 1, TimeUnit.SECONDS);

		ScheduledExecutorService consumerPool =
				Executors.newScheduledThreadPool(3);
		for(int i = 0; i < 3; i++) {
			consumerPool.scheduleWithFixedDelay(consumer,6, 3, TimeUnit.SECONDS);
		}

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

		while (true) {
			try {
				if(!consumerPool.awaitTermination(3, TimeUnit.SECONDS)) {
					break;
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		consumerPool.shutdown();
	}
}
