# ComposeCalculator
基于 Compose With  Material Design 3 的计算器
<br>
<br>

![介绍图](https://github.com/kineks0-0/ComposeCalculator/blob/master/dosc/2022-08-17%20(11).webp?raw=true)

![介绍图](https://github.com/kineks0-0/ComposeCalculator/blob/master/dosc/2022-08-17%20(7).webp?raw=true)

## 演示

https://user-images.githubusercontent.com/51161092/185769482-542255c5-8702-472a-97e8-cf25fc886418.mp4



<br>

## 简介

### UI设计
直接抄谷歌自家的计算器，    
不过为了实现 “ 一套布局 ” 自适应横竖屏，横屏布局就没一样了。

### 功能
- 基础四则运算 *(%作为百分比处理)*
- 算式优先度运算
- 函数 sin, abs 和 常量 pi, e
- 布局自适配不同屏幕大小 *(益得Compose的优势)* 和 添加异形屏沉浸
- 对于输入体验做了优化(例如自动处理左括号和右括号，以及运算符号）

## 实现
个人觉得最有意思的是通过自定义布局实现一套布局自适应横竖屏。

> *注意：官方推荐使用 窗口大小类（WindowSizeClasses）实现自适应布局*

> 多说无益，上代码。

### TheLayout 示例调用
```kotlin
@Preview(showBackground = true)
@Composable
fun TheLayoutPreview() {

    ComposeCalculatorTheme {
        //  自定义布局，自适应横竖屏
        TheLayout(modifier = Modifier.fillMaxSize()) {
            
            //  在横竖屏下不变的组件
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Text(text = "Column1")
            }

            //  根据屏幕方向切换不同内容的组件
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                when (isHorizontal()) {
                    true -> {
                        Text(text = "Row with Horizontal")
                    }
                    false -> {
                        Text(text = "Row with Vertical")
                    }
                }
            }

            //  仅根据屏幕方向对布局配置做差异化的组件
            Column(
                modifier = Modifier
                    .isHorizontal {
                        weight(1f)
                    }
                    .isNotHorizontal {
                        weight(2f)
                    }
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                //  不在横屏透明度就会变 0.1 的组件
                Text(
                    text = "Only Horizontal",
                    modifier = Modifier.isNotHorizontal { alpha(0.1f) })
            }
            
        }
    }

}
```

![示例图](https://github.com/kineks0-0/ComposeCalculator/blob/master/dosc/TheLayoutCodeExample.webp?raw=true)

<br>

### 实现思路.1
> 首先需要获取屏幕方向
```kotlin
@Composable
@Stable
fun isHorizontal() = when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> true
    // Other wise
    else -> false
}
```
虽然 Compose 没直接提供屏幕数据的Api,但能通过 Activity 的 Configuration 判断方向

<br>

### 实现思路.2
> 然后就是根据屏幕方向切换相应布局


```kotlin
@Composable
fun TheLayout(
    content: @Composable TheLayoutScopeInstance.() -> Unit
) {

    val scope by remember {
        mutableStateOf(TheLayoutScopeInstance(null, null))
    }

    when (isHorizontal()) {

         true -> {
             Row(···) {
                 scope.apply {
                     rowScope = this@Row
                     content()
                 }
             }
         }

         // Other wise
         false -> {
             Column(···) {
                 scope.apply {
                     columnScope = this@Column
                     content()
                 }
             }
         }

     }
 }


}
```
<br>
<br>
但会发现 Column 和 Row 混用导致像是 Modifier.weight() 之类由 布局作用域 内提供的 Modifier 扩展函数没法用。
<br>
不过翻源码实现可以发现扩展函数是由 ColumnScope 和 RowScope 提供的,只要给自定义布局也实现作用域相应的 Modifier 扩展函数就行。
<br>
<br>

```kotlin
@LayoutScopeMarker
@Immutable
class TheLayoutScopeInstance(
    var columnScope: ColumnScope?, var rowScope: RowScope?
) : TheLayoutScope {

    ···
    
    @Stable
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier {
        val modifier = this
        columnScope?.apply {
            return modifier.weight(weight, fill)
        }
        rowScope?.apply {
            return modifier.weight(weight, fill)
        }
        return modifier
    }
    
    ···
    
}
```

然后可以考虑再加上下面这两个自定义 Modifier 的扩展函数

> 非必要实现，但对于实际使用来说很方便

```kotlin
@Stable
fun Modifier.isHorizontal(doWork: Modifier.() -> Modifier) = composed {
    if (isHorizontal()) doWork(this)
    else this
}

@Stable
fun Modifier.isNotHorizontal(doWork: Modifier.() -> Modifier) = composed {
    if (!isHorizontal()) doWork(this)
    else this
}
```

## 虽然关系不大但还是想加上的几张图

![介绍图](https://github.com/kineks0-0/ComposeCalculator/blob/master/dosc/2022-08-17.webp?raw=true)
![介绍图](https://github.com/kineks0-0/ComposeCalculator/blob/master/dosc/2022-08-17%20(8).webp?raw=true)
![介绍图](https://github.com/kineks0-0/ComposeCalculator/blob/master/dosc/2022-08-17%20(10).webp?raw=true)
