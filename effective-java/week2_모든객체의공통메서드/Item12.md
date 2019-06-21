# 아이템12 : toString을 항상 재정의하라

`Object` 클래스의 `toString()` 메소드는 클래스의 정보를 완전하게 나타내지 못한다. 실제로 호출해보면 `User@adbbd`처럼 단지 `클래스명@16진수로_표시한_해시코드`의 형태를 보여줄 뿐이다. 

아래에는 실제 `Object` 클래스에 정의된 `toString()` 의 내용이다.
```java
public class Object {
    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}
```

`toString()`의 규약을 정리하면 다음과 같다.

* **'간결하면서도 사람이 읽기 쉬운 형태의 유익한 정보'를 반환해야 한다.** 
* **모든 하위클래스에서 재정의하는 것을 추천한다.**

위 규약을 준수한 클래스는 그렇지 않은 클래스보다 훨씬 유익한 정보를 담고 있으다. 그렇게에 디버깅이 쉬워지기도 한다. 왜냐하면 `User@adbbd`보다 `User {name=jake, age=23}`가 유익하다는 점은 자명하기 때문이다. 만약 우리가 `toString()`을 직접 호출할 일이 없더라도 다른 어딘가에 쓰일 일(`println` 및 `printf`, 문자열 연결 연산자 `assert`구문에 넘길 때 등)이 매우 많기 때문에, 웬만하면 재정의해주도록 하자.  


## 1. 좋은 `toString()` 재정의 방법

간결하면서도 사람이 읽기 쉬운 형태의 유익한 정보를 반환하기 위한 3가지 원칙이 있다.

* **그 객체가 가진 주요 정보 모두를 반환하는 게 좋다.** (스스로를 완벽히 설명하는 문자열)
* **반환하는 포맷을 명시하든 아니든, 주석으로 작성한 의도를 명확히 밝혀야 한다.**
* **`toString()`으로 반환하는 값들을 사용자가 파싱 없이 얻을 수 있는 추가적인 API를 제공하자.**

이 때, 직접 타이핑하여 재정의할 수도 있지만 IntelliJ같은 IDE는 해당 기능을 제공한다. 그외에도 라이브러리들이 많다. `Google`의 `AutoValue`, `Apache Commons Lang`의 `ToStringBuilder`, `Lombok`의 `@ToString` 등 여러가지가 있으니 취향껏 선택하여 사용하면 된다.


## 2. `toString()`을 재정의하지 않아도 되는 경우

사실 모든 클래스가 `toString()`을 재정의할 필요가 있는 것은 아니다. 아래의 경우들이 그 예시다.

* **대부분의 유틸리티 클래스** (속성 값이 의미 없음)
* **대부분의 `enum`타입** (이미 Java에서 완벽한 `toString()`을 제공함)



## 정리

> 모든 구체 클래스에서 `Object`의 `toString()`을 재정의하자. 단, 상위 클래스에서 알맞게 정의한 경우는 예외다. 이 때, 명확하고 유용하면서도 읽기 좋은 형태로 반환해야 한다. 


---