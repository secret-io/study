## 아이템5 : 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

#### 안좋은 예시1)
정적 유틸리티를 잘못 사용한 예 - 유연하지 않고 테스트 하기 어렵다.
```java
public class SpellChecker {

    private static final Lexicon dictionary = new KoreanDicationry();

    private SpellChecker() {
        // Noninstantiable
    }

    public static boolean isValid(String word) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        SpellChecker.isValid("hello");
    }
}


interface Lexicon {}

class KoreanDicationry implements Lexicon {}
```

#### 안좋은 예시2)
싱글턴을 잘못 사용한 예 - 유연하지 않고 테스트 하기 어렵다.
```java
public class SpellChecker {

    private final Lexicon dictionary = new KoreanDicationry();

    private SpellChecker() {
    }

    public static final SpellChecker INSTANCE = new SpellChecker() {
    };

    public boolean isValid(String word) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        SpellChecker.INSTANCE.isValid("hello");
    }

}


interface Lexicon {}

class KoreanDicationry implements Lexicon {}
```
##
**두 방식 모두 사전을 하나만 사용한다.**
1. 실전에서는 사전이 언어별로 따로 있고
2. 테스트용 사전도 필요할 수 있다.

> 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.
---

#### 올바른 예)
의존 객체 주입은 유연성과 테스트 용이성을 높여준다.
```java
public class SpellChecker {

    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary.get());
    }

    public boolean isValid(String word) {
        throw new UnsupportedOperationException();
    }
 
    public static void main(String[] args) {
        Lexicon lexicon = new TestDictionary();
        SpellChecker spellChecker = new SpellChecker(lexicon);
        spellChecker.isValid("hello");
    }

}

interface Lexicon {}

class KoreanDictionary implements Lexicon {}

class TestDictionary implements Lexicon {}
```

* 의존 객체 주입은 생성자, 정적 팩터리(아이템 1), 빌더(아이템 2) 모두에 똑같이 응용할 수 있다.
> 의존 객체 주입 기법은 클래스의 유연성, 재사용성, 테스트 용이성을 매우 향상 시켜준다.
