package example.transactions.examples.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Animal implements Eating {

    public static String CATEGORY = "domestic";
    private String name;

    protected abstract String getSound();
}
