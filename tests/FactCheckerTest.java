import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

public class FactCheckerTest {
    
    @Test
    public void testSimpleInconsistent() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "a", "b"),
                new Fact(Fact.FactType.TYPE_TWO, "a", "b")
        );
        
        assertFalse(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testSimpleConsistent() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "a", "b"),
                new Fact(Fact.FactType.TYPE_ONE, "b", "c")
        );

        assertTrue(FactChecker.areFactsConsistent(facts));
    }
    
    @Test
    public void testConsistent1() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "Anna", "Kenton"),
                new Fact(Fact.FactType.TYPE_TWO, "Kenton", "Katya"),
                new Fact(Fact.FactType.TYPE_TWO, "Katya", "Sanni"),
                new Fact(Fact.FactType.TYPE_ONE, "Sanni", "Matt"),
                new Fact(Fact.FactType.TYPE_TWO, "Matt", "Max")
        );
        
        assertTrue(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testInconsistent1() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "Anna", "Kenton"),
                new Fact(Fact.FactType.TYPE_TWO, "Kenton", "Katya"),
                new Fact(Fact.FactType.TYPE_TWO, "Katya", "Sanni"),
                new Fact(Fact.FactType.TYPE_ONE, "Sanni", "Matt"),
                new Fact(Fact.FactType.TYPE_TWO, "Matt", "Max"),
                new Fact(Fact.FactType.TYPE_ONE, "Max", "Sanni")
        );

        assertFalse(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testInconsistent2() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "Anna", "Kenton"),
                new Fact(Fact.FactType.TYPE_TWO, "Kenton", "Katya"),
                new Fact(Fact.FactType.TYPE_TWO, "Katya", "Sanni"),
                new Fact(Fact.FactType.TYPE_ONE, "Sanni", "Matt"),
                new Fact(Fact.FactType.TYPE_TWO, "Matt", "Max"),
                new Fact(Fact.FactType.TYPE_ONE, "Max", "Katya"),
                new Fact(Fact.FactType.TYPE_ONE, "Max", "Katya")
        );

        assertFalse(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testConsistent2() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_TWO, "Mark", "Anna"),
                new Fact(Fact.FactType.TYPE_ONE, "Anna", "Kenton"),
                new Fact(Fact.FactType.TYPE_ONE, "Kenton", "Katya"),
                new Fact(Fact.FactType.TYPE_TWO, "Katya", "Mark"),
                new Fact(Fact.FactType.TYPE_ONE, "Anna", "Kenton")
        );

        assertTrue(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testComplexConsistent() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "a", "b"),
                new Fact(Fact.FactType.TYPE_ONE, "b", "c"),
                new Fact(Fact.FactType.TYPE_ONE, "c", "d"),
                new Fact(Fact.FactType.TYPE_TWO, "a", "e"),
                new Fact(Fact.FactType.TYPE_TWO, "d", "e"),
                new Fact(Fact.FactType.TYPE_TWO, "c", "e"),
                new Fact(Fact.FactType.TYPE_TWO, "b", "e")
        );

        assertTrue(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testComplexConsistent2() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "a", "b"),
                new Fact(Fact.FactType.TYPE_ONE, "b", "c"),
                new Fact(Fact.FactType.TYPE_TWO, "c", "d"),
                new Fact(Fact.FactType.TYPE_TWO, "d", "e"),
                new Fact(Fact.FactType.TYPE_TWO, "f", "g"),
                new Fact(Fact.FactType.TYPE_ONE, "g", "a")
        );

        assertTrue(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testComplexInConsistent2() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "a", "b"),
                new Fact(Fact.FactType.TYPE_ONE, "b", "c"),
                new Fact(Fact.FactType.TYPE_TWO, "c", "d"),
                new Fact(Fact.FactType.TYPE_TWO, "d", "e"),
                new Fact(Fact.FactType.TYPE_TWO, "f", "g"),
                new Fact(Fact.FactType.TYPE_ONE, "g", "a"),
                new Fact(Fact.FactType.TYPE_TWO, "g", "b")
        );

        assertFalse(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testComplexInConsistent1() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "a", "b"),
                new Fact(Fact.FactType.TYPE_ONE, "b", "c"),
                new Fact(Fact.FactType.TYPE_ONE, "c", "d"),
                new Fact(Fact.FactType.TYPE_TWO, "d", "e"),
                new Fact(Fact.FactType.TYPE_ONE, "e", "a"),
                new Fact(Fact.FactType.TYPE_TWO, "e", "b")
        );

        assertFalse(FactChecker.areFactsConsistent(facts));
    }

    @Test
    public void testComplexInConsistent3() {
        List<Fact> facts = List.of(
                new Fact(Fact.FactType.TYPE_ONE, "a", "b"),
                new Fact(Fact.FactType.TYPE_ONE, "c", "a"),
                new Fact(Fact.FactType.TYPE_TWO, "c", "b")
        );

        assertFalse(FactChecker.areFactsConsistent(facts));
    }
}