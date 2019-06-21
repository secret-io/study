# 아이템13 : clone() 재정의는 주의해서 진행하라

`Cloneable`은 복제해도 되는 클래스임을 명시하는 용도의 인터페이스이다. `clone()` 메소드는 `Object`에 정의되어있다. `Cloneable` 인터페이스에는 아무것도 정의되어 있지 않지만, `clone()`의 동작 방식을 결정한다.
`Cloneable`을 구현한 클래스의 인스턴스에서 `clone()`을 호출하면 객체의 필드들을 하나하나 복사한 객체를 반환하며, 그렇지 않은 클래스의 인스턴스에서 호출하면 `CloneNotSupportedException`을 던진다.

그러므로 `clone()`을 사용하기 위해선, `Cloneable`을 구현해야하며, `Object`의 `clone()` 메소드를 `@Override` 해야한다.(특이하게도 접근제어자가 `protected`다)

## 1. `clone()`의 명세는 허술하다.


아래는 `Object`에 정의된 `clone()` 의 내용이다.


```java
public class Object {
    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * <p>
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *
     * @return     a clone of this instance.
     * @throws  CloneNotSupportedException  if the object's class does not
     *               support the {@code Cloneable} interface. Subclasses
     *               that override the {@code clone} method can also
     *               throw this exception to indicate that an instance cannot
     *               be cloned.
     * @see java.lang.Cloneable
     */
    protected native Object clone() throws CloneNotSupportedException;
}
```

`clone()`의 명세는 굉장히 허술하여 제대로 만드냐 안만드냐의 책임이 온전히 개발자에게 있다. 즉 시스템적으로 구조화되어있지 않아 문제 발생의 소지가 굉장히 크다는 뜻이다. 앞으로 그 이유를 살펴보겠다.

정의에 의하면, `clone()` 를 `@Override`하여 재정의할때 지켜야할 부분은 다음과 같다. (강제되는 사항은 아님)

* `x.clone != x`
* `x.clone().getClass() == x.getClass()`
* `x.clone().equals(x)` 
* 관습적으로, 반환된 객체는 `super.clone()`을 호출해서 얻어야 한다.
* 관습적으로, 반환된 객체는 원본과 독립적이어야 한다.

얼핏 보면 괜찮아 보이지만 실제로는 허술한 부분이 많다. 전부 지켜도 문제가 발생한다. 예를 들어 상위클래스에서 `super.clone()`이 아니라 생성자를 호출해 반환해도 컴파일러는 정상으로 판단한다. 
그런데 하위클래스에서 clone()을 사용하면 하위클래스의 복제된 객체가 반환되는 것이 아니라 상위클래스의 객체가 반환되는 문제점이 발생한다. 

아래는 바로 해당 문제점에 대한 코드다.


```java
public class Father implements Cloneable {
	@Override
	protected Father clone() {
		return new Father();
	}
}

public class Son extends Father {
	@Override
	protected Father clone() {
		return super.clone();
	}
}

Son son = new Son();
System.out.println(son.clone() instanceof Father); // true
System.out.println(son.clone() instanceof Son); // false
```
이를 올바르게 바꾸기 위해서는 다음과 같이 하위클래스를 변경해볼 수 있다.

```java
public class Son extends Father {
	@Override
	protected Father clone() {
	    try {
	        return (Son) super.clone();
	    } catch(CloneNotSupportedException e) {
	        throw new AssertionError();
	    }
	}
}
```

Java 1.5 이후 제네릭의 공변 반환형 덕분에 `@Override` 한 메소드의 반환되는 자료형은 하위클래스가 될 수 있다. 그렇기 때문에 100% 동작하는 코드가 된다. 실패할 가능성이 없기 때문에 `try~catch`문은 불필요한 것이다. 즉, `CloneNotSupportedException`은 `checked`가 아닌 `unchecked exception`이 되어야 한다. 
즉, 잘못 설계되었다는 의미이다. 

어쨌든 문제는 해결되었는가? 클래스의 필드가 기본 자료형이거나 변경 불가능한 객체 참조라면 그렇다. 하지만 필드에 가변객체가 존재하는 순간 재앙이 발생한다.
아무리 형변환을 시도해도 하위클래스의 가변객체 필드는 여전히 상위클래스를 가변객체 필드와 동일한 녀석을 참조하고 있기 때문에 문제가 발생한다. 이를 해결해야한다.


## 2. 올바르게 `clone()` 재정의하는 방법

`clone()`은 사실상 생성자와 같은 효과를 낸다. 새로운 객체를 반환하기 때문이다. 이 때, 만약 클래스가 완전히 독립적인 객체를 반환하도록 한다면 위와 같은 문제는 발생하지 않을 것이다. 즉 **올바른 `clone()`은 원본 객체에 아무런 해를 끼치지 않는 동시에 복제된 객체의 불변식을 보장해야 한다.** 

이를 위해 실제 값의 복사인 `deep copy`를 제안한다. 다만 제법 우아한 코드를 얻게 되지만, 아무래도 처리 속도에서 손해를 보게 된다. 또한 `Cloneable` 아키텍쳐의 기초인 필드단위 객체 복사를 회피하는 방법이기 때문에 그다지 좋지 않은 방식이다.

요약하자면, `Cloneable`을 구현하는 모든 클래스는 `clone()`를 재정의해야한다. 이 때, 접근 제한자는 `public`으로, 반환 타입은 클래스 자신으로 변경한다. 이 메서드는 `super.clone()`을 가장 먼저 호출 한 후, 필요한 필드들을 적절히 수정한다.(`deep copy`) 단, 기본 타입 필드와 불변 객체 참조만 갖는 클래스라면 수정할 필요는 없다.


## 3. 정말 `clone()`이 필요한가?

이미 `Cloneable`을 구현한 클래스라면 어쩔 수 없이 `clone()`이 잘 작동하도록 해야한다. 하지만 그렇지 않다면 굳이 `clone()`을 재정의하지는 말자. 사실 최고의 시나리오는 복제 기능을 제공하지 않는 것이다. 만약 제공해야만 하는 상황이 온다면 `clone()`을 재정의하는 것보다 훨씬 나은 대안이 있다.
바로 복사 생성자와 복사 팩터리다. 복사 생성자란, 자신과 같은 클래스의 인스턴스를 인수로 받는 생성자이며, 복사 팩터리는 복사 생성자의 정적 팩터리 메소드다.

```java
public Son(Son son) { ... }
public static Son newInstance(Son son) { ... }
```
이 방법은 생성자를 쓰지 않고 객체를 생성했던 위험천만하고 모순적인 매커니즘에서 벗어나며, 엉성한 문서 규약에 기대지도 않고, 정상적인 `final` 용법과 충돌하지도 않으며, 불필요한 예외 처리와 형변환 또한 없다.


## 정리

> 기본적으로 복제기능은 제공하지 않는 것이 최선이며, 만약 제공해야 한다면 생성자와 팩터리를 이용하자. 무조건 `Cloneable`을 이용한 복제는 위험하니 피해야한다. `final` 클래스라면 위험이 크지는 않지만 성능을 고려하면 피하는게 낫다. 다만 배열만은 유일한 예외가 될 수도 있다.


---