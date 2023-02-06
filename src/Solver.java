import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private static List<String[]> chains = new ArrayList<>();

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        all.addAll(left);
        all.addAll(top);
        all.addAll(right);
        all.addAll(bottom);

        String[] wordArray = Files.readAllLines(Paths.get("words")).stream()
                .filter(isLongEnough)
                .filter(word -> word.chars().noneMatch(c -> !all.contains((char) c)))
                .filter(Solver::respectsLetterPositions)
                .toArray(String[]::new);

        System.out.println(String.format("Number of possible words: %d", wordArray.length));

        Arrays.stream(wordArray).forEach(word -> {
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

        Arrays.stream(wordArray).forEach(word -> chainWords(word, new String[MAX_WORDS]));

        System.out.println(String.format("Number of chains: %d", chains.size()));

        chains.sort((c1, c2) -> Integer.compare(Collections.frequency(Arrays.asList(c2), null), Collections.frequency(Arrays.asList(c1), null)));

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

    private static void chainWords(String word, String[] chain) {
        for (int i = 0; i < chain.length; i++) {
            if (chain[i] == null) {
                chain[i] = word;
                break;
            }
        }

        if (allLettersUsed(chain)) {
            chains.add(chain);
            return;
        } else if (chain[MAX_WORDS - 1] != null) {
            return;
        }

        words.get(word.charAt(word.length() - 1))
                .forEach(w -> chainWords(w, Arrays.copyOf(chain, MAX_WORDS)));
    }

    private static boolean allLettersUsed(String[] chain) {
        List<Character> characters = new ArrayList<>();
        for (String word : chain) {
            if (word == null) {
                break;
            }

            word.chars().forEach(c -> characters.add((char) c));
        }

        return characters.containsAll(all);
    }
}
