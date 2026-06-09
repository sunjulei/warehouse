---
paths: **/*.uts, **/*.ts
---

## uts 和 ts 的差异

uts 是跨平台强类型语言，类似TS但类型要求更严格。所有变量必须先声明后使用，不支持undefined，用null替代。条件语句必须用布尔类型。

参考文档：[uts约束说明](https://doc.dcloud.net.cn/uni-app-x/uts/uts-harmony-constraints.html)

### 核心约束速查表

| 约束 | 说明 | 替代方案 |
|------|------|----------|
| 不支持 undefined | 用 null 替代 | `let x: string \| null = null` |
| 条件必须布尔 | 不支持 truthy/falsy | `if (x > 0)` 而非 `if (x)` |
| 对象字面量用 type | 不支持 interface 赋值 | 用 `type` 定义对象类型 |
| 不支持声明提升 | 必须先声明后使用 | 函数用表达式 `const fn = () => {}` |
| 不支持 namespace | 用模块替代 | `export`/`import` |
| 不支持 index signature | 用数组或类 | `class X { f: string[] = [] }` |
| 不支持 Symbol | 用字符串字面量 | `type Obj = { key1: string }` |
| 不支持 Utility Types | 手动定义等效类型 | `type Partial<T> = { ... }` |
| 不支持 as const | 用具体类型标注 | `let x: string = "hello"` |
| 不支持 #私有字段 | 用 private 关键字 | `private foo: number = 42` |
| 类不能作值使用 | 用实例或工厂函数 | `new MyClass()` 而非 `MyClass` |
| 不支持 Function.call/apply/bind | 用类方法 | 直接调用方法 |
| 不支持 generator | 用 async/await | `async function` |
| 不支持 JSX | 用 template | `<text>{{ name }}</text>` |
| 不支持 delete | 用 null 替代 | `obj.prop = null` |
| 不支持 globalThis | 用模块导出 | `export let x = 100` |

### 类型系统

**对象字面量**：没有类型标注时推导为 `UTSJSONObject`，推荐用 `type` 显式标注。

**type vs interface**：
- 对象字面量赋值只能给 `type` 定义的类型
- `type` 支持对象字面量，`interface` 不支持
- `type` 不支持嵌套对象字面量，需提取为独立 type

**不支持的类型特性**：
- 条件类型 `T extends number ? T : never`
- 映射类型 `{ [K in keyof T]: boolean }`
- Utility Types (Partial, Readonly, Pick, Omit 等)
- `as const` 断言
- `unknown` 声明（仅泛型中可用）
- 确定赋值断言 `let v!: T`

### 类和对象

**类约束**：
- 不支持 `#` 私有字段，用 `private`
- 不支持索引访问 `obj[field]`，用点运算符 `obj.field`
- 不支持静态块，用静态方法
- 类不能作为值使用，不能赋值给变量
- 继承必须显式声明构造器
- 不支持修改对象的方法，用继承 override

**接口约束**：
- 只能被 `implements`，不能被类 `implements`
- 不能继承类，只能继承接口
- 不能出现在局部作用域

### 函数

- 函数声明不能作为值传递，用函数表达式
- 不支持对函数声明属性
- 不支持 `Function.call`/`apply`/`bind`
- 函数表达式中不能访问未声明的变量（包括自身）
- 不支持将函数分配给接口

### 模块

- 不支持 `namespace`，用模块 `export`/`import`
- 不支持 `require`，用 `import`
- 不支持 `export =` 语法

### 类型检查和转换

- 不支持 `is` 运算符，用 `instanceof` + `as`
- 类型转换只支持 `as T` 语法

### 运算符和表达式

- 一元运算符 `+`/`-`/`~` 仅适用于数值类型
- 赋值语句不返回值
- 逗号运算符仅用在 for 循环
- 只能抛出 Error 类或其派生类
- 数组越界访问会抛异常（Kotlin/Swift）

### 其他约束

- 不支持 `with` 语句
- 不支持 `globalThis`
- 不支持声明合并
- 不支持生成器函数
- 不支持 JSX
- Enum 成员初始化器仅支持数字或字符串
- Enum 声明必须是顶级声明
- 类型别名不能在局部作用域
