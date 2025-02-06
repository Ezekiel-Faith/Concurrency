package ThreadProblems.dev.lpa;

record Participant(String name, String searchingFor, Maze maze, int[] startingPosition) {
	Participant{
		maze.moveLocation(startingPosition[0], startingPosition[1], name);
	}
}

class ParticipantThread extends Thread {
	public final Participant participant;

	public ParticipantThread(Participant participant) {
		this.participant = participant;
	}

	@Override
	public void run() {
		int[] lastSpot = participant.startingPosition();
		Maze maze = participant.maze();

		while (true) {

		}
	}
}

public class MazeRunner {
	public static void main(String[] args) {

	}
}
