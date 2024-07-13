import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The UniversalFA class represents a finite automaton. It provides functionality to load the automaton configuration from a file,
 * process test strings, and print the results.
 */
public class UniversalFA {
    private int initialState = 0;
    private Set<Integer> finalStates;
    private Set<Character> alphabet;
    private Map<Pair<Integer, Character>, Integer> transitionTable;
    private List<String> testStrings;

    /**
     * Constructs a UniversalFA object by initializing data structures and loading the configuration from the specified file path.
     *
     * @param configPath the file path of the FA configuration
     * @throws IOException if there is an error reading the file
     */
    public UniversalFA(String configPath) throws IOException {
        this.finalStates = new HashSet<>();
        this.alphabet = new HashSet<>();
        this.transitionTable = new LinkedHashMap<>();
        this.testStrings = new ArrayList<>();
        loadConfiguration(configPath);
    }

    /**
     * Loads the FA configuration from a specified file.
     * This method sets up the initial state, final states, alphabet, transitions, and test strings based on file content.
     *
     * @param path the file path of the FA configuration
     * @throws IOException if there is an error reading the file
     */

private void loadConfiguration(String path) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    String line;
    boolean readingTransitions = false;
    boolean readingTestStrings = false;

    while ((line = reader.readLine()) != null) {
        line = line.trim();  // Trim any leading or trailing spaces
        if (line.startsWith("num_states:")) {
            // This line is currently unused
            continue;
        } else if (line.startsWith("final_states:")) {
            String[] states = line.split(":")[1].trim().split(",");
            for (String state : states) {
                finalStates.add(Integer.parseInt(state.trim()));  // Ensure spaces are trimmed
            }
        } else if (line.startsWith("alphabet:")) {
            alphabet.clear();
            String[] characters = line.split(":")[1].replaceAll("\\s+", "").split(",");
            for (String c : characters) {
                if (!c.isEmpty()) {
                    alphabet.add(c.charAt(0));
                }
            }
        } else if (line.startsWith("transitions:")) {
            readingTransitions = true;
        } else if (line.startsWith("test_strings:")) {
            readingTransitions = false;
            readingTestStrings = true;
        } else if (readingTransitions && line.contains(",")) {
            String[] parts = line.split(",\\s*");  // Allow optional spaces after commas
            int state = Integer.parseInt(parts[0].trim());
            String inputRange = parts[1].trim();
            int nextState = Integer.parseInt(parts[2].trim());

            if (inputRange.contains("-")) {
                char start = inputRange.charAt(0);
                char end = inputRange.charAt(2);
                for (char c = start; c <= end; c++) {
                    transitionTable.put(new Pair<>(state, c), nextState);
                }
            } else {
                char input = inputRange.charAt(0);
                transitionTable.put(new Pair<>(state, input), nextState);
            }
        } else if (readingTestStrings && !line.isEmpty()) {
            testStrings.add(line);
        }
    }
    reader.close();
}

   /**
     * Processes and prints the results for all test strings by evaluating them through the FA.
     */

    public void processAndPrintResults() {
    System.out.println("Inputted Finite State Automaton Info:");

    // Printing set of states
    System.out.print("1) set of states: {");
    Set<Integer> states = new HashSet<>();
    transitionTable.forEach((key, value) -> {
        states.add(key.first);
        states.add(value);
    });
    System.out.print(states.stream().map(state -> "state " + state).collect(Collectors.joining(", ")));
    System.out.println("}, initial state is state " + initialState + " (default).");

    // Printing set of final states
    System.out.print("2) set of final state(s): {");
    System.out.print(finalStates.stream().map(state -> "state " + state).collect(Collectors.joining(", ")));
    System.out.println("}");

    // Printing alphabet set
    System.out.println("3) alphabet set: {" + alphabet.stream().map(String::valueOf).collect(Collectors.joining(", ")) + "}");
    System.out.println("4) transitions:");
    transitionTable.forEach((key, value) -> System.out.println("   " + key.first + " " + key.second + " " + value));

    // Processing and printing test strings results
    System.out.println("Results of test strings:");
    for (String string : testStrings) {
        if (string.equals("Λ")) {  // Check if the string is the empty string notation
            // Directly determines the result based on whether the initial state is a final state
            String result = finalStates.contains(initialState) ? "Accept" : "Reject";
            System.out.println("Λ: " + result);
        } else {
            String result = processString(string) ? "Accept" : "Reject";
            System.out.println(string + ": " + result);
        }
    }
}



    /**
     * Processes a single string through the FA.
     *
     * @param string the input string to be processed
     * @return true if the string is accepted by the FA, false otherwise
     */
    private boolean processString(String string) {
        int currentState = initialState;
        for (int i = 0; i < string.length(); i++) {
            char symbol = string.charAt(i);
            if (!alphabet.contains(symbol)) {
                return false;
            }
            Pair<Integer, Character> transitionKey = new Pair<>(currentState, symbol);
            if (!transitionTable.containsKey(transitionKey)) {
                return false;
            }
            currentState = transitionTable.get(transitionKey);
        }
        return finalStates.contains(currentState);
    }

    /**
     * Main method to demonstrate the functionality of the UniversalFA class.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            UniversalFA fa = new UniversalFA("C:\\Users\\zazor\\OneDrive\\Desktop\\CS 3650\\fa_configuration2.txt");
            fa.processAndPrintResults();
        } catch (IOException e) {
            System.out.println("Error reading configuration file: " + e.getMessage());
        }
    }

    /**
     * Helper class to represent a pair of values.
     * This class is used to store transitions of the finite automaton.
     *
     * @param <K> the type of the first component of the pair
     * @param <V> the type of the second component of the pair
     */
    private static class Pair<K, V> {
        K first;
        V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            return first.hashCode() ^ second.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return first.equals(pair.first) && second.equals(pair.second);
        }
    }
}


