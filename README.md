# commons-pool2-Demo
创建新的对象并初始化的操作，可能会消耗很多的时间。在这种对象的初始化工作包含了一些费时的操作的时候，尤其是这样。在需要大量生成这样的对象的时候，就可能会对性能造成一些不可忽略的影响。要缓解这个问题，除了选用更好的硬件和更棒的虚拟机以外，适当地采用一些能够减少对象创建次数的编码技巧，也是一种有效的对策。对象池化技术（Object Pooling）就是这方面的著名技巧，而Jakarta Commons Pool组件则是处理对象池化的得力外援。

Commons Pool组件提供了一整套用于实现对象池化的框架，以及若干种各具特色的对象池实现，可以有效地减少处理对象池化时的工作量，为其它重要的工作留下更多的精力和时间

Apache Common-pool2完全重写了的对象池的实现，显著的提升了性能和可伸缩性，特别是在高并发加载的情况下。2.0 版本包含可靠的实例跟踪和池监控

注意：该版本完全不兼容 1.x
官网：http://commons.apache.org/proper/commons-pool/
使用
Maven：
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>${commons-pool2-version}</version>
</dependency>
组成
ObjectPool：实现对对象存取和状态管理的池实现；如：线程池、数据库连接池
PooledObject：池化对象，是需要放到ObjectPool对象的一个包装类。添加了一些附加的信息，比如说状态信息，创建时间，激活时间，关闭时间等
PooledObjectFactory：工厂类，负责具体对象的创建、初始化，对象状态的销毁和验证
关系图如下：
http://upload-images.jianshu.io/upload_images/840965-e7a5179ac162e8b0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240

# ObjectPool
//从池中获取对象
T borrowObject() throws Exception, NoSuchElementException, IllegalStateException;

//将对象放回池中
void returnObject(T obj) throws Exception;

//废弃对象
void invalidateObject(T obj) throws Exception;

//添加对象
void addObject() throws Exception, IllegalStateException, UnsupportedOperationException;

//获取空闲对象个数
int getNumIdle();

//获取活跃对象个数
int getNumActive();

//清除池，池可用
void clear() throws Exception, UnsupportedOperationException;

//关闭池，池不可用
void close();

# PooledObject
// 获得目标对象
T getObject();

long getCreateTime();

long getActiveTimeMillis();

long getIdleTimeMillis();

long getLastBorrowTime();

long getLastReturnTime();

long getLastUsedTime();

boolean startEvictionTest();

boolean endEvictionTest(Deque<PooledObject<T>> idleQueue);

boolean allocate();

boolean deallocate();

void invalidate();

void setLogAbandoned(boolean logAbandoned);

void use();

void printStackTrace(PrintWriter writer);

PooledObjectState getState();

void markAbandoned();

void markReturning();

# PooledObjectFactory
// 创建一个新对象;当对象池中的对象个数不足时,将会使用此方法来"输出"一个新的"对象",并交付给对象池管理
PooledObject<T> makeObject() throws Exception;

// 销毁对象,如果对象池中检测到某个"对象"idle的时间超时,或者操作者向对象池"归还对象"时检测到"对象"已经无效,那么此时将会导致"对象销毁";
// "销毁对象"的操作设计相差甚远,但是必须明确:当调用此方法时,"对象"的生命周期必须结束.如果object是线程,那么此时线程必须退出;
// 如果object是socket操作,那么此时socket必须关闭;如果object是文件流操作,那么此时"数据flush"且正常关闭.
void destroyObject(PooledObject<T> p) throws Exception;

// 检测对象是否"有效";Pool中不能保存无效的"对象",因此"后台检测线程"会周期性的检测Pool中"对象"的有效性,如果对象无效则会导致此对象从Pool中移除,并destroy;
// 此外在调用者从Pool获取一个"对象"时,也会检测"对象"的有效性,确保不能讲"无效"的对象输出给调用者;
// 当调用者使用完毕将"对象归还"到Pool时,仍然会检测对象的有效性.所谓有效性,就是此"对象"的状态是否符合预期,是否可以对调用者直接使用;
// 如果对象是Socket,那么它的有效性就是socket的通道是否畅通/阻塞是否超时等.
boolean validateObject(PooledObject<T> p);

// "激活"对象,当Pool中决定移除一个对象交付给调用者时额外的"激活"操作,比如可以在activateObject方法中"重置"参数列表让调用者使用时感觉像一个"新创建"的对象一样;如果object是一个线程,可以在"激活"操作中重置"线程中断标记",或者让线程从阻塞中唤醒等;
// 如果object是一个socket,那么可以在"激活操作"中刷新通道,或者对socket进行链接重建(假如socket意外关闭)等.
void activateObject(PooledObject<T> p) throws Exception;

// "钝化"对象,当调用者"归还对象"时,Pool将会"钝化对象"；钝化的言外之意,就是此"对象"暂且需要"休息"一下.
// 如果object是一个socket,那么可以passivateObject中清除buffer,将socket阻塞;如果object是一个线程,可以在"钝化"操作中将线程sleep或者将线程中的某个对象wait.需要注意的时,activateObject和passivateObject两个方法需要对应,避免死锁或者"对象"状态的混乱.
void passivateObject(PooledObject<T> p) throws Exception;


# Config详解

lifo：连接池放池化对象方式，默认为true

true：放在空闲队列最前面

false：放在空闲队列最后面

fairness：等待线程拿空闲连接的方式，默认为false

true：相当于等待线程是在先进先出去拿空闲连接

maxWaitMillis：当连接池资源耗尽时，调用者最大阻塞的时间，超时将跑出异常。单位，毫秒数;默认为-1.表示永不超时. 默认值 -1

maxWait：commons-pool1中

minEvictableIdleTimeMillis：连接空闲的最小时间，达到此值后空闲连接将可能会被移除。负值(-1)表示不移除；默认值1000L 60L 30L

softMinEvictableIdleTimeMillis：连接空闲的最小时间，达到此值后空闲链接将会被移除，且保留“minIdle”个空闲连接数。负值(-1)表示不移除。默认值1000L 60L 30L

numTestsPerEvictionRun：默认值 3

evictionPolicyClassName：默认值org.apache.commons.pool2.impl.DefaultEvictionPolicy

testOnCreate：默认值false

testOnBorrow：向调用者输出“链接”资源时，是否检测是有有效，如果无效则从连接池中移除，并尝试获取继续获取。默认为false。建议保持默认值.

testOnReturn：默认值false

testWhileIdle：向调用者输出“链接”对象时，是否检测它的空闲超时；默认为false。如果“链接”空闲超时，将会被移除；建议保持默认值。默认值false

timeBetweenEvictionRunsMillis：“空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。默认值 -1L

blockWhenExhausted：默认值true

jmxEnabled：默认值true

jmxNamePrefix：默认值 pool

jmxNameBase：默认值 null

maxTotal：链接池中最大连接数，默认值8

commons-pool1 中maxActive改成maxTotal

maxIdle：连接池中最大空闲的连接数,默认为8

minIdle: 连接池中最少空闲的连接数,默认为0

softMinEvictableIdleTimeMillis: 连接空闲的最小时间，达到此值后空闲链接将会被移除，且保留“minIdle”个空闲连接数。默认为-1.

numTestsPerEvictionRun: 对于“空闲链接”检测线程而言，每次检测的链接资源的个数。默认为3.

whenExhaustedAction: 当“连接池”中active数量达到阀值时，即“链接”资源耗尽时，连接池需要采取的手段, 默认为1：
0：抛出异常
1：阻塞，直到有可用链接资源
2：强制创建新的链接资源
