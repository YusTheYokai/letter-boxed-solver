import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Solver {

    private static final int MAX_WORDS = 5;

    private static List<Character> left = Arrays.asList('m', 'h', 'l');
    private static List<Character> top = Arrays.asList('a', 'k', 'o');
    private static List<Character> right = Arrays.asList('i', 'b', 't');
    private static List<Character> bottom = Arrays.asList('r', 'n', 'c');
    private static List<Character> all = new ArrayList<>();

    private static Predicate<String> isLongEnough = word -> word.length() > 5;

    private static Map<Character, List<String>> words = new HashMap<>();

    private static List<List<String>> chains = new ArrayList<>();

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        all.addAll(left);
        all.addAll(top);
        all.addAll(right);
        all.addAll(bottom);

        List<String> wordList = Files.readAllLines(Paths.get("words")).stream()
                .filter(isLongEnough)
                .filter(word -> word.chars().noneMatch(c -> !all.contains((char) c)))
                .filter(Solver::respectsLetterPositions)
                .toList();

        System.out.println(String.format("Number of possible words: %d", wordList.size()));

        wordList.forEach(word -> {
            char firstLetter = word.charAt(0);
            List<String> list = words.get(firstLetter);
            if (list == null) {
                list = new ArrayList<>();
                list.add(word);
                words.put(firstLetter, list);
            } else {
                list.add(word);
            }
        });

        System.out.println("Words have been mapped");

        wordList.forEach(word -> chainWords(word, new ArrayList<>()));

        System.out.println(String.format("Number of chains: %d", chains.size()));

        chains.sort((c1, c2) -> Integer.compare(c1.size(), c2.size()));

        System.out.println("Chains have been sorted");

        chains.stream().limit(10).forEach(chain -> System.out.println(String.join(" -> ", chain)));

        System.out.println(System.currentTimeMillis() - start);
    }

    private static boolean respectsLetterPositions(String word) {
        return IntStream.range(0, word.length() - 1).noneMatch(i -> inSameList(word.charAt(i), word.charAt(i + 1)));
    }

    private static boolean inSameList(char c1, char c2) {
        return left.contains(c1) && left.contains(c2)
                || top.contains(c1) && top.contains(c2)
                || right.contains(c1) && right.contains(c2)
                || bottom.contains(c1) && bottom.contains(c2);
    }

    private static void chainWords(String word, List<String> chain) {
        chain.add(word);

        if (allLettersUsed(chain)) {
            chains.add(chain);
            return;
        } else if (chain.size() == MAX_WORDS) {
            return;
        }

        words.get(word.charAt(word.length() - 1))
                .forEach(w -> chainWords(w, new ArrayList<>(chain)));
    }

    private static boolean allLettersUsed(List<String> words) {
        List<Character> characters = new ArrayList<>();
        for (String word : words) {
            word.chars().forEach(c -> characters.add((char) c));
        }

        return characters.containsAll(all);
    }
}
