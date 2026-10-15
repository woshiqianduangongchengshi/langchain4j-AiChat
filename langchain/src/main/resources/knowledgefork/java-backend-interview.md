# Java 后端面试题库

## Java 基础与集合

Q1: HashMap 的底层实现原理？JDK 1.7 和 1.8 有什么区别？
考察点：数组+链表+红黑树结构、扩容机制、头插法死循环问题、treeify 阈值 8。

Q2: ConcurrentHashMap 如何保证线程安全？
考察点：JDK 1.7 分段锁 Segment、1.8 CAS + synchronized 锁头节点、size 计算的 baseCount 与 CounterCell。

Q3: ArrayList 和 LinkedList 的区别？各自适用场景？
考察点：随机访问 O(1) vs O(n)、插入删除成本、内存占用、扩容机制 1.5 倍。

Q4: == 和 equals 的区别？String 的 equals 怎么实现的？
考察点：引用比较 vs 内容比较、hashCode 与 equals 契约、字符串常量池。

## 并发编程

Q5: synchronized 和 ReentrantLock 的区别？
考察点：锁升级（偏向/轻量/重量级）、可中断锁、公平锁、Condition、AQS。

Q6: volatile 关键字的作用？能保证原子性吗？
考察点：可见性、禁止指令重排、happens-before、i++ 反例、DCL 单例。

Q7: 线程池的核心参数有哪些？任务提交后的执行流程？
考察点：corePoolSize/maximumPoolSize/keepAliveTime/阻塞队列/拒绝策略、先核心再队列后扩容最后拒绝。

Q8: ThreadLocal 的原理？为什么会有内存泄漏风险？
考察点：ThreadLocalMap 弱引用 key、value 强引用、用完要 remove、线程池场景。

Q9: CAS 是什么？ABA 问题怎么解决？
考察点：Unsafe.compareAndSwap、自旋开销、AtomicStampedReference 版本号。

## JVM

Q10: JVM 内存区域划分？哪些区域线程共享？
考察点：堆、方法区/元空间、虚拟机栈、本地方法栈、程序计数器、直接内存。

Q11: 垃圾回收算法有哪些？G1 和 CMS 的区别？
考察点：标记-清除/复制/标记-整理/分代收集、Region 分区、可预测停顿、SATB。

Q12: 什么对象会进入老年代？
考察点：年龄阈值 15、大对象直接晋升、动态年龄判断、空间分配担保。

Q13: 类加载过程？双亲委派模型有什么作用？
考察点：加载/验证/准备/解析/初始化、安全性与避免重复加载、SPI 破坏双亲委派。

Q14: 如何排查 OOM 和 CPU 飙高问题？
考察点：jmap/jstack/jstat、MAT 分析 dump、top -H 定位线程、Arthas。

## Spring 生态

Q15: Spring Bean 的生命周期？
考察点：实例化、属性注入、Aware、BeanPostProcessor、init-method、销毁。

Q16: Spring 如何解决循环依赖？
考察点：三级缓存 singletonFactories/earlySingletonObjects/singletonObjects、构造器注入无法解决。

Q17: Spring AOP 的实现原理？
考察点：JDK 动态代理 vs CGLIB、切面/切点/通知、代理失效场景（自调用）。

Q18: @Transactional 什么情况下会失效？
考察点：自调用、非 public、异常被捕获、rollbackFor 未配置、数据库引擎不支持。

Q19: SpringBoot 自动装配原理？
考察点：@EnableAutoConfiguration、spring.factories / AutoConfiguration.imports、条件注解。

Q20: SpringMVC 一次请求的处理流程？
考察点：DispatcherServlet、HandlerMapping、HandlerAdapter、Interceptor、ViewResolver。

## MySQL 与 Redis

Q21: MySQL 索引为什么用 B+ 树？
考察点：矮胖树减少 IO、叶子节点链表利于范围查询、对比 B 树/哈希/二叉树。

Q22: 什么是覆盖索引、最左前缀原则、索引下推？
考察点：回表、联合索引匹配规则、ICP 减少回表次数。

Q23: MVCC 是怎么实现的？
考察点：undo log 版本链、ReadView、RC 与 RR 的区别、当前读 vs 快照读。

Q24: Redis 持久化方式？RDB 和 AOF 怎么选？
考察点：快照 vs 追加日志、混合持久化、数据丢失容忍度。

Q25: 缓存穿透、击穿、雪崩怎么解决？
考察点：布隆过滤器/空值缓存、互斥锁/逻辑过期、随机过期时间/多级缓存。

Q26: Redis 分布式锁怎么实现？有什么问题？
考察点：SET NX EX、锁续期 Watchdog、Redisson、主从切换丢锁与 RedLock 争议。

## 分布式与微服务

Q27: CAP 和 BASE 理论？项目中如何取舍？
考察点：一致性/可用性/分区容错、最终一致性、注册中心 CP/AP 选择。

Q28: 分布式事务有哪些方案？
考察点：2PC/3PC、TCC、本地消息表、事务消息、Seata AT/Saga。

Q29: 接口幂等性如何保证？
考察点：唯一索引、token 机制、防重表、状态机、乐观锁。

Q30: 消息队列如何保证消息不丢、不重复消费？
考察点：生产者确认、Broker 持久化、消费者手动 ack、幂等消费。

## 项目与场景题

Q31: 介绍一个你最有挑战的项目，遇到过什么难题，怎么解决的？
考察点：表达结构 STAR 法则、技术深度、问题定位能力、复盘总结。

Q32: 秒杀系统怎么设计？
考察点：限流削峰、库存预热 Redis、Lua 原子扣减、异步下单、防超卖。

Q33: 如何设计一个短链接服务？
考察点：发号器/哈希、301 vs 302、缓存热点、布隆过滤器防穿透。

Q34: 系统响应变慢，你怎么排查？
考察点：监控指标、链路追踪、慢 SQL、GC 日志、线程池打满、自上而下分层定位。
