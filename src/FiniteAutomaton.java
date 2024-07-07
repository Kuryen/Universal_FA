import java.io.*;
import java.util.*;

/**
 * Class representing a finite automaton with dynamic alphabet handling, dead state detection, and file input capabilities.
 */
public class FiniteAutomaton {
    private int initialState;
    private Set<Integer> finalStates;
    private Set<Character> alphabet;
    private Map<Integer, Map<Character, Integer>> transitionTable;
    private Set<Integer> deadStates;

    /**
     * Constructor for FiniteAutomaton.
     *
     * @param initialState The initial state of the automaton.
     * @param finalStates  A set of final states.
     * @param alphabet     A set of valid input symbols.
     */
    public FiniteAutomaton(int initialState, Set<Integer> finalStates, Set<Character> alphabet) {
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.alphabet = alphabet;
        this.transitionTable = new HashMap<>();
        this.deadStates = new HashSet<>();
    }

    /**
     * Adds a transition to the automaton. Validates if the symbol is in the alphabet.
     *
     * @param state     The current state.
     * @param symbol    The input symbol.
     * @param nextState The next state.
     */
    public void addTransition(int state, char symbol, int nextState) {
        if (!alphabet.contains(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is not in the alphabet.");
        }
        this.transitionTable.putIfAbsent(state, new HashMap<>());
        this.transitionTable.get(state).put(symbol, nextState);
        checkForDeadState(state); // Check if adding this state results in a dead state
    }

    /**
     * Determines whether a string is accepted by the automaton.
     *
     * @param input The string to be tested.
     * @return true if accepted, false otherwise.
     */
    public boolean accepts(String input) {
        if (input.isEmpty()) {
            return finalStates.contains(initialState);
        }
        int currentState = this.initialState;
        for (char symbol : input.toCharArray()) {
            if (!alphabet.contains(symbol)) {
                return false; // Reject if symbol is not in the alphabet.
            }
            if (deadStates.contains(currentState)) {
                return false; // Early exit if current state is a dead state.
            }
            Map<Character, Integer> transitions = this.transitionTable.getOrDefault(currentState, new HashMap<>());
            if (!transitions.containsKey(symbol)) {
                return false; // No transition for this symbol
            }
            currentState = transitions.get(symbol);
        }
        return finalStates.contains(currentState);
    }

    /**
     * Identifies and marks dead states after each transition is added.
     *
     * @param state The state to check.
     */
    private void checkForDeadState(int state) {
        // Start a simple BFS from `state` to find any final state
        if (!transitionTable.containsKey(state) || finalStates.contains(state)) {
            return; // No need to check if it's already a final state or no transitions defined
        }
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(state);
        while (!queue.isEmpty()) {
            int currentState = queue.poll();
            if (finalStates.contains(currentState)) {
                return; // This state is not dead as it can reach a final state
            }
            visited.add(currentState);
            Map<Character, Integer> transitions = this.transitionTable.getOrDefault(currentState, new HashMap<>());
            for (int nextState : transitions.values()) {
                if (!visited.contains(nextState) && !queue.contains(nextState)) {
                    queue.add(nextState);
                }
            }
        }
        // If no final states are reached, mark this as a dead state
        this.deadStates.add(state);
    }

    /**
 * Processes the file containing finite automaton configurations and test strings.
 * Reads each line to find the start of a new automaton configuration, indicated by "FA:",
 * and initializes processing for each finite automaton found.
 *
 * @param filename The path to the file containing the automaton configurations.
 * @throws IOException If there is an error reading the file.
 */
private static void processFile(String filename) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;
    while ((line = reader.readLine()) != null) {
        if (line.startsWith("FA:")) {
            FiniteAutomaton fa = readFiniteAutomaton(reader);
            testFiniteAutomaton(fa, reader);
        }
    }
    reader.close();
}

/**
 * Reads the configuration for a finite automaton from a buffered reader, including
 * the initial state, final states, alphabet, and transitions.
 *
 * @param reader The BufferedReader object to read the FA configuration from.
 * @return FiniteAutomaton Returns a new instance of FiniteAutomaton configured based on the input.
 * @throws IOException If there is an error during reading from the reader.
 */
private static FiniteAutomaton readFiniteAutomaton(BufferedReader reader) throws IOException {
    Set<Integer> finalStates = new HashSet<>();
    Set<Character> alphabet = new HashSet<>();
    int initialState = Integer.parseInt(reader.readLine().trim()); // Read initial state
    String[] finals = reader.readLine().trim().split(","); // Read final states
    for (String finalState : finals) {
        finalStates.add(Integer.parseInt(finalState.trim()));
    }
    String alpha = reader.readLine().trim(); // Read alphabet
    for (char c : alpha.toCharArray()) {
        alphabet.add(c);
    }
    FiniteAutomaton fa = new FiniteAutomaton(initialState, finalStates, alphabet);

    String transition;
    while (!(transition = reader.readLine().trim()).equals("END")) {
        String[] parts = transition.split(",");
        int state = Integer.parseInt(parts[0].trim());
        char symbol = parts[1].trim().charAt(0);
        int nextState = Integer.parseInt(parts[2].trim());
        fa.addTransition(state, symbol, nextState);
    }
    return fa;
}

/**
 * Tests a finite automaton by reading test strings from a BufferedReader until "END" is encountered.
 * For each test string, it prints whether the automaton accepts or rejects the string.
 *
 * @param fa The FiniteAutomaton to test.
 * @param reader The BufferedReader from which test strings are read.
 * @throws IOException If there is an error reading from the reader.
 */
private static void testFiniteAutomaton(FiniteAutomaton fa, BufferedReader reader) throws IOException {
    String testString;
    System.out.println("Testing Finite Automaton:");
    while (!(testString = reader.readLine().trim()).equals("END")) {
        boolean result = fa.accepts(testString);
        System.out.printf("Test string: '%s' -> Result: %s\n", testString, result ? "Accept" : "Reject");
    }
}


    /**
     * Main class to execute the FA tests and manage file input.
     */

    public static void main(String[] args) {
        try {
            // Assume the FA configuration and test strings are in the same file for simplicity
            processFile("fa_configuration.txt");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}




