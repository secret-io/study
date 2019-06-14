# 아이템6  : 불필요한 객체 생성을 피하라
**예시 1)**
* 불변 클래스에서는 정적 팩터리 메서드를 사용해 불필요한 객체 생성을 피할 수 있다.
```java
public class StringTest {

    public static void main(String[] args) {
        Boolean true1 = Boolean.valueOf("true");
        Boolean true2 = Boolean.valueOf("true");

        System.out.println(true1 == true2);
        System.out.println(true1 == Boolean.TRUE);
    }
}
```
> 생성자는 호출할 때마다 새로운 객체를 만들지만, 팩터리 메서드는 전혀 그렇지 않다.


##
**예시 2)**
* 값비싼 객체를 재사용해 성능을 개선시킨다.
```java
public class RomanNumber {

    static boolean isRomanNumeral(String s) {
        return s.matches("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    }

}
```
보단

```java
public class RomanNumber {

    private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeral(String s) {
        return ROMAN.matcher(s).matches();
    }

}
```

성능 개선을 위해서, Pattern 인스턴스를 클래스 초기화(정적 초기화) 과정에서 직접 생성해 캐싱해두고,

나중에 isRomanNumeral 메서드가 호출될 때마다 이 인스턴스를 재사용한다.
##
```
단점 )
개선된 isRomanNumeral 방식의 클래스가 초기화후 이 메서드를 한번도 호출하지 않는다면 ROMAN 필든는 쓸모 없이 초기화된 꼴이다.
isRomanNumeral 메서드가 호출 될 때 필드를 초기화하는 지연 초기화로 불필요한  초기화를 없앨 수는 있지만 권하지 않는다.
1. 지연 초기화는 코드를 복잡하게 만든다.
2. 성능이 크게 개선 되지 않는다.
```


##
**예시 3)**
* 객체가 불변이라면 재사용해도 안정함이 명백하나, 덜 명확하거나 직관에 반대되는 상황도 있다.

```java
public class UsingKeySet {

    public static void main(String[] args) {
        Map<String, Integer> menu = new HashMap<>();
        menu.put("Burger", 8);
        menu.put("Pizza", 9);

        Set<String> names1 = menu.keySet();
        Set<String> names2 = menu.keySet();

        names1.remove("Burger");
        System.out.println(names2.size()); // 1
        System.out.println(menu.size()); // 1
    }
}
```
keySet을 호출할 때마다 새로운 Set인스턴스가 만들어 질거라 생각하지만,

사실 매번 같은 Set인스턴스를 반환할지도 모른다.

반환된 Set인스턴스가 가변이더라도 반환된 인스턴스들은 기능적으로 모두 똑같다. ~~(잘 이해가 안된다.)~~

즉, 반환한 객체 중 하나를 수정하면 다른 모든 객체가 따라서 바뀐다. ~~(얕은 복사같은 개념인가?)~~


##

**예시 4)**
* 박싱된 기본 타입보다는 기본 타입을 사용한다.
```java
public class AutoBoxingExample {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        long sum = 0l;
        // Long sum = 0l;
        for (long i = 0 ; i <= Integer.MAX_VALUE ; i++) {
            sum += i;
        }
        System.out.println(sum);
        System.out.println(System.currentTimeMillis() - start);
    }
}
```
문제점 : 불필요한 Long 인스턴스가 sum에 더해질때마다 2^31개가 만들어 진다.

----

**방어적 복사에 실패하면 언제 터져 나올지 모르는 버그와 보안 구멍으로 이어 지지만,**

**불필요한 객체 생성은 그저 코드 형태와 성능에만 영향을 준다.**

> 너무 재사용을 쫓지 말라는  생각한다. 성능 보다는 버그와 보안의 위험이 더 크다고 말하는 느낌이 들기 때문에
