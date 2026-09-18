# 가변·불변 객체와 판다스 view·copy

> 2026-09-19 · 자료구조, 라이브러리

## 1. 가변(Mutable) 객체와 불변(Immutable) 객체

### 한 줄 정의

객체를 만든 뒤 **그 객체 자체의 내용을 그 자리에서 바꿀 수 있느냐**의 차이.

| 구분 | 해당 타입 | 내용 변경 |
| --- | --- | --- |
| 불변 (Immutable) | `int`, `float`, `bool`, `str`, `tuple`, `frozenset`, `None` | 불가능 — 바꾸면 새 객체 생성 |
| 가변 (Mutable) | `list`, `dict`, `set`, 사용자 정의 클래스 | 가능 — 같은 객체가 그대로 바뀜 |

---

### 1-1. 변수는 "값"이 아니라 "객체를 가리키는 이름표"

파이썬에서 `a = [1, 2, 3]` 은 상자에 값을 넣는 게 아니라,
메모리 어딘가에 만들어진 리스트 객체에 `a` 라는 이름표를 붙이는 것.

이걸 확인하는 도구가 `id()` — 객체의 고유 번호를 돌려준다.
(우리가 쓰는 CPython에서는 이 값이 실제 메모리 주소지만, 다른 파이썬 구현에서는
"객체를 구분하는 고유 번호"일 뿐 주소가 아닐 수도 있다.)

```python
a = [1, 2, 3]
print(id(a))   # 예: 140234567890
```

---

### 1-2. 불변 객체의 동작

```python
s = "hello"
print(id(s))        # 예: 140111111

s = s + " world"
print(s)            # hello world
print(id(s))        # 다른 주소! ← 새 문자열 객체가 만들어짐
```

`s` 의 내용을 고친 게 아니라, **새 문자열을 만들어서 `s` 이름표를 그쪽으로 옮긴 것.**

직접 고치려고 하면 에러가 난다.

```python
s = "hello"
s[0] = "H"
# TypeError: 'str' object does not support item assignment
```

튜플도 마찬가지.

```python
t = (1, 2, 3)
t[0] = 99
# TypeError: 'tuple' object does not support item assignment
```

---

### 1-3. 가변 객체의 동작

```python
lst = [1, 2, 3]
print(id(lst))      # 예: 140222222

lst.append(4)
print(lst)          # [1, 2, 3, 4]
print(id(lst))      # 같은 주소! ← 객체 자체가 바뀜
```

`append`, `sort`, `remove`, `dict[key] = value` 등은 모두 **원본을 그 자리에서 고치는 연산**.

---

### 1-4. 이게 왜 중요한가 ① — 변수 두 개가 같은 객체를 가리킬 때

```python
# 가변 객체
a = [1, 2, 3]
b = a               # 복사가 아니라, 같은 리스트에 이름표 하나 더 붙임
b.append(4)

print(a)            # [1, 2, 3, 4]  ← a도 같이 바뀜
print(b)            # [1, 2, 3, 4]
print(a is b)       # True (같은 객체)
```

```python
# 불변 객체
x = 10
y = x
y = y + 1           # 새 정수 11을 만들어 y가 가리킴

print(x)            # 10   ← x는 그대로
print(y)            # 11
```

> 불변 객체는 "같이 가리켜도" 문제가 없다. 어차피 바꿀 수 없으니까.
> 사고는 **가변 객체를 공유할 때**만 생긴다.

---

### 1-5. 이게 왜 중요한가 ② — 함수 인자로 넘길 때

```python
def add_item(items):
    items.append(99)        # 전달받은 원본 리스트를 직접 수정

nums = [1, 2]
add_item(nums)
print(nums)                 # [1, 2, 99]  ← 원본이 바뀜
```

```python
def add_number(n):
    n = n + 99              # 새 정수를 만들어 지역 변수 n에 붙임

num = 1
add_number(num)
print(num)                  # 1  ← 원본 그대로
```

**원본을 지키고 싶으면 복사해서 넘긴다.**

```python
add_item(nums.copy())       # 또는 add_item(nums[:])
```

---

### 1-6. 자주 걸리는 함정 — 함수 기본값에 가변 객체 쓰기

```python
def add(item, box=[]):      # ❌ 위험
    box.append(item)
    return box

print(add(1))               # [1]
print(add(2))               # [1, 2]  ← 초기화 안 됨!
print(add(3))               # [1, 2, 3]
```

기본값 `[]` 는 **함수가 정의될 때 딱 한 번만 만들어지고**, 호출할 때마다 같은 리스트가 재사용된다.

```python
def add(item, box=None):    # ✅ 올바른 방법
    if box is None:
        box = []
    box.append(item)
    return box

print(add(1))               # [1]
print(add(2))               # [2]
```

---

### 1-7. 얕은 복사 vs 깊은 복사

```python
import copy

original = [[1, 2], [3, 4]]

shallow = original.copy()          # 얕은 복사 (= list(original), original[:])
deep = copy.deepcopy(original)     # 깊은 복사

shallow[0].append(99)              # 안쪽 리스트는 여전히 공유 중

print(original)   # [[1, 2, 99], [3, 4]]  ← 원본이 바뀜!
print(deep)       # [[1, 2], [3, 4]]      ← 안전
```

- **얕은 복사**: 바깥 리스트만 새로 만들고, 안쪽 객체들은 그대로 공유
- **깊은 복사**: 중첩된 객체까지 전부 새로 만듦

---

### 1-8. 자바와 비교

| 개념 | 자바 | 파이썬 |
| --- | --- | --- |
| 문자열 | `String` 불변, `StringBuilder` 가변 | `str` 불변 (가변 대안 없음, `join` 사용) |
| 리스트 | `ArrayList` 가변 | `list` 가변 |
| 정수 | `int` 원시형 / `Integer` 불변 | `int` 불변 객체 |
| 참조 전달 | 참조값 복사 전달 | 객체 참조 전달 (동일한 개념) |

자바에서 `List` 를 메소드에 넘기면 원본이 바뀌던 것과 똑같은 상황이다.

---

## 2. 판다스의 view와 copy

### 한 줄 정의

원본 DataFrame과 **메모리를 공유하느냐(view)**, **값을 따로 떼어냈느냐(copy)** 의 차이.

| 구분 | 메모리 | 수정하면 |
| --- | --- | --- |
| view | 원본과 공유 | 원본도 같이 바뀜 |
| copy | 독립된 새 데이터 | 원본은 그대로 |

1번에서 본 가변/불변 개념이 판다스 DataFrame에서 다시 나타나는 것.

> 📌 **먼저 버전부터 확인할 것.**
> 판다스 2.x와 3.0은 이 부분의 동작이 완전히 다르다.
> 아래 2-1 ~ 2-5는 **2.x 기준**이고, 3.0에서 무엇이 바뀌었는지는 2-6에 정리했다.
>
> ```python
> import pandas as pd
> print(pd.__version__)
> ```

---

### 2-1. 문제 상황 (판다스 2.x)

```python
import pandas as pd

df = pd.DataFrame({
    "이름": ["김", "이", "박"],
    "점수": [80, 90, 70]
})

sub = df[df["점수"] >= 80]    # view인지 copy인지 알 수 없음
sub["점수"] = 100             # ⚠️
```

실행하면 경고가 뜬다.

```text
SettingWithCopyWarning:
A value is trying to be set on a copy of a slice from a DataFrame.
Try using .loc[row_indexer, col_indexer] = value instead
```

**뜻**: "지금 네가 고치려는 `sub` 가 copy일 수도 있어서, 원본 `df` 에 반영이 안 될 수도 있다."

---

### 2-2. 왜 예측이 어려운가

`df[...]` 로 잘라낸 결과가 view가 될지 copy가 될지는 **판다스 내부의 메모리 배치에 따라 달라진다.**

- 열이 전부 같은 dtype이면 한 덩어리 메모리 → view가 되기 쉬움
- dtype이 섞여 있으면 조각난 메모리 → copy가 되기 쉬움

같은 코드가 데이터에 따라 다르게 동작할 수 있다는 뜻이라, **결과를 예측하려 하지 말고 의도를 명시하는 것**이 정답이다.

#### 연쇄 인덱싱(chained indexing)이 특히 위험

```python
df[df["점수"] >= 80]["점수"] = 100   # ❌ 절대 이렇게 쓰지 말 것
```

`df[...]` 가 먼저 임시 객체를 만들고, 그 임시 객체에 값을 넣고 버려진다. 원본은 안 바뀐다.
(판다스 3.0에서는 아예 `ChainedAssignmentError` 가 난다.)

---

### 2-3. 해결책 ① 원본을 고치고 싶다 → `.loc` 한 번에

```python
df.loc[df["점수"] >= 80, "점수"] = 100
print(df)
#   이름   점수
# 0  김   100
# 1  이   100
# 2  박    70
```

`.loc[행 조건, 열 이름]` 로 **한 번의 인덱싱**으로 접근하면 판다스가 원본을 직접 수정한다.

---

### 2-4. 해결책 ② 따로 떼어내 쓰고 싶다 → `.copy()` 명시

```python
sub = df[df["점수"] >= 80].copy()   # 확실한 독립 복사본
sub["점수"] = 100

print(df["점수"].tolist())    # [80, 90, 70]  ← 원본 안전
print(sub["점수"].tolist())   # [100, 100]
```

경고도 사라지고, 의도도 코드에 드러난다.

---

### 2-5. 무엇이 view이고 무엇이 copy인가 (2.x 기준 경향)

| 연산 | 경향 |
| --- | --- |
| `df["col"]` (단일 열 선택) | 보통 view |
| `df[0:3]` (행 슬라이싱) | 보통 view |
| `df.loc[0:2]`, `df.iloc[0:2]` (슬라이스) | 보통 view |
| `df.loc[[0, 1]]`, `df.iloc[[0, 1]]` (리스트로 뽑기) | 항상 copy |
| `df[불리언 조건]` (boolean mask) | 보통 copy |
| `df.query(...)` | copy |
| `df.head()`, `df.dropna()`, `df.sort_values()` | copy |
| `.copy()` | 항상 copy |

> ⚠️ 이건 **경향**일 뿐 보장이 아니다. 외우기보다 `.loc` / `.copy()` 습관을 들이는 게 훨씬 안전하다.

---

### 2-6. 판다스 3.0의 Copy-on-Write (CoW)

**Copy-on-Write** 는 판다스 1.5에서 처음 등장해 2.x 동안 선택 기능이었고, **판다스 3.0(2026년 초 출시)부터 기본값이자 유일한 모드**가 됐다.

바뀐 점:

- 모든 인덱싱·선택 결과가 **copy처럼** 동작한다 (원본이 조용히 바뀌는 일이 없음)
- `SettingWithCopyWarning` 이 **제거됐다** — 애매함이 사라졌으니 경고할 이유도 없어진 것
- 경고를 잠재우려고 넣던 방어적 `.copy()` 가 더 이상 필요 없다
- 연쇄 할당(`df[조건]["열"] = 값`)은 `ChainedAssignmentError` 로 막힌다
- 실제 수정 시점까지 복사를 미루기 때문에, 불필요한 복사가 줄어 오히려 빨라지는 경우가 많다

#### 버전별로 할 일

| 버전 | 할 일 |
| --- | --- |
| 2.x | `pd.options.mode.copy_on_write = True` 로 미리 켜서 3.0 동작을 연습할 수 있다 |
| 3.0 이상 | 위 옵션은 **deprecated 이고 아무 효과가 없다**. CoW가 항상 켜져 있으므로 그냥 쓰면 된다 |

```python
import pandas as pd
print(pd.__version__)

# 2.x 에서만 의미 있음 (3.0에서는 무시됨)
pd.options.mode.copy_on_write = True
```

> **`.loc` / `.copy()` 를 명시하는 습관은 어느 버전에서도 그대로 유효**하므로 계속 쓰면 된다.

---

## 3. 정리

- 파이썬 변수는 값이 아니라 **객체를 가리키는 이름표**다.
- 불변 객체(`str`, `tuple`, `int`)는 바꾸면 새 객체가 생기고, 가변 객체(`list`, `dict`, `set`)는 원본이 그 자리에서 바뀐다.
- 가변 객체를 공유하거나 함수에 넘길 때 의도치 않은 원본 변경이 일어난다 → 필요하면 `.copy()` / `copy.deepcopy()`.
- 함수 기본값에는 절대 가변 객체를 쓰지 않는다 (`box=None` 패턴).
- 판다스 2.x에서는 같은 문제가 반복된다. **원본 수정은 `.loc` 로, 분리 작업은 `.copy()` 로** — 이 두 가지만 지키면 `SettingWithCopyWarning` 을 볼 일이 없다.
- 판다스 3.0부터는 CoW가 기본이라 이 혼란 자체가 사라졌지만, 위 두 습관은 그대로 유효하다.
