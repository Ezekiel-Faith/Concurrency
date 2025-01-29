package synchronizationChallenge;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
	public static void main(String[] args) {
//		// Create a new order
//		Order order = new Order("Sneakers", 5);
//
//		// Print order details
//		System.out.println(order);
//
//		// Print estimated processing time
//		System.out.printf("Estimated Processing Time: %d minutes%n", order.estimatedProcessingTimeInMins());

		ShoeWarehouse warehouse = new ShoeWarehouse();
		warehouse.productList.put("ShoeType1", 50);
		warehouse.productList.put("ShoeType2", 30);
		warehouse.productList.put("ShoeType3", 20);
		warehouse.productList.put("ShoeType4", 10);
		warehouse.productList.put("ShoeType5", 15);

		var executors = Executors.newCachedThreadPool();
		// Consumer thread (each thread will process 5 orders)
		Runnable consumerTask = () -> {
			for (int i = 0; i < 5; i++) {
				warehouse.fulfillOrder();
				try {
					Thread.sleep(1000); // Simulate time it takes to fulfill each order
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		};

		// Use submit to get Futures for consumer tasks
		Future<?> consumerFuture1 = executors.submit(consumerTask);
		Future<?> consumerFuture2 = executors.submit(consumerTask);

		// Producer task (generates 10 orders)
		try {
			var producer = executors.submit(() -> {
				Random rand = new Random();
				for (int i = 0; i < 10; i++) {
					String shoeType = "ShoeType" + (rand.nextInt(5) + 1); // Random shoe type from ShoeType1 to ShoeType5
					int quantity = rand.nextInt(5) + 1; // Random quantity between 1 and 5
					var order = new Order(shoeType, quantity);    // Generate the order with the auto-generated orderID

					warehouse.receiverOrder(order); // Add order to warehouse
					try {
						Thread.sleep(500); // Simulate time between orders
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			});

			// Wait for the producer task to finish before moving on
			producer.get();  // This waits for the producer task to finish

			// Wait for the consumer tasks to finish
			consumerFuture1.get(); // Wait for the first consumer task to finish
			consumerFuture2.get(); // Wait for the second consumer task to finish
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// After all tasks are complete, shutdown the executor

			warehouse.shutdown();
			executors.shutdown();
		}
		/*
		// Consumer thread (each thread will process 5 orders)
		Runnable consumerTask = () -> {
			for (int i = 0; i < 5; i++) {
				warehouse.fulfillOrder();
				try {
					Thread.sleep(1000); // Simulate time it takes to fulfill each order
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		};

		// Start two consumer threads
		Thread consumer1 = new Thread(consumerTask);
		Thread consumer2 = new Thread(consumerTask);
		consumer1.start();
		consumer2.start();

		executors.execute(consumerTask);
		executors.execute(consumerTask);

		// Producer thread (generates 10 orders)
		Thread producer = new Thread(() -> {
			Random rand = new Random();
			for (int i = 0; i < 10; i++) {
				String shoeType = "ShoeType" + (rand.nextInt(5) + 1); // Random shoe type from ShoeType1 to ShoeType5
				int quantity = rand.nextInt(5) + 1; // Random quantity between 1 and 5
				Order order = new Order(shoeType, quantity);  // Generate the order with the auto-generated orderID

				warehouse.receiverOrder(order); // Call the receiverOrder method to add the order
				try {
					Thread.sleep(500); // Simulate time between orders
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});

		// Start the producer thread
		producer.start();
	*/
	}
}
