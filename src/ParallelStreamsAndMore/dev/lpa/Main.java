package ParallelStreamsAndMore.dev.lpa;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

record Person(String firstName, String lastname, int age) {
	private final static String[] firsts = {
			"Able", "Bob", "Charlie", "Donna", "Eve", "Fred"
	};

	private final static String[] lasts = {
			"Norton", "Ohara", "Peterson", "Quincy", "Richardson", "Smith"
	};

	private final static Random random = new Random();

	public Person() {
		this(firsts[random.nextInt(firsts.length)],
				lasts[random.nextInt(lasts.length)],
				random.nextInt(18, 100));
	}

	@Override
	public String toString() {
		return "%s, %s (%d)".formatted(lastname, firstName, age);
	}
}

public class Main {
	public static void main(String[] args) {
		var persons = Stream.generate(Person::new)
				.limit(10)
				.sorted(Comparator.comparing(Person::lastname))
				.toArray();
		for (var person : persons) {
			System.out.println(person);
		}

		System.out.println("-".repeat(20));

		Arrays.stream(persons)
				.limit(10)
				.parallel()
//				.sorted(Comparator.comparing(Person::lastname))
				.forEach(System.out::println);

		System.out.println("-".repeat(20));

		int sum = IntStream.range(1, 101)
				.parallel()
				.reduce(0, Integer::sum);
		System.out.println("The sum of the numbers is: " + sum);

		String humptyDumpty = """
				Humpty Dumpty sat on a wall.
				Humpty Dumpty had a great fall.
				All the king's horses and all the king's men
				couldn't put Humpty together again.
				""";
		System.out.println("-".repeat(20));
		var words = new Scanner(humptyDumpty).tokens().toList();
		words.forEach(System.out::println);
		System.out.println("-".repeat(20));

//		var backTogether = words
//				.stream()
//				.reduce(
//						new StringJoiner(" "),
//						StringJoiner::add,
//						StringJoiner::merge
//						);


//		using the parallelStream()

		var backTogether = words
				.parallelStream()
						.collect(Collectors.joining(" "));
//		OR

//		var backTogether = words
//				.parallelStream()
//						.reduce(" ", (s1, s2) -> s1.concat(s2).concat(" "));
		System.out.println(backTogether);

		Map<String, Long> lastNameCounts =
				Stream.generate(Person::new)
						.limit(10000)
						.parallel()
						.collect(Collectors.groupingByConcurrent(
								Person::lastname,
								Collectors.counting()
						));
		lastNameCounts.entrySet().forEach(System.out::println);

		long total = 0;

		for(var count : lastNameCounts.values()) {
			total+=count;
		}
		System.out.println("Total = " + total);

		System.out.println(lastNameCounts.getClass().getName());

		var lastCounts = Collections.synchronizedMap(
				new ConcurrentSkipListMap<String, Long>());

		Stream.generate(Person::new)
				.limit(10000)
				.parallel()
				.forEach((person) -> lastCounts.merge(person.lastname(),
						1L, Long::sum));
		System.out.println(lastCounts);

		total = 0;
		for(var count : lastCounts.values()) {
			total+=count;
		}
		System.out.println("Total = " + total);

		System.out.println(lastCounts.getClass().getName());
	}
}
