package petrolpark.mc.library.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionHelper {

    public static final <K, V> Map<K, V> map(K[] keys, Function<K, V> valueGenerator) {
        return Stream.of(keys).collect(Collectors.toMap(Function.identity(), valueGenerator));
    };
    
    public static final <T> List<List<T>> transpose(List<List<T>> matrix) {
        if (matrix.isEmpty()) return Collections.emptyList();

        int rows = matrix.size();
        int cols = matrix.get(0).size();

        List<List<T>> result = new ArrayList<>();

        for (int c = 0; c < cols; c++) {
            List<T> newRow = new ArrayList<>();
            for (int r = 0; r < rows; r++) {
                newRow.add(matrix.get(r).get(c));
            };
            result.add(newRow);
        };

        return result;
    };
};
