/* Copyright (c) 2007-2017 MIT 6.005/6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

import minesweeper.Cell.State;

/**
 * Multi-player Minesweeper server.
 * 
 * <p>
 * PS4 instructions: you MUST NOT change the specs of main() or runGameServer(),
 * or the implementation of main().
 */
public class GameServer {

	/** Default server port. */
	private static final int DEFAULT_PORT = 4444;
	/** Default board size. */
	private static final int DEFAULT_SIZE = 12;

	/** Socket for receiving client connections. */
	private final ServerSocket serverSocket;
	private final GameBoard board;
	private int numConnections; 

	// TODO: Abstraction function, rep invariant, rep exposure
	
	/*
	 * AF(serverSocket, board, numConnections) = a minesweeeper game server with given socket, board and number of players
	 * 
	 * rep invariant = true
	 * 
	 * safety from rep exposure: all fields private final
	 */

	// Thread safety for instance of GameServer
	// GameServer is thread-safe as all all methods are synchronized and the 2D board makes use of synchronized 
	// lists. Each Cell within the board is immutable.

	// Thread safety for system started by main()
	// main() is thread-safe as it sets up a Queue and the requests are processed sequentially. In addition, it employs GameBoard ADT which 
	// thread-safe. 

	/**
	 * Make a new game server that listens for connections on port.
	 * 
	 * @param port
	 *            port number, requires 0 <= port <= 65535
	 * @throws IOException
	 *             if an error occurs opening the server socket
	 */
	public GameServer(int port, GameBoard board) throws IOException {
		serverSocket = new ServerSocket(port);
		this.board = board;
		this.numConnections = 0;
	}

	/**
	 * Run the server, listening for and handling client connections. Never
	 * returns, unless an exception is thrown.
	 * 
	 * @throws IOException
	 *             if an error occurs waiting for a connection (IOExceptions
	 *             from individual clients do *not* terminate serve())
	 */

	public void serve() throws IOException {
		
		while (true) {
			
			// block until a client connects
			final Socket socket = serverSocket.accept();
			this.numConnections+=1;
			// create a new thread to handle that client
			Thread handler = new Thread(new Runnable() {
				public void run() {
					try {
						try {
							
							handleConnection(socket);
						} finally {
							socket.close();
						}
					} catch (IOException ioe) {
						// this exception wouldn't terminate serve(),
						// since we're now on a different thread, but
						// we still need to handle it
						ioe.printStackTrace();
					}
				}
			});
			// start the thread
			handler.start();
		}
	}

	/**
	 * Handle a single client connection. Returns when client disconnects.
	 * 
	 * @param socket
	 *            socket where the client is connected
	 * @throws IOException
	 *             if the connection encounters an error or terminates
	 *             unexpectedly
	 */
	private void handleConnection(Socket socket) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		// Welcome message
		out.println("Welcome to Minesweeper. Players: " + this.numConnections + " including you. Board: " + this.board.numColumns() + " columns by " + this.board.numRows() +  " rows. Type 'help' for help.");

		try {
			for (String line = in.readLine(); line != null; line = in.readLine()) {

				Optional<String> output = handleRequest(line);

				if (output.isPresent()) {
//					System.out.println(output.get());
					out.print(output.get());
					out.flush();
				}
				else {
					break;
				}
			}
		} finally {
			out.close();
			in.close();
		}
	}

	/**
	 * Handler for client input, performing requested operations and returning
	 * an output message.
	 * 
	 * @param input
	 *            message from client
	 * @return message to client, or null if none
	 */
	private Optional<String> handleRequest(String input) {
		String regex = "(look)|(help)|(bye)|" + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
		if (!input.matches(regex)) {
			// invalid input
			return Optional.of(this.board.help());
		}
		String[] tokens = input.split(" ");
		if (tokens[0].equals("look")) {
			return Optional.of(this.board.look());

		} else if (tokens[0].equals("help")) {
			// 'help' request
			return Optional.of(this.board.help());
		} else if (tokens[0].equals("bye")) {
			return Optional.empty();
		} else {
			int x = Integer.parseInt(tokens[1]);
			int y = Integer.parseInt(tokens[2]);
			if (tokens[0].equals("dig")) {
				return Optional.of(this.board.dig(x, y));
			} else if (tokens[0].equals("flag")) {
				return Optional.of(this.board.flag(x, y));

			} else if (tokens[0].equals("deflag")) {
				return Optional.of(this.board.deflag(x, y));
			}
		}

		throw new UnsupportedOperationException();
	}

	/**
	 * Start a game server using the given arguments.
	 * 
	 * <br>
	 * Usage:
	 * 
	 * <pre>
	 *      minesweeper.GameServer [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]
	 * </pre>
	 * 
	 * <p>
	 * PORT is an optional integer in the range 0 to 65535 inclusive, specifying
	 * the port the server should be listening on for incoming connections. <br>
	 * E.g. "--port 1234" starts the server listening on port 1234.
	 * 
	 * <p>
	 * SIZE_X and SIZE_Y are optional positive integer arguments, specifying
	 * that a random board of size SIZE_X*SIZE_Y should be generated. <br>
	 * E.g. "--size 42,58" starts the server initialized with a random board of
	 * size 42 x 58.
	 * 
	 * <p>
	 * FILE is an optional argument specifying a file pathname where a board has
	 * been stored. If this argument is given, the stored board should be loaded
	 * as the starting board. <br>
	 * E.g. "--file boardfile.txt" starts the server initialized with the board
	 * stored in boardfile.txt.
	 * 
	 * <p>
	 * The board file format, for use with the "--file" option, is specified by
	 * the following grammar:
	 * 
	 * <pre>
	 *      FILE ::= BOARD LINE+
	 *      BOARD ::= X SPACE Y NEWLINE
	 *      LINE ::= (VALUE SPACE)* VALUE NEWLINE
	 *      VALUE ::= "0" | "1"
	 *      X ::= INT
	 *      Y ::= INT
	 *      SPACE ::= " "
	 *      NEWLINE ::= "\n" | "\r" "\n"?
	 *      INT ::= [0-9]+
	 * </pre>
	 * 
	 * The file must contain Y LINEs where each LINE contains X VALUEs. 1
	 * indicates a bomb, 0 indicates no bomb.
	 * 
	 * <p>
	 * If neither --file nor --size is given, generate a random board of size 12
	 * x 12.
	 * 
	 * <p>
	 * Note that --file and --size may not be specified simultaneously.
	 * 
	 * @param args
	 *            arguments as described
	 */
	public static void main(String[] args) {
		int port = DEFAULT_PORT;
		int sizeX = DEFAULT_SIZE;
		int sizeY = DEFAULT_SIZE;
		Optional<File> file = Optional.empty();

		Queue<String> arguments = new LinkedList<>(Arrays.asList(args));
		try {
			while (!arguments.isEmpty()) {
				String flag = arguments.remove();
				try {
					if (flag.equals("--port")) {
						port = Integer.parseInt(arguments.remove());
					} else if (flag.equals("--size")) {
						String[] sizes = arguments.remove().split(",");
						sizeX = Integer.parseInt(sizes[0]);
						sizeY = Integer.parseInt(sizes[1]);
						file = Optional.empty();
					} else if (flag.equals("--file")) {
						sizeX = -1;
						sizeY = -1;
						file = Optional.of(new File(arguments.remove()));
						if (!file.get().isFile()) {
							throw new IllegalArgumentException("file not found: \"" + file.get() + "\"");
						}
					} else {
						throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
					}
				} catch (NoSuchElementException nsee) {
					throw new IllegalArgumentException("missing argument for " + flag);
				} catch (NumberFormatException nfe) {
					throw new IllegalArgumentException("unable to parse number for " + flag);
				}
			}
		} catch (IllegalArgumentException iae) {
			System.err.println(iae.getMessage());
			System.err.println("usage: GameServer [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]");
			return;
		}

		try {
			runGameServer(file, sizeX, sizeY, port);

		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public static List<String> processFile(File f) {
		String text = "";
		String thisLine = "";

		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			while ((thisLine = br.readLine()) != null) {
				text += thisLine;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		List<String> data = new ArrayList<>();

		text = text.replaceAll("\\s", "");

		for (int i = 0; i < text.length(); ++i) {
			data.add(text.substring(i, i + 1));
		}

		return data;

	}

	/**
	 * Start a new GameServer running on the specified port, with either a
	 * random new board or a board loaded from a file.
	 * 
	 * @param file
	 *            if file.isPresent(), start with a board loaded from the
	 *            specified file, according to the input file format defined in
	 *            the documentation for main(..)
	 * @param sizeX
	 *            if (!file.isPresent()), start with a random board with width
	 *            sizeX (and require sizeX > 0)
	 * @param sizeY
	 *            if (!file.isPresent()), start with a random board with height
	 *            sizeY (and require sizeY > 0)
	 * @param port
	 *            the network port on which the server should listen, requires 0
	 *            <= port <= 65535
	 * @throws IOException
	 *             if a network error occurs
	 */
	public static void runGameServer(Optional<File> file, int sizeX, int sizeY, int port) throws IOException {



		GameBoard board;

		if (file.isPresent()) {

			List<String> fileMapping = processFile(file.get());
			int col = Integer.parseInt(fileMapping.get(0));
			int rows = Integer.parseInt(fileMapping.get(1));
			board = new GameBoard(rows, col, false);

			int count = 2;
			for (int i = 0; i < rows; ++i) {
				for (int j = 0; j < col; ++j) {

					int bombPresent = Integer.parseInt(fileMapping.get(count));

					count += 1;

					if (bombPresent == 1) {
						board.getBoard().get(i).set(j, new Cell(j, i, State.UNTOUCHED, true));
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

		GameServer server = new GameServer(port, board);
		server.serve();

	}
}
