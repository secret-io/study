# 아이템7 : 다 쓴 객체 참조를 해제하라

Java에서는 Garbage Collection 덕분에 메모리 관리에 대해 신경 쓸 필요가 없다고 오해할 수 있지만, 사실은 그렇지 않다. 객체의 메모리 관리와 관련해 유의해야할 3가지 경우가 있다.
* 메모리 직접 관리
* 캐시
* 콜백


## 1. 메모리 관리

다음은 스택을 간단히 구현한 코드이다.
```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        this.ensureCapacity();
        this.elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size]; // 문제의 영역
    }

    private void ensureCapacity() {
        if (this.elements.length == size) {
            this.elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

언뜻 보기에 별 문제는 없어보인다. 하지만 이 코드에는 **메모리 누수**가 발생한다. 문제의 영역은 바로 `pop()` 메소드다. 스택에서 꺼내면서 단순히 `size`만 감소시킬 뿐, 꺼낸 객체에 대한 참조는 계속되기 때문에 (여전히 `elements`에 존재) 이 지점에서 누수가 발생한다. 흔한 경우는 아니지만 객체가 계속 쌓여 `OutOfMemoryError`가 발생할 수도 있다.

해법은 간단하다. 다음과 같이 다 쓴 참조를 `null`처리하도록 코드를 수정한다.

```java
public Object pop() {
    if (size == 0) {
        throw new EmptyStackException();
    }

    Object value = elements[--size];
    this.elements[size] = null; // 다 쓴 참조 해제
    return value;
}
```

이제 누군가 `null`처리한 참조를 사용하려 한다면 `NullPointerException`이 발생할 것이다. 올바르지 않은 객체를 가지고 엄한 일을 수행하는 예기치 않는 문제가 발생하는 것보다는 이게 훨씬 낫다.

하지만 **매번 객체 참조를 `null`처리할 필요는 없다.** 예를 들어 로컬 스코프에서의 다 쓴 참조는 로컬 밖으로 나가면 자동으로 해제되기 때문에 굳이 `null`처리할 필요가 없다. 객체 참조를 `null`처리하는 경우는 **아주 예외적인 상황**이다.

그 상황은 바로 **메모리를 직접 관리하는 클래스**인 경우다. 그러므로 이 경우에는 프로그래머가 반드시 GC에게 필요없는 객체임을 `null`처리를 통해 알려줘야만 한다.


## 2. 캐시

캐시 역시 메모리 누수를 조심해야 한다. 객체 참조를 캐시에 넣어둔 채로 캐시를 비우는 것을 잊기 쉽다. 해결책은 여러가지 있는데 3가지만 소개한다.
* **WeakHashMap** :
    * 캐시를 저장하는 `map`을 `WeakHashMap`으로 선언하면 캐시의 `key`에 대한 참조가 캐시 밖에서 필요 없어질 때, 해당 엔트리를 캐시에서 자동으로 비워준다. (Weak-Reference 개념 적용)
    * 캐시 엔트리의 유효기간 : 외부에서 `key`를 참조하는 동안

캐시 엔트리의 유효기간을 정확히 알 수 없는 경우에는, 시간이 지날수록 캐시 값이 의미 없는 것으로 판단한다.

* **백그라운드 쓰레드 활용** : `ScheduledThreadPoolExecutor`
* **새 엔트리 추가시 부수작업** : `LinkedHashMap` 클래스의 `removeEldestEntry` 메소드  


## 3. 콜백

마지막으로 리스너와 콜백이 있다.

클라이언트 코드가 콜백을 등록만 하고 뺄 수 있는 방법을 제공하지 않는다면, 계속해서 콜백이 쌓일 것이다. 이 때 콜백을 Weak-Reference로 저장하면 해결할 수 있다.
 
---


> 메모리 누수는 겉으로 잘 드러나지 않아서 발견하기가 힘들다. 심하면 수년간 시스템에 잠복해 있는 경우도 있다. 그래서 이런 문제는 미리 예방하는 것이 제일 중요하다.