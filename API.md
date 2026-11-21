# Snack4 接口字典

```swift
//初始化操作
//
-asObject() -> self:ONode  //将当前节点切换为对象
-asArray()  -> self:ONode  //将当前节点切换为数组

//检测操作
//
-isUndefined()   -> bool  //检查当前节点是否未定义
-isNull()        -> bool  //检查当前节点是否为null
-isNullOrEmpty() ->bool

-isObject()    -> bool  //检查当前节点是否为对象
-isArray()     -> bool  //检查当前节点是否为数组

-isValue()     -> bool  //检查当前节点是否为值
-isBoolean()   -> bool
-isNumber()    -> bool
-isString()    -> bool
-isDate()      -> bool

//公共
//
-options(opts:Options) -> self:ONode    //切换选项
-options() -> Options 				    //获取选项

-then(n->..) -> self:ONode     	//节点构建表达式

-select(jsonpath:String) -> new:ONode 	 //使用JsonPath表达式选择节点（默认缓存路径编译）
-exists(jsonpath:String) -> bool //使用JsonPath表达式查检节点是否存在（默认缓存路径编译）
-create(jsonpath:String) -> new:ONode
-delete(jsonpath:String) -> void

-usePaths()     -> self:ONode   //使用路径（把当前作为根级，深度生成每个子节点的路径）。一般只在根级生成一次 
-path()         -> String           //获取路径属性（可能为 null；比如临时集合，或者未生成）
-pathList()     -> List<String> //获取节点路径列表（如果是临时集合，会提取多个路径）
-parent()       -> ONode
-parents(depth) -> ONode

-clear()         -> void    //清除子节点，对象或数组有效
-size()          -> int     //子节点数量，对象或数组有效

//值操作
//
-isValue()     -> bool  //检查当前节点是否为值
-setValoue(val:Object) -> self:ONode  //设置节点值 //val:为常规类型或ONode
-getValue()            -> Object                //获取节点值数据结构体（如果不是值类型，会自动转换）
-getValueAs()          -> T

-getString()    //获取值并以string输出 //如果节点为对象或数组，则输出json
-getBoolean()
-getDate()
-getShort()     //获取值并以short输出...(以下同...)
-getInt()
-getLong()
-getFloat()
-getDouble()

//对象操作
//
-isObject()    -> bool  //检查当前节点是否为对象
-getObject() -> Map<String,ONode>                     //获取节点对象数据结构体（如果不是对象类型，会自动转换）
-hasKey(key:String) -> bool                   //是否存在对象子节点?
-rename(key:String,newKey:String) -> self:ONode //重命名子节点并返回自己

-get(key:String) -> child:ONode                 //获取对象子节点（不存在，返回空节点）***
-getOrNew(key:String) -> child:ONode                        //获取对象子节点（不存在，生成新的子节点并返回）
-getOrNull(key:String) -> child:ONode           //获取对象子节点（不存在，返回null）

-set(key:String,val:Object) -> self:ONode           //设置对象的子节点（会自动处理类型）
-setAll(map:Map<String,T>) ->self:ONode             //设置对象的子节点，将map的成员搬过来
-remove(key:String)                   //移除对象的子节点

//数组操作
//
-isArray()     -> bool  //检查当前节点是否为数组
-getArray() -> List<ONode>                   //获取节点数组数据结构体（如果不是数组，会自动转换）
-get(index:int)  -> child:ONode                 //获取数组子节点（不存在，返回空节点）
-getOrNew(index:int)  -> child:ONode                       //获取数组子节点（不存在，生成新的子节点并返回）
-getOrNull(index:int)  -> child:ONode           //获取数组子节点（不存在，返回null）

-addNew() -> child:ONode                        //生成新的数组子节点
-add(val) -> self:ONode                         //添加数组子节点 //val:为常规类型或ONode
-addAll(ary:Collection<T>) -> self:ONode                //添加数组子节点，将ary的成员点搬过来
-remove(index:int)                 //移除数组的子节点

//转换操作
//
-toString()   -> String               //转为string （由字符串转换器决定，默认为json）
-toJson()     -> String                 //转为json string
-toBean()     -> Object 			    //转为数据结构体（Map,List,Value）
-toBean(type) -> T        //转为java object（clz=Object.class：自动输出类型）
-toData()     -> Object   //兼容 snack3

//填充操作
-fill(source:Object)       -> self:ONode  //填充 bean 到当前节点
-fillJson(source:String)   -> self:ONode //填充 json 到当前节点

/**
 * 以下为静态操作
**/

//加载 bean
//
+ofBean(source:Object, Feature... features) -> new:ONode
+ofBean(source:Object, opts:Options) -> new:ONode

//加载 json
//
+ofJson(source:String, Feature... features) -> new:ONode	
+ofJson(source:String, opts:Options) -> new:ONode

//序列化操作
//
+serialize(source:Object, Feature... features) -> String                   //序列化
+serialize(source:Object, opts:Options) -> String                          //序列化

+deserialize(source:String, Feature... features) -> Object                 //反序列化
+deserialize(source:String, opts:Options) -> Object                        //反序列化

+deserialize(source:String, type:Type, Feature... features) -> T  //反序列化
+deserialize(source:String, type:Type, opts:Options) -> T         //反序列化

+deserialize(source:String, type:TypeRef, Feature... features) -> T  //反序列化
+deserialize(source:String, type:TypeRef, opts:Options) -> T         //反序列化
```

## 属性级特性（@ONodeAttr）

```java
public class User {
    public long id;          //跟随全局：开启 Write_LongAsString 时输出 "123"

    @ONodeAttr(features = Feature.Write_LongAsString)
    public long code;        //属性开启：无论全局如何，都输出 "123"

    @ONodeAttr(excludeFeatures = Feature.Write_LongAsString)
    public long version;     //属性排除（4.0.60+）：即使全局开启，也输出 123
}

ONode.serialize(user, Feature.Write_LongAsString); //{"id":"1","code":"2","version":3}
```

`excludeFeatures` 说明：

* 作用范围：属性值及其整棵子树（如 `List<Long>`、嵌套对象、Map 值）
* 优先级：属性 `features`（开启） > 属性 `excludeFeatures`（排除） > 全局 Options
* 可排除的特性（仅值格式类）：`Write_NumbersAsString`、`Write_LongAsString`、`Write_DoubleAsString`、`Write_BigDecimalAsPlain`、`Write_NumberTypeSuffix`、`Write_BooleanAsNumber`、`Write_UseDateFormat`。填了其它特性，或同一特性同时出现在 `features` 与 `excludeFeatures` 中时，解析类信息时会输出 warn 日志并忽略该项（冲突时以 `features` 开启为准），不会抛异常
* 按字面排除：全局同时开启 `Write_NumbersAsString` 与 `Write_LongAsString` 时，需两个都排除
* 限制：只对框架编码产生的节点生效。属性值（或 Map/集合里的元素）本身是 ONode 实例、或自定义编码器返回自己持有的 ONode 时，这些节点不享受排除（排除不会向下渗透到它们），也不会被修改；从中间节点调用 `toJson()` 时，仍按原有规则以起点节点的 Options 作为全局选项；只对属性（字段/getter）生效，不对类注解生效
* 实现上不增加节点内存：属性值的子树在编码时直接使用一个共享编解码仓库的只读派生 Options（内部按掩码缓存）。因此这些节点的 `options()` 为只读，不能再修改

