# 아이템14 : Comparable을 구현할지 고려하라

### 핵심정리
``` text
1. 순서를 고려해야 하는 값 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하라!
    * 쉽게 정렬, 검색, 비교 기능을 제공하는 컬렉션과 어우러지도록
2. compareTo 메서드에서 필드값 비교 시 < 와 > 연산자는 쓰지 말자
    * 대신 박싱 된 기본타입에서 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하자
```

---

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```

* Comparable 인터페이스의 유일한 메서드 compareTo()
* compareTo는 Obejct의 메서드가 아니다
* 성격은 두 가지만 빼면 Object의 equals와 같다
    1. 단순 동치성 비교에 + 순서까지 비교할 수 있다
    2. 제네릭하다
* Comparable을 구현했다 -> 해당 클래스의 인스턴스들에는 자연적인 순서가 있음을 뜻한다

---

### compareTo 메서드의 일반 규약의 equals의 규약과 비슷하다
* compareTo 규약을 지키지 못하면, 비교를 활용하는 클래스와 어울리지 못한다
    * TreeSet
    * TreeMap
    * Collections
    * Arrays

#### 규약 (반사성, 대칭성, 추이성)
1. 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다
2. 첫 번째가 두번 째보다 크고 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다
3. 크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야 한다
4. **compareTo 메서드로 수행한 동치성 검사의 결과가 equals와 같아야 한다(필수는 아니지만 꼭 지키길 권장)**

