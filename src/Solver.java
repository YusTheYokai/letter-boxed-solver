import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Solver {

    private static List<Character> left = Arrays.asList('m', 'h', 'l');
    private static List<Character> top = Arrays.asList('a', 'k', 'o');
    private static List<Character> right = Arrays.asList('i', 'b', 't');
    private static List<Character> bottom = Arrays.asList('r', 'n', 'c');
    private static List<Character> all = new ArrayList<>();

    private static Predicate<String> isLongEnough = word -> word.length() > 2;

    private static List<String> words;

    private static List<List<String>> chains = new ArrayList<>();

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {
        all.addAll(left);
        all.addAll(top);
        all.addAll(right);
        all.addAll(bottom);

        words = Files.readAllLines(Paths.get("words")).stream()
                .filter(isLongEnough)
                .filter(word -> word.chars().noneMatch(c -> !all.contains((char) c)))
                .filter(Solver::respectsLetterPositions).toList();

        words.forEach(word -> chainWords(word, new ArrayList<>()));
        chains.sort((c1, c2) -> Integer.compare(c1.size(), c2.size()));
        chains.stream().limit(10).forEach(chain -> System.out.println(String.join(" -> ", chain)));
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
        } else if (chain.size() == 5) {
            return;
        }

        words.stream()
                .filter(otherWord -> !word.equals(otherWord) && otherWord.charAt(0) == word.charAt(word.length() - 1))
                .forEach(otherWord -> chainWords(otherWord, new ArrayList<>(chain)));
    }

    private static boolean allLettersUsed(List<String> words) {
        List<Character> characters = new ArrayList<>();
        for (String word : words) {
            word.chars().forEach(c -> characters.add((char) c));
        }

        return characters.containsAll(all);
    }
}
