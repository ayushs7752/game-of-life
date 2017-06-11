package gol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

public class Main {

	private static final int DEFAULT_SIZE = 20;


	private static List<String> processFile(File f) {
		String text = "";
		String thisLine = "";
		String sizeX = "";
		String sizeY = "";

		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			int i = 0;

			while ((thisLine = br.readLine()) != null) {
				if (i == 0) {
					sizeX = thisLine.replaceAll("\\s", "");

				}
				if (i == 1) {
					sizeY = thisLine.replaceAll("\\s", "");
				} else {
					text += thisLine;

				}
				i += 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		text = text.replaceAll("\\s", "");

		List<String> output = new ArrayList<>(Arrays.asList(sizeX, sizeY, text));
		return output;

	}

	public static GameBoard boardFromFile(Optional<File> file, int sizeX, int sizeY) {
		GameBoard board;

		if (file.isPresent()) {

			List<String> fileMapping = processFile(file.get());

			int col = Integer.parseInt(fileMapping.get(0));
			int rows = Integer.parseInt(fileMapping.get(1));
			board = new GameBoard(rows, col, false);
			String boardDesign = fileMapping.get(2);

			int count = 0;
			for (int i = 0; i < rows; ++i) {
				for (int j = 0; j < col; ++j) {

					int isAlive = Integer.parseInt(boardDesign.substring(count, count + 1));

					count += 1;

					if (isAlive == 1) {
						board.getBoard().get(i).set(j, new Cell(j, i, true));
					}
				}
			}

		}

		else if (sizeX > 0 && sizeY > 0) {

			board = new GameBoard(sizeY, sizeX, true);

		}

		else {
			board = new GameBoard(DEFAULT_SIZE, DEFAULT_SIZE, true);
		}
		return board;
	}

	public static void main(String[] args) {

		Timer timer = new Timer();

		GameBoard board = boardFromFile(Optional.of(new File("src/board/gliderGun.txt")), -1, -1);

		timer.schedule(new runGame(board), 0, 200);

	}

}
