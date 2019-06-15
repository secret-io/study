# 아이템9 : try-finally보다는 try-with-resources를 사용하라

전통적으로 자원 회수를 보장하는 수단으로 `try-finally`가 쓰였다.


## 1. try-fianlly의 문제점
하지만 두가지 커다란 문제점이 있다.

* 코드가 복잡해지고 가독성이 좋지 않다.
* 마지막 예외를 제외하곤 이전의 모든 예외을 무시한다.

아래의 코드는 대표적인 `try-finally`의 예시다.
```java
public class TryFinally {
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }
}
```

하지만 만약 자원이 2개가 된다면 `try-finally`가 2번 중첩되는 지저분한 코드가 되어버린다. 

```java
public class Copy {
    private static final int BUFFER_SIZE = 8 * 1024;

    static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0)
                    out.write(buf, 0, n);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static void main(String[] args) throws IOException {
        String src = args[0];
        String dst = args[1];
        copy(src, dst);
    }
}
```

**가독성이 떨어지고 복잡해져서 프로그래머가 실수할 여지가 다분하다.** 하지만 문제는 여기서 끝나지 않는다. 두 코드 모두 치명적인 문제점을 갖고 있다.

그 문제점을 좀 더 쉽게 이해하기 위해서 다음 코드를 보자

```java
public class TryFinally implements AutoCloseable {
    public void play() throws FirstException {
        System.out.println("play");
        throw new FirstException();
    }

    @Override
    public void close() throws SecondException {
        System.out.println("clean");
        throw new SecondException();
    }

    public static void main(String[] args) {
        TryFinally tryFinally = null;

        try {
            tryFinally = new TryFinally();
            tryFinally.play();
        } finally {
            if (tryFinally != null) {
                tryFinally.close();
            }
        }
    }
}
```

`play()`에서 예외가 발생하면 곧장 `finally`블록을 실행시킨다. 이때 `close()`에서도 예외를 발생시키도록 코드를 구성했다.

이 때, 예외 스택을 추적해보면 일반적으로 **가장 중요한 첫 번째 예외인 `FirstException`는 완전히 무시**하고 가장 마지막 예외인 `SecondException`만 발견할 수 있다. 이는 **디버깅에 굉장히 치명적**이다.

## 2. try-with-resources 로 해결

위 문제들은 Java7에서 `try-with-resources` 덕에 완벽하게 해결되았다.
다만, 이 구조를 사용하기 위해서는 자원을 활용하는 객체가 `AutoCloseable` 인터페이스를 구현해야 한다.

다음 코드는 `try-with-resources`를 적용한 모습이다.

```java
public class TryFinally implements AutoCloseable {
    public void play() throws FirstException {
        System.out.println("play");
        throw new FirstException();
    }

    @Override
    public void close() throws SecondException {
        System.out.println("clean");
        throw new SecondException();
    }

    public static void main(String[] args) {
        try (TryFinally tryFinally = new TryFinally(); TryFinally tryFinally1 = new TryFinally()) {
            tryFinally.play();
            tryFinally1.play();
        }
    }
}
```

코드도 깔끔해져 가독성이 올라갔다. 그리고 문제를 분석할 때도 훨씬 좋다. 왜냐하면 `try-finally`와 다르게 이전에 발생한 예외를 무시하지 않기 때문이다. 스택 추적 내역에
이전의 예외들은 `suppressed` 라는 꼬리표를 달고 출력되어 디버깅에 아주 용이하다.


## 정리

> 앞으로 자원을 회수할 때는 무조건 `try-with-resources`를 사용하자.


---