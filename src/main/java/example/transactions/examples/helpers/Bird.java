package example.transactions.examples.helpers;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Bird extends Animal {
    private boolean walks;

    public Bird() {
        super("bird");
    }

    public Bird(String name, boolean walks) {
        super(name);
        setWalks(walks);
    }

    public Bird(String name) {
        super(name);
    }

    public boolean walks() {
        return walks;
    }

    public String getSound() {
        return "sound";
    }

    public String eats() {
        return "eats";
    }
}
