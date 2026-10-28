package neoemo.mods.myfirstminecraftmod.utils.Cards;

import neoemo.mods.myfirstminecraftmod.utils.Random;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class Deck {
    private Deque<Card> deck;

    public Deck() {
        this.deck = create52Deck();
    }

    private Deque<Card> create52Deck() {
        ArrayList<Card> deck = new ArrayList<>();
        Rank[] ranks = Rank.values();
        for (Rank rank : ranks) {
            for (Suit suit : Suit.values()) {
                deck.add(new Card(rank, suit));
            }
        }
        return shuffle(deck);
    }

    private Deque<Card> shuffle(ArrayList<Card> deck) {
        Deque<Card> shuffled = new ArrayDeque<>();
        while (!deck.isEmpty()) {
            int rand = Random.random(deck.size()) - 1;
            shuffled.add(deck.remove(rand));
        }
        return shuffled;
    }

    public Deque<Card> getDeck() {
        return deck;
    }

    public static String getSuitName(Suit suit) {
        return switch (suit) {
            case Heart -> "§cЧерви (§c♥§f)";
            case Diamond -> "§cБубны (§c♦§f)";
            case Club -> "§fТрефы (§f♣§f)";
            case Spade -> "§fПики (§f♠§f)";
        };
    }
}
