package utility;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * For doing weighted and unweighted sums
 * This primarily handles edge cases
 */
public class Average {

    public static <T> double ofAll(List<T> list, Function<T, Double> addend){
        return ofAll(list, addend, (nothing) -> 1.0);
    }

    public static <T> double ofAll(List<T> list, Function<T, Double> addend, Function<T, Double> weighter){
        double sum = 0;
        double weightSum = 0;

        for(T element : list){
            double value = addend.apply(element);
            double weight = weighter.apply(element);

            if (Double.isFinite(value)) {
                sum += value * weight;
                weightSum += weight;
            }
        }

        if (weightSum == 0) return 0;
        return sum / weightSum;
    }
}
